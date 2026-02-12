package com.healthai.app.ui.screens.results

// --- FIX: Added missing imports below ---
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable // Fixed: Added clickable import
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape // Fixed: Added CircleShape import
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.Description
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.healthai.app.ui.screens.dashboard.HelpixBottomNav
import kotlin.math.cos
import kotlin.math.sin

// --- EXACT COLORS FROM SCREENSHOTS ---
val BgDark = Color(0xFF0B1221) // Deep Blue Background
val CardBg = Color(0xFF131B2C) // Slightly lighter card bg
val TextWhite = Color.White
val TextGray = Color(0xFF8F9BB3)

val StatusGreen = Color(0xFF00C853) // Clear
val StatusYellow = Color(0xFFFFAB00) // Monitor
val StatusRed = Color(0xFFFF1744)   // Action

val BarCyan = Color(0xFF00E5FF) // Probability Bar
val BarBlue = Color(0xFF2979FF) // Confidence Bar
val ChartGreen = Color(0xFF00E676) // Confidence Column in Chart
val ChartCyan = Color(0xFF00B0FF)  // Probability Column in Chart

@Composable
fun ResultsScreen(navController: NavController) {
    val scrollState = rememberScrollState()

    Scaffold(
        bottomBar = { HelpixBottomNav(navController = navController) },
        containerColor = BgDark
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .background(BgDark)
                .verticalScroll(scrollState)
                .padding(16.dp)
        ) {
            // --- HEADER ---
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth().padding(bottom = 20.dp)
            ) {
                // Fixed: Clickable is now imported correctly
                Icon(
                    imageVector = Icons.Default.ArrowBack,
                    contentDescription = "Back",
                    tint = TextWhite,
                    modifier = Modifier
                        .size(24.dp)
                        .clickable { navController.popBackStack() }
                )
                Spacer(modifier = Modifier.width(16.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Text("Multi-Disease AI Analysis", color = BarCyan, fontSize = 20.sp, fontWeight = FontWeight.Bold)
                    Text("Complete health diagnostic report", color = TextGray, fontSize = 12.sp)
                }
                Button(
                    onClick = {},
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF1E293B)),
                    shape = RoundedCornerShape(8.dp),
                    contentPadding = PaddingValues(horizontal = 12.dp, vertical = 8.dp)
                ) {
                    Icon(Icons.Outlined.Description, null, tint = BarCyan, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("View Report", color = BarCyan, fontSize = 12.sp)
                }
            }

            // --- AI ANALYSIS CARD ---
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(16.dp))
                    .background(CardBg)
                    .border(1.dp, Color.White.copy(alpha = 0.05f), RoundedCornerShape(16.dp))
                    .padding(16.dp)
            ) {
                Column {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(40.dp)
                                .background(BarCyan.copy(alpha = 0.1f), RoundedCornerShape(10.dp)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(Icons.Default.Psychology, null, tint = BarCyan)
                        }
                        Spacer(modifier = Modifier.width(12.dp))
                        Column {
                            Text("AI Analysis Complete", color = TextWhite, fontSize = 16.sp, fontWeight = FontWeight.SemiBold)
                            Text("Powered by advanced machine learning", color = TextGray, fontSize = 12.sp)
                        }
                    }
                    Spacer(modifier = Modifier.height(16.dp))
                    Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                        StatusBox("4", "Clear", StatusGreen, Icons.Default.CheckCircle, Modifier.weight(1f))
                        StatusBox("1", "Monitor", StatusYellow, Icons.Default.Warning, Modifier.weight(1f))
                        StatusBox("1", "Action", StatusRed, Icons.Default.TrendingUp, Modifier.weight(1f))
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // --- DISEASE CARDS GRID ---
            // Row 1
            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                DiseaseCard("TB Detection", 8, 92, "Clear", StatusGreen, Modifier.weight(1f))
                DiseaseCard("Pneumonia", 15, 88, "Monitor", StatusYellow, Modifier.weight(1f))
            }
            Spacer(modifier = Modifier.height(12.dp))
            // Row 2
            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                DiseaseCard("Malaria Risk", 12, 85, "Monitor", StatusYellow, Modifier.weight(1f))
                DiseaseCard("Typhoid Risk", 10, 90, "Clear", StatusGreen, Modifier.weight(1f))
            }
            Spacer(modifier = Modifier.height(12.dp))
            // Row 3
            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                DiseaseCard("Skin Infection", 25, 80, "Review", StatusYellow, Modifier.weight(1f))
                DiseaseCard("Fever Category", 72, 95, "Action Needed", StatusRed, Modifier.weight(1f))
            }

            Spacer(modifier = Modifier.height(24.dp))

            // --- RADAR CHART (Risk Assessment) ---
            ChartContainer("Risk Assessment Overview") {
                RadarChartHexagon()
            }

            Spacer(modifier = Modifier.height(24.dp))

            // --- BAR CHART (Probability vs Confidence) ---
            ChartContainer("Probability vs Confidence") {
                Column {
                    BarChartGrouped()
                    Spacer(modifier = Modifier.height(16.dp))
                    // Legend
                    Row(horizontalArrangement = Arrangement.Center, modifier = Modifier.fillMaxWidth()) {
                        LegendItem(ChartGreen, "Confidence %")
                        Spacer(modifier = Modifier.width(16.dp))
                        LegendItem(ChartCyan, "Probability %")
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // --- RECOMMENDATIONS ---
            Text("Recommended Actions", color = BarCyan, fontSize = 16.sp, modifier = Modifier.padding(bottom = 12.dp))
            
            ActionCard(
                "Fever detected - High Priority", 
                "Consult a doctor immediately. Monitor temperature regularly.", 
                StatusRed
            )
            Spacer(modifier = Modifier.height(12.dp))
            ActionCard(
                "Skin condition requires review", 
                "Schedule a dermatologist appointment. Avoid exposure to irritants.", 
                StatusYellow
            )
            Spacer(modifier = Modifier.height(12.dp))
            ActionCard(
                "Other conditions look normal", 
                "Continue regular health monitoring and maintain a healthy lifestyle.", 
                StatusGreen
            )

            Spacer(modifier = Modifier.height(24.dp))

            // --- DOWNLOAD BUTTON ---
            Button(
                onClick = {},
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp)
                    .border(1.dp, BarCyan.copy(alpha = 0.3f), RoundedCornerShape(12.dp)),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF0F2040)),
                shape = RoundedCornerShape(12.dp)
            ) {
                Icon(Icons.Default.Download, null, tint = BarCyan)
                Spacer(modifier = Modifier.width(8.dp))
                Text("Download Complete Report", color = BarCyan, fontSize = 16.sp)
                Spacer(modifier = Modifier.width(8.dp))
                Text("Get a detailed PDF", color = TextGray, fontSize = 12.sp)
            }
            
            Spacer(modifier = Modifier.height(80.dp))
        }
    }
}

