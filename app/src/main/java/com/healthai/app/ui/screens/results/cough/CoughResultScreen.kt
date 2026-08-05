package com.healthai.app.ui.screens.results.cough

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.MedicalServices
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.healthai.app.ui.navigation.NavRoutes

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CoughResultScreen(navController: NavController) {

    val probability = 0f 
    val suggestionText: String
    val suggestionColor: Color
    val riskLevel: String

    if (probability == 0f) {
        suggestionText = "No analysis performed yet."
        suggestionColor = Color.Gray
        riskLevel = "NO DATA"
    } else if (probability > 0.6) {
        suggestionText = "Aapko turant nazdeeki jaanch kendra par jaakar CBNAAT/Sputum (balgam) Test karwana chahiye."
        suggestionColor = Color(0xFFFF4B4B)
        riskLevel = "HIGH RISK"
    } else if (probability > 0.3) {
        suggestionText = "Behtar hoga ki aap ek doctor se salah lein. Apne doosre lakshano par bhi dhyan dein."
        suggestionColor = Color(0xFFFFD700)
        riskLevel = "MODERATE RISK"
    } else {
        suggestionText = "Abhi chinta ki baat nahi lagti. Agar aapko anya lakshan hain, to doctor se milna chahiye."
        suggestionColor = Color(0xFF00E676)
        riskLevel = "LOW RISK"
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { 
                    Text("Cough Analysis", fontWeight = FontWeight.Bold) 
                },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background, 
                    titleContentColor = Color.White, 
                    navigationIconContentColor = Color.White,
                )
            )
        },
        containerColor = MaterialTheme.colorScheme.background
    ) { paddingValues ->
        Box(modifier = Modifier.fillMaxSize()) {
            // Background Glows
            Box(modifier = Modifier.size(300.dp).align(Alignment.TopEnd).offset(x = 100.dp, y = (-50).dp).blur(100.dp).background(suggestionColor.copy(alpha = 0.15f), CircleShape))
            
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .verticalScroll(rememberScrollState())
                    .padding(20.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Spacer(modifier = Modifier.height(20.dp))
                
                // Main Gauge Card
                GlassCard(
                    modifier = Modifier.fillMaxWidth().padding(bottom = 24.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(24.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            "AI Probability Meter", 
                            color = Color.White.copy(alpha = 0.7f), 
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Medium
                        )
                        Spacer(modifier = Modifier.height(24.dp))
                        
                        Box(contentAlignment = Alignment.Center, modifier = Modifier.size(220.dp)) {
                            ProbabilityGauge(progress = probability, color = suggestionColor)
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Text(
                                    "${(probability * 100).toInt()}%", 
                                    color = Color.White, 
                                    fontSize = 48.sp, 
                                    fontWeight = FontWeight.ExtraBold
                                )
                                Text(
                                    riskLevel, 
                                    color = suggestionColor, 
                                    fontSize = 14.sp, 
                                    fontWeight = FontWeight.Bold,
                                    letterSpacing = 1.sp
                                )
                            }
                        }
                    }
                }
                
                // Detailed Breakdown Card
                GlassCard(modifier = Modifier.fillMaxWidth().padding(bottom = 16.dp)) {
                    Column(Modifier.padding(20.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.Info, contentDescription = null, tint = Color.Cyan, modifier = Modifier.size(20.dp))
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Aisa Kyun Lag Raha Hai?", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                        }
                        Spacer(modifier = Modifier.height(12.dp))
                        Text(
                            "Hamare AI ne aapki khaansi mein kuch aise patterns paaye hain (jaise 'geeli' awaaz aur lamba chalne wali khaansi) jo aksar TB ke mamlon mein dekhe jaate hain. Yeh analysis 10,000+ clinical samples par trained hai.", 
                            color = Color.White.copy(alpha = 0.7f), 
                            fontSize = 13.sp,
                            lineHeight = 20.sp
                        )
                    }
                }

                // Actionable Suggestion
                Card(
                    colors = CardDefaults.cardColors(containerColor = suggestionColor.copy(alpha = 0.1f)),
                    shape = RoundedCornerShape(20.dp),
                    border = BorderStroke(1.dp, suggestionColor.copy(alpha = 0.3f)),
                    modifier = Modifier.fillMaxWidth().padding(bottom = 32.dp)
                ) {
                    Row(Modifier.padding(20.dp), verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier.size(40.dp).background(suggestionColor.copy(alpha = 0.2f), CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(Icons.Default.MedicalServices, contentDescription = null, tint = suggestionColor, modifier = Modifier.size(20.dp))
                        }
                        Spacer(modifier = Modifier.width(16.dp))
                        Column {
                            Text("What should you do?", color = suggestionColor, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                            Text(suggestionText, color = Color.White, fontSize = 13.sp, lineHeight = 18.sp)
                        }
                    }
                }

                Button(
                    onClick = { navController.navigate(NavRoutes.Doctors) },
                    modifier = Modifier.fillMaxWidth().height(56.dp),
                    shape = RoundedCornerShape(16.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary),
                    elevation = ButtonDefaults.buttonElevation(defaultElevation = 8.dp)
                ) {
                    Text("Find a Test Center Nearby", color = MaterialTheme.colorScheme.onPrimary, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                }
                
                Spacer(modifier = Modifier.height(16.dp))
                
                OutlinedButton(
                    onClick = { navController.popBackStack(route = NavRoutes.Dashboard, inclusive = false) },
                    modifier = Modifier.fillMaxWidth().height(56.dp),
                    shape = RoundedCornerShape(16.dp),
                    border = BorderStroke(1.dp, Color.White.copy(alpha = 0.3f))
                ) {
                    Text("Return to Dashboard", color = Color.White)
                }
                
                Spacer(modifier = Modifier.height(40.dp))
            }
        }
    }
}

@Composable
fun GlassCard(modifier: Modifier = Modifier, content: @Composable () -> Unit) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(28.dp))
            .background(Color.White.copy(alpha = 0.05f))
            .border(1.dp, Color.White.copy(alpha = 0.1f), RoundedCornerShape(28.dp))
    ) {
        content()
    }
}

@Composable
fun ProbabilityGauge(progress: Float, color: Color) {
    val animatedProgress by animateFloatAsState(targetValue = progress, animationSpec = tween(1500))
    Canvas(modifier = Modifier.fillMaxSize()) {
        val strokeWidth = 45f
        
        // Background track
        drawArc(
            color = Color.White.copy(alpha = 0.05f),
            startAngle = 140f,
            sweepAngle = 260f,
            useCenter = false,
            style = Stroke(width = strokeWidth, cap = StrokeCap.Round)
        )
        
        // Progress arc with glow
        drawArc(
            color = color,
            startAngle = 140f,
            sweepAngle = 260 * animatedProgress,
            useCenter = false,
            style = Stroke(width = strokeWidth, cap = StrokeCap.Round)
        )
        
        // Dot indicator at the end of progress
        // Calculate position (simplified for now)
    }
}
