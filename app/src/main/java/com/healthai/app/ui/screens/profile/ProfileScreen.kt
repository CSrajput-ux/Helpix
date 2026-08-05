package com.healthai.app.ui.screens.profile

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.pm.PackageManager
import android.location.Geocoder
import android.net.Uri
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ExitToApp
import androidx.compose.material.icons.automirrored.filled.HelpOutline
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.google.android.gms.location.LocationServices
import com.healthai.app.R
import com.healthai.app.data.remote.api.SignupRequest
import java.util.Locale

// Modern Healthcare Palette
val DeepSlate = Color(0xFF020617)
val MedicalTeal = Color(0xFF2DD4BF)
val CardBackground = Color(0xFF1E293B)
val TextGrey = Color(0xFF94A3B8)
val AccentBlue = Color(0xFF2563EB)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen(
    navController: NavController,
    viewModel: ProfileViewModel = viewModel(),
) {
    val user by viewModel.user.collectAsState()

    if (user == null) {
        AuthIntegratedScreen(navController, viewModel)
    } else {
        ProfileContent(navController, viewModel, user!!)
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AuthIntegratedScreen(navController: NavController, viewModel: ProfileViewModel) {
    var showAuthSheet by remember { mutableStateOf(false) }
    val sheetState = rememberModalBottomSheetState()

    Scaffold(
        containerColor = DeepSlate,
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text(
                text = "Profile",
                color = Color.White,
                fontSize = 28.sp,
                fontWeight = FontWeight.ExtraBold,
                modifier = Modifier.padding(bottom = 8.dp)
            )

            // 1. Header Card (Guest)
            MedicalCard {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(64.dp)
                            .background(CardBackground.copy(alpha = 0.5f), CircleShape)
                            .border(1.dp, MedicalTeal.copy(alpha = 0.3f), CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(Icons.Default.Person, contentDescription = null, tint = MedicalTeal, modifier = Modifier.size(32.dp))
                    }
                    Spacer(modifier = Modifier.width(16.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Text("Welcome to HELPiX", color = Color.White, fontSize = 18.sp, fontWeight = FontWeight.Bold)
                        Text("Sign in to sync your health data", color = TextGrey, fontSize = 13.sp)
                    }
                    Button(
                        onClick = { showAuthSheet = true },
                        colors = ButtonDefaults.buttonColors(containerColor = AccentBlue),
                        shape = RoundedCornerShape(12.dp),
                        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp)
                    ) {
                        Text("Sign In", color = Color.White, fontWeight = FontWeight.Bold)
                    }
                }
            }

                // 2. Switch Hub Card (Always show "Switch to Doctor Hub" for all users)
                MedicalCard(
                    backgroundColor = Color(0xFF1E293B),
                    modifier = Modifier.clickable { navController.navigate(com.healthai.app.ui.navigation.NavRoutes.DoctorDashboard) }
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(40.dp)
                                .background(MedicalTeal.copy(alpha = 0.1f), CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(Icons.Default.BusinessCenter, contentDescription = null, tint = MedicalTeal, modifier = Modifier.size(20.dp))
                        }
                        Spacer(modifier = Modifier.width(12.dp))
                        Text("Switch to Doctor Hub", color = Color.White, fontWeight = FontWeight.Bold)
                        Spacer(modifier = Modifier.weight(1f))
                        Icon(Icons.AutoMirrored.Filled.KeyboardArrowRight, contentDescription = null, tint = TextGrey)
                    }
                }

            // 3. Promo Card
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(24.dp))
                    .background(Brush.linearGradient(listOf(Color(0xFF2563EB), Color(0xFF3B82F6))))
            ) {
                // Background Illustration
                Icon(
                    Icons.Default.Celebration,
                    contentDescription = null,
                    modifier = Modifier
                        .align(Alignment.CenterEnd)
                        .offset(x = 20.dp)
                        .size(120.dp),
                    tint = Color.White.copy(alpha = 0.15f)
                )

                Column(modifier = Modifier.padding(24.dp)) {
                    Text("Spread the Word", color = Color.White, fontSize = 22.sp, fontWeight = FontWeight.ExtraBold)
                    Text("Share HELPiX with friends & family", color = Color.White.copy(alpha = 0.8f), fontSize = 14.sp)
                    Spacer(modifier = Modifier.height(20.dp))
                    Button(
                        onClick = { /* Share Logic */ },
                        colors = ButtonDefaults.buttonColors(containerColor = Color.White),
                        shape = RoundedCornerShape(12.dp),
                        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp)
                    ) {
                        Text("Share Now", color = AccentBlue, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                    }
                }
            }

            // 4. Categorized Sections
            ProfileSectionHeader("Health Vault")
            MedicalCard {
                ProfileListItem(Icons.Default.History, "Medical History", "View your past consultations") {
                    navController.navigate(com.healthai.app.ui.navigation.NavRoutes.HealthHistory)
                }
                HorizontalDivider(color = Color.White.copy(alpha = 0.05f))
                ProfileListItem(Icons.Default.Description, "Reports & Scans", "Access your uploaded documents") {
                    navController.navigate(com.healthai.app.ui.navigation.NavRoutes.HealthVault)
                }
                HorizontalDivider(color = Color.White.copy(alpha = 0.05f))
                ProfileListItem(Icons.Default.Vaccines, "Vaccinations", "Keep track of your immunizations") {
                    navController.navigate(com.healthai.app.ui.navigation.NavRoutes.Vaccinations)
                }
            }

            ProfileSectionHeader("Support")
            MedicalCard {
                ProfileListItem(Icons.AutoMirrored.Filled.HelpOutline, "Help Center", "FAQs and customer support") {
                    navController.navigate(com.healthai.app.ui.navigation.NavRoutes.HelpCenter)
                }
                HorizontalDivider(color = Color.White.copy(alpha = 0.05f))
                ProfileListItem(Icons.Default.Info, "About HELPiX", "Learn more about our mission") {
                    navController.navigate(com.healthai.app.ui.navigation.NavRoutes.AboutHelpix)
                }
            }
            
            Spacer(modifier = Modifier.height(32.dp))
        }
    }

    if (showAuthSheet) {
        ModalBottomSheet(
            onDismissRequest = { showAuthSheet = false },
            sheetState = sheetState,
            containerColor = CardBackground,
            dragHandle = { BottomSheetDefaults.DragHandle(color = MedicalTeal.copy(alpha = 0.5f)) }
        ) {
            AuthForm(viewModel) { showAuthSheet = false }
        }
    }
}

