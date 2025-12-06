package com.github.yohannestz.hlswebviewplayer


import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Bitmap
import android.util.AttributeSet
import android.util.Log
import android.webkit.ConsoleMessage
import android.webkit.JavascriptInterface
import android.webkit.WebChromeClient
import android.webkit.WebSettings
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.FrameLayout
import androidx.core.graphics.createBitmap


/**
 * A [FrameLayout] that plays HLS video streams using a WebView with hls.js.
 * This View is suitable for use in traditional XML layouts.
 *
 * It encapsulates a [WebView] that loads an internal HTML player. The playback
 * is controlled via the [controller] property.
 *
 * @see HlsWebViewPlayer for the Jetpack Compose equivalent.
 */
@SuppressLint("JavascriptInterface", "SetJavaScriptEnabled")
class HlsPlayerView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyle: Int = 0
): FrameLayout(context, attrs, defStyle) {


    private val webView: WebView
    /**
     * The controller for this player instance. Use this to send commands
     * like play, pause, etc., and to observe the player's state.
     */
    var controller: DefaultHlsPlayerController = DefaultHlsPlayerController()
        set(value) {
            field = value
            wireControllerBridge()
        }


    init {
        webView = WebView(context).apply {
            settings.javaScriptEnabled = true
            settings.mediaPlaybackRequiresUserGesture = false
            settings.domStorageEnabled = true
            settings.databaseEnabled = true
            settings.loadWithOverviewMode = true
            settings.useWideViewPort = true
            settings.cacheMode = WebSettings.LOAD_CACHE_ELSE_NETWORK
            setInitialScale(1)
            addJavascriptInterface(AndroidBridge(), "AndroidInterface")


            webViewClient = object: WebViewClient() {
                override fun onPageFinished(view: WebView?, url: String?) {
                    super.onPageFinished(view, url)
                    onStatusChanged("READY", "Player is ready")
                }
            }


            webChromeClient = object: WebChromeClient() {
                override fun getDefaultVideoPoster(): Bitmap {
                    return createBitmap(1, 1)
                }

                override fun onConsoleMessage(consoleMessage: ConsoleMessage): Boolean {
                    Log.d("HlsPlayerView", "[WebView Console] ${consoleMessage.message()}")
                    return true
                }
            }


            loadUrl("file:///android_asset/hls_player.html")
        }


        addView(webView, LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT))
        wireControllerBridge()
    }

    /**
     * Cleans up the resources used by the WebView.
     * This should be called when the view is no longer needed, to prevent memory leaks.
     */
    fun destroy() {
        webView.stopLoading()
        webView.destroy()
    }

    override fun onDetachedFromWindow() {
        super.onDetachedFromWindow()
        destroy()
    }


    private fun wireControllerBridge() {
        controller.webViewBridge = { cmd, arg ->
            val script = when (cmd) {
                "play" -> "playStream('${escapeJs(arg ?: "")}');"
                "stop" -> "stopStream();"
                "pause" -> "pauseStream();"
                "resume" -> "resumeStream();"
                "setVolume" -> "setVolume(${arg ?: "1.0"});"
                "setMuted" -> "setMuted(${arg == "1"});"
                else -> null
            }
            script?.let { webView.post { webView.evaluateJavascript(it, null) } }
        }
    }


    private fun escapeJs(s: String): String {
        return s.replace("\\", "\\\\").replace("'", "\\'").replace("\n", "\\n")
    }

    private fun onStatusChanged(status: String, message: String) {
        when (status.uppercase()) {
            "READY" -> controller._loadStatus.value = LoadStatus.IDLE
            "LOADING" -> controller._loadStatus.value = LoadStatus.LOADING
            "PLAYING" -> {
                controller._loadStatus.value = LoadStatus.SUCCESS
                controller._playStatus.value = PlayStatus.PLAY
            }
            "BUFFERING" -> controller._loadStatus.value = LoadStatus.LOADING
            "PAUSED" -> controller._playStatus.value = PlayStatus.PAUSE
            "STOPPED" -> {
                controller._playStatus.value = PlayStatus.STOP
                controller._loadStatus.value = LoadStatus.IDLE
            }
            "ERROR" -> {
                controller._loadStatus.value = LoadStatus.ERROR(message)
                controller._playStatus.value = PlayStatus.STOP
            }
            "ENDED" -> controller._playStatus.value = PlayStatus.STOP
        }
    }


    inner class AndroidBridge {
        @JavascriptInterface
        fun log(level: String, message: String) {
            when (level.uppercase()) {
                "ERROR" -> Log.e("HlsPlayerView", message)
                "WARN" -> Log.w("HlsPlayerView", message)
                "DEBUG" -> Log.d("HlsPlayerView", message)
                else -> Log.i("HlsPlayerView", message)
            }
        }


        @JavascriptInterface
        fun onStatusChanged(status: String, message: String) {
            post { this@HlsPlayerView.onStatusChanged(status, message) }
        }
    }
}