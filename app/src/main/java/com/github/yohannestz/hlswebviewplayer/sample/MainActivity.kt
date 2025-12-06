package com.github.yohannestz.hlswebviewplayer.sample

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        findViewById<Button>(R.id.xmlSampleButton).setOnClickListener {
            startActivity(Intent(this, XmlSampleActivity::class.java))
        }

        findViewById<Button>(R.id.composeSampleButton).setOnClickListener {
            startActivity(Intent(this, ComposeSampleActivity::class.java))
        }
    }
}