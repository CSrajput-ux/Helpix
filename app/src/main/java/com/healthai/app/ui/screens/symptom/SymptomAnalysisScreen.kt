package com.healthai.app.ui.screens.symptom

import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.healthai.app.ui.navigation.NavRoutes
import kotlinx.coroutines.delay

@Composable
fun SymptomAnalysisScreen(navController: NavController) {

    // Simulate analysis and navigate to a result screen after a delay
    LaunchedEffect(key1 = true) {
        delay(3000) // Simulate a 3-second analysis
        navController.navigate(NavRoutes.SymptomResult) { 
            popUpTo(NavRoutes.SymptomDoctorStart) { inclusive = true }
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
            SymptomAnalysisAnimation()
            Spacer(modifier = Modifier.height(32.dp))
            Text(
                text = "Analyzing Your Symptoms...",
                color = Color.White,
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "AI aapke lakshano ka vishleshan kar raha hai.",
                color = Color.Gray,
                fontSize = 14.sp,
                textAlign = TextAlign.Center
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
            animation = tween(1500, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        )
    )

    Canvas(modifier = Modifier.size(150.dp)) {
        drawArc(
            color = Color(0xFF2979FF), // Blue theme color
            startAngle = angle,
            sweepAngle = 120f,
            useCenter = false,
            style = Stroke(width = 8.dp.toPx())
        )
        drawArc(
            color = Color(0xFF2979FF).copy(alpha = 0.5f),
            startAngle = angle + 180,
            sweepAngle = 120f,
            useCenter = false,
            style = Stroke(width = 8.dp.toPx())
        )
    }
}
