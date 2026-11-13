package nl.hexmaster.finalapproach.platform

/**
 * Wraps a [SoundPlayer] and lets a sound through only while [enabled] is true, checked at play
 * time. A sound that is already playing is not cut off; nothing new starts while it is off.
 */
class GatedSoundPlayer(
    private val inner: SoundPlayer,
    private val enabled: () -> Boolean,
) : SoundPlayer {
    override fun play(effect: SoundEffect) {
        if (enabled()) inner.play(effect)
    }
}
