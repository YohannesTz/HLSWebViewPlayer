package com.github.yohannestz.hlswebviewplayer

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.viewinterop.AndroidView

/**
 * A Jetpack Compose composable that plays HLS video streams.
 *
 * This composable wraps the [HlsPlayerView] in an [AndroidView], making it easy to use
 * in a Compose-based UI. Playback is controlled by the provided [controller].
 *
 * @param modifier The modifier to be applied to the player.
 * @param controller The [HlsPlayerController] to control the player and observe its state.
 * A controller instance must be created and remembered outside of this composable.
 */
@Composable
fun HlsWebViewPlayer(
    modifier: Modifier = Modifier,
    controller: HlsPlayerController
) {
    val hlsPlayerController = controller as DefaultHlsPlayerController
    AndroidView(
        modifier = modifier,
        factory = { ctx ->
            HlsPlayerView(ctx).apply {
                this.controller = hlsPlayerController
            }
        },
        update = { view ->
            if (view.controller != hlsPlayerController) {
                view.controller = hlsPlayerController
            }
        }
    )
}