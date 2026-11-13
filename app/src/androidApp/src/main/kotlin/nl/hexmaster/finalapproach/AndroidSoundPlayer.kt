package nl.hexmaster.finalapproach

import android.content.Context
import android.media.AudioAttributes
import android.media.SoundPool
import nl.hexmaster.finalapproach.platform.SoundEffect
import nl.hexmaster.finalapproach.platform.SoundPlayer
import nl.hexmaster.final_approach.R

/** Most sounds that may play at once, so quick successive clearances overlap instead of cutting each other off. */
private const val MAX_SOUND_STREAMS = 4

private const val FULL_VOLUME = 1f
private const val PRIORITY = 1
private const val NO_LOOP = 0
private const val NORMAL_RATE = 1f

/**
 * Plays the game's sound effects through a [SoundPool]: low latency, no streaming. It follows
 * the media volume. A sound that has not finished loading plays nothing; [play] never throws.
 */
class AndroidSoundPlayer(context: Context) : SoundPlayer {

    private val pool: SoundPool = SoundPool.Builder()
        .setMaxStreams(MAX_SOUND_STREAMS)
        .setAudioAttributes(
            AudioAttributes.Builder()
                .setUsage(AudioAttributes.USAGE_GAME)
                .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                .build(),
        )
        .build()

    private val loaded = mutableSetOf<Int>()
    private val ids: Map<SoundEffect, Int>

    init {
        pool.setOnLoadCompleteListener { _, sampleId, status -> if (status == 0) loaded += sampleId }
        ids = mapOf(SoundEffect.Success to pool.load(context, R.raw.success, 1))
    }

    override fun play(effect: SoundEffect) {
        val id = ids[effect] ?: return
        if (id !in loaded) return
        pool.play(id, FULL_VOLUME, FULL_VOLUME, PRIORITY, NO_LOOP, NORMAL_RATE)
    }

    /** Frees the pool; call when the activity is destroyed. */
    fun release() {
        pool.release()
    }
}
