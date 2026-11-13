package nl.hexmaster.finalapproach.ui.game

import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.inset
import androidx.compose.ui.graphics.drawscope.rotate
import nl.hexmaster.finalapproach.domain.Angles
import nl.hexmaster.finalapproach.domain.LevelConfig
import nl.hexmaster.finalapproach.engine.TutorialDemo
import nl.hexmaster.finalapproach.engine.TutorialHighlight
import nl.hexmaster.finalapproach.input.WorldViewport
import nl.hexmaster.finalapproach.ui.sprites.FaIcon
import nl.hexmaster.finalapproach.ui.sprites.drawIcon
import nl.hexmaster.finalapproach.ui.theme.FaColors
import nl.hexmaster.finalapproach.ui.theme.FaDimens
import nl.hexmaster.finalapproach.ui.theme.FaMotion

private const val MS_PER_SECOND = 1000.0

/**
 * The tutorial's zone highlight, on the zone glow layer: a dashed outline `highlight-margin`
 * outside the runway or helipad (`line-3` sun over a `line-4` outline casing), marching on
 * `dur-march` and still with reduced motion, and for a runway a chunky arrow outside its landing
 * end that points along the landing direction. It never fills the zone.
 */
internal fun DrawScope.drawTutorialHighlight(
    highlight: TutorialHighlight,
    level: LevelConfig,
    viewport: WorldViewport,
    colors: FaColors,
    timeSeconds: Double,
    reduceMotion: Boolean,
) {
    val margin = FaDimens.highlightMargin.toPx()
    val dash = FaDimens.pathDash.toPx()
    val gap = FaDimens.pathGap.toPx()
    val phase = if (reduceMotion) 0f else -((timeSeconds * MS_PER_SECOND / FaMotion.durMarchMs) % 1.0).toFloat() * (dash + gap)
    val effect = PathEffect.dashPathEffect(floatArrayOf(dash, gap), phase)

    fun outline(draw: (Stroke) -> Unit) {
        draw(Stroke(FaDimens.line4.toPx(), cap = StrokeCap.Round, join = StrokeJoin.Round, pathEffect = effect))
    }

    when (highlight) {
        is TutorialHighlight.Runway -> {
            val runway = level.runways.firstOrNull { it.id == highlight.id } ?: return
            val center = viewport.offsetOf(runway.position)
            val width = viewport.px(runway.width) + 2f * margin
            val length = viewport.px(runway.length) + 2f * margin
            val corner = CornerRadius(FaDimens.radiusMd.toPx(), FaDimens.radiusMd.toPx())
            rotate(degreesOf(runway.orientation), center) {
                val topLeft = Offset(center.x - width / 2f, center.y - length / 2f)
                outline { drawRoundRect(colors.outline, topLeft, Size(width, length), corner, style = it) }
                val inner = Stroke(FaDimens.line3.toPx(), cap = StrokeCap.Round, join = StrokeJoin.Round, pathEffect = effect)
                drawRoundRect(colors.sun, topLeft, Size(width, length), corner, style = inner)
            }
            val heading = runway.landingHeadings.first()
            val direction = Angles.direction(heading).let { Offset(it.x.toFloat(), it.y.toFloat()) }
            // The arrow stands outside the landing end and points the way airplanes land.
            val threshold = center - direction * (viewport.px(runway.length) / 2f + margin)
            drawLandingArrow(threshold - direction * FaDimens.space2.toPx(), direction, colors)
        }

        is TutorialHighlight.Helipad -> {
            val pad = level.helipads.firstOrNull { it.id == highlight.id } ?: return
            val center = viewport.offsetOf(pad.position)
            val radius = viewport.px(pad.radius) + margin
            outline { drawCircle(colors.outline, radius, center, style = it) }
            drawCircle(
                colors.sun,
                radius,
                center,
                style = Stroke(FaDimens.line3.toPx(), cap = StrokeCap.Round, join = StrokeJoin.Round, pathEffect = effect),
            )
        }
    }
}

