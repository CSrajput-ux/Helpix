package com.healthai.app.ui.screens.cough

import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.healthai.app.ui.navigation.NavRoutes
import kotlinx.coroutines.delay
import kotlin.random.Random

@Composable
fun CoughRecordingScreen(navController: NavController) {

    var countdown by remember { mutableStateOf(3) }
    var isRecording by remember { mutableStateOf(false) }

    LaunchedEffect(key1 = true) {
        for (i in 3 downTo 1) {
            countdown = i
            delay(1000)
        }
        isRecording = true
        // TODO: Start actual audio recording here
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
            if (!isRecording) {
                Text(
                    text = countdown.toString(),
                    color = Color.White,
                    fontSize = 80.sp,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "Taiyar ho jayein...",
                    color = Color.Gray,
                    fontSize = 20.sp
                )
            } else {
                Text(
                    text = "Ab Khaansein",
                    color = Color(0xFFD500F9),
                    fontSize = 32.sp,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    text = "2-3 baar zor se khaansein",
                    color = Color.Gray,
                    fontSize = 16.sp
                )
                Spacer(modifier = Modifier.height(48.dp))
                Icon(Icons.Default.Mic, contentDescription = null, tint = Color.White, modifier = Modifier.size(64.dp))
                Spacer(modifier = Modifier.height(32.dp))
                CoughWaveformAnimation()
                
                Spacer(modifier = Modifier.height(64.dp))

                Button(onClick = { 
                    navController.navigate(NavRoutes.CoughAnalysis)
                 }) {
                    Text(text = "Stop Recording")
                }
            }
        }
    }
}

@Composable
fun CoughWaveformAnimation() {
    val infiniteTransition = rememberInfiniteTransition()
    val lineCount = 60
    
    val lines = List(lineCount) { index ->
        val durationMillis = remember(index) { Random.nextInt(200, 600) }
        val targetValue = remember(index) { Random.nextFloat() * 100f + 20f }

        infiniteTransition.animateFloat(
            initialValue = 10f,
            targetValue = targetValue,
            animationSpec = infiniteRepeatable(
                animation = tween(durationMillis, easing = FastOutSlowInEasing),
                repeatMode = RepeatMode.Reverse
            )
        )
    }

    Canvas(modifier = Modifier
        .fillMaxWidth()
        .height(120.dp)) {
        val lineWidth = size.width / (lineCount * 1.5f)
        var currentX = 0f
        
        lines.forEach { anim ->
            drawLine(
                color = Color(0xFFD500F9),
                start = Offset(x = currentX, y = (size.height - anim.value) / 2),
                end = Offset(x = currentX, y = (size.height + anim.value) / 2),
                strokeWidth = lineWidth
            )
            currentX += lineWidth * 1.5f
        }
    }
}
