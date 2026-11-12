package nl.hexmaster.finalapproach.ui.components

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
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
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import nl.hexmaster.finalapproach.domain.AircraftType
import nl.hexmaster.finalapproach.ui.sprites.FaIcon
import nl.hexmaster.finalapproach.ui.sprites.drawAircraftBody
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

    /** The aircraft thumbnail of a spawn counter is drawn at this fraction of the icon size. */
    const val COUNTER_SPRITE_FRACTION = 1f
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
        .padding(start = FaDimens.space3, end = FaDimens.space3, bottom = FaShadows.lipSm)
}

/**
 * A HUD chip with a [label] and a [value]. The value pops when it rises, unless the device asks
 * for reduced motion.
 */
@Composable
fun FaStatChip(label: String, value: Int, modifier: Modifier = Modifier, popOnRise: Boolean = false) {
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
        horizontalArrangement = Arrangement.spacedBy(FaDimens.space2),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        BasicText(label, style = type.label.copy(color = colors.inkMuted))
        BasicText(
            value.toString(),
            modifier = Modifier.graphicsLayer {
                scaleX = scale.value
                scaleY = scale.value
            },
            style = type.titleMd.copy(color = colors.ink),
        )
    }
}

/** A HUD chip showing how many aircraft of [type] spawned: the silhouette and the count. */
@Composable
fun FaSpawnCounter(type: AircraftType, count: Int, modifier: Modifier = Modifier) {
    val colors = FaTheme.colors
    val textStyle = FaTheme.type.titleMd
    Row(
        modifier
            .hudChipFace()
            .semantics(mergeDescendants = true) { contentDescription = "${type.displayName} $count" },
        horizontalArrangement = Arrangement.spacedBy(FaDimens.space2),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Canvas(Modifier.size(FaDimens.iconLg)) {
            drawAircraftBody(
                type,
                colors.fuselage,
                Offset(size.width / 2f, size.height / 2f),
                size.minDimension * HudConfig.COUNTER_SPRITE_FRACTION,
                0f,
                0f,
                colors,
            )
        }
        BasicText(count.toString(), style = textStyle.copy(color = colors.ink))
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
