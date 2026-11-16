// Generated from the design system (docs/design-system/bundle.js: METRICS). Do not edit by hand.
package nl.hexmaster.finalapproach.ui.sprites

/**
 * The sizes of runways, helipads and ground pieces, in world units. Across a runway they belong to
 * its 64-unit width (scale them by `width / 64`); along it they are plain world units. The game
 * draws from this table, so it cannot drift from the showcase.
 */
internal object GroundMetrics {
    object Runway {
        const val minLength = 240f
        const val fullLength = 460f
        const val touchdownLength = 560f
        const val centreInsetFull = 230f
        const val centreInsetShort = 80f
        const val width = 64f
        const val halfWidth = 32f
        const val rx = 6f
        const val shoulder = 10f
        const val shoulderHalf = 41f
        const val shoulderRx = 12f
        const val fringe = 24f
        const val fringeHalf = 54f
        const val fringeRx = 30f
        const val zonePad = 14f
        const val zoneHalf = 45f
        const val zoneRx = 16f
        const val numberInset = 58f
        const val numberSize = 27f
        val dash = listOf(26f, 20f)
        object Lines {
            const val outline = 3f
            const val shoulder = 1.6f
            const val rubber = 3f
            const val touchdown = 3f
            const val edge = 1.6f
            const val chevron = 3.2f
            const val centre = 3.2f
            const val light = 1f
        }
        object Chevron {
            const val start = 60f
            const val pitch = 40f
        }
        object EdgeLine {
            const val inset = 8f
            const val y = 27.5f
        }
        val rubber = listOf(listOf(74f, -10f, 96f), listOf(82f, -5f, 118f), listOf(78f, 6f, 104f), listOf(88f, 11f, 70f))
        object Bars {
            const val count = 6
            const val inset = 8f
            const val y0 = -25f
            const val pitch = 9f
            const val length = 22f
            const val width = 5f
            const val rx = 1f
        }
        object Aiming {
            const val inset = 118f
            val y = listOf(-21f, 10f)
            const val length = 40f
            const val width = 11f
            const val rx = 1.5f
        }
        object Touchdown {
            const val inset = 184f
            val y = listOf(-19f, -13f, 13f, 19f)
            const val length = 20f
        }
        object Lights {
            object Threshold {
                val y = listOf(-19f, -7f, 7f, 19f)
                const val out = 5f
                const val r = 2.3f
            }
            object Edge {
                const val spacing = 58f
                const val inset = 32f
                const val y = 36.5f
                const val r = 2.5f
            }
            object Approach {
                const val count = 4
                const val red = 2
                const val pitch = 9f
                const val y = 52f
                const val w = 6.5f
                const val h = 5.5f
                const val insetFull = 112f
                const val insetShort = 50f
                const val oneWayBack = 36f
            }
        }
    }
    object Helipad {
        object Fringe {
            const val half = 76f
            const val rx = 22f
        }
        object Pad {
            const val half = 69f
            const val rx = 16f
        }
        const val zoneRadius = 60f
        const val disc = 56f
        const val wornRing = 47f
        const val warnRing = 39f
        object H {
            const val x = 13f
            const val y = 18f
        }
        object CornerLight {
            const val at = 60f
            const val r = 3f
        }
        object Lines {
            const val pad = 2f
            const val joint = 1.2f
            const val outline = 3f
            const val wornRing = 5f
            const val warnRing = 5f
            const val h = 7.5f
            const val cornerLight = 1.2f
        }
        const val wornOpacity = 0.5f
    }
    object Ground {
        const val stripe = 52f
        const val stripePitch = 104f
        const val airfieldBoundary = 7f
        object Road {
            const val verge = 40f
            const val kerb = 29f
            const val surface = 25f
            val dash = listOf(9f, 10f)
            const val clearance = 44f
        }
        object Track {
            const val width = 11f
            const val edge = 4f
            const val clearance = 14f
            val rutDash = listOf(6f, 7f)
            const val rutShift = 2.5f
        }
        object Field {
            const val margin = 6f
            const val outerRx = 16f
            const val innerRx = 11f
            const val rowPitch = 9f
            const val rowInset = 7f
            const val clearance = 34f
        }
        object Pond {
            const val shore = 22f
            const val sand = 9f
            const val shallowsX = 0.74f
            const val shallowsY = 0.7f
            const val clearance = 30f
        }
        object Apron {
            const val margin = 6f
            const val rx = 14f
            const val innerRx = 10f
            const val joint = 45f
        }
        object Taxi {
            const val verge = 44f
            const val kerb = 33f
            const val surface = 29f
        }
        object Lot {
            const val margin = 5f
            const val rx = 10f
            const val innerRx = 7f
            const val bay = 22f
        }
        object Cluster {
            const val tuft = 3f
            const val flower = 5f
            const val spreadX = 16f
            const val spreadY = 12f
        }
        object Lines {
            const val roadDash = 1.8f
            const val trackRut = 1.5f
            const val taxiCentre = 1.8f
            const val taxiHold = 1.5f
            const val apronSlab = 2f
            const val apronJoint = 1.2f
            const val apronStand = 2f
            const val lotSlab = 1.6f
            const val lotBay = 1.5f
            const val fieldEdge = 2f
            const val pondShore = 1.6f
            const val pondFoam = 2f
            const val paddock = 2f
            const val cropRows = 4f
            const val ploughRows = 2.2f
        }
        const val airfieldBoundaryOpacity = 0.7f
        const val paddockOpacity = 0.9f
        object Patch {
            val rx = listOf(60f, 150f)
            val ry = listOf(40f, 90f)
            const val lobes = 9f
            const val jitter = 0.22f
            const val darkOpacity = 0.75f
            const val lightOpacity = 0.6f
            const val seaOpacity = 0.28f
        }
        object Stand {
            const val down = 36f
            const val length = 22f
            const val barAt = 14f
            const val barHalf = 11f
        }
        object HoldShort {
            const val half = 13f
            const val gap = 4f
            val dash = listOf(4f, 3f)
            const val dashAt = 9f
            const val dashGap = 4f
            const val fromEnd = 30f
        }
        val pondRipples = listOf(listOf(-38f, -12f, 10f, -6f, 22f), listOf(22f, 30f, 12f, -7f, 26f), listOf(-8f, -40f, 8f, -5f, 18f))
        const val airfieldCorner = 20f
        const val lotBayLength = 0.42f
        object Island {
            const val beach = 26f
            const val shallows = 22f
            const val sandEdge = 2f
            const val grassEdge = 7f
            val foamDash = listOf(46f, 30f)
            const val foamWidth = 2.2f
            const val foamOut = 29f
        }
    }
}
