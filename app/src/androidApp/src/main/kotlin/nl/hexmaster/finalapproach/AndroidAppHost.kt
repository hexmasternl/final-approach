package nl.hexmaster.finalapproach

import android.content.pm.ActivityInfo
import android.provider.Settings
import androidx.activity.ComponentActivity
import nl.hexmaster.finalapproach.platform.AppHost
import nl.hexmaster.finalapproach.platform.BackDispatcher

/** The Android side of [AppHost]. */
class AndroidAppHost(private val activity: ComponentActivity) : AppHost {

    /** The version of the installed build, read from the package rather than a constant. */
    override val versionName: String =
        activity.packageManager.getPackageInfo(activity.packageName, 0).versionName.orEmpty()

    /** Android has no single reduced-motion switch; animations turned off is the nearest one. */
    override val reduceMotion: Boolean =
        Settings.Global.getFloat(activity.contentResolver, Settings.Global.ANIMATOR_DURATION_SCALE, 1f) == 0f

    override val back = BackDispatcher()

    override fun quit() {
        activity.finish()
    }

    override fun lockLandscape(locked: Boolean) {
        activity.requestedOrientation = if (locked) {
            ActivityInfo.SCREEN_ORIENTATION_SENSOR_LANDSCAPE
        } else {
            ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED
        }
    }
}
