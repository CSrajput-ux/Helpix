package com.healthai.app.ui.screens.fitness

import android.Manifest
import android.content.pm.PackageManager
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import androidx.navigation.NavController
import com.healthai.app.R

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FitnessTrackerScreen(navController: NavController) {

    val context = LocalContext.current
    var hasPermission by remember {
        mutableStateOf(ContextCompat.checkSelfPermission(context, Manifest.permission.ACTIVITY_RECOGNITION) == PackageManager.PERMISSION_GRANTED)
    }
    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission(),
        onResult = { granted ->
            hasPermission = granted
        }
    )

    var selectedCategory by remember { mutableStateOf("General") }
    val categories = listOf("General", "Gym", "Professional", "Student", "Senior")

    LaunchedEffect(key1 = true) {
        if (!hasPermission) {
            launcher.launch(Manifest.permission.ACTIVITY_RECOGNITION)
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Fitness Tracker", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color(0xFF0F172A),
                    titleContentColor = Color.White,
                    navigationIconContentColor = Color.White
                )
            )
        },
        containerColor = Color(0xFF0F172A)
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .verticalScroll(rememberScrollState())
        ) {
            // Category Selector
            LazyRow(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 16.dp),
                contentPadding = PaddingValues(horizontal = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(categories) { category ->
                    FilterChip(
                        selected = selectedCategory == category,
                        onClick = { selectedCategory = category },
                        label = { Text(category) },
                        colors = FilterChipDefaults.filterChipColors(
                            containerColor = Color(0xFF1E293B),
                            labelColor = Color.Gray,
                            selectedContainerColor = colorResource(id = R.color.logo_cyan),
                            selectedLabelColor = Color.Black
                        ),
                        border = null,
                        shape = RoundedCornerShape(20.dp)
                    )
                }
            }

            if (hasPermission) {
                // Progress Overview
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    contentAlignment = Alignment.Center
                ) {
                    MainProgressRing(
                        steps = 7450f,
                        goal = 10000f,
                        category = selectedCategory
                    )
                }

                Spacer(modifier = Modifier.height(24.dp))

                // Personalized Stats based on category
                Text(
                    text = "$selectedCategory Focus",
                    color = Color.White,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(horizontal = 16.dp)
                )

                Spacer(modifier = Modifier.height(16.dp))

                CategoryStats(selectedCategory)

                Spacer(modifier = Modifier.height(24.dp))

                // Daily Activity
                Text(
                    text = "Today's Activity",
                    color = Color.White,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(horizontal = 16.dp)
                )

                Spacer(modifier = Modifier.height(12.dp))

                ActivityCard(
                    title = "Morning Walk",
                    subtitle = "3.2 km • 35 mins",
                    icon = Icons.Default.DirectionsWalk,
                    color = Color(0xFF00E5FF)
                )
                ActivityCard(
                    title = "Calories Burned",
                    subtitle = "420 / 600 kcal",
                    icon = Icons.Default.Whatshot,
                    color = Color(0xFFFF5252)
                )
                if (selectedCategory == "Gym") {
                    ActivityCard(
                        title = "Strength Training",
                        subtitle = "45 mins • 4 sets",
                        icon = Icons.Default.FitnessCenter,
                        color = Color(0xFF76FF03)
                    )
                }
                if (selectedCategory == "Professional" || selectedCategory == "Student") {
                    ActivityCard(
                        title = "Posture Alert",
                        subtitle = "Good Posture for 4h",
                        icon = Icons.Default.AccessibilityNew,
                        color = Color(0xFFFFD600)
                    )
                }

                Spacer(modifier = Modifier.height(32.dp))

            } else {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(32.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(
                            Icons.Default.Lock,
                            contentDescription = null,
                            tint = Color.Gray,
                            modifier = Modifier.size(64.dp)
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        Text(
                            text = "Activity permission is required to track your steps and personalized fitness data.",
                            color = Color.White,
                            textAlign = TextAlign.Center,
                            fontSize = 16.sp
                        )
                        Spacer(modifier = Modifier.height(24.dp))
                        Button(
                            onClick = { launcher.launch(Manifest.permission.ACTIVITY_RECOGNITION) },
                            colors = ButtonDefaults.buttonColors(containerColor = colorResource(id = R.color.logo_cyan))
                        ) {
                            Text("Grant Permission", color = Color.Black)
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun MainProgressRing(steps: Float, goal: Float, category: String) {
    val progress = steps / goal
    val animatedProgress by animateFloatAsState(targetValue = progress, animationSpec = tween(1500))

    Box(contentAlignment = Alignment.Center, modifier = Modifier.size(240.dp)) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            // Background track
            drawArc(
                color = Color(0xFF1E293B),
                startAngle = -220f,
                sweepAngle = 260f,
                useCenter = false,
                style = Stroke(width = 24f, cap = StrokeCap.Round)
            )
            // Progress track
            drawArc(
                brush = Brush.sweepGradient(
                    colors = listOf(Color(0xFF00E5FF), Color(0xFF76FF03), Color(0xFF00E5FF)),
                    center = center
                ),
                startAngle = -220f,
                sweepAngle = 260f * animatedProgress,
                useCenter = false,
                style = Stroke(width = 24f, cap = StrokeCap.Round)
            )
        }

        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Icon(
                imageVector = when(category) {
                    "Gym" -> Icons.Default.FitnessCenter
                    "Professional" -> Icons.Default.Work
                    "Student" -> Icons.Default.School
                    "Senior" -> Icons.Default.Elderly
                    else -> Icons.Default.DirectionsRun
                },
                contentDescription = null,
                tint = colorResource(id = R.color.logo_cyan),
                modifier = Modifier.size(32.dp)
            )
            Text(
                text = steps.toInt().toString(),
                color = Color.White,
                fontSize = 48.sp,
                fontWeight = FontWeight.ExtraBold
            )
            Text(
                text = "Steps / $category",
                color = Color.Gray,
                fontSize = 14.sp
            )
        }
    }
}

@Composable
fun CategoryStats(category: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        when (category) {
            "Gym" -> {
                StatCard("Workout", "4/5 Days", Icons.Default.Timer, Color(0xFF76FF03), Modifier.weight(1f))
                StatCard("Protein", "120g", Icons.Default.Restaurant, Color(0xFFFF9100), Modifier.weight(1f))
            }
            "Professional" -> {
                StatCard("Stand Time", "8/10h", Icons.Default.Accessibility, Color(0xFF00E5FF), Modifier.weight(1f))
                StatCard("Stress", "Low", Icons.Default.SelfImprovement, Color(0xFFB388FF), Modifier.weight(1f))
            }
            "Student" -> {
                StatCard("Focus", "4h 20m", Icons.Default.Psychology, Color(0xFFFFD600), Modifier.weight(1f))
                StatCard("Sleep", "7.5h", Icons.Default.Bedtime, Color(0xFF8C9EFF), Modifier.weight(1f))
            }
            "Senior" -> {
                StatCard("Mobility", "85%", Icons.Default.DirectionsWalk, Color(0xFF76FF03), Modifier.weight(1f))
                StatCard("Hydration", "1.5L", Icons.Default.WaterDrop, Color(0xFF00E5FF), Modifier.weight(1f))
            }
            else -> {
                StatCard("Distance", "5.2 km", Icons.Default.Map, Color(0xFF00E5FF), Modifier.weight(1f))
                StatCard("Calories", "320 kcal", Icons.Default.Whatshot, Color(0xFFFF5252), Modifier.weight(1f))
            }
        }
    }
}

