package com.healthai.app.ui.screens.skin

import androidx.lifecycle.ViewModel
import com.healthai.app.data.remote.api.HelpixRepository
import com.healthai.app.ml.SkinClassifier
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File
import javax.inject.Inject

/**
 * Sends skin photos through the Helpix backend, rather than calling a third
 * party from the APK. This keeps API credentials out of the app and makes the
 * displayed result, server record, and model source the same thing.
 */
@HiltViewModel
class SkinScanViewModel @Inject constructor(
    private val repository: HelpixRepository
) : ViewModel() {

    suspend fun analyze(imagePath: String): SkinClassifier.Recognition = withContext(Dispatchers.IO) {
        val image = File(imagePath)
        check(image.isFile && image.length() > 0L) { "Selected image is unavailable." }

        val response = repository.scanSkin(image)
        val result = response.body()
        if (!response.isSuccessful || result == null) {
            throw IllegalStateException(
                "Skin analysis is unavailable right now. Please try again in a moment."
            )
        }

        val confidence = result.confidence.toFloat().coerceIn(0f, 1f)
        val topPredictions = result.top_predictions.orEmpty()
            .sortedByDescending { it.confidence }
            .take(3)
            .map { it.label to it.confidence.toFloat().coerceIn(0f, 1f) }

        SkinClassifier.Recognition(
            label = result.detected_condition,
            confidence = confidence,
            isUncertain = confidence < SkinClassifier.CONFIDENCE_THRESHOLD,
            top3 = topPredictions.ifEmpty { listOf(result.detected_condition to confidence) },
            rawProbabilities = topPredictions.map { it.second }
        )
    }
}
