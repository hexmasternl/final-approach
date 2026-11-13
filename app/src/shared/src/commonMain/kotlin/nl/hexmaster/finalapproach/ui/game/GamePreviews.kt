package nl.hexmaster.finalapproach.ui.game

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.rememberTextMeasurer
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import nl.hexmaster.finalapproach.domain.Aircraft
import nl.hexmaster.finalapproach.domain.AircraftType
import nl.hexmaster.finalapproach.domain.LevelConfig
import nl.hexmaster.finalapproach.domain.LevelTheme
import nl.hexmaster.finalapproach.domain.Levels
import nl.hexmaster.finalapproach.domain.Vec2
import nl.hexmaster.finalapproach.engine.GameEngine
import nl.hexmaster.finalapproach.input.WorldViewport
import nl.hexmaster.finalapproach.ui.components.FaScoreCounter
import nl.hexmaster.finalapproach.ui.game.ambient.AmbientLife
import nl.hexmaster.finalapproach.ui.sprites.FaIcon
import nl.hexmaster.finalapproach.ui.sprites.ScenerySprite
import nl.hexmaster.finalapproach.ui.sprites.drawScenerySprite
import nl.hexmaster.finalapproach.ui.sprites.sceneryLines
import nl.hexmaster.finalapproach.ui.theme.FaDimens
import nl.hexmaster.finalapproach.ui.theme.FaMotion
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
    const val SHEET_HEIGHT = 520
    const val COLLISION_X = 900.0
    const val COLLISION_Y = 420.0
    const val COLLISION_HEADING = 1.2
    const val OTHER_X = 400.0
    const val OTHER_Y = 300.0
}

/**
 * A level's field as the game draws it, with ambient life that has run for a while. [theme] is
 * the palette it is drawn in: the level's own by default. No level is a Night level yet, so
 * drawing one in Night here is how its scenery is checked against the Night palette.
 */
@Composable
private fun FieldPreview(level: LevelConfig, theme: LevelTheme = level.theme) {
    FaTheme(night = theme == LevelTheme.Night, dawn = theme == LevelTheme.Dawn) {
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

// Every level in Day and in Night, for the design review of its scenery. Debug only: the game
// never offers these palettes as a setting.

@Preview(widthDp = PreviewData.PHONE_WIDTH, heightDp = PreviewData.PHONE_HEIGHT)
@Composable
private fun MeadowFieldNightPreview() = FieldPreview(Levels.meadowField, LevelTheme.Night)

@Preview(widthDp = PreviewData.PHONE_WIDTH, heightDp = PreviewData.PHONE_HEIGHT)
@Composable
private fun IslandAirportNightPreview() = FieldPreview(Levels.islandAirport, LevelTheme.Night)

@Preview(widthDp = PreviewData.PHONE_WIDTH, heightDp = PreviewData.PHONE_HEIGHT)
@Composable
private fun SavannaDayPreview() = FieldPreview(Levels.savannaDawn, LevelTheme.Day)

@Preview(widthDp = PreviewData.PHONE_WIDTH, heightDp = PreviewData.PHONE_HEIGHT)
@Composable
private fun SavannaNightPreview() = FieldPreview(Levels.savannaDawn, LevelTheme.Night)

/** Every scenery sprite at its grid size on the lawn, to hold beside the SVGs of the design system. */
@Composable
private fun ScenerySheet(theme: LevelTheme) {
    FaTheme(night = theme == LevelTheme.Night, dawn = theme == LevelTheme.Dawn) {
        val colors = FaTheme.colors
        Canvas(Modifier.fillMaxSize()) {
            drawRect(colors.grass500)
            val lines = sceneryLines(density)
            val gap = FaDimens.space4.toPx()
            var x = gap
            var y = gap
            var rowHeight = 0f
            for (sprite in ScenerySprite.entries) {
                val width = sprite.width.dp.toPx()
                val height = sprite.height.dp.toPx()
                if (x + width + gap > size.width) {
                    x = gap
                    y += rowHeight + gap
                    rowHeight = 0f
                }
                drawScenerySprite(sprite, Offset(x + width / 2f, y + height / 2f), width, height, 0f, colors, lines)
                x += width + gap
                rowHeight = maxOf(rowHeight, height)
            }
        }
    }
}

@Preview(widthDp = PreviewData.PHONE_WIDTH, heightDp = PreviewData.SHEET_HEIGHT)
@Composable
private fun ScenerySheetDayPreview() = ScenerySheet(LevelTheme.Day)

@Preview(widthDp = PreviewData.PHONE_WIDTH, heightDp = PreviewData.SHEET_HEIGHT)
@Composable
private fun ScenerySheetNightPreview() = ScenerySheet(LevelTheme.Night)

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

/** The collision view: two aircraft on the field when they touch, [elapsedMs] after the collision. */
@Composable
private fun CollisionPreview(elapsedMs: Float) {
    val level = Levels.meadowField
    val engine = remember {
        GameEngine(level, seed = 1).also {
            it.place(
                listOf(
                    Aircraft(1, AircraftType.Jet, Vec2(PreviewData.COLLISION_X, PreviewData.COLLISION_Y), 0.0, livery = 1),
                    Aircraft(2, AircraftType.Turboprop, Vec2(PreviewData.COLLISION_X, PreviewData.COLLISION_Y), PreviewData.COLLISION_HEADING, livery = 2),
                    Aircraft(3, AircraftType.LightProp, Vec2(PreviewData.OTHER_X, PreviewData.OTHER_Y), 0.0, livery = 3),
                ),
            )
        }
    }
    val sequence = remember(elapsedMs) { CollisionSequence(listOf(1, 2), 0f, reduceMotion = false).apply { advance(elapsedMs) } }
    Box(Modifier.fillMaxSize()) {
        FieldPreview(level)
        FaTheme {
            val state = engine.state.value
            BoxWithConstraints(Modifier.fillMaxSize()) {
                val density = LocalDensity.current
                val viewport = WorldViewport(
                    level.world.width, level.world.height,
                    with(density) { maxWidth.toPx() }.toDouble(), with(density) { maxHeight.toPx() }.toDouble(),
                )
                CollisionOverlay(sequence, state, engine, viewport, reduceMotion = false, lightOpacity = null)
            }
        }
    }
}

@Preview(widthDp = PreviewData.PHONE_WIDTH, heightDp = PreviewData.PHONE_HEIGHT)
@Composable
private fun CollisionMidFadePreview() = CollisionPreview(elapsedMs = FaMotion.durCollisionFadeMs / 2f)

@Preview(widthDp = PreviewData.PHONE_WIDTH, heightDp = PreviewData.PHONE_HEIGHT)
@Composable
private fun CollisionHeldPreview() = CollisionPreview(elapsedMs = FaMotion.durCollisionFadeMs + FaMotion.durCollisionHoldMs / 2f)
