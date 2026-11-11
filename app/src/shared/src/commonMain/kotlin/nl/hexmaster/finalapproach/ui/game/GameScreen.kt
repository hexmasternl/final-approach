package nl.hexmaster.finalapproach.ui.game

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.gestures.awaitEachGesture
import androidx.compose.foundation.gestures.awaitFirstDown
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
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
import androidx.compose.ui.text.rememberTextMeasurer
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import nl.hexmaster.finalapproach.domain.Aircraft
import nl.hexmaster.finalapproach.domain.AircraftStatus
import nl.hexmaster.finalapproach.domain.LevelConfig
import nl.hexmaster.finalapproach.domain.Levels
import nl.hexmaster.finalapproach.engine.BestScore
import nl.hexmaster.finalapproach.engine.GameEngine
import nl.hexmaster.finalapproach.engine.GameState
import nl.hexmaster.finalapproach.input.WorldViewport
import nl.hexmaster.finalapproach.platform.AppHost
import nl.hexmaster.finalapproach.ui.BackHandler
import nl.hexmaster.finalapproach.ui.sprites.alphaLayer
import nl.hexmaster.finalapproach.ui.sprites.drawAircraftBody
import nl.hexmaster.finalapproach.ui.sprites.drawAircraftShadow
import nl.hexmaster.finalapproach.ui.sprites.drawLawn
import nl.hexmaster.finalapproach.ui.theme.FaColors
import nl.hexmaster.finalapproach.ui.theme.FaDimens
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
fun GameScreen(host: AppHost, onQuit: () -> Unit, level: LevelConfig = Levels.meadowField) {
    val engine = remember(level) { GameEngine(level) }
    val state by engine.state.collectAsState()
    val bestScore = remember(level, host) { BestScore(host.highScores, level.id) }
    var optionsOpen by remember { mutableStateOf(false) }
    var screen by remember { mutableStateOf(IntSize.Zero) }
    val rings = remember(engine) { ProximityRings() }
    var clock by remember(engine) { mutableStateOf(0.0) }
    val colors = FaTheme.colors
    val reduceMotion = FaTheme.reduceMotion
    val measurer = rememberTextMeasurer()
    val numberStyle = FaTheme.type.titleMd
    val letterStyle = FaTheme.type.titleLg

    DisposableEffect(host) {
        host.lockLandscape(true)
        onDispose { host.lockLandscape(false) }
    }
    fun openOptions() {
        engine.pause()
        optionsOpen = true
    }

    fun closeOptions() {
        optionsOpen = false
        engine.resume()
    }
    BackHandler(host) { if (optionsOpen) closeOptions() else openOptions() }

    LaunchedEffect(state.score) { bestScore.update(state.score) }

    LaunchedEffect(engine) {
        var last = withFrameNanos { it }
        while (true) {
            val now = withFrameNanos { it }
            val delta = ((now - last) / GameScreenDefaults.NANOS_PER_SECOND).coerceAtMost(GameScreenDefaults.MAX_FRAME_SECONDS)
            last = now
            engine.update(delta)
            if (!engine.state.value.paused) rings.update(engine.state.value, { engine.specs[it.type].shape.boundingRadius }, (delta * MILLIS_PER_SECOND).toFloat())
            if (!engine.state.value.paused) clock += delta
        }
    }

    val viewport = WorldViewport(
        level.world.width,
        level.world.height,
        screen.width.toDouble().coerceAtLeast(1.0),
        screen.height.toDouble().coerceAtLeast(1.0),
    )
    val currentViewport by rememberUpdatedState(viewport)

    Box(Modifier.fillMaxSize()) {
    Canvas(
        Modifier
            .fillMaxSize()
            .onSizeChanged { screen = it }
            .pointerInput(engine) {
                awaitEachGesture {
                    val down = awaitFirstDown()
                    val view = currentViewport
                    val pickRadius = view.toWorldLength(FaDimens.touchAircraft.toPx().toDouble())
                    val id = engine.select(view.worldOf(down.position), pickRadius)
                        ?: return@awaitEachGesture
                    down.consume()
                    var drawing = false
                    while (true) {
                        val event = awaitPointerEvent()
                        val change = event.changes.firstOrNull { it.id == down.id } ?: break
                        if (!change.pressed) break
                        if (!drawing && (change.position - down.position).getDistance() > viewConfiguration.touchSlop) {
                            drawing = true
                            engine.beginPath(id)
                        }
                        if (drawing) engine.extendPath(id, currentViewport.worldOf(change.position))
                        change.consume()
                    }
                    if (drawing) engine.endPath(id)
                }
            },
    ) {
        drawLawn(GameScreenDefaults.lawnStripe.toPx(), colors)
        val (ground, buildings) = level.scenery.partition { it.isGround }
        ground.forEach { drawScenery(it, viewport, colors) }
        level.runways.forEach { drawRunway(it, viewport, colors, measurer, numberStyle) }
        level.helipads.forEach { drawHelipad(it, viewport, colors, measurer, letterStyle) }
        buildings.forEach { drawScenery(it, viewport, colors) }
        drawPaths(state, viewport, colors, reduceMotion)
        drawAircraft(state, engine, viewport, colors, reduceMotion)
        drawSelection(state, engine, viewport, colors)
        drawProximityRings(rings, proximityRingAngle(clock, reduceMotion), reduceMotion, viewport, colors)
    }
    Hud(
        best = bestScore.shown(state.score),
        score = state.score,
        types = level.spawnedTypes,
        spawned = state.spawnedByType,
        onOptions = ::openOptions,
    )
    if (optionsOpen) {
        OptionsPanel(
            onContinue = ::closeOptions,
            onRestart = {
                engine.restart()
                optionsOpen = false
            },
            onQuit = onQuit,
        )
    }
    }
}

/** The effects layer: a warning ring around every aircraft that is too close to another. */
private fun DrawScope.drawProximityRings(
    rings: ProximityRings,
    angle: Float,
    reduceMotion: Boolean,
    viewport: WorldViewport,
    colors: FaColors,
) {
    val margin = FaDimens.warnRingMargin.toPx()
    for (ring in rings.all) {
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

/** Ground shadows of everything airborne first, then every aircraft on top. */
private fun DrawScope.drawAircraft(
    state: GameState,
    engine: GameEngine,
    viewport: WorldViewport,
    colors: FaColors,
    reduceMotion: Boolean,
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
    for (aircraft in state.aircraft) {
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
                spriteSizePx(aircraft, engine, viewport),
                degreesOf(aircraft.heading),
                time,
                colors,
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
