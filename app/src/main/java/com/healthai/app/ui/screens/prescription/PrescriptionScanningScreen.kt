package com.healthai.app.ui.screens.prescription

import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Camera
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.healthai.app.ui.navigation.NavRoutes

// Note: This screen would reuse the CameraPreview from other modules like SkinScanningScreen
// For brevity, we are not duplicating the CameraPreview composable here.
// Assume a similar composable exists that takes a NavController.

@Composable
fun PrescriptionScanningScreen(navController: NavController) {
    Scaffold(containerColor = Color(0xFF0B1221)) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(32.dp))
            Text(
                text = "Scan Prescription",
                color = Color.White,
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = "Parchhi ko aayataakaar ghere ke andar laayein.",
                color = Color.Gray,
                fontSize = 14.sp,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(horizontal = 16.dp)
            )
            
            // Camera Preview Placeholder
            Box(modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
                .padding(vertical = 24.dp)) {
                // SkinCameraPreview() // Re-use the camera preview logic here
                 Box(modifier = Modifier
                    .align(Alignment.Center)
                    .fillMaxWidth(0.9f)
                    .fillMaxHeight(0.7f)
                    .border(3.dp, Color(0xFF2962FF))
                )
            }

            IconButton(
                onClick = { 
                    navController.navigate(NavRoutes.PrescriptionAnalysis)
                },
                modifier = Modifier.size(72.dp)
            ) {
                Icon(Icons.Default.Camera, contentDescription = "Capture", tint = Color.White, modifier = Modifier.fillMaxSize())
            }
            Spacer(modifier = Modifier.height(48.dp))
        }
    }
}