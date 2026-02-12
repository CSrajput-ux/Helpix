package com.healthai.app.ui.screens.diet

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
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
import androidx.navigation.NavController

data class Meal(val name: String, val description: String, val calories: Int, val time: String, var isEaten: Boolean = false)

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun DietPlannerScreen(navController: NavController) {

    // In a real app, this would come from a ViewModel and be based on user's goals
    val dailyPlan = listOf(
        Meal("Breakfast", "2 Poha with Peanuts", 350, "08:00 AM"),
        Meal("Lunch", "2 Roti, 1 katori Dal Palak, Salad", 500, "01:00 PM"),
        Meal("Snack", "1 Apple & Green Tea", 100, "04:30 PM"),
        Meal("Dinner", "1 bowl Khichdi with Ghee", 450, "08:00 PM")
    )

    var showSetup by remember { mutableStateOf(true) } // Show setup on first launch

    if (showSetup) {
        DietSetupScreen(onSetupComplete = { showSetup = false })
    } else {
        Scaffold(
            topBar = {
                TopAppBar(
                    title = { Text("AI Diet Planner") },
                    navigationIcon = {
                        IconButton(onClick = { navController.popBackStack() }) {
                            Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                        }
                    },
                    colors = TopAppBarDefaults.topAppBarColors(containerColor = Color(0xFF0B1221), titleContentColor = Color.White, navigationIconContentColor = Color.White)
                )
            },
            containerColor = Color(0xFF0B1221)
        ) { paddingValues ->
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                item { CalorieProgress() }
                items(dailyPlan) { meal ->
                    MealCard(meal)
                }
            }
        }
    }
}

@Composable
fun CalorieProgress() {
    // Dummy Progress
    Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.fillMaxWidth().padding(vertical = 16.dp)) {
        Text("Today's Goal: 1800 Cal", color = Color.White)
        Spacer(modifier = Modifier.height(8.dp))
        LinearProgressIndicator(
            progress = 0.4f, // 650 Cal / 1800 Cal
            modifier = Modifier.fillMaxWidth().height(8.dp).clip(RoundedCornerShape(4.dp)),
            color = Color(0xFF76FF03),
            trackColor = Color(0xFF1E293B)
        )
        Text("650 Cal consumed", color = Color.Gray, fontSize = 12.sp)
    }
}

@Composable
fun MealCard(meal: Meal) {
    var isEaten by remember { mutableStateOf(meal.isEaten) }
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFF1E293B))
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(meal.name, color = Color.White, fontWeight = FontWeight.Bold, fontSize = 18.sp)
            Text(meal.time, color = Color.Gray, fontSize = 12.sp)
            Divider(modifier = Modifier.padding(vertical = 8.dp), color = Color(0xFF334155))
            Row(verticalAlignment = Alignment.CenterVertically) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(meal.description, color = Color.White)
                    Text("~${meal.calories} Cal", color = Color(0xFF76FF03), fontSize = 12.sp)
                }
                Checkbox(
                    checked = isEaten,
                    onCheckedChange = { isEaten = it },
                    colors = CheckboxDefaults.colors(checkedColor = Color(0xFF76FF03))
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun DietSetupScreen(onSetupComplete: () -> Unit) {
    val goals = listOf("Vajan Kam Karna", "Vajan Badhana", "Sugar Control Karna", "BP Control Karna", "Swasth Rehna")
    val preferences = listOf("Shakahari (Veg)", "Masahari (Non-Veg)", "Ande Khate Hain (Eggetarian)")
    
    var selectedGoal by remember { mutableStateOf<String?>(null) }
    var selectedPreference by remember { mutableStateOf<String?>(null) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF0B1221))
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text("Personalize Your Diet Plan", color = Color.White, fontSize = 22.sp, fontWeight = FontWeight.Bold, textAlign = TextAlign.Center)
        Spacer(modifier = Modifier.height(32.dp))

        Text("Aapka lakshya kya hai?", color = Color.White, fontWeight = FontWeight.SemiBold)
        Spacer(modifier = Modifier.height(16.dp))
        FlowRow(horizontalArrangement = Arrangement.spacedBy(8.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
            goals.forEach { goal ->
                val isSelected = selectedGoal == goal
                FilterChip(
                    selected = isSelected,
                    onClick = { selectedGoal = goal },
                    label = { Text(goal) },
                    leadingIcon = if(isSelected) { {Icon(Icons.Default.Check, null)} } else null
                )
            }
        }

        Spacer(modifier = Modifier.height(32.dp))

        Text("Aapki bhojan pasand?", color = Color.White, fontWeight = FontWeight.SemiBold)
        Spacer(modifier = Modifier.height(16.dp))
        FlowRow(horizontalArrangement = Arrangement.spacedBy(8.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
            preferences.forEach { preference ->
                val isSelected = selectedPreference == preference
                FilterChip(
                    selected = isSelected,
                    onClick = { selectedPreference = preference },
                    label = { Text(preference) },
                    leadingIcon = if(isSelected) { {Icon(Icons.Default.Check, null)} } else null
                )
            }
        }
        
        Spacer(modifier = Modifier.height(48.dp))
        
        Button(
            onClick = onSetupComplete,
            enabled = selectedGoal != null && selectedPreference != null
        ) {
            Text("Start My Plan")
        }
    }
}