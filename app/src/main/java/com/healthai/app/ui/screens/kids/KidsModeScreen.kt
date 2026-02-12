package com.healthai.app.ui.screens.kids

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Gamepad
import androidx.compose.material.icons.filled.MedicalServices
import androidx.compose.material.icons.filled.AccessTime
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.healthai.app.R
import com.healthai.app.ui.screens.dashboard.HelpixBottomNav

// Custom Kids Theme Colors
val KidsBgTop = Color(0xFF4A0E4E) // Deep Purple
val KidsBgBottom = Color(0xFF1A237E) // Deep Blue
val NeonGreen = Color(0xFF00E676)
val NeonOrange = Color(0xFFFF9100)
val NeonPink = Color(0xFFF50057)
val CardDark = Color(0xFF2D1B4E)

@Composable
fun KidsModeScreen(navController: NavController) {
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
                        colors = listOf(KidsBgTop, KidsBgBottom)
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
                Row(verticalAlignment = Alignment.CenterVertically) {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back", tint = Color.White)
                    }
                    Column(modifier = Modifier.fillMaxWidth(), horizontalAlignment = Alignment.CenterHorizontally) {
                        Text("Kids Health Fun! 🎈", fontSize = 22.sp, fontWeight = FontWeight.Bold, color = Color.White)
                        Text("How are you feeling today?", fontSize = 14.sp, color = Color.LightGray)
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                // 1. Mood Selector
                Text("😊 How Do You Feel? 😊", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = Color(0xFFFFD740), modifier = Modifier.align(Alignment.CenterHorizontally))
                Spacer(modifier = Modifier.height(16.dp))
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    MoodCard("😊", "Great!", NeonGreen, Modifier.weight(1f))
                    MoodCard("😐", "Okay", NeonOrange, Modifier.weight(1f))
                    MoodCard("😢", "Not Good", NeonPink, Modifier.weight(1f))
                }

                Spacer(modifier = Modifier.height(32.dp))

                // 2. Symptoms Grid
                Text("🩺 What's Wrong? 🩺", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = Color(0xFFE040FB), modifier = Modifier.align(Alignment.CenterHorizontally))
                Spacer(modifier = Modifier.height(16.dp))
                
                // Row 1
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    SymptomCard("🤒", "Fever", Color(0xFFFF5252), Modifier.weight(1f))
                    SymptomCard("🤧", "Cold", Color(0xFFFFB74D), Modifier.weight(1f))
                    SymptomCard("🤢", "Tummy Ache", Color(0xFF69F0AE), Modifier.weight(1f))
                }
                Spacer(modifier = Modifier.height(12.dp))
                // Row 2
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    SymptomCard("😴", "Tired", Color(0xFFFFD740), Modifier.weight(1f))
                    SymptomCard("🤕", "Headache", Color(0xFFFFAB91), Modifier.weight(1f))
                    SymptomCard("😷", "Cough", Color(0xFF448AFF), Modifier.weight(1f))
                }

                Spacer(modifier = Modifier.height(32.dp))

                // 3. Growth Chart
                Text("📏 Growth Chart 📏", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = Color(0xFF40C4FF))
                Spacer(modifier = Modifier.height(12.dp))
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(16.dp))
                        .background(CardDark)
                        .border(1.dp, Color(0xFF7C4DFF), RoundedCornerShape(16.dp))
                        .padding(16.dp)
                ) {
                    Column {
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceAround) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Text("Height", color = Color.Gray, fontSize = 12.sp)
                                Text("120", color = Color(0xFF448AFF), fontSize = 32.sp, fontWeight = FontWeight.Bold)
                                Text("cm", color = Color.Gray, fontSize = 14.sp)
                            }
                            // Vertical Divider
                            Box(modifier = Modifier.height(60.dp).width(1.dp).background(Color.Gray.copy(alpha = 0.3f)))
                            
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Text("Weight", color = Color.Gray, fontSize = 12.sp)
                                Text("25", color = NeonGreen, fontSize = 32.sp, fontWeight = FontWeight.Bold)
                                Text("kg", color = Color.Gray, fontSize = 14.sp)
                            }
                        }
                        Spacer(modifier = Modifier.height(16.dp))
                        // Banner
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(8.dp))
                                .background(Color(0xFF6200EA).copy(alpha = 0.4f))
                                .padding(8.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text("🌟 You're growing great! 🌟", color = Color.White, fontWeight = FontWeight.Bold)
                        }
                    }
                }

                Spacer(modifier = Modifier.height(32.dp))

                // 4. Vaccine Tracker
                Text("💉 Vaccine Tracker 💉", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = NeonGreen)
                Spacer(modifier = Modifier.height(12.dp))
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(16.dp))
                        .background(CardDark)
                        .border(1.dp, NeonGreen.copy(alpha = 0.3f), RoundedCornerShape(16.dp))
                        .padding(16.dp)
                ) {
                    VaccineItem("Measles Vaccine", "Completed - June 2025", true)
                    Spacer(modifier = Modifier.height(12.dp))
                    VaccineItem("Flu Shot", "Due - December 2025", false)
                }

                Spacer(modifier = Modifier.height(32.dp))

                // 5. Health Games
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(20.dp))
                        .background(Brush.horizontalGradient(listOf(Color(0xFF6200EA), Color(0xFFC51162))))
                        .padding(2.dp) // Border space
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(20.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Icon(Icons.Default.Gamepad, contentDescription = null, tint = Color.White, modifier = Modifier.size(40.dp))
                        Spacer(modifier = Modifier.height(8.dp))
                        Text("Health Games!", fontSize = 20.sp, fontWeight = FontWeight.Bold, color = Color.White)
                        Text("Play fun games while learning about health!", fontSize = 12.sp, color = Color.White.copy(alpha = 0.8f))
                        Spacer(modifier = Modifier.height(16.dp))
                        Button(
                            onClick = {},
                            colors = ButtonDefaults.buttonColors(containerColor = Color.White),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Text("Play Now! 🎉", color = Color(0xFFC51162), fontWeight = FontWeight.Bold)
                        }
                    }
                }

                Spacer(modifier = Modifier.height(100.dp))
            }
        }
    }
}

