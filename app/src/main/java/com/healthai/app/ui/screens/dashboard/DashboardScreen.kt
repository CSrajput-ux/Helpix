package com.healthai.app.ui.screens.dashboard

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
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
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import androidx.navigation.compose.currentBackStackEntryAsState
import com.healthai.app.R
import com.healthai.app.ui.navigation.NavRoutes
import com.healthai.app.ui.viewmodel.HealthViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DashboardScreen(
    navController: NavController,
    healthViewModel: HealthViewModel = hiltViewModel()
) {
    val healthState by healthViewModel.uiState.collectAsState()

    Scaffold(
        bottomBar = {
            HelpixBottomNav(navController = navController)
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
                TopBarSection(navController)
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
                        .padding(16.dp)
                ) {
                    Column(
                        modifier = Modifier.fillMaxSize(),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        // Watch Connection Section
                        WatchConnectionCard(navController)
                        
                        Spacer(modifier = Modifier.height(20.dp))

                        // Real-time Vitals Preview
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                            DashboardVitalCard("Heart", "${healthState.heartRate}", Icons.Default.Favorite, Color(0xFFFF4081), Modifier.weight(1f))
                            DashboardVitalCard("Temp", if (healthState.temperature > 0) "${healthState.temperature}" else "--", Icons.Default.Thermostat, Color(0xFFFFAB00), Modifier.weight(1f))
                            DashboardVitalCard("BP", healthState.bloodPressure, Icons.Default.Bloodtype, Color(0xFF2979FF), Modifier.weight(1f))
                        }

                        Spacer(modifier = Modifier.height(24.dp))
                        
                        // New Main Scan Button
                        MainScanCard(navController)
                        
                        Spacer(modifier = Modifier.height(24.dp))
                        
                        Text(
                            text = "Health Services",
                            color = Color.White,
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.fillMaxWidth(),
                            textAlign = TextAlign.Start
                        )
                        
                        Spacer(modifier = Modifier.height(16.dp))

                        // Quick Actions Row
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            SmallFeatureCard(
                                title = "Doctors",
                                icon = Icons.Default.PersonSearch,
                                modifier = Modifier.weight(1f),
                                onClick = { navController.navigate(NavRoutes.Doctors) }
                            )
                            SmallFeatureCard(
                                title = "Tools",
                                icon = Icons.Default.GridView,
                                modifier = Modifier.weight(1f),
                                onClick = { navController.navigate(NavRoutes.Tools) }
                            )
                            SmallFeatureCard(
                                title = "Vault",
                                icon = Icons.Default.Folder,
                                modifier = Modifier.weight(1f),
                                onClick = { navController.navigate(NavRoutes.HealthVault) }
                            )
                            SmallFeatureCard(
                                title = "SOS",
                                icon = Icons.Default.Emergency,
                                modifier = Modifier.weight(1f),
                                onClick = { navController.navigate(NavRoutes.Emergency) }
                            )
                        }
                    }
                }
            }
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
fun MainScanCard(navController: NavController) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(130.dp)
            .clickable { navController.navigate(NavRoutes.Scan) },
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(
            containerColor = colorResource(id = R.color.logo_cyan).copy(alpha = 0.15f)
        ),
        border = BorderStroke(2.dp, Brush.linearGradient(
            colors = listOf(colorResource(id = R.color.logo_cyan), colorResource(id = R.color.neon_pink))
        ))
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Icon(
                imageVector = Icons.Default.QrCodeScanner,
                contentDescription = null,
                tint = colorResource(id = R.color.logo_cyan),
                modifier = Modifier.size(36.dp)
            )
            Spacer(modifier = Modifier.height(6.dp))
            Text(
                text = "START AI SCAN",
                color = Color.White,
                fontSize = 18.sp,
                fontWeight = FontWeight.ExtraBold,
                letterSpacing = 2.sp
            )
            Text(
                text = "Cough, Skin & Symptoms Analysis",
                color = Color.Gray,
                fontSize = 10.sp
            )
        }
    }
}

@Composable
fun SmallFeatureCard(title: String, icon: androidx.compose.ui.graphics.vector.ImageVector, modifier: Modifier = Modifier, onClick: () -> Unit) {
    Card(
        modifier = modifier
            .height(80.dp)
            .clickable { onClick() },
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = colorResource(id = R.color.helpix_bg_bottom).copy(alpha = 0.6f)
        ),
        border = BorderStroke(1.dp, Color.White.copy(alpha = 0.1f))
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Icon(icon, contentDescription = null, tint = Color.White, modifier = Modifier.size(22.dp))
            Spacer(modifier = Modifier.height(6.dp))
            Text(title, color = Color.White, fontSize = 11.sp, fontWeight = FontWeight.Medium)
        }
    }
}

