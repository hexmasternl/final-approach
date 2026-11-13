package nl.hexmaster.finalapproach.engine

import nl.hexmaster.finalapproach.domain.LevelConfig
import nl.hexmaster.finalapproach.domain.Levels
import nl.hexmaster.finalapproach.domain.RunwayMode

/** Meadow Field with its runway two-way: tests that land from either end use this layout. */
internal val twoWayMeadowField: LevelConfig = Levels.meadowField.let { level ->
    level.copy(runways = level.runways.map { it.copy(mode = RunwayMode.TwoWay) })
}
