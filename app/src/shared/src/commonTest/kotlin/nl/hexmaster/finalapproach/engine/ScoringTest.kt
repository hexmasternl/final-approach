package nl.hexmaster.finalapproach.engine

import nl.hexmaster.finalapproach.domain.AircraftType
import nl.hexmaster.finalapproach.domain.AircraftTuning
import nl.hexmaster.finalapproach.domain.EffectiveAircraftSpecs
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class ScoringTest {

    private val specs = EffectiveAircraftSpecs.Defaults

    @Test
    fun helicopterAwardsTheLeastAndJumboTheMost() {
        val afterHeli = Scoring.apply(0, listOf(GameEvent.Landed(1, AircraftType.Helicopter)), specs)
        val afterJumbo = Scoring.apply(afterHeli, listOf(GameEvent.Landed(2, AircraftType.Jumbo)), specs)
        val heliAward = afterHeli
        val jumboAward = afterJumbo - afterHeli
        assertEquals(specs.pointsOf(AircraftType.Helicopter), heliAward)
        assertEquals(specs.pointsOf(AircraftType.Jumbo), jumboAward)
        AircraftType.entries.forEach {
            assertTrue(specs.pointsOf(it) >= heliAward && specs.pointsOf(it) <= jumboAward)
        }
    }

    @Test
    fun overriddenPointsAreUsed() {
        val tuned = AircraftTuning(pointsOverrides = mapOf(AircraftType.Jet to 99)).resolve()
        assertEquals(99, Scoring.apply(0, listOf(GameEvent.Landed(1, AircraftType.Jet)), tuned))
    }

    @Test
    fun collisionAndMissLeaveTheScoreUnchanged() {
        val events = listOf(GameEvent.Collided(setOf(1, 2)), GameEvent.LeftArea(3))
        assertEquals(120, Scoring.apply(120, events, specs))
    }
}
