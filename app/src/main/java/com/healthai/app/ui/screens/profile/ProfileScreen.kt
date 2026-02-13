package com.healthai.app.ui.screens.profile

import android.app.Activity
import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatDelegate
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
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
    val languageManager = remember { LanguageManager(context) }

    Scaffold(
        containerColor = Color(0xFF0B1221)
    ) { paddingValues ->
        if (isLoading) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator(color = Color(0xFF00FFFF))
            }
        } else {
            user?.let { currentUser ->
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues)
                        .background(Color(0xFF0B1221))
                        .verticalScroll(scrollState)
                ) {
                    ProfileTopHeader(navController) { 
                        viewModel.logout()
                        navController.navigate("login_screen") { popUpTo(0) }
                    }
                    ProfileHeaderCard(currentUser)
                    PersonalInformationSection(
                        fullName = TextFieldValue(currentUser.name),
                        onFullNameChange = {},
                        email = TextFieldValue(currentUser.email),
                        onEmailChange = {},
                        phone = TextFieldValue("+1 234 567 890"),
                        onPhoneChange = {},
                        dob = TextFieldValue(currentUser.dateOfBirth?.toString() ?: ""),
                        onDobChange = {},
                        address = TextFieldValue(currentUser.clinicAddress ?: ""),
                        onAddressChange = {},
                        emergencyContact = TextFieldValue("+1 987 654 321"),
                        onEmergencyContactChange = {}
                    )
                    LanguageSwitcher(languageManager, context)
                    SettingsGrid(navController = navController)
                    DangerZone()
                    Spacer(modifier = Modifier.height(24.dp))
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
                painter = painterResource(id = R.drawable.ic_launcher_background), // Replace with your avatar
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
                text = user.name,
                color = Color.White,
                fontSize = 22.sp,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = user.email,
                color = Color.Gray,
                fontSize = 14.sp
            )
            Spacer(modifier = Modifier.height(12.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                Badge(text = "Verified Account", color = Color(0xFF10B981)) // Green
                if(user.userType == "DOCTOR"){
                    Badge(text = "Doctor", color = Color(0xFF06B6D4)) // Teal
                } else {
                    Badge(text = "Patient", color = Color(0xFF06B6D4)) // Teal
                }
            }
            Spacer(modifier = Modifier.height(24.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                Button(
                    onClick = { /* Handle Cancel */ },
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF334155))
                ) {
                    Text("Cancel", color = Color.White)
                }
                Button(
                    onClick = { /* Handle Save */ },
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF10B981)),
                    elevation = ButtonDefaults.buttonElevation(defaultElevation = 8.dp),
                    modifier = Modifier.shadow(8.dp, RoundedCornerShape(12.dp), spotColor = Color(0xFF10B981))
                ) {
                    Text("Save", color = Color.White)
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
            label = { Text("Language", color = Color.Gray) },
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
fun PersonalInformationSection(
    fullName: TextFieldValue, onFullNameChange: (TextFieldValue) -> Unit,
    email: TextFieldValue, onEmailChange: (TextFieldValue) -> Unit,
    phone: TextFieldValue, onPhoneChange: (TextFieldValue) -> Unit,
    dob: TextFieldValue, onDobChange: (TextFieldValue) -> Unit,
    address: TextFieldValue, onAddressChange: (TextFieldValue) -> Unit,
    emergencyContact: TextFieldValue, onEmergencyContactChange: (TextFieldValue) -> Unit
) {
    Column(modifier = Modifier.padding(16.dp)) {
        Text(
            text = "Personal Information",
            color = Color.White,
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(bottom = 16.dp)
        )
        
        InfoTextField(label = "Full Name", value = fullName, onValueChange = onFullNameChange)
        InfoTextField(label = "Email Address", value = email, onValueChange = onEmailChange)
        InfoTextField(label = "Phone Number", value = phone, onValueChange = onPhoneChange)
        InfoTextField(label = "Date of Birth", value = dob, onValueChange = onDobChange)
        GenderDropdown()
        BloodGroupDropdown()
        InfoTextField(label = "Address", value = address, onValueChange = onAddressChange)
        InfoTextField(label = "Emergency Contact", value = emergencyContact, onValueChange = onEmergencyContactChange)
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
    val options = listOf("Male", "Female", "Other", "Prefer not to say")
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
        "Security Settings" to Icons.Default.Lock,
        "Notifications" to Icons.Default.Notifications,
        "Health Preferences" to Icons.Default.Favorite,
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
