package nl.hexmaster.finalapproach.engine

import nl.hexmaster.finalapproach.domain.Aircraft
import nl.hexmaster.finalapproach.domain.AircraftStatus
import nl.hexmaster.finalapproach.domain.AircraftType
import nl.hexmaster.finalapproach.domain.AltitudeState
import nl.hexmaster.finalapproach.domain.LevelDefaults
import nl.hexmaster.finalapproach.domain.Levels
import nl.hexmaster.finalapproach.domain.Vec2
import nl.hexmaster.finalapproach.domain.meadow
import kotlin.math.PI
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertIs
import kotlin.test.assertNotNull
import kotlin.test.assertTrue

class GameEngineTest {

    private val level = Levels.meadowField
    private val dt = 0.05

    /** Meadow Field without spawns, for tests that place their own aircraft. */
    private val quiet = level.copy(spawn = level.spawn.copy(firstSpawnDelay = 10_000.0))

    private fun GameEngine.run(seconds: Double, each: (GameState) -> Unit = {}) {
        repeat((seconds / dt).toInt()) {
            update(dt)
            each(state.value)
        }
    }

    @Test
    fun updatesArePublishedOnTheStateFlow() {
        val engine = GameEngine(level, seed = 1)
        engine.update(dt)
        assertEquals(dt, engine.state.value.levelTime, 1e-12)
    }

    @Test
    fun spawnsAppearWithTheSpawnersTypePositionAndHeading() {
        val engine = GameEngine(level, seed = 7)
        val spawner = Spawner(level, 7, engine.specs::speedOf)
        var expected: Spawn? = null
        var time = 0.0
        while (expected == null) {
            time += dt
            expected = spawner.update(dt, time, emptyList())
        }
        while (engine.state.value.aircraft.isEmpty()) engine.update(dt)
        val first = engine.state.value.aircraft.single()
        assertEquals(expected.type, first.type)
        assertEquals(expected.position, first.position)
        assertEquals(expected.heading, first.heading)
    }

    @Test
    fun theFirstLevelNeverHasMoreThanThreeAircraftInTheAir() {
        val engine = GameEngine(level, seed = 42)
        var most = 0
        engine.run(600.0) { state ->
            most = maxOf(most, state.aircraft.count { it.status !is AircraftStatus.RollingOut })
        }
        assertTrue(most in 1..3, "most aircraft in the air: $most")
    }

    @Test
    fun theSameSeedGivesTheSameStates() {
        val a = GameEngine(level, seed = 3)
        val b = GameEngine(level, seed = 3)
        repeat(2000) {
            a.update(dt)
            b.update(dt)
            assertEquals(a.state.value, b.state.value)
        }
    }

    @Test
    fun anUnsteeredLevelEndsWhenAnAircraftLeaves() {
        val engine = GameEngine(level, seed = 5)
        engine.run(600.0)
        val status = assertIs<GameStatus.GameOver>(engine.state.value.status)
        if (status.reason == GameOverReason.LeftArea) {
            val gone = engine.state.value.aircraft.filter { it.id in status.aircraftIds }
            assertTrue(gone.isNotEmpty())
            gone.forEach { assertFalse(level.world.viewport.expandedBy(level.world.leaveMargin).contains(it.position)) }
        }
    }

    @Test
    fun anAircraftThatLeavesAfterEnteringEndsTheLevel() {
        val engine = GameEngine(quiet, seed = 1)
        engine.place(listOf(jet(1, Vec2(50.0, 300.0), -PI / 2).copy(hasEnteredViewport = true)))
        engine.run(3.0)
        val status = assertIs<GameStatus.GameOver>(engine.state.value.status)
        assertEquals(GameOverReason.LeftArea, status.reason)
        assertEquals(listOf(1), status.aircraftIds)
        assertTrue(engine.aircraft(1).position.x < -level.world.leaveMargin)
    }

    @Test
    fun anAircraftStillEnteringDoesNotEndTheLevel() {
        val engine = GameEngine(quiet, seed = 1)
        engine.place(listOf(jet(1, Vec2(-200.0, 300.0), -PI / 2)))
        engine.run(1.0)
        assertEquals(GameStatus.Playing, engine.state.value.status)
        assertFalse(engine.aircraft(1).hasEnteredViewport)
    }

