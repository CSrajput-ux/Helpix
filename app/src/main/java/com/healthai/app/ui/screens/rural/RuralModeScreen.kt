package com.healthai.app.ui.screens.rural

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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.healthai.app.R
import com.healthai.app.ui.screens.dashboard.HelpixBottomNav

// Custom Rural Theme Colors
val RuralBgTop = Color(0xFF3E2723) // Dark Brown
val RuralBgBottom = Color(0xFF1A120B) // Deep Earth
val RuralOrange = Color(0xFFFF6D00)
val RuralGreen = Color(0xFF2E7D32)
val RuralRed = Color(0xFFC62828)
val CardBrown = Color(0xFF4E342E)

@Composable
fun RuralModeScreen(navController: NavController) {
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
                        colors = listOf(RuralBgTop, RuralBgBottom)
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
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back", tint = Color(0xFFFFCC80))
                    }
                    Text("वापस जाएं / Back", color = Color(0xFFFFCC80), fontSize = 16.sp)
                }

                Column(modifier = Modifier.fillMaxWidth(), horizontalAlignment = Alignment.CenterHorizontally) {
                    Text("🌾 ग्रामीण स्वास्थ्य सेवा", fontSize = 24.sp, fontWeight = FontWeight.Bold, color = Color(0xFFFFE0B2))
                    Text("Rural Health Service", fontSize = 16.sp, color = Color(0xFFFFCC80))
                    Spacer(modifier = Modifier.height(4.dp))
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.WifiOff, contentDescription = null, tint = RuralGreen, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("ऑफ़लाइन मोड / Offline Mode Active", fontSize = 12.sp, color = RuralGreen)
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                // 1. Voice Input Section (Orange Border)
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .border(2.dp, RuralOrange.copy(alpha = 0.5f), RoundedCornerShape(16.dp))
                        .background(Color(0xFF2D1B0E).copy(alpha = 0.6f), RoundedCornerShape(16.dp))
                        .padding(20.dp)
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.fillMaxWidth()) {
                        Icon(Icons.Default.Mic, contentDescription = null, tint = RuralOrange, modifier = Modifier.size(48.dp))
                        Spacer(modifier = Modifier.height(8.dp))
                        Text("🎤 आवाज़ से बताएं", fontSize = 22.sp, fontWeight = FontWeight.Bold, color = Color.White)
                        Text("Voice Input", fontSize = 14.sp, color = Color.Gray)
                        Spacer(modifier = Modifier.height(8.dp))
                        Text("अपनी समस्या बोलें / Speak Your Problem", fontSize = 12.sp, color = Color.Gray)

                        Spacer(modifier = Modifier.height(16.dp))

                        Button(
                            onClick = { },
                            colors = ButtonDefaults.buttonColors(containerColor = RuralOrange),
                            modifier = Modifier.fillMaxWidth().height(50.dp),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Text("बोलना शुरू करें / Start Speaking", fontSize = 16.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                // 2. Disease Selection Grid
                Text(
                    text = "बीमारी चुनें / Select Disease",
                    fontSize = 18.sp,
                    color = RuralOrange,
                    modifier = Modifier.align(Alignment.CenterHorizontally)
                )
                Spacer(modifier = Modifier.height(16.dp))

                Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                        DiseaseCard("बुखार / Fever", "🤒", RuralRed, Modifier.weight(1f))
                        DiseaseCard("सर्दी / Cold", "🤧", RuralOrange, Modifier.weight(1f))
                    }
                    Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                        DiseaseCard("पेट दर्द / Stomach", "🤢", RuralGreen, Modifier.weight(1f))
                        DiseaseCard("सिरदर्द / Headache", "🤕", Color(0xFF8E24AA), Modifier.weight(1f))
                    }
                    Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                        DiseaseCard("खांसी / Cough", "😷", Color(0xFF039BE5), Modifier.weight(1f))
                        DiseaseCard("चोट / Injury", "🩹", Color(0xFF795548), Modifier.weight(1f))
                    }
                }

                Spacer(modifier = Modifier.height(32.dp))

                // 3. Emergency Contacts (Red Box)
                InfoSectionBox(RuralRed, "🚨 आपातकालीन संपर्क", "Emergency Contact") {
                    InfoRow(Icons.Default.LocalHospital, "स्वास्थ्य केंद्र / Health Center", "📞 108 (टोल फ्री / Toll Free)")
                    HorizontalDivider(color = RuralRed.copy(alpha = 0.3f), modifier = Modifier.padding(vertical = 8.dp))
                    InfoRow(Icons.Default.LocationOn, "नजदीकी अस्पताल / Nearest Hospital", "📍 2.5 किमी दूर / 2.5 km away")
                }

                Spacer(modifier = Modifier.height(16.dp))

                // 4. Nearby Centers (Green Box)
                InfoSectionBox(RuralGreen, "📍 नजदीकी केंद्र", "Nearby Centers") {
                    InfoRow(Icons.Default.MedicalServices, "प्राथमिक स्वास्थ्य केंद्र", "Primary Health Center\n✓ खुला / Open  2 किमी / 2 km")
                    HorizontalDivider(color = RuralGreen.copy(alpha = 0.3f), modifier = Modifier.padding(vertical = 8.dp))
                    InfoRow(Icons.Default.Business, "सामुदायिक स्वास्थ्य केंद्र", "Community Health Center\n✓ खुला / Open  5 किमी / 5 km")
                }

                Spacer(modifier = Modifier.height(24.dp))

                // 5. Offline Features
                Text("📶 ऑफ़लाइन सुविधाएं / Offline Features", fontSize = 18.sp, color = Color(0xFF4DB6AC), fontWeight = FontWeight.Bold)
                Spacer(modifier = Modifier.height(12.dp))

                Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    OfflineCard("लक्षण जांच", "Symptom Check", Icons.Default.ContentPaste, Color(0xFF795548), Modifier.weight(1f))
                    OfflineCard("दवा गाइड", "Medicine Guide", Icons.Default.Medication, Color(0xFF8E24AA), Modifier.weight(1f))
                }
                Spacer(modifier = Modifier.height(12.dp))
                Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    OfflineCard("प्राथमिक उपचार", "First Aid", Icons.Default.MedicalServices, RuralGreen, Modifier.weight(1f))
                    OfflineCard("संपर्क नंबर", "Contact Numbers", Icons.Default.Call, RuralOrange, Modifier.weight(1f))
                }

                Spacer(modifier = Modifier.height(100.dp))
            }
        }
    }
}

