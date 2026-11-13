package nl.hexmaster.finalapproach

import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.content.pm.ActivityInfo
import android.net.Uri
import android.provider.Settings
import android.view.WindowManager
import androidx.activity.ComponentActivity
import nl.hexmaster.finalapproach.domain.PlayerSettings
import nl.hexmaster.finalapproach.domain.ReduceMotionChoice
import nl.hexmaster.finalapproach.platform.Ads
import nl.hexmaster.finalapproach.platform.AppHost
import nl.hexmaster.finalapproach.platform.BackDispatcher
import nl.hexmaster.finalapproach.platform.HighScoreStore
import nl.hexmaster.finalapproach.platform.SettingsStore
import nl.hexmaster.finalapproach.platform.SoundPlayer

private const val PREFERENCES_NAME = "best-scores"
private const val SETTINGS_PREFERENCES_NAME = "settings"
private const val KEY_SOUND_EFFECTS = "sound_effects"
private const val KEY_REDUCE_MOTION = "reduce_motion"

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

    private val soundPlayer = AndroidSoundPlayer(activity)

    override val sounds: SoundPlayer = soundPlayer

    /** Best scores live in a private preferences file, one integer per level. */
    override val highScores = object : HighScoreStore {
        private val preferences = activity.getSharedPreferences(PREFERENCES_NAME, Context.MODE_PRIVATE)

        override fun best(levelId: String): Int = preferences.getInt(levelId, 0)

        override fun save(levelId: String, score: Int) {
            preferences.edit().putInt(levelId, score).apply()
        }
    }

    /** Frees what the host holds on to; call when the activity is destroyed. */
    fun release() {
        soundPlayer.release()
    }

    /** Settings live in their own private preferences file; the enum is stored by name and unknown values fall back to the default. */
    override val settings = object : SettingsStore {
        private val preferences = activity.getSharedPreferences(SETTINGS_PREFERENCES_NAME, Context.MODE_PRIVATE)

        override fun load(): PlayerSettings {
            val defaults = PlayerSettings()
            return PlayerSettings(
                soundEffects = preferences.getBoolean(KEY_SOUND_EFFECTS, defaults.soundEffects),
                reduceMotion = ReduceMotionChoice.entries
                    .firstOrNull { it.name == preferences.getString(KEY_REDUCE_MOTION, null) }
                    ?: defaults.reduceMotion,
            )
        }

        override fun save(settings: PlayerSettings) {
            preferences.edit()
                .putBoolean(KEY_SOUND_EFFECTS, settings.soundEffects)
                .putString(KEY_REDUCE_MOTION, settings.reduceMotion.name)
                .apply()
        }
    }

    override fun quit() {
        activity.finish()
    }

    override fun openUrl(url: String) {
        try {
            activity.startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(url)))
        } catch (_: ActivityNotFoundException) {
            // No browser on this device: stay on the page.
        }
    }

    override fun keepScreenOn(on: Boolean) {
        val window = activity.window
        if (on) {
            window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
        } else {
            window.clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
        }
    }

    override fun lockLandscape(locked: Boolean) {
        activity.requestedOrientation = if (locked) {
            ActivityInfo.SCREEN_ORIENTATION_SENSOR_LANDSCAPE
        } else {
            ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED
        }
    }
}
