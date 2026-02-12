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
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.outlined.Lock
import androidx.compose.material.icons.outlined.Phone
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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.healthai.app.R

@Composable
fun PhoneLoginScreen(navController: NavController) {
    var phoneNumber by remember { mutableStateOf("") }

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
        // Reuse Grid Background
        GridBackgroundPhone()

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            // --- LOGO SECTION ---
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
                        imageVector = Icons.Outlined.Lock, 
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

            // --- MAIN PHONE CARD ---
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
                    // Back Link
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
                            Icon(Icons.Outlined.Phone, contentDescription = null, tint = colorResource(id = R.color.logo_cyan), modifier = Modifier.size(20.dp))
                        }
                        Spacer(modifier = Modifier.width(12.dp))
                        Text(
                            text = "Phone Login",
                            color = Color.White,
                            fontSize = 22.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    Spacer(modifier = Modifier.height(24.dp))

                    // Phone Input (Using the renamed function)
                    Text("Phone Number", color = Color.LightGray, fontSize = 12.sp, fontWeight = FontWeight.Medium)
                    Spacer(modifier = Modifier.height(8.dp))
                    
                    PhoneTextField(
                        value = phoneNumber,
                        onValueChange = { phoneNumber = it },
                        placeholder = "+1 (555) 000-0000"
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    // Info Box (Blue Tint)
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(Color(0xFF0F2942), RoundedCornerShape(8.dp)) // Dark Blue tint
                            .border(1.dp, Color(0xFF1E3A5F), RoundedCornerShape(8.dp))
                            .padding(12.dp)
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Outlined.Lock, contentDescription = null, tint = colorResource(id = R.color.text_grey), modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "We'll send a 6-digit verification code to your phone number",
                                color = Color.LightGray,
                                fontSize = 12.sp,
                                lineHeight = 16.sp
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(24.dp))

                    // Send OTP Button
                    Button(
                        onClick = { /* Handle OTP Logic */ },
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
                                            colorResource(id = R.color.btn_phone_start),
                                            colorResource(id = R.color.btn_phone_end)
                                        )
                                    )
                                ),
                            contentAlignment = Alignment.Center
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text("Send OTP", color = Color.White, fontSize = 16.sp, fontWeight = FontWeight.Bold)
                                Spacer(modifier = Modifier.width(8.dp))
                                Icon(Icons.AutoMirrored.Filled.ArrowForward, contentDescription = null, tint = Color.White, modifier = Modifier.size(16.dp))
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.weight(1f))

            // Footer
            Text(
                text = "Protected by 256-bit encryption • Your data is secure",
                color = colorResource(id = R.color.text_grey).copy(alpha = 0.5f),
                fontSize = 10.sp,
                textAlign = TextAlign.Center
            )
        }
    }
}

// --- RENAMED TO PhoneTextField TO FIX CONFLICT ---
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PhoneTextField(
    value: String,
    onValueChange: (String) -> Unit,
    placeholder: String
) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        placeholder = { Text(placeholder, color = Color.Gray) },
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
        colors = OutlinedTextFieldDefaults.colors(
            unfocusedContainerColor = Color(0xFF1E293B),
            focusedContainerColor = Color(0xFF1E293B),
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
fun GridBackgroundPhone() {
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