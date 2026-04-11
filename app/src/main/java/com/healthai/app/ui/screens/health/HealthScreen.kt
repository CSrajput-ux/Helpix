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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
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
    var userType by remember { mutableStateOf("PATIENT") }

    // Fetch user type for BottomNav consistency
    LaunchedEffect(Unit) {
        val uid = FirebaseAuth.getInstance().currentUser?.uid
        if (uid != null) {
            FirebaseFirestore.getInstance().collection("users").document(uid).get()
                .addOnSuccessListener { document ->
                    userType = document.getString("userType") ?: "PATIENT"
                }
        }
    }
    
    Scaffold(
        bottomBar = {
            HelpixBottomNav(navController = navController, userType = userType)
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
            // Grid Background like Dashboard
            GridBackgroundHealth()

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 20.dp)
                    .verticalScroll(scrollState)
            ) {
                Spacer(modifier = Modifier.height(24.dp))

                // --- HEADER ---
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.Top
                ) {
                    Column {
                        Text(
                            text = "Daily Health Feed",
                            color = colorResource(id = R.color.logo_cyan),
                            fontSize = 26.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = state.currentDate,
                            color = Color.Gray,
                            fontSize = 12.sp,
                            modifier = Modifier.padding(top = 4.dp)
                        )
                    }
                    Column(horizontalAlignment = Alignment.End) {
                        Text("Your Score", color = Color.Gray, fontSize = 10.sp)
                        Text("${state.healthScore}", color = Color(0xFF00E676), fontSize = 32.sp, fontWeight = FontWeight.ExtraBold)
                    }
                }

                Spacer(modifier = Modifier.height(32.dp))

                // --- REAL-TIME VITALS CARDS (Matching Dashboard style) ---
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    VitalFeedCard(
                        title = "Heartbeat",
                        value = if (state.heartRate > 0) "${state.heartRate}" else "--",
                        unit = "bpm",
                        icon = Icons.Default.Favorite,
                        modifier = Modifier.weight(1f),
                        color = Color(0xFFFF4081)
                    )
                    VitalFeedCard(
                        title = "Temp",
                        value = if (state.temperature > 0) "${state.temperature}" else "--",
                        unit = "°C",
                        icon = Icons.Default.Thermostat,
                        modifier = Modifier.weight(1f),
                        color = Color(0xFFFFAB00)
                    )
                    VitalFeedCard(
                        title = "Blood Pressure",
                        value = if(state.bloodPressure.isNotBlank()) state.bloodPressure else "--/--",
                        unit = "",
                        icon = Icons.Default.Bloodtype,
                        modifier = Modifier.weight(1f),
                        color = Color(0xFF2979FF)
                    )
                }

                Spacer(modifier = Modifier.height(32.dp))

                // --- TODAY'S HEALTH SCORE CARD (BIG) ---
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(200.dp)
                        .clip(RoundedCornerShape(28.dp))
                        .background(colorResource(id = R.color.helpix_bg_top).copy(alpha = 0.6f))
                        .border(1.dp, colorResource(id = R.color.card_border_glow).copy(alpha = 0.3f), RoundedCornerShape(28.dp))
                        .padding(20.dp)
                ) {
                    Text("Today's Health Score", color = colorResource(id = R.color.logo_cyan), fontSize = 15.sp, fontWeight = FontWeight.Medium)
                    Icon(Icons.Default.Watch, contentDescription = null, tint = colorResource(id = R.color.logo_cyan), modifier = Modifier.align(Alignment.TopEnd).size(24.dp))

                    Box(modifier = Modifier.align(Alignment.Center)) {
                        CircularScoreGauge(score = state.healthScore)
                    }
                    
                    Text(
                        text = if (state.isWatchConnected) "Watch Connected" else "Syncing...",
                        color = if (state.isWatchConnected) Color(0xFF00E676) else Color(0xFFFFD600),
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.align(Alignment.BottomCenter)
                    )
                }

                Spacer(modifier = Modifier.height(32.dp))

                // --- URGENT NOTIFICATIONS SECTION ---
                Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.padding(bottom = 16.dp)) {
                    Icon(Icons.Default.NotificationsActive, contentDescription = null, tint = colorResource(id = R.color.logo_cyan), modifier = Modifier.size(22.dp))
                    Spacer(modifier = Modifier.width(10.dp))
                    Text("Urgent Health Notifications", color = colorResource(id = R.color.logo_cyan), fontSize = 18.sp, fontWeight = FontWeight.Bold)
                }

                AlertFeedCard("Medicine Reminder", "Time to take your Vitamin C (Next dose in 10 mins)", "Just now", Color(0xFF2979FF), Icons.Default.Alarm)
                AlertFeedCard("Medical Emergency", "Your heartbeat is slightly above normal. Rest for 5 mins.", "5 mins ago", Color(0xFFFF1744), Icons.Default.Warning)
                AlertFeedCard("Disease Area Warning", "You are entering a High Disease Outbreak Zone (Dengue). Wear protection.", "15 mins ago", Color(0xFFFFAB00), Icons.Default.ReportProblem)

                Spacer(modifier = Modifier.height(40.dp))
            }
        }
    }
}

