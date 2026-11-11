package nl.hexmaster.finalapproach.ui.menu

import kotlin.math.abs
import kotlin.math.hypot
import kotlin.random.Random
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertIs
import kotlin.test.assertTrue
import nl.hexmaster.finalapproach.domain.AircraftKind

private const val WIDTH = 412f
private const val HEIGHT = 915f
private const val FRAME = 1f / 60f

class MenuBackdropSimulationTest {
    private val config = MenuBackdropConfig()

    private fun simulation(seed: Int = 7) = MenuBackdropSimulation(config, Random(seed))

    /** Runs the simulation for [seconds] and hands every state, with its time, to [observe]. */
    private fun run(
        seconds: Float,
        seed: Int = 7,
        observe: (time: Float, state: MenuBackdropState) -> Unit,
    ) {
        val simulation = simulation(seed)
        var state = MenuBackdropState.initial(config)
        var time = 0f
        while (time < seconds) {
            state = simulation.step(state, FRAME, WIDTH, HEIGHT)
            time += FRAME
            observe(time, state)
        }
    }

    @Test
    fun stripesTurnWithTime() {
        val quarterTurn = config.rotationPeriodSeconds / 4f
        val state = simulation().step(MenuBackdropState.initial(config), quarterTurn, WIDTH, HEIGHT)

        assertEquals(90f, state.rotationDegrees, 0.001f)
    }

    @Test
    fun stripesTakeMinutesForAFullTurn() {
        assertTrue(config.rotationPeriodSeconds >= 120f)
    }

    @Test
    fun nothingMovesWithoutTimeOrWithoutAScreen() {
        val start = MenuBackdropState.initial(config)

        assertEquals(start, simulation().step(start, 0f, WIDTH, HEIGHT))
        assertEquals(start, simulation().step(start, FRAME, 0f, 0f))
    }

    @Test
    fun thereAreExactlyThreeClouds() {
        assertEquals(3, config.clouds.size)
    }

    @Test
    fun cloudsStayNearTheirStartAndOnScreen() {
        val simulation = simulation()
        for (cloud in config.clouds) {
            val start = simulation.cloudCenter(cloud, 0f, WIDTH, HEIGHT)
            val halfWidth = cloud.width / 2f
            val halfHeight = cloud.width * MenuBackdropSimulation.CLOUD_HEIGHT_RATIO / 2f
            var time = 0f
            while (time < 600f) {
                val center = simulation.cloudCenter(cloud, time, WIDTH, HEIGHT)
                val moved = hypot(center.x - start.x, center.y - start.y)
                assertTrue(moved <= 2f * config.cloudDriftRadius + 0.001f, "cloud drifted $moved dp")
                assertTrue(center.x - halfWidth >= 0f && center.x + halfWidth <= WIDTH, "cloud left the screen sideways")
                assertTrue(center.y - halfHeight >= 0f && center.y + halfHeight <= HEIGHT, "cloud left the screen vertically")
                time += 0.25f
            }
        }
    }

    @Test
    fun cloudsActuallyMove() {
        val simulation = simulation()
        val cloud = config.clouds.first()
        val a = simulation.cloudCenter(cloud, 0f, WIDTH, HEIGHT)
        val b = simulation.cloudCenter(cloud, cloud.periodSeconds / 2f, WIDTH, HEIGHT)

        assertTrue(hypot(a.x - b.x, a.y - b.y) > 1f)
    }

    @Test
    fun theFirstAircraftAppearsAfterTheGap() {
        var firstSeen: Float? = null
        run(seconds = 10f) { time, state ->
            if (firstSeen == null && state.slot is AircraftSlot.Flying) firstSeen = time
        }

        val seen = assertNotNullValue(firstSeen)
        assertTrue(abs(seen - config.aircraftGapSeconds) <= 2f * FRAME, "first aircraft at $seen s")
    }

    @Test
    fun aNewAircraftAppearsThreeSecondsAfterThePreviousOneLeft() {
        val gaps = mutableListOf<Float>()
        var leftAt: Float? = null
        var previous: AircraftSlot = AircraftSlot.Waiting(config.aircraftGapSeconds)
        run(seconds = 600f) { time, state ->
            val slot = state.slot
            if (previous is AircraftSlot.Flying && slot is AircraftSlot.Waiting) leftAt = time
            if (previous is AircraftSlot.Waiting && slot is AircraftSlot.Flying) {
                leftAt?.let { gaps += time - it }
            }
            previous = slot
        }

        assertTrue(gaps.size >= 20, "only ${gaps.size} aircraft passed")
        for (gap in gaps) {
            assertTrue(abs(gap - 3f) <= 2f * FRAME, "gap was $gap s")
        }
    }

