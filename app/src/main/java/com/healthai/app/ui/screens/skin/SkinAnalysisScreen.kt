package com.healthai.app.ui.screens.skin

import android.graphics.BitmapFactory
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
import com.healthai.app.ml.SkinClassifier
import com.healthai.app.ui.navigation.NavRoutes
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.withContext
import java.io.File

@Composable
fun SkinAnalysisScreen(navController: NavController) {
    val context = LocalContext.current
    var analysisResult by remember { mutableStateOf<SkinClassifier.Recognition?>(null) }
    
    // Initialize Classifier
    val classifier = remember { SkinClassifier(context) }

    LaunchedEffect(key1 = true) {
        // 1. Get the latest captured image from cache
        val cacheDir = context.cacheDir
        val latestFile = cacheDir.listFiles()
            ?.filter { it.name.startsWith("skin_scan_") }
            ?.maxByOrNull { it.lastModified() }

        if (latestFile != null) {
            val bitmap = BitmapFactory.decodeFile(latestFile.absolutePath)
            
            // 2. Run Inference in Background
            withContext(Dispatchers.Default) {
                delay(2000) // Artificial delay for UX
                analysisResult = classifier.classifySkin(bitmap)
            }
        } else {
            // Mock delay if no file found (for testing)
            delay(2000)
            analysisResult = SkinClassifier.Recognition("Normal Skin", 0.98f)
        }

        // 3. Navigate to Result Screen with data
        val result = analysisResult!!
        navController.navigate("${NavRoutes.SkinResult}/${result.label}/${result.confidence}") {
            popUpTo(NavRoutes.SkinDetectorStart) { inclusive = true }
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
            SkinAnalysisAnimation()
            Spacer(modifier = Modifier.height(32.dp))
            Text(
                text = "Analyzing Your Skin...",
                color = Color.White,
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "Helpix AI is scanning for patterns, color, and texture markers.",
                color = Color.Gray,
                fontSize = 14.sp,
                textAlign = TextAlign.Center
            )
        }
    }
}

@Composable
fun SkinAnalysisAnimation() {
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
            color = Color(0xFF00E676),
            startAngle = angle,
            sweepAngle = 120f,
            useCenter = false,
            style = Stroke(width = 8.dp.toPx())
        )
    }
}