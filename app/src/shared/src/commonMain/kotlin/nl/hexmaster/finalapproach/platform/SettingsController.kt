package nl.hexmaster.finalapproach.platform

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import nl.hexmaster.finalapproach.domain.PlayerSettings

/**
 * The one source of truth for the player's settings. It loads from [store] once, publishes the
 * current value, and writes every change through to the store at once.
 */
class SettingsController(private val store: SettingsStore) {
    private val mutableSettings = MutableStateFlow(store.load())

    val settings: StateFlow<PlayerSettings> = mutableSettings.asStateFlow()

    /** Applies [transform] to the current settings, saves the result and publishes it. */
    fun update(transform: (PlayerSettings) -> PlayerSettings) {
        val next = transform(mutableSettings.value)
        store.save(next)
        mutableSettings.value = next
    }
}
