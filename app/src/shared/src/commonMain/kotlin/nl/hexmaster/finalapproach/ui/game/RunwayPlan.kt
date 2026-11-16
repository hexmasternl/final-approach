package nl.hexmaster.finalapproach.ui.game

import nl.hexmaster.finalapproach.ui.sprites.GroundMetrics
import nl.hexmaster.finalapproach.ui.sprites.GroundMetrics.Runway
import kotlin.math.floor

/** The two ends of a runway along its axis: [Low] lies at the negative end of the axis, [High] at the positive one. */
internal enum class RunwayEnd(val sign: Int) { Low(-1), High(1) }

/** A point in the runway frame: x along the axis from the middle, y across it, in world units. */
internal class PlanPoint(val x: Float, val y: Float)

/** A rectangle in the runway frame, with its top left corner at [x], [y]. */
internal class PlanRect(val x: Float, val y: Float, val width: Float, val height: Float)

/** A line segment in the runway frame. */
internal class PlanLine(val x0: Float, val y0: Float, val x1: Float, val y1: Float)

/** A chevron of a one-way centre line: its [tip] and the two ends of its arms. */
internal class PlanChevron(val tip: PlanPoint, val armA: PlanPoint, val armB: PlanPoint)

/** One approach light: its box, and whether it is red (the other kind is white). */
internal class ApproachLight(val box: PlanRect, val red: Boolean)

/** What a runway shows at one end it can be landed from. */
internal class RunwayEndPlan(
    val end: RunwayEnd,
    val bars: List<PlanRect>,
    val aiming: List<PlanRect>,
    val touchdown: List<PlanLine>,
    val rubber: List<PlanLine>,
    val thresholdLights: List<PlanPoint>,
    /** Where the middle of the runway number goes. */
    val numberAt: PlanPoint,
)

/**
 * What a runway shows, in its own frame (design system `Runway.md`): the marked ends, the centre
 * line, the edge lights and the approach lights. Pure data: [drawRunway] paints it.
 *
 * @param centreDashes the centre line of a two-way runway, as one line to be dashed with [GroundMetrics.Runway.dash]; null when one-way
 * @param chevrons the centre line of a one-way runway, pointing the way airplanes land; empty when two-way
 * @param approachEnd the end the approach lights lie beside; null when the runway has none
 */
internal class RunwayPlan(
    val length: Float,
    val ends: List<RunwayEndPlan>,
    val edgeLines: List<PlanLine>,
    val centreDashes: PlanLine?,
    val chevrons: List<PlanChevron>,
    val edgeLights: List<PlanPoint>,
    val approachEnd: RunwayEnd?,
    val approachLights: List<ApproachLight>,
) {
    /** How many edge lights lie along one side. */
    val edgeLightsPerSide: Int get() = edgeLights.size / 2
}

/**
 * Plans a runway of [length] world units that can be landed from [landingEnds]: one end for a
 * one-way runway, both for a two-way one. Sizes come from [GroundMetrics.Runway]; those across the
 * runway are multiplied by [acrossScale] (the runway's width divided by the 64 of the design
 * system), those along it are world units.
 *
 * A runway shorter than [GroundMetrics.Runway.fullLength] keeps its threshold bars, number and
 * lights and drops the aiming blocks and tyre rubber; touchdown marks need
 * [GroundMetrics.Runway.touchdownLength].
 */
