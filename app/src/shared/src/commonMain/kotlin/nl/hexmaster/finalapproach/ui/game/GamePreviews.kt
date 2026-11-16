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
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.graphics.Paint
import androidx.compose.ui.graphics.drawscope.drawIntoCanvas
import androidx.compose.ui.graphics.drawscope.withTransform
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.rememberTextMeasurer
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import nl.hexmaster.finalapproach.domain.Aircraft
import nl.hexmaster.finalapproach.domain.AircraftType
import nl.hexmaster.finalapproach.domain.LevelConfig
import nl.hexmaster.finalapproach.domain.HelipadConfig
import nl.hexmaster.finalapproach.domain.LevelTheme
import nl.hexmaster.finalapproach.domain.RunwayConfig
import nl.hexmaster.finalapproach.domain.RunwayMode
import nl.hexmaster.finalapproach.domain.Levels
import nl.hexmaster.finalapproach.domain.SceneryDefaults
import nl.hexmaster.finalapproach.domain.SceneryItem
import nl.hexmaster.finalapproach.domain.SceneryKind
import nl.hexmaster.finalapproach.domain.Vec2
import nl.hexmaster.finalapproach.engine.GameEngine
import nl.hexmaster.finalapproach.engine.GameOverReason
import nl.hexmaster.finalapproach.input.WorldViewport
import nl.hexmaster.finalapproach.ui.PreviewAppHost
import nl.hexmaster.finalapproach.ui.components.FaScoreCounter
import nl.hexmaster.finalapproach.ui.game.ambient.AmbientLife
import nl.hexmaster.finalapproach.ui.sprites.FaIcon
import nl.hexmaster.finalapproach.ui.sprites.ScenerySprite
import nl.hexmaster.finalapproach.ui.sprites.bounds
import nl.hexmaster.finalapproach.ui.sprites.drawCloud
import nl.hexmaster.finalapproach.ui.sprites.drawSceneryShadow
import nl.hexmaster.finalapproach.ui.sprites.drawScenerySprite
import nl.hexmaster.finalapproach.ui.theme.FaDimens
import nl.hexmaster.finalapproach.ui.theme.FaMotion
import nl.hexmaster.finalapproach.ui.theme.FaOpacity
import nl.hexmaster.finalapproach.ui.theme.FaShadows
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
    const val SHEET_HEIGHT = 900
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
    const val EAST = 1.5707963267948966
    const val ZONE_X = 960.0
    const val ZONE_Y = 600.0
    const val LONG_RUNWAY = 760.0
    const val SHORT_RUNWAY = 300.0
    const val PAD_X = 960.0
    const val PAD_Y = 300.0
    const val TREE_X = 1300.0
    const val TREE_Y = 700.0
    const val TREE_SIZE = 86.0
    const val TREE_GAP = 40.0
    const val OTHER_X = 400.0
    const val CLOUD_WIDTH = 190f
    const val CLOUD_SMALL_WIDTH = 130f
    const val CLOUD_X = 0.3f
    const val CLOUD_Y = 0.3f
    const val CLOUD_SMALL_X = 0.72f
    const val CLOUD_SMALL_Y = 0.62f
    const val CLOUD_SHADOW_FACTOR = 1.6f
    const val OTHER_Y = 300.0
}

/**
 * A level's field as the game draws it, with ambient life that has run for a while. [theme] is
 * the palette it is drawn in: the level's own by default. No level is a Night level yet, so
 * drawing one in Night here is how its scenery is checked against the Night palette.
 */
@Composable
private fun FieldPreview(level: LevelConfig, theme: LevelTheme = level.theme, clouds: Boolean = false) {
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
            if (clouds) {
                // Clouds fly higher than aircraft, so their shadow falls further away.
                val shadow = Offset(FaShadows.floatOffsetX.toPx(), FaShadows.floatOffsetY.toPx()) * PreviewData.CLOUD_SHADOW_FACTOR
                drawCloud(Offset(size.width * PreviewData.CLOUD_X, size.height * PreviewData.CLOUD_Y), PreviewData.CLOUD_WIDTH.dp.toPx(), colors, shadow)
                drawCloud(Offset(size.width * PreviewData.CLOUD_SMALL_X, size.height * PreviewData.CLOUD_SMALL_Y), PreviewData.CLOUD_SMALL_WIDTH.dp.toPx(), colors, shadow)
            }
        }
    }
}

