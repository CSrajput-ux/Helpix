package com.healthai.app.ui.screens.dashboard

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
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
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import androidx.navigation.compose.currentBackStackEntryAsState
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.healthai.app.ui.navigation.NavRoutes
import com.healthai.app.ui.viewmodel.HealthViewModel

// --- User-Friendly Light Palette Constants ---
private val OffWhiteBg = Color(0xFFF8FAFC)
val CardBorderGray = Color(0xFFE2E8F0)
private val MedicalTeal = Color(0xFF0D9488)
private val SoftTealTint = Color(0xFFCCFBF1)
private val DarkTealText = Color(0xFF115E59)
private val FriendlyRoyalBlue = Color(0xFF2563EB)
private val DarkCharcoalText = Color(0xFF1E293B)
private val SlateGrayText = Color(0xFF475569)
private val CoralRed = Color(0xFFDC2626)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DashboardScreen(
    navController: NavController,
    healthViewModel: HealthViewModel = hiltViewModel()
) {
    val healthState by healthViewModel.uiState.collectAsState()
    var userType by remember { mutableStateOf("PATIENT") }

    LaunchedEffect(Unit) {
        val uid = FirebaseAuth.getInstance().currentUser?.uid
        if (uid != null) {
            FirebaseFirestore.getInstance().collection("users").document(uid).get()
                .addOnSuccessListener { document ->
                    userType = document.getString("userType") ?: "PATIENT"
                }
        }
    }

    Scaffold(
        bottomBar = {
            HelpixBottomNav(navController = navController, userType = userType)
        },
        containerColor = OffWhiteBg
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .background(OffWhiteBg)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(20.dp)
            ) {
                TopBarSection(navController)
                Spacer(modifier = Modifier.height(20.dp))

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f)
                        .border(1.dp, CardBorderGray, RoundedCornerShape(24.dp))
                        .background(Color.White, RoundedCornerShape(24.dp))
                        .padding(16.dp)
                ) {
                    Column(
                        modifier = Modifier.fillMaxSize(),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        WatchConnectionCard(navController)
                        Spacer(modifier = Modifier.height(16.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            DashboardVitalCard(
                                title = "Heart Rate",
                                value = "${healthState.heartRate} bpm",
                                icon = Icons.Default.Favorite,
                                iconTint = Color(0xFFEF4444),
                                circleBg = Color(0xFFFEE2E2),
                                modifier = Modifier.weight(1f)
                            )
                            DashboardVitalCard(
                                title = "Temp",
                                value = if (healthState.temperature > 0) "${healthState.temperature}°F" else "--",
                                icon = Icons.Default.Thermostat,
                                iconTint = Color(0xFFD97706),
                                circleBg = Color(0xFFFEF3C7),
                                modifier = Modifier.weight(1f)
                            )
                            DashboardVitalCard(
                                title = "BP",
                                value = healthState.bloodPressure,
                                icon = Icons.Default.Bloodtype,
                                iconTint = Color(0xFF2563EB),
                                circleBg = Color(0xFFDBEAFE),
                                modifier = Modifier.weight(1f)
                            )
                        }

                        Spacer(modifier = Modifier.height(20.dp))
                        CoughScanCard(navController)
                        Spacer(modifier = Modifier.height(20.dp))

                        Text(
                            text = "Health Services",
                            color = DarkCharcoalText,
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.fillMaxWidth()
                        )
                        Spacer(modifier = Modifier.height(12.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            SmallFeatureCard(
                                title = if (userType == "DOCTOR") "Schedule" else "Doctors",
                                icon = if (userType == "DOCTOR") Icons.Default.EventNote else Icons.Default.PersonSearch,
                                iconColor = MedicalTeal,
                                iconCircleBg = Color(0xFFCCFBF1),
                                modifier = Modifier.weight(1f),
                                onClick = {
                                    if (userType == "DOCTOR") navController.navigate(NavRoutes.DoctorSchedule)
                                    else navController.navigate(NavRoutes.Doctors)
                                }
                            )
                            SmallFeatureCard(
                                title = "Reader",
                                icon = Icons.Default.Description,
                                iconColor = Color(0xFF4F46E5),
                                iconCircleBg = Color(0xFFE0E7FF),
                                modifier = Modifier.weight(1f),
                                onClick = { navController.navigate(NavRoutes.PrescriptionReader) }
                            )
                            SmallFeatureCard(
                                title = "Vault",
                                icon = Icons.Default.Folder,
                                iconColor = Color(0xFF9333EA),
                                iconCircleBg = Color(0xFFF3E8FF),
                                modifier = Modifier.weight(1f),
                                onClick = { navController.navigate(NavRoutes.HealthVault) }
                            )
                            SmallFeatureCard(
                                title = "SOS",
                                icon = Icons.Default.Emergency,
                                iconColor = CoralRed,
                                iconCircleBg = Color(0xFFFEE2E2),
                                modifier = Modifier.weight(1f),
                                onClick = { navController.navigate(NavRoutes.Emergency) }
                            )
                        }
                    }

                    Box(
                        modifier = Modifier
                            .align(Alignment.BottomCenter)
                            .offset(y = 28.dp)
                    ) {
                        SkinScanButton(onClick = { navController.navigate(NavRoutes.SkinDetectorStart) })
                    }
                }
                Spacer(modifier = Modifier.height(28.dp))
            }
        }
    }
}