@Composable
fun AuthForm(viewModel: ProfileViewModel, onAuthSuccess: () -> Unit) {
    var isLoginMode by remember { mutableStateOf(true) }
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var name by remember { mutableStateOf("") }
    val context = LocalContext.current
    val isLoading by viewModel.isLoading.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(24.dp)
            .padding(bottom = 32.dp)
            .verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = if (isLoginMode) "Welcome Back" else "Create Account",
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold,
            color = Color.White
        )
        Text(
            text = if (isLoginMode) "Sign in to continue" else "Join HELPiX today",
            fontSize = 14.sp,
            color = TextGrey
        )
        
        Spacer(modifier = Modifier.height(32.dp))

        if (!isLoginMode) {
            ProfileTextField(
                label = "Full Name",
                value = name,
                onValueChange = { name = it },
                enabled = true,
                icon = Icons.Default.Person
            )
        }

        ProfileTextField(
            label = "Email",
            value = email,
            onValueChange = { email = it },
            enabled = true,
            icon = Icons.Default.Email
        )

        ProfileTextField(
            label = "Password",
            value = password,
            onValueChange = { password = it },
            enabled = true,
            icon = Icons.Default.Lock,
            keyboardType = KeyboardType.Password
        )

        Spacer(modifier = Modifier.height(32.dp))

        if (isLoading) {
            CircularProgressIndicator(color = MedicalTeal)
        } else {
            Button(
                onClick = {
                    if (isLoginMode) {
                        viewModel.login(email, password) { success, msg ->
                            Toast.makeText(context, msg, Toast.LENGTH_SHORT).show()
                            if (success) onAuthSuccess()
                        }
                    } else {
                        // Default all new users to PATIENT role
                        val req = SignupRequest(name, email, password, "PATIENT")
                        viewModel.signup(req) { success, msg ->
                            Toast.makeText(context, msg, Toast.LENGTH_SHORT).show()
                            if (success) onAuthSuccess()
                        }
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                shape = RoundedCornerShape(16.dp),
                colors = ButtonDefaults.buttonColors(containerColor = MedicalTeal)
            ) {
                Text(if (isLoginMode) "SIGN IN" else "REGISTER", fontWeight = FontWeight.Bold, color = DeepSlate)
            }
        }

        TextButton(onClick = { isLoginMode = !isLoginMode }) {
            Text(
                text = if (isLoginMode) "New to HELPiX? Create an account" else "Already have an account? Sign In",
                color = MedicalTeal,
                fontWeight = FontWeight.Medium
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileContent(
    navController: NavController,
    viewModel: ProfileViewModel,
    user: com.healthai.app.data.remote.api.UserProfile
) {
    val context = LocalContext.current

    // Form States
    var fullName by remember { mutableStateOf("") }
    var age by remember { mutableStateOf("") }
    var bloodGroup by remember { mutableStateOf("") }
    var location by remember { mutableStateOf("") }
    var gender by remember { mutableStateOf("") }
    var emergencyContact by remember { mutableStateOf("") }
    var allergies by remember { mutableStateOf("") }
    
    // Doctor specific states
    var specialization by remember { mutableStateOf("") }
    var licenseNumber by remember { mutableStateOf("") }
    var clinicAddress by remember { mutableStateOf("") }

    var isEditing by remember { mutableStateOf(false) }
    var genderExpanded by remember { mutableStateOf(false) }
    val genderOptions = listOf("Male", "Female", "Other", "Prefer not to say")

    val fusedLocationClient = remember { LocationServices.getFusedLocationProviderClient(context) }

    val locationPermissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) {
            getCurrentLocation(context, fusedLocationClient) { loc ->
                location = loc
            }
        }
    }

    val imagePickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        uri?.let { viewModel.uploadProfileImage(it) }
    }

    LaunchedEffect(user) {
        fullName = user.full_name ?: ""
        age = user.age?.toString() ?: ""
        bloodGroup = user.blood_group ?: ""
        location = user.location ?: ""
        gender = user.gender ?: ""
        emergencyContact = user.emergency_contact ?: ""
        allergies = user.allergies ?: ""
        specialization = user.specialization ?: ""
        licenseNumber = user.license_number ?: ""
        clinicAddress = user.clinic_address ?: ""
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(if (user.role == "DOCTOR") "Doctor Profile" else "My Profile", color = Color.White, fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", tint = Color.White)
                    }
                },
                actions = {
                    val isLoadingContent by viewModel.isLoading.collectAsState()
                    if (!isLoadingContent) {
                        TextButton(
                            onClick = {
                                if (isEditing) {
                                    viewModel.updateProfile(
                                        fullName = fullName,
                                        age = age.toIntOrNull(),
                                        bloodGroup = bloodGroup,
                                        gender = gender,
                                        emergencyContact = emergencyContact,
                                        location = location,
                                        allergies = allergies,
                                        specialization = if (user.role == "DOCTOR") specialization else null,
                                        licenseNumber = if (user.role == "DOCTOR") licenseNumber else null,
                                        clinicAddress = if (user.role == "DOCTOR") clinicAddress else null
                                    )
                                    isEditing = false
                                    Toast.makeText(context, "Profile Updated", Toast.LENGTH_SHORT).show()
                                } else {
                                    isEditing = true
                                }
                            }) {
                            Text(
                                if (isEditing) "SAVE" else "EDIT",
                                color = MedicalTeal,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = DeepSlate)
            )
        },
        containerColor = DeepSlate
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Header: Image and Basic Info
            MedicalCard {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Box(
                        modifier = Modifier
                            .size(100.dp)
                            .clip(CircleShape)
                            .border(2.dp, MedicalTeal, CircleShape)
                            .clickable { if (isEditing) imagePickerLauncher.launch("image/*") }
                    ) {
                        AsyncImage(
                            model = ImageRequest.Builder(LocalContext.current)
                                .data(user.profile_image_url)
                                .crossfade(true)
                                .build(),
                            contentDescription = "Profile Picture",
                            modifier = Modifier.fillMaxSize(),
                            contentScale = ContentScale.Crop,
                            error = androidx.compose.ui.graphics.vector.rememberVectorPainter(Icons.Default.Person),
                            placeholder = androidx.compose.ui.graphics.vector.rememberVectorPainter(Icons.Default.Person)
                        )
                        if (isEditing) {
                            Box(
                                modifier = Modifier.fillMaxSize().background(Color.Black.copy(alpha = 0.4f)),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(Icons.Default.CameraAlt, contentDescription = null, tint = Color.White)
                            }
                        }
                    }
                    Spacer(modifier = Modifier.height(12.dp))
                    Text(fullName.ifEmpty { "Health Enthusiast" }, color = Color.White, fontSize = 20.sp, fontWeight = FontWeight.Bold)
                    Text(user.email ?: "", color = TextGrey, fontSize = 14.sp)
                    
                    if (user.role == "DOCTOR") {
                        Spacer(modifier = Modifier.height(8.dp))
                        SuggestionChip(
                            onClick = { },
                            label = { Text(specialization.ifEmpty { "General Physician" }) },
                            colors = SuggestionChipDefaults.suggestionChipColors(labelColor = MedicalTeal),
                            border = BorderStroke(1.dp, MedicalTeal.copy(alpha = 0.5f))
                        )
                    }
                }
            }

            if (isEditing) {
                // Editing Mode
                ProfileSectionHeader("Personal Information")
                MedicalCard {
                    ProfileTextField("Full Name", fullName, { fullName = it }, true, Icons.Default.Person)
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        Box(modifier = Modifier.weight(1f)) {
                            ProfileTextField("Age", age, { age = it }, true, Icons.Default.DateRange, KeyboardType.Number)
                        }
                        Box(modifier = Modifier.weight(1f)) {
                            ProfileTextField("Blood Group", bloodGroup, { bloodGroup = it }, true, Icons.Default.Bloodtype)
                        }
                    }
                    
                    ExposedDropdownMenuBox(
                        expanded = genderExpanded,
                        onExpandedChange = { genderExpanded = !genderExpanded },
                        modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp)
                    ) {
                        OutlinedTextField(
                            value = gender,
                            onValueChange = {},
                            readOnly = true,
                            label = { Text("Gender") },
                            leadingIcon = { Icon(Icons.Default.Transgender, contentDescription = null, tint = MedicalTeal) },
                            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = genderExpanded) },
                            modifier = Modifier.menuAnchor(MenuAnchorType.PrimaryNotEditable).fillMaxWidth(),
                            shape = RoundedCornerShape(12.dp),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = MedicalTeal,
                                unfocusedBorderColor = Color.Gray,
                                focusedTextColor = Color.White,
                                unfocusedTextColor = Color.White
                            )
                        )
                        ExposedDropdownMenu(
                            expanded = genderExpanded,
                            onDismissRequest = { genderExpanded = false },
                            modifier = Modifier.background(CardBackground)
                        ) {
                            genderOptions.forEach { option ->
                                DropdownMenuItem(
                                    text = { Text(option, color = Color.White) },
                                    onClick = { gender = option; genderExpanded = false }
                                )
                            }
                        }
                    }
                }

                ProfileSectionHeader(if (user.role == "DOCTOR") "Professional Information" else "Medical Information")
                MedicalCard {
                    if (user.role == "DOCTOR") {
                        ProfileTextField("Specialization", specialization, { specialization = it }, true, Icons.Default.VerifiedUser)
                        ProfileTextField("License Number", licenseNumber, { licenseNumber = it }, true, Icons.Default.MedicalServices)
                        ProfileTextField("Clinic Address", clinicAddress, { clinicAddress = it }, true, Icons.Default.Business)
                    } else {
                        ProfileTextField("Emergency Contact", emergencyContact, { emergencyContact = it }, true, Icons.Default.Phone, KeyboardType.Phone)
                        ProfileTextField("Allergies", allergies, { allergies = it }, true, Icons.Default.Warning)
                    }
                }

                ProfileSectionHeader("Location")
                MedicalCard {
                    ProfileTextField(
                        label = "Location",
                        value = location,
                        onValueChange = { location = it },
                        enabled = true,
                        icon = Icons.Default.LocationOn,
                        trailingIcon = {
                            IconButton(onClick = {
                                if (ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                                    getCurrentLocation(context, fusedLocationClient) { location = it }
                                } else {
                                    locationPermissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION)
                                }
                            }) {
                                Icon(Icons.Default.MyLocation, contentDescription = null, tint = MedicalTeal)
                            }
                        }
                    )
                }
            } else {
                // Display Mode (Card Based List)

                // --- SWITCH TO DOCTOR HUB CARD (Always visible after login) ---
                MedicalCard(
                    backgroundColor = Color(0xFF1E293B),
                    modifier = Modifier.clickable { navController.navigate(com.healthai.app.ui.navigation.NavRoutes.DoctorDashboard) }
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(40.dp)
                                .background(MedicalTeal.copy(alpha = 0.1f), CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(Icons.Default.BusinessCenter, contentDescription = null, tint = MedicalTeal, modifier = Modifier.size(20.dp))
                        }
                        Spacer(modifier = Modifier.width(12.dp))
                        Text(
                            text = if (user.role == "DOCTOR") "Enter Doctor Dashboard" else "Switch to Doctor Hub", 
                            color = Color.White, 
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(modifier = Modifier.weight(1f))
                        Icon(Icons.AutoMirrored.Filled.KeyboardArrowRight, contentDescription = null, tint = TextGrey)
                    }
                }

                if (user.role != "DOCTOR") {
                    ProfileSectionHeader("Emergency Card")
                    Card(
                        colors = CardDefaults.cardColors(containerColor = Color(0xFF450A0A)), // Deep Red
                        shape = RoundedCornerShape(24.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(modifier = Modifier.padding(20.dp)) {
                            Row(modifier = Modifier.fillMaxWidth()) {
                                EmergencyItem("Blood Group", bloodGroup.ifEmpty { "Unknown" }, Modifier.weight(1f), Icons.Default.Bloodtype)
                                EmergencyItem("Age", if (age.isNotEmpty()) "$age Yrs" else "--", Modifier.weight(1f))
                            }
                            Spacer(modifier = Modifier.height(16.dp))
                            EmergencyItem("Allergies", allergies.ifEmpty { "None Reported" }, Modifier.fillMaxWidth())
                        }
                    }
                }

                ProfileSectionHeader("Personal Details")
                MedicalCard {
                    ProfileListItem(Icons.Default.Person, "Gender", gender.ifEmpty { "Not specified" }) {}
                    HorizontalDivider(color = Color.White.copy(alpha = 0.05f))
                    ProfileListItem(Icons.Default.LocationOn, "Location", location.ifEmpty { "Not set" }) {}
                }

                ProfileSectionHeader("Health Vault")
                MedicalCard {
                    ProfileListItem(Icons.Default.History, "Medical History", "View your past consultations") {
                        navController.navigate(com.healthai.app.ui.navigation.NavRoutes.HealthHistory)
                    }
                    HorizontalDivider(color = Color.White.copy(alpha = 0.05f))
                    ProfileListItem(Icons.Default.Description, "Reports & Scans", "Access your uploaded documents") {
                        navController.navigate(com.healthai.app.ui.navigation.NavRoutes.HealthVault)
                    }
                    HorizontalDivider(color = Color.White.copy(alpha = 0.05f))
                    ProfileListItem(Icons.Default.Vaccines, "Vaccinations", "Keep track of your immunizations") {
                        navController.navigate(com.healthai.app.ui.navigation.NavRoutes.Vaccinations)
                    }
                }

                ProfileSectionHeader("Contact & Security")
                MedicalCard {
                    ProfileListItem(Icons.Default.Phone, "Emergency Contact", emergencyContact.ifEmpty { "Not set" }) {}
                    HorizontalDivider(color = Color.White.copy(alpha = 0.05f))
                    ProfileListItem(Icons.Default.Email, "Email Address", user.email ?: "") {}
                }

                ProfileSectionHeader("Support")
                MedicalCard {
                    ProfileListItem(Icons.AutoMirrored.Filled.HelpOutline, "Help Center", "FAQs and customer support") {
                        navController.navigate(com.healthai.app.ui.navigation.NavRoutes.HelpCenter)
                    }
                    HorizontalDivider(color = Color.White.copy(alpha = 0.05f))
                    ProfileListItem(Icons.Default.Info, "About HELPiX", "Learn more about our mission") {
                        navController.navigate(com.healthai.app.ui.navigation.NavRoutes.AboutHelpix)
                    }
                }

                Button(
                    onClick = { viewModel.logout() },
                    modifier = Modifier.fillMaxWidth().padding(top = 16.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color.Red.copy(alpha = 0.1f)),
                    border = BorderStroke(1.dp, Color.Red.copy(alpha = 0.3f)),
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Icon(Icons.AutoMirrored.Filled.ExitToApp, contentDescription = null, tint = Color.Red)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Sign Out", color = Color.Red, fontWeight = FontWeight.Bold)
                }
            }
            
            Spacer(modifier = Modifier.height(32.dp))
        }
    }
}

