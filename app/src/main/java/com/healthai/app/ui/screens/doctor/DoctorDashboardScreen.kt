package com.healthai.app.ui.screens.doctor

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.PlaylistPlay
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.healthai.app.R
import com.healthai.app.ui.navigation.NavRoutes
import com.healthai.app.ui.screens.dashboard.HelpixBottomNav

// Professional Doctor Hub Palette
val DocDeepSlate = Color(0xFF020617)
val MedicalEmerald = Color(0xFF10B981)
val ProfessionalIndigo = Color(0xFF6366F1)
val DocCardBg = Color(0xFF1E293B)
val DocTextGrey = Color(0xFF94A3B8)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DoctorDashboardScreen(
    navController: NavController,
    viewModel: DoctorDashboardViewModel = hiltViewModel()
) {
    val doctorProfile by viewModel.doctorProfile.collectAsState()
    val appointments by viewModel.appointments.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    
    // Refresh dashboard whenever this screen comes into focus
    LaunchedEffect(Unit) {
        viewModel.loadDashboard()
    }
    val todayAppointmentsCount = remember(appointments) {
        val today = java.util.Calendar.getInstance()
        appointments.count { appt ->
            val apptCal = java.util.Calendar.getInstance().apply { time = appt.appointmentDate ?: java.util.Date(0) }
            apptCal.get(java.util.Calendar.YEAR) == today.get(java.util.Calendar.YEAR) &&
            apptCal.get(java.util.Calendar.DAY_OF_YEAR) == today.get(java.util.Calendar.DAY_OF_YEAR)
        }
    }

    Scaffold(
        bottomBar = {
            HelpixBottomNav(navController = navController, userType = "DOCTOR")
        },
        containerColor = DocDeepSlate
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            DocGridBackground()

            if (isLoading && doctorProfile == null) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator(color = MedicalEmerald)
                }
            } else if (doctorProfile?.userType != "DOCTOR" && doctorProfile?.licenseNumber.isNullOrBlank()) {
                // Verification Screen for non-doctors (only show if role is not doctor AND no license number is set)
                DoctorVerificationPlaceholder(navController)
            } else {
                // Actual Dashboard
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(20.dp)
                ) {
                    DoctorTopBarSection(navController)
                    Spacer(modifier = Modifier.height(24.dp))

                    // 1. Practice Overview Card (Redesigned)
                    PracticeOverviewCard(navController, doctorProfile)
                    
                    Spacer(modifier = Modifier.height(24.dp))

                    // 2. Wallet & Metrics Row
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                        DocMetricCard("Active Patients", "124", Icons.Default.Groups, ProfessionalIndigo, Modifier.weight(1f))
                        DocMetricCard("Today's Appts", "12", Icons.Default.EventAvailable, MedicalEmerald, Modifier.weight(1f))
                    }

                    Spacer(modifier = Modifier.height(24.dp))

                    // 3. Main Action Cards
                    Text(
                        text = "Clinic Management",
                        color = Color.White,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(bottom = 12.dp)
                    )

                    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                        DocActionCard(
                            title = "Queue Manager",
                            subtitle = "8 Patients in waiting room",
                            icon = Icons.AutoMirrored.Filled.PlaylistPlay,
                            accentColor = MedicalEmerald,
                            onClick = { /* Navigate to Queue */ }
                        )
                        DocActionCard(
                            title = "E-Prescriptions",
                            subtitle = "Create & share digital Rx",
                            icon = Icons.Default.Description,
                            accentColor = ProfessionalIndigo,
                            onClick = { navController.navigate(NavRoutes.PrescriptionReader) }
                        )
                    }

                    Spacer(modifier = Modifier.height(24.dp))

                    // 4. Quick Services Grid
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                        DocServiceItem("Schedule", Icons.Default.CalendarMonth, Modifier.weight(1f)) {
                            navController.navigate(NavRoutes.DoctorSchedule)
                        }
                        DocServiceItem("Patients", Icons.Default.Badge, Modifier.weight(1f)) {
                            navController.navigate(NavRoutes.DoctorPatients)
                        }
                        DocServiceItem("Records", Icons.Default.Inventory, Modifier.weight(1f)) {
                            navController.navigate(NavRoutes.HealthVault)
                        }
                        DocServiceItem("Vault", Icons.Default.AccountBalanceWallet, Modifier.weight(1f)) {
                            navController.navigate(NavRoutes.DoctorVault)
                        }
                    }
                    
                    Spacer(modifier = Modifier.weight(1f))
                    
                    // Floating Action (Centered Bottom)
                    Button(
                        onClick = { navController.navigate(NavRoutes.SkinDetectorStart) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(56.dp)
                            .shadow(16.dp, RoundedCornerShape(16.dp)),
                        colors = ButtonDefaults.buttonColors(containerColor = ProfessionalIndigo),
                        shape = RoundedCornerShape(16.dp)
                    ) {
                        Icon(Icons.Default.AutoGraph, contentDescription = null)
                        Spacer(modifier = Modifier.width(12.dp))
                        Text("Launch AI Clinical Analysis", fontWeight = FontWeight.Bold)
                    }
                    Spacer(modifier = Modifier.height(16.dp))
                }
            }
        }
    }
}

