package nl.hexmaster.finalapproach.ui.components

import kotlin.test.Test
import kotlin.test.assertEquals

class ScoreFormatTest {
    @Test
    fun scoresBelowOneThousandAreNotGrouped() {
        assertEquals("0", formatScore(0))
        assertEquals("450", formatScore(450))
        assertEquals("999", formatScore(999))
    }

    @Test
    fun thousandsAreGroupedWithAThinSpace() {
        assertEquals("1\u2009000", formatScore(1000))
        assertEquals("12\u2009450", formatScore(12450))
        assertEquals("1\u2009234\u2009567", formatScore(1234567))
    }

    @Test
    fun negativeScoresKeepTheirSign() {
        assertEquals("-1\u2009000", formatScore(-1000))
        assertEquals("-2\u2009147\u2009483\u2009648", formatScore(Int.MIN_VALUE))
    }
}