// ================== COMPONENTS ==================

@Composable
fun ChartContainer(title: String, content: @Composable () -> Unit) {
    Column {
        Text(title, color = BarCyan, fontSize = 16.sp, modifier = Modifier.padding(bottom = 12.dp))
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(16.dp))
                .background(CardBg)
                .border(1.dp, Color.White.copy(alpha = 0.05f), RoundedCornerShape(16.dp))
                .padding(20.dp),
            contentAlignment = Alignment.Center
        ) {
            content()
        }
    }
}

@Composable
fun StatusBox(count: String, label: String, color: Color, icon: androidx.compose.ui.graphics.vector.ImageVector, modifier: Modifier) {
    Column(
        modifier = modifier
            .border(1.dp, color.copy(alpha = 0.3f), RoundedCornerShape(12.dp))
            .background(color.copy(alpha = 0.05f), RoundedCornerShape(12.dp))
            .padding(vertical = 12.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Icon(icon, null, tint = color, modifier = Modifier.size(20.dp))
        Spacer(modifier = Modifier.height(8.dp))
        Text(count, color = color, fontSize = 20.sp, fontWeight = FontWeight.Bold)
        Text(label, color = TextGray, fontSize = 12.sp)
    }
}

@Composable
fun DiseaseCard(name: String, prob: Int, conf: Int, status: String, color: Color, modifier: Modifier) {
    Column(
        modifier = modifier
            .background(CardBg, RoundedCornerShape(12.dp))
            .border(1.dp, Color.White.copy(alpha = 0.05f), RoundedCornerShape(12.dp))
            .padding(12.dp)
    ) {
        Row(horizontalArrangement = Arrangement.SpaceBetween, modifier = Modifier.fillMaxWidth()) {
            Text(name, color = color, fontSize = 13.sp, fontWeight = FontWeight.Bold, maxLines = 1)
            Box(
                modifier = Modifier
                    .background(color.copy(alpha = 0.2f), RoundedCornerShape(4.dp))
                    .padding(horizontal = 6.dp, vertical = 2.dp)
            ) {
                Text(status, color = color, fontSize = 10.sp)
            }
        }
        Spacer(modifier = Modifier.height(12.dp))
        
        Text("Detection Probability ${prob}%", color = TextGray, fontSize = 10.sp)
        LinearProgressIndicator(
            progress = prob / 100f,
            color = BarCyan,
            trackColor = Color(0xFF1E293B),
            strokeCap = StrokeCap.Round,
            modifier = Modifier.fillMaxWidth().height(4.dp)
        )
        Spacer(modifier = Modifier.height(8.dp))
        
        Text("AI Confidence ${conf}%", color = TextGray, fontSize = 10.sp)
        LinearProgressIndicator(
            progress = conf / 100f,
            color = BarBlue,
            trackColor = Color(0xFF1E293B),
            strokeCap = StrokeCap.Round,
            modifier = Modifier.fillMaxWidth().height(4.dp)
        )
        Spacer(modifier = Modifier.height(8.dp))
        
        Row(verticalAlignment = Alignment.CenterVertically) {
            // Fixed: CircleShape is now imported correctly
            Box(modifier = Modifier.size(6.dp).background(color, CircleShape))
            Spacer(modifier = Modifier.width(4.dp))
            Text("Urgency: ${if (status == "Action Needed") "High" else if(status == "Monitor") "Medium" else "Low"}", color = Color.Gray, fontSize = 10.sp)
        }
    }
}

@Composable
fun ActionCard(title: String, subtitle: String, color: Color) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .border(1.dp, color.copy(alpha = 0.3f), RoundedCornerShape(12.dp))
            .background(color.copy(alpha = 0.05f), RoundedCornerShape(12.dp))
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(Icons.Default.Warning, null, tint = color)
        Spacer(modifier = Modifier.width(12.dp))
        Column {
            Text(title, color = color, fontSize = 14.sp, fontWeight = FontWeight.Bold)
            Text(subtitle, color = TextGray, fontSize = 12.sp)
        }
    }
}

