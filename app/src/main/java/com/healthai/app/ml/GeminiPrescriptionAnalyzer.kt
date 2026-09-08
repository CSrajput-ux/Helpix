package com.healthai.app.ml

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import com.google.ai.client.generativeai.GenerativeModel
import com.google.ai.client.generativeai.type.content
import com.google.ai.client.generativeai.type.generationConfig
import com.healthai.app.BuildConfig
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONArray
import org.json.JSONObject
import java.io.File

/**
 * GeminiPrescriptionAnalyzer
 *
 * Sends a prescription image to Gemini Vision API with an expert Medical OCR prompt
 * and returns a fully structured [PrescriptionResult].
 */
object GeminiPrescriptionAnalyzer {

    private const val MODEL_NAME = "gemini-3.7-flash"

    private val EXPERT_PROMPT = """
You are an expert Medical OCR and Prescription Parsing AI Assistant. Your task is to analyze medical prescription images (both handwritten and printed) and extract structured medical details with high accuracy.

### INSTRUCTIONS:
1. **Analyze & Decipher:** Carefully read doctor handwriting, abbreviations (e.g., OD, BD, TDS, SOS, QID, Tab, Cap, Syr), and dosages.
2. **Medical Knowledge Grounding:** If handwriting is unclear, cross-reference possible letter shapes with standard pharmaceutical names (brands/generics), but DO NOT fabricate or hallucinate medicines that are not visually indicated.
3. **Handle Ambiguity:** If a medicine name or dosage is partially illegible or uncertain, provide your best deciphered guess and set "confidence" to "low" or "medium".
4. **Structured Output:** Return strictly a valid JSON object matching the requested schema. Do not wrap in markdown quotes.

### OUTPUT JSON SCHEMA:
{
  "doctor_info": {
    "name": "string or null",
    "specialization": "string or null",
    "clinic_or_hospital": "string or null",
    "phone": "string or null"
  },
  "patient_info": {
    "name": "string or null",
    "age": "string or null",
    "gender": "string or null",
    "date": "YYYY-MM-DD or string or null"
  },
  "diagnosis_or_symptoms": ["list of strings"],
  "medicines": [
    {
      "name": "Medicine Name (Brand/Generic)",
      "type": "Tablet / Capsule / Syrup / Injection / Ointment / Drops / Other",
      "dosage": "e.g., 500mg, 10ml, etc.",
      "frequency": "e.g., 1-0-1, Once Daily (OD), Twice Daily (BD), SOS, etc.",
      "timing": "Before Food / After Food / Empty Stomach / Not Specified",
      "duration": "e.g., 5 days, 1 month, etc.",
      "instructions": "Any specific note like 'with warm water'",
      "confidence": "high / medium / low"
    }
  ],
  "tests_recommended": ["list of lab tests/scans recommended"],
  "dietary_or_general_advice": "string or null",
  "follow_up_date": "string or null",
  "disclaimer": "This is an AI-generated extraction from an image. Please verify with a registered pharmacist or medical professional before consuming any medicine."
}

Now analyze the prescription image and return ONLY the JSON, no extra text.
    """.trimIndent()

    // ─────────────────────────────────────────────────────────────────────────
    // Data models
    // ─────────────────────────────────────────────────────────────────────────

    data class DoctorInfo(
        val name: String?,
        val specialization: String?,
        val clinicOrHospital: String?,
        val phone: String?
    )

    data class PatientInfo(
        val name: String?,
        val age: String?,
        val gender: String?,
        val date: String?
    )

    data class MedicineEntry(
        val name: String,
        val type: String,
        val dosage: String,
        val frequency: String,
        val timing: String,
        val duration: String,
        val instructions: String?,
        val confidence: String   // high / medium / low
    )

    data class PrescriptionResult(
        val doctorInfo: DoctorInfo,
        val patientInfo: PatientInfo,
        val diagnosisOrSymptoms: List<String>,
        val medicines: List<MedicineEntry>,
        val testsRecommended: List<String>,
        val dietaryOrGeneralAdvice: String?,
        val followUpDate: String?,
        val disclaimer: String,
        val rawJson: String       // for debugging
    )

    sealed class AnalyzerResult {
        data class Success(val result: PrescriptionResult) : AnalyzerResult()
        data class Error(val message: String) : AnalyzerResult()
    }

    // ─────────────────────────────────────────────────────────────────────────
    // Main API call
    // ─────────────────────────────────────────────────────────────────────────

