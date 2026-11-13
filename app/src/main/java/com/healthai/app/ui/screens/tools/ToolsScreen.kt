package com.healthai.app.ui.screens.tools

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.healthai.app.ui.navigation.NavRoutes
import com.healthai.app.ui.screens.dashboard.CardBorderGray
import com.healthai.app.ui.screens.dashboard.HelpixBottomNav

// --- User-Friendly Light Palette Color Constants ---
private val OffWhiteBg = Color(0xFFF8FAFC)
private val MedicalTeal = Color(0xFF0D9488)
private val DarkCharcoalText = Color(0xFF1E293B)
private val SlateGrayText = Color(0xFF475569)

// --- Data Model ---
data class ToolItem(
    val title: String,
    val subtitle: String,
    val icon: ImageVector,
    val iconColor: Color,
    val circleBg: Color
)

@Composable
fun ToolsScreen(navController: NavController) {
    // Retained Active Tools with Soft Icon Circle Backgrounds
    val toolsList = listOf(
        ToolItem("Emergency SOS", "Quick emergency help", Icons.Default.Warning, Color(0xFFDC2626), Color(0xFFFEE2E2)),
        ToolItem("Medicine Reminders", "Never miss a dose", Icons.Default.NotificationsActive, Color(0xFFDB2777), Color(0xFFFCE7F3)),
        ToolItem("Health Records Vault", "Secure document storage", Icons.Default.Folder, Color(0xFF9333EA), Color(0xFFF3E8FF)),
        ToolItem("Prescription Reader", "AI OCR for prescriptions", Icons.Default.Description, Color(0xFF2563EB), Color(0xFFE0E7FF)),
        ToolItem("Disease Heatmap", "Track outbreak zones", Icons.Default.Map, Color(0xFFEA580C), Color(0xFFFFEDD5))
    )

    Scaffold(
        bottomBar = { HelpixBottomNav(navController = navController) },
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
                    .padding(horizontal = 16.dp)
            ) {
                // Header
                Spacer(modifier = Modifier.height(20.dp))
                Text(
                    text = "Smart Tools Zone",
                    color = MedicalTeal,
                    fontSize = 24.sp,
                    fontWeight = FontWeight.ExtraBold
                )
                Text(
                    text = "Your complete health operating system",
                    color = SlateGrayText,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Medium
                )
                Spacer(modifier = Modifier.height(20.dp))

                // Grid
                LazyVerticalGrid(
                    columns = GridCells.Fixed(2),
                    horizontalArrangement = Arrangement.spacedBy(16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp),
                    modifier = Modifier.weight(1f),
                    contentPadding = PaddingValues(bottom = 120.dp)
                ) {
                    items(
                        items = toolsList,
                        key = { tool -> tool.title }
                    ) { tool ->
                        ToolCard(tool) {
                            when (tool.title) {
                                "Skin Detector" -> navController.navigate(NavRoutes.SkinDetectorStart)
                                "Medicine Reminders" -> navController.navigate(NavRoutes.MedicineReminders)
                                "Health Records Vault" -> navController.navigate(NavRoutes.HealthVault)
                                "Prescription Reader" -> navController.navigate(NavRoutes.PrescriptionReader)
                                "Emergency SOS" -> navController.navigate(NavRoutes.Emergency)
                            }
                        }
                    }
                }
            }
            
            // Bottom Stats Panel
            Box(
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .fillMaxWidth()
                    .background(Color.White)
                    .border(1.dp, CardBorderGray)
            ) {
               ToolsStatsRow(toolsList.size) 
            }
        }
    }
}

@Composable
fun ToolCard(tool: ToolItem, onClick: () -> Unit) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(130.dp)
            .clip(RoundedCornerShape(18.dp))
            .background(Color.White)
            .border(1.dp, CardBorderGray, RoundedCornerShape(18.dp))
            .clickable { onClick() }
            .padding(16.dp)
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.SpaceBetween,
            horizontalAlignment = Alignment.Start
        ) {
            Box(
                modifier = Modifier
                    .size(42.dp)
                    .clip(CircleShape)
                    .background(tool.circleBg),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = tool.icon,
                    contentDescription = null,
                    tint = tool.iconColor,
                    modifier = Modifier.size(22.dp)
                )
            }
            
            Column {
                Text(
                    text = tool.title,
                    color = DarkCharcoalText,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    lineHeight = 18.sp
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = tool.subtitle,
                    color = SlateGrayText,
                    fontSize = 11.sp,
                    lineHeight = 14.sp
                )
            }
        }
    }
}

@Composable
fun ToolsStatsRow(activeToolsCount: Int) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
            .border(1.dp, CardBorderGray, RoundedCornerShape(16.dp))
            .background(Color(0xFFF8FAFC), RoundedCornerShape(16.dp))
            .padding(vertical = 14.dp),
        horizontalArrangement = Arrangement.SpaceEvenly
    ) {
        StatItem(activeToolsCount.toString(), "Active Tools", MedicalTeal, Icons.Default.Bolt)
        VerticalDivider()
        StatItem("0", "Scans Done", Color(0xFF16A34A), Icons.Default.CenterFocusWeak)
        VerticalDivider()
        StatItem("100%", "AI Ready", Color(0xFF9333EA), Icons.Default.Psychology)
    }
}

@Composable
fun StatItem(value: String, label: String, color: Color, icon: ImageVector) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Icon(icon, contentDescription = null, tint = color, modifier = Modifier.size(20.dp))
        Spacer(modifier = Modifier.height(4.dp))
        Text(text = value, fontSize = 18.sp, fontWeight = FontWeight.Bold, color = DarkCharcoalText)
        Text(text = label, fontSize = 10.sp, color = SlateGrayText, fontWeight = FontWeight.Medium)
    }
}

@Composable
fun VerticalDivider() {
    Box(
        modifier = Modifier
            .height(30.dp)
            .width(1.dp)
            .background(CardBorderGray)
    )
}
