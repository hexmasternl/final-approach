package nl.hexmaster.finalapproach.ui.game

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicText
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.disabled
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.rememberTextMeasurer
import nl.hexmaster.finalapproach.domain.LevelConfig
import nl.hexmaster.finalapproach.ui.components.FaChip
import nl.hexmaster.finalapproach.ui.components.FaChipTone
import nl.hexmaster.finalapproach.ui.components.FaIconImage
import nl.hexmaster.finalapproach.ui.components.FaMoulded
import nl.hexmaster.finalapproach.ui.components.ZeroDp
import nl.hexmaster.finalapproach.ui.components.faFaceShape
import nl.hexmaster.finalapproach.ui.components.faFlatSurface
import nl.hexmaster.finalapproach.ui.components.formatScore
import nl.hexmaster.finalapproach.ui.components.tonePaper
import nl.hexmaster.finalapproach.ui.sprites.FaIcon
import nl.hexmaster.finalapproach.ui.theme.FaDimens
import nl.hexmaster.finalapproach.ui.theme.FaOpacity
import nl.hexmaster.finalapproach.ui.theme.FaTheme

/** Test tags of the parts of a level card. */
internal object LevelCardTags {
    const val CARD = "level-card"
    const val LOCK_BADGE = "level-card-lock"
}

/**
 * One level as a card (`LevelCard.md`): the level's own art, a `line-ui` outline line, then the
 * name and the tags. An unlocked card is a moulded paper card that presses like a button; a
 * locked card is flat, washed out under a lock badge, and does not react.
 *
 * What the card shows besides the level comes in as [state]; deciding that a level is locked
 * does not belong here.
 */
@Composable
internal fun LevelCard(level: LevelConfig, state: LevelCardState, onClick: () -> Unit, modifier: Modifier = Modifier) {
    val colors = FaTheme.colors
    val locked = state.locked
    if (locked == null) {
        FaMoulded(
            onClick = onClick,
            tone = colors.tonePaper,
            radius = FaDimens.radiusPanel,
            lip = FaDimens.lipPanel,
            modifier = modifier.width(FaDimens.levelTile).testTag(LevelCardTags.CARD),
            contentAlignment = Alignment.TopCenter,
        ) {
            LevelCardFace(level, state, Modifier.clip(faFaceShape(FaDimens.radiusPanel, FaDimens.lipPanel)))
        }
    } else {
        // Disabled is flat: paper tones at full opacity, no lip, no shine, no contact shadow.
        Box(
            modifier
                .width(FaDimens.levelTile)
                .testTag(LevelCardTags.CARD)
                .faFlatSurface(
                    fill = colors.paper200,
                    outline = colors.ink3,
                    radius = FaDimens.radiusPanel,
                    contact = Color.Transparent,
                    contactOffset = ZeroDp,
                )
                .padding(FaDimens.lineUi)
                .semantics(mergeDescendants = true) {
                    disabled()
                    contentDescription = "${level.name}. ${locked.text}"
                },
        ) {
            LevelCardFace(level, state, Modifier.clip(RoundedCornerShape(FaDimens.radiusPanel - FaDimens.lineUi)))
        }
    }
}

@Composable
private fun LevelCardFace(level: LevelConfig, state: LevelCardState, modifier: Modifier) {
    val colors = FaTheme.colors
    val type = FaTheme.type
    val locked = state.locked
    Column(modifier.fillMaxWidth()) {
        Box(Modifier.fillMaxWidth().height(FaDimens.levelTileArt)) {
            // The art is the level's own field, in the palette of the level's own theme.
            LevelMiniature(
                level,
                levelPalette(level),
                rememberTextMeasurer(),
                type.titleMd,
                rememberGroundArt(level),
                Modifier.fillMaxWidth().height(FaDimens.levelTileArt),
            )
            if (locked != null) {
                Box(Modifier.matchParentSize().background(colors.paper0.copy(alpha = FaOpacity.levelWash)))
                LockBadge(Modifier.align(Alignment.Center))
            }
            if (state.showsBestScore) {
                FaChip(
                    text = formatScore(state.bestScore),
                    tone = FaChipTone.Sun,
                    icon = FaIcon.Star,
                    modifier = Modifier.align(Alignment.TopEnd).padding(FaDimens.space2),
                )
            }
        }
        Box(Modifier.fillMaxWidth().height(FaDimens.lineUi).background(if (locked == null) colors.outline else colors.ink3))
        Column(
            Modifier.fillMaxWidth().padding(FaDimens.space2),
            verticalArrangement = Arrangement.spacedBy(FaDimens.space1),
        ) {
            BasicText(level.name, style = type.titleSm.copy(color = if (locked == null) colors.ink else colors.inkMuted), maxLines = 1)
            Row(horizontalArrangement = Arrangement.spacedBy(FaDimens.space1)) {
                if (locked == null) {
                    state.tags.forEach { FaChip(it) }
                } else {
                    FaChip(locked.text, tone = FaChipTone.Plain, icon = FaIcon.Lock)
                }
            }
        }
    }
}

/** A `paper-0` disc with a `line-ui` outline and the lock icon: the lock itself, not only its tint, says "locked". */
@Composable
private fun LockBadge(modifier: Modifier = Modifier) {
    val colors = FaTheme.colors
    Box(modifier.size(FaDimens.lockBadge).testTag(LevelCardTags.LOCK_BADGE), contentAlignment = Alignment.Center) {
        Canvas(Modifier.size(FaDimens.lockBadge)) {
            drawCircle(colors.paper0)
            val line = FaDimens.lineUi.toPx()
            drawCircle(colors.outline, radius = size.minDimension / 2f - line / 2f, style = Stroke(line))
        }
        FaIconImage(FaIcon.Lock, colors.ink, FaDimens.iconMd)
    }
}
