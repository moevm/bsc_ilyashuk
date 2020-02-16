package com.cupofme.teststand

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat


class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        if (ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.RECORD_AUDIO
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.RECORD_AUDIO), 1)
        }

        findViewById<Button>(R.id.list_button).setOnClickListener {
            startActivity(Intent(this, AudioListActivity::class.java))
        }


        findViewById<Button>(R.id.nn_button).setOnClickListener {
            startActivity(Intent(this, NNActivity::class.java))
        }

        findViewById<Button>(R.id.segmentation_button).setOnClickListener {
            startActivity(Intent(this, SegmentationActivity::class.java))
        }
    }

}