/** The meadow with two clouds over it, in Day and in Night, next to the showcase's cloud. */
@Preview(widthDp = PreviewData.PHONE_WIDTH, heightDp = PreviewData.PHONE_HEIGHT)
@Composable
private fun CloudsOverMeadowPreview() = FieldPreview(Levels.meadowField, clouds = true)

@Preview(widthDp = PreviewData.PHONE_WIDTH, heightDp = PreviewData.PHONE_HEIGHT)
@Composable
private fun CloudsOverMeadowNightPreview() = FieldPreview(Levels.meadowField, LevelTheme.Night, clouds = true)

@Preview(widthDp = PreviewData.PHONE_WIDTH, heightDp = PreviewData.PHONE_HEIGHT)
@Composable
private fun MeadowFieldPreview() = FieldPreview(Levels.meadowField)

@Preview(widthDp = PreviewData.PHONE_WIDTH, heightDp = PreviewData.PHONE_HEIGHT)
@Composable
private fun IslandAirportPreview() = FieldPreview(Levels.islandAirport)

/** A bare field with just the given landing zones, to check their construction against Runway.md and Helipad.md. */
@Composable
private fun ZonesPreview(runways: List<RunwayConfig>, helipads: List<HelipadConfig> = emptyList(), theme: LevelTheme = LevelTheme.Day) =
    FieldPreview(Levels.meadowField.copy(runways = runways, helipads = helipads, scenery = emptyList(), paddock = null), theme)

/** Every flat ground piece once, to check it against Ground.md: road, track, taxiway, apron, car park, the three crop fields and a pond. */
private val groundSample: List<SceneryItem> = listOf(
    SceneryItem(SceneryKind.Road, Vec2(300.0, 540.0), 40.0, 1100.0),
    SceneryItem(SceneryKind.Road, Vec2(850.0, 140.0), 40.0, 1100.0, rotation = PreviewData.EAST),
    SceneryItem(SceneryKind.Track, Vec2(560.0, 380.0), 32.0, 500.0, rotation = PreviewData.EAST),
    SceneryItem(SceneryKind.FieldWheat, Vec2(1300.0, 190.0), 300.0, 140.0),
    SceneryItem(SceneryKind.FieldGreen, Vec2(1640.0, 190.0), 300.0, 140.0),
    SceneryItem(SceneryKind.FieldPlough, Vec2(1470.0, 400.0), 640.0, 160.0),
    SceneryItem(SceneryKind.Pond, Vec2(1560.0, 780.0), 240.0, 140.0),
    SceneryItem(SceneryKind.Apron, Vec2(760.0, 480.0), 320.0, 120.0),
    SceneryItem(SceneryKind.ParkedAircraft, Vec2(690.0, 480.0), SceneryDefaults.PARKED_AIRCRAFT_SIZE, SceneryDefaults.PARKED_AIRCRAFT_SIZE),
    SceneryItem(SceneryKind.Taxiway, Vec2(760.0, 560.0), 44.0, 76.0),
    SceneryItem(SceneryKind.CarPark, Vec2(520.0, 700.0), 110.0, 60.0),
)

@Composable
private fun GroundPiecesPreview(theme: LevelTheme) = FieldPreview(
    Levels.meadowField.copy(
        runways = listOf(RunwayConfig("r", Vec2(PreviewData.ZONE_X, 620.0), PreviewData.EAST, length = PreviewData.LONG_RUNWAY)),
        helipads = emptyList(),
        scenery = groundSample,
        paddock = null,
    ),
    theme,
)

@Preview(widthDp = PreviewData.PHONE_WIDTH, heightDp = PreviewData.PHONE_HEIGHT)
@Composable
private fun GroundPiecesDayPreview() = GroundPiecesPreview(LevelTheme.Day)

