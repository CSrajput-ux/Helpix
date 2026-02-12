package com.healthai.app.ui.screens.dashboard

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
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
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.currentBackStackEntryAsState
import com.healthai.app.R
import com.healthai.app.ui.navigation.NavRoutes

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DashboardScreen(navController: NavController) {
    var selectedGender by remember { mutableStateOf("Male") }

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
                TopBarSection()
                Spacer(modifier = Modifier.height(20.dp))
                ModesRow(navController = navController)
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
                        GenderSwitch(selectedGender) { selectedGender = it }

                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .fillMaxWidth(),
                            contentAlignment = Alignment.Center
                        ) {
                            BodyWireframe()
                            ScanTarget(0f, -140f)
                            ScanTarget(0f, -50f)
                            ScanTarget(-60f, 20f)
                            ScanTarget(40f, 130f)
                        }
                    }

                    Row(
                        modifier = Modifier.align(Alignment.BottomCenter).fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        QuickActionCard(
                            title = "Full Body Scan",
                            subtitle = "Multi-disease\nanalysis",
                            icon = Icons.Default.CenterFocusWeak,
                            modifier = Modifier.weight(1f).clickable { navController.navigate(NavRoutes.Results) }
                        )
                        QuickActionCard(
                            title = "Disease\nHeatmap",
                            subtitle = "View outbreak\nzones",
                            icon = Icons.Default.Map,
                            modifier = Modifier.weight(1f)
                        )
                    }
                }
            }
        }
    }
}

