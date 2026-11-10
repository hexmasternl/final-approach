package nl.hexmaster.finalapproach.ui

import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberUpdatedState
import nl.hexmaster.finalapproach.platform.AppHost

/**
 * Handles the system back button for as long as this is in the composition. The most recently
 * composed handler wins, so a page placed on top of another takes over its back presses.
 */
@Composable
fun BackHandler(host: AppHost, onBack: () -> Unit) {
    val currentOnBack by rememberUpdatedState(onBack)
    DisposableEffect(host.back) {
        val remove = host.back.push { currentOnBack() }
        onDispose { remove() }
    }
}
