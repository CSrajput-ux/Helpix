package com.healthai.app.ui.screens.health

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.healthai.app.R
import com.healthai.app.ui.navigation.NavRoutes
import com.healthai.app.ui.screens.dashboard.HelpixBottomNav 
import com.healthai.app.ui.viewmodel.HealthViewModel

@Composable
fun HealthScreen(
    navController: NavController,
    viewModel: HealthViewModel = hiltViewModel()
) {
    val state by viewModel.uiState.collectAsState()
    val scrollState = rememberScrollState()
    
    Scaffold(
        bottomBar = {
            HelpixBottomNav(navController = navController)
        }
    ) { padding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .background(
                    brush = Brush.verticalGradient(
                        colors = listOf(
                            colorResource(id = R.color.login_bg_top),
                            colorResource(id = R.color.login_bg_bottom)
                        )
                    )
                )
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 16.dp)
                    .verticalScroll(scrollState)
            ) {
                Spacer(modifier = Modifier.height(20.dp))

                // --- HEADER ---
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = stringResource(id = R.string.daily_health_feed),
                            color = colorResource(id = R.color.logo_cyan),
                            fontSize = 22.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = state.currentDate,
                            color = Color.Gray,
                            fontSize = 12.sp
                        )
                    }
                    Column(horizontalAlignment = Alignment.End) {
                        Text(stringResource(id = R.string.your_score), color = Color.Gray, fontSize = 10.sp)
                        Text("${state.healthScore}", color = Color(0xFF00E676), fontSize = 24.sp, fontWeight = FontWeight.Bold)
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                // --- NAVIGATION / REAL-TIME VITALS CARDS ---
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                    VitalDisplayCard(
                        title = "Heartbeat",
                        value = if (state.heartRate > 0) "${state.heartRate}" else "--",
                        unit = "bpm",
                        icon = Icons.Default.Favorite,
                        modifier = Modifier.weight(1f),
                        color = Color(0xFFFF4081)
                    )
                    VitalDisplayCard(
                        title = "Temp",
                        value = if (state.temperature > 0) "${state.temperature}" else "--",
                        unit = "°C",
                        icon = Icons.Default.Thermostat,
                        modifier = Modifier.weight(1f),
                        color = Color(0xFFFFAB00)
                    )
                    VitalDisplayCard(
                        title = "Blood Pressure",
                        value = state.bloodPressure,
                        unit = "",
                        icon = Icons.Default.Bloodtype,
                        modifier = Modifier.weight(1f),
                        color = Color(0xFF2979FF)
                    )
                }

                Spacer(modifier = Modifier.height(24.dp))

                // --- BIG HEALTH SCORE CARD ---
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(180.dp)
                        .clip(RoundedCornerShape(24.dp))
                        .background(colorResource(id = R.color.helpix_bg_top))
                        .border(1.dp, colorResource(id = R.color.card_border_glow).copy(alpha = 0.3f), RoundedCornerShape(24.dp))
                        .padding(16.dp)
                ) {
                    Text(stringResource(id = R.string.todays_health_score), color = colorResource(id = R.color.logo_cyan), fontSize = 14.sp)
                    Icon(Icons.Default.Watch, contentDescription = "Watch", tint = colorResource(id = R.color.logo_cyan), modifier = Modifier.align(Alignment.TopEnd))

                    Box(modifier = Modifier.align(Alignment.Center)) {
                        CircularScoreIndicator(score = state.healthScore)
                    }
                    
                    Text(
                        text = if (state.isWatchConnected) stringResource(id = R.string.watch_connected) else stringResource(id = R.string.syncing_watch),
                        color = if (state.isWatchConnected) Color(0xFF00E676) else Color.Yellow,
                        fontSize = 10.sp,
                        modifier = Modifier.align(Alignment.BottomCenter)
                    )
                }

                Spacer(modifier = Modifier.height(24.dp))

                // --- AI ALERTS ---
                SectionHeader("Urgent Health Notifications", Icons.Default.NotificationsActive)
                AlertCard("Medicine Reminder", "Time to take your Vitamin C (Next dose in 10 mins)", "Just now", Color(0xFF2979FF))
                AlertCard("Medical Emergency", "Your heartbeat is slightly above normal. Rest for 5 mins.", "5 mins ago", Color(0xFFFF1744))
                AlertCard("Disease Area Warning", "You are entering a High Disease Outbreak Zone (Dengue). Wear protection.", "15 mins ago", Color(0xFFFFAB00))

                Spacer(modifier = Modifier.height(100.dp))
            }
        }
    }
}

