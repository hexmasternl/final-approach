package nl.hexmaster.finalapproach.ui.game

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.gestures.awaitEachGesture
import androidx.compose.foundation.gestures.awaitFirstDown
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.systemGestures
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.setValue
import androidx.compose.runtime.withFrameNanos
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Density
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.text.rememberTextMeasurer
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import nl.hexmaster.finalapproach.domain.Aircraft
import nl.hexmaster.finalapproach.domain.AircraftStatus
import nl.hexmaster.finalapproach.domain.LevelConfig
import nl.hexmaster.finalapproach.domain.LevelTheme
import nl.hexmaster.finalapproach.domain.isPickable
import nl.hexmaster.finalapproach.domain.Levels
import nl.hexmaster.finalapproach.engine.BestScore
import nl.hexmaster.finalapproach.engine.GameEngine
import nl.hexmaster.finalapproach.engine.TutorialProgress
import nl.hexmaster.finalapproach.engine.GameOverReason
import nl.hexmaster.finalapproach.engine.GameState
import nl.hexmaster.finalapproach.engine.GameStatus
import nl.hexmaster.finalapproach.input.PathGesture
import nl.hexmaster.finalapproach.input.WorldViewport
import nl.hexmaster.finalapproach.platform.AppHost
import nl.hexmaster.finalapproach.platform.ScreenRect
import nl.hexmaster.finalapproach.platform.SoundPlayer
import nl.hexmaster.finalapproach.ui.BackHandler
import nl.hexmaster.finalapproach.ui.game.ambient.AmbientLife
import nl.hexmaster.finalapproach.ui.sprites.alphaLayer
import nl.hexmaster.finalapproach.ui.sprites.drawAircraftBody
import nl.hexmaster.finalapproach.ui.sprites.drawAircraftShadow
import nl.hexmaster.finalapproach.ui.theme.FaColors
import nl.hexmaster.finalapproach.ui.theme.FaDimens
import nl.hexmaster.finalapproach.ui.theme.FaMotion
import nl.hexmaster.finalapproach.ui.theme.FaOpacity
import nl.hexmaster.finalapproach.ui.theme.FaShadows
import nl.hexmaster.finalapproach.ui.theme.FaTheme

/** Tunables of the game screen that are not design tokens. */
internal object GameScreenDefaults {
    /** Width of one lawn stripe. */
    val lawnStripe = 64.dp

    /** A long frame (the app was paused) is cut to this, so aircraft do not jump. */
    const val MAX_FRAME_SECONDS = 0.1

    const val NANOS_PER_SECOND = 1_000_000_000.0
}

private const val MILLIS_PER_SECOND = 1000.0

/**
 * The game: [level] fills the screen in landscape, aircraft arrive and the player steers them.
 * The HUD shows scores and spawn counts; the cog wheel (or back) pauses the level and opens the
 * options panel. Quit leaves through [onQuit].
 */
@Composable
fun GameScreen(
    host: AppHost,
    sounds: SoundPlayer,
    keepScreenOnSetting: Boolean,
    onQuit: () -> Unit,
    level: LevelConfig = Levels.meadowField,
) {
    FaTheme(dawn = level.theme == LevelTheme.Dawn, reduceMotion = FaTheme.reduceMotion) {
        GameContent(host, sounds, keepScreenOnSetting, onQuit, level)
    }
}

