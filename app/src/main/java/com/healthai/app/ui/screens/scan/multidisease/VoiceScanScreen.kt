package com.healthai.app.ui.screens.scan.multidisease

import android.Manifest
import android.content.pm.PackageManager
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import androidx.navigation.NavController
import com.healthai.app.ui.navigation.NavRoutes
import kotlinx.coroutines.delay
import kotlin.random.Random

@Composable
fun VoiceScanScreen(navController: NavController) {
    val context = LocalContext.current
    var hasAudioPermission by remember {
        mutableStateOf(ContextCompat.checkSelfPermission(context, Manifest.permission.RECORD_AUDIO) == PackageManager.PERMISSION_GRANTED)
    }
    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission(),
        onResult = { granted ->
            hasAudioPermission = granted
        }
    )

    LaunchedEffect(key1 = true) {
        if (!hasAudioPermission) {
            launcher.launch(Manifest.permission.RECORD_AUDIO)
        }
    }

    Scaffold(containerColor = Color(0xFF0B1221)) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(32.dp))
            Text(
                text = "Voice Scan",
                color = Color.White,
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = "Jab kaha jaaye, tab 5 second ke liye 'Aaaaaah' bolen.",
                color = Color.Gray,
                fontSize = 14.sp,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(horizontal = 16.dp)
            )
            Spacer(modifier = Modifier.height(48.dp))

            if (hasAudioPermission) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(Icons.Default.Mic, contentDescription = "Microphone", tint = Color.White, modifier = Modifier.size(64.dp))
                    Spacer(modifier = Modifier.height(32.dp))
                    Text(text = "Ab Khaansein...", color = Color(0xFF00FFFF), fontSize = 20.sp, fontWeight = FontWeight.Bold)
                    Spacer(modifier = Modifier.height(32.dp))
                    WaveformAnimation()
                }
            } else {
                Text("Audio recording permission is required.", color = Color.White, textAlign = TextAlign.Center)
            }
            
            Spacer(modifier = Modifier.weight(1f))
            
            Button(
                onClick = { navController.navigate(NavRoutes.VitalsScan) },
                modifier = Modifier.fillMaxWidth().height(50.dp),
                 colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF00FFFF))
            ) {
                Text("Next Step", color = Color.Black)
                Icon(Icons.Default.ArrowForward, contentDescription = null, tint = Color.Black)
            }
        }
    }
}

@Composable
fun WaveformAnimation() {
    val infiniteTransition = rememberInfiniteTransition()
    val lineCount = 50
    val lines = List(lineCount) { index ->
        val durationMillis = remember(index) { Random.nextInt(300, 800) }
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
        val lineWidth = size.width / (lineCount * 2)
        var currentX = 0f
        
        lines.forEach { anim ->
            drawLine(
                color = Color(0xFF00FFFF),
                start = Offset(x = currentX, y = (size.height - anim.value) / 2),
                end = Offset(x = currentX, y = (size.height + anim.value) / 2),
                strokeWidth = lineWidth
            )
            currentX += lineWidth * 2
        }
    }
}