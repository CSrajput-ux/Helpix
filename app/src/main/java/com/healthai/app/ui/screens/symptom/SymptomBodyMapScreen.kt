package com.healthai.app.ui.screens.symptom

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.healthai.app.ui.navigation.NavRoutes
import com.healthai.app.ui.viewmodel.SymptomViewModel

data class BodyPartSection(
    val name: String,
    val icon: String,
    val symptoms: List<Pair<String, String>>
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SymptomBodyMapScreen(
    navController: NavController,
    viewModel: SymptomViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    var selectedBodyPart by remember { mutableStateOf<String?>("Head & Brain") }

    val bodyParts = listOf(
        BodyPartSection(
            "Head & Brain",
            "🧠",
            listOf(
                "headache" to "Headache",
                "dizziness" to "Dizziness",
                "loss_of_balance" to "Loss of Balance",
                "blurred_and_distorted_vision" to "Blurred Vision",
                "altered_sensorium" to "Confusion / Memory Issue"
            )
        ),
        BodyPartSection(
            "Throat & Neck",
            "🗣️",
            listOf(
                "throat_irritation" to "Sore Throat",
                "patches_in_throat" to "Throat Patches",
                "continuous_sneezing" to "Continuous Sneezing",
                "cough" to "Cough",
                "stiff_neck" to "Stiff Neck"
            )
        ),
        BodyPartSection(
            "Chest & Lungs",
            "🫁",
            listOf(
                "chest_pain" to "Chest Pain",
                "breathlessness" to "Shortness of Breath",
                "fast_heart_rate" to "Fast Heartbeat / Palpitations",
                "cough" to "Dry / Wet Cough",
                "mucoid_sputum" to "Phlegm / Sputum"
            )
        ),
        BodyPartSection(
            "Stomach & Digestion",
            "🤢",
            listOf(
                "stomach_pain" to "Stomach Pain",
                "acidity" to "Acidity / Heartburn",
                "vomiting" to "Vomiting",
                "nausea" to "Nausea",
                "diarrhoea" to "Diarrhea",
                "constipation" to "Constipation",
                "loss_of_appetite" to "Loss of Appetite"
            )
        ),
        BodyPartSection(
            "Limbs & Joints",
            "🦴",
            listOf(
                "joint_pain" to "Joint Pain",
                "muscle_pain" to "Muscle / Body Pain",
                "knee_pain" to "Knee Pain",
                "muscle_weakness" to "Muscle Weakness",
                "swelling_joints" to "Swelling in Joints",
                "movement_stiffness" to "Stiffness"
            )
        ),
        BodyPartSection(
            "Skin & Whole Body",
            "🌡️",
            listOf(
                "high_fever" to "High Fever",
                "chills" to "Chills / Shivering",
                "fatigue" to "Fatigue / Weakness",
                "itching" to "Itching",
                "skin_rash" to "Skin Rash",
                "sweating" to "Excessive Sweating"
            )
        )
    )

    val currentSection = bodyParts.find { it.name == selectedBodyPart } ?: bodyParts.first()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Select by Body Part", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back", tint = Color.White)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color(0xFF0B1221),
                    titleContentColor = Color.White
                )
            )
        },
        bottomBar = {
            if (uiState.selectedSymptoms.isNotEmpty()) {
                Surface(
                    color = Color(0xFF1E293B),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column {
                            Text("${uiState.selectedSymptoms.size} Symptoms Selected", color = Color.White, fontWeight = FontWeight.Bold)
                            Text("Ready for AI Diagnosis", color = Color(0xFF10B981), fontSize = 12.sp)
                        }
                        Button(
                            onClick = {
                                viewModel.analyzeSymptoms { success ->
                                    if (success) navController.navigate(NavRoutes.SymptomAnalysis)
                                }
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF2563EB)),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Text("Diagnose", fontWeight = FontWeight.Bold)
                            Spacer(modifier = Modifier.width(4.dp))
                            Icon(Icons.Default.ArrowForward, contentDescription = null, modifier = Modifier.size(16.dp))
                        }
                    }
                }
            }
        },
        containerColor = Color(0xFF0B1221)
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp)
        ) {
            Text(
                text = "Tap a body part to select your symptoms:",
                color = Color.Gray,
                fontSize = 13.sp
            )
            Spacer(modifier = Modifier.height(12.dp))

            // Body Parts Horizontal Selector
            LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                items(bodyParts) { part ->
                    val isSelected = part.name == selectedBodyPart
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(14.dp))
                            .background(if (isSelected) Color(0xFF2563EB) else Color(0xFF1E293B))
                            .border(1.dp, if (isSelected) Color(0xFF60A5FA) else Color(0xFF334155), RoundedCornerShape(14.dp))
                            .clickable { selectedBodyPart = part.name }
                            .padding(horizontal = 14.dp, vertical = 10.dp)
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(part.icon, fontSize = 16.sp)
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                part.name,
                                color = if (isSelected) Color.White else Color(0xFFCBD5E1),
                                fontSize = 13.sp,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            Text(
                text = "${currentSection.name} Symptoms:",
                color = Color.White,
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(10.dp))

            // Grid of symptoms for selected body part
            LazyVerticalGrid(
                columns = GridCells.Fixed(2),
                horizontalArrangement = Arrangement.spacedBy(10.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp),
                modifier = Modifier.weight(1f)
            ) {
                items(currentSection.symptoms) { (key, label) ->
                    val isSelected = uiState.selectedSymptoms.contains(key)
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { viewModel.toggleSymptom(key) },
                        shape = RoundedCornerShape(14.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = if (isSelected) Color(0xFF2563EB).copy(alpha = 0.25f) else Color(0xFF1E293B)
                        ),
                        border = androidx.compose.foundation.BorderStroke(
                            1.dp,
                            if (isSelected) Color(0xFF3B82F6) else Color(0xFF334155)
                        )
                    ) {
                        Row(
                            modifier = Modifier.padding(14.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(
                                text = label,
                                color = if (isSelected) Color(0xFF93C5FD) else Color.White,
                                fontSize = 13.sp,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                modifier = Modifier.weight(1f)
                            )
                            Icon(
                                imageVector = if (isSelected) Icons.Default.CheckCircle else Icons.Default.AddCircleOutline,
                                contentDescription = null,
                                tint = if (isSelected) Color(0xFF3B82F6) else Color.Gray,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                    }
                }
            }
        }
    }
}