    @Test
    fun anEdgeTouchWithinTheMarginIsTolerated() {
        val engine = GameEngine(quiet, seed = 1)
        val helicopter = Aircraft(1, AircraftType.Helicopter, Vec2(30.0, 300.0), -PI / 2, hasEnteredViewport = true)
        engine.place(listOf(helicopter))
        engine.beginPath(1)
        listOf(Vec2(-30.0, 300.0), Vec2(-30.0, 340.0), Vec2(100.0, 340.0)).forEach { engine.extendPath(1, it) }
        var leftmost = 0.0
        engine.run(6.0) { state -> state.aircraft.firstOrNull { it.id == 1 }?.let { leftmost = minOf(leftmost, it.position.x) } }
        assertEquals(GameStatus.Playing, engine.state.value.status)
        assertTrue(leftmost < 0.0, "the helicopter never crossed the edge")
        assertTrue(engine.aircraft(1).position.x > 0.0)
    }

    @Test
    fun aCollisionEndsTheLevelAndFreezesIt() {
        val engine = GameEngine(quiet, seed = 1)
        engine.place(listOf(jet(1, Vec2(500.0, 300.0), PI / 2), jet(2, Vec2(700.0, 300.0), -PI / 2)))
        engine.run(2.0)
        val over = engine.state.value
        assertEquals(GameStatus.GameOver(GameOverReason.Collision, listOf(1, 2)), over.status)
        engine.update(1.0)
        assertEquals(over, engine.state.value)
        assertEquals(null, engine.select(Vec2(600.0, 300.0), 100.0))
    }

    @Test
    fun aHeadOnPassInOneLargeFrameIsDetected() {
        val oneStep = quiet.copy(world = quiet.world.copy(maxStepSeconds = 10.0))
        val engine = GameEngine(oneStep, seed = 1)
        engine.place(listOf(jet(1, Vec2(500.0, 300.0), PI / 2), jet(2, Vec2(700.0, 300.0), -PI / 2)))
        engine.update(3.0)
        val status = assertIs<GameStatus.GameOver>(engine.state.value.status)
        assertEquals(GameOverReason.Collision, status.reason)
        assertTrue(engine.aircraft(1).position.x > engine.aircraft(2).position.x, "the jets passed each other")
    }

    @Test
    fun aLandedAircraftIsGoneForCollisionsAndLeaving() {
        val engine = GameEngine(quiet, seed = 1)
        val pad = level.helipads.single().position
        engine.place(
            listOf(
                Aircraft(1, AircraftType.Helicopter, pad + Vec2(0.0, -100.0), PI, hasEnteredViewport = true),
                jet(2, pad + Vec2(0.0, 300.0), 0.0),
            ),
        )
        engine.beginPath(1)
        engine.extendPath(1, pad)
        engine.run(5.0)
        assertEquals(GameStatus.Playing, engine.state.value.status)
        assertEquals(1, engine.state.value.landings)
        assertTrue(engine.state.value.aircraft.none { it.id == 1 })
        assertTrue(engine.aircraft(2).position.y < pad.y - 50.0, "the jet flew over the pad")
    }

    @Test
    fun closeAircraftAreWarnedUntilTheySeparate() {
        val engine = GameEngine(quiet, seed = 1)
        engine.place(listOf(jet(1, Vec2(560.0, 200.0), -PI / 2), jet(2, Vec2(560.0 + 80.0 * zoom, 200.0), PI / 2)))
        assertEquals(setOf(1, 2), engine.state.value.warnedAircraft)
        engine.update(0.02)
        assertEquals(setOf(1, 2), engine.state.value.warnedAircraft)
        engine.run(1.0)
        assertEquals(emptySet(), engine.state.value.warnedAircraft)
        assertEquals(GameStatus.Playing, engine.state.value.status)
    }

    @Test
    fun everyAircraftOfACloseGroupIsWarned() {
        val engine = GameEngine(quiet, seed = 1)
        engine.place(
            listOf(
                jet(1, Vec2(500.0, 200.0), 0.0),
                jet(2, Vec2(500.0 + 80.0 * zoom, 200.0), 0.0),
                jet(3, Vec2(500.0 + 40.0 * zoom, 200.0 + 64.0 * zoom), 0.0),
                jet(4, Vec2(500.0 + 320.0 * zoom, 200.0), 0.0),
            ),
        )
        assertEquals(setOf(1, 2, 3), engine.state.value.warnedAircraft)
    }
    @Test
    fun liveriesTakeTheNextFreeColour() {
        val engine = GameEngine(level, seed = 9)
        engine.run(200.0) { state ->
            val liveries = state.aircraft.map { it.livery }
            assertEquals(liveries.size, liveries.toSet().size, "shared livery in $liveries")
        }
    }