@Composable
fun DoctorVerificationPlaceholder(navController: NavController) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Box(
            modifier = Modifier
                .size(100.dp)
                .background(MedicalEmerald.copy(alpha = 0.1f), CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                Icons.Default.Security,
                contentDescription = null,
                tint = MedicalEmerald,
                modifier = Modifier.size(48.dp)
            )
        }
        
        Spacer(modifier = Modifier.height(24.dp))
        
        Text(
            "Doctor Verification Required",
            color = Color.White,
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center
        )
        
        Spacer(modifier = Modifier.height(12.dp))
        
        Text(
            "To access the Doctor Hub and manage patients, you must verify your professional credentials.",
            color = DocTextGrey,
            fontSize = 14.sp,
            textAlign = TextAlign.Center,
            lineHeight = 20.sp
        )
        
        Spacer(modifier = Modifier.height(40.dp))
        
        Button(
            onClick = { navController.navigate(NavRoutes.DoctorProfileManagement) },
            modifier = Modifier.fillMaxWidth().height(56.dp),
            colors = ButtonDefaults.buttonColors(containerColor = MedicalEmerald),
            shape = RoundedCornerShape(16.dp)
        ) {
            Text("Complete Professional Profile", color = DocDeepSlate, fontWeight = FontWeight.Bold)
        }
        
        Spacer(modifier = Modifier.height(16.dp))
        
        TextButton(onClick = { navController.popBackStack() }) {
            Text("Go Back to Patient Profile", color = MedicalEmerald)
        }
    }
}

@Composable
fun DoctorTopBarSection(navController: NavController) {
    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
        Column {
            Text(text = "HELPiX", color = MedicalEmerald, fontSize = 28.sp, fontWeight = FontWeight.ExtraBold)
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(modifier = Modifier.size(8.dp).background(MedicalEmerald, CircleShape))
                Spacer(modifier = Modifier.width(6.dp))
                Text(text = "Doctor Hub Online", color = DocTextGrey, fontSize = 12.sp, fontWeight = FontWeight.Medium)
            }
        }
        Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            DocIconBtn(Icons.Outlined.Notifications) {}
            DocIconBtn(Icons.Filled.Settings) { navController.navigate(NavRoutes.AppSettings) }
        }
    }
}

@Composable
fun PracticeOverviewCard(navController: NavController, doctor: com.healthai.app.domain.model.User?) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(110.dp)
            .clip(RoundedCornerShape(24.dp))
            .background(Brush.linearGradient(listOf(Color(0xFF1E1B4B), Color(0xFF312E81))))
            .clickable { navController.navigate(NavRoutes.DoctorProfileManagement) }
            .border(1.dp, Color.White.copy(alpha = 0.1f), RoundedCornerShape(24.dp))
    ) {
        // Decorative background icon
        Icon(
            Icons.Default.VerifiedUser,
            contentDescription = null,
            modifier = Modifier.align(Alignment.CenterEnd).offset(x = 20.dp).size(120.dp),
            tint = Color.White.copy(alpha = 0.05f)
        )
        
        Row(
            modifier = Modifier.fillMaxSize().padding(20.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier.size(54.dp).background(Color.White.copy(alpha = 0.1f), CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Icon(Icons.Default.MedicalInformation, contentDescription = null, tint = MedicalEmerald, modifier = Modifier.size(28.dp))
            }
            Spacer(modifier = Modifier.width(16.dp))
            Column {
                Text("Dr. ${doctor?.name ?: "Expert"}", color = Color.White, fontSize = 18.sp, fontWeight = FontWeight.Bold)
                Text(
                    text = "${doctor?.specialization ?: "Specialist"} • GMC Verified", 
                    color = DocTextGrey, 
                    fontSize = 12.sp
                )
            }
            Spacer(modifier = Modifier.weight(1f))
            Icon(Icons.Default.ChevronRight, contentDescription = null, tint = DocTextGrey)
        }
    }
}

