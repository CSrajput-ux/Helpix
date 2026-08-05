package com.healthai.app.ui.screens.doctor

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.healthai.app.data.remote.api.PatientSummary

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DoctorPatientsScreen(
    navController: NavController,
    viewModel: DoctorPatientsViewModel = hiltViewModel()   // FIX #12b: Hilt-injected VM
) {
    val state by viewModel.patientsState.collectAsState()
    var searchQuery by remember { mutableStateOf("") }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("My Patients", color = Color.White, fontWeight = FontWeight.ExtraBold) },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", tint = Color.White)
                    }
                },
                actions = {
                    IconButton(onClick = { viewModel.loadPatients() }) {
                        Icon(Icons.Default.Refresh, contentDescription = "Refresh", tint = DocTextGrey)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = DocDeepSlate)
            )
        },
        containerColor = DocDeepSlate
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            // Search Bar
            OutlinedTextField(
                value = searchQuery,
                onValueChange = { searchQuery = it },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                placeholder = { Text("Search by name or email", color = DocTextGrey) },
                leadingIcon = { Icon(Icons.Default.Search, contentDescription = null, tint = MedicalEmerald) },
                shape = RoundedCornerShape(16.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = MedicalEmerald,
                    unfocusedBorderColor = Color.White.copy(alpha = 0.1f),
                    focusedContainerColor = DocCardBg,
                    unfocusedContainerColor = DocCardBg,
                    focusedTextColor = Color.White,
                    unfocusedTextColor = Color.White
                )
            )

            when (val s = state) {
                is DoctorPatientsUiState.Loading -> {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        CircularProgressIndicator(color = MedicalEmerald)
                    }
                }

                is DoctorPatientsUiState.Error -> {
                    Box(
                        modifier = Modifier.fillMaxSize().padding(16.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Icon(
                                Icons.Default.ErrorOutline,
                                contentDescription = null,
                                tint = Color(0xFFEF4444),
                                modifier = Modifier.size(48.dp)
                            )
                            Spacer(modifier = Modifier.height(12.dp))
                            Text(s.message, color = DocTextGrey, fontSize = 14.sp)
                            Spacer(modifier = Modifier.height(16.dp))
                            Button(
                                onClick = { viewModel.loadPatients() },
                                colors = ButtonDefaults.buttonColors(containerColor = MedicalEmerald)
                            ) {
                                Text("Retry", color = Color.White)
                            }
                        }
                    }
                }

                is DoctorPatientsUiState.Success -> {
                    val filtered = s.patients.filter { patient ->
                        searchQuery.isBlank() ||
                            patient.full_name.contains(searchQuery, ignoreCase = true) ||
                            patient.email.contains(searchQuery, ignoreCase = true)
                    }

                    LazyColumn(
                        contentPadding = PaddingValues(16.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        if (filtered.isEmpty()) {
                            item {
                                Box(
                                    modifier = Modifier.fillMaxWidth().padding(top = 80.dp),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                        Icon(
                                            Icons.Default.PersonOff,
                                            contentDescription = null,
                                            tint = DocTextGrey,
                                            modifier = Modifier.size(48.dp)
                                        )
                                        Spacer(modifier = Modifier.height(12.dp))
                                        Text(
                                            if (searchQuery.isBlank()) "No patients assigned yet"
                                            else "No results for \"$searchQuery\"",
                                            color = DocTextGrey,
                                            fontSize = 14.sp
                                        )
                                    }
                                }
                            }
                        } else {
                            items(filtered, key = { it.patient_id }) { patient ->
                                BackendPatientCard(
                                    patient = patient,
                                    onUnfollow = { viewModel.unfollowPatient(patient.patient_id) }
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun BackendPatientCard(
    patient: PatientSummary,
    onUnfollow: () -> Unit
) {
    val avatarColors = listOf(MedicalEmerald, ProfessionalIndigo, Color(0xFFF59E0B), Color(0xFFEC4899))
    val color = avatarColors[(patient.full_name.hashCode() and 0x7FFFFFFF) % avatarColors.size]

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = DocCardBg),
        border = androidx.compose.foundation.BorderStroke(1.dp, Color.White.copy(alpha = 0.05f))
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(50.dp)
                    .clip(CircleShape)
                    .background(color.copy(alpha = 0.15f)),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    patient.full_name.firstOrNull()?.uppercase() ?: "?",
                    color = color,
                    fontWeight = FontWeight.Bold,
                    fontSize = 20.sp
                )
            }
            Spacer(modifier = Modifier.width(16.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(patient.full_name, color = Color.White, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                Text(patient.email, color = DocTextGrey, fontSize = 12.sp)
                Spacer(modifier = Modifier.height(4.dp))
                Text("Linked • ${patient.linked_at.take(10)}", color = color, fontSize = 11.sp, fontWeight = FontWeight.Medium)
            }
            IconButton(onClick = onUnfollow) {
                Icon(Icons.Default.PersonRemove, contentDescription = "Unfollow", tint = Color(0xFFEF4444), modifier = Modifier.size(20.dp))
            }
        }
    }
}

// Legacy support — kept so other code referencing PatientItem still compiles
data class PatientItem(val name: String, val info: String, val diagnosis: String, val color: Color)

@Composable
fun AestheticPatientCard(patient: PatientItem) {
    BackendPatientCard(
        patient = PatientSummary(
            patient_id = patient.name,
            full_name = patient.name,
            email = patient.info,
            linked_at = ""
        ),
        onUnfollow = {}
    )
}
