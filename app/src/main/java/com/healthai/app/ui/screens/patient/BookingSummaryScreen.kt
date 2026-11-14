package com.healthai.app.ui.screens.patient

import androidx.compose.foundation.BorderStroke
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.healthai.app.ui.navigation.NavRoutes
import com.healthai.app.ui.screens.dashboard.CardBorderGray
import java.util.Locale

private val OffWhiteBg = Color(0xFFF8FAFC)
private val MedicalTeal = Color(0xFF0D9488)
private val FriendlyRoyalBlue = Color(0xFF2563EB)
private val DarkCharcoalText = Color(0xFF1E293B)
private val SlateGrayText = Color(0xFF475569)

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
    val platformFee = fee * 0.10
    val totalPayable = fee + platformFee

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Booking Summary", color = DarkCharcoalText, fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", tint = DarkCharcoalText)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = OffWhiteBg)
            )
        },
        containerColor = OffWhiteBg
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(20.dp)
        ) {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(22.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                border = BorderStroke(1.dp, CardBorderGray)
            ) {
                Row(modifier = Modifier.padding(20.dp), verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(60.dp)
                            .clip(CircleShape)
                            .background(Color(0xFFCCFBF1)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(Icons.Default.Person, contentDescription = null, tint = MedicalTeal, modifier = Modifier.size(32.dp))
                    }
                    Spacer(modifier = Modifier.width(16.dp))
                    Column {
                        Text(docName, color = DarkCharcoalText, fontWeight = FontWeight.Bold, fontSize = 18.sp)
                        Text(specialization, color = SlateGrayText, fontSize = 14.sp)
                    }
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                InfoChip(Icons.Default.CalendarToday, date, Modifier.weight(1f))
                InfoChip(Icons.Default.AccessTime, time, Modifier.weight(1f))
            }

            Spacer(modifier = Modifier.height(28.dp))

            Text("Payment Details", color = DarkCharcoalText, fontWeight = FontWeight.Bold, fontSize = 16.sp)
            Spacer(modifier = Modifier.height(14.dp))

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color.White, RoundedCornerShape(18.dp))
                    .border(BorderStroke(1.dp, CardBorderGray), RoundedCornerShape(18.dp))
                    .padding(20.dp)
            ) {
                BillRow("Consultation Fee", "₹${String.format(Locale.getDefault(), "%.2f", fee)}")
                Spacer(modifier = Modifier.height(12.dp))
                BillRow("HelpiX Platform Fee (10%)", "₹${String.format(Locale.getDefault(), "%.2f", platformFee)}")
                Spacer(modifier = Modifier.height(16.dp))
                HorizontalDivider(color = CardBorderGray)
                Spacer(modifier = Modifier.height(16.dp))
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    Text("Total Payable", color = DarkCharcoalText, fontWeight = FontWeight.ExtraBold, fontSize = 18.sp)
                    Text("₹${String.format(Locale.getDefault(), "%.2f", totalPayable)}", color = MedicalTeal, fontWeight = FontWeight.ExtraBold, fontSize = 18.sp)
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
                colors = ButtonDefaults.buttonColors(containerColor = FriendlyRoyalBlue),
                shape = RoundedCornerShape(16.dp)
            ) {
                Text("Proceed to Pay ₹${String.format(Locale.getDefault(), "%.2f", totalPayable)}", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 16.sp)
            }
        }
    }
}

@Composable
fun InfoChip(icon: androidx.compose.ui.graphics.vector.ImageVector, text: String, modifier: Modifier) {
    Row(
        modifier = modifier
            .background(Color.White, RoundedCornerShape(14.dp))
            .border(BorderStroke(1.dp, CardBorderGray), RoundedCornerShape(14.dp))
            .padding(14.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(icon, contentDescription = null, tint = MedicalTeal, modifier = Modifier.size(16.dp))
        Spacer(modifier = Modifier.width(8.dp))
        Text(text, color = DarkCharcoalText, fontSize = 13.sp, fontWeight = FontWeight.SemiBold)
    }
}

@Composable
fun BillRow(label: String, amount: String) {
    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
        Text(label, color = SlateGrayText, fontSize = 14.sp)
        Text(amount, color = DarkCharcoalText, fontWeight = FontWeight.SemiBold, fontSize = 14.sp)
    }
}
