package com.healthai.app.ui.screens.profile

import android.app.Activity
import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatDelegate
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.os.LocaleListCompat
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.healthai.app.R
import com.healthai.app.services.LanguageManager
import com.healthai.app.ui.navigation.NavRoutes

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen(navController: NavController, viewModel: ProfileViewModel = viewModel()) {
    val user by viewModel.user.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()

    val scrollState = rememberScrollState()
    val context = LocalContext.current
    
    // Check if LanguageManager exists, if not, we'll provide a fallback or dummy
    val languageManager = remember { 
        try {
            LanguageManager(context)
        } catch (e: Exception) {
            null
        }
    }

    Scaffold(
        containerColor = Color(0xFF0B1221)
    ) { paddingValues ->
        Box(modifier = Modifier.fillMaxSize().padding(paddingValues)) {
            if (isLoading) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator(color = Color(0xFF00FFFF))
                }
            } else {
                user?.let { currentUser ->
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(Color(0xFF0B1221))
                            .verticalScroll(scrollState)
                    ) {
                        ProfileTopHeader(navController) { 
                            viewModel.logout()
                            navController.navigate(NavRoutes.Login) { 
                                popUpTo(0)
                                launchSingleTop = true 
                            }
                        }
                        ProfileHeaderCard(currentUser)
                        PersonalInformationSection(
                            user = currentUser
                        )
                        
                        languageManager?.let {
                            LanguageSwitcher(it, context)
                        }
                        
                        SettingsGrid(navController = navController)
                        DangerZone()
                        Spacer(modifier = Modifier.height(24.dp))
                    }
                } ?: run {
                    // This block handles the case where user is null (not logged in or data missing)
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.padding(24.dp)) {
                            Icon(Icons.Default.AccountCircle, contentDescription = null, tint = Color.Gray, modifier = Modifier.size(80.dp))
                            Spacer(modifier = Modifier.height(16.dp))
                            Text("Profile data is currently unavailable.", color = Color.White, textAlign = TextAlign.Center)
                            Text("Please ensure you are logged in.", color = Color.Gray, fontSize = 14.sp, textAlign = TextAlign.Center)
                            Spacer(modifier = Modifier.height(24.dp))
                            Button(
                                onClick = { 
                                    viewModel.logout()
                                    navController.navigate(NavRoutes.Login) { popUpTo(0) } 
                                },
                                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF00FFFF)),
                                shape = RoundedCornerShape(12.dp)
                            ) {
                                Text("Return to Login", color = Color.Black, fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun ProfileTopHeader(navController: NavController, onLogout: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 20.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        IconButton(onClick = { navController.popBackStack() }) {
            Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", tint = Color.White)
        }
        Text(
            text = "My Profile",
            color = Color.White,
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.weight(1f),
            textAlign = TextAlign.Center
        )
        TextButton(onClick = onLogout) {
            Text("Logout", color = Color(0xFFFF6B6B))
        }
    }
}

@Composable
fun ProfileHeaderCard(user: com.healthai.app.domain.model.User) {
    val cyanGlow = Color(0xFF00FFFF)
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFF1E293B)),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(
            modifier = Modifier.padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Image(
                painter = painterResource(id = R.drawable.ic_launcher_background), 
                contentDescription = "User Avatar",
                modifier = Modifier
                    .size(100.dp)
                    .clip(CircleShape)
                    .border(
                        width = 3.dp,
                        brush = Brush.radialGradient(
                            colors = listOf(cyanGlow, Color.Transparent),
                            radius = 150f
                        ),
                        shape = CircleShape
                    )
                    .shadow(elevation = 10.dp, shape = CircleShape, spotColor = cyanGlow)
            )
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = user.name.ifEmpty { "HealthAI User" },
                color = Color.White,
                fontSize = 22.sp,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = user.email.ifEmpty { "No email associated" },
                color = Color.Gray,
                fontSize = 14.sp
            )
            Spacer(modifier = Modifier.height(12.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                Badge(text = "Verified Account", color = Color(0xFF10B981)) 
                Badge(text = if(user.userType == "DOCTOR") "Doctor" else "Patient", color = Color(0xFF06B6D4))
            }
            Spacer(modifier = Modifier.height(24.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                Button(
                    onClick = { /* Edit Photo Action */ },
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF334155))
                ) {
                    Text("Edit Photo", color = Color.White)
                }
                Button(
                    onClick = { /* Save Changes Action */ },
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF10B981)),
                    elevation = ButtonDefaults.buttonElevation(defaultElevation = 8.dp),
                    modifier = Modifier.shadow(8.dp, RoundedCornerShape(12.dp), spotColor = Color(0xFF10B981))
                ) {
                    Text("Save Changes", color = Color.White)
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LanguageSwitcher(languageManager: LanguageManager, context: Context){
    val languages = listOf("English", "हिंदी")
    var expanded by remember { mutableStateOf(false) }
    var selectedOptionText by remember { mutableStateOf(if(languageManager.getLanguage() == "hi") "हिंदी" else "English") }

    ExposedDropdownMenuBox(
        expanded = expanded,
        onExpandedChange = { expanded = !expanded },
        modifier = Modifier.padding(16.dp)
    ) {
        OutlinedTextField(
            value = selectedOptionText,
            onValueChange = {},
            readOnly = true,
            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
            modifier = Modifier.fillMaxWidth().menuAnchor(),
            label = { Text("App Language", color = Color.Gray) },
            shape = RoundedCornerShape(12.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = Color(0xFF00FFFF),
                unfocusedBorderColor = Color(0xFF334155),
                focusedTextColor = Color.White,
                unfocusedTextColor = Color.White,
                focusedContainerColor = Color(0xFF1E293B),
                unfocusedContainerColor = Color(0xFF1E293B)
            )
        )
        ExposedDropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false }
        ) {
            languages.forEach { selectionOption ->
                DropdownMenuItem(
                    text = { Text(selectionOption) },
                    onClick = {
                        selectedOptionText = selectionOption
                        val lang = if (selectionOption == "English") "en" else "hi"
                        languageManager.saveLanguage(lang)
                        val appLocale: LocaleListCompat = LocaleListCompat.forLanguageTags(lang)
                        AppCompatDelegate.setApplicationLocales(appLocale)
                        expanded = false
                    }
                )
            }
        }
    }
}

