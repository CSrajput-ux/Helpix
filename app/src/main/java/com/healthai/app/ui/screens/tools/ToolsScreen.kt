package com.healthai.app.ui.screens.tools

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
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
import androidx.navigation.NavController
import com.healthai.app.R
import com.healthai.app.ui.navigation.NavRoutes
import com.healthai.app.ui.screens.dashboard.HelpixBottomNav

// --- Data Model ---
data class ToolItem(
    val title: String,
    val subtitle: String,
    val icon: ImageVector,
    val color: Color
)

@Composable
fun ToolsScreen(navController: NavController) {
    // Tool List
    val toolsList = listOf(
        ToolItem("Multi-Disease Scanner", "AI-powered full body analysis", Icons.Default.CenterFocusStrong, Color(0xFF00E5FF)),
        ToolItem("Cough TB Analyzer", "Detect TB from cough sound", Icons.Default.GraphicEq, Color(0xFFD500F9)),
        ToolItem("Skin Detector", "Identify skin conditions", Icons.Default.Face, Color(0xFF00E676)),
        ToolItem("Symptom Doctor", "Check your symptoms", Icons.Default.MedicalServices, Color(0xFF2979FF)),
        ToolItem("AI Chat Doctor", "24/7 medical assistance", Icons.Default.Chat, Color(0xFF00B0FF)),
        ToolItem("Emergency SOS", "Quick emergency help", Icons.Default.Warning, Color(0xFFFF1744)), 
        ToolItem("Nearby Hospitals", "Find healthcare centers", Icons.Default.LocalHospital, Color(0xFFFF9100)),
        ToolItem("Medicine Reminders", "Never miss a dose", Icons.Default.NotificationsActive, Color(0xFFFF4081)),
        ToolItem("Health Records Vault", "Secure document storage", Icons.Default.Folder, Color(0xFFAA00FF)),
        ToolItem("Diet Planner", "Personalized meal plans", Icons.Default.Restaurant, Color(0xFF76FF03)),
        ToolItem("Fitness Tracker", "Track your workouts", Icons.Default.MonitorHeart, Color(0xFF00E5FF)),
        ToolItem("Prescription Reader", "AI OCR for prescriptions", Icons.Default.Description, Color(0xFF2962FF)),
        ToolItem("Digital Health Passport", "Your medical ID card", Icons.Default.CreditCard, Color(0xFF6200EA)),
        ToolItem("Disease Heatmap", "Track outbreak zones", Icons.Default.Map, Color(0xFFFF6D00))
    )

    Scaffold(
        bottomBar = { HelpixBottomNav(navController = navController) }
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
                    .padding(horizontal = 16.dp)
            ) {
                // Header
                Spacer(modifier = Modifier.height(20.dp))
                Text(
                    text = "Smart Tools Zone",
                    color = colorResource(id = R.color.logo_cyan),
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "Your complete health operating system",
                    color = Color.Gray,
                    fontSize = 14.sp
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
                    items(toolsList) { tool ->
                        ToolCard(tool) {
                            when (tool.title) {
                                "Multi-Disease Scanner" -> navController.navigate(NavRoutes.MultiDiseaseScanStart)
                                "Cough TB Analyzer" -> navController.navigate(NavRoutes.CoughAnalyzerStart)
                                "Skin Detector" -> navController.navigate(NavRoutes.SkinDetectorStart)
                                "Symptom Doctor" -> navController.navigate(NavRoutes.SymptomDoctorStart)
                                "AI Chat Doctor" -> navController.navigate(NavRoutes.AiChat)
                                "Nearby Hospitals" -> navController.navigate(NavRoutes.NearbyHospitals)
                                "Medicine Reminders" -> navController.navigate(NavRoutes.MedicineReminders)
                                "Health Records Vault" -> navController.navigate(NavRoutes.HealthVault)
                                "Diet Planner" -> navController.navigate(NavRoutes.DietPlanner)
                                "Fitness Tracker" -> navController.navigate(NavRoutes.FitnessTracker)
                                "Prescription Reader" -> navController.navigate(NavRoutes.PrescriptionReader)
                                "Emergency SOS" -> navController.navigate(NavRoutes.Emergency)
                                // Add navigation for other tools here
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
                    .background(Color(0xFF020617).copy(alpha = 0.95f))
            ) {
               ToolsStatsRow() 
            }
        }
    }
}

@Composable
fun ToolCard(tool: ToolItem, onClick: () -> Unit) { // Added onClick parameter
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(130.dp)
            .clip(RoundedCornerShape(16.dp))
            .background(Color(0xFF1E293B))
            .border(1.dp, Color.White.copy(alpha = 0.05f), RoundedCornerShape(16.dp))
            .clickable { onClick() } // Trigger onClick here
            .padding(16.dp)
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.SpaceBetween,
            horizontalAlignment = Alignment.Start
        ) {
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(tool.color.copy(alpha = 0.1f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = tool.icon,
                    contentDescription = null,
                    tint = tool.color,
                    modifier = Modifier.size(24.dp)
                )
            }
            
            Column {
                Text(
                    text = tool.title,
                    color = Color.White,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    lineHeight = 18.sp
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = tool.subtitle,
                    color = Color.Gray,
                    fontSize = 10.sp,
                    lineHeight = 12.sp
                )
            }
        }
    }
}

@Composable
fun ToolsStatsRow() {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
            .border(1.dp, Color.White.copy(alpha = 0.1f), RoundedCornerShape(16.dp))
            .background(Color(0xFF0F172A), RoundedCornerShape(16.dp))
            .padding(vertical = 16.dp),
        horizontalArrangement = Arrangement.SpaceEvenly
    ) {
        StatItem("24", "Active Tools", Color(0xFF2979FF), Icons.Default.Bolt)
        VerticalDivider()
        StatItem("142", "Scans Done", Color(0xFF00E676), Icons.Default.CenterFocusWeak)
        VerticalDivider()
        StatItem("98%", "AI Accuracy", Color(0xFFD500F9), Icons.Default.Psychology)
    }
}

@Composable
fun StatItem(value: String, label: String, color: Color, icon: ImageVector) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Icon(icon, contentDescription = null, tint = color, modifier = Modifier.size(20.dp))
        Spacer(modifier = Modifier.height(4.dp))
        Text(text = value, fontSize = 18.sp, fontWeight = FontWeight.Bold, color = Color.White)
        Text(text = label, fontSize = 10.sp, color = Color.Gray)
    }
}

@Composable
fun VerticalDivider() {
    Box(
        modifier = Modifier
            .height(30.dp)
            .width(1.dp)
            .background(Color.Gray.copy(alpha = 0.3f))
    )
}
