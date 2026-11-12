package nl.hexmaster.finalapproach.ui.components

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.tween
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.text.BasicText
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import nl.hexmaster.finalapproach.domain.AircraftType
import nl.hexmaster.finalapproach.ui.sprites.FaIcon
import nl.hexmaster.finalapproach.ui.theme.FaDimens
import nl.hexmaster.finalapproach.ui.theme.FaMotion
import nl.hexmaster.finalapproach.ui.theme.FaShadows
import nl.hexmaster.finalapproach.ui.theme.FaTheme

/** Tunables of the HUD that are not design tokens. */
object HudConfig {
    /** The score value grows to this factor of its size when it rises, then returns. */
    const val SCORE_POP_SCALE = 1.15f

    /** The share of `dur-pop` the score value spends growing; the rest it spends settling back. */
    const val SCORE_POP_RISE_SHARE = 0.5f
}

/** The outlined chip face every HUD item stands on. */
@Composable
private fun Modifier.hudChipFace(): Modifier {
    val colors = FaTheme.colors
    return this
        .height(FaDimens.hudChip + FaShadows.lipSm)
        .faBlock(
            fill = colors.surface,
            outline = colors.outline,
            border = FaDimens.line3,
            radius = FaDimens.radiusMd,
            lip = FaShadows.lipSm,
        )
        .padding(start = FaDimens.space4, end = FaDimens.space4, bottom = FaShadows.lipSm)
}

/**
 * The Score Counter: a HUD chip with an optional leading [icon] and the [label] above the
 * [value]. [compact] shows the value in the smaller `score-compact` style, as for Best. The value
 * pops when it rises if [popOnRise] is set, unless the device asks for reduced motion.
 */
@Composable
fun FaScoreCounter(
    label: String,
    value: Int,
    modifier: Modifier = Modifier,
    compact: Boolean = false,
    icon: FaIcon? = null,
    iconColor: Color = FaTheme.colors.ink,
    popOnRise: Boolean = false,
) {
    val colors = FaTheme.colors
    val type = FaTheme.type
    val scale = remember { Animatable(1f) }
    var shown by remember { mutableIntStateOf(value) }
    val reduceMotion = FaTheme.reduceMotion
    LaunchedEffect(value) {
        if (popOnRise && !reduceMotion && value > shown) {
            val riseMs = (FaMotion.durPopMs * HudConfig.SCORE_POP_RISE_SHARE).toInt()
            scale.animateTo(HudConfig.SCORE_POP_SCALE, tween(riseMs, easing = FaMotion.easeSnap))
            scale.animateTo(1f, tween(FaMotion.durPopMs - riseMs, easing = FaMotion.easeBounce))
        }
        shown = value
    }
    Row(
        modifier
            .hudChipFace()
            .semantics(mergeDescendants = true) { contentDescription = "$label $value" },
        horizontalArrangement = Arrangement.spacedBy(FaDimens.space3),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        if (icon != null) FaIconImage(icon, iconColor, FaDimens.iconMd)
        Column {
            BasicText(label.uppercase(), style = type.label.copy(color = colors.inkMuted))
            BasicText(
                formatScore(value),
                modifier = Modifier.graphicsLayer {
                    scaleX = scale.value
                    scaleY = scale.value
                },
                style = (if (compact) type.scoreCompact else type.score).copy(color = colors.ink),
            )
        }
    }
}

/** The name of an aircraft type as shown to the player. */
val AircraftType.displayName: String
    get() = when (this) {
        AircraftType.LightProp -> "Light prop"
        AircraftType.Turboprop -> "Turboprop"
        AircraftType.Jet -> "Jet"
        AircraftType.Jumbo -> "Jumbo"
        AircraftType.Helicopter -> "Helicopter"
    }

/** A square chip button with an icon, at least a full touch target. */
@Composable
fun FaIconButton(icon: FaIcon, description: String, onClick: () -> Unit, modifier: Modifier = Modifier) {
    val colors = FaTheme.colors
    Box(
        modifier
            .width(FaDimens.hudChip)
            .height(FaDimens.hudChip + FaShadows.lipSm)
            .faBlock(
                fill = colors.surface,
                outline = colors.outline,
                border = FaDimens.line3,
                radius = FaDimens.radiusMd,
                lip = FaShadows.lipSm,
            )
            .clickable(role = Role.Button, onClick = onClick)
            .semantics { contentDescription = description }
            .padding(bottom = FaShadows.lipSm),
        contentAlignment = Alignment.Center,
    ) {
        FaIconImage(icon, colors.ink, FaDimens.iconMd)
    }
}
