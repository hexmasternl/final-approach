package nl.hexmaster.finalapproach.ui.game

import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import nl.hexmaster.finalapproach.domain.LevelDefaults
import nl.hexmaster.finalapproach.domain.Levels
import nl.hexmaster.finalapproach.domain.Vec2
import nl.hexmaster.finalapproach.input.WorldViewport
import nl.hexmaster.finalapproach.ui.components.touchInset
import nl.hexmaster.finalapproach.ui.theme.FaDimens
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

/** The HUD stays inside the top band kept clear of landing zones on the smallest supported phone. */
class HudFitTest {
    @Test
    fun hudBottomIsTheRealBottomOfTheHudRow() {
        listOf(0.dp, 24.dp, 48.dp).forEach { topInset ->
            val rowTop = topInset + hudRowTopPadding
            // The touch target of the compact icon button fills the row.
            val touchBottom = rowTop + hudRowHeight
            // The strips and the button are drawn in the middle of the row, on a contact shadow.
            val drawnBottom = rowTop + (hudRowHeight + FaDimens.stripHeight) / 2 + FaDimens.contactOffsetField
            assertEquals(maxOf(touchBottom, drawnBottom), hudBottom(topInset), "top inset $topInset")
        }
    }

    @Test
    fun hudRowIsATouchTargetAroundTheCompactIconButton() {
        assertEquals(FaDimens.stripHeight + touchInset(FaDimens.stripHeight) * 2, hudRowHeight)
        assertEquals(FaDimens.space4, hudRowTopPadding + touchInset(FaDimens.stripHeight))
    }

    private class Screen(val name: String, val width: Dp, val height: Dp, val topInset: Dp)

    private val smallestPhone = listOf(
        Screen("landscape", 640.dp, 360.dp, 0.dp),
        Screen("portrait", 360.dp, 640.dp, 48.dp),
    )

    @Test
    fun hudStaysInsideTheHudBandOnEveryLevel() {
        Levels.all.forEach { level ->
            smallestPhone.forEach { screen ->
                val viewport = WorldViewport(
                    level.world.width,
                    level.world.height,
                    screen.width.value.toDouble(),
                    screen.height.value.toDouble(),
                )
                val bottom = viewport.toWorld(Vec2(0.0, hudBottom(screen.topInset).value.toDouble())).y
                assertTrue(
                    bottom <= LevelDefaults.HUD_BAND,
                    "${level.id} ${screen.name}: HUD bottom at $bottom is below the HUD band",
                )
            }
        }
    }
}