@Composable
fun WatchConnectionCard(navController: NavController) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(90.dp)
            .clickable { navController.navigate(NavRoutes.DeviceConnect) },
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(
            containerColor = colorResource(id = R.color.helpix_bg_bottom).copy(alpha = 0.8f)
        ),
        border = BorderStroke(1.dp, colorResource(id = R.color.logo_cyan).copy(alpha = 0.3f))
    ) {
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .background(colorResource(id = R.color.logo_cyan).copy(alpha = 0.1f), CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Watch,
                    contentDescription = null,
                    tint = colorResource(id = R.color.logo_cyan),
                    modifier = Modifier.size(22.dp)
                )
            }
            
            Spacer(modifier = Modifier.width(16.dp))
            
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = "Smart Watch",
                    color = Color.White,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "Track real-time vitals",
                    color = Color.Gray,
                    fontSize = 10.sp
                )
            }
            
            Icon(
                imageVector = Icons.Default.ChevronRight,
                contentDescription = null,
                tint = Color.Gray
            )
        }
    }
}

// --- NAVIGATION BAR ---
@Composable
fun HelpixBottomNav(navController: NavController) {
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    NavigationBar(
        containerColor = colorResource(id = R.color.login_bg_bottom),
        contentColor = Color.White
    ) {
        NavItem(
            name = "Home",
            icon = Icons.Filled.Home,
            isSelected = currentRoute == NavRoutes.Dashboard,
            onClick = {
                if (currentRoute != NavRoutes.Dashboard) {
                    navController.navigate(NavRoutes.Dashboard) {
                        popUpTo(NavRoutes.Dashboard) { inclusive = true }
                    }
                }
            }
        )

        NavItem(
            name = stringResource(id = R.string.health),
            icon = Icons.Filled.MonitorHeart,
            isSelected = currentRoute == NavRoutes.Health,
            onClick = {
                if (currentRoute != NavRoutes.Health) {
                    navController.navigate(NavRoutes.Health)
                }
            }
        )

        NavItem(
            name = "Doctors",
            icon = Icons.Filled.MedicalServices,
            isSelected = currentRoute == NavRoutes.Doctors,
            onClick = {
                if (currentRoute != NavRoutes.Doctors) {
                    navController.navigate(NavRoutes.Doctors)
                }
            }
        )

        NavItem(
            name = "Tools",
            icon = Icons.Filled.GridView,
            isSelected = currentRoute == NavRoutes.Tools,
            onClick = {
                if (currentRoute != NavRoutes.Tools) {
                    navController.navigate(NavRoutes.Tools)
                }
            }
        )

        NavItem(
            name = "Profile",
            icon = Icons.Filled.Person,
            isSelected = currentRoute == NavRoutes.Profile,
            onClick = {
                if (currentRoute != NavRoutes.Profile) {
                    navController.navigate(NavRoutes.Profile)
                }
            }
        )
    }
}

@Composable
fun RowScope.NavItem(name: String, icon: androidx.compose.ui.graphics.vector.ImageVector, isSelected: Boolean, onClick: () -> Unit) {
    NavigationBarItem(
        selected = isSelected,
        onClick = onClick,
        icon = { Icon(icon, contentDescription = name) },
        label = { Text(name, fontSize = 10.sp) },
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
fun TopBarSection(navController: NavController) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column {
            Text(
                text = "HELPiX",
                color = colorResource(id = R.color.logo_cyan),
                fontSize = 28.sp,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = "AI Health Scanner",
                color = colorResource(id = R.color.text_secondary),
                fontSize = 14.sp
            )
        }
        Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            IconBtn(Icons.Outlined.Notifications)
            Box(modifier = Modifier.clickable { navController.navigate(NavRoutes.Profile) }) {
                IconBtn(Icons.Filled.Person)
            }
        }
    }
}

@Composable
fun IconBtn(icon: androidx.compose.ui.graphics.vector.ImageVector) {
    Box(
        modifier = Modifier
            .size(44.dp)
            .border(1.dp, Color.White.copy(alpha = 0.1f), CircleShape)
            .background(Color.White.copy(alpha = 0.05f), CircleShape),
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