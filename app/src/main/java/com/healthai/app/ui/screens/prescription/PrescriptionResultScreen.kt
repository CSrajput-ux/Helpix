package com.healthai.app.ui.screens.prescription

import android.net.Uri
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
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
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import coil.compose.rememberAsyncImagePainter
import com.healthai.app.ml.GeminiPrescriptionAnalyzer.MedicineEntry
import com.healthai.app.ui.navigation.NavRoutes
import java.io.File

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PrescriptionResultScreen(navController: NavController) {

    val result = PrescriptionResultHolder.result
    val error = PrescriptionResultHolder.error
    val imagePath = PrescriptionResultHolder.imagePath

    // If there is an error, show it
    if (error != null) {
        Scaffold(
            topBar = {
                TopAppBar(
                    title = { Text("Analysis Failed") },
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
                    .padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Text("❌", fontSize = 48.sp)
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    text = "Could not read prescription",
                    color = Color.White,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = error,
                    color = Color.Gray,
                    textAlign = androidx.compose.ui.text.style.TextAlign.Center
                )
            }
        }
        return
    }

    // Mutable state list so users can edit the parsed result before saving/setting reminders
    val extractedMedicines = remember {
        mutableStateListOf<MedicineEntry>().apply {
            result?.medicines?.let { addAll(it) }
        }
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
            if (imagePath != null) {
                Image(
                    painter = rememberAsyncImagePainter(File(imagePath)),
                    contentDescription = "Scanned Prescription",
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(200.dp)
                        .clip(RoundedCornerShape(16.dp))
                        .background(Color(0xFF1E293B)),
                    contentScale = ContentScale.Crop
                )
                Spacer(modifier = Modifier.height(16.dp))
            }

            // Doctor & Diagnosis Info
            if (result != null) {
                Card(
                    colors = CardDefaults.cardColors(containerColor = Color(0xFF1E293B)),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text("Doctor: ${result.doctorInfo.name ?: "N/A"}", color = Color.White, fontWeight = FontWeight.Bold)
                        Text("Diagnosis: ${result.diagnosisOrSymptoms.joinToString()}", color = Color.Gray, fontSize = 13.sp)
                    }
                }
                Spacer(modifier = Modifier.height(16.dp))
            }

            Text("Please verify the medicines below and edit if needed:", color = Color.Gray, fontSize = 12.sp)
            Spacer(modifier = Modifier.height(16.dp))

            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(16.dp),
                modifier = Modifier.weight(1f)
            ) {
                items(extractedMedicines.size) { index ->
                    MedicineVerificationCard(
                        medicine = extractedMedicines[index],
                        onUpdate = { updatedMedicine ->
                            extractedMedicines[index] = updatedMedicine
                        }
                    )
                }
                
                item {
                    if (result?.disclaimer != null) {
                        Text(
                            text = result.disclaimer,
                            color = Color(0xFFFFA000),
                            fontSize = 11.sp,
                            modifier = Modifier.padding(vertical = 16.dp)
                        )
                    }
                }
            }

            Button(
                onClick = { 
                    // TODO: Map extractedMedicines to Reminder entities and save to DB
                    navController.navigate(NavRoutes.MedicineReminders) 
                },
                modifier = Modifier.fillMaxWidth().height(50.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF2962FF))
            ) {
                Text("Set Reminders for these Medicines", fontWeight = FontWeight.Bold)
            }
        }
    }
}

@Composable
fun MedicineVerificationCard(medicine: MedicineEntry, onUpdate: (MedicineEntry) -> Unit) {
    var medName by remember { mutableStateOf(medicine.name) }
    var dosage by remember { mutableStateOf(medicine.dosage) }
    var frequency by remember { mutableStateOf(medicine.frequency) }
    var duration by remember { mutableStateOf(medicine.duration) }

    Card(
        colors = CardDefaults.cardColors(containerColor = Color(0xFF1E293B)),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
            // Show low confidence warning if needed
            if (medicine.confidence.lowercase() == "low") {
                Text("⚠️ Low Confidence - Please Verify", color = Color(0xFFFF6B6B), fontSize = 12.sp, fontWeight = FontWeight.Bold)
            }

            OutlinedTextField(
                value = medName,
                onValueChange = { 
                    medName = it
                    onUpdate(medicine.copy(name = it))
                },
                label = { Text("Medicine Name") },
                modifier = Modifier.fillMaxWidth()
            )
            
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedTextField(
                    value = dosage,
                    onValueChange = { 
                        dosage = it
                        onUpdate(medicine.copy(dosage = it))
                    },
                    label = { Text("Dosage") },
                    modifier = Modifier.weight(1f)
                )
                OutlinedTextField(
                    value = frequency,
                    onValueChange = { 
                        frequency = it
                        onUpdate(medicine.copy(frequency = it))
                    },
                    label = { Text("Frequency") },
                    modifier = Modifier.weight(1f)
                )
            }
            
            OutlinedTextField(
                value = duration,
                onValueChange = { 
                    duration = it
                    onUpdate(medicine.copy(duration = it))
                },
                label = { Text("Duration (e.g., 5 Days)") },
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}
