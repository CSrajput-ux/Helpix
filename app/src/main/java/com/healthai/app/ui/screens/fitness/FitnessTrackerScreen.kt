package com.healthai.app.ui.screens.fitness

import android.Manifest
import android.content.pm.PackageManager
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import androidx.navigation.NavController

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FitnessTrackerScreen(navController: NavController) {

    val context = LocalContext.current
    var hasPermission by remember {
        mutableStateOf(ContextCompat.checkSelfPermission(context, Manifest.permission.ACTIVITY_RECOGNITION) == PackageManager.PERMISSION_GRANTED)
    }
    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission(),
        onResult = { granted ->
            hasPermission = granted
        }
    )

    LaunchedEffect(key1 = true) {
        if (!hasPermission) {
            launcher.launch(Manifest.permission.ACTIVITY_RECOGNITION)
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Fitness Tracker") },
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
            if (hasPermission) {
                // Dummy data for preview
                val steps = 7500f
                val stepGoal = 10000f
                val activeMinutes = 45f
                val activeMinutesGoal = 60f

                Spacer(modifier = Modifier.height(32.dp))

                Box(contentAlignment = Alignment.Center, modifier = Modifier.size(250.dp)) {
                    ActivityRing(progress = 1f, color = Color(0xFF00E5FF).copy(alpha = 0.3f), strokeWidth = 20f)
                    ActivityRing(progress = steps / stepGoal, color = Color(0xFF00E5FF), strokeWidth = 20f)
                    
                    ActivityRing(progress = 1f, color = Color(0xFF76FF03).copy(alpha = 0.3f), size = 200.dp, strokeWidth = 20f)
                    ActivityRing(progress = activeMinutes / activeMinutesGoal, color = Color(0xFF76FF03), size = 200.dp, strokeWidth = 20f)

                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text("Steps", color = Color.Gray, fontSize = 14.sp)
                        Text(steps.toInt().toString(), color = Color.White, fontSize = 40.sp, fontWeight = FontWeight.Bold)
                        Text("Goal: ${stepGoal.toInt()}", color = Color.Gray, fontSize = 12.sp)
                    }
                }

                 Spacer(modifier = Modifier.height(48.dp))
                 Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceAround) {
                     StatItem(value = "${activeMinutes.toInt()} min", label = "Active Time", color = Color(0xFF76FF03))
                     StatItem(value = "5.2 km", label = "Distance", color = Color(0xFF00E5FF))
                     StatItem(value = "320 kCal", label = "Calories", color = Color(0xFFFF9100))
                 }

            } else {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                     Text(
                        text = "Activity permission is required to track your steps.",
                        color = Color.White, 
                        textAlign = TextAlign.Center
                    ) 
                }
            }
        }
    }
}

@Composable
fun ActivityRing(
    progress: Float,
    color: Color,
    size: androidx.compose.ui.unit.Dp = 250.dp,
    strokeWidth: Float = 20f
) {
    val animatedProgress by animateFloatAsState(targetValue = progress, animationSpec = tween(1000))
    Canvas(modifier = Modifier.size(size)) {
        drawArc(
            color = color,
            startAngle = -90f,
            sweepAngle = 360 * animatedProgress,
            useCenter = false,
            style = Stroke(width = strokeWidth, cap = StrokeCap.Round)
        )
    }
}

@Composable
fun StatItem(value: String, label: String, color: Color) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(text = value, fontSize = 22.sp, fontWeight = FontWeight.Bold, color = color)
        Text(text = label, fontSize = 12.sp, color = Color.Gray)
    }
}