@Composable
fun HelpixBottomNav(navController: NavController, userType: String? = null) {
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    val resolvedUserType = userType ?: "PATIENT"

    Surface(
        color = Color.White,
        shadowElevation = 8.dp,
        modifier = Modifier.fillMaxWidth().border(BorderStroke(1.dp, CardBorderGray))
    ) {
        NavigationBar(
            containerColor = Color.White,
            contentColor = DarkCharcoalText
        ) {
            NavItem(
                name = "Home",
                icon = Icons.Filled.Home,
                isSelected = currentRoute == NavRoutes.Dashboard || currentRoute == NavRoutes.DoctorDashboard
            ) {
                val route = if (resolvedUserType == "DOCTOR") NavRoutes.DoctorDashboard else NavRoutes.Dashboard
                navController.navigate(route) { popUpTo(route) { inclusive = true } }
            }

            if (resolvedUserType == "DOCTOR") {
                NavItem(
                    name = "Bookings",
                    icon = Icons.Filled.EventNote,
                    isSelected = currentRoute == NavRoutes.DoctorBookings
                ) {
                    navController.navigate(NavRoutes.DoctorBookings)
                }
            } else {
                NavItem(
                    name = "Health",
                    icon = Icons.Filled.MonitorHeart,
                    isSelected = currentRoute == NavRoutes.Health
                ) {
                    navController.navigate(NavRoutes.Health)
                }
            }

            val isScheduleSelected = currentRoute == NavRoutes.DoctorSchedule || currentRoute == NavRoutes.Doctors
            NavItem(
                name = if (resolvedUserType == "DOCTOR") "Schedule" else "Doctors",
                icon = Icons.Filled.MedicalServices,
                isSelected = isScheduleSelected
            ) {
                if (resolvedUserType == "DOCTOR") {
                    navController.navigate(NavRoutes.DoctorSchedule)
                } else {
                    navController.navigate(NavRoutes.Doctors)
                }
            }

            if (resolvedUserType == "DOCTOR") {
                NavItem(
                    name = "Vault",
                    icon = Icons.Filled.AccountBalanceWallet,
                    isSelected = currentRoute == NavRoutes.DoctorVault
                ) {
                    navController.navigate(NavRoutes.DoctorVault)
                }
            } else {
                NavItem(
                    name = "Tools",
                    icon = Icons.Filled.GridView,
                    isSelected = currentRoute == NavRoutes.Tools
                ) {
                    navController.navigate(NavRoutes.Tools)
                }
            }

            NavItem(
                name = "Profile",
                icon = Icons.Filled.Person,
                isSelected = currentRoute == NavRoutes.Profile
            ) {
                navController.navigate(NavRoutes.Profile)
            }
        }
    }
}

@Composable
fun RowScope.NavItem(
    name: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    NavigationBarItem(
        selected = isSelected,
        onClick = onClick,
        icon = { Icon(icon, contentDescription = name) },
        label = {
            Text(
                text = name,
                fontSize = 11.sp,
                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium
            )
        },
        colors = NavigationBarItemDefaults.colors(
            selectedIconColor = MedicalTeal,
            selectedTextColor = MedicalTeal,
            indicatorColor = SoftTealTint,
            unselectedIconColor = SlateGrayText,
            unselectedTextColor = SlateGrayText
        )
    )
}

@Composable
fun TopBarSection(navController: NavController) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column {
            Text(
                text = "HELPiX",
                color = MedicalTeal,
                fontSize = 28.sp,
                fontWeight = FontWeight.ExtraBold
            )
            Text(
                text = "AI Health Scanner",
                color = SlateGrayText,
                fontSize = 14.sp,
                fontWeight = FontWeight.Medium
            )
        }
        Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            IconBtn(Icons.Outlined.Settings) { navController.navigate(NavRoutes.AppSettings) }
            IconBtn(Icons.Outlined.Notifications) { }
            IconBtn(Icons.Filled.Person) { navController.navigate(NavRoutes.Profile) }
        }
    }
}

