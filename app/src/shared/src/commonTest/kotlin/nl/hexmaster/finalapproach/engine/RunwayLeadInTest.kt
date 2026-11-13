package nl.hexmaster.finalapproach.engine

import nl.hexmaster.finalapproach.domain.AircraftKind
import nl.hexmaster.finalapproach.domain.AircraftType
import nl.hexmaster.finalapproach.domain.Angles
import nl.hexmaster.finalapproach.domain.EffectiveAircraftSpecs
import nl.hexmaster.finalapproach.domain.LandingConfig
import nl.hexmaster.finalapproach.domain.LandingZone
import nl.hexmaster.finalapproach.domain.LandingZones
import nl.hexmaster.finalapproach.domain.Levels
import nl.hexmaster.finalapproach.domain.Vec2
import kotlin.math.PI
import kotlin.math.abs
import kotlin.math.ceil
import kotlin.test.Test
import kotlin.test.assertTrue

class RunwayLeadInTest {

    private val east = PI / 2
    private val landing = LandingConfig()
    private val spacing = EngineConfig().minPointSpacing
    private val zone = LandingZones.of(twoWayMeadowField).filterIsInstance<LandingZone.Runway>()
        .first { abs(Angles.difference(it.direction, east)) < 1e-9 }
    private val halfWidth = zone.area.width / 2.0

    private fun radius(type: AircraftType): Double =
        EffectiveAircraftSpecs.Defaults[type].let { it.speed / it.turnRate }

    /** A straight drawn path of [length] that ends at [entry] heading [heading]; the aircraft is at its start. */
    private fun straight(entry: Vec2, heading: Double, length: Double): Pair<Vec2, List<Vec2>> {
        val dir = Angles.direction(heading)
        val start = entry - dir * length
        val count = ceil(length / spacing).toInt()
        return start to (1..count).map { start + dir * (length * it / count) }
    }

    private fun entry(offset: Double): Vec2 = zone.threshold + zone.area.across * offset

    private fun snap(type: AircraftType, offset: Double, angle: Double, length: Double? = null): Pair<Vec2, List<Vec2>> {
        val r = radius(type)
        val heading = east + angle
        val (start, path) = straight(entry(offset), heading, length ?: (landing.leadInTurningRadii * r + 200.0))
        return start to RunwayLeadIn.build(start, heading, path, zone, r, landing, spacing)
    }

    private val cases = AircraftType.entries.filter { it.kind == AircraftKind.Airplane }.flatMap { type ->
        listOf(-halfWidth, 0.0, halfWidth).flatMap { offset ->
            listOf(-landing.alignmentTolerance, 0.0, landing.alignmentTolerance).map { Triple(type, offset, it) }
        }
    }

    private fun across(point: Vec2): Double = (point - zone.threshold) dot zone.area.across

    @Test
    fun theSnappedPathEndsOnTheCentreLineInTheLandingDirection() {
        cases.forEach { (type, offset, angle) ->
            val (_, path) = snap(type, offset, angle)
            val last = path.last()
            val label = "$type offset $offset angle $angle"
            assertTrue(abs(across(last)) < 1e-6, "$label ends at $last")
            val along = zone.distanceAlong(last)
            assertTrue(along >= landing.touchdownDistance - 1e-6 && along <= zone.area.length, "$label at $along")
            val lastHeading = Angles.heading(last - path[path.size - 2])
            assertTrue(abs(Angles.difference(lastHeading, zone.direction)) < 1e-6, "$label last heading $lastHeading")
        }
    }

    @Test
    fun theLeadInIsFlyableAndHasNoCorner() {
        cases.forEach { (type, offset, angle) ->
            val (start, path) = snap(type, offset, angle)
            val points = listOf(start) + path
            val r = radius(type)
            val tightest = points.windowed(3).minOf { (a, b, c) -> PathSmoother.circumradius(a, b, c) }
            assertTrue(tightest >= r, "$type offset $offset angle $angle: tightest turn $tightest < radius $r")
        }
    }

    @Test
    fun aShortPathStartsTheLeadInAtTheAircraft() {
        val (start, path) = snap(AircraftType.Jumbo, halfWidth, 0.0, length = 60.0)
        val first = path.first()
        assertTrue(start.distanceTo(first) <= spacing * 1.5, "first point $first, start $start")
        val heading = Angles.heading(first - start)
        assertTrue(abs(Angles.difference(heading, east)) < 0.2, "leaves the aircraft at $heading")
        assertTrue(abs(across(path.last())) < 1e-6)
    }

    @Test
    fun anEntryDeepInTheZoneTouchesDownLevelWithIt() {
        val deep = zone.centreLinePoint(100.0) + zone.area.across * 20.0
        val (start, drawn) = straight(deep, east, 400.0)
        val path = RunwayLeadIn.build(start, east, drawn, zone, radius(AircraftType.LightProp), landing, spacing)
        assertTrue(abs(zone.distanceAlong(path.last()) - 100.0) < 1e-6, "${path.last()}")
    }
}
