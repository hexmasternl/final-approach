package nl.hexmaster.finalapproach.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.BasicText
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.graphicsLayer
import nl.hexmaster.finalapproach.ui.sprites.FaIcon
import nl.hexmaster.finalapproach.ui.theme.FaDimens
import nl.hexmaster.finalapproach.ui.theme.FaTheme

/** Geometry of the tilted labels that is not a token of its own. */
private object FaLabelDefaults {
    /** Tilt of a tangerine sticker and of a stamp, in degrees. */
    const val TILT_DEGREES = -4f

    /** Tilt of a sun sticker, the other way, in degrees. */
    const val TILT_BACK_DEGREES = 3f
}

/** The tint of a [FaChip]. The words on the chip carry the meaning; the tint only supports it. */
enum class FaChipTone { Sky, Sun, Mint, Cherry, Plain }

/**
 * A chip: a small flat tag for a fact, such as "1 runway". A tinted fill with a fine edge in a
 * darker tone of the same colour. It has no outline and no lip, and cannot be pressed.
 */
@Composable
fun FaChip(text: String, modifier: Modifier = Modifier, tone: FaChipTone = FaChipTone.Sky, icon: FaIcon? = null) {
    val colors = FaTheme.colors
    val (fill, edge, ink) = when (tone) {
        FaChipTone.Sky -> Triple(colors.sky100, colors.sky300, colors.sky900)
        FaChipTone.Sun -> Triple(colors.sun200, colors.sun700, colors.sun900)
        FaChipTone.Mint -> Triple(colors.mint200, colors.mint700, colors.mint900)
        FaChipTone.Cherry -> Triple(colors.cherry200, colors.cherry700, colors.cherry900)
        FaChipTone.Plain -> Triple(colors.paper200, colors.paper400, colors.inkMuted)
    }
    Row(
        modifier
            .height(FaDimens.chipHeight)
            .drawBehind {
                val line = FaDimens.lineDetail.toPx()
                val radius = FaDimens.radiusChip.toPx()
                drawRoundRect(fill, cornerRadius = CornerRadius(radius))
                drawRoundRect(
                    edge,
                    Offset(line / 2f, line / 2f),
                    Size(size.width - line, size.height - line),
                    CornerRadius(radius - line / 2f),
                    style = Stroke(line),
                )
            }
            .padding(horizontal = FaDimens.space2),
        horizontalArrangement = Arrangement.spacedBy(FaDimens.space1),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        if (icon != null) FaIconImage(icon, ink, FaDimens.iconXs)
        BasicText(text, style = FaTheme.type.caption.copy(color = ink), maxLines = 1)
    }
}

/** The colour of a [FaSticker]. */
enum class FaStickerTone { Tangerine, Sun }

/**
 * A sticker: the tilted pill for a moment worth cheering, such as a streak or a score popup. It
 * is the only tilted shape on the HUD and carries a paper rim, so it reads on any ground. It
 * shows something and cannot be pressed.
 *
 * @param caption optional small capitals after the text
 */
@Composable
fun FaSticker(
    text: String,
    modifier: Modifier = Modifier,
    tone: FaStickerTone = FaStickerTone.Tangerine,
    icon: FaIcon? = null,
    caption: String? = null,
) {
    val colors = FaTheme.colors
    val type = FaTheme.type
    val (light, base, deep) = when (tone) {
        FaStickerTone.Tangerine -> Triple(colors.tangerine200, colors.tangerine500, colors.tangerine700)
        FaStickerTone.Sun -> Triple(colors.sun200, colors.sun500, colors.sun700)
    }
    val tilt = if (tone == FaStickerTone.Sun) FaLabelDefaults.TILT_BACK_DEGREES else FaLabelDefaults.TILT_DEGREES
    Row(
        modifier
            // The rim and the shadow lie outside the sticker's own bounds; a layer does not clip them.
            .graphicsLayer { rotationZ = tilt }
            .height(FaDimens.stickerHeight)
            .drawBehind {
                val rim = FaDimens.stickerRim.toPx()
                val rimmed = Size(size.width + 2f * rim, size.height + 2f * rim)
                val pill = CornerRadius(rimmed.height / 2f)
                drawRoundRect(colors.contactField, Offset(-rim, -rim + FaDimens.contactOffsetField.toPx()), rimmed, pill)
                drawRoundRect(colors.paper0, Offset(-rim, -rim), rimmed, pill)
                drawMoulded(
                    tone = FaTone(light, base, deep, deep),
                    outline = colors.outline,
                    border = FaDimens.lineUi.toPx(),
                    radius = size.height / 2f,
                    lip = FaDimens.lipInset.toPx(),
                    insetLip = true,
                    lipEdge = 0f,
                )
            }
            .padding(horizontal = FaDimens.space3),
        horizontalArrangement = Arrangement.spacedBy(FaDimens.space1),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        if (icon != null) FaIconImage(icon, colors.ink, FaDimens.iconXs)
        BasicText(text, style = type.buttonLabel.copy(color = colors.ink), maxLines = 1)
        if (caption != null) BasicText(caption.uppercase(), style = type.labelSm.copy(color = colors.ink), maxLines = 1)
    }
}

/**
 * A stamp: a small tilted label with a double border, as inked onto a boarding pass. It repeats
 * in two or three words what the text beside it says; it never carries the message alone.
 */
@Composable
fun FaStamp(text: String, modifier: Modifier = Modifier) {
    val colors = FaTheme.colors
    BasicText(
        text.uppercase(),
        modifier = modifier
            .graphicsLayer { rotationZ = FaLabelDefaults.TILT_DEGREES }
            // Room for the outer border, which lies around the inner one.
            .padding(FaDimens.lineUiSm + FaDimens.lineDetail)
            .drawBehind {
                val inner = FaDimens.lineUiSm.toPx()
                val outer = FaDimens.lineDetail.toPx()
                val radius = FaDimens.radiusChip.toPx()
                drawRoundRect(
                    colors.cherry700,
                    Offset(inner / 2f, inner / 2f),
                    Size(size.width - inner, size.height - inner),
                    CornerRadius(radius - inner / 2f),
                    style = Stroke(inner),
                )
                val reach = inner + outer / 2f
                drawRoundRect(
                    colors.cherry700,
                    Offset(-reach, -reach),
                    Size(size.width + 2f * reach, size.height + 2f * reach),
                    CornerRadius(radius + reach),
                    style = Stroke(outer),
                )
            }
            .padding(horizontal = FaDimens.space2, vertical = FaDimens.space1),
        style = FaTheme.type.labelSm.copy(color = colors.cherry900),
        maxLines = 1,
    )
}