@Composable
fun LegendItem(color: Color, text: String) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Box(modifier = Modifier.size(10.dp).background(color))
        Spacer(modifier = Modifier.width(4.dp))
        Text(text, color = TextGray, fontSize = 12.sp)
    }
}

// ================== CUSTOM DRAWINGS ==================

@Composable
fun RadarChartHexagon() {
    Canvas(modifier = Modifier.size(200.dp)) {
        val center = Offset(size.width / 2, size.height / 2)
        val radius = size.width / 2.2f
        
        // Draw Web (Hexagons)
        val paintStroke = Stroke(width = 1.dp.toPx())
        val webColor = Color.Gray.copy(alpha = 0.3f)
        
        for (i in 1..4) {
            val r = radius * (i / 4f)
            val path = Path()
            for (j in 0 until 6) {
                val angle = Math.toRadians((60 * j - 90).toDouble())
                val x = center.x + (r * cos(angle)).toFloat()
                val y = center.y + (r * sin(angle)).toFloat()
                if (j == 0) path.moveTo(x, y) else path.lineTo(x, y)
            }
            path.close()
            drawPath(path, webColor, style = paintStroke)
        }
        
        // Draw Spokes
        for (j in 0 until 6) {
            val angle = Math.toRadians((60 * j - 90).toDouble())
            val x = center.x + (radius * cos(angle)).toFloat()
            val y = center.y + (radius * sin(angle)).toFloat()
            drawLine(webColor, center, Offset(x, y), strokeWidth = 1.dp.toPx())
        }

        // Draw Data Shape (Dummy Data matching screenshot)
        // Order: TB, Pneumonia, Malaria, Typhoid, Skin, Fever
        val values = listOf(0.1f, 0.3f, 0.2f, 0.1f, 0.4f, 0.8f) 
        val dataPath = Path()
        
        for (j in 0 until 6) {
            val angle = Math.toRadians((60 * j - 90).toDouble())
            val r = radius * values[j]
            val x = center.x + (r * cos(angle)).toFloat()
            val y = center.y + (r * sin(angle)).toFloat()
            if (j == 0) dataPath.moveTo(x, y) else dataPath.lineTo(x, y)
        }
        dataPath.close()
        
        drawPath(dataPath, BarCyan.copy(alpha = 0.3f)) // Fill
        drawPath(dataPath, BarCyan, style = Stroke(width = 2.dp.toPx())) // Border
        
        // Labels (Manual placement for simplicity)
    }
    // Overlay Labels manually for simplicity
    Box(Modifier.size(200.dp)) {
        Text("TB", color = TextGray, fontSize = 10.sp, modifier = Modifier.align(Alignment.TopCenter))
        Text("Pneumonia", color = TextGray, fontSize = 10.sp, modifier = Modifier.align(Alignment.TopEnd).padding(top=40.dp))
        Text("Malaria", color = TextGray, fontSize = 10.sp, modifier = Modifier.align(Alignment.BottomEnd).padding(bottom=40.dp))
        Text("Typhoid", color = TextGray, fontSize = 10.sp, modifier = Modifier.align(Alignment.BottomCenter))
        Text("Skin", color = TextGray, fontSize = 10.sp, modifier = Modifier.align(Alignment.BottomStart).padding(bottom=40.dp))
        Text("Fever", color = TextGray, fontSize = 10.sp, modifier = Modifier.align(Alignment.TopStart).padding(top=40.dp))
    }
}

