package nl.hexmaster.finalapproach.engine

import nl.hexmaster.finalapproach.domain.LevelDefaults
import kotlin.test.Test
import kotlin.test.assertEquals

class EngineConfigTest {
    private val zoom = LevelDefaults.WORLD_ZOOM

    @Test
    fun fingerDistancesAreTheirFormerValueTimesTheWorldZoom() {
        val config = EngineConfig()
        assertEquals(6.0 * zoom, config.minReachRadius)
        assertEquals(12.0 * zoom, config.minPointSpacing)
        assertEquals(4.0 * zoom, config.smoothing.tolerance)
    }

    @Test
    fun fractionsAndCountsDoNotFollowTheZoom() {
        val smoothing = PathSmoothingConfig()
        assertEquals(0.1, smoothing.spacingTurnFraction)
        assertEquals(1.1, smoothing.minRadiusFactor)
        assertEquals(0.25, smoothing.lookaheadTurnFraction)
        assertEquals(2, smoothing.chaikinIterations)
    }
}
