package com.healthai.app.ui.screens.doctor

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
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
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.healthai.app.R
import com.healthai.app.ui.screens.dashboard.HelpixBottomNav // Import Bottom Nav

// --- Mock Data ---
data class Doctor(
    val name: String,
    val specialty: String,
    val experience: String,
    val rating: String,
    val reviews: String,
    val time: String,
    val distance: String,
    val fee: String,
    val imageColor: Color // Placeholder color
)

val doctorsList = listOf(
    Doctor("Dr. Sarah Johnson", "General Physician", "15 years experience", "4.9", "(234)", "Today, 2:00 PM", "2.5 km away", "₹500", Color(0xFF42A5F5)),
    Doctor("Dr. Michael Chen", "Pulmonologist", "12 years experience", "4.8", "(189)", "Tomorrow, 10:00 AM", "3.8 km away", "₹800", Color(0xFF66BB6A)),
    Doctor("Dr. Priya Sharma", "Dermatologist", "10 years experience", "4.9", "(312)", "Today, 5:00 PM", "1.2 km away", "₹600", Color(0xFFAB47BC)),
    Doctor("Dr. Rajesh Kumar", "Infectious Disease", "18 years experience", "4.7", "(156)", "Today, 4:00 PM", "5.0 km away", "₹900", Color(0xFFFF7043))
)

@Composable
fun DoctorsScreen(navController: NavController) {
    Scaffold(
        bottomBar = { HelpixBottomNav(navController = navController) }, // Bottom Navigation
        containerColor = Color(0xFF0A0E17) // Dark Background
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .background(
                    brush = Brush.verticalGradient(
                        colors = listOf(Color(0xFF0F172A), Color(0xFF020617))
                    )
                )
        ) {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 16.dp),
                contentPadding = PaddingValues(bottom = 100.dp)
            ) {
                // Header
                item {
                    Spacer(modifier = Modifier.height(20.dp))
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back", tint = Color.White, modifier = Modifier.size(24.dp))
                        Spacer(modifier = Modifier.width(16.dp))
                        Column {
                            Text("Find a Doctor", fontSize = 24.sp, fontWeight = FontWeight.Bold, color = Color.White)
                            Text("Book appointments with top healthcare professionals", fontSize = 12.sp, color = Color.Gray)
                        }
                    }
                    Spacer(modifier = Modifier.height(24.dp))
                }

                // Specialty Filter
                item {
                    Text("Select Specialty", fontSize = 16.sp, fontWeight = FontWeight.SemiBold, color = Color(0xFF00E5FF))
                    Spacer(modifier = Modifier.height(12.dp))
                    SpecialtyRow()
                    Spacer(modifier = Modifier.height(24.dp))
                }

                // Doctors List
                items(doctorsList) { doctor ->
                    DoctorCard(doctor, navController)
                    Spacer(modifier = Modifier.height(16.dp))
                }

                // Bottom Stats
                item {
                    Spacer(modifier = Modifier.height(16.dp))
                    StatsRow()
                    Spacer(modifier = Modifier.height(20.dp))
                }
            }
        }
    }
}

// --- Components ---

@Composable
fun SpecialtyRow() {
    val specialties = listOf(
        Triple("General", Icons.Default.MedicalServices, Color(0xFF00BCD4)),
        Triple("Cardiology", Icons.Default.Favorite, Color(0xFFE91E63)),
        Triple("Dermatology", Icons.Default.Face, Color(0xFFE040FB)),
        Triple("Pediatrics", Icons.Default.ChildCare, Color(0xFFFFC107)),
        Triple("Neurology", Icons.Default.Psychology, Color(0xFF673AB7))
    )

    LazyRow(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
        items(specialties) { (name, icon, color) ->
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier
                    .width(100.dp)
                    .clip(RoundedCornerShape(16.dp))
                    .background(color.copy(alpha = 0.1f))
                    .border(1.dp, color.copy(alpha = 0.3f), RoundedCornerShape(16.dp))
                    .padding(12.dp)
            ) {
                Icon(icon, contentDescription = null, tint = color, modifier = Modifier.size(28.dp))
                Spacer(modifier = Modifier.height(8.dp))
                Text(name, color = Color.White, fontSize = 12.sp)
            }
        }
    }
}

