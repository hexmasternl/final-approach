package nl.hexmaster.finalapproach.engine

import nl.hexmaster.finalapproach.domain.Aircraft
import nl.hexmaster.finalapproach.domain.AircraftStatus
import nl.hexmaster.finalapproach.domain.AircraftType
import nl.hexmaster.finalapproach.domain.Levels
import nl.hexmaster.finalapproach.domain.Vec2
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertIs
import kotlin.test.assertNotEquals
import kotlin.test.assertNotNull
import kotlin.test.assertNull
import kotlin.test.assertTrue

class TutorialEngineTest {

    private val level = Levels.meadowField
    private val dt = 0.05

    private fun engine() = GameEngine(level, seed = 1, tutorialActive = true)

    private fun GameEngine.tutorial() = assertNotNull(state.value.tutorial)

    private fun GameEngine.aircraft(type: AircraftType) = state.value.aircraft.first { it.type == type }

    /** Runs updates until [done] holds, or fails after [seconds]. */
    private fun GameEngine.runUntil(seconds: Double = 120.0, done: (GameState) -> Boolean) {
        var time = 0.0
        while (!done(state.value)) {
            check(time < seconds) { "gave up waiting; tutorial ${state.value.tutorial}" }
            update(dt)
            time += dt
        }
    }

    private fun GameEngine.toStep(step: TutorialStep, phase: TutorialPhase) = runUntil { it.tutorial?.step == step && it.tutorial.phase == phase }

    /** Goal and runway explanations tapped away: the light prop is flying in. */
    private fun GameEngine.pastTheExplanations() {
        acknowledgeTutorial()
        acknowledgeTutorial()
    }

    /** A path from the aircraft's side of the field into runway 27 from the east, lined up. */
    private fun GameEngine.landPath(id: Int, vararg lead: Vec2) {
        beginPath(id)
        (lead.toList() + listOf(Vec2(1520.0, 630.0), Vec2(1420.0, 630.0), Vec2(1320.0, 630.0), Vec2(1220.0, 630.0))).forEach { extendPath(id, it) }
        endPath(id)
    }

    private fun GameEngine.clearLightProp() {
        toStep(TutorialStep.LightProp, TutorialPhase.Drawing)
        val plane = aircraft(AircraftType.LightProp)
        select(plane.position, 80.0)
        landPath(plane.id, Vec2(1480.0, 400.0), Vec2(1530.0, 520.0))
    }

    private fun GameEngine.clearTurboprop() {
        acknowledgeTutorial()
        val plane = aircraft(AircraftType.Turboprop)
        select(plane.position, 80.0)
        landPath(plane.id, Vec2(1600.0, 950.0), Vec2(1800.0, 900.0), Vec2(1850.0, 700.0), Vec2(1750.0, 560.0), Vec2(1650.0, 630.0))
    }

    @Test
    fun aTutorialStartsAtTheGoalWithNoAircraftAndNothingMoves() {
        val engine = engine()
        val tutorial = engine.tutorial()
        assertEquals(TutorialStep.Goal, tutorial.step)
        assertEquals(1, tutorial.stepNumber)
        assertTrue(tutorial.holding)
        assertTrue(engine.state.value.aircraft.isEmpty())
        engine.update(1.0)
        assertEquals(0.0, engine.state.value.levelTime)
        assertTrue(engine.state.value.aircraft.isEmpty())
    }

    @Test
    fun stepsRunInOrderOneToSix() {
        val engine = engine()
        assertEquals(1, engine.tutorial().stepNumber)
        engine.acknowledgeTutorial()
        assertEquals(TutorialStep.Runway, engine.tutorial().step)
        assertEquals(2, engine.tutorial().stepNumber)
        assertEquals(TutorialHighlight.Runway("09-27"), engine.tutorial().highlight)
        engine.acknowledgeTutorial()
        assertEquals(TutorialStep.LightProp, engine.tutorial().step)
        engine.clearLightProp()
        assertEquals(TutorialStep.Turboprop, engine.tutorial().step)
        engine.toStep(TutorialStep.Turboprop, TutorialPhase.Explaining)
        engine.clearTurboprop()
        assertEquals(TutorialStep.Helipad, engine.tutorial().step)
        assertEquals(5, engine.tutorial().stepNumber)
        assertEquals(TutorialHighlight.Helipad("h1"), engine.tutorial().highlight)
        engine.acknowledgeTutorial()
        assertEquals(TutorialStep.Helicopter, engine.tutorial().step)
        assertEquals(6, engine.tutorial().stepNumber)
    }

