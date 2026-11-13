package nl.hexmaster.finalapproach.ui.game

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
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
import nl.hexmaster.finalapproach.engine.GameOverReason
import nl.hexmaster.finalapproach.input.WorldViewport
import nl.hexmaster.finalapproach.ui.PreviewAppHost
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
    const val SMALL_PHONE_WIDTH = 640
    const val SMALL_PHONE_HEIGHT = 360
    const val PORTRAIT_WIDTH = 360
    const val PORTRAIT_HEIGHT = 760
    const val BANNER_HEIGHT = 50
    const val TOTAL_LANDED = 37
    val landed = mapOf(
        AircraftType.LightProp to 12,
        AircraftType.Turboprop to 9,
        AircraftType.Jet to 8,
        AircraftType.Jumbo to 3,
        AircraftType.Helicopter to 5,
    )
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

/** The HUD while the tutorial runs, in Night: the strips, Skip tutorial and the cog wheel. */
@Preview(widthDp = PreviewData.PHONE_WIDTH, heightDp = PreviewData.PHONE_HEIGHT)
@Composable
private fun MeadowHudNightPreview() {
    Box(Modifier.fillMaxSize()) {
        FieldPreview(Levels.meadowField, LevelTheme.Night)
        FaTheme(night = true) {
            Hud(best = PreviewData.BEST_LARGE, score = PreviewData.SCORE_LARGE, onOptions = {}, onSkipTutorial = {})
        }
    }
}

/** The score strips; switch to interactive mode and change the score to see the pop. */
@Preview
@Composable
private fun ScoreStripPreview() {
    FaTheme {
        Row(Modifier.padding(FaDimens.space4), horizontalArrangement = Arrangement.spacedBy(FaDimens.space3)) {
            FaScoreCounter(HudText.BEST, PreviewData.BEST_LARGE, icon = FaIcon.Star)
            FaScoreCounter(HudText.SCORE, PreviewData.SCORE_LARGE, accent = true, popOnRise = true)
        }
    }
}

/** The pause dialog over the field. */
@Composable
private fun OptionsPanelPreview(night: Boolean) {
    Box(Modifier.fillMaxSize()) {
        FieldPreview(Levels.meadowField, if (night) LevelTheme.Night else LevelTheme.Day)
        FaTheme(night = night, reduceMotion = true) { OptionsPanel(onContinue = {}, onRestart = {}, onQuit = {}) }
    }
}

@Preview(widthDp = PreviewData.PHONE_WIDTH, heightDp = PreviewData.PHONE_HEIGHT)
@Composable
private fun OptionsPanelDayPreview() = OptionsPanelPreview(night = false)

@Preview(widthDp = PreviewData.PHONE_WIDTH, heightDp = PreviewData.PHONE_HEIGHT)
@Composable
private fun OptionsPanelNightPreview() = OptionsPanelPreview(night = true)

@Preview(widthDp = PreviewData.PORTRAIT_WIDTH, heightDp = PreviewData.PORTRAIT_HEIGHT)
@Composable
private fun OptionsPanelPortraitPreview() = OptionsPanelPreview(night = false)

/**
 * The game-ended dialog over the field with every aircraft type landed, above a placeholder as
 * high as the ad banner. On the small phone it must show whole.
 */
@Composable
private fun GameEndedPreview(night: Boolean, reason: GameOverReason = GameOverReason.Collision) {
    Box(Modifier.fillMaxSize()) {
        FieldPreview(Levels.meadowField, if (night) LevelTheme.Night else LevelTheme.Day)
        FaTheme(night = night, reduceMotion = true) {
            GameEndedDialog(
                reason = reason,
                types = AircraftType.entries,
                landed = PreviewData.landed,
                total = PreviewData.TOTAL_LANDED,
                points = PreviewData.SCORE_LARGE,
                onTryAgain = {},
                onMainPage = {},
                below = { Spacer(Modifier.fillMaxWidth().height(PreviewData.BANNER_HEIGHT.dp)) },
            )
        }
    }
}

@Preview(widthDp = PreviewData.SMALL_PHONE_WIDTH, heightDp = PreviewData.SMALL_PHONE_HEIGHT)
@Composable
private fun GameEndedSmallPhoneDayPreview() = GameEndedPreview(night = false)

@Preview(widthDp = PreviewData.SMALL_PHONE_WIDTH, heightDp = PreviewData.SMALL_PHONE_HEIGHT)
@Composable
private fun GameEndedSmallPhoneNightPreview() = GameEndedPreview(night = true)

@Preview(widthDp = PreviewData.PHONE_WIDTH, heightDp = PreviewData.PHONE_HEIGHT)
@Composable
private fun GameEndedLeftAreaPreview() = GameEndedPreview(night = false, reason = GameOverReason.LeftArea)

@Preview(widthDp = PreviewData.PORTRAIT_WIDTH, heightDp = PreviewData.PORTRAIT_HEIGHT)
@Composable
private fun GameEndedPortraitPreview() = GameEndedPreview(night = false)

/** The level tiles, to compare each miniature with its level. */
@Composable
private fun LevelTilesPreview(night: Boolean) {
    FaTheme(night = night) {
        Row(Modifier.padding(FaDimens.space4), horizontalArrangement = Arrangement.spacedBy(FaDimens.space4)) {
            Levels.all.forEach { LevelTile(it, onClick = {}) }
        }
    }
}

@Preview
@Composable
private fun LevelTilesDayPreview() = LevelTilesPreview(night = false)

@Preview
@Composable
private fun LevelTilesNightPreview() = LevelTilesPreview(night = true)

/** The level select page as the player sees it. */
@Composable
private fun LevelSelectPreview(night: Boolean) {
    FaTheme(night = night, reduceMotion = true) { LevelSelectPage(PreviewAppHost, onStart = {}, onBack = {}) }
}

@Preview(widthDp = PreviewData.PHONE_WIDTH, heightDp = PreviewData.PHONE_HEIGHT)
@Composable
private fun LevelSelectDayPreview() = LevelSelectPreview(night = false)

@Preview(widthDp = PreviewData.PHONE_WIDTH, heightDp = PreviewData.PHONE_HEIGHT)
@Composable
private fun LevelSelectNightPreview() = LevelSelectPreview(night = true)

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
