package com.healthai.app.ml

import android.content.Context
import android.graphics.Bitmap
import android.util.Log
import org.tensorflow.lite.Interpreter
import java.io.FileInputStream
import java.nio.MappedByteBuffer
import java.nio.channels.FileChannel

class SkinClassifier(private val context: Context) {

    private var interpreter: Interpreter? = null
    
    // Updated to match model's ACTUAL 6 output classes from logcat
    private val labels = listOf(
        "Acne",
        "Eczema",
        "Psoriasis",
        "Tinea/Ringworm",
        "Scabies",
        "Normal Skin"
    )

    data class Recognition(
        val label: String,
        val confidence: Float
    )

    companion object {
        private const val TAG = "SkinClassifier"
        private const val MODEL_PATH = "model.tflite"
        private const val INPUT_SIZE = 180
    }

    init {
        try {
            interpreter = Interpreter(loadModelFile())
            Log.d(TAG, "Model loaded. Output shape: ${interpreter?.getOutputTensor(0)?.shape()?.contentToString()}")
        } catch (e: Exception) {
            Log.e(TAG, "Initialization error: ${e.message}")
        }
    }

    private fun loadModelFile(): MappedByteBuffer {
        val fileDescriptor = context.assets.openFd(MODEL_PATH)
        val inputStream = FileInputStream(fileDescriptor.fileDescriptor)
        val fileChannel = inputStream.channel
        return fileChannel.map(FileChannel.MapMode.READ_ONLY, fileDescriptor.startOffset, fileDescriptor.declaredLength)
    }

    fun classifySkin(bitmap: Bitmap): Recognition {
        val currentInterpreter = interpreter ?: return Recognition("Model Error", 0f)

        return try {
            // 1. Preprocess: Resize to 180x180
            val scaledBitmap = Bitmap.createScaledBitmap(bitmap, INPUT_SIZE, INPUT_SIZE, true)
            
            // 2. Prepare 4D Input Array [1, 180, 180, 3] and Normalize /255.0f
            val input = Array(1) { Array(INPUT_SIZE) { Array(INPUT_SIZE) { FloatArray(3) } } }
            for (y in 0 until INPUT_SIZE) {
                for (x in 0 until INPUT_SIZE) {
                    val pixel = scaledBitmap.getPixel(x, y)
                    input[0][y][x][0] = ((pixel shr 16) and 0xFF) / 255.0f
                    input[0][y][x][1] = ((pixel shr 8) and 0xFF) / 255.0f
                    input[0][y][x][2] = (pixel and 0xFF) / 255.0f
                }
            }

            // 3. Prepare Output Array (MUST be [1, 6] to match model)
            val output = Array(1) { FloatArray(6) }

            // 4. Run Inference
            currentInterpreter.run(input, output)

            // 5. Get Top Result
            val confidences = output[0]
            var maxIdx = 0
            var maxConf = -1f
            for (i in confidences.indices) {
                if (confidences[i] > maxConf) {
                    maxConf = confidences[i]
                    maxIdx = i
                }
            }

            Recognition(
                label = if (maxIdx < labels.size) labels[maxIdx] else "Unknown ($maxIdx)",
                confidence = maxConf
            )

        } catch (e: Exception) {
            Log.e(TAG, "Inference error: ${e.message}")
            Recognition("Inference Error", 0f)
        }
    }

    fun close() {
        interpreter?.close()
        interpreter = null
    }
}
