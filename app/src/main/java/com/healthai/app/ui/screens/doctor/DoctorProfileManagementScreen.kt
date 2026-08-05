package com.healthai.app.ui.screens.doctor

import android.widget.Toast
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
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
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.healthai.app.ui.screens.profile.MedicalCard
import com.healthai.app.ui.screens.profile.ProfileSectionHeader
import com.healthai.app.ui.screens.profile.ProfileTextField

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DoctorProfileManagementScreen(
    navController: NavController,
    viewModel: DoctorDashboardViewModel = hiltViewModel()
) {
    val doctorProfile by viewModel.doctorProfile.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val context = LocalContext.current

    var spec by remember { mutableStateOf("") }
    var license by remember { mutableStateOf("") }
    var clinic by remember { mutableStateOf("") }
    var experience by remember { mutableStateOf("") }

    LaunchedEffect(doctorProfile) {
        doctorProfile?.let {
            spec = it.specialization ?: ""
            license = it.licenseNumber ?: ""
            clinic = it.clinicAddress ?: ""
            experience = it.experienceYears?.toString() ?: ""
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Professional Profile", color = Color.White, fontWeight = FontWeight.ExtraBold) },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", tint = Color.White)
                    }
                },
                actions = {
                    if (isLoading) {
                        CircularProgressIndicator(modifier = Modifier.size(24.dp), color = MedicalEmerald)
                    } else {
                        TextButton(onClick = {
                            if (spec.isBlank() || license.isBlank() || clinic.isBlank()) {
                                Toast.makeText(context, "Please fill all professional details", Toast.LENGTH_SHORT).show()
                            } else {
                                viewModel.updateProfessionalProfile(spec, license, clinic, experience.toIntOrNull()) { success ->
                                    if (success) {
                                        Toast.makeText(context, "Profile Verified Successfully", Toast.LENGTH_SHORT).show()
                                        // Navigate to Dashboard and clear this screen from stack
                                        navController.navigate(com.healthai.app.ui.navigation.NavRoutes.DoctorDashboard) {
                                            popUpTo(com.healthai.app.ui.navigation.NavRoutes.DoctorProfileManagement) { inclusive = true }
                                        }
                                    } else {
                                        Toast.makeText(context, "Update Failed", Toast.LENGTH_SHORT).show()
                                    }
                                }
                            }
                        }) {
                            Text("SAVE", color = MedicalEmerald, fontWeight = FontWeight.Bold)
                        }
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
                .verticalScroll(rememberScrollState())
                .padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Header: Professional Badge
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(140.dp)
                    .clip(RoundedCornerShape(24.dp))
                    .background(ProfessionalIndigo.copy(alpha = 0.1f))
                    .border(1.dp, ProfessionalIndigo.copy(alpha = 0.2f), RoundedCornerShape(24.dp)),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Box(
                        modifier = Modifier
                            .size(64.dp)
                            .background(
                                if (doctorProfile?.userType == "DOCTOR" || !doctorProfile?.licenseNumber.isNullOrBlank()) MedicalEmerald.copy(alpha = 0.2f)
                                else Color.Gray.copy(alpha = 0.2f),
                                CircleShape
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            if (doctorProfile?.userType == "DOCTOR" || !doctorProfile?.licenseNumber.isNullOrBlank()) Icons.Default.Verified else Icons.Default.NewReleases,
                            contentDescription = null,
                            tint = if (doctorProfile?.userType == "DOCTOR" || !doctorProfile?.licenseNumber.isNullOrBlank()) MedicalEmerald else Color.Gray,
                            modifier = Modifier.size(32.dp)
                        )
                    }
                    Spacer(modifier = Modifier.height(12.dp))
                    Text(
                        if (doctorProfile?.userType == "DOCTOR" || !doctorProfile?.licenseNumber.isNullOrBlank()) "Verified Professional" else "Verification Pending",
                        color = if (doctorProfile?.userType == "DOCTOR" || !doctorProfile?.licenseNumber.isNullOrBlank()) MedicalEmerald else Color.Gray,
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp
                    )
                }
            }

            ProfileSectionHeader("Clinic Credentials")
            MedicalCard(backgroundColor = DocCardBg) {
                ProfileTextField("Specialization", spec, { spec = it }, true, Icons.Default.MedicalServices)
                ProfileTextField("License Number", license, { license = it }, true, Icons.Default.Badge)
                ProfileTextField("Years of Experience", experience, { experience = it }, true, Icons.Default.Timeline)
            }

            ProfileSectionHeader("Practice Location")
            MedicalCard(backgroundColor = DocCardBg) {
                ProfileTextField("Clinic Address", clinic, { clinic = it }, true, Icons.Default.Business)
            }

            if (doctorProfile?.userType == "DOCTOR") {
                ProfileSectionHeader("Consultation Settings")
                MedicalCard(backgroundColor = DocCardBg) {
                    DocManagementItem(Icons.Default.Schedule, "Manage Working Hours", "Mon - Sat • 09:00 - 18:00") {}
                    HorizontalDivider(color = Color.White.copy(alpha = 0.05f))
                    DocManagementItem(Icons.Default.Payments, "Consultation Fee", "₹800 per session") {}
                }
            }
            
            Spacer(modifier = Modifier.height(32.dp))
            
            Spacer(modifier = Modifier.height(20.dp))
        }
    }
}

@Composable
fun DocManagementItem(icon: ImageVector, title: String, subtitle: String, onClick: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .padding(vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier.size(40.dp).background(DocDeepSlate, CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Icon(icon, contentDescription = null, tint = ProfessionalIndigo, modifier = Modifier.size(20.dp))
        }
        Spacer(modifier = Modifier.width(16.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(title, color = Color.White, fontWeight = FontWeight.SemiBold, fontSize = 15.sp)
            Text(subtitle, color = DocTextGrey, fontSize = 12.sp)
        }
        Icon(Icons.Default.ChevronRight, contentDescription = null, tint = DocTextGrey, modifier = Modifier.size(18.dp))
    }
}