@Composable
private fun GameContent(host: AppHost, sounds: SoundPlayer, keepScreenOnSetting: Boolean, onQuit: () -> Unit, level: LevelConfig) {
    val tutorialProgress = remember(level, host) { TutorialProgress(host.tutorials, level.id) }
    val engine = remember(level, host) {
        GameEngine(
            level,
            tutorialActive = level.tutorial != null && !host.tutorials.isCompleted(level.id),
            onTutorialCompleted = tutorialProgress::markCompleted,
        )
    }
    val state by engine.state.collectAsState()
    val bestScore = remember(level, host) { BestScore(host.highScores, level.id) }
    var optionsOpen by remember { mutableStateOf(false) }
    var screen by remember { mutableStateOf(IntSize.Zero) }
    val rings = remember(engine) { ProximityRings() }
    var clock by remember(engine) { mutableStateOf(0.0) }
    var collision by remember(engine) { mutableStateOf<CollisionSequence?>(null) }
    val colors = FaTheme.colors
    val reduceMotion = FaTheme.reduceMotion
    val ambient = remember(engine, reduceMotion) { AmbientLife(level, seed = level.id.hashCode().toLong(), reduceMotion = reduceMotion) }
    val density = LocalDensity.current
    val layoutDirection = LocalLayoutDirection.current
    val gestureInsets = WindowInsets.systemGestures
    val leftStrip by rememberUpdatedState(gestureInsets.getLeft(density, layoutDirection))
    val rightStrip by rememberUpdatedState(gestureInsets.getRight(density, layoutDirection))
    val currentDensity by rememberUpdatedState(density)
    val measurer = rememberTextMeasurer()
    val numberStyle = FaTheme.type.titleMd
    val letterStyle = FaTheme.type.titleLg

    DisposableEffect(host) {
        host.lockLandscape(true)
        onDispose { host.lockLandscape(false) }
    }
    // The screen stays on only while the level runs; leaving the screen always restores the timeout.
    val keepScreenOn = shouldKeepScreenOn(keepScreenOnSetting, state)
    DisposableEffect(host, keepScreenOn) {
        host.keepScreenOn(keepScreenOn)
        onDispose { host.keepScreenOn(false) }
    }
    // The system bars hide only while the level runs; leaving the screen always shows them again.
    // The collision sequence still counts as the level running until the dialog shows.
    val running = isLevelRunning(state) || collision?.dialogVisible == false
    LaunchedEffect(host, running) { host.setImmersive(running) }
    DisposableEffect(host) {
        onDispose {
            host.setImmersive(false)
            host.setGestureExclusion(emptyList())
        }
    }
    fun openOptions() {
        engine.pause()
        optionsOpen = true
    }

    fun closeOptions() {
        optionsOpen = false
        engine.resume()
    }

    // The level waits for the ad that comes before it; with no ad ready the callback is immediate.
    LaunchedEffect(engine) {
        engine.pause()
        host.ads.showInterstitial {
            engine.resume()
            host.ads.preload()
        }
    }

    // A restart is a level start too: the new run begins when the ad is dismissed.
    fun restartWithAd(afterRestart: () -> Unit = {}) {
        host.ads.showInterstitial {
            engine.restart()
            afterRestart()
            host.ads.preload()
        }
    }
    BackHandler(host) {
        when (backAction(state, optionsOpen)) {
            BackAction.Quit -> onQuit()
            BackAction.CloseOptions -> closeOptions()
            BackAction.OpenOptions -> openOptions()
        }
    }

    LaunchedEffect(state.score) { bestScore.update(state.score) }

    val viewport = WorldViewport(
        level.world.width,
        level.world.height,
        screen.width.toDouble().coerceAtLeast(1.0),
        screen.height.toDouble().coerceAtLeast(1.0),
    )
    val currentViewport by rememberUpdatedState(viewport)

    LaunchedEffect(engine, host) {
        var last = withFrameNanos { it }
        var zones = emptyList<ScreenRect>()
        while (true) {
            val now = withFrameNanos { it }
            val delta = ((now - last) / GameScreenDefaults.NANOS_PER_SECOND).coerceAtMost(GameScreenDefaults.MAX_FRAME_SECONDS)
            last = now
            engine.update(delta)
            SoundCues.of(engine.state.value.events).forEach(sounds::play)
            tutorialProgress.onEvents(engine.state.value.events)
            if (!engine.state.value.paused) rings.update(engine.state.value, { engine.specs[it.type].shape.boundingRadius }, (delta * MILLIS_PER_SECOND).toFloat())
            if (!engine.state.value.paused) ambient.update(delta)
            if (!engine.state.value.paused) clock += delta
            // Touches that start on an aircraft near a side edge must not become a back gesture; the zones
            // are sent only when they change.
            val planned = gestureZones(engine.state.value, isLevelRunning(engine.state.value) || collision?.dialogVisible == false, currentViewport, screen, leftStrip, rightStrip, currentDensity)
            if (planned != zones) {
                zones = planned
                host.setGestureExclusion(planned)
            }
            // The collision sequence starts when the level ends by a collision, and goes when it restarts.
            val ended = engine.state.value.status as? GameStatus.GameOver
            val current = collision
            collision = when {
                ended == null || ended.reason != GameOverReason.Collision -> null
                current == null -> CollisionSequence(ended.aircraftIds, proximityRingAngle(clock, reduceMotion), reduceMotion)
                else -> current.also { it.advance((delta * MILLIS_PER_SECOND).toFloat()) }
            }
        }
    }

    Box(Modifier.fillMaxSize()) {
    Canvas(
        Modifier
            .fillMaxSize()
            .onSizeChanged { screen = it }
            .pointerInput(engine) {
                val gesture = PathGesture(engine)
                awaitEachGesture {
                    val down = awaitFirstDown()
                    val view = currentViewport
                    val pickRadius = view.toWorldLength(FaDimens.touchAircraft.toPx().toDouble())
                    if (!gesture.down(view.worldOf(down.position), pickRadius)) return@awaitEachGesture
                    down.consume()
                    try {
                        while (true) {
                            val event = awaitPointerEvent()
                            val change = event.changes.firstOrNull { it.id == down.id } ?: break
                            if (!change.pressed) break
                            val pastSlop = (change.position - down.position).getDistance() > viewConfiguration.touchSlop
                            gesture.move(currentViewport.worldOf(change.position), pastSlop)
                            change.consume()
                        }
                    } finally {
                        gesture.up()
                    }
                }
            },
    ) {
        drawField(level, ambient, viewport, colors, measurer, numberStyle, letterStyle)
        // Layers: zone glows (the tutorial highlight), paths (the drag demo's dots), aircraft, effects (its finger).
        state.tutorial?.highlight?.let { drawTutorialHighlight(it, level, viewport, colors, clock, reduceMotion) }
        drawPaths(state, viewport, colors, reduceMotion)
        state.tutorial?.demo?.let { drawTutorialDemoPath(it, viewport, colors) }
        drawAircraft(state, engine, viewport, colors, reduceMotion, ambient.lightOpacity.takeIf { level.theme == LevelTheme.Dawn })
        drawSelection(state, engine, viewport, colors)
        drawProximityRings(rings, proximityRingAngle(clock, reduceMotion), reduceMotion, viewport, colors, skip = collision?.aircraftIds.orEmpty())
        state.tutorial?.demo?.let { drawTutorialDemoFinger(it, viewport, colors, clock, reduceMotion) }
    }
    Hud(
        best = bestScore.shown(state.score),
        score = state.score,
        onOptions = ::openOptions,
        onSkipTutorial = if (state.tutorial != null) engine::skipTutorial else null,
    )
    state.tutorial?.let { tutorial ->
        TutorialOverlay(tutorial, level, state.aircraft, viewport, onGotIt = engine::acknowledgeTutorial)
    }
    collision?.let {
        CollisionOverlay(it, state, engine, viewport, reduceMotion, ambient.lightOpacity.takeIf { level.theme == LevelTheme.Dawn })
    }
    val over = (state.status as? GameStatus.GameOver)?.takeIf { it.reason != GameOverReason.Collision || collision?.dialogVisible == true }
    if (over != null) {
        GameEndedDialog(
            reason = over.reason,
            types = level.spawnedTypes,
            landed = state.landedByType,
            total = state.totalLanded,
            points = state.score,
            onTryAgain = { restartWithAd() },
            onMainPage = onQuit,
            below = { host.ads.Banner(Modifier.fillMaxWidth()) },
        )
    } else if (optionsOpen) {
        OptionsPanel(
            onContinue = ::closeOptions,
            onRestart = { restartWithAd { optionsOpen = false } },
            onQuit = onQuit,
        )
    }
    }
}

