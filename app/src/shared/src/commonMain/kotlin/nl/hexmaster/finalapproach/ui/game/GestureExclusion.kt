package nl.hexmaster.finalapproach.ui.game

import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import nl.hexmaster.finalapproach.platform.ScreenRect

/**
 * Limits of the system-gesture exclusion zones. They are platform limits and layout tuning, not
 * design tokens. The system honours at most [maxHeightPerEdge] of exclusion per screen edge;
 * [zoneHalfHeight] is the margin above and below an aircraft that a zone covers.
 */
internal data class GestureExclusionConfig(
    val maxHeightPerEdge: Dp = 200.dp,
    val zoneHalfHeight: Dp = 48.dp,
)

/** An aircraft as the planner sees it: its centre in window pixels, and whether it can be picked. */
internal data class GestureTarget(val id: Int, val x: Float, val y: Float, val pickable: Boolean)

/**
 * Plans the zones that keep a system back gesture from stealing a touch that starts on an aircraft
 * near the left or right edge. Everything is in window pixels.
 *
 * An aircraft counts for an edge when its pick area (radius [pickRadius]) reaches that edge's back
 * gesture strip ([leftStrip], [rightStrip]; 0 means the edge has none). Its zone covers the strip
 * horizontally and [zoneHalfHeight] above and below it, clamped to the screen. Per edge the
 * selected aircraft comes first, then the closest to the edge; a zone that would push the edge's
 * total height over [maxHeightPerEdge] is left out. Overlapping zones are merged.
 */
internal fun planGestureExclusion(
    aircraft: List<GestureTarget>,
    selectedId: Int?,
    screenWidth: Int,
    screenHeight: Int,
    leftStrip: Int,
    rightStrip: Int,
    pickRadius: Float,
    maxHeightPerEdge: Int,
    zoneHalfHeight: Int,
): List<ScreenRect> {
    val left = edgeIntervals(aircraft, selectedId, screenHeight, leftStrip, pickRadius, maxHeightPerEdge, zoneHalfHeight) { it.x }
    val right = edgeIntervals(aircraft, selectedId, screenHeight, rightStrip, pickRadius, maxHeightPerEdge, zoneHalfHeight) { screenWidth - it.x }
    val leftEdge = leftStrip.coerceAtMost(screenWidth)
    val rightEdge = rightStrip.coerceAtMost(screenWidth)
    return left.map { ScreenRect(0, it.first, leftEdge, it.last) } +
        right.map { ScreenRect(screenWidth - rightEdge, it.first, screenWidth, it.last) }
}

/** The merged vertical intervals, as first..last pixel bounds, for one edge. [distance] is a target's distance to that edge. */
private fun edgeIntervals(
    aircraft: List<GestureTarget>,
    selectedId: Int?,
    screenHeight: Int,
    strip: Int,
    pickRadius: Float,
    maxHeight: Int,
    halfHeight: Int,
    distance: (GestureTarget) -> Float,
): List<IntRange> {
    if (strip <= 0) return emptyList()
    val candidates = aircraft
        .filter { it.pickable && distance(it) - pickRadius < strip }
        .sortedWith(compareBy({ if (it.id == selectedId) 0 else 1 }, distance))
    var accepted = emptyList<IntRange>()
    for (target in candidates) {
        val top = (target.y - halfHeight).toInt().coerceIn(0, screenHeight)
        val bottom = (target.y + halfHeight).toInt().coerceIn(0, screenHeight)
        if (bottom <= top) continue
        val merged = merge(accepted + listOf(top..bottom))
        if (merged.sumOf { it.last - it.first } <= maxHeight) accepted = merged
    }
    return accepted
}

/** Merges overlapping or touching intervals, sorted by their start. */
private fun merge(intervals: List<IntRange>): List<IntRange> {
    val result = mutableListOf<IntRange>()
    for (interval in intervals.sortedBy { it.first }) {
        val last = result.lastOrNull()
        if (last != null && interval.first <= last.last) {
            result[result.size - 1] = last.first..maxOf(last.last, interval.last)
        } else {
            result += interval
        }
    }
    return result
}
