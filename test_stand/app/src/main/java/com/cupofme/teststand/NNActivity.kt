package com.cupofme.teststand

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Bundle
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import java.util.*

class NNActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_nn)

        val rand = Random()
        val bitmap: Bitmap? = BitmapFactory.decodeStream(assets.open("image${rand.nextInt(3)}.jpg"))

        // showing image on UI
        val imageView = findViewById<ImageView>(R.id.image)
        imageView.setImageBitmap(bitmap)
    }
}
