package com.healthai.app.ml

import android.graphics.Bitmap
import android.util.Log
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.ByteArrayOutputStream
import java.util.concurrent.TimeUnit

/**
 * Hugging Face Skin Lesion Classifier API Client.
 *
 * Model: kokulan123/skin-lesion-classifier (EfficientNet-B2)
 * Classes: akiec, bcc, bkl, df, mel, nv, vasc
 * Input: 224 x 224 RGB image bytes
 */
class HuggingFaceSkinClassifier {

    data class LesionPrediction(
        val code: String,
        val displayName: String,
        val confidence: Float,
        val confidencePercent: String
    )

    private data class HfRawPrediction(
        val label: String,
        val score: Float
    )

    private val client: OkHttpClient = OkHttpClient.Builder()
        .connectTimeout(30, TimeUnit.SECONDS)
        .readTimeout(60, TimeUnit.SECONDS)
        .writeTimeout(60, TimeUnit.SECONDS)
        .build()

    private val gson = Gson()

    /**
     * Resizes the bitmap to 224x224 RGB and converts it to raw binary bytes (JPEG compressed).
     */
    fun preprocessBitmapTo224Bytes(bitmap: Bitmap): ByteArray {
        val resized = Bitmap.createScaledBitmap(bitmap, INPUT_SIZE, INPUT_SIZE, true)
        val stream = ByteArrayOutputStream()
        resized.compress(Bitmap.CompressFormat.JPEG, 95, stream)
        return stream.toByteArray()
    }

    /**
     * Sends the preprocessed binary image to Hugging Face Inference API and returns predictions.
     */
    suspend fun classify(bitmap: Bitmap): Result<List<LesionPrediction>> = withContext(Dispatchers.IO) {
        try {
            val bytes = preprocessBitmapTo224Bytes(bitmap)
            val mediaType = "application/octet-stream".toMediaTypeOrNull()
            val body = bytes.toRequestBody(mediaType)

            val request = Request.Builder()
                .url(API_ENDPOINT)
                .addHeader("Authorization", authHeader)
                .addHeader("Content-Type", "application/octet-stream")
                .post(body)
                .build()

            val response = client.newCall(request).execute()
            val responseBody = response.body?.string().orEmpty()

            if (!response.isSuccessful) {
                Log.e(TAG, "Hugging Face API returned error ${response.code}: $responseBody")
                return@withContext Result.failure(
                    Exception("Hugging Face API error (${response.code}): $responseBody")
                )
            }

            val listType = object : TypeToken<List<HfRawPrediction>>() {}.type
            val rawList: List<HfRawPrediction> = gson.fromJson(responseBody, listType)

            val sorted = rawList.sortedByDescending { it.score }.map { raw ->
                val cleanCode = raw.label.trim().lowercase()
                val fullTitle = CLASS_NAME_MAP[cleanCode] ?: raw.label
                LesionPrediction(
                    code = cleanCode,
                    displayName = fullTitle,
                    confidence = raw.score,
                    confidencePercent = String.format("%.2f%%", raw.score * 100)
                )
            }

            if (sorted.isEmpty()) {
                Result.failure(Exception("Empty predictions received from model."))
            } else {
                Result.success(sorted)
            }
        } catch (e: Exception) {
            Log.e(TAG, "Classification request failed", e)
            Result.failure(e)
        }
    }

    /**
     * Helper to adapt Hugging Face result directly into the app's SkinClassifier.Recognition model.
     */
    suspend fun classifyAsRecognition(bitmap: Bitmap): SkinClassifier.Recognition {
        val result = classify(bitmap).getOrThrow()
        val top = result.first()
        val top3 = result.take(3).map { it.displayName to it.confidence }
        val rawProbs = result.map { it.confidence }

        return SkinClassifier.Recognition(
            label = top.displayName,
            confidence = top.confidence,
            isUncertain = top.confidence < 0.40f,
            top3 = top3,
            rawProbabilities = rawProbs
        )
    }

    companion object {
        private const val TAG = "HFSkinClassifier"
        private const val API_ENDPOINT =
            "https://api-inference.huggingface.co/models/kokulan123/skin-lesion-classifier"
        private val authHeader: String
            get() = "Bearer ${com.healthai.app.BuildConfig.HF_API_KEY}"
        private const val INPUT_SIZE = 224

        val CLASS_NAME_MAP = mapOf(
            "akiec" to "Actinic Keratosis",
            "bcc"   to "Basal Cell Carcinoma",
            "bkl"   to "Benign Keratosis-like Lesion",
            "df"    to "Dermatofibroma",
            "mel"   to "Melanoma",
            "nv"    to "Melanocytic Nevi",
            "vasc"  to "Vascular Lesion"
        )
    }
}
