package com.cupofme.teststand

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Bundle
import android.util.Log
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import org.pytorch.IValue
import org.pytorch.Module
import org.pytorch.torchvision.TensorImageUtils
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.util.*

class NNActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_nn)
        var bitmap: Bitmap? = null
        var module: Module? = null
        try {
            val rand = Random()
            bitmap = BitmapFactory.decodeStream(assets.open("image${rand.nextInt(3)}.jpg"))

            module = Module.load(assetFilePath(this, "model.pt"))
        } catch (e: IOException) {
            Log.e("Neural network", "Error reading assets", e)
            finish()
        }
        // showing image on UI
        val imageView = findViewById<ImageView>(R.id.image)
        imageView.setImageBitmap(bitmap)
        // preparing input tensor
        val inputTensor = TensorImageUtils.bitmapToFloat32Tensor(
            bitmap,
            TensorImageUtils.TORCHVISION_NORM_MEAN_RGB, TensorImageUtils.TORCHVISION_NORM_STD_RGB
        )
        // running the model
        val outputTensor = module!!.forward(IValue.from(inputTensor)).toTensor()

        // getting tensor content as array of floats
        val scores = outputTensor.dataAsFloatArray

        // searching for the index with maximum score
        var maxScore = -Float.MAX_VALUE
        var maxScoreIdx = -1
        for (i in scores.indices) {
            if (scores[i] > maxScore) {
                maxScore = scores[i]
                maxScoreIdx = i
            }
        }
        val className: String = ImageNetClasses.IMAGENET_CLASSES[maxScoreIdx]
        // showing className on UI
        val textView = findViewById<TextView>(R.id.text)
        textView.text = className
    }

    companion object {
        @Throws(IOException::class)
        fun assetFilePath(
            context: Context,
            assetName: String?
        ): String {
            val file = File(context.filesDir, assetName)
            if (file.exists() && file.length() > 0) {
                return file.absolutePath
            }
            context.assets.open(assetName!!).use { inputStream ->
                FileOutputStream(file).use { os ->
                    val buffer = ByteArray(4 * 1024)
                    var read: Int
                    while (inputStream.read(buffer).also { read = it } != -1) {
                        os.write(buffer, 0, read)
                    }
                    os.flush()
                }
                return file.absolutePath
            }
        }
    }
}
