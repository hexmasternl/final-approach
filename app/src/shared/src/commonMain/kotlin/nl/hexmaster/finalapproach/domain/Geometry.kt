package nl.hexmaster.finalapproach.domain

import kotlin.math.PI
import kotlin.math.abs
import kotlin.math.atan2
import kotlin.math.cos
import kotlin.math.hypot
import kotlin.math.max
import kotlin.math.min
import kotlin.math.sin

/**
 * A point or vector in world units. The world's y axis points down, as on screen.
 *
 * Headings are radians, clockwise from straight up (north): 0 is up, pi/2 is right (east).
 */
data class Vec2(val x: Double, val y: Double) {
    operator fun plus(other: Vec2) = Vec2(x + other.x, y + other.y)
    operator fun minus(other: Vec2) = Vec2(x - other.x, y - other.y)
    operator fun times(factor: Double) = Vec2(x * factor, y * factor)
    operator fun unaryMinus() = Vec2(-x, -y)

    infix fun dot(other: Vec2): Double = x * other.x + y * other.y

    val length: Double get() = hypot(x, y)

    fun distanceTo(other: Vec2): Double = hypot(x - other.x, y - other.y)

    /** This vector turned by [angle] radians clockwise (on screen, with y down). */
    fun rotated(angle: Double): Vec2 {
        val c = cos(angle)
        val s = sin(angle)
        return Vec2(x * c - y * s, x * s + y * c)
    }

    companion object {
        val Zero = Vec2(0.0, 0.0)
    }
}

/** Helpers for headings in radians, clockwise from up. */
object Angles {
    const val TWO_PI = 2.0 * PI

    /** [angle] wrapped into (-pi, pi]. */
    fun normalize(angle: Double): Double {
        var a = angle % TWO_PI
        if (a <= -PI) a += TWO_PI
        if (a > PI) a -= TWO_PI
        return a
    }

    /** The signed smallest turn from [from] to [to], in (-pi, pi]. */
    fun difference(from: Double, to: Double): Double = normalize(to - from)

    /** The unit vector a heading points along. */
    fun direction(heading: Double): Vec2 = Vec2(sin(heading), -cos(heading))

    /** The heading of [vector]; 0 for the zero vector. */
    fun heading(vector: Vec2): Double = atan2(vector.x, -vector.y)

    fun degrees(value: Double): Double = value * PI / 180.0
}

/** A circle in world units. */
data class Circle(val center: Vec2, val radius: Double)

/**
 * A rectangle turned to [heading]: its length lies along the heading, its width across it.
 * A runway is one of these.
 */
data class OrientedRect(
    val center: Vec2,
    val heading: Double,
    val length: Double,
    val width: Double,
) {
    val axis: Vec2 get() = Angles.direction(heading)
    val across: Vec2 get() = Vec2(-axis.y, axis.x)

    val corners: List<Vec2>
        get() {
            val a = axis * (length / 2.0)
            val c = across * (width / 2.0)
            return listOf(center + a + c, center + a - c, center - a - c, center - a + c)
        }

    /** [point] in the rectangle's own frame: x along the axis, y across it. */
    fun toLocal(point: Vec2): Vec2 {
        val d = point - center
        return Vec2(d dot axis, d dot across)
    }

    fun contains(point: Vec2): Boolean {
        val local = toLocal(point)
        return abs(local.x) <= length / 2.0 && abs(local.y) <= width / 2.0
    }
}

/** An axis-aligned rectangle, used for the viewport. */
data class Bounds(val left: Double, val top: Double, val right: Double, val bottom: Double) {
    val width: Double get() = right - left
    val height: Double get() = bottom - top

    fun contains(point: Vec2): Boolean =
        point.x in left..right && point.y in top..bottom

    fun expandedBy(margin: Double) = Bounds(left - margin, top - margin, right + margin, bottom + margin)

    /**
     * Where a ray from [origin] along [direction] enters and leaves these bounds, as distances
     * along the ray. Null when the ray misses them or they lie behind the origin.
     */
    fun rayCrossing(origin: Vec2, direction: Vec2): Pair<Double, Double>? {
        var enter = Double.NEGATIVE_INFINITY
        var exit = Double.POSITIVE_INFINITY
        for ((o, d, lo, hi) in listOf(
            listOf(origin.x, direction.x, left, right),
            listOf(origin.y, direction.y, top, bottom),
        )) {
            if (abs(d) < EPSILON) {
                if (o < lo || o > hi) return null
            } else {
                val t1 = (lo - o) / d
                val t2 = (hi - o) / d
                enter = max(enter, min(t1, t2))
                exit = min(exit, max(t1, t2))
            }
        }
        if (exit <= max(enter, 0.0)) return null
        return max(enter, 0.0) to exit
    }

    private companion object {
        const val EPSILON = 1e-12
    }
}

/** Overlap tests between the shapes the game uses. Touching edges do not count as overlap. */
object Overlap {
    fun circles(a: Circle, b: Circle): Boolean =
        a.center.distanceTo(b.center) < a.radius + b.radius

    fun rectCircle(rect: OrientedRect, circle: Circle): Boolean {
        val local = rect.toLocal(circle.center)
        val closest = Vec2(
            local.x.coerceIn(-rect.length / 2.0, rect.length / 2.0),
            local.y.coerceIn(-rect.width / 2.0, rect.width / 2.0),
        )
        return closest.distanceTo(local) < circle.radius
    }

    /** Separating-axis test for two turned rectangles. */
    fun rects(a: OrientedRect, b: OrientedRect): Boolean {
        val axes = listOf(a.axis, a.across, b.axis, b.across)
        val ca = a.corners
        val cb = b.corners
        for (axis in axes) {
            val pa = ca.map { it dot axis }
            val pb = cb.map { it dot axis }
            if (pa.max() <= pb.min() || pb.max() <= pa.min()) return false
        }
        return true
    }

    /** True when the segment from [from] to [to] passes through [circle]. */
    fun segmentCircle(from: Vec2, to: Vec2, circle: Circle): Boolean =
        closestPointOnSegment(from, to, circle.center).distanceTo(circle.center) < circle.radius

    fun closestPointOnSegment(from: Vec2, to: Vec2, point: Vec2): Vec2 {
        val d = to - from
        val lengthSquared = d dot d
        if (lengthSquared == 0.0) return from
        val t = ((point - from) dot d / lengthSquared).coerceIn(0.0, 1.0)
        return from + d * t
    }
}