@Composable
fun VitalFeedCard(title: String, value: String, unit: String, icon: ImageVector, modifier: Modifier, color: Color) {
    Card(
        modifier = modifier.height(110.dp),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White.copy(alpha = 0.05f)),
        border = androidx.compose.foundation.BorderStroke(1.dp, color.copy(alpha = 0.2f))
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.fillMaxSize(), verticalArrangement = Arrangement.Center) {
            Icon(icon, contentDescription = null, tint = color, modifier = Modifier.size(24.dp))
            Spacer(modifier = Modifier.height(8.dp))
            Row(verticalAlignment = Alignment.Bottom) {
                Text(value, color = Color.White, fontSize = 20.sp, fontWeight = FontWeight.ExtraBold)
                if (unit.isNotEmpty()) {
                    Spacer(modifier = Modifier.width(2.dp))
                    Text(unit, color = Color.Gray, fontSize = 10.sp, modifier = Modifier.padding(bottom = 3.dp))
                }
            }
            Text(title, color = Color.Gray, fontSize = 10.sp, fontWeight = FontWeight.Medium)
        }
    }
}

@Composable
fun CircularScoreGauge(score: Int) {
    Box(contentAlignment = Alignment.Center, modifier = Modifier.size(110.dp)) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            val strokeWidth = 10.dp.toPx()
            drawCircle(color = Color.White.copy(alpha = 0.05f), style = Stroke(strokeWidth))
            drawArc(
                brush = Brush.sweepGradient(listOf(Color(0xFF00E5FF), Color(0xFF00E676), Color(0xFF00E5FF))),
                startAngle = -90f,
                sweepAngle = (360 * (score / 100f)),
                useCenter = false,
                style = Stroke(strokeWidth, cap = StrokeCap.Round)
            )
        }
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text("$score", color = Color.White, fontSize = 32.sp, fontWeight = FontWeight.ExtraBold)
            Text("Excellent", color = Color(0xFF00E676), fontSize = 11.sp, fontWeight = FontWeight.Bold)
        }
    }
}

@Composable
fun AlertFeedCard(title: String, subtitle: String, time: String, color: Color, icon: ImageVector) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = 12.dp),
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = colorResource(id = R.color.helpix_bg_top).copy(alpha = 0.4f)),
        border = androidx.compose.foundation.BorderStroke(1.dp, color.copy(alpha = 0.2f))
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier.size(44.dp).background(color.copy(alpha = 0.1f), CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Icon(icon, contentDescription = null, tint = color, modifier = Modifier.size(22.dp))
            }
            Spacer(modifier = Modifier.width(16.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(title, color = color, fontWeight = FontWeight.Bold, fontSize = 15.sp)
                Text(subtitle, color = Color.Gray, fontSize = 12.sp, lineHeight = 16.sp)
            }
            Text(time, color = Color.Gray, fontSize = 10.sp, modifier = Modifier.align(Alignment.Top))
        }
    }
}

@Composable
fun GridBackgroundHealth() {
    val color = Color.White.copy(alpha = 0.03f)
    Canvas(modifier = Modifier.fillMaxSize()) {
        val gridSize = 45.dp.toPx()
        for (x in 0..size.width.toInt() step gridSize.toInt()) {
            drawLine(color, start = Offset(x.toFloat(), 0f), end = Offset(x.toFloat(), size.height))
        }
        for (y in 0..size.height.toInt() step gridSize.toInt()) {
            drawLine(color, start = Offset(0f, y.toFloat()), end = Offset(size.width, y.toFloat()))
        }
    }
}
