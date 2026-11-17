package nl.hexmaster.finalapproach.engine

import nl.hexmaster.finalapproach.domain.Aircraft
import nl.hexmaster.finalapproach.domain.AircraftType
import nl.hexmaster.finalapproach.domain.LandingZone
import nl.hexmaster.finalapproach.domain.Levels
import nl.hexmaster.finalapproach.domain.Vec2
import kotlin.math.PI
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertIs
import kotlin.test.assertTrue

class ClearanceEventTest {

    // Meadow Field: runway 09 is one-way (east), centred at 960, 630, 640 long; helipad at 1430, 630.
    private val level = Levels.meadowField.let { it.copy(spawn = it.spawn.copy(firstSpawnDelay = 10_000.0)) }
    private val dt = 0.05
    private val west = 3 * PI / 2
    private val east = PI / 2

    private fun engine() = GameEngine(level, seed = 1)

    private fun GameEngine.plane(id: Int, at: Vec2, heading: Double, type: AircraftType = AircraftType.LightProp) =
        Aircraft(id, type, at, heading, hasEnteredViewport = true)

    private fun GameEngine.draw(id: Int, vararg points: Vec2) {
        beginPath(id)
        points.forEach { extendPath(id, it) }
        endPath(id)
    }

    private fun GameEngine.cleared() = state.value.events.filterIsInstance<GameEvent.ClearedToLand>()

    private val approachFromWest = arrayOf(Vec2(220.0, 700.0), Vec2(320.0, 640.0), Vec2(420.0, 630.0), Vec2(520.0, 630.0), Vec2(620.0, 630.0), Vec2(720.0, 630.0))

    @Test
    fun aClearanceIsReportedByTheNextUpdateOnlyOnce() {
        val engine = engine()
        engine.place(listOf(engine.plane(1, Vec2(120.0, 700.0), east)))
        engine.draw(1, *approachFromWest)
        assertEquals(emptyList(), engine.cleared(), "reported before the next update")
        engine.update(dt)
        val events = engine.cleared()
        assertEquals(1, events.size)
        assertEquals(1, events.single().aircraftId)
        assertIs<LandingZone.Runway>(events.single().zone)
        engine.update(dt)
        assertEquals(emptyList(), engine.cleared())
    }

    @Test
    fun pointsDraggedAfterTheCutOffAddNoEvent() {
        val engine = engine()
        engine.place(listOf(engine.plane(1, Vec2(120.0, 700.0), east)))
        engine.draw(1, *approachFromWest, Vec2(920.0, 630.0), Vec2(1020.0, 630.0))
        engine.update(dt)
        assertEquals(1, engine.cleared().size)
    }

    @Test
    fun twoClearancesBeforeOneUpdateBothAreReported() {
        val engine = engine()
        val pad = level.helipads.single().position
        engine.place(
            listOf(
                engine.plane(1, Vec2(120.0, 700.0), east),
                engine.plane(2, pad + Vec2(0.0, -300.0), PI, AircraftType.Helicopter),
            ),
        )
        engine.draw(1, *approachFromWest)
        engine.draw(2, pad + Vec2(0.0, -200.0), pad + Vec2(0.0, -100.0), pad)
        engine.update(dt)
        assertEquals(setOf(1, 2), engine.cleared().map { it.aircraftId }.toSet())
        assertTrue(engine.cleared().any { it.zone is LandingZone.Helipad })
    }

    @Test
    fun aReRoutedAircraftIsClearedAgain() {
        val engine = engine()
        engine.place(listOf(engine.plane(1, Vec2(120.0, 700.0), east)))
        engine.draw(1, *approachFromWest)
        engine.update(dt)
        engine.draw(1, *approachFromWest)
        engine.update(dt)
        assertEquals(1, engine.cleared().size)
    }

    @Test
    fun noEventForOpenAirAMisalignedApproachTheWrongEndOrTheWrongZoneType() {
        val engine = engine()
        val pad = level.helipads.single().position
        engine.place(
            listOf(
                engine.plane(1, Vec2(120.0, 300.0), east),
                engine.plane(2, Vec2(120.0, 900.0), east),
                engine.plane(3, Vec2(1820.0, 630.0), west),
                engine.plane(4, pad + Vec2(0.0, -300.0), PI),
                engine.plane(5, Vec2(920.0, 300.0), PI, AircraftType.Helicopter),
            ),
        )
        engine.draw(1, Vec2(220.0, 300.0), Vec2(320.0, 300.0))
        // Straight down onto the runway threshold: not aligned.
        engine.draw(2, Vec2(640.0, 800.0), Vec2(640.0, 700.0), Vec2(640.0, 630.0), Vec2(640.0, 560.0))
        // Westbound into the east end of the one-way runway.
        engine.draw(3, Vec2(1720.0, 630.0), Vec2(1620.0, 630.0), Vec2(1520.0, 630.0), Vec2(1420.0, 630.0), Vec2(1220.0, 630.0))
        // An airplane into the helipad.
        engine.draw(4, pad + Vec2(0.0, -200.0), pad + Vec2(0.0, -100.0), pad)
        // A helicopter into the runway.
        engine.draw(5, Vec2(920.0, 400.0), Vec2(920.0, 500.0), Vec2(920.0, 630.0), Vec2(920.0, 700.0))
        engine.update(dt)
        assertEquals(emptyList(), engine.cleared())
    }

    @Test
    fun aClearanceAddsNoScoreAndIsNotALanding() {
        val engine = engine()
        engine.place(listOf(engine.plane(1, Vec2(120.0, 700.0), east)))
        engine.draw(1, *approachFromWest)
        engine.update(dt)
        assertEquals(1, engine.cleared().size)
        assertEquals(0, engine.state.value.score)
        assertEquals(0, engine.state.value.landings)
    }

    @Test
    fun restartDropsAnUnreportedClearance() {
        val engine = engine()
        engine.place(listOf(engine.plane(1, Vec2(120.0, 700.0), east)))
        engine.draw(1, *approachFromWest)
        engine.restart(seed = 2)
        engine.update(dt)
        assertEquals(emptyList(), engine.cleared())
    }

    @Test
    fun aReportedClearanceIsNotRepeatedByUpdatesThatDoNothing() {
        val engine = engine()
        engine.place(listOf(engine.plane(1, Vec2(120.0, 700.0), east)))
        engine.draw(1, *approachFromWest)
        engine.update(dt)
        assertEquals(1, engine.cleared().size)
        engine.pause()
        assertEquals(emptyList(), engine.cleared())
        engine.update(dt)
        assertEquals(emptyList(), engine.cleared())
    }
}
