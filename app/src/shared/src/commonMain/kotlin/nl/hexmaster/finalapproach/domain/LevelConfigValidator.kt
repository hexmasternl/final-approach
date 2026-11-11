package nl.hexmaster.finalapproach.domain

import kotlin.math.abs

/** Checks a [LevelConfig] and lists every problem in words. An empty list means it is valid. */
object LevelConfigValidator {

    fun validate(config: LevelConfig): List<String> = buildList {
        addAll(zoneProblems(config))
        addAll(curveProblems(config))
        addAll(worldProblems(config))
        addAll(sceneryProblems(config))
    }

    private fun sceneryProblems(config: LevelConfig): List<String> = buildList {
        val zones = LandingZones.of(config)
        config.scenery.forEachIndexed { index, item ->
            val name = "Scenery ${index + 1} (${item.kind})"
            if (item.width <= 0.0 || item.height <= 0.0) add("$name needs a positive width and height")
            val shape = item.shape
            config.runways.filter { Overlap.rects(shape, it.shape) }
                .forEach { add("$name covers runway '${it.id}'") }
            config.helipads.filter { Overlap.rectCircle(shape, it.shape) }
                .forEach { add("$name covers helipad '${it.id}'") }
            zones.filter { zone ->
                when (zone) {
                    is LandingZone.Runway -> Overlap.rects(shape, zone.area)
                    is LandingZone.Helipad -> Overlap.rectCircle(shape, zone.area)
                }
            }.forEach { add("$name covers landing zone '${it.id}'") }
        }
    }

    private fun zoneProblems(config: LevelConfig): List<String> = buildList {
        if (config.runways.isEmpty() && config.helipads.isEmpty()) {
            add("A landing zone is required: add at least one runway or helipad")
        }
        val viewport = config.world.viewport
        for (runway in config.runways) {
            if (!runway.shape.corners.all(viewport::contains)) {
                add("Runway '${runway.id}' extends outside the viewport")
            }
            if (runway.length <= 0.0 || runway.width <= 0.0) {
                add("Runway '${runway.id}' needs a positive length and width")
            }
            val mode = runway.mode
            if (mode is RunwayMode.OneWay) {
                val offAxis = abs(Angles.difference(runway.orientation, mode.heading))
                if (offAxis > AXIS_TOLERANCE && abs(offAxis - kotlin.math.PI) > AXIS_TOLERANCE) {
                    add("Runway '${runway.id}' has a one-way heading that does not lie along its axis")
                }
            }
        }
        for (pad in config.helipads) {
            val c = pad.position
            val r = pad.radius
            if (r <= 0.0) add("Helipad '${pad.id}' needs a positive radius")
            if (c.x - r < viewport.left || c.x + r > viewport.right || c.y - r < viewport.top || c.y + r > viewport.bottom) {
                add("Helipad '${pad.id}' extends outside the viewport")
            }
        }
        val runways = config.runways
        val pads = config.helipads
        for (i in runways.indices) for (j in i + 1 until runways.size) {
            if (Overlap.rects(runways[i].shape, runways[j].shape)) {
                add("Runway '${runways[i].id}' and runway '${runways[j].id}' overlap")
            }
        }
        for (runway in runways) for (pad in pads) {
            if (Overlap.rectCircle(runway.shape, pad.shape)) {
                add("Runway '${runway.id}' and helipad '${pad.id}' overlap")
            }
        }
        for (i in pads.indices) for (j in i + 1 until pads.size) {
            if (Overlap.circles(pads[i].shape, pads[j].shape)) {
                add("Helipad '${pads[i].id}' and helipad '${pads[j].id}' overlap")
            }
        }
    }

    private fun curveProblems(config: LevelConfig): List<String> = buildList {
        val phases = config.phases
        if (phases.isEmpty()) {
            add("The difficulty curve needs at least one phase")
            return@buildList
        }
        if (phases.first().startSeconds != 0.0) add("Phase 1 must start at time 0")
        phases.forEachIndexed { index, phase ->
            val name = "Phase ${index + 1}"
            if (index > 0 && phase.startSeconds <= phases[index - 1].startSeconds) {
                add("$name starts at ${phase.startSeconds}s, which is not after the previous phase")
            }
            if (phase.spawnIntervalMin <= 0.0 || phase.spawnIntervalMax <= 0.0) {
                add("$name needs a positive spawn interval")
            }
            if (phase.spawnIntervalMin > phase.spawnIntervalMax) {
                add("$name has a minimum spawn interval above its maximum")
            }
            if (phase.weights.isEmpty()) add("$name allows no aircraft type")
            phase.weights.filterValues { it <= 0 }.keys.forEach {
                add("$name gives $it a weight that is not positive")
            }
            if (phase.maxAircraft < 1) add("$name needs a maximum of at least 1 aircraft in the air")
            val types = phase.weights.keys
            if (types.any { it.kind == AircraftKind.Helicopter } && config.helipads.isEmpty()) {
                add("$name allows helicopters, and helicopters need a helipad")
            }
            if (types.any { it.kind == AircraftKind.Airplane } && config.runways.isEmpty()) {
                add("$name allows airplanes, and airplanes need a runway")
            }
        }
    }

    private fun worldProblems(config: LevelConfig): List<String> = buildList {
        val world = config.world
        if (world.width <= 0.0 || world.height <= 0.0) add("The world needs a positive width and height")
        if (world.minimumSeparation <= 0.0) add("The minimum separation must be positive")
        if (world.spawnMargin < 0.0) add("The spawn margin must not be negative")
        addAll(config.aircraftTuning.problems())
        if (config.spawn.edges.isEmpty()) add("At least one spawn edge must be permitted")
    }

    /** How far a one-way heading may lie off the runway axis, in radians. */
    private const val AXIS_TOLERANCE = 1e-6
}