/** The side-edge zones for [state] while its level runs, none otherwise; see [planGestureExclusion]. */
private fun gestureZones(
    state: GameState,
    running: Boolean,
    viewport: WorldViewport,
    screen: IntSize,
    leftStrip: Int,
    rightStrip: Int,
    density: Density,
    config: GestureExclusionConfig = GestureExclusionConfig(),
): List<ScreenRect> {
    if (!running || screen == IntSize.Zero) return emptyList()
    val targets = state.aircraft.map {
        val at = viewport.offsetOf(it.position)
        GestureTarget(it.id, at.x, at.y, it.isPickable)
    }
    return with(density) {
        planGestureExclusion(
            targets, state.selectedId, screen.width, screen.height, leftStrip, rightStrip,
            FaDimens.touchAircraft.toPx(), config.maxHeightPerEdge.roundToPx(), config.zoneHalfHeight.roundToPx(),
        )
    }
}

/** The effects layer: a warning ring around every aircraft that is too close to another. */
private fun DrawScope.drawProximityRings(
    rings: ProximityRings,
    angle: Float,
    reduceMotion: Boolean,
    viewport: WorldViewport,
    colors: FaColors,
    skip: List<Int>,
) {
    val margin = FaDimens.warnRingMargin.toPx()
    for (ring in rings.all) {
        if (ring.id in skip) continue
        drawProximityRing(viewport.offsetOf(ring.position), viewport.px(ring.radius) + margin, angle, if (reduceMotion) 1f else ring.scale, ring.alpha, colors)
    }
}

