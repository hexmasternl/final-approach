package nl.hexmaster.finalapproach

import android.content.Context
import android.content.pm.ActivityInfo
import android.provider.Settings
import androidx.activity.ComponentActivity
import nl.hexmaster.finalapproach.platform.Ads
import nl.hexmaster.finalapproach.platform.AppHost
import nl.hexmaster.finalapproach.platform.BackDispatcher
import nl.hexmaster.finalapproach.platform.HighScoreStore

private const val PREFERENCES_NAME = "best-scores"

/** The Android side of [AppHost]. */
class AndroidAppHost(private val activity: ComponentActivity) : AppHost {

    /** The version of the installed build, read from the package rather than a constant. */
    override val versionName: String =
        activity.packageManager.getPackageInfo(activity.packageName, 0).versionName.orEmpty()

    /** Android has no single reduced-motion switch; animations turned off is the nearest one. */
    override val reduceMotion: Boolean =
        Settings.Global.getFloat(activity.contentResolver, Settings.Global.ANIMATOR_DURATION_SCALE, 1f) == 0f

    override val back = BackDispatcher()

    override val ads: Ads = AdMobAds(activity)

    /** Best scores live in a private preferences file, one integer per level. */
    override val highScores = object : HighScoreStore {
        private val preferences = activity.getSharedPreferences(PREFERENCES_NAME, Context.MODE_PRIVATE)

        override fun best(levelId: String): Int = preferences.getInt(levelId, 0)

        override fun save(levelId: String, score: Int) {
            preferences.edit().putInt(levelId, score).apply()
        }
    }

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
