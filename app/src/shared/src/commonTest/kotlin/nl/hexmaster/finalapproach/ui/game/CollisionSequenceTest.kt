package nl.hexmaster.finalapproach.ui.game

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class CollisionSequenceTest {

    private fun sequence(reduceMotion: Boolean = false) = CollisionSequence(listOf(1, 2), 42f, reduceMotion)

    private fun CollisionSequence.at(ms: Float) = apply { advance(ms) }

    @Test
    fun theBlackoutStartsClear() {
        val sequence = sequence()
        assertEquals(0f, sequence.blackoutAlpha)
        assertFalse(sequence.dialogVisible)
    }

    @Test
    fun theBlackoutIsPartwayHalfwayThroughTheFade() {
        val alpha = sequence().at(500f).blackoutAlpha
        assertTrue(alpha > 0f && alpha < 1f, "alpha $alpha")
    }

    @Test
    fun theBlackoutIsOpaqueAtTheEndOfTheFadeAndStaysSo() {
        val sequence = sequence().at(1000f)
        assertEquals(1f, sequence.blackoutAlpha)
        assertEquals(1f, sequence.at(500f).blackoutAlpha)
        assertEquals(1f, sequence.at(5000f).blackoutAlpha)
    }

    @Test
    fun theDialogWaitsForTheFadeAndTheHold() {
        val sequence = sequence().at(1500f)
        assertFalse(sequence.dialogVisible)
        sequence.advance(499f)
        assertFalse(sequence.dialogVisible)
        sequence.advance(1f)
        assertTrue(sequence.dialogVisible)
    }

    @Test
    fun reducedMotionBlacksOutAtOnceAndShowsTheDialogAfterTheHold() {
        val sequence = sequence(reduceMotion = true)
        assertEquals(1f, sequence.blackoutAlpha)
        assertFalse(sequence.dialogVisible)
        sequence.advance(999f)
        assertFalse(sequence.dialogVisible)
        sequence.advance(1f)
        assertTrue(sequence.dialogVisible)
    }

    @Test
    fun itKeepsTheCollidingAircraftAndTheFrozenAngle() {
        val sequence = sequence()
        assertEquals(listOf(1, 2), sequence.aircraftIds)
        assertEquals(42f, sequence.ringAngleDegrees)
    }
}
