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
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.compose.currentBackStackEntryAsState
import com.healthai.app.R
import com.healthai.app.ui.navigation.NavRoutes
import com.healthai.app.ui.screens.dashboard.HelpixBottomNav

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DoctorDashboardScreen(
    navController: NavController,
    viewModel: DoctorDashboardViewModel = viewModel()
) {
    Scaffold(
        bottomBar = {
            HelpixBottomNav(navController = navController, userType = "DOCTOR")
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
            GridBackground()

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(20.dp)
            ) {
                DoctorTopBarSection(navController)
                Spacer(modifier = Modifier.height(24.dp))

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f)
                        .border(1.dp, colorResource(id = R.color.card_border_glow).copy(alpha = 0.3f), RoundedCornerShape(24.dp))
                        .background(
                            colorResource(id = R.color.helpix_bg_top).copy(alpha = 0.5f),
                            RoundedCornerShape(24.dp)
                        )
                ) {
                    Column(
                        modifier = Modifier.fillMaxSize().padding(16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        DoctorOverviewCard()
                        Spacer(modifier = Modifier.height(20.dp))

                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                            DashboardVitalCard("Patients", "42", Icons.Default.People, Color(0xFF2979FF), Modifier.weight(1f))
                            DashboardVitalCard("Today", "8", Icons.Default.Today, Color(0xFFFFAB00), Modifier.weight(1f))
                            DashboardVitalCard("Revenue", "₹12k", Icons.Default.Payments, Color(0xFFFF4081), Modifier.weight(1f))
                        }

                        Spacer(modifier = Modifier.height(24.dp))
                        QueueManagerCard(navController)
                        Spacer(modifier = Modifier.height(24.dp))
                        
                        Text(text = "Medical Services", color = Color.White, fontSize = 18.sp, fontWeight = FontWeight.Bold, modifier = Modifier.fillMaxWidth())
                        Spacer(modifier = Modifier.height(16.dp))

                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                            SmallFeatureCard(title = "Schedule", icon = Icons.Default.EventNote, modifier = Modifier.weight(1f), onClick = { 
                                navController.navigate("doctor_schedule")
                            })
                            SmallFeatureCard(title = "Patients", icon = Icons.Default.Groups, modifier = Modifier.weight(1f), onClick = { })
                            SmallFeatureCard(title = "Vault", icon = Icons.Default.Folder, modifier = Modifier.weight(1f), onClick = { navController.navigate(NavRoutes.HealthVault) })
                            SmallFeatureCard(title = "SOS", icon = Icons.Default.Emergency, modifier = Modifier.weight(1f), onClick = { navController.navigate(NavRoutes.Emergency) })
                        }
                    }

                    Box(modifier = Modifier.align(Alignment.BottomCenter).offset(y = 28.dp)) {
                        DoctorScanButton(onClick = { 
                            navController.navigate(NavRoutes.SkinDetectorStart)
                        })
                    }
                }
                Spacer(modifier = Modifier.height(32.dp))
            }
        }
    }
}

@Composable
fun DoctorTopBarSection(navController: NavController) {
    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
        Column {
            Text(text = "HELPiX", color = colorResource(id = R.color.logo_cyan), fontSize = 28.sp, fontWeight = FontWeight.Bold)
            Text(text = "Medical Dashboard", color = colorResource(id = R.color.text_secondary), fontSize = 14.sp)
        }
        Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            IconBtn(Icons.Outlined.Settings) { navController.navigate(NavRoutes.AppSettings) }
            IconBtn(Icons.Outlined.Notifications) {}
            IconBtn(Icons.Filled.Person) { navController.navigate(NavRoutes.Profile) }
        }
    }
}

@Composable
fun DoctorOverviewCard() {
    Card(
        modifier = Modifier.fillMaxWidth().height(90.dp),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = colorResource(id = R.color.helpix_bg_bottom).copy(alpha = 0.8f)),
        border = BorderStroke(1.dp, colorResource(id = R.color.logo_cyan).copy(alpha = 0.3f))
    ) {
        Row(modifier = Modifier.fillMaxSize().padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
            Box(modifier = Modifier.size(40.dp).background(colorResource(id = R.color.logo_cyan).copy(alpha = 0.1f), CircleShape), contentAlignment = Alignment.Center) {
                Icon(Icons.Default.Dashboard, contentDescription = null, tint = colorResource(id = R.color.logo_cyan), modifier = Modifier.size(22.dp))
            }
            Spacer(modifier = Modifier.width(16.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text("Practice Overview", color = Color.White, fontSize = 16.sp, fontWeight = FontWeight.Bold)
                Text("Real-time clinic analytics", color = Color.Gray, fontSize = 10.sp)
            }
            Icon(Icons.Default.ChevronRight, contentDescription = null, tint = Color.Gray)
        }
    }
}

