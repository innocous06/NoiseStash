package com.sounddose.audio

import android.content.Context
import android.util.Log
import com.sounddose.dose.NoiseCategory
import org.tensorflow.lite.Interpreter
import org.tensorflow.lite.nnapi.NnApiDelegate
import java.io.BufferedReader
import java.io.InputStreamReader
import java.nio.MappedByteBuffer
import java.nio.channels.FileChannel

data class ClassificationResult(
    val category: NoiseCategory,
    val rawLabel: String,
    val confidence: Float,
    val isNpuAccelerated: Boolean
)

class AudioClassifier(private val context: Context) {

    companion object {
        private const val TAG = "AudioClassifier"
        private const val MODEL_FILENAME = "yamnet.tflite"
        private const val CLASS_MAP_FILENAME = "yamnet_class_map.csv"
        private const val INPUT_SIZE = 15600
    }

    private var interpreter: Interpreter? = null
    private var nnApiDelegate: NnApiDelegate? = null
    private var classLabels: List<String> = emptyList()
    private var isNpuActive: Boolean = false

    fun initialize() {
        classLabels = loadClassMap()
        try {
            val model = loadModelFile(MODEL_FILENAME)
            val options = Interpreter.Options()

            try {
                // Target Snapdragon NPU via NNAPI Delegate
                val delegate = NnApiDelegate()
                options.addDelegate(delegate)
                nnApiDelegate = delegate
                isNpuActive = true
                Log.i(TAG, "Snapdragon NPU acceleration enabled via NNAPI Delegate")
            } catch (e: Exception) {
                Log.w(TAG, "NNAPI Delegate not available, falling back to CPU: ${e.message}")
                options.setNumThreads(4)
                isNpuActive = false
            }

            interpreter = Interpreter(model, options)
            Log.i(TAG, "YAMNet model successfully loaded")
        } catch (e: Exception) {
            Log.e(TAG, "Failed to load YAMNet model: ${e.message}", e)
        }
    }

    fun classify(samples: FloatArray): ClassificationResult {
        val interp = interpreter ?: return ClassificationResult(
            category = NoiseCategory.AMBIENT,
            rawLabel = "Model initializing",
            confidence = 0f,
            isNpuAccelerated = isNpuActive
        )

        val input = FloatArray(INPUT_SIZE)
        val copyCount = minOf(samples.size, INPUT_SIZE)
        System.arraycopy(samples, 0, input, 0, copyCount)

        // YAMNet expects [1, 15600]
        val inputArray = arrayOf(input)
        val outputMap = HashMap<Int, Any>()
        val outputScores = Array(1) { FloatArray(521) }
        outputMap[0] = outputScores

        try {
            interp.run(inputArray, outputScores)

            val scores = outputScores[0]
            var topIdx = 0
            var topScore = scores[0]

            for (i in 1 until scores.size) {
                if (scores[i] > topScore) {
                    topScore = scores[i]
                    topIdx = i
                }
            }

            val label = if (topIdx < classLabels.size) classLabels[topIdx] else "Ambient sound"
            val category = if (topScore >= 0.20f) {
                NoiseCategory.fromYamnetLabel(label)
            } else {
                NoiseCategory.AMBIENT
            }

            return ClassificationResult(
                category = category,
                rawLabel = label,
                confidence = topScore,
                isNpuAccelerated = isNpuActive
            )
        } catch (e: Exception) {
            Log.e(TAG, "Inference error: ${e.message}")
            return ClassificationResult(
                category = NoiseCategory.AMBIENT,
                rawLabel = "Ambient",
                confidence = 0.5f,
                isNpuAccelerated = isNpuActive
            )
        }
    }

    fun close() {
        try {
            interpreter?.close()
            nnApiDelegate?.close()
        } catch (e: Exception) {
            // Ignored
        }
        interpreter = null
        nnApiDelegate = null
    }

    private fun loadModelFile(filename: String): MappedByteBuffer {
        val assetFd = context.assets.openFd(filename)
        val inputStream = java.io.FileInputStream(assetFd.fileDescriptor)
        val fileChannel = inputStream.channel
        return fileChannel.map(FileChannel.MapMode.READ_ONLY, assetFd.startOffset, assetFd.declaredLength)
    }

    private fun loadClassMap(): List<String> {
        val list = mutableListOf<String>()
        try {
            val stream = context.assets.open(CLASS_MAP_FILENAME)
            val reader = BufferedReader(InputStreamReader(stream))
            reader.readLine() // Header
            reader.forEachLine { line ->
                val parts = line.split(",")
                if (parts.size >= 3) {
                    list.add(parts[2].trim().removeSurrounding("\""))
                }
            }
            reader.close()
        } catch (e: Exception) {
            Log.w(TAG, "Failed reading class map: " + e.message)
        }
        return list
    }
}