@Preview(widthDp = PreviewData.PHONE_WIDTH, heightDp = PreviewData.PHONE_HEIGHT)
@Composable
private fun GroundPiecesNightPreview() = GroundPiecesPreview(LevelTheme.Night)

@Preview(widthDp = PreviewData.PHONE_WIDTH, heightDp = PreviewData.PHONE_HEIGHT)
@Composable
private fun TwoWayRunwayPreview() = ZonesPreview(listOf(RunwayConfig("r", Vec2(PreviewData.ZONE_X, PreviewData.ZONE_Y), PreviewData.EAST, length = PreviewData.LONG_RUNWAY)))

@Preview(widthDp = PreviewData.PHONE_WIDTH, heightDp = PreviewData.PHONE_HEIGHT)
@Composable
private fun OneWayRunwayPreview() = ZonesPreview(
    listOf(RunwayConfig("r", Vec2(PreviewData.ZONE_X, PreviewData.ZONE_Y), PreviewData.EAST, length = PreviewData.LONG_RUNWAY, mode = RunwayMode.OneWay(PreviewData.EAST))),
)

@Preview(widthDp = PreviewData.PHONE_WIDTH, heightDp = PreviewData.PHONE_HEIGHT)
@Composable
private fun ShortRunwayPreview() = ZonesPreview(listOf(RunwayConfig("r", Vec2(PreviewData.ZONE_X, PreviewData.ZONE_Y), PreviewData.EAST, length = PreviewData.SHORT_RUNWAY)))

@Preview(widthDp = PreviewData.PHONE_WIDTH, heightDp = PreviewData.PHONE_HEIGHT)
@Composable
private fun RunwayAndHelipadDawnPreview() = ZonesPreview(
    runways = listOf(RunwayConfig("r", Vec2(PreviewData.ZONE_X, PreviewData.ZONE_Y), PreviewData.EAST, length = PreviewData.LONG_RUNWAY)),
    helipads = listOf(HelipadConfig("h", Vec2(PreviewData.PAD_X, PreviewData.PAD_Y))),
    theme = LevelTheme.Dawn,
)

@Preview(widthDp = PreviewData.PHONE_WIDTH, heightDp = PreviewData.PHONE_HEIGHT)
@Composable
private fun HelipadPreview() = ZonesPreview(
    runways = listOf(RunwayConfig("r", Vec2(PreviewData.ZONE_X, PreviewData.ZONE_Y), PreviewData.EAST, length = PreviewData.LONG_RUNWAY)),
    helipads = listOf(HelipadConfig("h", Vec2(PreviewData.PAD_X, PreviewData.PAD_Y))),
)

/** Two trees that overlap: where their shadows meet, the ground must not be darker than under one. */
@Preview(widthDp = PreviewData.PHONE_WIDTH, heightDp = PreviewData.PHONE_HEIGHT)
@Composable
private fun OverlappingTreeShadowsPreview() = FieldPreview(
    Levels.meadowField.copy(
        scenery = listOf(
            SceneryItem(SceneryKind.Tree, Vec2(PreviewData.TREE_X, PreviewData.TREE_Y), PreviewData.TREE_SIZE, PreviewData.TREE_SIZE),
            SceneryItem(SceneryKind.Tree, Vec2(PreviewData.TREE_X + PreviewData.TREE_GAP, PreviewData.TREE_Y), PreviewData.TREE_SIZE, PreviewData.TREE_SIZE),
        ),
    ),
)

@Preview(widthDp = PreviewData.PHONE_WIDTH, heightDp = PreviewData.PHONE_HEIGHT)
@Composable
private fun SavannaDawnPreview() = FieldPreview(Levels.savannaDawn)

// Every level in its own theme and in the other two, for the design review of its scenery. Debug only: the game
// never offers these palettes as a setting.

@Preview(widthDp = PreviewData.PHONE_WIDTH, heightDp = PreviewData.PHONE_HEIGHT)
@Composable
private fun MeadowFieldNightPreview() = FieldPreview(Levels.meadowField, LevelTheme.Night)