// --- NAVIGATION BAR (FIXED) ---
@Composable
fun HelpixBottomNav(navController: NavController) {
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    NavigationBar(
        containerColor = colorResource(id = R.color.login_bg_bottom),
        contentColor = Color.White
    ) {
        NavItem(
            name = "Scan",
            icon = Icons.Filled.QrCodeScanner,
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
            name = "Health",
            icon = Icons.Filled.MonitorHeart,
            isSelected = currentRoute == NavRoutes.Health,
            onClick = {
                if (currentRoute != NavRoutes.Health) {
                    navController.navigate(NavRoutes.Health)
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

// --- VISUAL COMPONENTS ---

@Composable
fun TopBarSection() {
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
            IconBtn(Icons.Outlined.Menu)
        }
    }
}

@Composable
fun ModesRow(navController: NavController) {
    LazyRow(
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        contentPadding = PaddingValues(end = 16.dp)
    ) {
        item {
            Box(modifier = Modifier.clickable { navController.navigate(NavRoutes.SeniorMode) }) { 
                ModeChip("Senior Mode", Color(0xFF9C27B0), Icons.Default.Person)
            }
        }
        item {
            Box(modifier = Modifier.clickable { navController.navigate(NavRoutes.KidsMode) }) { // <-- Add Clickable
                ModeChip("Kids Mode", Color(0xFF00C853), Icons.Default.Face)
            }
        }
        item {
            Box(modifier = Modifier.clickable { navController.navigate(NavRoutes.RuralMode) }) { 
                ModeChip("Rural Mode", Color(0xFFFFAB00), Icons.Default.Agriculture)
            }
        }
        item {
            Box(modifier = Modifier.clickable { navController.navigate(NavRoutes.Emergency) }) { 
                ModeChip("Emergency", Color(0xFFFF5252), Icons.Default.Warning)
            }
        }
    }
}

@Composable
fun ModeChip(text: String, color: Color, icon: androidx.compose.ui.graphics.vector.ImageVector) {
    Box(
        modifier = Modifier
            .height(40.dp)
            .border(1.dp, color.copy(alpha = 0.5f), RoundedCornerShape(20.dp))
            .background(color.copy(alpha = 0.1f), RoundedCornerShape(20.dp))
            .padding(horizontal = 16.dp),
        contentAlignment = Alignment.Center
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(icon, contentDescription = null, tint = color, modifier = Modifier.size(16.dp))
            Spacer(modifier = Modifier.width(8.dp))
            Text(text = text, color = color, fontWeight = FontWeight.Bold, fontSize = 13.sp)
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
fun GenderSwitch(selected: String, onSelect: (String) -> Unit) {
    Row(
        modifier = Modifier
            .width(200.dp)
            .height(44.dp)
            .border(1.dp, Color.White.copy(alpha = 0.2f), RoundedCornerShape(22.dp))
            .background(Color.Black.copy(alpha = 0.3f), RoundedCornerShape(22.dp)),
        verticalAlignment = Alignment.CenterVertically
    ) {
        GenderOption("Male", selected == "Male") { onSelect("Male") }
        GenderOption("Female", selected == "Female") { onSelect("Female") }
    }
}

@Composable
fun RowScope.GenderOption(text: String, isSelected: Boolean, onClick: () -> Unit) {
    Box(
        modifier = Modifier
            .weight(1f)
            .fillMaxHeight()
            .padding(2.dp)
            .clip(RoundedCornerShape(20.dp))
            .background(if (isSelected) colorResource(id = R.color.logo_blue) else Color.Transparent)
            .clickable { onClick() },
        contentAlignment = Alignment.Center
    ) {
        Text(text, color = Color.White, fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal)
    }
}

@Composable
fun BodyWireframe() {
    val cyanColor = colorResource(id = R.color.logo_cyan)
    Canvas(modifier = Modifier.height(400.dp).width(220.dp)) {
        val stroke = Stroke(width = 4.dp.toPx(), cap = StrokeCap.Round)

        drawCircle(color = cyanColor, radius = 30.dp.toPx(), center = center.copy(y = center.y - 140.dp.toPx()), style = stroke)
        drawLine(cyanColor, start = center.copy(y = center.y - 110.dp.toPx()), end = center.copy(y = center.y - 90.dp.toPx()), strokeWidth = 4.dp.toPx())
        drawRoundRect(
            color = cyanColor,
            topLeft = Offset(center.x - 45.dp.toPx(), center.y - 90.dp.toPx()),
            size = Size(90.dp.toPx(), 140.dp.toPx()),
            cornerRadius = CornerRadius(10f, 10f),
            style = stroke
        )
        val leftArm = Path().apply {
            moveTo(center.x - 50.dp.toPx(), center.y - 80.dp.toPx())
            quadraticBezierTo(center.x - 100.dp.toPx(), center.y - 20.dp.toPx(), center.x - 60.dp.toPx(), center.y + 40.dp.toPx())
        }
        drawPath(leftArm, cyanColor, style = stroke)
        val rightArm = Path().apply {
            moveTo(center.x + 50.dp.toPx(), center.y - 80.dp.toPx())
            quadraticBezierTo(center.x + 100.dp.toPx(), center.y - 20.dp.toPx(), center.x + 60.dp.toPx(), center.y + 40.dp.toPx())
        }
        drawPath(rightArm, cyanColor, style = stroke)
        drawLine(cyanColor, start = Offset(center.x - 25.dp.toPx(), center.y + 50.dp.toPx()), end = Offset(center.x - 35.dp.toPx(), center.y + 180.dp.toPx()), strokeWidth = 4.dp.toPx())
        drawLine(cyanColor, start = Offset(center.x + 25.dp.toPx(), center.y + 50.dp.toPx()), end = Offset(center.x + 35.dp.toPx(), center.y + 180.dp.toPx()), strokeWidth = 4.dp.toPx())
    }
}

@Composable
fun ScanTarget(offsetX: Float, offsetY: Float) {
    val cyanColor = colorResource(id = R.color.logo_cyan)
    Box(
        modifier = Modifier
            .offset(x = offsetX.dp, y = offsetY.dp)
            .size(40.dp)
    ) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            val stroke = Stroke(width = 2.dp.toPx(), cap = StrokeCap.Round)
            val w = size.width
            val h = size.height
            val corner = 10.dp.toPx()

            drawPath(Path().apply { moveTo(0f, corner); lineTo(0f, 0f); lineTo(corner, 0f) }, cyanColor, style = stroke)
            drawPath(Path().apply { moveTo(w-corner, 0f); lineTo(w, 0f); lineTo(w, corner) }, cyanColor, style = stroke)
            drawPath(Path().apply { moveTo(0f, h-corner); lineTo(0f, h); lineTo(corner, h) }, cyanColor, style = stroke)
            drawPath(Path().apply { moveTo(w-corner, h); lineTo(w, h); lineTo(w, h-corner) }, cyanColor, style = stroke)

            drawCircle(color = cyanColor.copy(alpha = 0.1f), radius = w/2)
        }
    }
}

@Composable
fun QuickActionCard(title: String, subtitle: String, icon: androidx.compose.ui.graphics.vector.ImageVector, modifier: Modifier) {
    Box(
        modifier = modifier
            .height(100.dp)
            .border(1.dp, colorResource(id = R.color.logo_cyan).copy(alpha = 0.3f), RoundedCornerShape(16.dp))
            .background(colorResource(id = R.color.helpix_bg_bottom).copy(alpha = 0.9f), RoundedCornerShape(16.dp))
            .padding(12.dp)
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Icon(icon, contentDescription = null, tint = colorResource(id = R.color.logo_cyan))
            Column {
                Text(title, color = Color.White, fontSize = 13.sp, fontWeight = FontWeight.Bold)
                Text(subtitle, color = Color.Gray, fontSize = 10.sp, lineHeight = 12.sp)
            }
        }
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