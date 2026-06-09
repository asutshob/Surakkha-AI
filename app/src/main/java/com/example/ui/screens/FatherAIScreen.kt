package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Send
import androidx.compose.material.icons.filled.SupportAgent
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.GreenPrimary
import com.example.ui.viewmodel.ChatMessage
import com.example.ui.viewmodel.SurakkhaViewModel
import com.example.ui.viewmodel.UiState

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun FatherAIScreen(
    viewModel: SurakkhaViewModel,
    messages: List<ChatMessage>,
    isBangla: Boolean,
    modifier: Modifier = Modifier
) {
    var rawInputText by remember { mutableStateOf("") }
    val chatUiState by viewModel.chatUiState.collectAsState()
    val listState = rememberLazyListState()

    // Auto scroll down when new messages land
    LaunchedEffect(messages.size) {
        if (messages.isNotEmpty()) {
            listState.animateScrollToItem(messages.size - 1)
        }
    }

    val emergencyActions = if (isBangla) {
        listOf(
            Pair("৯৯৯ জরুরি সেবা", "জাতীয় জরুরি সেবা ৯৯৯ এর হটলাইন এবং সাহায্য প্রাপ্তির নিয়ম সম্পর্কে বলুন।"),
            Pair("১০৯০ দুর্যোগ বার্তা", "যোগাযোগের দুর্যোগ প্রস্তুতি এবং ঘূর্ণিঝড়-বন্যা পূর্বাভাস ১০৯০ সেবা সম্পর্কে বলুন।"),
            Pair("৩৩৩ তথ্য সেবা", "সরকারি তথ্য ও সেবাপ্রাপ্তির হটলাইন ৩৩৩ এর মাধ্যমে কী কী সাহায্য পাওয়া যায়?"),
            Pair("১০৯৮ শিশু সহায়তা", "শিশু ও নারী সুরক্ষায় বিশেষ হেল্পলাইন ১০৯৮ কিভাবে ব্যবহার করা যায়?")
        )
    } else {
        listOf(
            Pair("999 Emergency", "Tell me about the National Emergency Hotline 999 services."),
            Pair("1090 Disaster Info", "Tell me about the Disaster Information Helpline 1090."),
            Pair("333 Gov Portal", "Describe the government service helpline 333."),
            Pair("1098 Child Helpline", "Explain how Child Helpline 1098 assists in emergencies.")
        )
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .testTag("father_ai_screen")
    ) {
        // Welcome Central Assistant Banner
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            colors = CardDefaults.cardColors(
                containerColor = GreenPrimary.copy(alpha = 0.08F)
            ),
            shape = RoundedCornerShape(12.dp)
        ) {
            Row(
                modifier = Modifier.padding(12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(44.dp)
                        .clip(CircleShape)
                        .background(GreenPrimary.copy(alpha = 0.15F)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.SupportAgent,
                        contentDescription = "Surakkha Assistant",
                        tint = GreenPrimary,
                        modifier = Modifier.size(24.dp)
                    )
                }
                Spacer(modifier = Modifier.width(12.dp))
                Column {
                    Text(
                        text = if (isBangla) "সুরক্ষা সহায়ক (জরুরি সাহায্যকারী)" else "Surakkha Assistant (Emergency Guide)",
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.Bold,
                        color = GreenPrimary
                    )
                    Text(
                        text = if (isBangla) "আপনার সার্বক্ষণিক এআই শুভাকাঙ্ক্ষী সহচর • সচল" else "Your 24/7 Caring AI Companion • Online",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }

        // Chat Conversation Stream
        LazyColumn(
            state = listState,
            modifier = Modifier
                .weight(1F)
                .fillMaxWidth()
                .padding(horizontal = 12.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            items(messages) { message ->
                ChatBubbleLayout(message)
            }

            // Typing Animated Indicator showing AI load status
            if (chatUiState is UiState.Loading) {
                item {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.padding(8.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(28.dp)
                                .clip(CircleShape)
                                .background(GreenPrimary.copy(alpha = 0.12F)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.SupportAgent,
                                contentDescription = null,
                                tint = GreenPrimary,
                                modifier = Modifier.size(16.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(8.dp))
                        Card(
                            colors = CardDefaults.cardColors(
                                containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.6F)
                            ),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Text(
                                text = if (isBangla) "সুরক্ষা সহায়ক উত্তর তৈরি করছেন..." else "Surakkha Assistant is typing protective guide...",
                                fontSize = 11.sp,
                                modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }
            }
        }

        // Quick action buttons for emergency/safety - Replace suggestion chips
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 12.dp, vertical = 6.dp)
        ) {
            Text(
                text = if (isBangla) "জরুরি সাহায্য হটলাইন ও পরামর্শ" else "Emergency Helplines & Support",
                style = MaterialTheme.typography.labelSmall,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary,
                modifier = Modifier.padding(bottom = 6.dp)
            )
            
            FlowRow(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(6.dp),
                verticalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                emergencyActions.forEach { action ->
                    SuggestionChip(
                        onClick = { viewModel.sendChatMessage(action.second) },
                        label = { Text(action.first, fontSize = 10.sp, fontWeight = FontWeight.Bold) },
                        colors = SuggestionChipDefaults.suggestionChipColors(
                            containerColor = MaterialTheme.colorScheme.errorContainer.copy(alpha = 0.2f),
                            labelColor = MaterialTheme.colorScheme.error
                        )
                    )
                }
            }
        }

        // Text Input Bar at bottom
        Surface(
            tonalElevation = 2.dp,
            modifier = Modifier.fillMaxWidth()
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                OutlinedTextField(
                    value = rawInputText,
                    onValueChange = { rawInputText = it },
                    placeholder = { Text(if (isBangla) "যেকোনো সাহায্য চেয়ে বাংলায় লিখুন..." else "Ask Surakkha Assistant any query...") },
                    modifier = Modifier
                        .weight(1F)
                        .height(52.dp)
                        .testTag("chat_input_field"),
                    singleLine = true,
                    shape = RoundedCornerShape(24.dp)
                )
                
                IconButton(
                    onClick = {
                        if (rawInputText.trim().isNotEmpty()) {
                            viewModel.sendChatMessage(rawInputText.trim())
                            rawInputText = ""
                        }
                    },
                    modifier = Modifier
                        .size(46.dp)
                        .clip(CircleShape)
                        .background(GreenPrimary)
                        .testTag("chat_send_button"),
                    colors = IconButtonDefaults.iconButtonColors(contentColor = Color.White)
                ) {
                    Icon(
                        imageVector = Icons.Default.Send,
                        contentDescription = "Send",
                        modifier = Modifier.size(18.dp)
                    )
                }
            }
        }
    }
}

@Composable
fun ChatBubbleLayout(message: ChatMessage) {
    val isUser = message.isUser
    val alignment = if (isUser) Alignment.End else Alignment.Start
    val containerBg = if (isUser) {
        MaterialTheme.colorScheme.primary
    } else {
        MaterialTheme.colorScheme.surfaceVariant
    }
    val contentColor = if (isUser) {
        MaterialTheme.colorScheme.onPrimary
    } else {
        MaterialTheme.colorScheme.onSurfaceVariant
    }

    val bubbleShape = if (isUser) {
        RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp, bottomStart = 16.dp, bottomEnd = 2.dp)
    } else {
        RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp, bottomStart = 2.dp, bottomEnd = 16.dp)
    }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 2.dp),
        horizontalAlignment = alignment
    ) {
        Row(
            verticalAlignment = Alignment.Top,
            horizontalArrangement = if (isUser) Arrangement.End else Arrangement.Start,
            modifier = Modifier.fillMaxWidth(0.9F)
        ) {
            if (!isUser) {
                Box(
                    modifier = Modifier
                        .size(28.dp)
                        .clip(CircleShape)
                        .background(GreenPrimary.copy(alpha = 0.12F))
                        .offset(y = 2.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.SupportAgent,
                        contentDescription = null,
                        tint = GreenPrimary,
                        modifier = Modifier.size(16.dp)
                    )
                }
                Spacer(modifier = Modifier.width(6.dp))
            }
            
            Surface(
                color = containerBg,
                contentColor = contentColor,
                shape = bubbleShape,
                tonalElevation = 1.dp
            ) {
                Text(
                    text = message.text,
                    style = MaterialTheme.typography.bodySmall,
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                    lineHeight = 18.sp
                )
            }
        }
    }
}