    @Test
    fun theNormalSpawnerStaysOffAndOnlyTheScriptedAircraftComeIn() {
        val engine = engine()
        engine.pastTheExplanations()
        engine.runUntil { it.tutorial?.phase == TutorialPhase.Drawing }
        assertEquals(listOf(AircraftType.LightProp), engine.state.value.aircraft.map { it.type })
    }

    @Test
    fun inputIsIgnoredWhileTheLightPropIsNotWellInView() {
        val engine = engine()
        engine.pastTheExplanations()
        val plane = engine.aircraft(AircraftType.LightProp)
        assertTrue(!engine.tutorial().touchEnabled)
        assertNull(engine.select(plane.position, 500.0))
        engine.beginPath(plane.id)
        engine.extendPath(plane.id, Vec2(1500.0, 400.0))
        assertTrue(engine.aircraft(AircraftType.LightProp).path.points.isEmpty())
    }

    @Test
    fun theLightPropDrawingPhaseHoldsTheGameButAcceptsInput() {
        val engine = engine()
        engine.pastTheExplanations()
        engine.toStep(TutorialStep.LightProp, TutorialPhase.Drawing)
        val tutorial = engine.tutorial()
        assertTrue(tutorial.holding)
        assertTrue(tutorial.touchEnabled)
        assertNotNull(tutorial.demo)
        val before = engine.state.value
        engine.update(1.0)
        assertEquals(before.aircraft, engine.state.value.aircraft)
        val plane = engine.aircraft(AircraftType.LightProp)
        assertEquals(plane.id, engine.select(plane.position, 80.0))
    }

    @Test
    fun aPathThatDoesNotLandKeepsTheDrawingPhase() {
        val engine = engine()
        engine.pastTheExplanations()
        engine.toStep(TutorialStep.LightProp, TutorialPhase.Drawing)
        val plane = engine.aircraft(AircraftType.LightProp)
        engine.select(plane.position, 80.0)
        engine.beginPath(plane.id)
        engine.extendPath(plane.id, Vec2(1500.0, 400.0))
        engine.extendPath(plane.id, Vec2(1500.0, 300.0))
        engine.endPath(plane.id)
        assertEquals(TutorialPhase.Drawing, engine.tutorial().phase)
        assertEquals(TutorialStep.LightProp, engine.tutorial().step)
    }

    @Test
    fun aLandingPathStartsTheTurbopropStep() {
        val engine = engine()
        engine.pastTheExplanations()
        engine.clearLightProp()
        val tutorial = engine.tutorial()
        assertEquals(TutorialStep.Turboprop, tutorial.step)
        assertEquals(TutorialPhase.Tracking, tutorial.phase)
        assertTrue(tutorial.touchEnabled)
        assertTrue(!tutorial.holding)
        assertEquals(setOf(AircraftType.LightProp, AircraftType.Turboprop), engine.state.value.aircraft.map { it.type }.toSet())
        assertEquals(engine.aircraft(AircraftType.Turboprop).id, tutorial.trackedAircraftId)
    }

    @Test
    fun theTurbopropExplanationHoldsAndTheHelipadStepWaitsForItsClearance() {
        val engine = engine()
        engine.pastTheExplanations()
        engine.clearLightProp()
        engine.toStep(TutorialStep.Turboprop, TutorialPhase.Explaining)
        assertTrue(engine.tutorial().holding)
        val frozen = engine.state.value.aircraft
        engine.update(1.0)
        assertEquals(frozen, engine.state.value.aircraft)
        engine.acknowledgeTutorial()
        assertEquals(TutorialPhase.AwaitingClearance, engine.tutorial().phase)
        engine.update(dt)
        assertEquals(TutorialStep.Turboprop, engine.tutorial().step)
        engine.clearTurboprop()
        assertEquals(TutorialStep.Helipad, engine.tutorial().step)
    }

