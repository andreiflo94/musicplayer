package com.example.musicplayer.domain

/** Player decisions that don't depend on Media3, so they can be tested on their own. */
object PlaybackRules {

    /**
     * "Previous" restarts the current song once it has started playing; only right at its start
     * does it go back to the song before (if there is one).
     */
    fun previousGoesToPreviousSong(progressPercent: Long, hasPreviousSong: Boolean): Boolean =
        progressPercent <= 0 && hasPreviousSong

    /** Where a seek to [percent] of the song lands, or null while its duration is still unknown. */
    fun seekPositionMs(durationMs: Long, percent: Long): Long? =
        if (durationMs <= 0) null else durationMs * percent.coerceIn(0, 100) / 100
}
