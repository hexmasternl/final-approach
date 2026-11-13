package nl.hexmaster.finalapproach.platform

import nl.hexmaster.finalapproach.domain.PlayerSettings
import nl.hexmaster.finalapproach.domain.ReduceMotionChoice
import kotlin.test.Test
import kotlin.test.assertEquals

class SettingsControllerTest {

    @Test
    fun startsWithWhatTheStoreHolds() {
        val store = InMemorySettingsStore(PlayerSettings(soundEffects = false))
        assertEquals(PlayerSettings(soundEffects = false), SettingsController(store).settings.value)
    }

    @Test
    fun anUpdateIsEmittedAndSaved() {
        val store = InMemorySettingsStore()
        val controller = SettingsController(store)
        controller.update { it.copy(reduceMotion = ReduceMotionChoice.On) }
        assertEquals(ReduceMotionChoice.On, controller.settings.value.reduceMotion)
        assertEquals(ReduceMotionChoice.On, store.load().reduceMotion)
        assertEquals(1, store.saveCalls)
    }

    @Test
    fun aNewControllerOnTheSameStoreLoadsTheSavedValue() {
        val store = InMemorySettingsStore()
        SettingsController(store).update { it.copy(soundEffects = false, reduceMotion = ReduceMotionChoice.Off) }
        val restarted = SettingsController(store)
        assertEquals(PlayerSettings(soundEffects = false, reduceMotion = ReduceMotionChoice.Off), restarted.settings.value)
    }
}