/** A chunky arrow whose tip is at [tip] and which points along [direction], in sun over an outline casing. */
private fun DrawScope.drawLandingArrow(tip: Offset, direction: Offset, colors: FaColors) {
    val length = FaDimens.space7.toPx()
    val head = FaDimens.space5.toPx()
    val side = Offset(-direction.y, direction.x)
    val tail = tip - direction * length
    val path = Path().apply {
        moveTo(tail.x, tail.y)
        lineTo(tip.x, tip.y)
        moveTo(tip.x, tip.y)
        lineTo(tip.x - direction.x * head + side.x * head, tip.y - direction.y * head + side.y * head)
        moveTo(tip.x, tip.y)
        lineTo(tip.x - direction.x * head - side.x * head, tip.y - direction.y * head - side.y * head)
    }
    drawPath(path, colors.outline, style = Stroke(FaDimens.line4.toPx() + FaDimens.line3.toPx(), cap = StrokeCap.Round, join = StrokeJoin.Round))
    drawPath(path, colors.sun, style = Stroke(FaDimens.line4.toPx(), cap = StrokeCap.Round, join = StrokeJoin.Round))
}

/** The dotted path of the drag demo, on the path layer: `on-outline` dots with an `outline` casing. */
internal fun DrawScope.drawTutorialDemoPath(demo: TutorialDemo, viewport: WorldViewport, colors: FaColors) {
    val from = viewport.offsetOf(demo.from)
    val to = viewport.offsetOf(demo.to)
    val dot = FaDimens.pathDot.toPx()
    val spacing = dot + FaDimens.pathGap.toPx()
    val dots = PathEffect.dashPathEffect(floatArrayOf(0f, spacing), 0f)
    drawLine(colors.outline, from, to, strokeWidth = dot, cap = StrokeCap.Round, pathEffect = dots)
    drawLine(colors.onOutline, from, to, strokeWidth = dot - 2f * FaDimens.line1.toPx(), cap = StrokeCap.Round, pathEffect = dots)
}

/**
 * The finger of the drag demo, on the effects layer: it travels the dotted path in
 * `dur-demo-drag` with `ease-out-soft`, pauses for `dur-pop` and repeats. With reduced motion it
 * stands still at the end of the path.
 */
internal fun DrawScope.drawTutorialDemoFinger(
    demo: TutorialDemo,
    viewport: WorldViewport,
    colors: FaColors,
    timeSeconds: Double,
    reduceMotion: Boolean,
) {
    val from = viewport.offsetOf(demo.from)
    val to = viewport.offsetOf(demo.to)
    val cycle = (FaMotion.durDemoDragMs + FaMotion.durPopMs).toDouble()
    val progress = if (reduceMotion) {
        1f
    } else {
        val t = (timeSeconds * MS_PER_SECOND) % cycle
        FaMotion.easeOutSoft.transform((t / FaMotion.durDemoDragMs).coerceAtMost(1.0).toFloat())
    }
    val tip = from + (to - from) * progress
    val icon = FaDimens.iconLg.toPx()
    // The fingertip is the top of the icon's glyph, centred on the point being dragged.
    val left = tip.x - icon / 2f
    val top = tip.y - icon * FINGERTIP_FRACTION
    val casing = FaDimens.line1.toPx()
    for (offset in listOf(Offset(casing, 0f), Offset(-casing, 0f), Offset(0f, casing), Offset(0f, -casing))) {
        inset(left + offset.x, top + offset.y, size.width - left - offset.x - icon, size.height - top - offset.y - icon) {
            drawIcon(FaIcon.Finger, colors.outline)
        }
    }
    inset(left, top, size.width - left - icon, size.height - top - icon) { drawIcon(FaIcon.Finger, colors.onOutline) }
}

/** Where the fingertip sits in the finger icon, as a fraction of its height from the top. */
private const val FINGERTIP_FRACTION = 0.2f
