package nl.hexmaster.finalapproach

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.addCallback
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import nl.hexmaster.finalapproach.ui.App

/** The thin Android host: it wires the platform to the shared app and nothing more. */
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        val host = AndroidAppHost(this)
        // Every back press goes to the shared code first. A screen that does not handle it
        // leaves the app.
        onBackPressedDispatcher.addCallback(this) {
            if (!host.back.dispatch()) finish()
        }
        setContent { App(host) }
    }
}
