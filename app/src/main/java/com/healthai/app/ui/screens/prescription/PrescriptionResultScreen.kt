package com.healthai.app.ui.screens.prescription

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.healthai.app.R // Assume a placeholder image is in drawables

data class ExtractedMedicine(val name: String, val dosage: String, val duration: String)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PrescriptionResultScreen(navController: NavController) {

    // Dummy data for preview
    val extractedMedicines = remember {
        mutableStateListOf(
            ExtractedMedicine("Paracetamol", "1-0-1", "5 Days"),
            ExtractedMedicine("Azithromycin", "1-1-1", "3 Days")
        )
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Verify Prescription Details") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color(0xFF0B1221), titleContentColor = Color.White, navigationIconContentColor = Color.White)
            )
        },
        containerColor = Color(0xFF0B1221)
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp)
        ) {
            // Scanned Image Preview
            Image(
                painter = painterResource(id = R.drawable.ic_launcher_background), // TODO: Replace with actual scanned image
                contentDescription = "Scanned Prescription",
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp)
                    .clip(RoundedCornerShape(16.dp))
            )
            Spacer(modifier = Modifier.height(16.dp))
            Text("Please verify the details below and edit if needed:", color = Color.Gray, fontSize = 12.sp)
            Spacer(modifier = Modifier.height(16.dp))

            LazyColumn(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                items(extractedMedicines) { medicine ->
                    MedicineVerificationCard(medicine)
                }
            }

            Spacer(modifier = Modifier.weight(1f))

            Button(onClick = { /* TODO: Navigate to Medicine Reminders with this data */ }) {
                Text("Set Reminders for these Medicines")
            }
        }
    }
}

@Composable
fun MedicineVerificationCard(medicine: ExtractedMedicine) {
    var medName by remember { mutableStateOf(medicine.name) }
    var dosage by remember { mutableStateOf(medicine.dosage) }
    var duration by remember { mutableStateOf(medicine.duration) }

    Card(
        colors = CardDefaults.cardColors(containerColor = Color(0xFF1E293B)),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
            OutlinedTextField(
                value = medName,
                onValueChange = { medName = it },
                label = { Text("Medicine Name") },
                modifier = Modifier.fillMaxWidth()
            )
            OutlinedTextField(
                value = dosage,
                onValueChange = { dosage = it },
                label = { Text("Dosage (e.g., 1-0-1)") },
                modifier = Modifier.fillMaxWidth()
            )
            OutlinedTextField(
                value = duration,
                onValueChange = { duration = it },
                label = { Text("Duration (e.g., 5 Days)") },
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}