@Preview(widthDp = PreviewData.PHONE_WIDTH, heightDp = PreviewData.PHONE_HEIGHT)
@Composable
private fun IslandAirportNightPreview() = FieldPreview(Levels.islandAirport, LevelTheme.Night)

@Preview(widthDp = PreviewData.PHONE_WIDTH, heightDp = PreviewData.PHONE_HEIGHT)
@Composable
private fun MeadowFieldDawnPreview() = FieldPreview(Levels.meadowField, LevelTheme.Dawn)

@Preview(widthDp = PreviewData.PHONE_WIDTH, heightDp = PreviewData.PHONE_HEIGHT)
@Composable
private fun IslandAirportDawnPreview() = FieldPreview(Levels.islandAirport, LevelTheme.Dawn)

@Preview(widthDp = PreviewData.PHONE_WIDTH, heightDp = PreviewData.PHONE_HEIGHT)
@Composable
private fun SavannaDayPreview() = FieldPreview(Levels.savannaDawn, LevelTheme.Day)

@Preview(widthDp = PreviewData.PHONE_WIDTH, heightDp = PreviewData.PHONE_HEIGHT)
@Composable
private fun SavannaNightPreview() = FieldPreview(Levels.savannaDawn, LevelTheme.Night)

/**
 * Every scenery sprite of the design system on the lawn, each at its natural size with its hard
 * cast shadow, to hold beside the tiles of the showcase. Windows are glass by day and lit at
 * night and at dawn.
 */
@Composable
private fun ScenerySheet(theme: LevelTheme) {
    FaTheme(night = theme == LevelTheme.Night, dawn = theme == LevelTheme.Dawn) {
        val colors = FaTheme.colors
        Canvas(Modifier.fillMaxSize()) {
            drawRect(colors.grass500)
            val gap = FaDimens.space4.toPx()
            var x = gap
            var y = gap
            var rowHeight = 0f
            val tiles = ScenerySprite.entries.map { it to it.bounds }
            // Flat things first would hide under standing ones; the sheet keeps the design system's order.
            for ((sprite, bounds) in tiles) {
                val width = maxOf(bounds.width, sprite.width).dp.toPx()
                val height = maxOf(bounds.height, sprite.height).dp.toPx()
                if (x + width + gap > size.width) {
                    x = gap
                    y += rowHeight + gap
                    rowHeight = 0f
                }
                val center = Offset(x + width / 2f, y + height / 2f)
                val scale = 1.dp.toPx()
                withTransform({
                    translate(center.x, center.y)
                    scale(scale, scale, Offset.Zero)
                    translate(-bounds.center.x, -bounds.center.y)
                }) {
                    drawIntoCanvas { canvas ->
                        canvas.saveLayer(Rect(-SHEET_EXTENT, -SHEET_EXTENT, SHEET_EXTENT, SHEET_EXTENT), Paint().apply { alpha = FaOpacity.shadowCast })
                        drawSceneryShadow(sprite, colors.shadowCast)
                        canvas.restore()
                    }
                }
                drawScenerySprite(sprite, center, bounds.width * scale, bounds.height * scale, 0f, colors)
                x += width + gap
                rowHeight = maxOf(rowHeight, height)
            }
        }
    }
}

private const val SHEET_EXTENT = 400f

@Preview(widthDp = PreviewData.PHONE_WIDTH, heightDp = PreviewData.SHEET_HEIGHT)
@Composable
private fun ScenerySheetDayPreview() = ScenerySheet(LevelTheme.Day)

@Preview(widthDp = PreviewData.PHONE_WIDTH, heightDp = PreviewData.SHEET_HEIGHT)
@Composable
private fun ScenerySheetNightPreview() = ScenerySheet(LevelTheme.Night)

@Preview(widthDp = PreviewData.PHONE_WIDTH, heightDp = PreviewData.SHEET_HEIGHT)
@Composable
private fun ScenerySheetDawnPreview() = ScenerySheet(LevelTheme.Dawn)

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
