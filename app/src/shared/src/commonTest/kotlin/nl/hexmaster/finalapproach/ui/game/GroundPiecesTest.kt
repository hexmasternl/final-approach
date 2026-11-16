package nl.hexmaster.finalapproach.ui.game

import nl.hexmaster.finalapproach.domain.Levels
import nl.hexmaster.finalapproach.domain.SceneryDefaults
import nl.hexmaster.finalapproach.domain.SceneryItem
import nl.hexmaster.finalapproach.domain.SceneryKind
import nl.hexmaster.finalapproach.ui.sprites.GroundMetrics
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class GroundPiecesTest {
    private fun taxiwayOf(level: nl.hexmaster.finalapproach.domain.LevelConfig): SceneryItem =
        level.scenery.first { it.kind == SceneryKind.Taxiway }

    @Test
    fun theHoldShortBarsLieAtTheEndNearTheRunway() {
        // Meadow: the runway lies south of the taxiway; island: south too; savanna: the taxiway's north end meets runway 09R.
        assertEquals(1f, towardsRunway(taxiwayOf(Levels.meadowField), Levels.meadowField))
        assertEquals(1f, towardsRunway(taxiwayOf(Levels.islandAirport), Levels.islandAirport))
        assertEquals(-1f, towardsRunway(taxiwayOf(Levels.savannaDawn), Levels.savannaDawn))
    }

    @Test
    fun theHoldShortBarsFollowTheTaxiwayWhenItIsTurned() {
        val level = Levels.meadowField
        val turned = taxiwayOf(level).copy(rotation = Math.PI)
        assertEquals(-1f, towardsRunway(turned, level))
    }

    @Test
    fun theDomainBeachIsTheDesignSystemBeach() {
        assertEquals(GroundMetrics.Ground.Island.beach.toDouble(), SceneryDefaults.BEACH_WIDTH)
    }

    @Test
    fun everyPieceKindWithItsOwnDrawingIsFlatGround() {
        assertTrue(groundPieceKinds.all { it.ground }, "pieces with their own ground drawing lie flat")
        assertFalse(SceneryKind.Jetty in groundPieceKinds, "the jetty is drawn as a sprite")
        assertTrue(SceneryKind.Taxiway in groundPieceKinds)
    }
}
