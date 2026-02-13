package com.healthai.app.ui.screens.health

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.healthai.app.R
import com.healthai.app.domain.model.VitalsLog

@Composable
fun HealthHistoryScreen(viewModel: HealthHistoryViewModel = viewModel()) {

    val vitalsHistory by viewModel.vitalsHistory.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()

    LaunchedEffect(Unit) {
        viewModel.fetchVitalsHistory()
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF0B1221)),
        contentAlignment = Alignment.Center
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
        ) {
            Text(
                stringResource(id = R.string.vitals_history),
                color = Color.White,
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(24.dp))

            if (isLoading) {
                CircularProgressIndicator(color = Color(0xFF00E5FF))
            } else if (vitalsHistory.isNotEmpty()) {
                LineChart(vitalsHistory)
            } else {
                Text("No vitals history found.", color = Color.Gray)
            }
        }
    }
}

@Composable
fun LineChart(data: List<VitalsLog>) {
    val heartRateData = data.map { it.heartRate.toFloat() }
    val maxHeartRate = heartRateData.maxOrNull() ?: 100f
    val minHeartRate = heartRateData.minOrNull() ?: 60f

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(300.dp)
            .background(Color(0xFF1A293D), shape = androidx.compose.foundation.shape.RoundedCornerShape(20.dp))
            .padding(16.dp)
    ) {
        Canvas(modifier = Modifier.fillMaxSize()) { 
            val path = Path()
            val stepX = size.width / (heartRateData.size - 1)

            heartRateData.forEachIndexed { i, heartRate ->
                val x = i * stepX
                val y = size.height * (1 - (heartRate - minHeartRate) / (maxHeartRate - minHeartRate))
                if (i == 0) {
                    path.moveTo(x, y)
                } else {
                    path.lineTo(x, y)
                }
            }

            drawPath(
                path = path,
                brush = Brush.horizontalGradient(listOf(Color(0xFF00E5FF), Color(0xFF00A2FF))),
                style = Stroke(width = 5f)
            )
        }
    }
}