    @Test
    fun anAircraftOnlyLeavesWhenItIsCompletelyOffScreen() {
        var previous: AircraftSlot = AircraftSlot.Waiting(config.aircraftGapSeconds)
        run(seconds = 300f) { _, state ->
            val before = previous
            if (before is AircraftSlot.Flying && state.slot is AircraftSlot.Waiting) {
                val a = before.aircraft
                // One frame before it was removed it was at most one step away from the removal line.
                val step = a.speed * FRAME
                val outside = a.x < -a.radius + step || a.x > WIDTH + a.radius - step ||
                    a.y < -a.radius + step || a.y > HEIGHT + a.radius - step
                assertTrue(outside, "aircraft removed while still on screen at ${a.x}, ${a.y}")
            }
            previous = state.slot
        }
    }

    @Test
    fun aNewAircraftStartsCompletelyOffScreen() {
        var previous: AircraftSlot = AircraftSlot.Waiting(config.aircraftGapSeconds)
        var spawns = 0
        run(seconds = 300f) { _, state ->
            val slot = state.slot
            if (previous is AircraftSlot.Waiting && slot is AircraftSlot.Flying) {
                val a = slot.aircraft
                val outside = a.x < -a.radius || a.x > WIDTH + a.radius || a.y < -a.radius || a.y > HEIGHT + a.radius
                assertTrue(outside, "aircraft appeared inside the screen at ${a.x}, ${a.y}")
                spawns++
            }
            previous = slot
        }
        assertTrue(spawns > 5)
    }

    @Test
    fun everyRouteCrossesTheMiddleOfTheScreen() {
        val left = WIDTH * MenuBackdropSimulation.CENTRAL_START
        val right = WIDTH * (MenuBackdropSimulation.CENTRAL_START + MenuBackdropSimulation.CENTRAL_SPAN)
        val top = HEIGHT * MenuBackdropSimulation.CENTRAL_START
        val bottom = HEIGHT * (MenuBackdropSimulation.CENTRAL_START + MenuBackdropSimulation.CENTRAL_SPAN)
        var crossed = false
        var flights = 0
        var previous: AircraftSlot = AircraftSlot.Waiting(config.aircraftGapSeconds)
        run(seconds = 900f) { _, state ->
            val slot = state.slot
            if (slot is AircraftSlot.Flying) {
                val a = slot.aircraft
                if (a.x in left..right && a.y in top..bottom) crossed = true
            }
            if (previous is AircraftSlot.Flying && slot is AircraftSlot.Waiting) {
                assertTrue(crossed, "an aircraft never reached the middle of the screen")
                crossed = false
                flights++
            }
            previous = slot
        }
        assertTrue(flights >= 30, "only $flights flights completed")
    }

    @Test
    fun airplanesAndHelicoptersBothAppearFromDifferentEdges() {
        val kinds = mutableSetOf<AircraftKind>()
        val edges = mutableSetOf<String>()
        val liveries = mutableSetOf<Int>()
        var previous: AircraftSlot = AircraftSlot.Waiting(config.aircraftGapSeconds)
        run(seconds = 1800f) { _, state ->
            val slot = state.slot
            if (previous is AircraftSlot.Waiting && slot is AircraftSlot.Flying) {
                val a = slot.aircraft
                kinds += a.type.kind
                liveries += a.livery
                edges += when {
                    a.y < 0f -> "top"
                    a.x > WIDTH -> "right"
                    a.y > HEIGHT -> "bottom"
                    else -> "left"
                }
            }
            previous = slot
        }

        assertEquals(setOf(AircraftKind.Airplane, AircraftKind.Helicopter), kinds)
        assertEquals(setOf("top", "right", "bottom", "left"), edges)
        assertTrue(liveries.all { it in 1..config.liveryCount })
        assertTrue(liveries.size > 1)
    }

    @Test
    fun theSameSeedGivesTheSameAircraft() {
        fun firstAircraft(seed: Int): MenuAircraft {
            var found: MenuAircraft? = null
            run(seconds = 5f, seed = seed) { _, state ->
                val slot = state.slot
                if (found == null && slot is AircraftSlot.Flying) found = slot.aircraft
            }
            return assertNotNullValue(found)
        }

        val a = firstAircraft(42)
        val b = firstAircraft(42)
        assertEquals(a.type, b.type)
        assertEquals(a.livery, b.livery)
    }

    @Test
    fun thePageOpensWaitingForTheFirstAircraft() {
        val slot = MenuBackdropState.initial(config).slot
        assertIs<AircraftSlot.Waiting>(slot)
        assertEquals(config.aircraftGapSeconds, slot.secondsLeft)
    }

    private fun <T : Any> assertNotNullValue(value: T?): T {
        assertTrue(value != null, "expected a value")
        return value
    }
}
