package com.healthai.app.ui.screens.cough

import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.healthai.app.ui.navigation.NavRoutes
import kotlinx.coroutines.delay
import kotlin.random.Random

@Composable
fun CoughRecordingScreen(navController: NavController) {

    var recordingCount by remember { mutableStateOf(0) }
    val totalRequired = 5
    var isRecording by remember { mutableStateOf(false) }
    var showCountdown by remember { mutableStateOf(true) }
    var countdown by remember { mutableStateOf(3) }

    LaunchedEffect(recordingCount) {
        if (recordingCount < totalRequired) {
            showCountdown = true
            for (i in 3 downTo 1) {
                countdown = i
                delay(1000)
            }
            showCountdown = false
            isRecording = true
            delay(4000) // Record for 4 seconds
            isRecording = false
            recordingCount++
        } else {
            // All 5 recordings done, move to analysis
            delay(1000)
            navController.navigate(NavRoutes.CoughAnalysis)
        }
    }

    Scaffold(containerColor = Color(0xFF0B1221)) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Top
        ) {
            Spacer(modifier = Modifier.height(40.dp))
            
            // Progress Indicator
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                repeat(totalRequired) { index ->
                    Box(
                        modifier = Modifier
                            .size(12.dp)
                            .clip(CircleShape)
                            .background(
                                if (index < recordingCount) Color(0xFF00E5FF)
                                else if (index == recordingCount && isRecording) Color(0xFFD500F9)
                                else Color.Gray.copy(alpha = 0.3f)
                            )
                    )
                    if (index < totalRequired - 1) {
                        Spacer(modifier = Modifier.width(8.dp))
                    }
                }
            }
            
            Text(
                text = "Recording $recordingCount of $totalRequired",
                color = Color.Gray,
                fontSize = 14.sp,
                modifier = Modifier.padding(top = 12.dp)
            )

            Spacer(modifier = Modifier.weight(1f))

            if (showCountdown) {
                Text(
                    text = "Get Ready",
                    color = Color.White,
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    text = countdown.toString(),
                    color = Color(0xFF00E5FF),
                    fontSize = 80.sp,
                    fontWeight = FontWeight.ExtraBold
                )
            } else if (isRecording) {
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
                    textAlign = TextAlign.Center,
                    fontSize = 18.sp
                )
                
                Spacer(modifier = Modifier.height(48.dp))
                
                Box(contentAlignment = Alignment.Center) {
                    PulseAnimation()
                    Icon(
                        Icons.Default.Mic, 
                        contentDescription = null, 
                        tint = Color.White, 
                        modifier = Modifier.size(64.dp)
                    )
                }

                Spacer(modifier = Modifier.height(48.dp))
                
                CoughWaveformAnimation()
            } else if (recordingCount < totalRequired) {
                Text(
                    text = "Processing Recording...",
                    color = Color(0xFF00E5FF),
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Medium
                )
                Spacer(modifier = Modifier.height(24.dp))
                CircularProgressIndicator(color = Color(0xFF00E5FF))
            } else {
                Text(
                    text = "Analyzing Final Results...",
                    color = Color(0xFF00E676),
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(24.dp))
                CircularProgressIndicator(color = Color(0xFF00E676))
            }

            Spacer(modifier = Modifier.weight(1f))

            if (isRecording) {
                Text(
                    text = "Recording in progress...",
                    color = Color.Red.copy(alpha = 0.7f),
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold
                )
            }
            
            Spacer(modifier = Modifier.height(40.dp))
        }
    }
}

@Composable
fun PulseAnimation() {
    val infiniteTransition = rememberInfiniteTransition()
    val scale by infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue = 2f,
        animationSpec = infiniteRepeatable(
            animation = tween(1000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        )
    )
    val alpha by infiniteTransition.animateFloat(
        initialValue = 0.5f,
        targetValue = 0f,
        animationSpec = infiniteRepeatable(
            animation = tween(1000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        )
    )

    Box(
        modifier = Modifier
            .size(100.dp)
            .background(Color(0xFFD500F9).copy(alpha = alpha), CircleShape)
            .padding(12.dp)
            .background(Color(0xFFD500F9).copy(alpha = alpha), CircleShape)
    )
}

@Composable
fun CoughWaveformAnimation() {
    val infiniteTransition = rememberInfiniteTransition()
    val lineCount = 40
    
    val lines = List(lineCount) { index ->
        val durationMillis = remember(index) { Random.nextInt(300, 700) }
        val targetValue = remember(index) { Random.nextFloat() * 80f + 20f }

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
        .height(100.dp)) {
        val lineWidth = 6.dp.toPx()
        val spacing = 10.dp.toPx()
        val totalWidth = (lineCount * lineWidth) + ((lineCount - 1) * spacing)
        var currentX = (size.width - totalWidth) / 2
        
        lines.forEach { anim ->
            drawLine(
                color = Color(0xFFD500F9),
                start = Offset(x = currentX, y = (size.height - anim.value) / 2),
                end = Offset(x = currentX, y = (size.height + anim.value) / 2),
                strokeWidth = lineWidth,
                cap = androidx.compose.ui.graphics.StrokeCap.Round
            )
            currentX += lineWidth + spacing
        }
    }
}