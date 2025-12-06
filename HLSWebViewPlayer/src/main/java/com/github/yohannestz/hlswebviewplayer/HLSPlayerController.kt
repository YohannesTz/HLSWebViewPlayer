package com.github.yohannestz.hlswebviewplayer

import kotlinx.coroutines.flow.StateFlow

/**
 * Sealed class representing the playback status of the player.
 */
sealed class PlayStatus {
    /** Player is stopped. */
    object STOP: PlayStatus()
    /** Player is actively playing. */
    object PLAY: PlayStatus()
    /** Player is paused. */
    object PAUSE: PlayStatus()
}

/**
 * Sealed class representing the stream loading status.
 */
sealed class LoadStatus {
    /** Player is idle, no stream is loaded. */
    object IDLE: LoadStatus()
    /** Stream is currently loading or buffering. */
    object LOADING: LoadStatus()
    /** Stream has loaded successfully. */
    object SUCCESS: LoadStatus()
    /** An error occurred while loading the stream. */
    data class ERROR(val message:String): LoadStatus()
}


/**
 * Interface for controlling the HLS player.
 */
interface HlsPlayerController {
    // Commands
    /**
     * Loads and starts playing a stream from the given URL.
     * @param url The HLS stream URL (.m3u8).
     */
    fun play(url: String)

    /**
     * Stops playback and unloads the stream.
     */
    fun stop()

    /**
     * Pauses the current playback.
     */
    fun pause()

    /**
     * Resumes playback if paused.
     */
    fun resume()

    /**
     * Sets the player volume.
     * @param volume The volume level from 0.0f (silent) to 1.0f (max).
     */
    fun setVolume(volume: Float)

    /**
     * Mutes or unmutes the player.
     * @param muted True to mute, false to unmute.
     */
    fun setMuted(muted: Boolean)


    // State flows
    /**
     * A [StateFlow] that emits the current [PlayStatus] of the player.
     */
    val playStatus: StateFlow<PlayStatus>

    /**
     * A [StateFlow] that emits the current [LoadStatus] of the stream.
     */
    val loadStatus: StateFlow<LoadStatus>
}