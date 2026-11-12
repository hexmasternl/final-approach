package nl.hexmaster.finalapproach.ui.game

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.rememberTextMeasurer
import androidx.compose.ui.tooling.preview.Preview
import nl.hexmaster.finalapproach.domain.LevelConfig
import nl.hexmaster.finalapproach.domain.LevelTheme
import nl.hexmaster.finalapproach.domain.Levels
import nl.hexmaster.finalapproach.input.WorldViewport
import nl.hexmaster.finalapproach.ui.components.FaScoreCounter
import nl.hexmaster.finalapproach.ui.game.ambient.AmbientLife
import nl.hexmaster.finalapproach.ui.sprites.FaIcon
import nl.hexmaster.finalapproach.ui.theme.FaDimens
import nl.hexmaster.finalapproach.ui.theme.FaTheme

/** Sample data for the previews: a run part way through a level. Not used by the game. */
private object PreviewData {
    const val BEST = 240
    const val SCORE = 130
    const val BEST_LARGE = 18_720
    const val SCORE_LARGE = 12_450
    const val AMBIENT_SECONDS = 40.0
    const val AMBIENT_STEP = 0.05
    const val PHONE_WIDTH = 800
    const val PHONE_HEIGHT = 380
}

/** A level's field as the game draws it, with ambient life that has run for a while. */
@Composable
private fun FieldPreview(level: LevelConfig) {
    FaTheme(dawn = level.theme == LevelTheme.Dawn) {
        val colors = FaTheme.colors
        val measurer = rememberTextMeasurer()
        val numberStyle = FaTheme.type.titleMd
        val letterStyle = FaTheme.type.titleLg
        val ambient = remember(level) {
            AmbientLife(level, seed = level.id.hashCode().toLong()).apply {
                repeat((PreviewData.AMBIENT_SECONDS / PreviewData.AMBIENT_STEP).toInt()) { update(PreviewData.AMBIENT_STEP) }
            }
        }
        Canvas(Modifier.fillMaxSize()) {
            val viewport = WorldViewport(level.world.width, level.world.height, size.width.toDouble(), size.height.toDouble())
            drawField(level, ambient, viewport, colors, measurer, numberStyle, letterStyle)
        }
    }
}

@Preview(widthDp = PreviewData.PHONE_WIDTH, heightDp = PreviewData.PHONE_HEIGHT)
@Composable
private fun MeadowFieldPreview() = FieldPreview(Levels.meadowField)

@Preview(widthDp = PreviewData.PHONE_WIDTH, heightDp = PreviewData.PHONE_HEIGHT)
@Composable
private fun IslandAirportPreview() = FieldPreview(Levels.islandAirport)

@Preview(widthDp = PreviewData.PHONE_WIDTH, heightDp = PreviewData.PHONE_HEIGHT)
@Composable
private fun SavannaDawnPreview() = FieldPreview(Levels.savannaDawn)

/** The HUD over the field it sits on, with the counts of a run part way through. */
@Composable
private fun HudPreview(level: LevelConfig) {
    Box(Modifier.fillMaxSize()) {
        FieldPreview(level)
        FaTheme(dawn = level.theme == LevelTheme.Dawn) {
            Hud(
                best = PreviewData.BEST,
                score = PreviewData.SCORE,
                onOptions = {},
            )
        }
    }
}

@Preview(widthDp = PreviewData.PHONE_WIDTH, heightDp = PreviewData.PHONE_HEIGHT)
@Composable
private fun MeadowHudPreview() = HudPreview(Levels.meadowField)

@Preview(widthDp = PreviewData.PHONE_WIDTH, heightDp = PreviewData.PHONE_HEIGHT)
@Composable
private fun SavannaHudPreview() = HudPreview(Levels.savannaDawn)

/** The score chips; switch to interactive mode and change the score to see the pop. */
@Preview
@Composable
private fun ScoreChipPreview() {
    FaTheme {
        Row(Modifier.padding(FaDimens.space4), horizontalArrangement = Arrangement.spacedBy(FaDimens.space3)) {
            FaScoreCounter(HudText.BEST, PreviewData.BEST_LARGE, compact = true, icon = FaIcon.Star, iconColor = FaTheme.colors.sunDeep)
            FaScoreCounter(HudText.SCORE, PreviewData.SCORE_LARGE, popOnRise = true)
        }
    }
}

@Preview(widthDp = PreviewData.PHONE_WIDTH, heightDp = PreviewData.PHONE_HEIGHT)
@Composable
private fun OptionsPanelPreview() {
    Box(Modifier.fillMaxSize()) {
        FieldPreview(Levels.meadowField)
        FaTheme { OptionsPanel(onContinue = {}, onRestart = {}, onQuit = {}) }
    }
}

/** The three level tiles, to compare each miniature with its level. */
@Preview
@Composable
private fun LevelTilesPreview() {
    FaTheme {
        Row(Modifier.padding(FaDimens.space4), horizontalArrangement = Arrangement.spacedBy(FaDimens.space4)) {
            Levels.all.forEach { LevelTile(it, onClick = {}) }
        }
    }
}