@Composable
fun BarChartGrouped() {
    val data = listOf(
        Pair(95f, 10f), // TB
        Pair(90f, 15f), // Pneumonia
        Pair(85f, 12f), // Malaria
        Pair(92f, 10f), // Typhoid
        Pair(80f, 25f), // Skin
        Pair(98f, 72f)  // Fever
    )
    val labels = listOf("TB", "Pneu", "Mal", "Typh", "Skin", "Fev")
    
    Row(
        modifier = Modifier.fillMaxWidth().height(150.dp),
        horizontalArrangement = Arrangement.SpaceEvenly,
        verticalAlignment = Alignment.Bottom
    ) {
        data.forEachIndexed { index, (conf, prob) ->
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Bottom,
                modifier = Modifier.fillMaxHeight().weight(1f)
            ) {
                Row(
                    verticalAlignment = Alignment.Bottom,
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    // Confidence Bar (Green)
                    Box(
                        modifier = Modifier
                            .width(8.dp)
                            .fillMaxHeight(conf / 100f)
                            .background(ChartGreen, RoundedCornerShape(topStart = 2.dp, topEnd = 2.dp))
                    )
                    // Probability Bar (Cyan)
                    Box(
                        modifier = Modifier
                            .width(8.dp)
                            .fillMaxHeight(prob / 100f)
                            .background(ChartCyan, RoundedCornerShape(topStart = 2.dp, topEnd = 2.dp))
                    )
                }
                Spacer(modifier = Modifier.height(4.dp))
                Text(labels[index], color = TextGray, fontSize = 10.sp, maxLines = 1)
            }
        }
    }
}