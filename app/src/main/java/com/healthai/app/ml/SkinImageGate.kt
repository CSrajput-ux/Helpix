package com.healthai.app.ml

import android.graphics.Bitmap
import android.graphics.Color

/**
 * SkinImageGate — Lightweight pixel-level skin color detector
 *
 * IMAGE → Gate Check → NOT SKIN (reject) / SKIN (pass to TFLite)
 *
 * Uses 3 well-known skin detection rules from published research:
 *  1. Kovac RGB rule
 *  2. HSV range rule
 *  3. Normalized RGB rule (Chai & Ngan)
 *
 * At least 12% of sampled pixels must match skin rules to "pass".
 */
object SkinImageGate {

    /** Minimum fraction of pixels that must look like skin */
    private const val SKIN_PIXEL_RATIO_THRESHOLD = 0.12f

    /** Downscale to this for fast analysis (no need for full-res) */
    private const val SAMPLE_SIZE = 100

    data class GateResult(
        val passed: Boolean,
        val skinRatio: Float,       // 0.0 – 1.0
        val reason: String          // Human-readable explanation
    )

    /**
     * Main entry point. Call on Dispatchers.Default.
     * Returns [GateResult.passed] = true if image looks like skin.
     */
    fun check(bitmap: Bitmap): GateResult {
        val sample = downsample(bitmap)
        val total = sample.width * sample.height
        var skinCount = 0

        for (x in 0 until sample.width) {
            for (y in 0 until sample.height) {
                val pixel = sample.getPixel(x, y)
                val r = Color.red(pixel)
                val g = Color.green(pixel)
                val b = Color.blue(pixel)
                if (isSkinPixel(r, g, b)) skinCount++
            }
        }

        // Cleanup sample if it was newly created
        if (sample !== bitmap) sample.recycle()

        val ratio = skinCount.toFloat() / total
        val passed = ratio >= SKIN_PIXEL_RATIO_THRESHOLD

        return GateResult(
            passed = passed,
            skinRatio = ratio,
            reason = if (passed)
                "Image contains ${(ratio * 100).toInt()}% skin-like pixels — Gate PASSED ✅"
            else
                "Only ${(ratio * 100).toInt()}% skin-like pixels found (need ≥${(SKIN_PIXEL_RATIO_THRESHOLD * 100).toInt()}%) — Gate REJECTED ❌"
        )
    }

    // ─────────────────────────────────────────────────────────────────────────
    // Private helpers
    // ─────────────────────────────────────────────────────────────────────────

    private fun downsample(bitmap: Bitmap): Bitmap {
        return if (bitmap.width > SAMPLE_SIZE || bitmap.height > SAMPLE_SIZE) {
            Bitmap.createScaledBitmap(bitmap, SAMPLE_SIZE, SAMPLE_SIZE, false)
        } else {
            bitmap
        }
    }

    /**
     * Returns true if (r, g, b) pixel matches ANY of the 3 skin rules.
     */
    private fun isSkinPixel(r: Int, g: Int, b: Int): Boolean {
        // ── Rule 1: Kovac et al. RGB skin rule ────────────────────────────────
        val maxVal = maxOf(r, g, b)
        val minVal = minOf(r, g, b)
        val kovac = r > 95 && g > 40 && b > 20 &&
                (maxVal - minVal) > 15 &&
                kotlin.math.abs(r - g) > 15 &&
                r > g && r > b

        // ── Rule 2: HSV range skin rule ───────────────────────────────────────
        val hsv = FloatArray(3)
        android.graphics.Color.RGBToHSV(r, g, b, hsv)
        val h = hsv[0]
        val s = hsv[1]
        val v = hsv[2]
        val hsvRule = h in 0f..50f && s in 0.15f..0.90f && v in 0.20f..1.0f

        // ── Rule 3: Normalized RGB (Chai & Ngan) ──────────────────────────────
        val sum = (r + g + b).coerceAtLeast(1)
        val rn = r.toFloat() / sum
        val gn = g.toFloat() / sum
        val chaiNgan = rn in 0.35f..0.75f && gn in 0.20f..0.45f

        // Must match Rule 1 OR (Rule 2 AND Rule 3) — reduces false positives
        return kovac || (hsvRule && chaiNgan)
    }
}
