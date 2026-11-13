package nl.hexmaster.finalapproach.engine

import nl.hexmaster.finalapproach.domain.LevelDefaults
import kotlin.test.Test
import kotlin.test.assertEquals

class EngineConfigTest {
    private val zoom = LevelDefaults.WORLD_ZOOM

    @Test
    fun fingerDistancesAreTheirFormerValueTimesTheWorldZoom() {
        val config = EngineConfig()
        assertEquals(12.0 * zoom, config.minPointSpacing)
        assertEquals(4.0 * zoom, config.smoothing.tolerance)
    }

    @Test
    fun countsDoNotFollowTheZoom() {
        assertEquals(2, PathSmoothingConfig().chaikinIterations)
    }
}
