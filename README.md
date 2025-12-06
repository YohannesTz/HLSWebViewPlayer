# HLS WebView Player

HLSWebViewPlayer is a lightweight Android library for playing HLS streams using a `WebView`. It leverages the robustness of `hls.js` to provide a reliable playback experience, avoiding many of the pitfalls and inconsistencies of native Android media players.

The library offers a simple player view for both traditional XML layouts (`HlsPlayerView`) and Jetpack Compose (`HlsWebViewPlayer`).

## Features

-   **Reliable HLS Playback**: Uses `hls.js` for wide compatibility and stable streaming.
-   **Simple Control API**: Easy-to-use controller for play, pause, stop, volume, and mute operations.
-   **State Observation**: Exposes player and load status as `StateFlow` for reactive UI updates.
-   **XML & Compose Support**: Seamless integration with both traditional and modern Android UI toolkits.
-   **Lightweight**: Adds minimal overhead to your application.

## Setup

To use this library, you would typically add it as a dependency to your `build.gradle.kts` or `build.gradle` file. (Note: This example assumes local module dependency. For remote dependency, you would use a repository like JitPack).

```kotlin
dependencies {
    implementation(project(":HLSWebViewPlayer"))
}
```

Ensure you have internet permissions in your `AndroidManifest.xml`:
```xml
<uses-permission android:name="android.permission.INTERNET" />
```

## Usage

### 1. Jetpack Compose

Using the player in a Compose-based UI is straightforward.

**Step 1: Create and remember a controller**

In your composable, create an instance of `DefaultHlsPlayerController` and hold it using `remember`.

```kotlin
import androidx.compose.runtime.remember
import com.github.yohannestz.hlswebviewplayer.DefaultHlsPlayerController

@Composable
fun MyPlayerScreen() {
    val controller = remember { DefaultHlsPlayerController() }
    // ...
}
```

**Step 2: Add the `HlsWebViewPlayer` composable**

Add the `HlsWebViewPlayer` to your UI, passing the controller you created.

```kotlin
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.ui.Modifier
import com.github.yohannestz.hlswebviewplayer.HlsWebViewPlayer

HlsWebViewPlayer(
    modifier = Modifier.aspectRatio(16 / 9f),
    controller = controller
)
```

**Step 3: Control the player**

Use the controller to manage playback and observe its state.

```kotlin
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import com.github.yohannestz.hlswebviewplayer.PlayStatus

val playStatus by controller.playStatus.collectAsState()

// Example: Play a stream when a button is clicked
Button(onClick = {
    if (playStatus == PlayStatus.PLAY) {
        controller.pause()
    } else {
        // HLS stream URL
        val hlsUrl = "https://your-stream-url.m3u8"
        controller.play(hlsUrl)
    }
}) {
    Text(if (playStatus == PlayStatus.PLAY) "Pause" else "Play")
}
```

### 2. XML Layouts

Using the player in an XML-based UI is just as simple.

**Step 1: Add the `HlsPlayerView` to your layout**

In your XML layout file, add the `HlsPlayerView`.

```xml
<!-- res/layout/activity_main.xml -->
<com.github.yohannestz.hlswebviewplayer.HlsPlayerView
    android:id="@+id/hlsPlayerView"
    android:layout_width="match_parent"
    android:layout_height="250dp" />
```

**Step 2: Get the controller and use it**

In your Activity or Fragment, get the view and access its `controller` property.

```kotlin
// src/main/java/com/example/MainActivity.kt
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.github.yohannestz.hlswebviewplayer.HlsPlayerView
import com.github.yohannestz.hlswebviewplayer.PlayStatus
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val playerView = findViewById<HlsPlayerView>(R.id.hlsPlayerView)
        val controller = playerView.controller

        // Observe player state
        controller.playStatus.onEach { status ->
            // Update UI based on status (e.g., change button icon)
        }.launchIn(lifecycleScope)

        // Example: Play a stream
        val hlsUrl = "https://your-stream-url.m3u8"
        controller.play(hlsUrl)
    }
}
```

---

That's it! You now have a reliable HLS player integrated into your Android application.