@Composable
fun VitalDisplayCard(title: String, value: String, unit: String, icon: ImageVector, modifier: Modifier, color: Color) {
    Box(
        modifier = modifier
            .height(100.dp)
            .clip(RoundedCornerShape(16.dp))
            .background(colorResource(id = R.color.helpix_bg_top))
            .padding(10.dp)
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.fillMaxSize(), verticalArrangement = Arrangement.Center) {
            Icon(icon, contentDescription = null, tint = color, modifier = Modifier.size(20.dp))
            Spacer(modifier = Modifier.height(4.dp))
            Row(verticalAlignment = Alignment.Bottom) {
                Text(value, color = Color.White, fontSize = 18.sp, fontWeight = FontWeight.Bold)
                if (unit.isNotEmpty()) {
                    Spacer(modifier = Modifier.width(2.dp))
                    Text(unit, color = Color.Gray, fontSize = 10.sp, modifier = Modifier.padding(bottom = 2.dp))
                }
            }
            Text(title, color = Color.Gray, fontSize = 10.sp, fontWeight = FontWeight.Medium)
        }
    }
}

// --- SUB-COMPONENTS ---

@Composable
fun SectionHeader(title: String, icon: ImageVector) {
    Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.padding(bottom = 12.dp)) {
        Icon(icon, contentDescription = null, tint = colorResource(id = R.color.logo_cyan), modifier = Modifier.size(20.dp))
        Spacer(modifier = Modifier.width(8.dp))
        Text(title, color = colorResource(id = R.color.logo_cyan), fontSize = 16.sp, fontWeight = FontWeight.SemiBold)
    }
}

@Composable
fun CircularScoreIndicator(score: Int) {
    Box(contentAlignment = Alignment.Center, modifier = Modifier.size(100.dp)) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            val strokeWidth = 8.dp.toPx()
            drawCircle(color = Color.DarkGray.copy(alpha = 0.3f), style = Stroke(strokeWidth))
            drawArc(
                brush = Brush.sweepGradient(listOf(Color(0xFF00E5FF), Color(0xFF00E676))),
                startAngle = -90f,
                sweepAngle = (360 * (score / 100f)),
                useCenter = false,
                style = Stroke(strokeWidth, cap = StrokeCap.Round)
            )
        }
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text("$score", color = Color.White, fontSize = 28.sp, fontWeight = FontWeight.Bold)
            Text("Excellent", color = Color(0xFF00E676), fontSize = 10.sp)
        }
    }
}

@Composable
fun MetricCard(title: String, status: String, value: String, color: Color, progress: Float, modifier: Modifier) {
    Box(
        modifier = modifier
            .height(100.dp)
            .clip(RoundedCornerShape(16.dp))
            .background(colorResource(id = R.color.helpix_bg_top))
            .padding(12.dp)
    ) {
        Column(verticalArrangement = Arrangement.SpaceBetween, modifier = Modifier.fillMaxSize()) {
            Row(horizontalArrangement = Arrangement.SpaceBetween, modifier = Modifier.fillMaxWidth()) {
                Icon(if (title.contains("Heart")) Icons.Default.Favorite else Icons.Default.Air, contentDescription = null, tint = color, modifier = Modifier.size(18.dp))
                Text(status, color = color, fontSize = 10.sp)
            }
            Column {
                Text(title, color = Color.White, fontSize = 12.sp)
                Spacer(modifier = Modifier.height(4.dp))
                LinearProgressIndicator(
                    progress = progress,
                    color = color,
                    trackColor = color.copy(alpha = 0.2f),
                    strokeCap = StrokeCap.Round,
                    modifier = Modifier.fillMaxWidth().height(4.dp)
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(value, color = Color.White, fontSize = 10.sp, modifier = Modifier.align(Alignment.End))
            }
        }
    }
}

@Composable
fun AlertCard(title: String, subtitle: String, time: String, color: Color) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = 12.dp)
            .clip(RoundedCornerShape(16.dp))
            .background(colorResource(id = R.color.helpix_bg_top))
            .border(1.dp, color.copy(alpha = 0.3f), RoundedCornerShape(16.dp))
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(if (title.contains("Emergency")) Icons.Default.Warning else if (title.contains("Disease")) Icons.Default.ReportProblem else Icons.Default.Alarm, contentDescription = null, tint = color)
        Spacer(modifier = Modifier.width(16.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(title, color = color, fontWeight = FontWeight.Bold, fontSize = 14.sp)
            Text(subtitle, color = Color.Gray, fontSize = 12.sp)
        }
        Text(time, color = Color.Gray, fontSize = 10.sp)
    }
}