package nl.hexmaster.finalapproach.domain

/** What the player chose on the settings page. Every field has a default: a fresh install plays with them. */
data class PlayerSettings(
    val soundEffects: Boolean = true,
    val reduceMotion: ReduceMotionChoice = ReduceMotionChoice.Device,
)

/** How the player wants reduced motion decided: follow the device, or force it on or off. */
enum class ReduceMotionChoice {
    Device,
    On,
    Off,
    ;

    /** True when animation should be reduced, given what the device [device] asks for. */
    fun resolve(device: Boolean): Boolean = when (this) {
        Device -> device
        On -> true
        Off -> false
    }
}
