package com.healthai.app.ui.screens.patient

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.healthai.app.R
import com.healthai.app.ui.navigation.NavRoutes

import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BookingSummaryScreen(
    navController: NavController,
    docName: String,
    specialization: String,
    fee: Double,
    date: String,
    time: String
) {
    val platformFee = fee * 0.10 // 10% Platform Fee
    val totalPayable = fee + platformFee

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Booking Summary", color = Color.White, fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", tint = Color.White)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color(0xFF0F172A))
            )
        },
        containerColor = Color(0xFF0F172A)
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp)
        ) {
            // Doctor Card
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFF1E293B))
            ) {
                Row(modifier = Modifier.padding(20.dp), verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(60.dp)
                            .clip(CircleShape)
                            .background(colorResource(id = R.color.logo_cyan).copy(alpha = 0.1f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(Icons.Default.Person, contentDescription = null, tint = colorResource(id = R.color.logo_cyan))
                    }
                    Spacer(modifier = Modifier.width(16.dp))
                    Column {
                        Text(docName, color = Color.White, fontWeight = FontWeight.Bold, fontSize = 18.sp)
                        Text(specialization, color = Color.Gray, fontSize = 14.sp)
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Date & Time
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                InfoChip(Icons.Default.CalendarToday, date, Modifier.weight(1f))
                InfoChip(Icons.Default.AccessTime, time, Modifier.weight(1f))
            }

            Spacer(modifier = Modifier.height(32.dp))

            Text("Payment Details", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 16.sp)
            Spacer(modifier = Modifier.height(16.dp))

            // Bill Breakdown
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color(0xFF1E293B).copy(alpha = 0.5f), RoundedCornerShape(16.dp))
                    .padding(20.dp)
            ) {
                BillRow("Consultation Fee", "₹${String.format(Locale.getDefault(), "%.2f", fee)}")
                Spacer(modifier = Modifier.height(12.dp))
                BillRow("HelpiX Platform Fee (10%)", "₹${String.format(Locale.getDefault(), "%.2f", platformFee)}")
                Spacer(modifier = Modifier.height(16.dp))
                HorizontalDivider(color = Color.White.copy(alpha = 0.05f))
                Spacer(modifier = Modifier.height(16.dp))
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    Text("Total Payable", color = Color.White, fontWeight = FontWeight.ExtraBold, fontSize = 18.sp)
                    Text("₹${String.format(Locale.getDefault(), "%.2f", totalPayable)}", color = colorResource(id = R.color.logo_cyan), fontWeight = FontWeight.ExtraBold, fontSize = 18.sp)
                }
            }

            Spacer(modifier = Modifier.weight(1f))

            Button(
                onClick = { 
                    navController.navigate(NavRoutes.PaymentProcess)
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                colors = ButtonDefaults.buttonColors(containerColor = colorResource(id = R.color.logo_cyan)),
                shape = RoundedCornerShape(16.dp)
            ) {
                Text("Proceed to Pay ₹${String.format(Locale.getDefault(), "%.2f", totalPayable)}", color = Color.Black, fontWeight = FontWeight.Bold, fontSize = 16.sp)
            }
        }
    }
}

@Composable
fun InfoChip(icon: androidx.compose.ui.graphics.vector.ImageVector, text: String, modifier: Modifier) {
    Row(
        modifier = modifier
            .background(Color(0xFF1E293B), RoundedCornerShape(12.dp))
            .padding(12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(icon, contentDescription = null, tint = Color.Gray, modifier = Modifier.size(16.dp))
        Spacer(modifier = Modifier.width(8.dp))
        Text(text, color = Color.White, fontSize = 13.sp, fontWeight = FontWeight.Medium)
    }
}

@Composable
fun BillRow(label: String, amount: String) {
    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
        Text(label, color = Color.Gray, fontSize = 14.sp)
        Text(amount, color = Color.White, fontWeight = FontWeight.SemiBold, fontSize = 14.sp)
    }
}
