package com.healthai.app.ui.screens.health

import androidx.compose.foundation.BorderStroke
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
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.healthai.app.ui.screens.dashboard.CardBorderGray
import com.healthai.app.ui.screens.dashboard.HelpixBottomNav 
import com.healthai.app.ui.screens.profile.ProfileViewModel
import com.healthai.app.ui.viewmodel.HealthViewModel

private val OffWhiteBg = Color(0xFFF8FAFC)
private val MedicalTeal = Color(0xFF0D9488)
private val FriendlyRoyalBlue = Color(0xFF2563EB)
private val DarkCharcoalText = Color(0xFF1E293B)
private val SlateGrayText = Color(0xFF475569)

@Composable
fun HealthScreen(
    navController: NavController,
    viewModel: HealthViewModel = hiltViewModel()
) {
    val state by viewModel.uiState.collectAsState()
    val scrollState = rememberScrollState()
    val profileViewModel: ProfileViewModel = hiltViewModel()
    val profile by profileViewModel.user.collectAsState()
    val userType = profile?.role ?: "PATIENT"
    
    Scaffold(
        bottomBar = {
            HelpixBottomNav(navController = navController, userType = userType)
        },
        containerColor = OffWhiteBg
    ) { padding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .background(OffWhiteBg)
        ) {
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
                            color = MedicalTeal,
                            fontSize = 26.sp,
                            fontWeight = FontWeight.ExtraBold
                        )
                        Text(
                            text = state.currentDate,
                            color = SlateGrayText,
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Medium,
                            modifier = Modifier.padding(top = 4.dp)
                        )
                    }
                    Column(horizontalAlignment = Alignment.End) {
                        Text("Your Score", color = SlateGrayText, fontSize = 11.sp, fontWeight = FontWeight.Medium)
                        Text("${state.healthScore}", color = Color(0xFF16A34A), fontSize = 32.sp, fontWeight = FontWeight.ExtraBold)
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                // --- REAL-TIME VITALS CARDS ---
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    VitalFeedCard(
                        title = "Heartbeat",
                        value = if (state.heartRate > 0) "${state.heartRate}" else "--",
                        unit = "bpm",
                        icon = Icons.Default.Favorite,
                        modifier = Modifier.weight(1f),
                        iconTint = Color(0xFFEF4444),
                        circleBg = Color(0xFFFEE2E2)
                    )
                    VitalFeedCard(
                        title = "Temp",
                        value = if (state.temperature > 0) "${state.temperature}" else "--",
                        unit = "°F",
                        icon = Icons.Default.Thermostat,
                        modifier = Modifier.weight(1f),
                        iconTint = Color(0xFFD97706),
                        circleBg = Color(0xFFFEF3C7)
                    )
                    VitalFeedCard(
                        title = "Blood Pressure",
                        value = if(state.bloodPressure.isNotBlank()) state.bloodPressure else "--/--",
                        unit = "",
                        icon = Icons.Default.Bloodtype,
                        modifier = Modifier.weight(1f),
                        iconTint = Color(0xFF2563EB),
                        circleBg = Color(0xFFDBEAFE)
                    )
                }

                Spacer(modifier = Modifier.height(24.dp))

                // --- TODAY'S HEALTH SCORE CARD ---
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(200.dp)
                        .clip(RoundedCornerShape(24.dp))
                        .background(Color.White)
                        .border(BorderStroke(1.dp, CardBorderGray), RoundedCornerShape(24.dp))
                        .padding(20.dp)
                ) {
                    Text("Today's Health Score", color = MedicalTeal, fontSize = 15.sp, fontWeight = FontWeight.Bold)
                    Icon(Icons.Default.Watch, contentDescription = null, tint = MedicalTeal, modifier = Modifier.align(Alignment.TopEnd).size(24.dp))

                    Box(modifier = Modifier.align(Alignment.Center)) {
                        CircularScoreGauge(score = state.healthScore)
                    }
                    
                    Text(
                        text = if (state.isWatchConnected) "Watch Connected" else "Syncing...",
                        color = if (state.isWatchConnected) Color(0xFF16A34A) else Color(0xFFD97706),
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.align(Alignment.BottomCenter)
                    )
                }

                Spacer(modifier = Modifier.height(28.dp))

                // --- URGENT NOTIFICATIONS SECTION ---
                Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.padding(bottom = 16.dp)) {
                    Icon(Icons.Default.NotificationsActive, contentDescription = null, tint = MedicalTeal, modifier = Modifier.size(22.dp))
                    Spacer(modifier = Modifier.width(10.dp))
                    Text("Urgent Health Notifications", color = DarkCharcoalText, fontSize = 18.sp, fontWeight = FontWeight.Bold)
                }

                AlertFeedCard("Medicine Reminder", "Time to take your Vitamin C (Next dose in 10 mins)", "Just now", FriendlyRoyalBlue, Icons.Default.Alarm)
                AlertFeedCard("Medical Emergency", "Your heartbeat is slightly above normal. Rest for 5 mins.", "5 mins ago", Color(0xFFDC2626), Icons.Default.Warning)
                AlertFeedCard("Disease Area Warning", "You are entering a High Disease Outbreak Zone (Dengue). Wear protection.", "15 mins ago", Color(0xFFEA580C), Icons.Default.ReportProblem)

                Spacer(modifier = Modifier.height(40.dp))
            }
        }
    }
}

