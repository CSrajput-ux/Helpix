package com.healthai.app.ui.screens.doctor

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
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
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.healthai.app.R
import com.healthai.app.domain.model.User
import com.healthai.app.ui.navigation.NavRoutes
import com.healthai.app.ui.screens.dashboard.HelpixBottomNav

@Composable
fun DoctorsScreen(
    navController: NavController,
    viewModel: DoctorListViewModel = viewModel()
) {
    val doctors by viewModel.doctors.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()

    // Group doctors by specialty
    val groupedDoctors = remember(doctors) {
        doctors.groupBy { it.specialization ?: "General Physician" }
    }

    LaunchedEffect(Unit) {
        viewModel.fetchDoctors()
    }

    Scaffold(
        bottomBar = { HelpixBottomNav(navController = navController) },
        containerColor = Color(0xFF0A0E17)
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
            Column(modifier = Modifier.fillMaxSize()) {
                LazyColumn(
                    modifier = Modifier.weight(1f),
                    contentPadding = PaddingValues(bottom = 16.dp)
                ) {
                    // Header
                    item {
                        Spacer(modifier = Modifier.height(20.dp))
                        Row(
                            modifier = Modifier.padding(horizontal = 16.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            IconButton(onClick = { navController.popBackStack() }) {
                                Icon(Icons.Default.ArrowBack, contentDescription = "Back", tint = Color.White)
                            }
                            Spacer(modifier = Modifier.width(8.dp))
                            Column {
                                Text("Book Appointment", fontSize = 24.sp, fontWeight = FontWeight.Bold, color = Color.White)
                                Text("Consult with our expert doctors", fontSize = 12.sp, color = Color.Gray)
                            }
                        }
                        Spacer(modifier = Modifier.height(24.dp))
                    }

                    if (isLoading) {
                        item {
                            Box(modifier = Modifier.fillMaxWidth().height(200.dp), contentAlignment = Alignment.Center) {
                                CircularProgressIndicator(color = colorResource(id = R.color.logo_cyan))
                            }
                        }
                    } else if (doctors.isEmpty()) {
                        item {
                            Box(modifier = Modifier.fillMaxWidth().height(200.dp), contentAlignment = Alignment.Center) {
                                Text("No doctors available at the moment.", color = Color.Gray)
                            }
                        }
                    } else {
                        // Category-wise Sections based on actual data from database/backend
                        groupedDoctors.forEach { (specialtyName, doctorsInCategory) ->
                            val (icon, color) = getSpecialtyInfo(specialtyName)
                            
                            item {
                                SpecialtyHeader(specialtyName, icon, color)
                            }
                            
                            item {
                                LazyRow(
                                    contentPadding = PaddingValues(horizontal = 16.dp),
                                    horizontalArrangement = Arrangement.spacedBy(16.dp),
                                    modifier = Modifier.padding(bottom = 24.dp)
                                ) {
                                    items(doctorsInCategory) { doctor ->
                                        DoctorSectionCard(doctor, color, navController)
                                    }
                                }
                            }
                        }
                    }
                }

                // Stats Section FIXED at the bottom
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    StatItem("500+", "Available", Color(0xFF2979FF), Modifier.weight(1f))
                    StatItem("98%", "Rating", Color(0xFF00C853), Modifier.weight(1f))
                    StatItem("24/7", "Support", Color(0xFFD500F9), Modifier.weight(1f))
                }
            }
        }
    }
}

fun getSpecialtyInfo(specialty: String): Pair<ImageVector, Color> {
    return when (specialty.lowercase()) {
        "cardiologist" -> Icons.Default.Favorite to Color(0xFFE91E63)
        "pulmonologist" -> Icons.Default.Air to Color(0xFF4CAF50)
        "dermatologist" -> Icons.Default.Face to Color(0xFFE040FB)
        "neurologist" -> Icons.Default.Psychology to Color(0xFF673AB7)
        "pediatrician" -> Icons.Default.ChildCare to Color(0xFFFFC107)
        "dentist" -> Icons.Default.MedicalServices to Color(0xFF00BCD4)
        else -> Icons.Default.MedicalServices to Color(0xFF00E5FF)
    }
}

@Composable
fun SpecialtyHeader(name: String, icon: ImageVector, color: Color) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(icon, contentDescription = null, tint = color, modifier = Modifier.size(20.dp))
        Spacer(modifier = Modifier.width(8.dp))
        Text(name, color = Color.White, fontSize = 18.sp, fontWeight = FontWeight.Bold)
        Spacer(modifier = Modifier.weight(1f))
        Text("See All", color = color, fontSize = 12.sp, modifier = Modifier.clickable { })
    }
}

@Composable
fun DoctorSectionCard(doctor: User, themeColor: Color, navController: NavController) {
    Card(
        modifier = Modifier
            .width(280.dp)
            .clickable { navController.navigate(NavRoutes.DoctorDetails) },
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFF1E293B)),
        border = BorderStroke(1.dp, Color.White.copy(alpha = 0.05f))
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(50.dp)
                        .clip(CircleShape)
                        .background(themeColor.copy(alpha = 0.1f))
                        .border(1.dp, themeColor.copy(alpha = 0.3f), CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(Icons.Default.Person, contentDescription = null, tint = themeColor, modifier = Modifier.size(30.dp))
                }
                Spacer(modifier = Modifier.width(12.dp))
                Column {
                    Text(doctor.name, color = Color.White, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                    Text(doctor.specialization ?: "Specialist", color = Color.Gray, fontSize = 12.sp)
                }
            }
            
            Spacer(modifier = Modifier.height(16.dp))
            
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.Star, contentDescription = null, tint = Color(0xFFFFC107), modifier = Modifier.size(14.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("4.9 (120+ reviews)", color = Color.Gray, fontSize = 11.sp)
                }
                Text("₹600", color = colorResource(id = R.color.logo_cyan), fontWeight = FontWeight.Bold, fontSize = 14.sp)
            }
            
            Spacer(modifier = Modifier.height(16.dp))
            
            Button(
                onClick = { navController.navigate(NavRoutes.DoctorDetails) },
                modifier = Modifier.fillMaxWidth().height(40.dp),
                colors = ButtonDefaults.buttonColors(containerColor = themeColor.copy(alpha = 0.2f)),
                shape = RoundedCornerShape(10.dp),
                border = BorderStroke(1.dp, themeColor.copy(alpha = 0.5f))
            ) {
                Text("Book Now", color = Color.White, fontSize = 13.sp)
            }
        }
    }
}

@Composable
fun StatItem(value: String, label: String, color: Color, modifier: Modifier) {
    Column(
        modifier = modifier
            .background(Color(0xFF1E293B).copy(alpha = 0.5f), RoundedCornerShape(12.dp))
            .border(1.dp, Color.White.copy(alpha = 0.05f), RoundedCornerShape(12.dp))
            .padding(12.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(value, color = color, fontSize = 18.sp, fontWeight = FontWeight.Bold)
        Text(label, color = Color.Gray, fontSize = 10.sp)
    }
}
