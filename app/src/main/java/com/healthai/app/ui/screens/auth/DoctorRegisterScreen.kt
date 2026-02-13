package com.healthai.app.ui.screens.auth

import android.util.Patterns
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.google.firebase.auth.FirebaseAuth
import com.healthai.app.R
import com.healthai.app.data.repository.UserRepository
import com.healthai.app.domain.model.User
import com.healthai.app.ui.components.AIButton
import com.healthai.app.ui.navigation.NavRoutes
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DoctorRegisterScreen(navController: NavController) {
    var name by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var specialization by remember { mutableStateOf("") }
    var licenseNumber by remember { mutableStateOf("") }
    var clinicAddress by remember { mutableStateOf("") }
    var isLoading by remember { mutableStateOf(false) }

    val scope = rememberCoroutineScope()
    val context = LocalContext.current
    val auth = FirebaseAuth.getInstance()
    val userRepository = UserRepository()

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                brush = Brush.verticalGradient(
                    colors = listOf(
                        colorResource(id = R.color.login_bg_top),
                        colorResource(id = R.color.login_bg_bottom)
                    )
                )
            )
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp),
            verticalArrangement = Arrangement.Center
        ) {
            Text(stringResource(id = R.string.doctor_registration), color = Color.White, fontSize = 28.sp, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.height(32.dp))

            val commonColors = TextFieldDefaults.outlinedTextFieldColors(
                focusedBorderColor = colorResource(id = R.color.logo_cyan),
                unfocusedBorderColor = colorResource(id = R.color.text_grey),
                focusedTextColor = Color.White,
                containerColor = Color.Transparent
            )

            OutlinedTextField(value = name, onValueChange = { name = it }, label = { Text(stringResource(id = R.string.full_name), color = Color.Gray) }, colors = commonColors, modifier = Modifier.fillMaxWidth())
            Spacer(modifier = Modifier.height(16.dp))
            OutlinedTextField(value = email, onValueChange = { email = it }, label = { Text(stringResource(id = R.string.email), color = Color.Gray) }, colors = commonColors, modifier = Modifier.fillMaxWidth())
            Spacer(modifier = Modifier.height(16.dp))
            OutlinedTextField(value = password, onValueChange = { password = it }, label = { Text(stringResource(id = R.string.password), color = Color.Gray) }, colors = commonColors, modifier = Modifier.fillMaxWidth())
            Spacer(modifier = Modifier.height(16.dp))
            OutlinedTextField(value = specialization, onValueChange = { specialization = it }, label = { Text("Specialization", color = Color.Gray) }, colors = commonColors, modifier = Modifier.fillMaxWidth())
            Spacer(modifier = Modifier.height(16.dp))
            OutlinedTextField(value = licenseNumber, onValueChange = { licenseNumber = it }, label = { Text("License Number", color = Color.Gray) }, colors = commonColors, modifier = Modifier.fillMaxWidth())
            Spacer(modifier = Modifier.height(16.dp))
            OutlinedTextField(value = clinicAddress, onValueChange = { clinicAddress = it }, label = { Text("Clinic Address", color = Color.Gray) }, colors = commonColors, modifier = Modifier.fillMaxWidth())

            Spacer(modifier = Modifier.height(32.dp))

            if (isLoading) {
                Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator(color = colorResource(id = R.color.logo_cyan))
                }
            } else {
                AIButton(
                    text = stringResource(id = R.string.register).uppercase(),
                    backgroundColor = colorResource(id = R.color.logo_blue),
                    textColor = Color.White,
                    onClick = {
                        if (name.isBlank() || email.isBlank() || password.isBlank() || specialization.isBlank() || licenseNumber.isBlank()) {
                            Toast.makeText(context, "Please fill all mandatory fields.", Toast.LENGTH_SHORT).show()
                            return@AIButton
                        }
                        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()){
                            Toast.makeText(context, "Please enter a valid email address.", Toast.LENGTH_SHORT).show()
                            return@AIButton
                        }
                        if (password.length < 6){
                            Toast.makeText(context, "Password must be at least 6 characters long.", Toast.LENGTH_SHORT).show()
                            return@AIButton
                        }

                        isLoading = true
                        auth.createUserWithEmailAndPassword(email, password)
                            .addOnCompleteListener { task ->
                                if (task.isSuccessful) {
                                    val firebaseUser = auth.currentUser
                                    val user = User(
                                        id = firebaseUser!!.uid,
                                        email = email,
                                        name = name,
                                        userType = "DOCTOR",
                                        specialization = specialization,
                                        licenseNumber = licenseNumber,
                                        clinicAddress = clinicAddress
                                    )
                                    scope.launch {
                                        userRepository.createUser(user)
                                        Toast.makeText(context, "Doctor registration successful!", Toast.LENGTH_SHORT).show()
                                        navController.navigate(NavRoutes.DoctorDashboard) { popUpTo(NavRoutes.Login) { inclusive = true } }
                                        isLoading = false
                                    }
                                } else {
                                    Toast.makeText(context, "Registration failed: ${task.exception?.message}", Toast.LENGTH_LONG).show()
                                    isLoading = false
                                }
                            }

                    }
                )
            }
        }
    }
}