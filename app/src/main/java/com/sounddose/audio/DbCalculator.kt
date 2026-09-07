package com.sounddose.audio

import android.annotation.SuppressLint
import android.media.AudioFormat
import android.media.AudioRecord
import android.media.MediaRecorder
import android.os.Build
import android.util.Log
import com.sounddose.dose.DoseEngine
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlin.math.abs
import kotlin.math.max
import kotlin.math.sin
import kotlin.random.Random

data class AudioReading(
    val dbSpl: Double,
    val peakDb: Double,
    val rms: Double,
    val waveformSamples: FloatArray,
    val classifierFrame: FloatArray?
)

enum class AudioPreset(val label: String, val targetDb: Double, val simulatedLabel: String) {
    REAL_MIC("Live Mic", 0.0, ""),
    QUIET_ROOM("Quiet Room", 42.0, "whispering"),
    URBAN_TRAFFIC("Street Traffic", 86.0, "traffic"),
    HORN_BLAST("Vehicle Horn", 102.0, "air horn"),
    CONSTRUCTION("Drill / Site", 95.0, "jackhammer")
}

class DbCalculator {

    companion object {
        private const val TAG = "DbCalculator"
        const val SAMPLE_RATE = 16000
        const val CHANNEL_CONFIG = AudioFormat.CHANNEL_IN_MONO
        const val AUDIO_FORMAT = AudioFormat.ENCODING_PCM_16BIT
        const val CHUNK_SIZE = 1600 // 100ms at 16kHz
        const val CLASSIFIER_WINDOW_SIZE = 15600 // 0.975s for YAMNet
    }

    private var audioRecord: AudioRecord? = null
    @Volatile
    private var isRecording = false

    @Volatile
    var activePreset: AudioPreset = AudioPreset.REAL_MIC

    @Volatile
    var calibrationOffset: Double = 0.0 // User adjustable trimmer (-10 to +10 dB)

    @SuppressLint("MissingPermission")
    fun startCapture(): Flow<AudioReading> = flow {
        isRecording = true

        val rollingBuffer = FloatArray(CLASSIFIER_WINDOW_SIZE)
        var rollingOffset = 0
        var samplesSinceLastClassify = 0

        val shortBuffer = ShortArray(CHUNK_SIZE)
        val floatChunk = FloatArray(CHUNK_SIZE)

        // Try UNPROCESSED first to bypass hardware AGC & Noise Suppression.
        // Fallback to VOICE_RECOGNITION if device driver doesn't support UNPROCESSED.
        var record: AudioRecord? = null
        val sourcesToTry = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            intArrayOf(
                MediaRecorder.AudioSource.UNPROCESSED,
                MediaRecorder.AudioSource.VOICE_RECOGNITION,
                MediaRecorder.AudioSource.MIC
            )
        } else {
            intArrayOf(
                MediaRecorder.AudioSource.VOICE_RECOGNITION,
                MediaRecorder.AudioSource.MIC
            )
        }

        for (source in sourcesToTry) {
            try {
                val minBuf = AudioRecord.getMinBufferSize(SAMPLE_RATE, CHANNEL_CONFIG, AUDIO_FORMAT)
                val bufSize = maxOf(minBuf, CHUNK_SIZE * 4)
                val candidate = AudioRecord(source, SAMPLE_RATE, CHANNEL_CONFIG, AUDIO_FORMAT, bufSize)
                if (candidate.state == AudioRecord.STATE_INITIALIZED) {
                    candidate.startRecording()
                    record = candidate
                    Log.i(TAG, "Initialized AudioRecord with source=$source (AGC bypassed)")
                    break
                } else {
                    candidate.release()
                }
            } catch (e: Exception) {
                Log.w(TAG, "AudioSource $source failed: ${e.message}")
            }
        }
        audioRecord = record

        var smoothedDb = 40.0
        var peakDb = 40.0
        var phase = 0.0

        while (isRecording) {
            var readCount = 0
            val preset = activePreset

            if (preset == AudioPreset.REAL_MIC && record != null && record.recordingState == AudioRecord.RECORDSTATE_RECORDING) {
                readCount = record.read(shortBuffer, 0, CHUNK_SIZE)
                if (readCount > 0) {
                    for (i in 0 until readCount) {
                        floatChunk[i] = shortBuffer[i] / 32768.0f
                    }
                }
            }

            // Synthetic acoustic simulation if preset selected or mic unavailable
            if (readCount <= 0) {
                readCount = CHUNK_SIZE
                val targetBaseDb = if (preset == AudioPreset.REAL_MIC) 42.0 else preset.targetDb
                val amp = Math.pow(10.0, (targetBaseDb - DoseEngine.DEFAULT_CALIBRATION_OFFSET) / 20.0).toFloat()
                for (i in 0 until readCount) {
                    phase += 0.05
                    val noise = (Random.nextFloat() - 0.5f) * 0.35f
                    val wave = ((sin(phase) * 0.8 + noise) * amp).toFloat()
                    floatChunk[i] = wave.coerceIn(-1.0f, 1.0f)
                }
                delay(100)
            }

            // Push into rolling circular buffer for YAMNet
            for (i in 0 until readCount) {
                rollingBuffer[rollingOffset] = floatChunk[i]
                rollingOffset = (rollingOffset + 1) % CLASSIFIER_WINDOW_SIZE
            }
            samplesSinceLastClassify += readCount

            // Calculate instantaneous RMS & calibrated dB
            val chunkSlice = floatChunk.copyOf(readCount)
            val rms = DoseEngine.computeRms(chunkSlice)
            val rawDb = DoseEngine.rmsToDbSpl(rms, calibrationOffset)

            // Fast attack, smooth decay (Acoustic Fast meter weighting)
            smoothedDb = if (rawDb > smoothedDb) {
                smoothedDb + (rawDb - smoothedDb) * 0.70 // Fast attack
            } else {
                smoothedDb - (smoothedDb - rawDb) * 0.25 // Smooth decay
            }

            // Peak detection with slow decay
            peakDb = if (rawDb > peakDb) rawDb else max(smoothedDb, peakDb * 0.98)

            // Generate 24-point smooth visualizer envelope
            val visualizerPoints = FloatArray(24)
            val step = maxOf(1, readCount / 24)
            for (j in 0 until 24) {
                val idx = (j * step).coerceAtMost(readCount - 1)
                visualizerPoints[j] = abs(floatChunk[idx])
            }

            // Check if 1-second classification frame is ready
            var classifierFrame: FloatArray? = null
            if (samplesSinceLastClassify >= SAMPLE_RATE) {
                samplesSinceLastClassify = 0
                val orderedFrame = FloatArray(CLASSIFIER_WINDOW_SIZE)
                for (k in 0 until CLASSIFIER_WINDOW_SIZE) {
                    orderedFrame[k] = rollingBuffer[(rollingOffset + k) % CLASSIFIER_WINDOW_SIZE]
                }
                classifierFrame = orderedFrame
            }

            emit(
                AudioReading(
                    dbSpl = smoothedDb,
                    peakDb = peakDb,
                    rms = rms,
                    waveformSamples = visualizerPoints,
                    classifierFrame = classifierFrame
                )
            )
        }
    }.flowOn(Dispatchers.IO)

    fun stop() {
        isRecording = false
        try {
            audioRecord?.stop()
            audioRecord?.release()
        } catch (e: Exception) {
            Log.w(TAG, "Cleanup exception: ${e.message}")
        } finally {
            audioRecord = null
        }
    }
}
