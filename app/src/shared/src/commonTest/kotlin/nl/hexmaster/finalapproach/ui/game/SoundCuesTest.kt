package nl.hexmaster.finalapproach.ui.game

import nl.hexmaster.finalapproach.domain.AircraftType
import nl.hexmaster.finalapproach.domain.LandingZones
import nl.hexmaster.finalapproach.domain.Levels
import nl.hexmaster.finalapproach.engine.GameEvent
import nl.hexmaster.finalapproach.platform.SoundEffect
import kotlin.test.Test
import kotlin.test.assertEquals

class SoundCuesTest {

    private val zone = LandingZones.of(Levels.meadowField).first()

    @Test
    fun aClearancePlaysOneSuccess() {
        assertEquals(listOf(SoundEffect.Success), SoundCues.of(listOf(GameEvent.ClearedToLand(1, zone))))
    }

    @Test
    fun twoClearancesPlayTwoSuccesses() {
        val events = listOf(GameEvent.ClearedToLand(1, zone), GameEvent.ClearedToLand(2, zone))
        assertEquals(listOf(SoundEffect.Success, SoundEffect.Success), SoundCues.of(events))
    }

    @Test
    fun otherEventsMakeNoSound() {
        val events = listOf(GameEvent.Landed(1, AircraftType.Jet), GameEvent.Collided(setOf(1, 2)), GameEvent.LeftArea(3))
        assertEquals(emptyList(), SoundCues.of(events))
    }
}
