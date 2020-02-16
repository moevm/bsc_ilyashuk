package com.cupofme.teststand

import android.media.AudioFormat
import android.media.AudioRecord
import android.media.MediaRecorder
import android.os.Bundle
import android.os.Handler
import android.os.Process
import android.util.Log
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import kotlin.concurrent.thread
import kotlin.math.log10

const val SAMPLING_RATE = 44100
const val AUDIO_RECORDING = "SEGMENTATION"

class SegmentationActivity : AppCompatActivity() {
    var isRecording = false

    private val shortPauseLength = 100
    private val shortSpeechLength = 50

    private val audioBuffer: ShortArray
    private val countdownRecord: AudioRecord
    private val speechRecord: AudioRecord
    private val countdownVolumesList = mutableListOf<Double>()
    private val speechVolumesList = mutableListOf<Double>()
    private var silenceLevel: Double? = null
    private var isPause = false
    private var speechDuration: Long = 0
    private var pauseStartTime: Long = 0
    private val pausesCount = mutableListOf<Long>()

    init {
        var bufferSize = AudioRecord.getMinBufferSize(
            SAMPLING_RATE, AudioFormat.CHANNEL_IN_MONO, AudioFormat.ENCODING_PCM_16BIT
        )
        if (bufferSize == AudioRecord.ERROR || bufferSize == AudioRecord.ERROR_BAD_VALUE) {
            bufferSize = SAMPLING_RATE * 2
        }

        audioBuffer = ShortArray(bufferSize / 2)

        countdownRecord = initAudioRecording(bufferSize)
        speechRecord = initAudioRecording(bufferSize)
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_segmentation)

        val button = findViewById<Button>(R.id.start_button)



        button.setOnClickListener {
            isRecording = !isRecording
            button.text = if (isRecording) "Stop" else "Start"

            if (isRecording) {
                startRecordSpeechAudio()
            }
        }

        var cond = true
        Handler().postDelayed({
            cond = false
        }, 2000)
        recordCountdownAudio { cond }

    }

    private fun initAudioRecording(bufferSize: Int): AudioRecord {
        val record = AudioRecord(
            MediaRecorder.AudioSource.MIC, SAMPLING_RATE,
            AudioFormat.CHANNEL_IN_MONO, AudioFormat.ENCODING_PCM_16BIT, bufferSize
        )
        if (record.state != AudioRecord.STATE_INITIALIZED) {
            throw Exception("Кажет нет разрешения")
        }

        return record
    }

    private fun recordCountdownAudio(continueCondition: () -> Boolean) {
        thread {
            Process.setThreadPriority(Process.THREAD_PRIORITY_AUDIO)
            countdownVolumesList.clear()
            countdownRecord.startRecording()
            Log.i(AUDIO_RECORDING, "Started countdown audio recording")

            while (continueCondition.invoke()) {
                val shortsRead = countdownRecord.read(audioBuffer, 0, audioBuffer.size)
                countdownVolumesList.add(calculateVolume(shortsRead))
            }

            countdownVolumesList.sort()
            countdownRecord.stop()
            countdownRecord.release()

            val volumeLevels = getVolumeLevels(countdownVolumesList)
            Log.i(AUDIO_RECORDING, "Silence min level: ${volumeLevels.first}")
            Log.i(AUDIO_RECORDING, "Silence max level: ${volumeLevels.second}")
            Log.i(AUDIO_RECORDING, "Silence average level: ${volumeLevels.third}")

            Log.i(AUDIO_RECORDING, "Finished countdown audio recording")
        }
    }

    private fun calculateVolume(shortsRead: Int): Double {
        var v: Long = 0
        for (i in 0 until shortsRead) {
            v += audioBuffer[i] * audioBuffer[i]
        }

        val amplitude = v / shortsRead.toDouble()
        return if (amplitude > 0) 10 * log10(amplitude) else 0.0
    }

    private fun startRecordSpeechAudio() {
        thread {
            Process.setThreadPriority(Process.THREAD_PRIORITY_AUDIO)
            speechRecord.startRecording()
            Log.i(AUDIO_RECORDING, "Started speech audio recording")

            val startTime = System.currentTimeMillis()
            var speechStartTime: Long = 0
            var totalFragmentsRead: Long = 0
            var silentFragmentsRead: Long = 0
            var totalFragmentsOnSlide: Long = 0
            var silentFragmentsOnSlide: Long = 0

            silenceLevel = getAudioVolumeLevel(
                getAverageCountdownVolumeLevel(),
                getMaximalCountdownVolumeLevel()
            )

            while (isRecording) {
                val shortsRead = speechRecord.read(audioBuffer, 0, audioBuffer.size)
                val fragmentVolume = calculateVolume(shortsRead)
                speechVolumesList.add(fragmentVolume)
                if (fragmentVolume < silenceLevel!!) {
                    if (!isPause) {
                        isPause = true
                        if ((System.currentTimeMillis() - speechStartTime) < shortSpeechLength) {
                            // the pause is still continuing, so we remove the pause in the list
                            // and later it will be replaced with the current, longer pause
                            // which has the same start time
                            pausesCount.dropLast(1)
                        } else {
                            pauseStartTime = System.currentTimeMillis()
                        }
                    }
                    silentFragmentsRead++
                    silentFragmentsOnSlide++
                } else if (isPause) {
                    val pauseLength = System.currentTimeMillis() - pauseStartTime
                    if (pauseLength >= shortPauseLength) {
                        pausesCount.add(pauseLength)
                    }
                    isPause = false
                    speechStartTime = System.currentTimeMillis()
                }
                totalFragmentsOnSlide++
                totalFragmentsRead++
            }

            speechDuration = System.currentTimeMillis() - startTime

            speechVolumesList.sort()

            speechRecord.stop()
            speechRecord.release()

            Log.i(AUDIO_RECORDING, "Finished speech audio recording")
        }
    }

    private fun getAudioVolumeLevel(maxLevel: Double, avgLevel: Double): Double =
        (maxLevel + avgLevel) / 2

    private fun getMaximalCountdownVolumeLevel(): Double {
        return getVolumeLevels(countdownVolumesList).second
    }

    private fun getAverageCountdownVolumeLevel(): Double {
        return getVolumeLevels(countdownVolumesList).third
    }

    private fun getVolumeLevels(volumesList: MutableList<Double>): Triple<Double, Double, Double> {
        return if (volumesList.isEmpty()) {
            Triple(0.0, 0.0, 0.0)
        } else {
            Triple(volumesList[0], volumesList.last(), volumesList.average())
        }
    }
}
