package com.healthai.app.ml

/**
 * Sanity checks for the seven-class probability vector emitted by the offline
 * skin-lesion model. A classifier must never be presented as a diagnosis when
 * its output is malformed or too ambiguous to distinguish its top classes.
 */
internal object SkinPredictionValidator {
    const val CLASS_COUNT = 7
    private const val MIN_TOP_CONFIDENCE = 0.60f
    private const val MIN_TOP_MARGIN = 0.15f
    private const val PROBABILITY_SUM_TOLERANCE = 0.02f

    data class Validation(
        val isReliable: Boolean,
        val reason: String? = null
    )

    fun validate(probabilities: List<Float>): Validation {
        if (probabilities.size != CLASS_COUNT) {
            return Validation(false, "The model returned an unexpected number of classes.")
        }
        if (probabilities.any { !it.isFinite() || it < 0f || it > 1f }) {
            return Validation(false, "The model returned invalid probability values.")
        }

        val sum = probabilities.sum()
        if (kotlin.math.abs(sum - 1f) > PROBABILITY_SUM_TOLERANCE) {
            return Validation(false, "The model output was not a probability distribution.")
        }

        val sorted = probabilities.sortedDescending()
        if (sorted[0] < MIN_TOP_CONFIDENCE) {
            return Validation(false, "No skin-lesion class was a confident match.")
        }
        if (sorted[0] - sorted[1] < MIN_TOP_MARGIN) {
            return Validation(false, "The two most likely skin-lesion classes were too close to distinguish.")
        }
        return Validation(true)
    }
}