// --- COMPONENTS ---

@Composable
fun MoodCard(emoji: String, label: String, color: Color, modifier: Modifier) {
    Box(
        modifier = modifier
            .height(100.dp)
            .clip(RoundedCornerShape(16.dp))
            .background(color.copy(alpha = 0.1f))
            .border(1.dp, color, RoundedCornerShape(16.dp))
            .clickable { },
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(emoji, fontSize = 32.sp)
            Spacer(modifier = Modifier.height(8.dp))
            Text(label, color = color, fontWeight = FontWeight.Bold)
        }
    }
}

@Composable
fun SymptomCard(emoji: String, label: String, color: Color, modifier: Modifier) {
    Box(
        modifier = modifier
            .height(90.dp)
            .clip(RoundedCornerShape(12.dp))
            .background(color.copy(alpha = 0.15f))
            .clickable { },
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(emoji, fontSize = 24.sp)
            Spacer(modifier = Modifier.height(4.dp))
            Text(label, color = Color.White, fontSize = 12.sp)
        }
    }
}

@Composable
fun VaccineItem(name: String, status: String, isDone: Boolean) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(if (isDone) Color(0xFF1B5E20) else Color(0xFF3E2723))
            .padding(12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(32.dp)
                .clip(CircleShape)
                .background(if (isDone) NeonGreen else NeonOrange),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                if (isDone) Icons.Default.CheckCircle else Icons.Default.AccessTime,
                contentDescription = null,
                tint = Color.Black,
                modifier = Modifier.size(20.dp)
            )
        }
        Spacer(modifier = Modifier.width(16.dp))
        Column {
            Text(name, color = Color.White, fontWeight = FontWeight.Bold)
            Text(status, color = if (isDone) NeonGreen else NeonOrange, fontSize = 12.sp)
        }
    }
}