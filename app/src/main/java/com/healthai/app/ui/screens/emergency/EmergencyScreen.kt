package com.healthai.app.ui.screens.emergency

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

// Custom Emergency Colors
val EmergencyBgTop = Color(0xFF4A0E0E) // Dark Red
val EmergencyBgBottom = Color(0xFF1A0505) // Almost Black
val AlertRed = Color(0xFFD32F2F) // Bright Red
val CardRedBg = Color(0xFF2B1212) // Semi-transparent Red
val NeonRed = Color(0xFFFF5252)

@Composable
fun EmergencyScreen(navController: NavController) {
    val scrollState = rememberScrollState()

    Scaffold(
        bottomBar = { HelpixBottomNav(navController = navController) },
        containerColor = Color.Black
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .background(
                    brush = Brush.verticalGradient(
                        colors = listOf(EmergencyBgTop, EmergencyBgBottom)
                    )
                )
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 16.dp)
                    .verticalScroll(scrollState)
            ) {
                // Header
                Spacer(modifier = Modifier.height(20.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.Warning, contentDescription = null, tint = NeonRed, modifier = Modifier.size(32.dp))
                        Spacer(modifier = Modifier.width(12.dp))
                        Column {
                            Text("EMERGENCY MODE", fontSize = 20.sp, fontWeight = FontWeight.Bold, color = Color.White)
                            Text("Immediate Assistance", fontSize = 12.sp, color = NeonRed)
                        }
                    }
                    // Close Button
                    IconButton(
                        onClick = { navController.popBackStack() },
                        modifier = Modifier.background(Color.White.copy(alpha = 0.1f), CircleShape)
                    ) {
                        Icon(Icons.Default.Close, contentDescription = "Close", tint = Color.White)
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                // Alert Banner
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .border(1.dp, NeonRed.copy(alpha = 0.5f), RoundedCornerShape(50))
                        .background(NeonRed.copy(alpha = 0.1f), RoundedCornerShape(50))
                        .padding(vertical = 8.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text("🚨 Quick Response Activated 🚨", color = Color.White, fontSize = 14.sp)
                }

                Spacer(modifier = Modifier.height(24.dp))

                // 1. BIG CALL BUTTON
                Button(
                    onClick = { /* Call Action */ },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(140.dp)
                        .border(2.dp, NeonRed, RoundedCornerShape(16.dp)),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF590D0D)), // Deep Red
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(Icons.Default.Phone, contentDescription = null, modifier = Modifier.size(40.dp))
                        Spacer(modifier = Modifier.height(8.dp))
                        Text("CALL AMBULANCE", fontSize = 24.sp, fontWeight = FontWeight.Bold)
                        Text("Tap to Call 108", fontSize = 14.sp, color = Color.White.copy(alpha = 0.8f))
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                // 2. Personal Emergency Card
                Text("👤 Your Emergency Card", color = Color.White, fontSize = 16.sp, modifier = Modifier.padding(bottom = 12.dp))
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .border(1.dp, NeonRed.copy(alpha = 0.3f), RoundedCornerShape(16.dp))
                        .background(CardRedBg, RoundedCornerShape(16.dp))
                        .padding(16.dp)
                ) {
                    Column {
                        Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                            PersonalDetailBox("Name", "John Doe", Modifier.weight(1f))
                            PersonalDetailBox("Age", "32 years", Modifier.weight(1f))
                        }
                        Spacer(modifier = Modifier.height(12.dp))
                        Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                            PersonalDetailBox("Blood Group", "🩸 O+", Modifier.weight(1f))
                            PersonalDetailBox("Allergies", "None", Modifier.weight(1f))
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // 3. Location & Nearby
                Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    EmergencyStatCard("Nearest Hospital", "1.2 km away", Icons.Default.LocationOn, Modifier.weight(1f))
                    EmergencyStatCard("Blood Banks", "3 nearby", Icons.Default.WaterDrop, Modifier.weight(1f))
                }

                Spacer(modifier = Modifier.height(16.dp))

                // 4. Location Tracking
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .border(1.dp, NeonRed.copy(alpha = 0.3f), RoundedCornerShape(16.dp))
                        .background(CardRedBg, RoundedCornerShape(16.dp))
                        .padding(16.dp)
                ) {
                    Column {
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                            Row {
                                Icon(Icons.Default.MyLocation, contentDescription = null, tint = Color.White, modifier = Modifier.size(20.dp))
                                Spacer(modifier = Modifier.width(8.dp))
                                Text("Location Tracking", color = Color.White, fontSize = 16.sp)
                            }
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Box(modifier = Modifier.size(8.dp).background(Color.Green, CircleShape))
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("Active", color = Color.Green, fontSize = 12.sp)
                            }
                        }
                        Spacer(modifier = Modifier.height(12.dp))
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(Color.Black.copy(alpha = 0.3f), RoundedCornerShape(8.dp))
                                .padding(12.dp)
                        ) {
                            Column {
                                Text("Current Location", color = NeonRed, fontSize = 10.sp)
                                Text("Sharing with emergency services...", color = Color.White, fontSize = 14.sp)
                                Text("Lat: 28.6139, Long: 77.2090", color = Color.Gray, fontSize = 10.sp)
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                // 5. Contacts List
                Text("Emergency Contacts", color = Color.White, fontSize = 16.sp, modifier = Modifier.padding(bottom = 12.dp))
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .border(1.dp, NeonRed.copy(alpha = 0.3f), RoundedCornerShape(16.dp))
                        .background(CardRedBg, RoundedCornerShape(16.dp))
                        .padding(16.dp)
                ) {
                    Column {
                        ContactRow(Icons.Default.LocalHospital, "Ambulance", "108 - Free")
                        Divider(color = NeonRed.copy(alpha = 0.2f), modifier = Modifier.padding(vertical = 12.dp))
                        ContactRow(Icons.Default.Favorite, "Emergency Contact", "+91 98765 43210")
                        Divider(color = NeonRed.copy(alpha = 0.2f), modifier = Modifier.padding(vertical = 12.dp))
                        ContactRow(Icons.Default.LocalPolice, "Police", "100")
                    }
                }

                Spacer(modifier = Modifier.height(100.dp))
            }
        }
    }
}