@Composable
fun StatCard(label: String, value: String, icon: ImageVector, color: Color, modifier: Modifier = Modifier) {
    Card(
        modifier = modifier,
        colors = CardDefaults.cardColors(containerColor = Color(0xFF1E293B)),
        shape = RoundedCornerShape(16.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Icon(icon, contentDescription = null, tint = color, modifier = Modifier.size(20.dp))
            Spacer(modifier = Modifier.height(12.dp))
            Text(value, color = Color.White, fontSize = 20.sp, fontWeight = FontWeight.Bold)
            Text(label, color = Color.Gray, fontSize = 12.sp)
        }
    }
}

@Composable
fun ActivityCard(title: String, subtitle: String, icon: ImageVector, color: Color) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 6.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFF1E293B)),
        shape = RoundedCornerShape(16.dp)
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(CircleShape)
                    .background(color.copy(alpha = 0.1f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(icon, contentDescription = null, tint = color, modifier = Modifier.size(24.dp))
            }
            Spacer(modifier = Modifier.width(16.dp))
            Column {
                Text(title, color = Color.White, fontSize = 16.sp, fontWeight = FontWeight.Bold)
                Text(subtitle, color = Color.Gray, fontSize = 12.sp)
            }
            Spacer(modifier = Modifier.weight(1f))
            Icon(Icons.Default.ChevronRight, contentDescription = null, tint = Color.DarkGray)
        }
    }
}
