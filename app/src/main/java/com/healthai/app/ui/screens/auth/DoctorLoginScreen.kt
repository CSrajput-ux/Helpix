package com.healthai.app.ui.screens.auth

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
import androidx.compose.material3.TextButton
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
import com.google.firebase.firestore.FirebaseFirestore
import com.healthai.app.R
import com.healthai.app.ui.components.AIButton
import com.healthai.app.ui.navigation.NavRoutes
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DoctorLoginScreen(navController: NavController) {
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var isLoading by remember { mutableStateOf(false) }

    val scope = rememberCoroutineScope()
    val context = LocalContext.current
    val auth = FirebaseAuth.getInstance()
    val firestore = FirebaseFirestore.getInstance()

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
            Text(stringResource(id = R.string.doctor_login), color = Color.White, fontSize = 28.sp, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.height(32.dp))

            val commonColors = TextFieldDefaults.outlinedTextFieldColors(
                focusedBorderColor = colorResource(id = R.color.logo_cyan),
                unfocusedBorderColor = colorResource(id = R.color.text_grey),
                focusedTextColor = Color.White,
                containerColor = Color.Transparent
            )

            OutlinedTextField(value = email, onValueChange = { email = it }, label = { Text(stringResource(id = R.string.email), color = Color.Gray) }, colors = commonColors, modifier = Modifier.fillMaxWidth())
            Spacer(modifier = Modifier.height(16.dp))
            OutlinedTextField(value = password, onValueChange = { password = it }, label = { Text(stringResource(id = R.string.password), color = Color.Gray) }, colors = commonColors, modifier = Modifier.fillMaxWidth())
            
            Spacer(modifier = Modifier.height(32.dp))

            if (isLoading) {
                Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator(color = colorResource(id = R.color.logo_cyan))
                }
            } else {
                AIButton(
                    text = stringResource(id = R.string.login).uppercase(),
                    backgroundColor = colorResource(id = R.color.logo_blue),
                    textColor = Color.White,
                    onClick = {
                        if (email.isNotBlank() && password.isNotBlank()) {
                            isLoading = true
                            auth.signInWithEmailAndPassword(email, password)
                                .addOnCompleteListener { task ->
                                    if (task.isSuccessful) {
                                        scope.launch {
                                            val firebaseUser = auth.currentUser!!
                                            val userDoc = firestore.collection("users").document(firebaseUser.uid).get().await()
                                            if (userDoc.exists() && userDoc.getString("userType") == "DOCTOR") {
                                                Toast.makeText(context, "Doctor login successful!", Toast.LENGTH_SHORT).show()
                                                navController.navigate(NavRoutes.DoctorDashboard) { popUpTo(NavRoutes.Login) { inclusive = true } }
                                            } else {
                                                auth.signOut()
                                                Toast.makeText(context, "Login failed: Not a doctor account.", Toast.LENGTH_LONG).show()
                                            }
                                            isLoading = false
                                        }
                                    } else {
                                        Toast.makeText(context, "Login failed: ${task.exception?.message}", Toast.LENGTH_LONG).show()
                                        isLoading = false
                                    }
                                }
                        } else {
                            Toast.makeText(context, "Please fill all fields.", Toast.LENGTH_SHORT).show()
                        }
                    }
                )
            }
            Spacer(modifier = Modifier.height(16.dp))
            TextButton(onClick = { navController.navigate(NavRoutes.DoctorRegister) }) {
                Text(stringResource(id = R.string.are_you_a_doctor), color = colorResource(id = R.color.logo_cyan))
            }
        }
    }
}