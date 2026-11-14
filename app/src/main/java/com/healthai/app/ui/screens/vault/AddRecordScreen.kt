package com.healthai.app.ui.screens.vault

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.hilt.navigation.compose.hiltViewModel
import android.widget.Toast

private val OffWhiteBg = Color(0xFFF8FAFC)
private val CardBorderGray = Color(0xFFE2E8F0)
private val MedicalTeal = Color(0xFF0D9488)
private val DarkCharcoalText = Color(0xFF1E293B)
private val SlateGrayText = Color(0xFF475569)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddRecordScreen(
    navController: NavController,
    viewModel: HealthVaultViewModel = hiltViewModel()
) {
    var title by remember { mutableStateOf("") }
    var category by remember { mutableStateOf("Reports") }
    var selectedUri by remember { mutableStateOf<Uri?>(null) }
    var isUploading by remember { mutableStateOf(false) }

    val context = LocalContext.current

    val galleryLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        selectedUri = uri
    }

    val cameraLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.TakePicturePreview()
    ) { bitmap -> }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Secure Upload", color = DarkCharcoalText, fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back", tint = DarkCharcoalText)
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
                .padding(20.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            OutlinedTextField(
                value = title,
                onValueChange = { title = it },
                label = { Text("Record Title (e.g. Blood Report)", color = SlateGrayText) },
                modifier = Modifier.fillMaxWidth(),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedTextColor = DarkCharcoalText,
                    unfocusedTextColor = DarkCharcoalText,
                    focusedBorderColor = MedicalTeal,
                    unfocusedBorderColor = CardBorderGray
                )
            )

            Spacer(modifier = Modifier.height(20.dp))

            Text("Select Category", color = SlateGrayText, modifier = Modifier.align(Alignment.Start), fontWeight = FontWeight.Medium)
            Row(
                modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                listOf("Reports", "Prescriptions", "Scans").forEach { cat ->
                    val isSelected = category == cat
                    Button(
                        onClick = { category = cat },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = if (isSelected) MedicalTeal else Color.White,
                            contentColor = if (isSelected) Color.White else DarkCharcoalText
                        ),
                        border = BorderStroke(1.dp, if (isSelected) MedicalTeal else CardBorderGray),
                        modifier = Modifier.weight(1f)
                    ) {
                        Text(cat, fontSize = 11.sp, fontWeight = FontWeight.SemiBold)
                    }
                }
            }

            Spacer(modifier = Modifier.height(30.dp))

            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                SelectionCard(
                    "Camera",
                    Icons.Default.CameraAlt,
                    Modifier.weight(1f)
                ) { cameraLauncher.launch(null) }
                
                SelectionCard(
                    "Gallery",
                    Icons.Default.Image,
                    Modifier.weight(1f)
                ) { galleryLauncher.launch("*/*") }
            }

            Spacer(modifier = Modifier.height(20.dp))

            if (selectedUri != null) {
                Text("File Selected: ${selectedUri?.path?.takeLast(20)}...", color = Color(0xFF16A34A), fontWeight = FontWeight.Bold)
            }

            Spacer(modifier = Modifier.weight(1f))

            Button(
                onClick = {
                    if (title.isNotBlank() && selectedUri != null) {
                        isUploading = true
                        viewModel.uploadFile(context, selectedUri!!, title, category) { success ->
                            isUploading = false
                            if (success) {
                                Toast.makeText(context, "Upload successful!", Toast.LENGTH_SHORT).show()
                                navController.popBackStack()
                            } else {
                                Toast.makeText(context, "Upload failed.", Toast.LENGTH_SHORT).show()
                            }
                        }
                    }
                },
                modifier = Modifier.fillMaxWidth().height(56.dp),
                colors = ButtonDefaults.buttonColors(containerColor = MedicalTeal),
                shape = RoundedCornerShape(14.dp),
                enabled = !isUploading && title.isNotBlank() && selectedUri != null
            ) {
                if (isUploading) {
                    CircularProgressIndicator(color = Color.White, modifier = Modifier.size(24.dp))
                } else {
                    Text("Securely Upload to Vault", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                }
            }
        }
    }
}

@Composable
fun SelectionCard(title: String, icon: androidx.compose.ui.graphics.vector.ImageVector, modifier: Modifier, onClick: () -> Unit) {
    Card(
        modifier = modifier.height(120.dp).clickable { onClick() },
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        border = BorderStroke(1.dp, CardBorderGray)
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(icon, contentDescription = null, tint = MedicalTeal, modifier = Modifier.size(32.dp))
            Spacer(modifier = Modifier.height(8.dp))
            Text(title, color = DarkCharcoalText, fontWeight = FontWeight.Bold)
        }
    }
}
