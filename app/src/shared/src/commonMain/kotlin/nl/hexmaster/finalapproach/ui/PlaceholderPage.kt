package nl.hexmaster.finalapproach.ui

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.text.BasicText
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import nl.hexmaster.finalapproach.platform.AppHost
import nl.hexmaster.finalapproach.ui.components.FaButton
import nl.hexmaster.finalapproach.ui.components.FaButtonVariant
import nl.hexmaster.finalapproach.ui.components.faBlock
import nl.hexmaster.finalapproach.ui.sprites.FaIcon
import nl.hexmaster.finalapproach.ui.sprites.drawLawn
import nl.hexmaster.finalapproach.ui.sprites.drawSunburst
import nl.hexmaster.finalapproach.ui.theme.FaDimens
import nl.hexmaster.finalapproach.ui.theme.FaShadows
import nl.hexmaster.finalapproach.ui.theme.FaTheme

/** The ground a placeholder page stands on. */
enum class PlaceholderGround { Sky, Lawn }

private object PlaceholderDefaults {
    val panelMaxWidth = 380.dp
    val lawnStripe = 64.dp
    const val BACK = "Back"
}

/**
 * Stands in for a page that is not built yet. It shows a panel with a title, one sentence and
 * a way back, and returns on the system back button too.
 */
@Composable
fun PlaceholderPage(
    host: AppHost,
    title: String,
    body: String,
    ground: PlaceholderGround,
    onBack: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val colors = FaTheme.colors
    val type = FaTheme.type
    BackHandler(host, onBack)

    Box(modifier.fillMaxSize()) {
        Canvas(Modifier.fillMaxSize()) {
            when (ground) {
                PlaceholderGround.Sky -> drawSunburst(center, 0f, colors)
                PlaceholderGround.Lawn -> drawLawn(PlaceholderDefaults.lawnStripe.toPx(), colors)
            }
        }
        Box(
            Modifier.fillMaxSize().windowInsetsPadding(WindowInsets.safeDrawing).padding(FaDimens.space5),
            contentAlignment = Alignment.Center,
        ) {
            Column(
                Modifier
                    .widthIn(max = PlaceholderDefaults.panelMaxWidth)
                    .fillMaxWidth()
                    .faBlock(colors.surface, colors.outline, FaDimens.line3, FaDimens.radiusLg, FaShadows.lipLg)
                    .padding(
                        start = FaDimens.space5,
                        top = FaDimens.space5,
                        end = FaDimens.space5,
                        bottom = FaDimens.space5 + FaShadows.lipLg,
                    ),
            ) {
                BasicText(title, style = type.titleMd.copy(color = colors.ink))
                BasicText(
                    body,
                    modifier = Modifier.padding(top = FaDimens.space3, bottom = FaDimens.space5),
                    style = type.body.copy(color = colors.inkMuted),
                )
                FaButton(
                    text = PlaceholderDefaults.BACK,
                    onClick = onBack,
                    modifier = Modifier.fillMaxWidth(),
                    variant = FaButtonVariant.Ghost,
                    icon = FaIcon.ArrowLeft,
                )
            }
        }
    }
}
