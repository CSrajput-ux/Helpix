package com.healthai.app.ui.screens.results.cough

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CoughResultScreen(navController: NavController) {

    // Dummy data for preview
    val probability = 0.75f // 75%
    val suggestionText: String
    val suggestionColor: Color

    when {
        probability > 0.6 -> {
            suggestionText = "Aapko turant nazdeeki jaanch kendra par jaakar CBNAAT/Sputum (balgam) Test karwana chahiye."
            suggestionColor = Color(0xFFFF6B6B)
        }
        probability > 0.3 -> {
            suggestionText = "Behtar hoga ki aap ek doctor se salah lein. Apne doosre lakshano par bhi dhyan dein."
            suggestionColor = Color.Yellow
        }
        else -> {
            suggestionText = "Abhi chinta ki baat nahi lagti. Agar aapko anya lakshan hain, to doctor se milna chahiye."
            suggestionColor = Color.Green
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Cough Analysis Report") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color(0xFF0B1221), titleContentColor = Color.White, navigationIconContentColor = Color.White)
            )
        },
        containerColor = Color(0xFF0B1221)
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(32.dp))
            Text("AI Probability Meter", color = Color.White, fontSize = 20.sp)
            
            Box(contentAlignment = Alignment.Center, modifier = Modifier.size(200.dp).padding(16.dp)) {
                ProbabilityGauge(progress = probability, color = suggestionColor)
                Text("${(probability * 100).toInt()}%", color = suggestionColor, fontSize = 40.sp, fontWeight = FontWeight.Bold)
            }
            
            Text("High Probability", color = suggestionColor, fontWeight = FontWeight.Bold)
            
            Spacer(modifier = Modifier.height(24.dp))
            
            // AI Explanation
            Card(colors = CardDefaults.cardColors(containerColor = Color(0xFF1E293B)), modifier = Modifier.fillMaxWidth()) {
                 Column(Modifier.padding(16.dp)) {
                    Text("Aisa Kyun Lag Raha Hai?", color = Color.White, fontWeight = FontWeight.Bold)
                    Text("Hamare AI ne aapki khaansi mein kuch aise patterns paaye hain (jaise 'geeli' awaaz aur lamba chalne wali khaansi) jo aksar TB ke mamlon mein dekhe jaate hain.", color = Color.Gray, fontSize = 12.sp)
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Suggestion Card
            Card(colors = CardDefaults.cardColors(containerColor = suggestionColor.copy(alpha = 0.1f)), modifier = Modifier.fillMaxWidth()) {
                 Column(Modifier.padding(16.dp)) {
                    Text("Should you test?", color = suggestionColor, fontWeight = FontWeight.Bold)
                    Text(suggestionText, color = Color.White, fontSize = 14.sp)
                }
            }

            Spacer(modifier = Modifier.weight(1f))

            Button(onClick = { /* TODO: Navigate to Doctors/Labs screen */ }) {
                Text("Find a Test Center")
            }
        }
    }
}

@Composable
fun ProbabilityGauge(progress: Float, color: Color) {
    val animatedProgress by animateFloatAsState(targetValue = progress, animationSpec = tween(1000))
    Canvas(modifier = Modifier.fillMaxSize()) {
        drawArc(
            color = color.copy(alpha = 0.2f),
            startAngle = 135f,
            sweepAngle = 270f,
            useCenter = false,
            style = Stroke(width = 40f, cap = StrokeCap.Round)
        )
        drawArc(
            color = color,
            startAngle = 135f,
            sweepAngle = 270 * animatedProgress,
            useCenter = false,
            style = Stroke(width = 40f, cap = StrokeCap.Round)
        )
    }
}