@Composable
fun Badge(text: String, color: Color) {
    Box(
        modifier = Modifier
            .background(color.copy(alpha = 0.1f), RoundedCornerShape(8.dp))
            .border(1.dp, color, RoundedCornerShape(8.dp))
            .padding(horizontal = 8.dp, vertical = 4.dp)
    ) {
        Text(text, color = color, fontSize = 10.sp, fontWeight = FontWeight.SemiBold)
    }
}

@Composable
fun PersonalInformationSection(user: com.healthai.app.domain.model.User) {
    var name by remember { mutableStateOf(TextFieldValue(user.name)) }
    var email by remember { mutableStateOf(TextFieldValue(user.email)) }
    
    Column(modifier = Modifier.padding(16.dp)) {
        Text(
            text = "Personal Information",
            color = Color.White,
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(bottom = 16.dp)
        )
        
        InfoTextField(label = "Full Name", value = name, onValueChange = { name = it })
        InfoTextField(label = "Email Address", value = email, onValueChange = { email = it })
        GenderDropdown()
        BloodGroupDropdown()
    }
}

@Composable
fun InfoTextField(label: String, value: TextFieldValue, onValueChange: (TextFieldValue) -> Unit) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        label = { Text(label, color = Color.Gray) },
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = 12.dp),
        shape = RoundedCornerShape(12.dp),
        colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor = Color(0xFF00FFFF),
            unfocusedBorderColor = Color(0xFF334155),
            focusedTextColor = Color.White,
            unfocusedTextColor = Color.White,
            focusedContainerColor = Color(0xFF1E293B),
            unfocusedContainerColor = Color(0xFF1E293B),
            cursorColor = Color(0xFF00FFFF)
        )
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GenderDropdown() {
    val options = listOf("Male", "Female", "Other")
    var expanded by remember { mutableStateOf(false) }
    var selectedOptionText by remember { mutableStateOf("Gender") }

    ExposedDropdownMenuBox(
        expanded = expanded,
        onExpandedChange = { expanded = !expanded },
        modifier = Modifier.padding(bottom = 12.dp)
    ) {
        OutlinedTextField(
            value = selectedOptionText,
            onValueChange = {},
            readOnly = true,
            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
            modifier = Modifier.fillMaxWidth().menuAnchor(),
            label = { Text("Gender", color = Color.Gray) },
            shape = RoundedCornerShape(12.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = Color(0xFF00FFFF),
                unfocusedBorderColor = Color(0xFF334155),
                focusedTextColor = Color.White,
                unfocusedTextColor = Color.White,
                focusedContainerColor = Color(0xFF1E293B),
                unfocusedContainerColor = Color(0xFF1E293B)
            )
        )
        ExposedDropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false }
        ) {
            options.forEach { selectionOption ->
                DropdownMenuItem(
                    text = { Text(selectionOption) },
                    onClick = {
                        selectedOptionText = selectionOption
                        expanded = false
                    }
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BloodGroupDropdown() {
    val options = listOf("A+", "A-", "B+", "B-", "AB+", "AB-", "O+", "O-")
    var expanded by remember { mutableStateOf(false) }
    var selectedOptionText by remember { mutableStateOf("Blood Group") }

    ExposedDropdownMenuBox(
        expanded = expanded,
        onExpandedChange = { expanded = !expanded },
        modifier = Modifier.padding(bottom = 12.dp)
    ) {
        OutlinedTextField(
            value = selectedOptionText,
            onValueChange = {},
            readOnly = true,
            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
            modifier = Modifier.fillMaxWidth().menuAnchor(),
            label = { Text("Blood Group", color = Color.Gray) },
            shape = RoundedCornerShape(12.dp),
             colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = Color(0xFF00FFFF),
                unfocusedBorderColor = Color(0xFF334155),
                focusedTextColor = Color.White,
                unfocusedTextColor = Color.White,
                focusedContainerColor = Color(0xFF1E293B),
                unfocusedContainerColor = Color(0xFF1E293B)
            )
        )
        ExposedDropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false }
        ) {
            options.forEach { selectionOption ->
                DropdownMenuItem(
                    text = { Text(selectionOption) },
                    onClick = {
                        selectedOptionText = selectionOption
                        expanded = false
                    }
                )
            }
        }
    }
}


