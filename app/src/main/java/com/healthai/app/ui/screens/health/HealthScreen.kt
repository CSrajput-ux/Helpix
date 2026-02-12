package com.healthai.app.ui.screens.health

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.healthai.app.R
import com.healthai.app.ui.screens.dashboard.HelpixBottomNav // Import from Dashboard
import com.healthai.app.ui.viewmodel.HealthViewModel

@Composable
fun HealthScreen(
    navController: NavController,
    viewModel: HealthViewModel = hiltViewModel()
) {
    val state by viewModel.uiState.collectAsState()
    val scrollState = rememberScrollState()
    
    // Purana "selectedTab" state hata diya gaya hai

    Scaffold(
        bottomBar = {
            // FIXED: Ab ye sirf navController leta hai
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
                            text = "Daily Health Feed",
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
                        Text("Your Score", color = Color.Gray, fontSize = 10.sp)
                        Text("${state.healthScore}", color = Color(0xFF00E676), fontSize = 24.sp, fontWeight = FontWeight.Bold)
                    }
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
                    Text("Today's Health Score", color = colorResource(id = R.color.logo_cyan), fontSize = 14.sp)
                    Icon(Icons.Default.Watch, contentDescription = "Watch", tint = colorResource(id = R.color.logo_cyan), modifier = Modifier.align(Alignment.TopEnd))

                    Box(modifier = Modifier.align(Alignment.Center)) {
                        CircularScoreIndicator(score = state.healthScore)
                    }
                    
                    Text(
                        text = if (state.isWatchConnected) "• Watch Connected" else "• Syncing Watch...",
                        color = if (state.isWatchConnected) Color(0xFF00E676) else Color.Yellow,
                        fontSize = 10.sp,
                        modifier = Modifier.align(Alignment.BottomCenter)
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                // --- METRICS GRID ---
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    MetricCard("Lung Health", "Excellent", "${state.lungHealth}%", Color(0xFF00E5FF), 0.92f, Modifier.weight(1f))
                    MetricCard("Skin Health", "Good", "${state.skinHealth}%", Color(0xFF00E676), 0.85f, Modifier.weight(1f))
                }
                Spacer(modifier = Modifier.height(12.dp))
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    MetricCard("Sleep Quality", state.sleepQuality, "${(state.sleepScore * 100).toInt()}%", Color(0xFFD500F9), state.sleepScore, Modifier.weight(1f))
                    MetricCard("Heart Rate", "Normal", "${state.heartRate} bpm", Color(0xFFFF4081), 0.72f, Modifier.weight(1f))
                }

                Spacer(modifier = Modifier.height(24.dp))

                // --- AI ALERTS ---
                SectionHeader("AI Health Alerts", Icons.Default.Psychology)
                AlertCard("Vitamin D Low", "Consider spending 15 mins in sunlight", "2h ago", Color(0xFFFFAB00))
                AlertCard("Hydration Reminder", "Drink 2 more glasses of water today", "4h ago", Color(0xFF2979FF))

                Spacer(modifier = Modifier.height(24.dp))

                // --- MEDICINES ---
                SectionHeader("Daily Medicines", Icons.Default.Medication)
                MedicineCard("Vitamin C", "9:00 AM", true)
                MedicineCard("Calcium", "2:00 PM", false)
                MedicineCard("Multivitamin", "8:00 PM", false)

                Spacer(modifier = Modifier.height(24.dp))

                // --- RISK PREDICTIONS ---
                SectionHeader("Risk Predictions", Icons.Default.TrendingUp)
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(16.dp))
                        .background(colorResource(id = R.color.helpix_bg_top))
                        .padding(16.dp)
                ) {
                    Column {
                        RiskItem("Seasonal Flu", 35, Color(0xFFFFC107))
                        Spacer(modifier = Modifier.height(12.dp))
                        RiskItem("Common Cold", 22, Color(0xFF448AFF))
                        Spacer(modifier = Modifier.height(12.dp))
                        RiskItem("Allergies", 18, Color(0xFF00E676))
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                // --- RECOMMENDATIONS ---
                SectionHeader("Recommended Today", Icons.Default.Recommend)
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    RecommendationCard("Diet Plan", "2000 cal target", Icons.Default.Restaurant, Color(0xFF00E676), Modifier.weight(1f))
                    RecommendationCard("Exercise", "30 min cardio", Icons.Default.FitnessCenter, Color(0xFFFF9100), Modifier.weight(1f))
                }
                
                Spacer(modifier = Modifier.height(100.dp))
            }
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
        Icon(Icons.Default.Warning, contentDescription = null, tint = color)
        Spacer(modifier = Modifier.width(16.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(title, color = color, fontWeight = FontWeight.Bold, fontSize = 14.sp)
            Text(subtitle, color = Color.Gray, fontSize = 12.sp)
        }
        Text(time, color = Color.Gray, fontSize = 10.sp)
    }
}

@Composable
fun MedicineCard(name: String, time: String, isTaken: Boolean) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = 12.dp)
            .clip(RoundedCornerShape(16.dp))
            .background(colorResource(id = R.color.helpix_bg_top))
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Box(
                modifier = Modifier
                    .size(24.dp)
                    .clip(CircleShape)
                    .background(if (isTaken) Color(0xFF00E676) else Color.Transparent)
                    .border(2.dp, if (isTaken) Color.Transparent else Color.Gray, CircleShape),
                contentAlignment = Alignment.Center
            ) {
                if (isTaken) Icon(Icons.Default.Check, contentDescription = null, tint = Color.Black, modifier = Modifier.size(16.dp))
            }
            Spacer(modifier = Modifier.width(16.dp))
            Column {
                Text(name, color = Color.White, fontWeight = FontWeight.Bold)
                Text(time, color = Color.Gray, fontSize = 12.sp)
            }
        }
        Button(
            onClick = {},
            colors = ButtonDefaults.buttonColors(containerColor = if (isTaken) Color.DarkGray else colorResource(id = R.color.logo_cyan).copy(alpha = 0.2f)),
            contentPadding = PaddingValues(horizontal = 16.dp, vertical = 0.dp),
            modifier = Modifier.height(32.dp)
        ) {
            Text(if (isTaken) "Taken" else "Take Now", fontSize = 12.sp, color = if (isTaken) Color.Gray else colorResource(id = R.color.logo_cyan))
        }
    }
}

@Composable
fun RiskItem(name: String, percent: Int, color: Color) {
    Column {
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
            Text(name, color = Color.White, fontSize = 13.sp)
            Text("$percent%", color = color, fontSize = 13.sp)
        }
        Spacer(modifier = Modifier.height(6.dp))
        LinearProgressIndicator(
            progress = percent / 100f,
            color = color,
            trackColor = Color.DarkGray,
            strokeCap = StrokeCap.Round,
            modifier = Modifier.fillMaxWidth().height(6.dp)
        )
    }
}

@Composable
fun RecommendationCard(title: String, subtitle: String, icon: ImageVector, color: Color, modifier: Modifier) {
    Box(
        modifier = modifier
            .height(100.dp)
            .clip(RoundedCornerShape(16.dp))
            .background(color.copy(alpha = 0.1f))
            .border(1.dp, color.copy(alpha = 0.3f), RoundedCornerShape(16.dp))
            .padding(12.dp)
    ) {
        Column(verticalArrangement = Arrangement.SpaceBetween, modifier = Modifier.fillMaxSize()) {
            Icon(icon, contentDescription = null, tint = color)
            Column {
                Text(title, color = color, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                Text(subtitle, color = Color.Gray, fontSize = 11.sp)
            }
        }
    }
}