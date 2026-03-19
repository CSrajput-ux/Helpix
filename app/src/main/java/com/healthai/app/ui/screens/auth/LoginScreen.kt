package com.healthai.app.ui.screens.auth

import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.outlined.Email
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.google.firebase.auth.FirebaseAuth
import com.healthai.app.R
import com.healthai.app.ui.navigation.NavRoutes
import kotlinx.coroutines.launch

@Composable
fun LoginScreen(navController: NavController) {
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var isRememberMeChecked by remember { mutableStateOf(false) }
    var isLoading by remember { mutableStateOf(false) }
    var userRole by remember { mutableStateOf("PATIENT") } // Default to Patient

    val scope = rememberCoroutineScope()
    val context = LocalContext.current
    val auth = FirebaseAuth.getInstance()

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
        GridBackgroundLogin()

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier.size(80.dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(60.dp)
                        .blur(20.dp)
                        .background(colorResource(id = R.color.logo_blue).copy(alpha = 0.5f), CircleShape)
                )
                Box(
                    modifier = Modifier
                        .size(60.dp)
                        .background(
                            brush = Brush.linearGradient(
                                colors = listOf(colorResource(id = R.color.logo_cyan), colorResource(id = R.color.logo_blue))
                            ),
                            shape = CircleShape
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = if (userRole == "PATIENT") Icons.Default.Person else Icons.Default.Lock, 
                        contentDescription = null,
                        tint = Color.White,
                        modifier = Modifier.size(30.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            Text(
                text = buildAnnotatedString {
                    withStyle(style = SpanStyle(color = Color.White, fontWeight = FontWeight.Bold)) {
                        append("HELP")
                    }
                    withStyle(style = SpanStyle(color = colorResource(id = R.color.logo_cyan), fontWeight = FontWeight.Bold)) {
                        append("iX")
                    }
                },
                fontSize = 28.sp
            )
            
            Text(
                text = if (userRole == "PATIENT") "✨ Patient Portal Login" else "⚕️ Doctor Portal Login",
                color = colorResource(id = R.color.text_grey),
                fontSize = 14.sp,
                fontWeight = FontWeight.Medium
            )

            Spacer(modifier = Modifier.height(24.dp))

            // Role Switcher
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color(0xFF1E293B).copy(alpha = 0.5f), RoundedCornerShape(12.dp))
                    .padding(4.dp)
            ) {
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .clip(RoundedCornerShape(8.dp))
                        .background(if (userRole == "PATIENT") colorResource(id = R.color.logo_cyan) else Color.Transparent)
                        .clickable { userRole = "PATIENT" }
                        .padding(vertical = 10.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text("Patient", color = if (userRole == "PATIENT") Color.Black else Color.White, fontWeight = FontWeight.Bold)
                }
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .clip(RoundedCornerShape(8.dp))
                        .background(if (userRole == "DOCTOR") colorResource(id = R.color.logo_cyan) else Color.Transparent)
                        .clickable { userRole = "DOCTOR" }
                        .padding(vertical = 10.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text("Doctor", color = if (userRole == "DOCTOR") Color.Black else Color.White, fontWeight = FontWeight.Bold)
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .shadow(elevation = 20.dp, spotColor = colorResource(id = R.color.card_border_glow))
                    .border(1.dp, colorResource(id = R.color.card_border_glow).copy(alpha = 0.3f), RoundedCornerShape(24.dp))
                    .clip(RoundedCornerShape(24.dp))
                    .background(
                        brush = Brush.verticalGradient(
                            colors = listOf(
                                colorResource(id = R.color.card_bg_gradient_start),
                                colorResource(id = R.color.card_bg_gradient_end)
                            )
                        )
                    )
                    .padding(24.dp)
            ) {
                Column {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(40.dp)
                                .background(colorResource(id = R.color.logo_cyan).copy(alpha = 0.1f), CircleShape)
                                .border(1.dp, colorResource(id = R.color.logo_cyan).copy(alpha = 0.3f), CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(Icons.Outlined.Email, contentDescription = null, tint = colorResource(id = R.color.logo_cyan), modifier = Modifier.size(20.dp))
                        }
                        Spacer(modifier = Modifier.width(12.dp))
                        Text(
                            text = if (userRole == "PATIENT") "Patient Login" else "Doctor Login",
                            color = Color.White,
                            fontSize = 22.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    Spacer(modifier = Modifier.height(24.dp))

                    Text(stringResource(id = R.string.email), color = Color.LightGray, fontSize = 12.sp, fontWeight = FontWeight.Medium)
                    Spacer(modifier = Modifier.height(8.dp))
                    CustomTextField(
                        value = email,
                        onValueChange = { email = it },
                        placeholder = "your.email@example.com"
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    Text(stringResource(id = R.string.password), color = Color.LightGray, fontSize = 12.sp, fontWeight = FontWeight.Medium)
                    Spacer(modifier = Modifier.height(8.dp))
                    CustomTextField(
                        value = password,
                        onValueChange = { password = it },
                        placeholder = "Enter your password",
                        isPassword = true
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Checkbox(
                                checked = isRememberMeChecked,
                                onCheckedChange = { isRememberMeChecked = it },
                                colors = CheckboxDefaults.colors(
                                    checkedColor = colorResource(id = R.color.logo_cyan),
                                    uncheckedColor = Color.Gray,
                                    checkmarkColor = Color.Black
                                )
                            )
                            Text("Remember me", color = Color.Gray, fontSize = 12.sp)
                        }
                        Text(
                            "Forgot password?",
                            color = colorResource(id = R.color.logo_cyan),
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.clickable { }
                        )
                    }

                    Spacer(modifier = Modifier.height(24.dp))

                    if (isLoading) {
                        Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
                            CircularProgressIndicator(color = colorResource(id = R.color.logo_cyan))
                        }
                    } else {
                        Button(
                            onClick = { 
                                if (email.isNotBlank() && password.isNotBlank()) {
                                    isLoading = true
                                    auth.signInWithEmailAndPassword(email.trim(), password.trim())
                                        .addOnCompleteListener { task ->
                                            if (task.isSuccessful) {
                                                Log.d("LoginScreen", "Login successful")
                                                Toast.makeText(context, "Login successful!", Toast.LENGTH_SHORT).show()
                                                if (userRole == "PATIENT") {
                                                    navController.navigate(NavRoutes.Dashboard) { popUpTo(NavRoutes.Login) { inclusive = true } }
                                                } else {
                                                    navController.navigate(NavRoutes.DoctorDashboard) { popUpTo(NavRoutes.Login) { inclusive = true } }
                                                }
                                            } else {
                                                Log.e("LoginScreen", "Login failed", task.exception)
                                                Toast.makeText(context, "Login failed: ${task.exception?.message}", Toast.LENGTH_LONG).show()
                                            }
                                            isLoading = false
                                        }
                                } else {
                                    Toast.makeText(context, "Please fill all fields.", Toast.LENGTH_SHORT).show()
                                }
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(50.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = Color.Transparent),
                            contentPadding = PaddingValues(),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Box(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .background(
                                        brush = Brush.horizontalGradient(
                                            colors = listOf(
                                                colorResource(id = R.color.btn_email_start),
                                                colorResource(id = R.color.btn_email_end)
                                            )
                                        )
                                    ),
                                contentAlignment = Alignment.Center
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Text(stringResource(id = R.string.login), color = Color.White, fontSize = 16.sp, fontWeight = FontWeight.Bold)
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Icon(Icons.Default.ArrowForward, contentDescription = null, tint = Color.White, modifier = Modifier.size(16.dp))
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(24.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.Center
                    ) {
                        Text("Don't have an account? ", color = Color.Gray, fontSize = 14.sp)
                        Text(
                            stringResource(id = R.string.register),
                            color = colorResource(id = R.color.logo_cyan),
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.clickable { 
                                if (userRole == "PATIENT") {
                                    navController.navigate(NavRoutes.Register) 
                                } else {
                                    navController.navigate(NavRoutes.DoctorRegister)
                                }
                            }
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.weight(1f))

            Text(
                text = "Protected by 256-bit encryption • Your data is secure",
                color = colorResource(id = R.color.text_grey).copy(alpha = 0.5f),
                fontSize = 10.sp,
                textAlign = TextAlign.Center
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CustomTextField(
    value: String,
    onValueChange: (String) -> Unit,
    placeholder: String,
    isPassword: Boolean = false
) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        placeholder = { Text(placeholder, color = Color.Gray) },
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        visualTransformation = if (isPassword) PasswordVisualTransformation() else androidx.compose.ui.text.input.VisualTransformation.None,
        keyboardOptions = if (isPassword) KeyboardOptions(keyboardType = KeyboardType.Password) else KeyboardOptions(keyboardType = KeyboardType.Email),
        colors = TextFieldDefaults.outlinedTextFieldColors(
            containerColor = Color(0xFF1E293B),
            focusedBorderColor = colorResource(id = R.color.logo_cyan),
            unfocusedBorderColor = Color(0xFF334155),
            focusedTextColor = Color.White,
            unfocusedTextColor = Color.White,
            cursorColor = colorResource(id = R.color.logo_cyan)
        ),
        singleLine = true
    )
}

@Composable
fun GridBackgroundLogin() {
    Canvas(modifier = Modifier.fillMaxSize()) {
        val gridSize = 50.dp.toPx()
        val width = size.width
        val height = size.height
        
        for (x in 0..((width / gridSize).toInt())) {
            drawLine(
                color = Color.White.copy(alpha = 0.03f),
                start = Offset(x * gridSize, 0f),
                end = Offset(x * gridSize, height),
                strokeWidth = 1f
            )
        }
        
        for (y in 0..((height / gridSize).toInt())) {
            drawLine(
                color = Color.White.copy(alpha = 0.03f),
                start = Offset(0f, y * gridSize),
                end = Offset(width, y * gridSize),
                strokeWidth = 1f
            )
        }
    }
}