package nl.hexmaster.finalapproach.ui.components

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicText
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import nl.hexmaster.finalapproach.domain.AircraftType
import nl.hexmaster.finalapproach.ui.sprites.FaIcon
import nl.hexmaster.finalapproach.ui.theme.FaDimens
import nl.hexmaster.finalapproach.ui.theme.FaMotion
import nl.hexmaster.finalapproach.ui.theme.FaTheme

/** Tunables of the HUD that are not design tokens. */
object HudConfig {
    /** The score value grows to this factor of its size when it rises, then returns. */
    const val SCORE_POP_SCALE = 1.15f

    /** The share of `dur-pop` the score value spends growing; the rest it spends settling back. */
    const val SCORE_POP_RISE_SHARE = 0.5f
}

/**
 * The flight strip: the flat readout every HUD value sits in. `strip-height` high, on the field's
 * contact shadow, without a lip: it shows something and cannot be pressed.
 *
 * @param label the name of the value, shown in capitals in a cell on the left; none when null
 * @param accent gives the label cell the sun tint, for the score
 * @param icon optional icon before the label
 * @param value the readout itself, in `strip-value`
 */
@Composable
fun FaFlightStrip(
    modifier: Modifier = Modifier,
    label: String? = null,
    accent: Boolean = false,
    icon: FaIcon? = null,
    value: @Composable RowScope.() -> Unit,
) {
    val colors = FaTheme.colors
    val type = FaTheme.type
    val cellFill = if (accent) colors.sun200 else colors.paper200
    val cellRule = if (accent) colors.sun700 else colors.paper400
    val cellInk = if (accent) colors.ink else colors.inkMuted
    Row(
        modifier
            .height(FaDimens.stripHeight)
            .faFlatSurface(
                fill = colors.paper100,
                outline = colors.outline,
                radius = FaDimens.radiusStrip,
                contact = colors.contactField,
                contactOffset = FaDimens.contactOffsetField,
            )
            .padding(FaDimens.lineUi)
            .clip(RoundedCornerShape(FaDimens.radiusStrip - FaDimens.lineUi)),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        if (label != null) {
            Row(
                Modifier
                    .fillMaxHeight()
                    .background(cellFill)
                    .drawBehind {
                        val rule = FaDimens.lineDetail.toPx()
                        drawRect(cellRule, Offset(size.width - rule, 0f), Size(rule, size.height))
                    }
                    .padding(horizontal = FaDimens.space2),
                horizontalArrangement = Arrangement.spacedBy(FaDimens.space1),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                if (icon != null) FaIconImage(icon, cellInk, FaDimens.iconXs)
                BasicText(label.uppercase(), style = type.labelSm.copy(color = cellInk), maxLines = 1)
            }
        }
        Row(
            Modifier.padding(horizontal = FaDimens.space3),
            horizontalArrangement = Arrangement.spacedBy(FaDimens.space2),
            verticalAlignment = Alignment.CenterVertically,
            content = value,
        )
    }
}

/**
 * The Score Counter: a flight strip with the [label] in its cell, an optional [icon] before it,
 * and the [value]. [accent] tints the label cell, as for the current score. The value pops when it
 * rises if [popOnRise] is set, unless the device asks for reduced motion.
 */
@Composable
fun FaScoreCounter(
    label: String,
    value: Int,
    modifier: Modifier = Modifier,
    accent: Boolean = false,
    icon: FaIcon? = null,
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
        } else {
            scale.snapTo(1f)
        }
        shown = value
    }
    FaFlightStrip(
        modifier = modifier.semantics(mergeDescendants = true) { contentDescription = "$label $value" },
        label = label,
        accent = accent,
        icon = icon,
    ) {
        BasicText(
            formatScore(value),
            modifier = Modifier.graphicsLayer {
                scaleX = scale.value
                scaleY = scale.value
            },
            style = type.stripValue.copy(color = colors.ink),
            maxLines = 1,
        )
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
