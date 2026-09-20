package com.healthai.app.ml

import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class SkinPredictionValidatorTest {

    @Test
    fun acceptsAWellSeparatedProbabilityDistribution() {
        val result = SkinPredictionValidator.validate(
            listOf(0.02f, 0.03f, 0.04f, 0.02f, 0.06f, 0.78f, 0.05f)
        )

        assertTrue(result.isReliable)
    }

    @Test
    fun rejectsAnAmbiguousProbabilityDistribution() {
        val result = SkinPredictionValidator.validate(
            listOf(0.36f, 0.34f, 0.08f, 0.05f, 0.06f, 0.06f, 0.05f)
        )

        assertFalse(result.isReliable)
    }

    @Test
    fun rejectsMalformedModelOutput() {
        val result = SkinPredictionValidator.validate(
            listOf(0.4f, 0.4f, 0.4f, 0.4f, 0.4f, 0.4f, 0.4f)
        )

        assertFalse(result.isReliable)
    }
}
