package com.healthai.app.ui.screens.senior

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
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.healthai.app.R
import com.healthai.app.ui.screens.dashboard.HelpixBottomNav

// Custom Senior Theme Colors
val SeniorBgTop = Color(0xFF2E003E) // Dark Purple
val SeniorBgBottom = Color(0xFF0D0D0D) // Almost Black
val SosRed = Color(0xFFFF0040) // Bright Red
val CardPurple = Color(0xFF2D1B4E)

@Composable
fun SeniorModeScreen(navController: NavController) {
    val scrollState = rememberScrollState()

    Scaffold(
        bottomBar = { HelpixBottomNav(navController = navController) }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .background(
                    brush = Brush.verticalGradient(
                        colors = listOf(SeniorBgTop, SeniorBgBottom)
                    )
                )
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 16.dp)
                    .verticalScroll(scrollState)
            ) {
                // Header with Back Button
                Spacer(modifier = Modifier.height(20.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back", tint = Color.White)
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    Column {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text("👴", fontSize = 24.sp)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Senior Care Mode", fontSize = 24.sp, fontWeight = FontWeight.Bold, color = Color(0xFFE1BEE7))
                        }
                        Text("Easy & Simple Health Monitoring", fontSize = 14.sp, color = Color.Gray)
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                // 1. EMERGENCY SOS BUTTON (The Big Red One)
                Button(
                    onClick = { /* Trigger SOS */ },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(100.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = SosRed),
                    shape = RoundedCornerShape(16.dp),
                    elevation = ButtonDefaults.buttonElevation(defaultElevation = 10.dp)
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.Warning, contentDescription = null, modifier = Modifier.size(40.dp), tint = Color.White)
                        Spacer(modifier = Modifier.width(16.dp))
                        Column {
                            Text("EMERGENCY SOS", fontSize = 22.sp, fontWeight = FontWeight.Bold, color = Color.White)
                            Text("Tap for Immediate Help", fontSize = 14.sp, color = Color.White)
                        }
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                // 2. Large Action Cards
                SeniorActionCard("Health Check", "View your daily health report", Icons.Default.MonitorHeart, Color(0xFFD500F9))
                Spacer(modifier = Modifier.height(16.dp))
                
                SeniorActionCard("My Medicines", "View medicine schedule", Icons.Default.Medication, Color(0xFF00B0FF))
                Spacer(modifier = Modifier.height(16.dp))
                
                SeniorActionCard("Call Doctor", "Contact your physician", Icons.Default.Call, Color(0xFF00E676))
                Spacer(modifier = Modifier.height(16.dp))
                
                SeniorActionCard("Appointments", "See upcoming visits", Icons.Default.CalendarToday, Color(0xFFFF9100))

                Spacer(modifier = Modifier.height(24.dp))

                // 3. Voice Assistant Section
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .border(1.dp, Color(0xFF00B0FF).copy(alpha = 0.5f), RoundedCornerShape(24.dp))
                        .background(Color(0xFF1A1A2E).copy(alpha = 0.6f), RoundedCornerShape(24.dp))
                        .padding(24.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(Icons.Default.VolumeUp, contentDescription = null, tint = Color(0xFF00B0FF), modifier = Modifier.size(40.dp))
                        Spacer(modifier = Modifier.height(8.dp))
                        Text("Voice Assistant", fontSize = 20.sp, fontWeight = FontWeight.Bold, color = Color.White)
                        Text("Tap and speak your needs", fontSize = 14.sp, color = Color.Gray)
                        
                        Spacer(modifier = Modifier.height(16.dp))
                        
                        Button(
                            onClick = { },
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF00B0FF)),
                            modifier = Modifier.fillMaxWidth().height(50.dp)
                        ) {
                            Text("Start Voice Command", fontSize = 16.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }

                Spacer(modifier = Modifier.height(100.dp))
            }
        }
    }
}

@Composable
fun SeniorActionCard(title: String, subtitle: String, icon: ImageVector, color: Color) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(90.dp)
            .border(1.dp, color.copy(alpha = 0.5f), RoundedCornerShape(16.dp))
            .background(Color(0xFF1E1E1E).copy(alpha = 0.8f), RoundedCornerShape(16.dp))
            .clickable { }
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Icon Box
        Box(
            modifier = Modifier
                .size(56.dp)
                .background(color, RoundedCornerShape(12.dp)),
            contentAlignment = Alignment.Center
        ) {
            Icon(icon, contentDescription = null, tint = Color.White, modifier = Modifier.size(30.dp))
        }
        
        Spacer(modifier = Modifier.width(16.dp))
        
        // Text
        Column {
            Text(title, fontSize = 20.sp, fontWeight = FontWeight.SemiBold, color = Color.White)
            Text(subtitle, fontSize = 14.sp, color = Color.Gray)
        }
    }
}