@Composable
fun QueueManagerCard(navController: NavController) {
    Card(
        modifier = Modifier.fillMaxWidth().height(130.dp),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = colorResource(id = R.color.logo_cyan).copy(alpha = 0.15f)),
        border = BorderStroke(2.dp, Brush.linearGradient(colors = listOf(colorResource(id = R.color.logo_cyan), colorResource(id = R.color.neon_pink))))
    ) {
        Column(modifier = Modifier.fillMaxSize(), horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.Center) {
            Icon(Icons.AutoMirrored.Filled.PlaylistPlay, contentDescription = null, tint = colorResource(id = R.color.logo_cyan), modifier = Modifier.size(36.dp))
            Spacer(modifier = Modifier.height(6.dp))
            Text("PATIENT QUEUE MANAGER", color = Color.White, fontSize = 18.sp, fontWeight = FontWeight.ExtraBold, letterSpacing = 1.sp)
            Text("AI-assisted appointment prioritization", color = Color.Gray, fontSize = 10.sp)
        }
    }
}

@Composable
fun DoctorScanButton(onClick: () -> Unit) {
    Button(
        onClick = onClick,
        modifier = Modifier.height(56.dp).shadow(12.dp, RoundedCornerShape(28.dp)),
        shape = RoundedCornerShape(28.dp),
        colors = ButtonDefaults.buttonColors(containerColor = colorResource(id = R.color.logo_blue)),
        contentPadding = PaddingValues(horizontal = 24.dp)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(Icons.Default.QrCodeScanner, contentDescription = null, tint = Color.White, modifier = Modifier.size(24.dp))
            Spacer(modifier = Modifier.width(12.dp))
            Text("Scan Skin", color = Color.White, fontSize = 18.sp, fontWeight = FontWeight.Bold)
        }
    }
}

@Composable
fun DashboardVitalCard(title: String, value: String, icon: androidx.compose.ui.graphics.vector.ImageVector, color: Color, modifier: Modifier) {
    Card(
        modifier = modifier.height(70.dp),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White.copy(alpha = 0.05f)),
        border = BorderStroke(1.dp, color.copy(alpha = 0.2f))
    ) {
        Column(modifier = Modifier.fillMaxSize(), horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.Center) {
            Icon(icon, contentDescription = null, tint = color, modifier = Modifier.size(16.dp))
            Text(value, color = Color.White, fontSize = 16.sp, fontWeight = FontWeight.Bold)
            Text(title, color = Color.Gray, fontSize = 9.sp)
        }
    }
}

@Composable
fun SmallFeatureCard(title: String, icon: androidx.compose.ui.graphics.vector.ImageVector, modifier: Modifier = Modifier, onClick: () -> Unit) {
    Card(
        modifier = modifier.height(80.dp).clickable { onClick() },
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = colorResource(id = R.color.helpix_bg_bottom).copy(alpha = 0.6f)),
        border = BorderStroke(1.dp, Color.White.copy(alpha = 0.1f))
    ) {
        Column(modifier = Modifier.fillMaxSize(), horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.Center) {
            Icon(icon, contentDescription = null, tint = Color.White, modifier = Modifier.size(22.dp))
            Spacer(modifier = Modifier.height(6.dp))
            Text(title, color = Color.White, fontSize = 11.sp, fontWeight = FontWeight.Medium)
        }
    }
}

@Composable
fun IconBtn(icon: androidx.compose.ui.graphics.vector.ImageVector, onClick: () -> Unit) {
    Box(
        modifier = Modifier.size(44.dp).border(1.dp, Color.White.copy(alpha = 0.1f), CircleShape)
            .background(Color.White.copy(alpha = 0.05f), CircleShape).clickable { onClick() },
        contentAlignment = Alignment.Center
    ) {
        Icon(icon, contentDescription = null, tint = Color.White)
    }
}

@Composable
fun GridBackground() {
    val color = Color.White.copy(alpha = 0.05f)
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
