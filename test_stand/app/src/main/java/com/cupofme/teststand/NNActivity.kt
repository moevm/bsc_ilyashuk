package com.cupofme.teststand

import android.Manifest
import android.content.pm.PackageManager
import android.content.res.AssetManager
import android.media.AudioFormat
import android.media.AudioRecord
import android.media.MediaRecorder
import android.os.Build
import android.os.Bundle
import android.os.Process
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
import java.util.concurrent.locks.ReentrantLock
import kotlin.math.max


class NNActivity : AppCompatActivity() {
    // Working variables.
    private var recordingBuffer = ShortArray(RECORDING_LENGTH)
    private var recordingOffset = 0
    private var shouldContinue = true
    private var recordingThread: Thread? = null
    private var shouldContinueRecognition = true
    private var recognitionThread: Thread? = null
    private val recordingBufferLock = ReentrantLock()
    private val labels: List<String> = listOf(
        "yes",
        "no",
        "up",
        "down,",
        "left",
        "right",
        "on",
        "off",
        "stop",
        "go"
    )
    private var tfLite: Interpreter? = null

    companion object {
        // Constants that control the behavior of the recognition code and model
        // settings. See the audio recognition tutorial for a detailed explanation of
        // all these, but you should customize them to match your training settings if
        // you are running your own model.
        private const val SAMPLE_RATE = 16000
        private const val SAMPLE_DURATION_MS = 1000
        private const val RECORDING_LENGTH = (SAMPLE_RATE * SAMPLE_DURATION_MS / 1000)
        private const val MINIMUM_TIME_BETWEEN_SAMPLES_MS: Long = 30
        private const val MODEL_FILENAME = "file:///android_asset/emotion_recognition.tflite"

        // UI elements.
        private const val REQUEST_RECORD_AUDIO = 13
        private val LOG_TAG = NNActivity::class.java.simpleName


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

        requestMicrophonePermission()
        startRecording()
        startRecognition()
    }

