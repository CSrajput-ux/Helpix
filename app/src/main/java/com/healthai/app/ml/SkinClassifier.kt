package com.healthai.app.ml

/**
 * Shared result type for the server-owned skin classifier.
 *
 * Inference must go through Helpix's authenticated backend. The model author's
 * Space API cannot be called reliably from the APK and doing so would expose a
 * provider token in every installed application.
 */
class SkinClassifier : AutoCloseable {

    data class Recognition(
        val label: String,
        val confidence: Float,
        val isUncertain: Boolean,
        val top3: List<Pair<String, Float>>,
        /** Returned probabilities, ordered from strongest to weakest match. */
        val rawProbabilities: List<Float>
    )

    override fun close() {
        // Result holder only; no local resources to release.
    }

    companion object {
        const val CLASS_COUNT = 7
        const val CONFIDENCE_THRESHOLD = 0.4f
    }
}
