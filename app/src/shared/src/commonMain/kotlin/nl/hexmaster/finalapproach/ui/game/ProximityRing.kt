package nl.hexmaster.finalapproach.ui.game

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.rotate
import nl.hexmaster.finalapproach.domain.Aircraft
import nl.hexmaster.finalapproach.domain.Vec2
import nl.hexmaster.finalapproach.engine.GameState
import nl.hexmaster.finalapproach.ui.theme.FaColors
import nl.hexmaster.finalapproach.ui.theme.FaDimens
import nl.hexmaster.finalapproach.ui.theme.FaMotion

/**
 * The proximity rings on screen, with their appear and disappear transitions. The engine only
 * says which aircraft are warned; this keeps a ring a little longer so it can fade away, and
 * follows its aircraft while it does.
 */
internal class ProximityRings {

    /** One ring around aircraft [id], [radius] world units around [position]. */
    class Ring(val id: Int, var position: Vec2, var radius: Double) {
        /** Whether the aircraft is still warned; false while the ring fades away. */
        var shown: Boolean = true
            internal set

        /** 0 when gone, 1 when fully shown. */
        var level: Float = 0f
            internal set

        /** Scale of the ring: it pops in with a bounce, and leaves at full size. */
        val scale: Float get() = if (shown) FaMotion.easeBounce.transform(level) else 1f

        /** Opacity of the ring. */
        val alpha: Float
            get() = if (shown) level else 1f - FaMotion.easeOutSoft.transform(1f - level)
    }

    private val rings = linkedMapOf<Int, Ring>()

    val all: Collection<Ring> get() = rings.values

    /** Moves the transitions on by [deltaMs] milliseconds for [state]. */
    fun update(state: GameState, radiusOf: (Aircraft) -> Double, deltaMs: Float) {
        val byId = state.aircraft.associateBy { it.id }
        for (id in state.warnedAircraft) {
            val aircraft = byId[id] ?: continue
            rings.getOrPut(id) { Ring(id, aircraft.position, radiusOf(aircraft)) }.shown = true
        }
        for (ring in rings.values) {
            byId[ring.id]?.let { ring.position = it.position }
            if (ring.id !in state.warnedAircraft) ring.shown = false
            ring.level = if (ring.shown) {
                (ring.level + deltaMs / FaMotion.durPopMs).coerceAtMost(1f)
            } else {
                (ring.level - deltaMs / FaMotion.durQuickMs).coerceAtLeast(0f)
            }
        }
        rings.values.removeAll { !it.shown && it.level <= 0f }
    }
}

/** Rotation of the ring dashes after [seconds], in degrees; 0 with reduced motion. */
internal fun proximityRingAngle(seconds: Double, reduceMotion: Boolean): Float {
    if (reduceMotion) return 0f
    val turns = seconds * MILLIS_PER_SECOND / FaMotion.durRingSpinMs
    return ((turns - kotlin.math.floor(turns)) * FULL_TURN_DEGREES).toFloat()
}

/**
 * A dashed ring of [radius] pixels around [center]: an outline casing under a dash in [dashColor]
 * (`warning` for a proximity warning, `danger` for a collision), turned by [angleDegrees].
 */
internal fun DrawScope.drawProximityRing(
    center: Offset,
    radius: Float,
    angleDegrees: Float,
    scale: Float,
    alpha: Float,
    colors: FaColors,
    dashColor: Color = colors.warning,
) {
    if (alpha <= 0f || scale <= 0f) return
    val dashes = PathEffect.dashPathEffect(floatArrayOf(FaDimens.warnRingDash.toPx(), FaDimens.warnRingGap.toPx()))
    val r = radius * scale
    rotate(angleDegrees, center) {
        drawCircle(
            colors.outline,
            r,
            center,
            alpha = alpha,
            style = Stroke(width = FaDimens.line4.toPx(), cap = StrokeCap.Round, pathEffect = dashes),
        )
        drawCircle(
            dashColor,
            r,
            center,
            alpha = alpha,
            style = Stroke(width = FaDimens.line2.toPx(), cap = StrokeCap.Round, pathEffect = dashes),
        )
    }
}

private const val MILLIS_PER_SECOND = 1000.0
private const val FULL_TURN_DEGREES = 360.0