    private fun requestMicrophonePermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            requestPermissions(
                arrayOf(Manifest.permission.RECORD_AUDIO),
                REQUEST_RECORD_AUDIO
            )
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        if (requestCode == REQUEST_RECORD_AUDIO && grantResults.isNotEmpty() &&
            grantResults[0] == PackageManager.PERMISSION_GRANTED
        ) {
            startRecording()
            startRecognition()
        }
    }

    private fun mfcc(buffer: FloatArray) {
        val sampleRate = 16000
        val bufferSize = 512
        val bufferOverlap = 128
        AndroidFFMPEGLocator(this)
        val mfccList: MutableList<FloatArray> = ArrayList(200)
        val inStream: InputStream = assets.open("audio/03-01-06-02-01-01-09.wav")
        val dispatcher = AudioDispatcher(
            UniversalAudioInputStream(
                inStream,
                TarsosDSPAudioFormat(sampleRate.toFloat(), bufferSize, 1, true, true)
            ), bufferSize, bufferOverlap
        )
        val mfcc = MFCC(bufferSize, sampleRate.toFloat(), 20, 50, 300f, 3000f)
        dispatcher.addAudioProcessor(mfcc)
        dispatcher.addAudioProcessor(object : AudioProcessor {
            override fun processingFinished() {}
            override fun process(audioEvent: AudioEvent): Boolean {
                mfccList.add(mfcc.mfcc)
                return true
            }
        })
        dispatcher.run()
    }

    @Synchronized
    fun startRecording() {
        if (recordingThread != null) {
            return
        }
        shouldContinue = true
        recordingThread = Thread(Runnable { record() })
        recordingThread!!.start()
    }

    private fun record() {
        Process.setThreadPriority(Process.THREAD_PRIORITY_AUDIO)
        // Estimate the buffer size we'll need for this device.
        var bufferSize = AudioRecord.getMinBufferSize(
            SAMPLE_RATE,
            AudioFormat.CHANNEL_IN_MONO,
            AudioFormat.ENCODING_PCM_16BIT
        )
        if (bufferSize == AudioRecord.ERROR || bufferSize == AudioRecord.ERROR_BAD_VALUE) {
            bufferSize = SAMPLE_RATE * 2
        }
        val audioBuffer = ShortArray(bufferSize / 2)
        val record = AudioRecord(
            MediaRecorder.AudioSource.DEFAULT,
            SAMPLE_RATE,
            AudioFormat.CHANNEL_IN_MONO,
            AudioFormat.ENCODING_PCM_16BIT,
            bufferSize
        )
        if (record.state != AudioRecord.STATE_INITIALIZED) {
            Log.e(LOG_TAG, "Audio Record can't initialize!")
            return
        }
        record.startRecording()
        Log.v(LOG_TAG, "Start recording")
        // Loop, gathering audio data and copying it to a round-robin buffer.
        while (shouldContinue) {
            val numberRead = record.read(audioBuffer, 0, audioBuffer.size)
            val maxLength = recordingBuffer.size
            val newRecordingOffset = recordingOffset + numberRead
            val secondCopyLength = max(0, newRecordingOffset - maxLength)
            val firstCopyLength = numberRead - secondCopyLength
            // We store off all the data for the recognition thread to access. The ML
            // thread will copy out of this buffer into its own, while holding the
            // lock, so this should be thread safe.
            recordingBufferLock.lock()
            mfcc(FloatArray(audioBuffer.size) { audioBuffer[it].toFloat() })
            recordingOffset = try {
                System.arraycopy(
                    audioBuffer,
                    0,
                    recordingBuffer,
                    recordingOffset,
                    firstCopyLength
                )
                System.arraycopy(
                    audioBuffer,
                    firstCopyLength,
                    recordingBuffer,
                    0,
                    secondCopyLength
                )
                newRecordingOffset % maxLength
            } finally {
                recordingBufferLock.unlock()
            }
        }
        record.stop()
        record.release()
    }

    @Synchronized
    fun startRecognition() {
        if (recognitionThread != null) {
            return
        }
        shouldContinueRecognition = true
        recognitionThread = Thread(Runnable { recognize() })
        recognitionThread!!.start()
    }

    private fun recognize() {
        val inputBuffer = ShortArray(RECORDING_LENGTH)
        val floatInputBuffer = Array(RECORDING_LENGTH) { FloatArray(1) }
        val outputScores = Array(1) { FloatArray(labels.size) }

        // Loop, grabbing recorded data and running the recognition model on it.
        while (shouldContinueRecognition) { // The recording thread places data in this round-robin buffer, so lock to
            // make sure there's no writing happening and then copy it to our own
            // local version.
            recordingBufferLock.lock()
            try {
                val maxLength = recordingBuffer.size
                val firstCopyLength = maxLength - recordingOffset
                val secondCopyLength = recordingOffset
                System.arraycopy(
                    recordingBuffer,
                    recordingOffset,
                    inputBuffer,
                    0,
                    firstCopyLength
                )
                System.arraycopy(
                    recordingBuffer,
                    0,
                    inputBuffer,
                    firstCopyLength,
                    secondCopyLength
                )
            } finally {
                recordingBufferLock.unlock()
            }
            // We need to feed in float values between -1.0f and 1.0f, so divide the
            // signed 16-bit inputs.
            for (i in 0 until RECORDING_LENGTH) {
                floatInputBuffer[i][0] = inputBuffer[i] / 32767.0f
            }
            val output: MutableMap<Int, Any> = HashMap()
            output[0] = outputScores

            val input = FloatArray(216) { 0.2f }

            // Run the model.
            tfLite?.runForMultipleInputsOutputs(arrayOf(input), output)

            Log.v(LOG_TAG, output.toString())

            try { // We don't need to run too frequently, so snooze for a bit.
                Thread.sleep(MINIMUM_TIME_BETWEEN_SAMPLES_MS)
            } catch (e: InterruptedException) {
            }
        }
    }
}