@Composable
fun SettingsGrid(navController: NavController) {
    val settingsItems = listOf(
        "Security" to Icons.Default.Lock,
        "Notifications" to Icons.Default.Notifications,
        "Preferences" to Icons.Default.Favorite,
        "App Settings" to Icons.Default.Settings
    )
    
    Column(modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)) {
         Row(horizontalArrangement = Arrangement.spacedBy(16.dp), modifier = Modifier.fillMaxWidth()) {
            SettingsCard(title = settingsItems[0].first, icon = settingsItems[0].second, modifier = Modifier.weight(1f)) {}
            SettingsCard(title = settingsItems[1].first, icon = settingsItems[1].second, modifier = Modifier.weight(1f)) {}
        }
        Spacer(modifier = Modifier.height(16.dp))
        Row(horizontalArrangement = Arrangement.spacedBy(16.dp), modifier = Modifier.fillMaxWidth()) {
            SettingsCard(title = settingsItems[2].first, icon = settingsItems[2].second, modifier = Modifier.weight(1f)) {}
            SettingsCard(title = settingsItems[3].first, icon = settingsItems[3].second, modifier = Modifier.weight(1f)) {
                navController.navigate(NavRoutes.AppSettings)
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsCard(title: String, icon: ImageVector, modifier: Modifier = Modifier, onClick: () -> Unit) {
    Card(
        modifier = modifier.aspectRatio(1.5f),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFF1E293B)),
        onClick = onClick
    ) {
        Column(
            modifier = Modifier.fillMaxSize().padding(12.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Icon(icon, contentDescription = title, tint = Color(0xFF00FFFF), modifier = Modifier.size(32.dp))
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = title,
                color = Color.White,
                fontSize = 12.sp,
                textAlign = TextAlign.Center,
                fontWeight = FontWeight.SemiBold
            )
        }
    }
}

@Composable
fun DangerZone() {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 24.dp)
            .border(1.dp, Color(0xFFFF5252), RoundedCornerShape(16.dp)),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFFFF5252).copy(alpha = 0.1f)),
        onClick = { /* Handle Deactivate */ }
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 24.dp, vertical = 16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(Icons.Default.Warning, contentDescription = "Warning", tint = Color(0xFFFF5252))
            Spacer(modifier = Modifier.width(16.dp))
            Text(
                text = "Deactivate Account",
                color = Color(0xFFFF5252),
                fontWeight = FontWeight.Bold,
                fontSize = 16.sp
            )
        }
    }
}