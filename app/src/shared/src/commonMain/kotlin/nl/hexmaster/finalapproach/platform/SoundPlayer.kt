package nl.hexmaster.finalapproach.platform

/** A sound effect the game can ask the platform to play. */
enum class SoundEffect {
    /** An aircraft was cleared to land. */
    Success,
}

/**
 * Plays short sound effects. The host implements it with a platform audio API. [play] never
 * blocks and never throws: when the sound is not loaded yet, or the platform has no player,
 * it plays nothing.
 */
interface SoundPlayer {
    fun play(effect: SoundEffect)
}

/** The player of a platform without sound, and of tests: nothing is ever played. */
object SilentSoundPlayer : SoundPlayer {
    override fun play(effect: SoundEffect) = Unit
}