@Composable
fun DoctorCard(doctor: Doctor, navController: NavController) {
    Card(
        colors = CardDefaults.cardColors(containerColor = Color(0xFF1E293B)),
        shape = RoundedCornerShape(16.dp),
        modifier = Modifier.fillMaxWidth().border(1.dp, Color.White.copy(alpha = 0.05f), RoundedCornerShape(16.dp))
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            // Top Row: Image & Info
            Row(verticalAlignment = Alignment.Top) {
                // Placeholder Image
                Box(
                    modifier = Modifier
                        .size(60.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(doctor.imageColor),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(Icons.Default.Person, contentDescription = null, tint = Color.White)
                }
                
                Spacer(modifier = Modifier.width(16.dp))
                
                Column(modifier = Modifier.weight(1f)) {
                    Text(doctor.name, color = Color.White, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                    Text(doctor.specialty, color = Color(0xFF94A3B8), fontSize = 13.sp)
                    Text(doctor.experience, color = Color(0xFF64748B), fontSize = 12.sp)
                }

                // Rating Badge
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(8.dp))
                        .background(Color(0xFF332D00)) // Dark Gold
                        .border(1.dp, Color(0xFFFFC107).copy(alpha = 0.5f), RoundedCornerShape(8.dp))
                        .padding(horizontal = 8.dp, vertical = 4.dp)
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.Star, contentDescription = null, tint = Color(0xFFFFC107), modifier = Modifier.size(12.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(doctor.rating, color = Color(0xFFFFC107), fontSize = 12.sp, fontWeight = FontWeight.Bold)
                        Text(" ${doctor.reviews}", color = Color.Gray, fontSize = 10.sp)
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Details Row (Time, Location, Fee)
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                DetailItem(Icons.Default.Schedule, doctor.time, Color(0xFF00E676))
                DetailItem(Icons.Default.LocationOn, doctor.distance, Color(0xFF2979FF))
                Text("Fee: ${doctor.fee}", color = Color(0xFF00E5FF), fontSize = 13.sp, fontWeight = FontWeight.Bold)
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Action Buttons
            Row(modifier = Modifier.fillMaxWidth()) {
                Button(
                    onClick = { 
                        // Navigate to detail screen (reusing the one we made earlier)
                        navController.navigate("doctor_details") 
                    },
                    modifier = Modifier.weight(1f).height(45.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF00B0FF)),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Icon(Icons.Default.CalendarToday, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Book Appointment", fontSize = 13.sp)
                }
                
                Spacer(modifier = Modifier.width(8.dp))
                
                // Small Action Buttons
                ActionBtn(Icons.Default.Videocam, Color(0xFF00C853))
                Spacer(modifier = Modifier.width(8.dp))
                ActionBtn(Icons.Default.Call, Color(0xFF651FFF))
                Spacer(modifier = Modifier.width(8.dp))
                ActionBtn(Icons.Default.Chat, Color(0xFF2962FF))
            }
        }
    }
}

@Composable
fun DetailItem(icon: ImageVector, text: String, color: Color) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Icon(icon, contentDescription = null, tint = color, modifier = Modifier.size(14.dp))
        Spacer(modifier = Modifier.width(4.dp))
        Text(text, color = Color(0xFF94A3B8), fontSize = 12.sp)
    }
}

@Composable
fun ActionBtn(icon: ImageVector, color: Color) {
    Box(
        modifier = Modifier
            .size(45.dp)
            .clip(RoundedCornerShape(12.dp))
            .background(color.copy(alpha = 0.15f))
            .border(1.dp, color.copy(alpha = 0.3f), RoundedCornerShape(12.dp)),
        contentAlignment = Alignment.Center
    ) {
        Icon(icon, contentDescription = null, tint = color, modifier = Modifier.size(20.dp))
    }
}

@Composable
fun StatsRow() {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        StatBox("500+", "Doctors Available", Color(0xFF2979FF))
        StatBox("98%", "Patient Satisfaction", Color(0xFF00C853))
        StatBox("24/7", "Support Available", Color(0xFFD500F9))
    }
}

@Composable
fun RowScope.StatBox(value: String, label: String, color: Color) {
    Column(
        modifier = Modifier
            .weight(1f)
            .padding(4.dp)
            .clip(RoundedCornerShape(12.dp))
            .background(Color(0xFF1E293B).copy(alpha = 0.5f))
            .border(1.dp, Color.White.copy(alpha = 0.05f), RoundedCornerShape(12.dp))
            .padding(vertical = 16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(value, fontSize = 20.sp, fontWeight = FontWeight.Bold, color = color)
        Text(label, fontSize = 10.sp, color = Color.Gray, modifier = Modifier.padding(top = 4.dp))
    }
}