    @Test
    fun completionDoesNotWaitForAnyLanding() {
        val engine = engine()
        engine.pastTheExplanations()
        engine.clearLightProp()
        engine.toStep(TutorialStep.Turboprop, TutorialPhase.Explaining)
        engine.clearTurboprop()
        engine.acknowledgeTutorial()
        engine.toStep(TutorialStep.Helicopter, TutorialPhase.Explaining)
        engine.acknowledgeTutorial()
        assertNull(engine.state.value.tutorial)
        assertTrue(engine.state.value.aircraft.isNotEmpty())
        engine.update(dt)
        assertTrue(engine.state.value.events.contains(GameEvent.TutorialCompleted))
        engine.update(dt)
        assertTrue(GameEvent.TutorialCompleted !in engine.state.value.events)
    }

    @Test
    fun aTurbopropThatLeavesRestartsTheStepFromItsStart() {
        val engine = engine()
        engine.pastTheExplanations()
        engine.clearLightProp()
        val atStepStart = engine.state.value
        engine.toStep(TutorialStep.Turboprop, TutorialPhase.Explaining)
        engine.acknowledgeTutorial()
        engine.runUntil { it.tutorial?.retryReason != null }
        val state = engine.state.value
        assertEquals(GameStatus.Playing, state.status)
        assertEquals(GameOverReason.LeftArea, state.tutorial?.retryReason)
        assertEquals(TutorialStep.Turboprop, state.tutorial?.step)
        assertEquals(TutorialPhase.Tracking, state.tutorial?.phase)
        assertEquals(atStepStart.score, state.score)
        val lightProp = state.aircraft.first { it.type == AircraftType.LightProp }
        assertEquals(atStepStart.aircraft.first { it.type == AircraftType.LightProp }, lightProp)
        val turboprop = state.aircraft.first { it.type == AircraftType.Turboprop }
        assertEquals(level.tutorial?.turboprop?.position, turboprop.position)
    }

    @Test
    fun aCollisionDuringTheHelicopterStepRestartsItWithoutGameOver() {
        val engine = engine()
        engine.pastTheExplanations()
        engine.clearLightProp()
        engine.toStep(TutorialStep.Turboprop, TutorialPhase.Explaining)
        engine.clearTurboprop()
        engine.acknowledgeTutorial()
        val atStepStart = engine.state.value
        assertEquals(TutorialStep.Helicopter, atStepStart.tutorial?.step)
        val first = Aircraft(50, AircraftType.LightProp, Vec2(900.0, 400.0), 0.0, hasEnteredViewport = true)
        engine.place(listOf(first, first.copy(id = 51)))
        engine.update(dt)
        val state = engine.state.value
        assertEquals(GameStatus.Playing, state.status)
        assertEquals(GameOverReason.Collision, state.tutorial?.retryReason)
        assertEquals(TutorialStep.Helicopter, state.tutorial?.step)
        assertEquals(atStepStart.score, state.score)
        assertTrue(state.aircraft.any { it.type == AircraftType.Helicopter })
        assertTrue(state.aircraft.none { it.id == 50 || it.id == 51 })
    }

    @Test
    fun skippingOnTheFirstStepHandsOverToTheNormalLevel() {
        val engine = engine()
        engine.skipTutorial()
        assertNull(engine.state.value.tutorial)
        engine.update(dt)
        assertTrue(engine.state.value.events.contains(GameEvent.TutorialCompleted))
        assertTrue(engine.state.value.levelTime > 0.0)
    }

