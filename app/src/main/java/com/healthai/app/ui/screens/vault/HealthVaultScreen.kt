package com.healthai.app.ui.screens.vault

import androidx.biometric.BiometricPrompt
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
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import androidx.fragment.app.FragmentActivity
import androidx.navigation.NavController
import android.content.Intent
import android.net.Uri
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.AsyncImage
import androidx.compose.ui.layout.ContentScale
import com.healthai.app.data.remote.api.VaultFileResponse
import com.healthai.app.ui.navigation.NavRoutes

private val OffWhiteBg = Color(0xFFF8FAFC)
private val CardBorderGray = Color(0xFFE2E8F0)
private val MedicalTeal = Color(0xFF0D9488)
private val FriendlyRoyalBlue = Color(0xFF2563EB)
private val DarkCharcoalText = Color(0xFF1E293B)
private val SlateGrayText = Color(0xFF475569)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HealthVaultScreen(
    navController: NavController,
    viewModel: HealthVaultViewModel = hiltViewModel()
) {
    var isUnlocked by remember { mutableStateOf(false) }
    val uiState by viewModel.uiState.collectAsState()
    
    val context = LocalContext.current
    val activity = remember(context) {
        var c = context
        while (c is android.content.ContextWrapper) {
            if (c is FragmentActivity) break
            c = c.baseContext
        }
        c as? FragmentActivity
    }

    if (!isUnlocked) {
        VaultLockScreen {
            activity?.let {
                showBiometricPrompt(it) { isUnlocked = true }
            }
        }
    } else {
        Scaffold(
            topBar = {
                TopAppBar(
                    title = { Text("Helpix Secure Vault", color = DarkCharcoalText, fontWeight = FontWeight.Bold) },
                    navigationIcon = {
                        IconButton(onClick = { navController.popBackStack() }) {
                            Icon(Icons.Default.ArrowBack, contentDescription = "Back", tint = DarkCharcoalText)
                        }
                    },
                    actions = {
                        IconButton(onClick = { isUnlocked = false }) {
                            Icon(Icons.Default.LockOpen, contentDescription = "Lock Vault", tint = MedicalTeal)
                        }
                    },
                    colors = TopAppBarDefaults.topAppBarColors(containerColor = OffWhiteBg)
                )
            },
            floatingActionButton = {
                FloatingActionButton(
                    onClick = { navController.navigate(NavRoutes.AddRecord) },
                    containerColor = MedicalTeal,
                    contentColor = Color.White
                ) {
                    Icon(Icons.Default.CloudUpload, contentDescription = "Upload", tint = Color.White)
                }
            },
            containerColor = OffWhiteBg
        ) { paddingValues ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .padding(16.dp)
            ) {
                VaultHeader()
                Spacer(modifier = Modifier.height(20.dp))
                
                CategoryRow()
                Spacer(modifier = Modifier.height(24.dp))

                Text("Recent Uploads", color = DarkCharcoalText, fontSize = 18.sp, fontWeight = FontWeight.Bold)
                Spacer(modifier = Modifier.height(16.dp))

                when (val state = uiState) {
                    is VaultUiState.Loading -> CircularProgressIndicator(color = MedicalTeal)
                    is VaultUiState.Error -> Text("Error: ${state.message}", color = Color.Red)
                    is VaultUiState.Success -> {
                        if (state.files.isEmpty()) {
                            Text("No files in vault.", color = SlateGrayText)
                        } else {
                            LazyColumn(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                                items(
                                    items = state.files,
                                    key = { record -> record.file_id }
                                ) { record ->
                                    RecordCard(record, context)
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun VaultLockScreen(onUnlockRequest: () -> Unit) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(OffWhiteBg),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Box(
                modifier = Modifier
                    .size(120.dp)
                    .border(2.dp, MedicalTeal, CircleShape)
                    .background(Color(0xFFCCFBF1), CircleShape)
                    .padding(20.dp),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    Icons.Default.Fingerprint,
                    contentDescription = null,
                    modifier = Modifier.size(60.dp),
                    tint = MedicalTeal
                )
            }
            Spacer(modifier = Modifier.height(32.dp))
            Text("Vault Protected", color = DarkCharcoalText, fontSize = 26.sp, fontWeight = FontWeight.Bold)
            Text(
                "Access your medical reports securely",
                color = SlateGrayText,
                fontSize = 14.sp,
                modifier = Modifier.padding(top = 8.dp)
            )
            Spacer(modifier = Modifier.height(48.dp))
            Button(
                onClick = onUnlockRequest,
                colors = ButtonDefaults.buttonColors(containerColor = FriendlyRoyalBlue),
                shape = RoundedCornerShape(24.dp),
                modifier = Modifier.height(50.dp).width(200.dp)
            ) {
                Icon(Icons.Default.Lock, contentDescription = null, modifier = Modifier.size(18.dp), tint = Color.White)
                Spacer(modifier = Modifier.width(8.dp))
                Text("Unlock Vault", color = Color.White, fontWeight = FontWeight.Bold)
            }
        }
    }
}

@Composable
fun RecordCard(record: VaultFileResponse, context: android.content.Context) {
    val isImage = record.content_type.startsWith("image/")
    Card(
        modifier = Modifier.fillMaxWidth().clickable {
            if (!record.file_url.isNullOrEmpty()) {
                val intent = Intent(Intent.ACTION_VIEW, Uri.parse(record.file_url))
                context.startActivity(intent)
            }
        },
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
                    .size(48.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(Color(0xFFF3E8FF)),
                contentAlignment = Alignment.Center
            ) {
                if (isImage && !record.file_url.isNullOrEmpty()) {
                    AsyncImage(
                        model = record.file_url,
                        contentDescription = null,
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Crop
                    )
                } else {
                    Icon(
                        if (record.content_type.contains("pdf")) Icons.Default.Description else Icons.Default.InsertDriveFile,
                        contentDescription = null,
                        tint = Color(0xFF9333EA)
                    )
                }
            }
            Spacer(modifier = Modifier.width(16.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(record.filename, color = DarkCharcoalText, fontWeight = FontWeight.Bold, fontSize = 16.sp, maxLines = 1)
                Text("${record.uploaded_at.take(10)} • ${record.size_bytes / 1024} KB", color = SlateGrayText, fontSize = 12.sp)
            }
            Icon(Icons.Default.OpenInBrowser, contentDescription = "Open", tint = MedicalTeal)
        }
    }
}

@Composable
fun CategoryRow() {
    val categories = listOf("All", "Reports", "Prescriptions", "Scans")
    var selected by remember { mutableStateOf("All") }
    
    Row(
        modifier = Modifier.fillMaxWidth().height(48.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        categories.forEach { cat ->
            val isSelected = selected == cat
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(20.dp))
                    .background(if (isSelected) MedicalTeal else Color.White)
                    .border(BorderStroke(1.dp, if (isSelected) MedicalTeal else CardBorderGray), RoundedCornerShape(20.dp))
                    .clickable { selected = cat }
                    .padding(horizontal = 16.dp, vertical = 8.dp)
            ) {
                Text(cat, color = if (isSelected) Color.White else SlateGrayText, fontSize = 12.sp, fontWeight = FontWeight.Medium)
            }
        }
    }
}

@Composable
fun VaultHeader() {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(20.dp))
            .background(Color(0xFFCCFBF1))
            .border(BorderStroke(1.dp, MedicalTeal.copy(alpha = 0.3f)), RoundedCornerShape(20.dp))
            .padding(20.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text("Military Grade Encryption", color = Color(0xFF115E59), fontWeight = FontWeight.Bold, fontSize = 14.sp)
            Text("Your data is encrypted locally and never shared.", color = Color(0xFF115E59).copy(alpha = 0.85f), fontSize = 12.sp)
        }
        Icon(Icons.Default.VerifiedUser, contentDescription = null, tint = MedicalTeal, modifier = Modifier.size(32.dp))
    }
}

fun showBiometricPrompt(
    activity: FragmentActivity,
    onSuccess: () -> Unit
) {
    val executor = ContextCompat.getMainExecutor(activity)
    val biometricPrompt = BiometricPrompt(activity, executor,
        object : BiometricPrompt.AuthenticationCallback() {
            override fun onAuthenticationSucceeded(result: BiometricPrompt.AuthenticationResult) {
                onSuccess()
            }
        })

    val promptInfo = BiometricPrompt.PromptInfo.Builder()
        .setTitle("Unlock Health Vault")
        .setSubtitle("Use your fingerprint to access sensitive reports")
        .setNegativeButtonText("Cancel")
        .build()

    biometricPrompt.authenticate(promptInfo)
}
