package nl.hexmaster.finalapproach.domain

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class PlayerSettingsTest {

    @Test
    fun aFreshInstallHasSoundOnAndReducedMotionFollowingTheDevice() {
        val settings = PlayerSettings()
        assertTrue(settings.soundEffects)
        assertEquals(ReduceMotionChoice.Device, settings.reduceMotion)
        assertTrue(settings.keepScreenOn)
    }

    @Test
    fun deviceFollowsTheDevice() {
        assertTrue(ReduceMotionChoice.Device.resolve(device = true))
        assertFalse(ReduceMotionChoice.Device.resolve(device = false))
    }

    @Test
    fun onAlwaysReducesMotion() {
        assertTrue(ReduceMotionChoice.On.resolve(device = true))
        assertTrue(ReduceMotionChoice.On.resolve(device = false))
    }

    @Test
    fun offNeverReducesMotion() {
        assertFalse(ReduceMotionChoice.Off.resolve(device = true))
        assertFalse(ReduceMotionChoice.Off.resolve(device = false))
    }
}
