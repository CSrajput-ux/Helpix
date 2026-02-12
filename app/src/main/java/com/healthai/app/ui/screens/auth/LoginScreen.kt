package com.healthai.app.ui.screens.auth

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.outlined.Email
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.colorResource
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
import com.healthai.app.R

@Composable
fun LoginScreen(navController: NavController) {
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var isRememberMeChecked by remember { mutableStateOf(false) }

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
        // Reuse Grid Background for consistency
        GridBackgroundLogin()

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            // --- LOGO SECTION (Exact same as Welcome Screen) ---
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
                        imageVector = Icons.Default.Lock, // Using Lock as Shield substitute
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
                text = "✨ AI-Powered Healthcare Platform",
                color = colorResource(id = R.color.text_grey),
                fontSize = 12.sp
            )

            Spacer(modifier = Modifier.height(32.dp))

            // --- MAIN LOGIN CARD ---
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
                    // Back Button
                    Text(
                        text = "← Back to options",
                        color = colorResource(id = R.color.logo_cyan),
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Medium,
                        modifier = Modifier.clickable { navController.popBackStack() }
                    )

                    Spacer(modifier = Modifier.height(24.dp))

                    // Title Row
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
                            text = "Email Login",
                            color = Color.White,
                            fontSize = 22.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    Spacer(modifier = Modifier.height(24.dp))

                    // Email Input
                    Text("Email Address", color = Color.LightGray, fontSize = 12.sp, fontWeight = FontWeight.Medium)
                    Spacer(modifier = Modifier.height(8.dp))
                    CustomTextField(
                        value = email,
                        onValueChange = { email = it },
                        placeholder = "your.email@example.com"
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    // Password Input
                    Text("Password", color = Color.LightGray, fontSize = 12.sp, fontWeight = FontWeight.Medium)
                    Spacer(modifier = Modifier.height(8.dp))
                    CustomTextField(
                        value = password,
                        onValueChange = { password = it },
                        placeholder = "Enter your password",
                        isPassword = true
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    // Remember Me & Forgot Password
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

                    // Login Button (Blue Gradient)
                    Button(
                        onClick = { navController.navigate("dashboard_screen") },
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
                                Text("Login", color = Color.White, fontSize = 16.sp, fontWeight = FontWeight.Bold)
                                Spacer(modifier = Modifier.width(8.dp))
                                Icon(Icons.Default.ArrowForward, contentDescription = null, tint = Color.White, modifier = Modifier.size(16.dp))
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(24.dp))

                    // Footer Sign Up
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.Center
                    ) {
                        Text("Don't have an account? ", color = Color.Gray, fontSize = 14.sp)
                        Text(
                            "Sign up",
                            color = colorResource(id = R.color.logo_cyan),
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.clickable { navController.navigate("register_screen") }
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.weight(1f))

            // Bottom Encryption Text
            Text(
                text = "Protected by 256-bit encryption • Your data is secure",
                color = colorResource(id = R.color.text_grey).copy(alpha = 0.5f),
                fontSize = 10.sp,
                textAlign = TextAlign.Center
            )
        }
    }
}

// --- Custom TextField Component to match the design ---
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
            containerColor = Color(0xFF1E293B), // Dark blue-grey background
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