    @Test
    fun tapSelectsTheNearestAircraftWithinThePickRadius() {
        val engine = GameEngine(level, seed = 1)
        engine.place(listOf(jet(1, Vec2(100.0, 100.0)), jet(2, Vec2(160.0, 100.0))))
        assertEquals(2, engine.select(Vec2(140.0, 100.0), 50.0))
        assertEquals(2, engine.state.value.selectedId)
        assertEquals(null, engine.select(Vec2(400.0, 400.0), 50.0))
        assertEquals(null, engine.state.value.selectedId)
    }

    @Test
    fun aNewPathReplacesTheOldOne() {
        val engine = GameEngine(level, seed = 1)
        engine.place(listOf(jet(1, Vec2(100.0, 100.0))))
        engine.beginPath(1)
        engine.extendPath(1, Vec2(100.0, 200.0))
        engine.endPath(1)
        engine.beginPath(1)
        engine.extendPath(1, Vec2(200.0, 100.0))
        assertEquals(listOf(Vec2(200.0, 100.0)), engine.aircraft(1).path.points)
    }

    @Test
    fun pointsCloserThanTheMinimumSpacingAreDropped() {
        val engine = GameEngine(level, seed = 1)
        engine.place(listOf(jet(1, Vec2(100.0, 100.0))))
        engine.beginPath(1)
        engine.extendPath(1, Vec2(150.0, 100.0))
        engine.extendPath(1, Vec2(150.0 + engine.config.minPointSpacing / 2.0, 100.0))
        assertEquals(listOf(Vec2(150.0, 100.0)), engine.aircraft(1).path.points)
    }

    @Test
    fun aPathIntoALandingZoneIsCutOffAndFurtherPointsAreIgnored() {
        val engine = GameEngine(level, seed = 1)
        val helipad = level.helipads.single().position
        engine.place(listOf(Aircraft(1, AircraftType.Helicopter, helipad + Vec2(0.0, -200.0), PI)))
        engine.beginPath(1)
        engine.extendPath(1, helipad)
        engine.extendPath(1, helipad + Vec2(0.0, 200.0))
        val aircraft = engine.aircraft(1)
        assertIs<AircraftStatus.ClearedToLand>(aircraft.status)
        assertTrue(aircraft.path.locked)
        assertTrue(aircraft.path.points.last().y < helipad.y)
    }

    @Test
    fun aFlyingAircraftCrossesARollingOutAirplaneWithoutACollision() {
        val engine = GameEngine(quiet, seed = 1)
        val rolling = jet(1, Vec2(300.0, 300.0)).copy(
            altitude = AltitudeState.Landed,
            status = AircraftStatus.RollingOut(0.0, 500.0, "r"),
        )
        engine.place(listOf(rolling, jet(2, Vec2(300.0, 300.0))))
        engine.update(dt)
        assertEquals(GameStatus.Playing, engine.state.value.status)
    }

    @Test
    fun anAirplaneIsInFlightUntilTouchdownAndLandedThroughRollout() {
        val engine = GameEngine(quiet, seed = 1)
        engine.place(listOf(jet(1, meadow(150.0, 400.0), PI / 2).copy(hasEnteredViewport = true)))
        engine.beginPath(1)
        engine.extendPath(1, meadow(250.0, 400.0))
        engine.extendPath(1, meadow(400.0, 400.0))
        assertIs<AircraftStatus.ClearedToLand>(engine.aircraft(1).status)
        var sawRollout = false
        engine.run(10.0) { state ->
            val plane = state.aircraft.firstOrNull { it.id == 1 } ?: return@run
            when (plane.status) {
                is AircraftStatus.ClearedToLand -> assertEquals(AltitudeState.InFlight, plane.altitude)
                is AircraftStatus.RollingOut -> {
                    sawRollout = true
                    assertEquals(AltitudeState.Landed, plane.altitude)
                }
                else -> Unit
            }
        }
        assertTrue(sawRollout, "the jet touched down and rolled out")
        assertEquals(GameStatus.Playing, engine.state.value.status)
    }

