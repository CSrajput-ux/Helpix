package com.healthai.app.ui.screens.symptom

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Send
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.healthai.app.ui.navigation.NavRoutes
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

data class ChatMessage(val text: String, val isUser: Boolean)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SymptomChatScreen(navController: NavController) {
    val messages = remember {
        mutableStateListOf(ChatMessage("Namaste! Main aapka AI Health Assistant hoon. Kripya batayein, aapko kaisa mehsoos ho raha hai?", false))
    }
    var input by remember { mutableStateOf(TextFieldValue("")) }
    val listState = rememberLazyListState()
    val coroutineScope = rememberCoroutineScope()

    val onSend = {
        if (input.text.isNotBlank()) {
            val userMessage = input.text
            messages.add(ChatMessage(userMessage, true))
            input = TextFieldValue("")
            
            coroutineScope.launch {
                listState.animateScrollToItem(0)
                // Simulate AI thinking and responding
                delay(1500)
                messages.add(getAIResponse(userMessage))
                listState.animateScrollToItem(0)
            }
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("AI Symptom Doctor") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color(0xFF0B1221), titleContentColor = Color.White, navigationIconContentColor = Color.White)
            )
        },
        containerColor = Color(0xFF0B1221)
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            LazyColumn(
                state = listState,
                modifier = Modifier
                    .weight(1f)
                    .padding(horizontal = 16.dp),
                reverseLayout = true
            ) {
                items(messages.reversed()) { message ->
                    MessageBubble(message)
                }
            }

            // Input bar
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                OutlinedTextField(
                    value = input,
                    onValueChange = { input = it },
                    label = { Text("Type your symptom...") },
                    modifier = Modifier.weight(1f),
                    keyboardOptions = KeyboardOptions(imeAction = ImeAction.Send),
                    keyboardActions = KeyboardActions(onSend = { onSend() }),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = Color(0xFF2979FF),
                        unfocusedBorderColor = Color(0xFF334155),
                        focusedTextColor = Color.White,
                        unfocusedTextColor = Color.White,
                    )
                )
                Spacer(modifier = Modifier.width(8.dp))
                IconButton(onClick = onSend) {
                    Icon(Icons.Default.Send, contentDescription = "Send", tint = Color(0xFF2979FF))
                }
            }
        }
    }
}

// Simulated AI Logic
private fun getAIResponse(userMessage: String): ChatMessage {
    return when {
        userMessage.contains("sirdard", ignoreCase = true) || userMessage.contains("headache", ignoreCase = true) -> {
            ChatMessage("Theek hai. Aapko yeh kab se hai?", false)
        }
        userMessage.contains("bukhar", ignoreCase = true) || userMessage.contains("fever", ignoreCase = true) -> {
            ChatMessage("Samajh gaya. Sirdard ke alawa, kya aapko thakaan ya jukam jaisa koi aur lakshan hai?", false)
        }
        userMessage.contains("pet dard", ignoreCase = true) || userMessage.contains("stomach ache", ignoreCase = true) -> {
            ChatMessage("Aapne aakhri baar kya khaya tha?", false)
        }
        else -> {
            ChatMessage("Theek hai. Iske baare mein thoda aur batayein.", false)
        }
    }
}

@Composable
fun MessageBubble(message: ChatMessage) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        horizontalArrangement = if (message.isUser) Arrangement.End else Arrangement.Start
    ) {
        Box(
            modifier = Modifier
                .clip(RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp, bottomStart = if(message.isUser) 16.dp else 0.dp, bottomEnd = if(message.isUser) 0.dp else 16.dp))
                .background(if (message.isUser) Color(0xFF2979FF) else Color(0xFF1E293B))
                .padding(12.dp)
        ) {
            Text(text = message.text, color = Color.White)
        }
    }
}