internal fun runwayPlan(length: Float, landingEnds: Set<RunwayEnd>, acrossScale: Float = 1f): RunwayPlan {
    val m = GroundMetrics.Runway
    val l = maxOf(m.minLength, length)
    val half = l / 2f
    val full = l >= m.fullLength
    val inset = if (full) m.centreInsetFull else m.centreInsetShort
    val oneWay = landingEnds.size == 1
    val ends = RunwayEnd.entries.filter { it in landingEnds }.map { endPlan(it, half, l, full, acrossScale) }
    // The centre line: chevrons run from the far end to the landing end, and point the way airplanes land.
    val chevrons = mutableListOf<PlanChevron>()
    if (oneWay) {
        val flip = if (landingEnds.single() == RunwayEnd.High) 1f else -1f
        var x = -half + Runway.Chevron.start
        while (x < half - inset - CHEVRON_END_CLEARANCE) {
            val a = CHEVRON_ARM
            chevrons += PlanChevron(
                tip = PlanPoint(flip * x, 0f),
                armA = PlanPoint(flip * (x + a.along), -a.across * acrossScale),
                armB = PlanPoint(flip * (x + a.along), a.across * acrossScale),
            )
            x += Runway.Chevron.pitch
        }
    }
    val edgeY = Runway.EdgeLine.y * acrossScale
    val lightY = Runway.Lights.Edge.y * acrossScale
    val lights = mutableListOf<PlanPoint>()
    var x = -floor((half - Runway.Lights.Edge.inset) / Runway.Lights.Edge.spacing) * Runway.Lights.Edge.spacing
    while (x <= half - Runway.Lights.Edge.inset) {
        lights += PlanPoint(x, -lightY)
        lights += PlanPoint(x, lightY)
        x += Runway.Lights.Edge.spacing
    }
    val approachEnd = if (oneWay) landingEnds.single() else RunwayEnd.Low
    return RunwayPlan(
        length = l,
        ends = ends,
        edgeLines = listOf(-edgeY, edgeY).map { PlanLine(-half + Runway.EdgeLine.inset, it, half - Runway.EdgeLine.inset, it) },
        centreDashes = if (oneWay) null else PlanLine(-half + inset, 0f, half - inset, 0f),
        chevrons = chevrons,
        edgeLights = lights,
        approachEnd = approachEnd,
        approachLights = approachLights(approachEnd, half, full, acrossScale),
    )
}

/** The arm of a chevron: how far it reaches back along the runway and out across it. */
private class ChevronArm(val along: Float, val across: Float)

private val CHEVRON_ARM = ChevronArm(along = 12f, across = 9f)

/** A one-way centre line stops this far before the marks of the landing end. */
private const val CHEVRON_END_CLEARANCE = 6f

/** Positions measured from [end] inwards: a mark [u] units in from the end lies at x = sign * (half - u). */
private fun alongOf(end: RunwayEnd, half: Float, u: Float): Float = end.sign * (half - u)

private fun endPlan(end: RunwayEnd, half: Float, length: Float, full: Boolean, across: Float): RunwayEndPlan {
    val m = GroundMetrics.Runway
    val b = Runway.Bars
    // A rectangle [u] units in from the end, [along] long, as the rectangle with its low corner first.
    fun rect(u: Float, y: Float, along: Float, height: Float): PlanRect {
        val a = alongOf(end, half, u)
        val b2 = alongOf(end, half, u + along)
        return PlanRect(minOf(a, b2), y * across, along, height * across)
    }
    fun line(u: Float, y: Float, along: Float) =
        PlanLine(alongOf(end, half, u), y * across, alongOf(end, half, u + along), y * across)
    return RunwayEndPlan(
        end = end,
        bars = List(b.count) { rect(b.inset, b.y0 + it * b.pitch, b.length, b.width) },
        aiming = if (full) Runway.Aiming.y.map { rect(Runway.Aiming.inset, it, Runway.Aiming.length, Runway.Aiming.width) } else emptyList(),
        touchdown = if (length >= m.touchdownLength) Runway.Touchdown.y.map { line(Runway.Touchdown.inset, it, Runway.Touchdown.length) } else emptyList(),
        rubber = if (full) m.rubber.map { line(it[0], it[1], it[2]) } else emptyList(),
        thresholdLights = Runway.Lights.Threshold.y.map { PlanPoint(alongOf(end, half, -Runway.Lights.Threshold.out), it * across) },
        numberAt = PlanPoint(alongOf(end, half, m.numberInset), 0f),
    )
}

/** Four approach lights in the fringe beside [end], the two nearest the end red. */
private fun approachLights(end: RunwayEnd, half: Float, full: Boolean, across: Float): List<ApproachLight> {
    val a = Runway.Lights.Approach
    val inset = if (full) a.insetFull else a.insetShort
    // The first box starts [inset] from the low end; at the high end the row is mirrored by its own length.
    val start = if (end == RunwayEnd.Low) -half + inset else half - inset - a.oneWayBack
    return List(a.count) { i ->
        val nearEnd = if (end == RunwayEnd.Low) i < a.red else i >= a.count - a.red
        ApproachLight(PlanRect(start + i * a.pitch, -a.y * across, a.w, a.h * across), red = nearEnd)
    }
}
