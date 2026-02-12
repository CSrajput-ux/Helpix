package com.healthai.app.ui.screens.prescription

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
fun PrescriptionAnalysisScreen(navController: NavController) {

    // Simulate analysis and navigate to the result/verification screen
    LaunchedEffect(key1 = true) {
        delay(3500) // Simulate a 3.5-second analysis
        navController.navigate(NavRoutes.PrescriptionResult) { 
            popUpTo(NavRoutes.PrescriptionReader) { inclusive = true }
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
                text = "AI doctor ki likhai ko samajh raha hai...",
                color = Color.Gray,
                fontSize = 14.sp,
                textAlign = TextAlign.Center
            )
        }
    }
}

@Composable
fun PrescriptionAnalysisAnimation() {
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
            color = Color(0xFF2962FF), // Blue theme color
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