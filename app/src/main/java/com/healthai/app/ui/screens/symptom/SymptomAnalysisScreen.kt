package com.healthai.app.ui.screens.symptom

import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.healthai.app.ui.navigation.NavRoutes
import com.healthai.app.ui.viewmodel.SymptomViewModel
import kotlinx.coroutines.delay

@Composable
fun SymptomAnalysisScreen(
    navController: NavController,
    viewModel: SymptomViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    LaunchedEffect(uiState.analysisResult) {
        if (uiState.analysisResult != null) {
            delay(1500) // Aesthetic delay for animation
            navController.navigate(NavRoutes.SymptomResult) {
                popUpTo(NavRoutes.SymptomDoctorStart) { inclusive = false }
            }
        } else if (!uiState.isLoading && uiState.selectedSymptoms.isNotEmpty()) {
            viewModel.analyzeSymptoms { success ->
                if (success) {
                    navController.navigate(NavRoutes.SymptomResult) {
                        popUpTo(NavRoutes.SymptomDoctorStart) { inclusive = false }
                    }
                }
            }
        }
    }

    Scaffold(containerColor = Color(0xFF0B1221)) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            SymptomAnalysisAnimation()
            Spacer(modifier = Modifier.height(36.dp))
            Text(
                text = "Analyzing Your Symptoms...",
                color = Color.White,
                fontSize = 22.sp,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center
            )
            Spacer(modifier = Modifier.height(10.dp))
            Text(
                text = "Comparing against 130+ clinical disease patterns with Decision Tree AI & Gemini.",
                color = Color.Gray,
                fontSize = 14.sp,
                textAlign = TextAlign.Center,
                lineHeight = 20.sp
            )
        }
    }
}

@Composable
fun SymptomAnalysisAnimation() {
    val infiniteTransition = rememberInfiniteTransition()
    val angle by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(1400, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        )
    )

    Canvas(modifier = Modifier.size(140.dp)) {
        drawArc(
            color = Color(0xFF2563EB),
            startAngle = angle,
            sweepAngle = 120f,
            useCenter = false,
            style = Stroke(width = 7.dp.toPx())
        )
        drawArc(
            color = Color(0xFF60A5FA).copy(alpha = 0.5f),
            startAngle = angle + 180,
            sweepAngle = 120f,
            useCenter = false,
            style = Stroke(width = 7.dp.toPx())
        )
    }
}