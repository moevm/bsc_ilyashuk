package com.cupofme.teststand

import android.Manifest
import android.content.pm.PackageManager
import android.content.res.AssetManager
import android.media.AudioFormat
import android.media.AudioRecord
import android.media.MediaRecorder
import android.os.*
import android.util.Log
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import org.tensorflow.lite.Interpreter
import java.io.FileInputStream
import java.io.IOException
import java.nio.MappedByteBuffer
import java.nio.channels.FileChannel
import java.util.*
import java.util.concurrent.locks.ReentrantLock
import kotlin.math.max
import kotlin.math.roundToInt

/**
 * An activity that listens for audio and then uses a TensorFlow model to detect particular classes,
 * by default a small set of action words.
 */
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
        "_silence_",
        "_unknown_",
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
    private var recognizeCommands: RecognizeCommands? = null
    private var tfLite: Interpreter? = null
    private var yesTextView: TextView? = null
    private var noTextView: TextView? = null
    private var upTextView: TextView? = null
    private var downTextView: TextView? = null
    private var leftTextView: TextView? = null
    private var rightTextView: TextView? = null
    private var onTextView: TextView? = null
    private var offTextView: TextView? = null
    private var stopTextView: TextView? = null
    private var goTextView: TextView? = null
    private val handler = Handler()
    private var selectedTextView: TextView? = null
    private var backgroundThread: HandlerThread? = null
    private var backgroundHandler: Handler? = null

    companion object {
        // Constants that control the behavior of the recognition code and model
        // settings. See the audio recognition tutorial for a detailed explanation of
        // all these, but you should customize them to match your training settings if
        // you are running your own model.
        private const val SAMPLE_RATE = 16000
        private const val SAMPLE_DURATION_MS = 1000
        private const val RECORDING_LENGTH = (SAMPLE_RATE * SAMPLE_DURATION_MS / 1000)
        private const val AVERAGE_WINDOW_DURATION_MS: Long = 1000
        private const val DETECTION_THRESHOLD = 0.50f
        private const val SUPPRESSION_MS = 1500
        private const val MINIMUM_COUNT = 3
        private const val MINIMUM_TIME_BETWEEN_SAMPLES_MS: Long = 30
        private const val MODEL_FILENAME = "file:///android_asset/conv_actions_frozen.tflite"
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

        private const val HANDLE_THREAD_NAME = "CameraBackground"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_nn)
        // Set up an object to smooth recognition results to increase accuracy.
        recognizeCommands = RecognizeCommands(
            labels,
            AVERAGE_WINDOW_DURATION_MS,
            DETECTION_THRESHOLD,
            SUPPRESSION_MS,
            MINIMUM_COUNT,
            MINIMUM_TIME_BETWEEN_SAMPLES_MS
        )
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
        tfLite!!.resizeInput(0, intArrayOf(RECORDING_LENGTH, 1))
        tfLite!!.resizeInput(1, intArrayOf(1))
        // Start the recording and recognition threads.
        requestMicrophonePermission()
        startRecording()
        startRecognition()
        yesTextView = findViewById(R.id.yes)
        noTextView = findViewById(R.id.no)
        upTextView = findViewById(R.id.up)
        downTextView = findViewById(R.id.down)
        leftTextView = findViewById(R.id.left)
        rightTextView = findViewById(R.id.right)
        onTextView = findViewById(R.id.on)
        offTextView = findViewById(R.id.off)
        stopTextView = findViewById(R.id.stop)
        goTextView = findViewById(R.id.go)
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
        if (requestCode == REQUEST_RECORD_AUDIO && grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED
        ) {
            startRecording()
            startRecognition()
        }
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
        Log.v(LOG_TAG, "Start recognition")
        val inputBuffer = ShortArray(RECORDING_LENGTH)
        val floatInputBuffer = Array(RECORDING_LENGTH) { FloatArray(1) }
        val outputScores = Array(1) { FloatArray(labels.size) }
        val sampleRateList = intArrayOf(SAMPLE_RATE)
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
            val inputArray = arrayOf(floatInputBuffer, sampleRateList)
            val outputMap: MutableMap<Int, Any> = HashMap()
            outputMap[0] = outputScores

            // Run the model.
            tfLite!!.runForMultipleInputsOutputs(inputArray, outputMap)

            // Use the smoother to figure out if we've had a real recognition event.
            val currentTime = System.currentTimeMillis()
            val result = recognizeCommands!!.processLatestResults(outputScores[0], currentTime)
            runOnUiThread {
                // If we do have a new command, highlight the right list entry.
                if (!result.foundCommand.startsWith("_") && result.isNewCommand) {
                    var labelIndex = -1
                    for (i in labels.indices) {
                        if (labels[i] == result.foundCommand) {
                            labelIndex = i
                        }
                    }
                    when (labelIndex - 2) {
                        0 -> selectedTextView = yesTextView
                        1 -> selectedTextView = noTextView
                        2 -> selectedTextView = upTextView
                        3 -> selectedTextView = downTextView
                        4 -> selectedTextView = leftTextView
                        5 -> selectedTextView = rightTextView
                        6 -> selectedTextView = onTextView
                        7 -> selectedTextView = offTextView
                        8 -> selectedTextView = stopTextView
                        9 -> selectedTextView = goTextView
                    }
                    if (selectedTextView != null) {
                        selectedTextView!!.setBackgroundResource(R.drawable.round_corner_text_bg_selected)
                        val score = (result.score * 100).roundToInt().toString() + "%"
                        selectedTextView!!.text = selectedTextView!!.text.toString() + "\n" + score
                        selectedTextView!!.setTextColor(
                            resources.getColor(android.R.color.holo_orange_light)
                        )
                        handler.postDelayed({
                            val originalString =
                                selectedTextView!!.text.toString().replace(score, "")
                                    .trim { it <= ' ' }
                            selectedTextView!!.text = originalString
                            selectedTextView!!.setBackgroundResource(R.drawable.round_corner_text_bg_unselected)
                            selectedTextView!!.setTextColor(resources.getColor(android.R.color.darker_gray))
                        }, 750)
                    }
                }
            }
            try { // We don't need to run too frequently, so snooze for a bit.
                Thread.sleep(MINIMUM_TIME_BETWEEN_SAMPLES_MS)
            } catch (e: InterruptedException) {
            }
        }
        Log.v(LOG_TAG, "End recognition")
    }

    private fun startBackgroundThread() {
        backgroundThread = HandlerThread(HANDLE_THREAD_NAME)
        backgroundThread!!.start()
        backgroundHandler = Handler(backgroundThread!!.looper)
    }

    private fun stopBackgroundThread() {
        backgroundThread!!.quitSafely()
        try {
            backgroundThread!!.join()
            backgroundThread = null
            backgroundHandler = null
        } catch (e: InterruptedException) {
            Log.e("amlan", "Interrupted when stopping background thread", e)
        }
    }

    override fun onResume() {
        super.onResume()
        startBackgroundThread()
    }

    override fun onStop() {
        super.onStop()
        stopBackgroundThread()
    }
}