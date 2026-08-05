package com.healthai.app.ui.screens.patient

import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.healthai.app.R
import com.healthai.app.ui.navigation.NavRoutes
import kotlinx.coroutines.delay

import kotlin.time.Duration.Companion.seconds

@Composable
fun PaymentProcessScreen(navController: NavController) {
    var isProcessing by remember { mutableStateOf(true) }

    LaunchedEffect(Unit) {
        delay(3.seconds) // Simulate network/payment delay
        isProcessing = false
        delay(2.seconds) // Show success for 2 seconds
        navController.navigate(NavRoutes.MyAppointments) {
            popUpTo(NavRoutes.Dashboard) { inclusive = false }
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF0F172A)),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            if (isProcessing) {
                CircularProgressIndicator(
                    modifier = Modifier.size(64.dp),
                    color = colorResource(id = R.color.logo_cyan),
                    strokeWidth = 6.dp
                )
                Spacer(modifier = Modifier.height(24.dp))
                Text("Securely Processing Payment...", color = Color.White, fontSize = 18.sp, fontWeight = FontWeight.Medium)
                Spacer(modifier = Modifier.height(8.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.Shield, contentDescription = null, tint = Color.Gray, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Helpix Secure Gateway", color = Color.Gray, fontSize = 12.sp)
                }
            } else {
                Box(
                    modifier = Modifier
                        .size(80.dp)
                        .clip(CircleShape)
                        .background(Color(0xFF00E676)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(Icons.Default.Check, contentDescription = "Success", tint = Color.Black, modifier = Modifier.size(48.dp))
                }
                Spacer(modifier = Modifier.height(24.dp))
                Text("Payment Successful!", color = Color.White, fontSize = 22.sp, fontWeight = FontWeight.Bold)
                Text("Your appointment is confirmed.", color = Color.Gray, fontSize = 14.sp)
            }
        }
    }
}