private fun DrawScope.drawPaths(state: GameState, viewport: WorldViewport, colors: FaColors, reduceMotion: Boolean) {
    for (aircraft in state.aircraft) {
        if (aircraft.path.isEmpty) continue
        val points = (listOf(aircraft.position) + aircraft.path.points).map(viewport::offsetOf)
        val look = PathLook(
            selected = aircraft.id == state.selectedId,
            sinceLocked = aircraft.path.lockedAt?.let { state.levelTime - it },
            reduceMotion = reduceMotion,
            timeSeconds = state.levelTime,
        )
        drawFlightPath(points, colors.livery(aircraft.livery), look, colors)
    }
}

private fun spriteSizePx(aircraft: Aircraft, engine: GameEngine, viewport: WorldViewport): Float =
    viewport.px(engine.specs[aircraft.type].spriteSize)

/**
 * Size of the body sprite: a helicopter in its landing sequence shrinks onto its shadow, which
 * keeps full size. With reduced motion it is drawn in the final still pose all the time.
 */
private fun bodySizePx(aircraft: Aircraft, engine: GameEngine, viewport: WorldViewport, reduceMotion: Boolean): Float {
    val full = spriteSizePx(aircraft, engine, viewport)
    val status = aircraft.status as? AircraftStatus.Landing ?: return full
    val progress = if (reduceMotion) 1f else (status.elapsed / engine.level.landing.helicopterLandingSeconds).toFloat().coerceIn(0f, 1f)
    return full * (1f + (FaOpacity.descentScaleEnd - 1f) * FaMotion.easeOutSoft.transform(progress))
}

/** Ground shadows of everything airborne first, then every aircraft on top. */
private fun DrawScope.drawAircraft(
    state: GameState,
    engine: GameEngine,
    viewport: WorldViewport,
    colors: FaColors,
    reduceMotion: Boolean,
    lightOpacity: Float?,
) {
    val time = if (reduceMotion) 0f else state.levelTime.toFloat()
    val shadowOffset = Offset(FaShadows.floatOffsetX.toPx(), FaShadows.floatOffsetY.toPx())
    for (aircraft in state.aircraft) {
        if (aircraft.status is AircraftStatus.RollingOut) continue
        drawAircraftShadow(
            aircraft.type,
            viewport.offsetOf(aircraft.position),
            spriteSizePx(aircraft, engine, viewport),
            degreesOf(aircraft.heading),
            time,
            colors,
            shadowOffset,
        )
    }
    for (aircraft in state.aircraft) drawOneAircraft(aircraft, engine, viewport, colors, reduceMotion, lightOpacity, time)
}

/** One aircraft without its ground shadow: the body, and its lights on a Dawn level. */
internal fun DrawScope.drawOneAircraft(
    aircraft: Aircraft,
    engine: GameEngine,
    viewport: WorldViewport,
    colors: FaColors,
    reduceMotion: Boolean,
    lightOpacity: Float?,
    time: Float,
) {
    val status = aircraft.status
    val alpha = if (status is AircraftStatus.RollingOut) {
        (1.0 - status.fadeElapsed / engine.level.landing.fadeSeconds).coerceIn(0.0, 1.0).toFloat()
    } else {
        1f
    }
    alphaLayer(alpha) {
        drawAircraftBody(
            aircraft.type,
            colors.livery(aircraft.livery),
            viewport.offsetOf(aircraft.position),
            bodySizePx(aircraft, engine, viewport, reduceMotion),
            degreesOf(aircraft.heading),
            time,
            colors,
        )
        if (lightOpacity != null) {
            drawAircraftLights(
                viewport.offsetOf(aircraft.position),
                aircraft.heading,
                spriteSizePx(aircraft, engine, viewport),
                lightOpacity,
            )
        }
    }
}

private fun DrawScope.drawSelection(state: GameState, engine: GameEngine, viewport: WorldViewport, colors: FaColors) {
    val selected = state.aircraft.firstOrNull { it.id == state.selectedId } ?: return
    val radius = spriteSizePx(selected, engine, viewport) / 2f + FaDimens.space2.toPx()
    drawCircle(
        colors.selectRing,
        radius,
        viewport.offsetOf(selected.position),
        style = Stroke(width = FaDimens.line3.toPx()),
    )
}
