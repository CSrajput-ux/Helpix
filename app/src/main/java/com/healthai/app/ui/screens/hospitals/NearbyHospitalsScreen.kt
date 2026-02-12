package com.healthai.app.ui.screens.hospitals

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material.icons.filled.LocalHospital
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController

data class Hospital(val name: String, val distance: String, val type: String, val specialty: String, val hasEmergency: Boolean)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NearbyHospitalsScreen(navController: NavController) {

    val hospitals = listOf(
        Hospital("City Central Hospital", "2.5 km", "Private", "Multi-specialty", true),
        Hospital("Government General Hospital", "3.1 km", "Government", "General Medicine", true),
        Hospital("Sunshine Children's Clinic", "4.2 km", "Private", "Pediatrics", false),
        Hospital("CardioCare Heart Institute", "5.0 km", "Private", "Cardiology", true)
    )

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Nearby Hospitals") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = {
                    IconButton(onClick = { /* TODO: Show filter dialog */ }) {
                        Icon(Icons.Default.FilterList, contentDescription = "Filter")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color(0xFF0B1221), titleContentColor = Color.White, navigationIconContentColor = Color.White, actionIconContentColor = Color.White)
            )
        },
        containerColor = Color(0xFF0B1221)
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            // Map placeholder
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(250.dp)
                    .background(Color(0xFF1E293B)),
                contentAlignment = Alignment.Center
            ) {
                Text("Map View Placeholder", color = Color.Gray)
            }

            LazyColumn(modifier = Modifier.padding(16.dp)) {
                items(hospitals) { hospital ->
                    HospitalListItem(hospital)
                    Spacer(modifier = Modifier.height(12.dp))
                }
            }
        }
    }
}

@Composable
fun HospitalListItem(hospital: Hospital) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFF1E293B))
    ) {
        Row(modifier = Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
            Icon(Icons.Default.LocalHospital, contentDescription = null, tint = Color(0xFFFF9100), modifier = Modifier.size(40.dp))
            Spacer(modifier = Modifier.width(16.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(hospital.name, color = Color.White, fontWeight = FontWeight.Bold)
                Text("${hospital.specialty} • ${hospital.distance}", color = Color.Gray, fontSize = 12.sp)
                Row(horizontalArrangement = Arrangement.spacedBy(4.dp), verticalAlignment = Alignment.CenterVertically) {
                    Badge(text = hospital.type, color = if(hospital.type == "Government") Color.Cyan else Color.LightGray)
                    if(hospital.hasEmergency){
                         Badge(text = "24/7 Emergency", color = Color(0xFFFF6B6B))
                    }
                }
            }
        }
    }
}

@Composable
fun Badge(text: String, color: Color) {
    Box(
        modifier = Modifier
            .background(color.copy(alpha = 0.1f), RoundedCornerShape(4.dp))
            .padding(horizontal = 6.dp, vertical = 2.dp)
    ) {
        Text(text, color = color, fontSize = 10.sp, fontWeight = FontWeight.SemiBold)
    }
}