// --- REUSABLE COMPONENTS ---

@Composable
fun MedicalCard(
    modifier: Modifier = Modifier,
    backgroundColor: Color = CardBackground,
    content: @Composable ColumnScope.() -> Unit
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = backgroundColor),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Column(modifier = Modifier.padding(20.dp), content = content)
    }
}

@Composable
fun ProfileListItem(
    icon: ImageVector,
    title: String,
    subtitle: String? = null,
    trailingIcon: ImageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .padding(vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(40.dp)
                .background(DeepSlate.copy(alpha = 0.5f), CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Icon(icon, contentDescription = null, tint = MedicalTeal, modifier = Modifier.size(20.dp))
        }
        Spacer(modifier = Modifier.width(16.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(title, color = Color.White, fontWeight = FontWeight.SemiBold, fontSize = 15.sp)
            if (subtitle != null) {
                Text(subtitle, color = TextGrey, fontSize = 12.sp)
            }
        }
        Icon(trailingIcon, contentDescription = null, tint = TextGrey, modifier = Modifier.size(18.dp))
    }
}

@Composable
fun ProfileSectionHeader(title: String) {
    Text(
        text = title,
        color = TextGrey,
        fontSize = 13.sp,
        fontWeight = FontWeight.Medium,
        modifier = Modifier.padding(top = 8.dp, bottom = 4.dp)
    )
}

@Composable
fun AuthRoleItem(label: String, isSelected: Boolean, modifier: Modifier, onClick: () -> Unit) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(12.dp))
            .background(if (isSelected) MedicalTeal else Color.Transparent)
            .clickable { onClick() }
            .padding(vertical = 10.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = label,
            color = if (isSelected) DeepSlate else Color.White,
            fontWeight = FontWeight.Bold
        )
    }
}

