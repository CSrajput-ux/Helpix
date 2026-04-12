package com.healthai.app.ml

import android.content.Context
import android.graphics.Bitmap
import android.util.Log
import org.tensorflow.lite.Interpreter
import java.io.FileInputStream
import java.nio.ByteBuffer
import java.nio.ByteOrder
import java.nio.channels.FileChannel

class SkinClassifier(private val context: Context) {

    private var interpreter: Interpreter? = null

    data class Recognition(
        val label: String,
        val confidence: Float
    )

    companion object {
        private const val TAG = "SkinClassifier"
        private const val MODEL_PATH = "model.tflite"
        private const val INPUT_SIZE = 180
        private const val PIXEL_SIZE = 3 // RGB
        
        private val LABELS = listOf(
            "Herpes HPV and other STDs",
            "Warts and Viral Infections",
            "Lupus and Connective Tissue diseases",
            "Systemic Disease",
            "Scabies and Bites",
            "Vasculitis",
            "Vascular Tumors",
            "Urticaria Hives",
            "Hair Loss Photos",
            "Seborrheic Keratoses",
            "Nail Fungus",
            "Melanoma and Skin Cancer",
            "Exanthems and Drug Eruptions",
            "Pigmentation Disorders",
            "Poison Ivy and Contact Dermatitis",
            "Tinea and Fungal Infections",
            "Cellulitis and Bacterial Infections",
            "Psoriasis and Lichen Planus",
            "Eczema",
            "Malignant Lesions",
            "Bullous Disease",
            "Atopic Dermatitis",
            "Acne and Rosacea"
        )
    }

    init {
        loadModel()
    }

    private fun loadModel() {
        try {
            val assetFileDescriptor = context.assets.openFd(MODEL_PATH)
            val inputStream = FileInputStream(assetFileDescriptor.fileDescriptor)
            val fileChannel = inputStream.channel
            val startOffset = assetFileDescriptor.startOffset
            val declaredLength = assetFileDescriptor.declaredLength
            val modelBuffer = fileChannel.map(FileChannel.MapMode.READ_ONLY, startOffset, declaredLength)
            
            interpreter = Interpreter(modelBuffer)
            Log.d(TAG, "Model loaded successfully from $MODEL_PATH")
        } catch (e: Exception) {
            Log.e(TAG, "Model loading error: ${e.message}")
        }
    }

    fun classifySkin(bitmap: Bitmap): Recognition {
        val currentInterpreter = interpreter ?: return Recognition("Initialization Error", 0f)

        try {
            // Resize and Preprocess
            val scaledBitmap = Bitmap.createScaledBitmap(bitmap, INPUT_SIZE, INPUT_SIZE, true)
            val inputBuffer = convertBitmapToByteBuffer(scaledBitmap)

            // Output buffer for 23 classes
            val output = Array(1) { FloatArray(LABELS.size) }

            currentInterpreter.run(inputBuffer, output)

            return getTopResult(output[0])

        } catch (e: Exception) {
            Log.e(TAG, "Inference error: ${e.message}")
            return Recognition("Inference Error", 0f)
        }
    }

    private fun convertBitmapToByteBuffer(bitmap: Bitmap): ByteBuffer {
        val byteBuffer = ByteBuffer.allocateDirect(4 * INPUT_SIZE * INPUT_SIZE * PIXEL_SIZE)
        byteBuffer.order(ByteOrder.nativeOrder())
        val intValues = IntArray(INPUT_SIZE * INPUT_SIZE)
        bitmap.getPixels(intValues, 0, bitmap.width, 0, 0, bitmap.width, bitmap.height)

        var pixel = 0
        for (i in 0 until INPUT_SIZE) {
            for (j in 0 until INPUT_SIZE) {
                val value = intValues[pixel++]
                // Normalize to [0, 1] by dividing by 255.0f
                byteBuffer.putFloat(((value shr 16) and 0xFF) / 255.0f)
                byteBuffer.putFloat(((value shr 8) and 0xFF) / 255.0f)
                byteBuffer.putFloat((value and 0xFF) / 255.0f)
            }
        }
        return byteBuffer
    }

    private fun getTopResult(confidences: FloatArray): Recognition {
        var maxIdx = 0
        var maxConf = -1f

        for (i in confidences.indices) {
            if (confidences[i] > maxConf) {
                maxConf = confidences[i]
                maxIdx = i
            }
        }

        return if (maxConf < 0.6f) {
            Recognition("Not sure, try again", maxConf)
        } else {
            Recognition(
                label = if (maxIdx < LABELS.size) LABELS[maxIdx] else "Unknown",
                confidence = maxConf
            )
        }
    }

    fun close() {
        interpreter?.close()
        interpreter = null
    }
}
