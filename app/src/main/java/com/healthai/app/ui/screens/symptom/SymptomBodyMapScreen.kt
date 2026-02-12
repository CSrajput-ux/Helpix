package com.healthai.app.ui.screens.symptom

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.healthai.app.R // Make sure to add a body map placeholder image in res/drawable
import com.healthai.app.ui.navigation.NavRoutes

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SymptomBodyMapScreen(navController: NavController) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Select Body Part") },
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
        BoxWithConstraints(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            // This is a placeholder for a proper body map.
            // A real implementation would use an interactive canvas or clickable image sections.
            Image(
                painter = painterResource(id = R.drawable.ic_launcher_background), // TODO: Replace with a real body map image
                contentDescription = "Body Map",
                modifier = Modifier.fillMaxSize(),
                alignment = Alignment.Center
            )
            
            // Example of a clickable hotspot over the head
            Hotspot(x = 0.5f, y = 0.15f, onClick = { navController.navigate(NavRoutes.SymptomAnalysis) })
            Hotspot(x = 0.5f, y = 0.35f, onClick = { navController.navigate(NavRoutes.SymptomAnalysis) })
        }
    }
}

@Composable
fun BoxWithConstraintsScope.Hotspot(x: Float, y: Float, onClick: () -> Unit) {
    Box(
        modifier = Modifier
            .align(Alignment.TopStart)
            .offset(x = maxWidth * x, y = maxHeight * y)
            .size(40.dp)
            .clip(CircleShape)
            .background(Color(0xFF2979FF).copy(alpha = 0.5f))
            .clickable(onClick = onClick)
    )
}