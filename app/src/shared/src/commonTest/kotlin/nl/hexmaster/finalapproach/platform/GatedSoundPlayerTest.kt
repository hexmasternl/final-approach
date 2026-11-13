package nl.hexmaster.finalapproach.platform

import kotlin.test.Test
import kotlin.test.assertEquals

class GatedSoundPlayerTest {

    @Test
    fun withSoundOffNothingIsPlayed() {
        val inner = RecordingSoundPlayer()
        GatedSoundPlayer(inner) { false }.play(SoundEffect.Success)
        assertEquals(emptyList(), inner.played)
    }

    @Test
    fun withSoundOnItIsPlayedOnce() {
        val inner = RecordingSoundPlayer()
        GatedSoundPlayer(inner) { true }.play(SoundEffect.Success)
        assertEquals(listOf(SoundEffect.Success), inner.played)
    }

    @Test
    fun switchingBackOnLetsLaterPlaysThrough() {
        val inner = RecordingSoundPlayer()
        var on = false
        val gated = GatedSoundPlayer(inner) { on }
        gated.play(SoundEffect.Success)
        on = true
        gated.play(SoundEffect.Success)
        assertEquals(listOf(SoundEffect.Success), inner.played)
    }
}
