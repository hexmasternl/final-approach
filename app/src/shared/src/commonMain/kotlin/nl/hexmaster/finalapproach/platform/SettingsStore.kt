package nl.hexmaster.finalapproach.platform

import nl.hexmaster.finalapproach.domain.PlayerSettings

/** Where the player's settings are kept, on this device. The host implements it. */
interface SettingsStore {
    /** The stored settings; anything missing or unreadable is the default. */
    fun load(): PlayerSettings

    /** Stores [settings]. */
    fun save(settings: PlayerSettings)
}

/** A [SettingsStore] that keeps the settings in memory only; used by tests and as a safe default. */
class InMemorySettingsStore(private var stored: PlayerSettings = PlayerSettings()) : SettingsStore {
    /** How many times [save] was called. */
    var saveCalls = 0
        private set

    override fun load(): PlayerSettings = stored

    override fun save(settings: PlayerSettings) {
        saveCalls++
        stored = settings
    }
}