// --- COMPONENTS ---

@Composable
fun DiseaseCard(title: String, emoji: String, color: Color, modifier: Modifier) {
    Box(
        modifier = modifier
            .height(80.dp)
            .clip(RoundedCornerShape(12.dp))
            .background(color.copy(alpha = 0.15f))
            .border(1.dp, color.copy(alpha = 0.5f), RoundedCornerShape(12.dp))
            .clickable { },
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(emoji, fontSize = 24.sp)
            Spacer(modifier = Modifier.height(4.dp))
            Text(title, color = Color.White, fontSize = 14.sp)
        }
    }
}

@Composable
fun InfoSectionBox(color: Color, titleHindi: String, titleEng: String, content: @Composable () -> Unit) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .border(1.dp, color.copy(alpha = 0.6f), RoundedCornerShape(16.dp))
            .background(color.copy(alpha = 0.1f), RoundedCornerShape(16.dp))
            .padding(16.dp)
    ) {
        Column {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(modifier = Modifier.size(40.dp).background(color.copy(alpha = 0.2f), CircleShape), contentAlignment = Alignment.Center) {
                    Icon(if (color == RuralRed) Icons.Default.Phone else Icons.Default.LocationOn, contentDescription = null, tint = color)
                }
                Spacer(modifier = Modifier.width(12.dp))
                Column {
                    Text(titleHindi, fontSize = 18.sp, fontWeight = FontWeight.Bold, color = Color.White)
                    Text(titleEng, fontSize = 12.sp, color = color)
                }
            }
            Spacer(modifier = Modifier.height(12.dp))
            Box(modifier = Modifier.background(Color.Black.copy(alpha = 0.3f), RoundedCornerShape(8.dp)).padding(12.dp).fillMaxWidth()) {
                Column {
                    content()
                }
            }
        }
    }
}

@Composable
fun InfoRow(icon: ImageVector, title: String, subtitle: String) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Icon(icon, contentDescription = null, tint = Color.Gray, modifier = Modifier.size(20.dp))
        Spacer(modifier = Modifier.width(12.dp))
        Column {
            Text(title, fontSize = 14.sp, color = Color.White)
            Text(subtitle, fontSize = 12.sp, color = Color(0xFFB0BEC5))
        }
    }
}

@Composable
fun OfflineCard(titleHindi: String, titleEng: String, icon: ImageVector, color: Color, modifier: Modifier) {
    Box(
        modifier = modifier
            .height(90.dp)
            .clip(RoundedCornerShape(12.dp))
            .background(Color(0xFF3E2723))
            .border(1.dp, color.copy(alpha = 0.5f), RoundedCornerShape(12.dp))
            .padding(8.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Icon(icon, contentDescription = null, tint = color)
            Spacer(modifier = Modifier.height(8.dp))
            Text(titleHindi, fontSize = 13.sp, color = Color.White, fontWeight = FontWeight.Bold)
            Text(titleEng, fontSize = 10.sp, color = Color.Gray)
        }
    }
}