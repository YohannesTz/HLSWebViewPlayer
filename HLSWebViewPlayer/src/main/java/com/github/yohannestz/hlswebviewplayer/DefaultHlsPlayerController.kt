package com.github.yohannestz.hlswebviewplayer

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

/**
 * Default implementation of the [HlsPlayerController] interface.
 * This class manages the player state and sends commands to the underlying WebView.
 */
class DefaultHlsPlayerController: HlsPlayerController {
    internal val _playStatus = MutableStateFlow<PlayStatus>(PlayStatus.STOP)
    internal val _loadStatus = MutableStateFlow<LoadStatus>(LoadStatus.IDLE)


    override val playStatus: StateFlow<PlayStatus> = _playStatus
    override val loadStatus: StateFlow<LoadStatus> = _loadStatus


    // These methods are intended to be proxied to the WebView bridge
    internal var webViewBridge: ((cmd: String, arg: String?) -> Unit)? = null


    override fun play(url: String) {
        webViewBridge?.invoke("play", url)
        _loadStatus.value = LoadStatus.LOADING
    }


    override fun stop() {
        webViewBridge?.invoke("stop", null)
    }


    override fun pause() {
        webViewBridge?.invoke("pause", null)
    }


    override fun resume() {
        webViewBridge?.invoke("resume", null)
    }


    override fun setVolume(volume: Float) {
        webViewBridge?.invoke("setVolume", volume.toString())
    }


    override fun setMuted(muted: Boolean) {
        webViewBridge?.invoke("setMuted", if (muted) "1" else "0")
    }
}