@Composable
fun DocMetricCard(title: String, value: String, icon: androidx.compose.ui.graphics.vector.ImageVector, color: Color, modifier: Modifier) {
    Card(
        modifier = modifier.height(90.dp),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = DocCardBg),
        border = BorderStroke(1.dp, color.copy(alpha = 0.2f))
    ) {
        Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
            Icon(icon, contentDescription = null, tint = color, modifier = Modifier.size(20.dp))
            Spacer(modifier = Modifier.weight(1f))
            Row(verticalAlignment = Alignment.Bottom) {
                Text(value, color = Color.White, fontSize = 22.sp, fontWeight = FontWeight.ExtraBold)
                Spacer(modifier = Modifier.width(4.dp))
                Text(title, color = DocTextGrey, fontSize = 10.sp, modifier = Modifier.padding(bottom = 4.dp))
            }
        }
    }
}

@Composable
fun DocActionCard(title: String, subtitle: String, icon: androidx.compose.ui.graphics.vector.ImageVector, accentColor: Color, onClick: () -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth().height(80.dp).clickable { onClick() },
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = DocCardBg),
        border = BorderStroke(1.dp, Color.White.copy(alpha = 0.05f))
    ) {
        Row(modifier = Modifier.fillMaxSize().padding(horizontal = 16.dp), verticalAlignment = Alignment.CenterVertically) {
            Box(modifier = Modifier.size(44.dp).background(accentColor.copy(alpha = 0.1f), CircleShape), contentAlignment = Alignment.Center) {
                Icon(icon, contentDescription = null, tint = accentColor, modifier = Modifier.size(24.dp))
            }
            Spacer(modifier = Modifier.width(16.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(title, color = Color.White, fontWeight = FontWeight.Bold, fontSize = 15.sp)
                Text(subtitle, color = DocTextGrey, fontSize = 11.sp)
            }
            Icon(Icons.Default.ChevronRight, contentDescription = null, tint = DocTextGrey.copy(alpha = 0.5f), modifier = Modifier.size(20.dp))
        }
    }
}

@Composable
fun DocServiceItem(title: String, icon: androidx.compose.ui.graphics.vector.ImageVector, modifier: Modifier, onClick: () -> Unit) {
    Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = modifier.clickable { onClick() }) {
        Box(
            modifier = Modifier
                .size(56.dp)
                .background(DocCardBg, RoundedCornerShape(16.dp))
                .border(1.dp, Color.White.copy(alpha = 0.05f), RoundedCornerShape(16.dp)),
            contentAlignment = Alignment.Center
        ) {
            Icon(icon, contentDescription = title, tint = Color.White, modifier = Modifier.size(24.dp))
        }
        Spacer(modifier = Modifier.height(8.dp))
        Text(title, color = DocTextGrey, fontSize = 11.sp, fontWeight = FontWeight.Medium)
    }
}

@Composable
fun DocIconBtn(icon: androidx.compose.ui.graphics.vector.ImageVector, onClick: () -> Unit) {
    Box(
        modifier = Modifier.size(44.dp).background(DocCardBg, CircleShape).clickable { onClick() },
        contentAlignment = Alignment.Center
    ) {
        Icon(icon, contentDescription = null, tint = Color.White, modifier = Modifier.size(20.dp))
    }
}

@Composable
fun DocGridBackground() {
    val color = Color.White.copy(alpha = 0.03f)
    Canvas(modifier = Modifier.fillMaxSize()) {
        val gridSize = 40.dp.toPx()
        for (x in 0..size.width.toInt() step gridSize.toInt()) {
            drawLine(color, start = Offset(x.toFloat(), 0f), end = Offset(x.toFloat(), size.height))
        }
        for (y in 0..size.height.toInt() step gridSize.toInt()) {
            drawLine(color, start = Offset(0f, y.toFloat()), end = Offset(size.width, y.toFloat()))
        }
    }
}
