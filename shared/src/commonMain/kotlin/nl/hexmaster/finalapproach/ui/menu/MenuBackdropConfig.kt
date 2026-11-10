package nl.hexmaster.finalapproach.ui.menu

import nl.hexmaster.finalapproach.domain.AircraftType
import nl.hexmaster.finalapproach.ui.theme.FaDimens

/**
 * One of the clouds on the main page.
 *
 * @param anchorX horizontal position as a fraction of the screen width
 * @param anchorY vertical position as a fraction of the screen height
 * @param width drawn width in dp
 * @param periodSeconds time for one lap of its small drift path
 * @param phase start angle of the drift path in radians, so clouds never move in step
 */
data class CloudSpec(
    val anchorX: Float,
    val anchorY: Float,
    val width: Float,
    val periodSeconds: Float,
    val phase: Float,
)

/** Every tunable value of the main page backdrop. Lengths are in dp, times in seconds. */
data class MenuBackdropConfig(
    /** Time for one full turn of the stripe pattern. */
    val rotationPeriodSeconds: Float = 240f,
    /** Angle covered by one stripe. */
    val rayDegrees: Float = 9f,
    /** The three clouds. */
    val clouds: List<CloudSpec> = listOf(
        CloudSpec(anchorX = 0.20f, anchorY = 0.16f, width = 150f, periodSeconds = 11f, phase = 0f),
        CloudSpec(anchorX = 0.82f, anchorY = 0.34f, width = 110f, periodSeconds = 14f, phase = 2f),
        CloudSpec(anchorX = 0.64f, anchorY = 0.86f, width = 170f, periodSeconds = 17f, phase = 4f),
    ),
    /** How far a cloud moves from its anchor. */
    val cloudDriftRadius: Float = 10f,
    /** Time between one aircraft leaving the screen and the next one appearing. */
    val aircraftGapSeconds: Float = 3f,
    /** Passing aircraft are decoration and are drawn larger than in gameplay. */
    val aircraftScale: Float = 1.5f,
    /** Menu flying speed per type in dp per second. Not the gameplay speed. */
    val aircraftSpeeds: Map<AircraftType, Float> = mapOf(
        AircraftType.LightProp to 100f,
        AircraftType.Turboprop to 120f,
        AircraftType.Jet to 150f,
        AircraftType.Jumbo to 130f,
        AircraftType.Helicopter to 90f,
    ),
    /** Number of flight-path colours an aircraft's livery is picked from. */
    val liveryCount: Int = 6,
) {
    /** Drawn size of [type] on the main page, in dp. */
    fun spriteSize(type: AircraftType): Float = aircraftScale * when (type) {
        AircraftType.LightProp -> FaDimens.spriteLightProp.value
        AircraftType.Turboprop -> FaDimens.spriteTurboprop.value
        AircraftType.Jet -> FaDimens.spriteJet.value
        AircraftType.Jumbo -> FaDimens.spriteJumbo.value
        AircraftType.Helicopter -> FaDimens.spriteHelicopter.value
    }
}
