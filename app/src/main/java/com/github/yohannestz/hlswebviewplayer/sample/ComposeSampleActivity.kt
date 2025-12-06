package com.github.yohannestz.hlswebviewplayer.sample

import android.os.Bundle
import android.widget.Toast
import androidx.activity.compose.setContent
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.github.yohannestz.hlswebviewplayer.DefaultHlsPlayerController
import com.github.yohannestz.hlswebviewplayer.HlsWebViewPlayer
import com.github.yohannestz.hlswebviewplayer.LoadStatus
import com.github.yohannestz.hlswebviewplayer.PlayStatus

class ComposeSampleActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MaterialTheme {
                ComposePlayerScreen()
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ComposePlayerScreen() {
    val context = LocalContext.current
    val controller = remember { DefaultHlsPlayerController() }
    val playStatus by controller.playStatus.collectAsState()
    val loadStatus by controller.loadStatus.collectAsState()
    var hlsUrl by remember { mutableStateOf("https://test-streams.mux.dev/x36xhzz/x36xhzz.m3u8") }

    Scaffold(
        topBar = {
            TopAppBar(title = { Text("Jetpack Compose Sample") })
        }
    ) { paddingValues ->
        Column(modifier = Modifier
            .padding(paddingValues)
            .padding(16.dp)) {
            OutlinedTextField(
                value = hlsUrl,
                onValueChange = { hlsUrl = it },
                label = { Text("HLS Stream URL") },
                modifier = Modifier.fillMaxWidth()
            )

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .aspectRatio(16 / 9f)
                    .padding(vertical = 8.dp)
            ) {
                HlsWebViewPlayer(
                    modifier = Modifier.fillMaxSize(),
                    controller = controller
                )

                if (loadStatus is LoadStatus.LOADING) {
                    CircularProgressIndicator(
                        modifier = Modifier.align(Alignment.Center)
                    )
                }
            }

            Button(
                modifier = Modifier.fillMaxWidth(),
                onClick = {
                    val url = hlsUrl.trim()
                    if (url.isEmpty()) {
                        Toast.makeText(context, "Please enter an HLS URL", Toast.LENGTH_SHORT).show()
                    }
                    else {
                        when (playStatus) {
                            PlayStatus.PLAY -> controller.pause()
                            PlayStatus.PAUSE -> controller.resume()
                            else -> controller.play(url)
                        }
                    }
                }) {
                Text(
                    text = when (playStatus) {
                        PlayStatus.PLAY -> "Pause"
                        PlayStatus.PAUSE -> "Resume"
                        else -> "Play"
                    }
                )
            }
        }
    }
}