    @Test
    fun twoLandedAirplanesThatMeetOnTheRunwayCollide() {
        val engine = GameEngine(quiet, seed = 1)
        val east = jet(1, meadow(600.0, 400.0), PI / 2).copy(
            altitude = AltitudeState.Landed,
            status = AircraftStatus.RollingOut(PI / 2, 500.0, "r"),
        )
        val west = jet(2, meadow(680.0, 400.0), -PI / 2).copy(
            altitude = AltitudeState.Landed,
            status = AircraftStatus.RollingOut(-PI / 2, 500.0, "r"),
        )
        engine.place(listOf(east, west))
        engine.run(2.0)
        val status = assertIs<GameStatus.GameOver>(engine.state.value.status)
        assertEquals(GameOverReason.Collision, status.reason)
    }

    @Test
    fun aJetLandingRightBehindALightPropInTheSameDirectionDoesNotCollide() {
        val engine = GameEngine(quiet, seed = 1)
        val runway = level.runways.single().id
        val prop = Aircraft(1, AircraftType.LightProp, meadow(420.0, 400.0), PI / 2, hasEnteredViewport = true).copy(
            altitude = AltitudeState.Landed,
            status = AircraftStatus.RollingOut(PI / 2, 360.0, runway),
        )
        engine.place(listOf(prop, jet(2, meadow(150.0, 400.0), PI / 2).copy(hasEnteredViewport = true)))
        engine.beginPath(2)
        engine.extendPath(2, meadow(250.0, 400.0))
        engine.extendPath(2, meadow(400.0, 400.0))
        assertIs<AircraftStatus.ClearedToLand>(engine.aircraft(2).status)
        engine.run(20.0)
        assertEquals(GameStatus.Playing, engine.state.value.status)
        assertTrue(engine.state.value.aircraft.isEmpty(), "both rollouts finished")
    }

    @Test
    fun airplanesLandingFromOppositeEndsOfATwoWayRunwayCollide() {
        val engine = GameEngine(quiet, seed = 1)
        engine.place(
            listOf(
                jet(1, meadow(200.0, 400.0), PI / 2).copy(hasEnteredViewport = true),
                jet(2, meadow(1080.0, 400.0), -PI / 2).copy(hasEnteredViewport = true),
            ),
        )
        engine.beginPath(1)
        engine.extendPath(1, meadow(400.0, 400.0))
        engine.beginPath(2)
        engine.extendPath(2, meadow(880.0, 400.0))
        engine.run(20.0)
        val status = assertIs<GameStatus.GameOver>(engine.state.value.status)
        assertEquals(GameOverReason.Collision, status.reason)
    }

    @Test
    fun aLandingAndACollisionInTheSameUpdateAreBothCounted() {
        val engine = GameEngine(quiet, seed = 1)
        val pad = level.helipads.single().position
        engine.place(listOf(Aircraft(1, AircraftType.Helicopter, pad + Vec2(0.0, -100.0), PI, hasEnteredViewport = true)))
        engine.beginPath(1)
        engine.extendPath(1, pad)
        val cleared = engine.aircraft(1)
        assertIs<AircraftStatus.ClearedToLand>(cleared.status)
        val touchingDown = cleared.copy(position = pad, path = cleared.path.copy(points = emptyList()))
        engine.place(listOf(touchingDown, jet(2, Vec2(300.0, 300.0)), jet(3, Vec2(300.0, 300.0))))
        engine.update(dt)
        val state = engine.state.value
        assertEquals(GameOverReason.Collision, assertIs<GameStatus.GameOver>(state.status).reason)
        assertEquals(1, state.landings)
        assertEquals(1, state.landedByType[AircraftType.Helicopter])
    }

    private fun jet(id: Int, position: Vec2, heading: Double = 0.0) = Aircraft(id, AircraftType.Jet, position, heading)

    /** Gaps between aircraft are written in units before the world zoom-out and follow it. */
    private val zoom = LevelDefaults.WORLD_ZOOM

    private fun GameEngine.aircraft(id: Int) = assertNotNull(state.value.aircraft.firstOrNull { it.id == id })
}
