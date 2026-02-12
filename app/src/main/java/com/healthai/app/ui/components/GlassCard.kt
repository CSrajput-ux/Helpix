package com.healthai.app.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.healthai.app.R

@Composable
fun GlassCard(
    title: String,
    subtitle: String,
    iconColor: Color,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    // Gradient border brush (top-left shiny, bottom-right dull)
    val borderBrush = Brush.linearGradient(
        colors = listOf(
            colorResource(id = R.color.glass_border_shiny),
            colorResource(id = R.color.glass_border_dull)
        )
    )

    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(150.dp) // Slightly taller to match screenshot
            .clip(RoundedCornerShape(28.dp)) // More rounded corners
            .background(colorResource(id = R.color.glass_surface_thin))
            .border(
                width = 1.dp,
                brush = borderBrush,
                shape = RoundedCornerShape(28.dp)
            )
            .clickable { onClick() }
            .padding(18.dp)
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            // Icon Placeholder with glassy background
            Box(
                modifier = Modifier
                    .size(42.dp)
                    .clip(RoundedCornerShape(14.dp))
                    .background(iconColor.copy(alpha = 0.15f)),
                contentAlignment = Alignment.Center
            ) {
                // Future Icon here
                Box(
                    modifier = Modifier
                        .size(18.dp)
                        .background(iconColor, shape = RoundedCornerShape(5.dp))
                )
            }

            Column {
                Text(
                    text = title,
                    color = Color.White,
                    fontWeight = FontWeight.Bold,
                    fontSize = 17.sp,
                    lineHeight = 22.sp
                )
                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    text = subtitle,
                    color = colorResource(id = R.color.text_secondary),
                    fontSize = 13.sp
                )
            }
        }
    }
}