    suspend fun analyze(imagePath: String): AnalyzerResult = withContext(Dispatchers.IO) {
        try {
            val apiKey = BuildConfig.GEMINI_API_KEY
            if (apiKey.isBlank()) {
                return@withContext AnalyzerResult.Error(
                    "Gemini API key missing. Add geminiApiKey=YOUR_KEY to local.properties"
                )
            }

            // Load bitmap from file
            val bitmap = BitmapFactory.decodeFile(imagePath)
                ?: return@withContext AnalyzerResult.Error("Could not read image file.")

            // Scale down if needed (Gemini accepts up to ~4MB per image)
            val scaledBitmap = scaleBitmap(bitmap, maxDimension = 1024)

            // Build Gemini model (gemini-1.5-flash: fast + cheap, supports vision)
            val model = GenerativeModel(
                modelName = MODEL_NAME,
                apiKey = apiKey,
                generationConfig = generationConfig {
                    temperature = 0.1f          // Low temp = deterministic medical output
                    maxOutputTokens = 2048
                }
            )

            // Build the request with image + prompt
            val response = model.generateContent(
                content {
                    image(scaledBitmap)
                    text(EXPERT_PROMPT)
                }
            )

            val rawText = response.text
                ?: return@withContext AnalyzerResult.Error("Gemini returned empty response.")

            // Strip markdown code fences if model adds them anyway
            val jsonText = rawText
                .trim()
                .removePrefix("```json")
                .removePrefix("```")
                .removeSuffix("```")
                .trim()

            val result = parseJson(jsonText)
            AnalyzerResult.Success(result)

        } catch (e: Exception) {
            val errorStr = e.toString()
            val userFriendlyMessage = when {
                errorStr.contains("high demand", ignoreCase = true) || errorStr.contains("503") ->
                    "The AI model is currently experiencing high demand. Please try again later."
                errorStr.contains("MissingFieldException") && errorStr.contains("GRpcError") ->
                    "The AI service is temporarily unavailable (High Demand). Please try again later."
                else -> "Gemini API error: ${e.message ?: "Unknown error"}"
            }
            AnalyzerResult.Error(userFriendlyMessage)
        }
    }

    // ─────────────────────────────────────────────────────────────────────────
    // JSON → PrescriptionResult
    // ─────────────────────────────────────────────────────────────────────────

    private fun parseJson(json: String): PrescriptionResult {
        val root = JSONObject(json)

        // Doctor
        val docObj = root.optJSONObject("doctor_info")
        val doctorInfo = DoctorInfo(
            name = docObj?.optString("name").nullIfEmpty(),
            specialization = docObj?.optString("specialization").nullIfEmpty(),
            clinicOrHospital = docObj?.optString("clinic_or_hospital").nullIfEmpty(),
            phone = docObj?.optString("phone").nullIfEmpty()
        )

        // Patient
        val patObj = root.optJSONObject("patient_info")
        val patientInfo = PatientInfo(
            name = patObj?.optString("name").nullIfEmpty(),
            age = patObj?.optString("age").nullIfEmpty(),
            gender = patObj?.optString("gender").nullIfEmpty(),
            date = patObj?.optString("date").nullIfEmpty()
        )

        // Diagnosis
        val diagArr = root.optJSONArray("diagnosis_or_symptoms")
        val diagnosis = diagArr?.toStringList() ?: emptyList()

        // Medicines
        val medsArr = root.optJSONArray("medicines")
        val medicines = mutableListOf<MedicineEntry>()
        if (medsArr != null) {
            for (i in 0 until medsArr.length()) {
                val m = medsArr.getJSONObject(i)
                medicines.add(
                    MedicineEntry(
                        name = m.optString("name", "Unknown"),
                        type = m.optString("type", "Other"),
                        dosage = m.optString("dosage", "—"),
                        frequency = m.optString("frequency", "—"),
                        timing = m.optString("timing", "Not Specified"),
                        duration = m.optString("duration", "—"),
                        instructions = m.optString("instructions").nullIfEmpty(),
                        confidence = m.optString("confidence", "medium")
                    )
                )
            }
        }

        // Tests
        val testsArr = root.optJSONArray("tests_recommended")
        val tests = testsArr?.toStringList() ?: emptyList()

        return PrescriptionResult(
            doctorInfo = doctorInfo,
            patientInfo = patientInfo,
            diagnosisOrSymptoms = diagnosis,
            medicines = medicines,
            testsRecommended = tests,
            dietaryOrGeneralAdvice = root.optString("dietary_or_general_advice").nullIfEmpty(),
            followUpDate = root.optString("follow_up_date").nullIfEmpty(),
            disclaimer = root.optString(
                "disclaimer",
                "This is an AI-generated extraction. Please verify with a registered pharmacist."
            ),
            rawJson = json
        )
    }

    // ─────────────────────────────────────────────────────────────────────────
    // Helpers
    // ─────────────────────────────────────────────────────────────────────────

    private fun scaleBitmap(bitmap: Bitmap, maxDimension: Int): Bitmap {
        val w = bitmap.width
        val h = bitmap.height
        if (w <= maxDimension && h <= maxDimension) return bitmap
        val scale = maxDimension.toFloat() / maxOf(w, h)
        return Bitmap.createScaledBitmap(bitmap, (w * scale).toInt(), (h * scale).toInt(), true)
    }

    private fun String?.nullIfEmpty(): String? =
        if (isNullOrBlank() || this == "null") null else this

    private fun JSONArray.toStringList(): List<String> {
        val list = mutableListOf<String>()
        for (i in 0 until length()) {
            val s = optString(i)
            if (s.isNotBlank()) list.add(s)
        }
        return list
    }
}
