package com.healthai.app.ui.screens.symptom

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.healthai.app.data.remote.api.DiagnosisResult
import com.healthai.app.ui.navigation.NavRoutes
import com.healthai.app.ui.viewmodel.SymptomViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SymptomResultScreen(
    navController: NavController,
    viewModel: SymptomViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val result = uiState.analysisResult
    val scrollState = rememberScrollState()

    val triageLevel = result?.triage_level ?: "Green"
    val triageColor = when (triageLevel) {
        "Red" -> Color(0xFFEF4444)
        "Yellow" -> Color(0xFFF59E0B)
        else -> Color(0xFF10B981)
    }

    val triageTitle = when (triageLevel) {
        "Red" -> "Urgent Medical Attention Advised"
        "Yellow" -> "Specialist Consultation Recommended"
        else -> "Mild Risk — Home Care & Monitoring"
    }

    val triageDescription = when (triageLevel) {
        "Red" -> "Aapke lakshano ke aadhar par turant doctor ya emergency se salah lene ki aavashyakta hai."
        "Yellow" -> "Salah di jaati hai ki aap 24-48 ghante ke andar ek specialist doctor se milein."
        else -> "Aap ghar par in lakshano ki dekhbhal kar sakte hain. Agar lakshan badhein to doctor se sampark karein."
    }

    val primaryCondition = result?.predicted_condition 
        ?: result?.possible_conditions?.firstOrNull()?.condition 
        ?: "General Health Assessment"

    val confidence = result?.confidence_score 
        ?: ((result?.possible_conditions?.firstOrNull()?.probability ?: 0.8) * 100)

    val specialist = result?.recommended_specialist ?: "General Physician"

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("AI Diagnostic Assessment", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack(NavRoutes.SymptomDoctorStart, inclusive = false) }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back", tint = Color.White)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color(0xFF0B1221),
                    titleContentColor = Color.White
                )
            )
        },
        containerColor = Color(0xFF0B1221)
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .verticalScroll(scrollState)
                .padding(16.dp)
        ) {
            // Triage Alert Banner
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = triageColor.copy(alpha = 0.15f)),
                border = androidx.compose.foundation.BorderStroke(1.5.dp, triageColor)
            ) {
                Row(
                    modifier = Modifier.padding(16.dp),
                    verticalAlignment = Alignment.Top
                ) {
                    Box(
                        modifier = Modifier
                            .size(36.dp)
                            .clip(CircleShape)
                            .background(triageColor),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = if (triageLevel == "Red") Icons.Default.Warning else Icons.Default.Shield,
                            contentDescription = null,
                            tint = Color.White,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(12.dp))
                    Column {
                        Text(triageTitle, color = triageColor, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                        Spacer(modifier = Modifier.height(3.dp))
                        Text(triageDescription, color = Color(0xFFE2E8F0), fontSize = 12.sp, lineHeight = 16.sp)
                    }
                }
            }

            Spacer(modifier = Modifier.height(18.dp))

            // Primary Diagnosis Card
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFF1E293B))
            ) {
                Column(modifier = Modifier.padding(20.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "PRELIMINARY ML DIAGNOSIS",
                            color = Color(0xFF60A5FA),
                            fontSize = 11.sp,
                            fontWeight = FontWeight.ExtraBold,
                            letterSpacing = 1.sp
                        )
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(8.dp))
                                .background(Color(0xFF2563EB).copy(alpha = 0.2f))
                                .padding(horizontal = 8.dp, vertical = 4.dp)
                        ) {
                            Text("Risk: ${result?.overall_risk?.capitalize() ?: "Moderate"}", color = Color(0xFF93C5FD), fontSize = 11.sp, fontWeight = FontWeight.Bold)
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    Text(
                        text = primaryCondition,
                        color = Color.White,
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Bold
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    // Confidence Score Meter
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("Model Confidence", color = Color.Gray, fontSize = 12.sp)
                        Text(
                            text = "${"%.1f".format(confidence)}%",
                            color = Color(0xFF38BDF8),
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                    Spacer(modifier = Modifier.height(6.dp))
                    LinearProgressIndicator(
                        progress = { (confidence / 100f).toFloat().coerceIn(0.1f, 1f) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(8.dp)
                            .clip(RoundedCornerShape(4.dp)),
                        color = Color(0xFF2563EB),
                        trackColor = Color(0xFF334155)
                    )

                    Spacer(modifier = Modifier.height(18.dp))
                    Divider(color = Color(0xFF334155))
                    Spacer(modifier = Modifier.height(14.dp))

                    // Recommended Specialist Box
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(12.dp))
                            .background(Color(0xFF0F172A))
                            .padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(Icons.Default.MedicalServices, contentDescription = null, tint = Color(0xFF38BDF8), modifier = Modifier.size(24.dp))
                        Spacer(modifier = Modifier.width(10.dp))
                        Column {
                            Text("Recommended Specialist", color = Color.Gray, fontSize = 11.sp)
                            Text(specialist, color = Color.White, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Top Differential Diagnoses
            val otherConditions = result?.possible_conditions ?: emptyList()
            if (otherConditions.isNotEmpty()) {
                Text(
                    text = "Potential Differential Conditions",
                    color = Color.White,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(10.dp))

                otherConditions.forEach { cond ->
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp),
                        shape = RoundedCornerShape(14.dp),
                        colors = CardDefaults.cardColors(containerColor = Color(0xFF1E293B))
                    ) {
                        Column(modifier = Modifier.padding(14.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(cond.condition, color = Color.White, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                                val matchPct = (cond.probability * 100).toInt().coerceAtLeast(15)
                                Text("$matchPct% Match", color = Color(0xFF38BDF8), fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
                            }
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(cond.recommendation, color = Color(0xFF94A3B8), fontSize = 12.sp, lineHeight = 16.sp)
                        }
                    }
                }
                Spacer(modifier = Modifier.height(18.dp))
            }

            // Google Gemini Clinical Advice Card
            if (!result?.gemini_explanation.isNullOrBlank()) {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFF0F172A)),
                    border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF334155))
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(28.dp)
                                    .clip(CircleShape)
                                    .background(Color(0xFF2563EB)),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(Icons.Default.AutoAwesome, contentDescription = null, tint = Color.White, modifier = Modifier.size(16.dp))
                            }
                            Spacer(modifier = Modifier.width(10.dp))
                            Text("Helpix AI Clinical Summary", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                        }
                        Spacer(modifier = Modifier.height(10.dp))
                        Text(
                            text = result?.gemini_explanation ?: "",
                            color = Color(0xFFCBD5E1),
                            fontSize = 12.sp,
                            lineHeight = 18.sp
                        )
                    }
                }
                Spacer(modifier = Modifier.height(24.dp))
            }

            // Action Buttons
            Button(
                onClick = { navController.navigate(NavRoutes.Doctors) },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp),
                shape = RoundedCornerShape(14.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF2563EB))
            ) {
                Icon(Icons.Default.CalendarMonth, contentDescription = null)
                Spacer(modifier = Modifier.width(8.dp))
                Text("Find & Consult a $specialist", fontWeight = FontWeight.Bold, fontSize = 14.sp)
            }

            Spacer(modifier = Modifier.height(10.dp))

            OutlinedButton(
                onClick = { navController.navigate(NavRoutes.SymptomChat) },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp),
                shape = RoundedCornerShape(14.dp),
                border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF38BDF8))
            ) {
                Icon(Icons.Default.Chat, contentDescription = null, tint = Color(0xFF38BDF8))
                Spacer(modifier = Modifier.width(8.dp))
                Text("Ask AI Doctor More Questions", color = Color(0xFF38BDF8), fontWeight = FontWeight.Bold, fontSize = 14.sp)
            }

            Spacer(modifier = Modifier.height(20.dp))
        }
    }
}