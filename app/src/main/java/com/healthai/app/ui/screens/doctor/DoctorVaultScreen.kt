package com.healthai.app.ui.screens.doctor

import android.widget.Toast
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.healthai.app.data.remote.api.WalletTransactionResponse
import com.healthai.app.ui.screens.dashboard.CardBorderGray
import java.util.Locale

private val OffWhiteBg = Color(0xFFF8FAFC)
private val DarkCharcoalText = Color(0xFF1E293B)
private val SlateGrayText = Color(0xFF475569)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DoctorVaultScreen(
    navController: NavController,
    viewModel: DoctorDashboardViewModel = hiltViewModel()
) {
    val balance by viewModel.vaultBalance.collectAsState()
    val pending by viewModel.vaultPending.collectAsState()
    val transactions by viewModel.vaultTransactions.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val context = LocalContext.current

    var showWithdrawDialog by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        viewModel.loadDashboard()
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Doctor Financial Vault", color = DarkCharcoalText, fontWeight = FontWeight.ExtraBold) },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", tint = DarkCharcoalText)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = OffWhiteBg)
            )
        },
        bottomBar = {
            com.healthai.app.ui.screens.dashboard.HelpixBottomNav(navController = navController, userType = "DOCTOR")
        },
        containerColor = OffWhiteBg
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(20.dp)
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(180.dp)
                    .clip(RoundedCornerShape(24.dp))
                    .background(Brush.linearGradient(listOf(MedicalEmerald, Color(0xFF0F766E))))
                    .padding(24.dp)
            ) {
                Column {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.AccountBalanceWallet, contentDescription = null, tint = Color.White.copy(alpha = 0.9f), modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Available Earnings", color = Color.White.copy(alpha = 0.9f), fontSize = 14.sp)
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                    Text("₹${String.format(Locale.getDefault(), "%,.2f", balance)}", color = Color.White, fontSize = 36.sp, fontWeight = FontWeight.ExtraBold)
                    
                    if (pending > 0) {
                        Text("Pending: ₹${String.format(Locale.getDefault(), "%,.2f", pending)}", color = Color.White.copy(alpha = 0.8f), fontSize = 12.sp)
                    }

                    Spacer(modifier = Modifier.weight(1f))
                    
                    Button(
                        onClick = { if (balance > 0) showWithdrawDialog = true else Toast.makeText(context, "No balance to withdraw", Toast.LENGTH_SHORT).show() },
                        colors = ButtonDefaults.buttonColors(containerColor = Color.White),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text("Withdraw Funds", color = MedicalEmerald, fontWeight = FontWeight.Bold)
                        Spacer(modifier = Modifier.width(6.dp))
                        Icon(Icons.AutoMirrored.Filled.ArrowForward, contentDescription = null, tint = MedicalEmerald, modifier = Modifier.size(16.dp))
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            Text("Payment History", color = DarkCharcoalText, fontSize = 18.sp, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.height(12.dp))

            if (isLoading) {
                Box(modifier = Modifier.fillMaxWidth().height(150.dp), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator(color = MedicalEmerald)
                }
            } else {
                LazyColumn(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    if (transactions.isEmpty()) {
                        item {
                            Box(modifier = Modifier.fillMaxWidth().padding(top = 40.dp), contentAlignment = Alignment.Center) {
                                Text("No payment history found", color = SlateGrayText, fontSize = 14.sp)
                            }
                        }
                    } else {
                        items(
                            items = transactions,
                            key = { tx -> tx.transaction_id }
                        ) { tx ->
                            VaultTransactionItem(tx)
                        }
                    }
                }
            }
            
            Spacer(modifier = Modifier.height(16.dp))
        }

        if (showWithdrawDialog) {
            WithdrawalDialog(
                balance = balance,
                onDismiss = { showWithdrawDialog = false },
                onConfirm = { amount ->
                    viewModel.requestWithdrawal(amount) { success, msg ->
                        showWithdrawDialog = false
                        if (success) {
                            Toast.makeText(context, "Withdrawal Request Submitted!", Toast.LENGTH_SHORT).show()
                        } else {
                            Toast.makeText(context, msg.ifBlank { "Withdrawal Failed" }, Toast.LENGTH_SHORT).show()
                        }
                    }
                }
            )
        }
    }
}

@Composable
fun VaultTransactionItem(tx: WalletTransactionResponse) {
    val isEarning = tx.type.equals("CREDIT", ignoreCase = true) || tx.type.equals("EARNING", ignoreCase = true)
    val color = if (isEarning) Color(0xFF16A34A) else Color(0xFFDC2626)

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        border = BorderStroke(1.dp, CardBorderGray)
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(42.dp)
                    .background(color.copy(alpha = 0.1f), CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = if (isEarning) Icons.Default.ArrowDownward else Icons.Default.ArrowUpward,
                    contentDescription = null,
                    tint = color,
                    modifier = Modifier.size(20.dp)
                )
            }
            Spacer(modifier = Modifier.width(14.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = if (isEarning) "Patient Consultation Fee" else "Bank Payout Withdrawal",
                    color = DarkCharcoalText,
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp
                )
                Text(
                    text = if (!tx.appointment_id.isNullOrBlank()) "Appt: ${tx.appointment_id.take(8)}" else "Ref: ${tx.transaction_id.take(8)}",
                    color = SlateGrayText,
                    fontSize = 11.sp
                )
            }
            Column(horizontalAlignment = Alignment.End) {
                Text(
                    text = (if (isEarning) "+" else "-") + "₹${String.format(Locale.getDefault(), "%.2f", tx.amount)}",
                    color = color,
                    fontWeight = FontWeight.ExtraBold,
                    fontSize = 15.sp
                )
                if (tx.status != "COMPLETED") {
                    Text(tx.status, color = ProfessionalIndigo, fontSize = 9.sp, fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}

@Composable
fun WithdrawalDialog(balance: Double, onDismiss: () -> Unit, onConfirm: (Double) -> Unit) {
    var amount by remember { mutableStateOf(balance.toString()) }

    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = Color.White,
        title = { Text("Request Withdrawal", color = DarkCharcoalText, fontWeight = FontWeight.Bold) },
        text = {
            Column {
                Text("Enter the amount you wish to withdraw to your registered bank account.", color = SlateGrayText, fontSize = 14.sp)
                Spacer(modifier = Modifier.height(20.dp))
                OutlinedTextField(
                    value = amount,
                    onValueChange = { amount = it },
                    label = { Text("Amount (₹)", color = SlateGrayText) },
                    prefix = { Text("₹", color = DarkCharcoalText) },
                    modifier = Modifier.fillMaxWidth(),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = DarkCharcoalText,
                        unfocusedTextColor = DarkCharcoalText,
                        focusedBorderColor = MedicalEmerald,
                        unfocusedBorderColor = CardBorderGray
                    )
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text("Available: ₹${String.format(Locale.getDefault(), "%.2f", balance)}", color = MedicalEmerald, fontSize = 12.sp, fontWeight = FontWeight.Bold)
            }
        },
        confirmButton = {
            Button(
                onClick = { amount.toDoubleOrNull()?.let { if (it > 0 && it <= balance) onConfirm(it) } },
                colors = ButtonDefaults.buttonColors(containerColor = MedicalEmerald)
            ) {
                Text("Confirm Withdrawal", color = Color.White, fontWeight = FontWeight.Bold)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Cancel", color = SlateGrayText) }
        }
    )
}