    @Test
    fun theRampCountsFromTheEndOfTheTutorial() {
        val engine = GameEngine(
            level.copy(spawn = level.spawn.copy(firstSpawnDelay = 100_000.0)),
            seed = 1,
            tutorialActive = true,
        )
        engine.skipTutorial()
        val last = level.phases.last()
        engine.update(dt)
        engine.update(179.0 - engine.difficultyTime)
        assertTrue(engine.state.value.status == GameStatus.Playing, "level still running")
        assertEquals(179.0, engine.difficultyTime, 1e-6)
        assertEquals(last, level.phaseAt(engine.difficultyTime))
        engine.update(1.0)
        assertEquals(180.0, engine.difficultyTime, 1e-6)
        assertEquals(last.startSeconds + 60.0, level.phaseAt(engine.difficultyTime).startSeconds)
    }

    @Test
    fun completionIsReportedToTheCallbackAtOnceEvenWithoutAnotherUpdate() {
        var completed = 0
        val engine = GameEngine(level, seed = 1, tutorialActive = true, onTutorialCompleted = { completed++ })
        engine.skipTutorial()
        engine.restart(seed = 2)
        assertEquals(1, completed)
    }

    @Test
    fun skippingKeepsTheAircraftInTheAir() {
        val engine = engine()
        engine.pastTheExplanations()
        engine.clearLightProp()
        val inAir = engine.state.value.aircraft.map { it.id }
        engine.skipTutorial()
        assertNull(engine.state.value.tutorial)
        engine.update(dt)
        assertTrue(engine.state.value.aircraft.map { it.id }.containsAll(inAir))
    }

    @Test
    fun theFirstNormalSpawnComesAfterTheFirstSpawnDelayFromHandOver() {
        val engine = engine()
        engine.skipTutorial()
        engine.run(level.spawn.firstSpawnDelay - 0.5)
        assertTrue(engine.state.value.aircraft.isEmpty())
        engine.run(level.spawn.firstSpawnDelay + 10.0)
        assertTrue(engine.state.value.aircraft.isNotEmpty())
        assertTrue(engine.state.value.aircraft.all { it.type in level.phases.first().weights.keys })
    }

    @Test
    fun aCollisionAfterTheHandOverEndsTheLevel() {
        val engine = engine()
        engine.skipTutorial()
        val first = Aircraft(50, AircraftType.LightProp, Vec2(900.0, 400.0), 0.0, hasEnteredViewport = true)
        engine.place(listOf(first, first.copy(id = 51)))
        engine.update(dt)
        assertIs<GameStatus.GameOver>(engine.state.value.status)
    }

    @Test
    fun restartDuringTheTutorialStartsItAgainFromStepOne() {
        val engine = engine()
        engine.pastTheExplanations()
        engine.clearLightProp()
        assertNotEquals(TutorialStep.Goal, engine.tutorial().step)
        engine.restart(seed = 2)
        assertEquals(TutorialStep.Goal, engine.tutorial().step)
        assertTrue(engine.state.value.aircraft.isEmpty())
    }

    @Test
    fun restartAfterTheTutorialIsANormalLevel() {
        val engine = engine()
        engine.skipTutorial()
        engine.restart(seed = 2)
        assertNull(engine.state.value.tutorial)
    }

    @Test
    fun pauseAndResumeDuringAnExplanationStayHeld() {
        val engine = engine()
        engine.pause()
        engine.resume()
        assertTrue(engine.tutorial().holding)
        engine.update(1.0)
        assertEquals(0.0, engine.state.value.levelTime)
    }

    @Test
    fun aLevelWithoutTheTutorialFlagPlaysNormally() {
        val engine = GameEngine(level, seed = 1)
        assertNull(engine.state.value.tutorial)
        engine.update(dt)
        assertTrue(engine.state.value.levelTime > 0.0)
    }

    private fun GameEngine.run(seconds: Double) {
        repeat((seconds / dt).toInt()) { update(dt) }
    }

    @Test
    fun aClearedAircraftHasTheClearedStatus() {
        val engine = engine()
        engine.pastTheExplanations()
        engine.clearLightProp()
        assertTrue(engine.state.value.aircraft.any { it.status is AircraftStatus.ClearedToLand })
    }
}