@Composable
fun EmergencyItem(label: String, value: String, modifier: Modifier, icon: ImageVector? = null) {
    Column(modifier = modifier) {
        Text(label, color = Color.White.copy(alpha = 0.6f), fontSize = 11.sp, fontWeight = FontWeight.Medium)
        Spacer(modifier = Modifier.height(2.dp))
        Row(verticalAlignment = Alignment.CenterVertically) {
            if (icon != null) {
                Icon(icon, contentDescription = null, tint = Color.White, modifier = Modifier.size(14.dp))
                Spacer(modifier = Modifier.width(4.dp))
            }
            Text(value, color = Color.White, fontSize = 16.sp, fontWeight = FontWeight.ExtraBold)
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileTextField(
    label: String,
    value: String,
    onValueChange: (String) -> Unit,
    enabled: Boolean,
    icon: ImageVector,
    keyboardType: KeyboardType = KeyboardType.Text,
    trailingIcon: @Composable (() -> Unit)? = null
) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        label = { Text(label) },
        enabled = enabled,
        modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
        leadingIcon = { Icon(icon, contentDescription = null, tint = if (enabled) MedicalTeal else Color.Gray) },
        trailingIcon = trailingIcon,
        shape = RoundedCornerShape(12.dp),
        keyboardOptions = KeyboardOptions(keyboardType = keyboardType),
        colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor = MedicalTeal,
            unfocusedBorderColor = Color.Gray,
            disabledBorderColor = Color.DarkGray,
            disabledTextColor = Color.White,
            focusedTextColor = Color.White,
            unfocusedTextColor = Color.White,
            disabledLabelColor = Color.Gray,
            focusedContainerColor = Color.Transparent,
            unfocusedContainerColor = Color.Transparent
        )
    )
}

@SuppressLint("MissingPermission")
fun getCurrentLocation(context: Context, client: com.google.android.gms.location.FusedLocationProviderClient, onLocationFound: (String) -> Unit) {
    client.lastLocation.addOnSuccessListener { loc ->
        if (loc != null) {
            val geocoder = Geocoder(context, Locale.getDefault())
            try {
                val addresses = geocoder.getFromLocation(loc.latitude, loc.longitude, 1)
                if (!addresses.isNullOrEmpty()) {
                    val address = addresses[0]
                    val city = address.locality ?: ""
                    val subLocality = address.subLocality ?: ""
                    onLocationFound(if (subLocality.isNotEmpty()) "$subLocality, $city" else city)
                } else {
                    onLocationFound("${loc.latitude}, ${loc.longitude}")
                }
            } catch (_: Exception) {
                onLocationFound("${loc.latitude}, ${loc.longitude}")
            }
        } else {
            Toast.makeText(context, "Please turn on GPS and wait a moment", Toast.LENGTH_SHORT).show()
        }
    }
}
