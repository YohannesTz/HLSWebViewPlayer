package com.github.yohannestz.hlswebviewplayer.sample

import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.ProgressBar
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.github.yohannestz.hlswebviewplayer.HlsPlayerView
import com.github.yohannestz.hlswebviewplayer.LoadStatus
import com.github.yohannestz.hlswebviewplayer.PlayStatus
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach

class XmlSampleActivity : AppCompatActivity() {

    private val defaultHlsUrl = "https://test-streams.mux.dev/x36xhzz/x36xhzz.m3u8"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_xml_sample)
        supportActionBar?.title = "XML Sample"
        findViewById<EditText>(R.id.hlsUrlEditText).setText(defaultHlsUrl)
        setupXmlPlayer()
    }

    private fun setupXmlPlayer() {
        val playerView = findViewById<HlsPlayerView>(R.id.hlsPlayerView)
        val playButton = findViewById<Button>(R.id.playButton)
        val hlsUrlEditText = findViewById<EditText>(R.id.hlsUrlEditText)
        val progressBar = findViewById<ProgressBar>(R.id.progressBar)
        val controller = playerView.controller

        controller.playStatus.onEach { status ->
            playButton.text = when (status) {
                PlayStatus.PLAY -> "Pause"
                PlayStatus.PAUSE -> "Resume"
                else -> "Play"
            }
        }.launchIn(lifecycleScope)

        controller.loadStatus.onEach { status ->
            progressBar.visibility = if (status is LoadStatus.LOADING) View.VISIBLE else View.GONE
        }.launchIn(lifecycleScope)

        playButton.setOnClickListener {
            val hlsUrl = hlsUrlEditText.text.toString().trim()
            if (hlsUrl.isEmpty()) {
                Toast.makeText(this, "Please enter an HLS URL", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            when (controller.playStatus.value) {
                PlayStatus.PLAY -> controller.pause()
                PlayStatus.PAUSE -> controller.resume()
                else -> controller.play(hlsUrl)
            }
        }
    }
}