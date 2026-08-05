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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.healthai.app.data.remote.api.WalletTransactionResponse
import java.util.Locale

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
        viewModel.loadDashboard() // This will trigger loadVaultData
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Doctor Financial Vault", color = Color.White, fontWeight = FontWeight.ExtraBold) },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", tint = Color.White)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = DocDeepSlate)
            )
        },
        bottomBar = {
            com.healthai.app.ui.screens.dashboard.HelpixBottomNav(navController = navController, userType = "DOCTOR")
        },
        containerColor = DocDeepSlate
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(20.dp)
        ) {
            // Balance Card
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(180.dp)
                    .clip(RoundedCornerShape(24.dp))
                    .background(Brush.linearGradient(listOf(MedicalEmerald, Color(0xFF065F46))))
                    .padding(24.dp)
            ) {
                Column {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.AccountBalanceWallet, contentDescription = null, tint = Color.White.copy(alpha = 0.8f), modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Available Earnings", color = Color.White.copy(alpha = 0.8f), fontSize = 14.sp)
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                    Text("₹${String.format(Locale.getDefault(), "%,.2f", balance)}", color = Color.White, fontSize = 36.sp, fontWeight = FontWeight.ExtraBold)
                    
                    if (pending > 0) {
                        Text("Pending: ₹${String.format(Locale.getDefault(), "%,.2f", pending)}", color = Color.White.copy(alpha = 0.6f), fontSize = 12.sp)
                    }

                    Spacer(modifier = Modifier.weight(1f))
                    
                    Button(
                        onClick = { if (balance > 0) showWithdrawDialog = true else Toast.makeText(context, "No balance to withdraw", Toast.LENGTH_SHORT).show() },
                        colors = ButtonDefaults.buttonColors(containerColor = Color.White),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.fillMaxWidth().height(44.dp),
                        contentPadding = PaddingValues(0.dp)
                    ) {
                        Text("Withdraw Funds to Bank", color = Color(0xFF065F46), fontWeight = FontWeight.Bold)
                        Spacer(modifier = Modifier.width(8.dp))
                        Icon(Icons.AutoMirrored.Filled.ArrowForward, contentDescription = null, tint = Color(0xFF065F46), modifier = Modifier.size(16.dp))
                    }
                }
            }

            Spacer(modifier = Modifier.height(32.dp))

            // Vault Quick Stats
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                VaultStatCard("Total Earned", "₹${String.format(Locale.getDefault(), "%,.0f", balance + pending + 5000)}", Icons.Default.TrendingUp, MedicalEmerald, Modifier.weight(1f))
                VaultStatCard("Withdrawn", "₹5,000", Icons.Default.Payments, ProfessionalIndigo, Modifier.weight(1f))
            }

            Spacer(modifier = Modifier.height(32.dp))

            Text("Payment History", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 18.sp)
            Spacer(modifier = Modifier.height(16.dp))

            if (isLoading && transactions.isEmpty()) {
                Box(modifier = Modifier.fillMaxWidth().weight(1f), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator(color = MedicalEmerald)
                }
            } else {
                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                    modifier = Modifier.weight(1f)
                ) {
                    if (transactions.isEmpty()) {
                        item {
                            Box(modifier = Modifier.fillMaxWidth().padding(top = 40.dp), contentAlignment = Alignment.Center) {
                                Text("No payment history found", color = DocTextGrey, fontSize = 14.sp)
                            }
                        }
                    } else {
                        items(transactions) { tx ->
                            VaultTransactionItem(tx)
                        }
                    }
                }
            }
            
            Spacer(modifier = Modifier.height(16.dp))
            
            Text(
                "Financial Security Note: All payments are secured by Helpix clinical escrow. Funds are usually clear for withdrawal 24-48 hours after patient consultation.",
                color = DocTextGrey,
                fontSize = 10.sp,
                lineHeight = 14.sp,
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth()
            )
        }
    }

    if (showWithdrawDialog) {
        WithdrawalDialog(
            balance = balance,
            onDismiss = { showWithdrawDialog = false },
            onConfirm = { amount ->
                viewModel.requestWithdrawal(amount) { success, msg ->
                    Toast.makeText(context, msg, Toast.LENGTH_SHORT).show()
                    if (success) showWithdrawDialog = false
                }
            }
        )
    }
}

