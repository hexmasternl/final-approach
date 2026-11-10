package nl.hexmaster.finalapproach.ui.menu

import kotlin.math.PI
import kotlin.math.atan2
import kotlin.math.cos
import kotlin.math.hypot
import kotlin.math.sin
import kotlin.random.Random
import nl.hexmaster.finalapproach.domain.AircraftType

/** A decorative aircraft crossing the main page. Positions are in dp from the top-left corner. */
data class MenuAircraft(
    val type: AircraftType,
    /** Flight-path colour index, 1 to [MenuBackdropConfig.liveryCount]. */
    val livery: Int,
    val x: Float,
    val y: Float,
    /** Unit direction of travel. */
    val directionX: Float,
    val directionY: Float,
    /** Speed in dp per second. */
    val speed: Float,
    /** Distance from the centre within which any part of the sprite or its shadow can be. */
    val radius: Float,
    /** True once any part of the aircraft has been inside the screen. */
    val hasEntered: Boolean = false,
) {
    /** Heading in degrees, clockwise from straight up, which is how sprites are drawn. */
    val headingDegrees: Float
        get() = (atan2(directionX, -directionY) * 180.0 / PI).toFloat()
}

/** The single slot for a passing aircraft: either one is flying, or the page waits for the next. */
sealed interface AircraftSlot {
    data class Waiting(val secondsLeft: Float) : AircraftSlot
    data class Flying(val aircraft: MenuAircraft) : AircraftSlot
}

/** Everything that moves on the main page backdrop. */
data class MenuBackdropState(
    val elapsedSeconds: Float,
    val rotationDegrees: Float,
    val slot: AircraftSlot,
) {
    companion object {
        /** The state the page opens in: nothing has moved and the first aircraft is one gap away. */
        fun initial(config: MenuBackdropConfig) = MenuBackdropState(
            elapsedSeconds = 0f,
            rotationDegrees = 0f,
            slot = AircraftSlot.Waiting(config.aircraftGapSeconds),
        )
    }
}

/** A point on the backdrop in dp. */
data class BackdropPoint(val x: Float, val y: Float)

/**
 * Advances the main page backdrop. It holds no state of its own apart from the random source,
 * so the same seed always produces the same sequence of aircraft.
 */
class MenuBackdropSimulation(
    private val config: MenuBackdropConfig,
    private val random: Random,
) {
    /** Returns [state] advanced by [deltaSeconds] on a screen of [width] by [height] dp. */
    fun step(state: MenuBackdropState, deltaSeconds: Float, width: Float, height: Float): MenuBackdropState {
        if (deltaSeconds <= 0f || width <= 0f || height <= 0f) return state
        val rotation = (state.rotationDegrees + 360f * deltaSeconds / config.rotationPeriodSeconds) % 360f
        return state.copy(
            elapsedSeconds = state.elapsedSeconds + deltaSeconds,
            rotationDegrees = rotation,
            slot = stepSlot(state.slot, deltaSeconds, width, height),
        )
    }

    private fun stepSlot(slot: AircraftSlot, deltaSeconds: Float, width: Float, height: Float): AircraftSlot =
        when (slot) {
            is AircraftSlot.Waiting -> {
                val left = slot.secondsLeft - deltaSeconds
                if (left > 0f) AircraftSlot.Waiting(left) else AircraftSlot.Flying(spawn(width, height))
            }

            is AircraftSlot.Flying -> {
                val moved = move(slot.aircraft, deltaSeconds, width, height)
                val gone = moved.hasEntered && isFullyOutside(moved, width, height)
                if (gone) AircraftSlot.Waiting(config.aircraftGapSeconds) else AircraftSlot.Flying(moved)
            }
        }

    private fun move(aircraft: MenuAircraft, deltaSeconds: Float, width: Float, height: Float): MenuAircraft {
        val moved = aircraft.copy(
            x = aircraft.x + aircraft.directionX * aircraft.speed * deltaSeconds,
            y = aircraft.y + aircraft.directionY * aircraft.speed * deltaSeconds,
        )
        return if (moved.hasEntered || isFullyOutside(moved, width, height)) moved else moved.copy(hasEntered = true)
    }

    private fun isFullyOutside(aircraft: MenuAircraft, width: Float, height: Float): Boolean =
        aircraft.x < -aircraft.radius || aircraft.x > width + aircraft.radius ||
            aircraft.y < -aircraft.radius || aircraft.y > height + aircraft.radius

    /**
     * Creates an aircraft just outside a random point on a random edge, heading for a random
     * point in the central half of the screen, so every route crosses the middle.
     */
    private fun spawn(width: Float, height: Float): MenuAircraft {
        val type = AircraftType.entries[random.nextInt(AircraftType.entries.size)]
        val radius = config.spriteSize(type) * RADIUS_FACTOR
        val outside = radius * SPAWN_MARGIN
        val along = random.nextFloat()
        val (startX, startY) = when (random.nextInt(EDGE_COUNT)) {
            0 -> along * width to -outside
            1 -> width + outside to along * height
            2 -> along * width to height + outside
            else -> -outside to along * height
        }
        val targetX = width * (CENTRAL_START + CENTRAL_SPAN * random.nextFloat())
        val targetY = height * (CENTRAL_START + CENTRAL_SPAN * random.nextFloat())
        val distance = hypot(targetX - startX, targetY - startY)
        return MenuAircraft(
            type = type,
            livery = 1 + random.nextInt(config.liveryCount),
            x = startX,
            y = startY,
            directionX = (targetX - startX) / distance,
            directionY = (targetY - startY) / distance,
            speed = config.aircraftSpeeds.getValue(type),
            radius = radius,
        )
    }

    /**
     * Centre of [cloud] at [elapsedSeconds]: its anchor plus a small closed drift path. The
     * anchor is pulled in from the edges so the whole cloud stays on screen.
     */
    fun cloudCenter(cloud: CloudSpec, elapsedSeconds: Float, width: Float, height: Float): BackdropPoint {
        val drift = config.cloudDriftRadius
        val marginX = cloud.width / 2f + drift
        val marginY = cloud.width * CLOUD_HEIGHT_RATIO / 2f + drift
        val anchorX = (cloud.anchorX * width).coerceIn(marginX, maxOf(marginX, width - marginX))
        val anchorY = (cloud.anchorY * height).coerceIn(marginY, maxOf(marginY, height - marginY))
        val angle = (2.0 * PI * elapsedSeconds / cloud.periodSeconds + cloud.phase).toFloat()
        return BackdropPoint(
            x = anchorX + drift * cos(angle),
            y = anchorY + drift * DRIFT_FLATTENING * sin(angle),
        )
    }

    companion object {
        /** A cloud is drawn on a 132 by 76 grid. */
        const val CLOUD_HEIGHT_RATIO = 76f / 132f

        /** Share of the screen, from each side, that lies outside the central half. */
        const val CENTRAL_START = 0.25f
        const val CENTRAL_SPAN = 0.5f

        private const val EDGE_COUNT = 4

        /** Half the sprite plus room for its ground shadow. */
        private const val RADIUS_FACTOR = 0.75f

        /** Spawn slightly further out than the radius, so nothing is visible on the first frame. */
        private const val SPAWN_MARGIN = 1.02f

        /** The drift path is an ellipse, flatter than it is wide. */
        private const val DRIFT_FLATTENING = 0.6f
    }
}
