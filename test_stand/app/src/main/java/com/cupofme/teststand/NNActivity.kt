package com.cupofme.teststand

import android.content.res.AssetManager
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import be.tarsos.dsp.AudioDispatcher
import be.tarsos.dsp.AudioEvent
import be.tarsos.dsp.AudioProcessor
import be.tarsos.dsp.io.TarsosDSPAudioFormat
import be.tarsos.dsp.io.UniversalAudioInputStream
import be.tarsos.dsp.io.android.AndroidFFMPEGLocator
import be.tarsos.dsp.mfcc.MFCC
import org.tensorflow.lite.Interpreter
import java.io.FileInputStream
import java.io.IOException
import java.io.InputStream
import java.nio.MappedByteBuffer
import java.nio.channels.FileChannel
import java.util.*


class NNActivity : AppCompatActivity() {
    private val labels = listOf(
        "female_angry",
        "female_calm",
        "female_fearful",
        "female_happy",
        "female_sad",
        "male_angry",
        "male_calm",
        "male_fearful",
        "male_happy",
        "male_sad"
    )
    private var tfLite: Interpreter? = null

    companion object {
        private const val MODEL_FILENAME = "file:///android_asset/emotion_recognition.tflite"

        @Throws(IOException::class)
        private fun loadModelFile(assets: AssetManager, modelFilename: String): MappedByteBuffer {
            val fileDescriptor = assets.openFd(modelFilename)
            val inputStream = FileInputStream(fileDescriptor.fileDescriptor)
            val fileChannel = inputStream.channel
            val startOffset = fileDescriptor.startOffset
            val declaredLength = fileDescriptor.declaredLength
            return fileChannel.map(
                FileChannel.MapMode.READ_ONLY,
                startOffset,
                declaredLength
            )
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_nn)

        val actualModelFilename =
            MODEL_FILENAME.split("file:///android_asset/")
                .dropLastWhile { it.isEmpty() }.toTypedArray()[1]
        tfLite = try {
            Interpreter(
                loadModelFile(
                    assets,
                    actualModelFilename
                )
            )
        } catch (e: Exception) {
            throw RuntimeException(e)
        }

        val mfccs = mfcc("audio/03-01-06-02-01-01-09.wav")

        mfccs.forEach { mfcc ->
            val outputScores = arrayOf(FloatArray(labels.size))
            val output: MutableMap<Int, Any> = HashMap()
            output[0] = outputScores
            tfLite?.runForMultipleInputsOutputs(arrayOf(mfcc), output)
            val array = output[0] as Array<FloatArray>
            array.forEach {
                Log.d("EMOTIONS", labels[it.indexOf(it.max()!!)])
            }
        }
    }

    private fun mfcc(assetName: String): List<FloatArray> {
        val sampleRate = 16000f
        val bufferSize = 512
        val bufferOverlap = 128
        AndroidFFMPEGLocator(this)
        val mfccList: MutableList<FloatArray> = ArrayList(200)
        val inStream: InputStream = assets.open(assetName)
        val dispatcher = AudioDispatcher(
            UniversalAudioInputStream(
                inStream,
                TarsosDSPAudioFormat(sampleRate, bufferSize, 1, true, true)
            ), bufferSize, bufferOverlap
        )
        val mfcc = MFCC(bufferSize, sampleRate, 216, 50, 300f, 3000f)
        dispatcher.addAudioProcessor(mfcc)
        dispatcher.addAudioProcessor(object : AudioProcessor {
            override fun processingFinished() {}
            override fun process(audioEvent: AudioEvent): Boolean {
                mfccList.add(mfcc.mfcc)
                return true
            }
        })
        dispatcher.run()
        return mfccList
    }
}