@Composable
fun VaultStatCard(title: String, value: String, icon: androidx.compose.ui.graphics.vector.ImageVector, color: Color, modifier: Modifier) {
    Card(
        modifier = modifier.height(80.dp),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = DocCardBg),
        border = BorderStroke(1.dp, Color.White.copy(alpha = 0.05f))
    ) {
        Column(modifier = Modifier.fillMaxSize().padding(12.dp)) {
            Icon(icon, contentDescription = null, tint = color, modifier = Modifier.size(16.dp))
            Spacer(modifier = Modifier.weight(1f))
            Text(value, color = Color.White, fontSize = 18.sp, fontWeight = FontWeight.ExtraBold)
            Text(title, color = DocTextGrey, fontSize = 10.sp)
        }
    }
}

@Composable
fun VaultTransactionItem(tx: WalletTransactionResponse) {
    val isEarning = tx.type.equals("CREDIT", ignoreCase = true) || tx.type.equals("EARNING", ignoreCase = true)
    
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = DocCardBg),
        border = BorderStroke(1.dp, Color.White.copy(alpha = 0.05f))
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(44.dp)
                    .background(
                        if (isEarning) MedicalEmerald.copy(alpha = 0.1f) else Color.Red.copy(alpha = 0.1f),
                        CircleShape
                    ),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    if (isEarning) Icons.Default.Add else Icons.Default.Remove,
                    contentDescription = null,
                    tint = if (isEarning) MedicalEmerald else Color.Red,
                    modifier = Modifier.size(20.dp)
                )
            }
            Spacer(modifier = Modifier.width(16.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = if (isEarning) "Consultation Fee Received" else "Withdrawal to Bank", 
                    color = Color.White, 
                    fontWeight = FontWeight.Bold, 
                    fontSize = 14.sp
                )
                Text(tx.created_at.split("T")[0], color = DocTextGrey, fontSize = 11.sp)
            }
            Column(horizontalAlignment = Alignment.End) {
                Text(
                    (if (isEarning) "+" else "-") + "₹${String.format(Locale.getDefault(), "%.2f", tx.amount)}",
                    color = if (isEarning) MedicalEmerald else Color.White,
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
        containerColor = DocCardBg,
        title = { Text("Request Withdrawal", color = Color.White, fontWeight = FontWeight.Bold) },
        text = {
            Column {
                Text("Enter the amount you wish to withdraw to your registered bank account.", color = DocTextGrey, fontSize = 14.sp)
                Spacer(modifier = Modifier.height(20.dp))
                OutlinedTextField(
                    value = amount,
                    onValueChange = { amount = it },
                    label = { Text("Amount (₹)") },
                    prefix = { Text("₹") },
                    modifier = Modifier.fillMaxWidth(),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = Color.White,
                        unfocusedTextColor = Color.White,
                        focusedBorderColor = MedicalEmerald
                    )
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text("Available: ₹${String.format(Locale.getDefault(), "%.2f", balance)}", color = MedicalEmerald, fontSize = 12.sp)
            }
        },
        confirmButton = {
            Button(
                onClick = { amount.toDoubleOrNull()?.let { if (it > 0 && it <= balance) onConfirm(it) } },
                colors = ButtonDefaults.buttonColors(containerColor = MedicalEmerald)
            ) {
                Text("Confirm Withdrawal", color = DocDeepSlate, fontWeight = FontWeight.Bold)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Cancel", color = DocTextGrey) }
        }
    )
}
