package com.healthai.app.ml

import android.content.Context
import android.graphics.Bitmap

/**
 * Skin Classifier powered by Hugging Face Image Classification API.
 * (Offline TFLite model removed in favor of Cloud API).
 */
class SkinClassifier(context: Context? = null) : AutoCloseable {

    data class Recognition(
        val label: String,
        val confidence: Float,
        val isUncertain: Boolean,
        val top3: List<Pair<String, Float>>,
        /** All seven probabilities, in model-output order. */
        val rawProbabilities: List<Float>
    )

    private val hfClassifier = HuggingFaceSkinClassifier()

    suspend fun classifySkin(bitmap: Bitmap): Recognition {
        return hfClassifier.classifyAsRecognition(bitmap)
    }

    override fun close() {
        // No local resources to release
    }

    companion object {
        const val CLASS_COUNT = 7
        const val CONFIDENCE_THRESHOLD = 0.4f
    }
}
