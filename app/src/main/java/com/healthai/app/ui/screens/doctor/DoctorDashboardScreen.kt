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
import com.google.firebase.auth.FirebaseAuth
import com.healthai.app.R
import com.healthai.app.domain.model.Appointment
import com.healthai.app.ui.navigation.NavRoutes
import java.text.SimpleDateFormat
import java.util.Locale

@Composable
fun DoctorDashboardScreen(navController: NavController, viewModel: DoctorDashboardViewModel = viewModel()) {

    var selectedTab by remember { mutableStateOf("Home") }
    val auth = FirebaseAuth.getInstance()
    val appointments by viewModel.appointments.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()

    LaunchedEffect(Unit) {
        viewModel.fetchAppointments()
    }

    Scaffold(
        bottomBar = {
            DoctorBottomNav(selectedTab) { selectedTab = it }
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .background(
                    brush = Brush.verticalGradient(
                        colors = listOf(
                            colorResource(id = R.color.login_bg_top),
                            colorResource(id = R.color.login_bg_bottom)
                        )
                    )
                )
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp)
            ) {
                // Header
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("Doctor Dashboard", fontSize = 24.sp, fontWeight = FontWeight.Bold, color = Color.White)
                    Icon(
                        Icons.Default.Logout, 
                        contentDescription = "Logout",
                        tint = Color.White,
                        modifier = Modifier.size(24.dp).clickable { 
                            auth.signOut()
                            navController.navigate(NavRoutes.Login) { popUpTo(NavRoutes.DoctorDashboard) { inclusive = true } }
                        }
                    )
                }
                
                Spacer(modifier = Modifier.height(24.dp))

                // Content based on selected tab
                when (selectedTab) {
                    "Home" -> {
                        if (isLoading) {
                            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                                CircularProgressIndicator(color = colorResource(id = R.color.logo_cyan))
                            }
                        } else {
                            LazyColumn {
                                item {
                                    Text("Upcoming Appointments", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = Color.White)
                                    Spacer(modifier = Modifier.height(16.dp))
                                }
                                items(appointments) { appointment ->
                                    AppointmentCard(appointment)
                                    Spacer(modifier = Modifier.height(16.dp))
                                }
                            }
                        }
                    }
                    "Schedule" -> EmptyStateView("Your Schedule", Icons.Default.EventNote)
                    "Vault" -> EmptyStateView("Patient Records Vault", Icons.Default.Folder)
                    "Profile" -> EmptyStateView("Your Profile", Icons.Default.Person)
                }
            }
        }
    }
}

@Composable
fun EmptyStateView(title: String, icon: ImageVector) {
    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Icon(icon, contentDescription = null, tint = Color.Gray, modifier = Modifier.size(64.dp))
            Spacer(modifier = Modifier.height(16.dp))
            Text(title, color = Color.White, fontSize = 20.sp, fontWeight = FontWeight.Medium)
            Text("Content coming soon...", color = Color.Gray, fontSize = 14.sp)
        }
    }
}

@Composable
fun DoctorBottomNav(selectedTab: String, onTabSelected: (String) -> Unit) {
    NavigationBar(
        containerColor = Color(0xFF0F172A),
        contentColor = Color.White,
        tonalElevation = 8.dp
    ) {
        // Tab 1: Home
        DoctorNavItem(
            label = "Home",
            icon = Icons.Default.Home,
            selected = selectedTab == "Home",
            onClick = { onTabSelected("Home") }
        )

        // Tab 2: Schedule
        DoctorNavItem(
            label = "Schedule",
            icon = Icons.Default.CalendarMonth,
            selected = selectedTab == "Schedule",
            onClick = { onTabSelected("Schedule") }
        )

        // Tab 3: Scan (The Circular Center Button)
        Box(
            modifier = Modifier.weight(1f),
            contentAlignment = Alignment.Center
        ) {
            Box(
                modifier = Modifier
                    .size(56.dp)
                    .offset(y = (-10).dp)
                    .clip(CircleShape)
                    .background(colorResource(id = R.color.logo_blue))
                    .clickable { /* Handle Scan Click */ },
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.QrCodeScanner,
                    contentDescription = "Scan",
                    tint = Color.White,
                    modifier = Modifier.size(28.dp)
                )
            }
        }

        // Tab 4: Vault
        DoctorNavItem(
            label = "Vault",
            icon = Icons.Default.Folder,
            selected = selectedTab == "Vault",
            onClick = { onTabSelected("Vault") }
        )

        // Tab 5: Profile
        DoctorNavItem(
            label = "Profile",
            icon = Icons.Default.Person,
            selected = selectedTab == "Profile",
            onClick = { onTabSelected("Profile") }
        )
    }
}

@Composable
fun RowScope.DoctorNavItem(label: String, icon: ImageVector, selected: Boolean, onClick: () -> Unit) {
    NavigationBarItem(
        selected = selected,
        onClick = onClick,
        icon = { Icon(icon, contentDescription = label) },
        label = { Text(label, fontSize = 10.sp) },
        colors = NavigationBarItemDefaults.colors(
            selectedIconColor = colorResource(id = R.color.logo_cyan),
            selectedTextColor = colorResource(id = R.color.logo_cyan),
            indicatorColor = colorResource(id = R.color.logo_cyan).copy(alpha = 0.1f),
            unselectedIconColor = Color.Gray,
            unselectedTextColor = Color.Gray
        )
    )
}

@Composable
fun AppointmentCard(appointment: Appointment) {
    val formattedDate = SimpleDateFormat("EEE, MMM d, yyyy 'at' h:mm a", Locale.getDefault()).format(appointment.appointmentDate!!)
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = colorResource(id = R.color.helpix_bg_top))
    ) {
        Row(modifier = Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .clip(CircleShape)
                    .background(colorResource(id = R.color.logo_cyan).copy(alpha = 0.1f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(Icons.Default.CalendarToday, contentDescription = null, tint = colorResource(id = R.color.logo_cyan), modifier = Modifier.size(24.dp))
            }
            Spacer(modifier = Modifier.size(16.dp))
            Column {
                Text("Patient: ${appointment.patientId}", color = Color.White, fontWeight = FontWeight.Bold)
                Text(formattedDate, color = Color.Gray, fontSize = 12.sp)
                Text(appointment.status, color = if (appointment.status == "SCHEDULED") colorResource(id = R.color.logo_cyan) else Color.Gray, fontSize = 12.sp)
            }
        }
    }
}