@Composable
fun VitalFeedCard(title: String, value: String, unit: String, icon: ImageVector, modifier: Modifier, iconTint: Color, circleBg: Color) {
    Card(
        modifier = modifier.height(110.dp),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        border = BorderStroke(1.dp, CardBorderGray)
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.fillMaxSize(), verticalArrangement = Arrangement.Center) {
            Box(
                modifier = Modifier
                    .size(32.dp)
                    .background(circleBg, CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Icon(icon, contentDescription = null, tint = iconTint, modifier = Modifier.size(18.dp))
            }
            Spacer(modifier = Modifier.height(8.dp))
            Row(verticalAlignment = Alignment.Bottom) {
                Text(value, color = DarkCharcoalText, fontSize = 20.sp, fontWeight = FontWeight.ExtraBold)
                if (unit.isNotEmpty()) {
                    Spacer(modifier = Modifier.width(2.dp))
                    Text(unit, color = SlateGrayText, fontSize = 10.sp, modifier = Modifier.padding(bottom = 3.dp))
                }
            }
            Text(title, color = SlateGrayText, fontSize = 10.sp, fontWeight = FontWeight.Medium)
        }
    }
}

@Composable
fun CircularScoreGauge(score: Int) {
    Box(contentAlignment = Alignment.Center, modifier = Modifier.size(110.dp)) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            val strokeWidth = 10.dp.toPx()
            drawCircle(color = CardBorderGray, style = Stroke(strokeWidth))
            drawArc(
                brush = Brush.sweepGradient(listOf(MedicalTeal, Color(0xFF16A34A), MedicalTeal)),
                startAngle = -90f,
                sweepAngle = (360 * (score / 100f)),
                useCenter = false,
                style = Stroke(strokeWidth, cap = StrokeCap.Round)
            )
        }
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text("$score", color = DarkCharcoalText, fontSize = 32.sp, fontWeight = FontWeight.ExtraBold)
            Text("Excellent", color = Color(0xFF16A34A), fontSize = 11.sp, fontWeight = FontWeight.Bold)
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
        colors = CardDefaults.cardColors(containerColor = Color.White),
        border = BorderStroke(1.dp, CardBorderGray)
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
                Text(subtitle, color = SlateGrayText, fontSize = 12.sp, lineHeight = 16.sp)
            }
            Text(time, color = SlateGrayText, fontSize = 10.sp, modifier = Modifier.align(Alignment.Top))
        }
    }
}