// --- HELPER COMPONENTS ---

@Composable
fun PersonalDetailBox(label: String, value: String, modifier: Modifier) {
    Box(
        modifier = modifier
            .background(Color.Black.copy(alpha = 0.2f), RoundedCornerShape(8.dp))
            .border(1.dp, NeonRed.copy(alpha = 0.2f), RoundedCornerShape(8.dp))
            .padding(12.dp)
    ) {
        Column {
            Text(label, color = NeonRed, fontSize = 10.sp)
            Spacer(modifier = Modifier.height(4.dp))
            Text(value, color = Color.White, fontSize = 14.sp, fontWeight = FontWeight.Bold)
        }
    }
}

@Composable
fun EmergencyStatCard(title: String, subtitle: String, icon: ImageVector, modifier: Modifier) {
    Box(
        modifier = modifier
            .height(80.dp)
            .border(1.dp, NeonRed.copy(alpha = 0.3f), RoundedCornerShape(16.dp))
            .background(CardRedBg, RoundedCornerShape(16.dp))
            .padding(12.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Icon(icon, contentDescription = null, tint = Color.White, modifier = Modifier.size(20.dp))
            Spacer(modifier = Modifier.height(4.dp))
            Text(title, color = Color.White, fontSize = 12.sp)
            Text(subtitle, color = Color.Gray, fontSize = 10.sp)
        }
    }
}

@Composable
fun ContactRow(icon: ImageVector, title: String, subtitle: String) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .background(NeonRed.copy(alpha = 0.2f), CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Icon(icon, contentDescription = null, tint = NeonRed)
            }
            Spacer(modifier = Modifier.width(12.dp))
            Column {
                Text(title, color = Color.White, fontSize = 14.sp, fontWeight = FontWeight.Bold)
                Text(subtitle, color = Color.Gray, fontSize = 12.sp)
            }
        }
        
        Button(
            onClick = {},
            colors = ButtonDefaults.buttonColors(containerColor = NeonRed),
            contentPadding = PaddingValues(horizontal = 16.dp, vertical = 0.dp),
            modifier = Modifier.height(32.dp)
        ) {
            Text("CALL", fontSize = 12.sp, fontWeight = FontWeight.Bold)
        }
    }
}