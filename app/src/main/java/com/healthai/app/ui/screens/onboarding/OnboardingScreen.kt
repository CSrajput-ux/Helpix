package com.healthai.app.ui.screens.onboarding

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.outlined.Security
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.healthai.app.R

@Composable
fun OnboardingScreen(navController: NavController) {
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
        // 1. Subtle Grid Background Effect
        GridBackground()

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            // 2. Logo Section
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier.size(100.dp)
            ) {
                // Outer Glow
                Box(
                    modifier = Modifier
                        .size(80.dp)
                        .blur(20.dp)
                        .background(colorResource(id = R.color.logo_blue).copy(alpha = 0.5f), CircleShape)
                )
                // Icon Background
                Box(
                    modifier = Modifier
                        .size(80.dp)
                        .background(
                            brush = Brush.linearGradient(
                                colors = listOf(colorResource(id = R.color.logo_cyan), colorResource(id = R.color.logo_blue))
                            ),
                            shape = CircleShape
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Outlined.Security,
                        contentDescription = null,
                        tint = Color.White,
                        modifier = Modifier.size(40.dp)
                    )
                }
            }
            
            Spacer(modifier = Modifier.height(16.dp))

            // HELPiX Text
            Text(
                text = buildAnnotatedString {
                    withStyle(style = SpanStyle(color = Color.White, fontWeight = FontWeight.Bold)) {
                        append("HELP")
                    }
                    withStyle(style = SpanStyle(color = colorResource(id = R.color.logo_cyan), fontWeight = FontWeight.Bold)) {
                        append("iX")
                    }
                },
                fontSize = 32.sp
            )
            
            Spacer(modifier = Modifier.height(8.dp))
            
            Text(
                text = "✨ AI-Powered Healthcare Platform",
                color = colorResource(id = R.color.text_grey),
                fontSize = 14.sp
            )

            Spacer(modifier = Modifier.height(48.dp))

            // 3. The Main Card
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
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = "Welcome Back",
                        color = Color.White,
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Bold
                    )
                    
                    Spacer(modifier = Modifier.height(24.dp))

                    // Email Button
                    GradientButton(
                        text = "Continue with Email",
                        icon = Icons.Default.Email,
                        gradient = Brush.horizontalGradient(
                            colors = listOf(
                                colorResource(id = R.color.btn_email_start),
                                colorResource(id = R.color.btn_email_end)
                            )
                        ),
                        onClick = { navController.navigate("login_screen") }
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    // Phone Button
                    GradientButton(
                        text = "Continue with Phone",
                        icon = Icons.Default.Phone,
                        gradient = Brush.horizontalGradient(
                            colors = listOf(
                                colorResource(id = R.color.btn_phone_start),
                                colorResource(id = R.color.btn_phone_end)
                            )
                        ),
                        // CHANGE THIS LINE:
                        onClick = { navController.navigate("phone_login_screen") } 
                    )

                    Spacer(modifier = Modifier.height(24.dp))

                    Text(
                        text = "Secure • Private • HIPAA Compliant",
                        color = colorResource(id = R.color.text_grey),
                        fontSize = 12.sp
                    )
                }
            }

            Spacer(modifier = Modifier.height(48.dp))
            
            Text(
                text = "Protected by 256-bit encryption • Your data is secure",
                color = colorResource(id = R.color.text_grey).copy(alpha = 0.5f),
                fontSize = 10.sp,
                textAlign = TextAlign.Center
            )
        }
    }
}

@Composable
fun GradientButton(
    text: String,
    icon: ImageVector,
    gradient: Brush,
    onClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(56.dp)
            .clip(RoundedCornerShape(12.dp))
            .background(gradient)
            .clickable { onClick() },
        contentAlignment = Alignment.Center
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = Color.White,
                modifier = Modifier.size(20.dp)
            )
            Spacer(modifier = Modifier.width(12.dp))
            Text(
                text = text,
                color = Color.White,
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold
            )
        }
    }
}

@Composable
fun GridBackground() {
    Canvas(modifier = Modifier.fillMaxSize()) {
        val gridSize = 50.dp.toPx()
        val width = size.width
        val height = size.height
        
        // Vertical Lines
        for (x in 0..((width / gridSize).toInt())) {
            drawLine(
                color = Color.White.copy(alpha = 0.03f),
                start = Offset(x * gridSize, 0f),
                end = Offset(x * gridSize, height),
                strokeWidth = 1f
            )
        }
        
        // Horizontal Lines
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