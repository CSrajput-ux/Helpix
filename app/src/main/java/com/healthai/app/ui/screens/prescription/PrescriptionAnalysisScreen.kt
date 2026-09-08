package com.healthai.app.ui.screens.prescription

import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.healthai.app.ml.GeminiPrescriptionAnalyzer
import com.healthai.app.ui.navigation.NavRoutes
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

/**
 * PrescriptionAnalysisScreen
 *
 * - Shows animated spinner while Gemini API analyzes the prescription image
 * - On success → navigates to PrescriptionResultScreen with the result
 * - On failure → navigates to PrescriptionResultScreen with error flag
 *
 * imagePath: absolute path of the captured/selected prescription image
 */
@Composable
fun PrescriptionAnalysisScreen(
    navController: NavController,
    imagePath: String?
) {
    val context = LocalContext.current
    var statusText by remember { mutableStateOf("Reading prescription...") }

    LaunchedEffect(imagePath) {
        if (imagePath.isNullOrBlank()) {
            navController.navigate(NavRoutes.PrescriptionResult + "?error=No+image+selected") {
                popUpTo(NavRoutes.PrescriptionReader) { inclusive = true }
            }
            return@LaunchedEffect
        }

        // Phase 1: OCR hint
        statusText = "Sending to Gemini Vision AI..."

        val result = withContext(Dispatchers.IO) {
            GeminiPrescriptionAnalyzer.analyze(imagePath)
        }

        when (result) {
            is GeminiPrescriptionAnalyzer.AnalyzerResult.Success -> {
                // Store result in companion object for retrieval on result screen
                PrescriptionResultHolder.result = result.result
                PrescriptionResultHolder.imagePath = imagePath
                navController.navigate(NavRoutes.PrescriptionResult) {
                    popUpTo(NavRoutes.PrescriptionReader) { inclusive = true }
                }
            }
            is GeminiPrescriptionAnalyzer.AnalyzerResult.Error -> {
                PrescriptionResultHolder.result = null
                PrescriptionResultHolder.error = result.message
                PrescriptionResultHolder.imagePath = imagePath
                navController.navigate(NavRoutes.PrescriptionResult) {
                    popUpTo(NavRoutes.PrescriptionReader) { inclusive = true }
                }
            }
        }
    }

    Scaffold(containerColor = Color(0xFF0B1221)) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            PrescriptionAnalysisAnimation()
            Spacer(modifier = Modifier.height(32.dp))
            Text(
                text = "Reading Your Prescription...",
                color = Color.White,
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = statusText,
                color = Color.Gray,
                fontSize = 14.sp,
                textAlign = TextAlign.Center
            )
            Spacer(modifier = Modifier.height(24.dp))
            Text(
                text = "Gemini AI deciphering doctor's handwriting...",
                color = Color(0xFF2962FF).copy(alpha = 0.7f),
                fontSize = 12.sp,
                textAlign = TextAlign.Center
            )
        }
    }
}

/**
 * Simple in-memory holder to pass the result between screens.
 * (Alternative to complex nav args for large objects)
 */
object PrescriptionResultHolder {
    var result: GeminiPrescriptionAnalyzer.PrescriptionResult? = null
    var error: String? = null
    var imagePath: String? = null
}

@Composable
fun PrescriptionAnalysisAnimation() {
    val infiniteTransition = rememberInfiniteTransition(label = "rx-spinner")
    val angle by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(1500, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "rx-angle"
    )

    Canvas(modifier = Modifier.size(150.dp)) {
        drawArc(
            color = Color(0xFF2962FF).copy(alpha = 0.2f),
            startAngle = 0f,
            sweepAngle = 360f,
            useCenter = false,
            style = Stroke(width = 8.dp.toPx())
        )
        drawArc(
            color = Color(0xFF2962FF),
            startAngle = angle,
            sweepAngle = 120f,
            useCenter = false,
            style = Stroke(width = 8.dp.toPx())
        )
        drawArc(
            color = Color(0xFF2962FF).copy(alpha = 0.5f),
            startAngle = angle + 180,
            sweepAngle = 120f,
            useCenter = false,
            style = Stroke(width = 8.dp.toPx())
        )
    }
}