@Composable
fun IconBtn(icon: androidx.compose.ui.graphics.vector.ImageVector, onClick: () -> Unit) {
    Box(
        modifier = Modifier
            .size(44.dp)
            .border(1.dp, CardBorderGray, CircleShape)
            .background(Color.White, CircleShape)
            .clickable { onClick() },
        contentAlignment = Alignment.Center
    ) {
        Icon(icon, contentDescription = null, tint = DarkCharcoalText)
    }
}

@Composable
fun WatchConnectionCard(navController: NavController) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(86.dp)
            .clickable { navController.navigate(NavRoutes.DeviceConnect) },
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        border = BorderStroke(1.dp, CardBorderGray)
    ) {
        Row(
            modifier = Modifier.fillMaxSize().padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(44.dp)
                    .background(Color(0xFFE0F2FE), CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    Icons.Default.Watch,
                    contentDescription = null,
                    tint = Color(0xFF0284C7),
                    modifier = Modifier.size(24.dp)
                )
            }
            Spacer(modifier = Modifier.width(14.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = "Smart Watch",
                    color = DarkCharcoalText,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "Track real-time vitals",
                    color = SlateGrayText,
                    fontSize = 12.sp
                )
            }
            Icon(Icons.Default.ChevronRight, contentDescription = null, tint = SlateGrayText)
        }
    }
}

@Composable
fun DashboardVitalCard(
    title: String,
    value: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    iconTint: Color,
    circleBg: Color,
    modifier: Modifier
) {
    Card(
        modifier = modifier.height(84.dp),
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        border = BorderStroke(1.dp, CardBorderGray)
    ) {
        Column(
            modifier = Modifier.fillMaxSize().padding(8.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Box(
                modifier = Modifier
                    .size(28.dp)
                    .background(circleBg, CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Icon(icon, contentDescription = null, tint = iconTint, modifier = Modifier.size(16.dp))
            }
            Spacer(modifier = Modifier.height(4.dp))
            Text(value, color = DarkCharcoalText, fontSize = 14.sp, fontWeight = FontWeight.Bold)
            Text(title, color = SlateGrayText, fontSize = 10.sp, fontWeight = FontWeight.Medium)
        }
    }
}

@Composable
fun CoughScanCard(navController: NavController) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(125.dp)
            .clickable { navController.navigate(NavRoutes.CoughAnalyzerStart) },
        shape = RoundedCornerShape(22.dp),
        colors = CardDefaults.cardColors(containerColor = SoftTealTint),
        border = BorderStroke(1.5.dp, MedicalTeal)
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Icon(
                Icons.Default.GraphicEq,
                contentDescription = null,
                tint = MedicalTeal,
                modifier = Modifier.size(36.dp)
            )
            Spacer(modifier = Modifier.height(6.dp))
            Text(
                text = "COUGH TB ANALYZER",
                color = DarkTealText,
                fontSize = 17.sp,
                fontWeight = FontWeight.ExtraBold,
                letterSpacing = 0.5.sp
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = "AI-powered TB, Cough & Respiratory Analysis",
                color = DarkTealText.copy(alpha = 0.85f),
                fontSize = 11.sp,
                fontWeight = FontWeight.Medium
            )
        }
    }
}

@Composable
fun SmallFeatureCard(
    title: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    iconColor: Color,
    iconCircleBg: Color,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    Card(
        modifier = modifier.height(82.dp).clickable { onClick() },
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        border = BorderStroke(1.dp, CardBorderGray)
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Box(
                modifier = Modifier
                    .size(36.dp)
                    .background(iconCircleBg, CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Icon(icon, contentDescription = null, tint = iconColor, modifier = Modifier.size(20.dp))
            }
            Spacer(modifier = Modifier.height(6.dp))
            Text(
                text = title,
                color = DarkCharcoalText,
                fontSize = 12.sp,
                fontWeight = FontWeight.SemiBold
            )
        }
    }
}

@Composable
fun SkinScanButton(onClick: () -> Unit) {
    Button(
        onClick = onClick,
        modifier = Modifier
            .height(56.dp)
            .shadow(8.dp, RoundedCornerShape(28.dp)),
        shape = RoundedCornerShape(28.dp),
        colors = ButtonDefaults.buttonColors(containerColor = FriendlyRoyalBlue),
        contentPadding = PaddingValues(horizontal = 28.dp)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(
                Icons.Default.QrCodeScanner,
                contentDescription = null,
                tint = Color.White,
                modifier = Modifier.size(24.dp)
            )
            Spacer(modifier = Modifier.width(10.dp))
            Text(
                text = "Scan Skin",
                color = Color.White,
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold
            )
        }
    }
}
