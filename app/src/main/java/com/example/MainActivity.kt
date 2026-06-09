package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.clickable
import androidx.compose.foundation.border
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.compose.animation.core.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.ui.screens.*
import com.example.ui.theme.MyApplicationTheme
import com.example.ui.viewmodel.AppTab
import com.example.ui.viewmodel.SurakkhaViewModel

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MyApplicationTheme {
                SurakkhaApp()
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SurakkhaApp() {
    val viewModel: SurakkhaViewModel = viewModel()
    
    val currentTab by viewModel.currentTab.collectAsState()
    val isBangla by viewModel.isBangla.collectAsState()
    
    val reports by viewModel.reports.collectAsState()
    val checklistItems by viewModel.checklistItems.collectAsState()
    val documents by viewModel.simplifiedDocuments.collectAsState()
    val analytics by viewModel.analytics.collectAsState()
    val chatMessages by viewModel.chatMessages.collectAsState()

    val isSosActive by viewModel.isSosActive.collectAsState()
    val sosCountdown by viewModel.sosCountdown.collectAsState()
    val sosStatus by viewModel.sosStatus.collectAsState()

    // Immersive, stunning global SOS Broadcast Dialog
    if (isSosActive) {
        Dialog(
            onDismissRequest = { },
            properties = DialogProperties(
                dismissOnBackPress = false,
                dismissOnClickOutside = false,
                usePlatformDefaultWidth = false
            )
        ) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color(0xFF0D0D0D).copy(alpha = 0.97f))
                    .padding(24.dp),
                contentAlignment = Alignment.Center
            ) {
                val infiniteRadar = rememberInfiniteTransition(label = "radar")
                val radarScale by infiniteRadar.animateFloat(
                    initialValue = 0.8f,
                    targetValue = 2.4f,
                    animationSpec = infiniteRepeatable(
                        animation = tween(1800, easing = LinearEasing),
                        repeatMode = RepeatMode.Restart
                    ),
                    label = "radar"
                )
                val radarOpacity by infiniteRadar.animateFloat(
                    initialValue = 0.6f,
                    targetValue = 0f,
                    animationSpec = infiniteRepeatable(
                        animation = tween(1800, easing = LinearEasing),
                        repeatMode = RepeatMode.Restart
                    ),
                    label = "radar"
                )

                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(24.dp),
                    modifier = Modifier.fillMaxWidth().align(Alignment.Center)
                ) {
                    // Pulsing Radar Sphere in the center
                    Box(
                        contentAlignment = Alignment.Center,
                        modifier = Modifier.size(160.dp)
                    ) {
                        // Pulsing Ring
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .scale(radarScale)
                                .clip(CircleShape)
                                .background(MaterialTheme.colorScheme.error.copy(alpha = radarOpacity))
                        )
                        // Outer Static ring
                        Box(
                            modifier = Modifier
                                .size(110.dp)
                                .border(2.dp, MaterialTheme.colorScheme.error, CircleShape)
                        )
                        // Central emergency button
                        Box(
                            modifier = Modifier
                                .size(90.dp)
                                .clip(CircleShape)
                                .background(MaterialTheme.colorScheme.error),
                            contentAlignment = Alignment.Center
                        ) {
                            if (sosCountdown > 0) {
                                Text(
                                    text = "$sosCountdown",
                                    style = MaterialTheme.typography.displayMedium,
                                    fontWeight = FontWeight.ExtraBold,
                                    color = Color.White
                                )
                            } else {
                                Icon(
                                    imageVector = Icons.Default.Campaign,
                                    contentDescription = null,
                                    tint = Color.White,
                                    modifier = Modifier.size(45.dp)
                                )
                            }
                        }
                    }

                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(8.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(
                            text = if (isBangla) "🚨 লাইভ এসওএস জরুরি ব্রডকাস্ট" else "🚨 LIVE SOS EMERGENCY BROADCAST",
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.error,
                            textAlign = TextAlign.Center
                        )
                        Text(
                            text = if (isBangla) 
                                "সুরক্ষা এআই জিপিএস ও ফায়ারবেস ক্লাউড নেটওয়ার্ক" 
                                else "Surakkha AI GPS & Firebase Cloud Network",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            textAlign = TextAlign.Center
                        )
                    }

                    // Progress Status Board
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                        border = BorderStroke(1.dp, MaterialTheme.colorScheme.error.copy(alpha = 0.3f))
                    ) {
                        Column(
                            modifier = Modifier.padding(16.dp),
                            verticalArrangement = Arrangement.spacedBy(8.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                CircularProgressIndicator(
                                    modifier = Modifier.size(16.dp),
                                    strokeWidth = 2.dp,
                                    color = MaterialTheme.colorScheme.error
                                )
                                Text(
                                    text = if (isBangla) "ফায়ারবেস ও স্যাটেলাইট সংযোগ ট্র্যাকিং:" else "Firebase Sync & Satellite Status:",
                                    style = MaterialTheme.typography.labelSmall,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                            Text(
                                text = sosStatus,
                                style = MaterialTheme.typography.bodyMedium,
                                fontWeight = FontWeight.SemiBold,
                                color = MaterialTheme.colorScheme.onSurface,
                                textAlign = TextAlign.Center
                            )
                        }
                    }

                    // Emergency helper dispatch
                    Column(
                        modifier = Modifier.fillMaxWidth(),
                        verticalArrangement = Arrangement.spacedBy(10.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = if (isBangla) "জাতীয় জরুরি সেবা ৯৯৯ এ সরাসরি লিংক সাহায্য" else "Direct Helpline Link Connection to 999 Desk",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Button(
                            onClick = { /* Simulated Call 999 */ },
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFD32F2F)),
                            modifier = Modifier.fillMaxWidth().height(48.dp)
                        ) {
                            Icon(imageVector = Icons.Default.Phone, contentDescription = null)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = if (isBangla) "৯৯৯ জরুরি সেবা ডায়াল করুন" else "Dial 999 Emergency Support",
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    // Cancel Action Button
                    OutlinedButton(
                        onClick = { viewModel.cancelSos() },
                        border = BorderStroke(1.5.dp, MaterialTheme.colorScheme.error),
                        colors = ButtonDefaults.outlinedButtonColors(contentColor = MaterialTheme.colorScheme.error),
                        modifier = Modifier.fillMaxWidth().height(48.dp).testTag("cancel_sos_button")
                    ) {
                        Text(
                            text = if (isBangla) "অ্যালার্ট বাতিল বা বন্ধ করুন" else "CANCEL & DISMISS ALARM",
                            fontWeight = FontWeight.ExtraBold,
                            letterSpacing = 1.sp
                        )
                    }
                }
            }
        }
    }

    Scaffold(
        modifier = Modifier
            .fillMaxSize()
            .testTag("app_scaffold"),
        topBar = {
            TopAppBar(
                title = {
                    Row(
                        modifier = Modifier.fillMaxWidth(0.95F),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(
                                text = if (isBangla) "সুরক্ষা এআই" else "Surakkha AI",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = if (isBangla) "SEDP শো-কেসিং ২০২৬" else "Gov SEDP Showcasing 2026",
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.primary,
                                fontWeight = FontWeight.SemiBold
                            )
                        }

                        // Actions: Language Toggle & Analytics Shortcut
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            // Language Toggle Button
                            TextButton(
                                onClick = { viewModel.toggleLanguage() },
                                colors = ButtonDefaults.textButtonColors(
                                    contentColor = MaterialTheme.colorScheme.primary
                                ),
                                modifier = Modifier.testTag("language_toggle")
                            ) {
                                Text(
                                    text = if (isBangla) "English" else "বাংলা",
                                    fontWeight = FontWeight.ExtraBold,
                                    fontSize = 13.sp
                                )
                            }
                            
                            // Analytics Shortcut icon
                            IconButton(
                                onClick = { viewModel.selectTab(AppTab.ANALYTICS) },
                                modifier = Modifier.testTag("top_bar_analytics_button")
                            ) {
                                Icon(
                                    imageVector = if (currentTab == AppTab.ANALYTICS) Icons.Default.Analytics else Icons.Outlined.Analytics,
                                    contentDescription = "Impact Analytics",
                                    tint = if (currentTab == AppTab.ANALYTICS) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface
                                )
                            }
                        }
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface,
                    titleContentColor = MaterialTheme.colorScheme.onSurface
                )
            )
        },
        bottomBar = {
            NavigationBar(
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("bottom_nav_bar")
                    .windowInsetsPadding(WindowInsets.navigationBars) // Ensure gesture nav padding
            ) {
                // Nav Items: Home, Missing Persons, Disaster Guardian, Documents, Father AI
                val navTabs = listOf(
                    AppTab.HOME,
                    AppTab.MISSING_PERSONS,
                    AppTab.DISASTER_GUARDIAN,
                    AppTab.DOCUMENTS,
                    AppTab.FATHER_AI
                )

                navTabs.forEach { tab ->
                    val isSelected = currentTab == tab
                    val label = if (isBangla) tab.titleBn else tab.titleEn
                    
                    val icon = when (tab) {
                        AppTab.HOME -> if (isSelected) Icons.Filled.Home else Icons.Outlined.Home
                        AppTab.MISSING_PERSONS -> if (isSelected) Icons.Filled.People else Icons.Outlined.People
                        AppTab.DISASTER_GUARDIAN -> if (isSelected) Icons.Filled.Thunderstorm else Icons.Outlined.Thunderstorm
                        AppTab.DOCUMENTS -> if (isSelected) Icons.Filled.DocumentScanner else Icons.Outlined.DocumentScanner
                        AppTab.FATHER_AI -> if (isSelected) Icons.Filled.SupportAgent else Icons.Outlined.SupportAgent
                        else -> Icons.Outlined.Help
                    }

                    NavigationBarItem(
                        selected = isSelected,
                        onClick = { viewModel.selectTab(tab) },
                        icon = { Icon(imageVector = icon, contentDescription = label) },
                        label = { Text(label, fontSize = 9.sp, fontWeight = FontWeight.Bold, maxLines = 1) },
                        modifier = Modifier.testTag("nav_item_${tab.name.lowercase()}"),
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = MaterialTheme.colorScheme.primary,
                            selectedTextColor = MaterialTheme.colorScheme.primary,
                            unselectedIconColor = MaterialTheme.colorScheme.onSurfaceVariant,
                            unselectedTextColor = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    )
                }
            }
        },
        floatingActionButton = {
            val infiniteTransition = rememberInfiniteTransition(label = "pulse")
            val pulseScale by infiniteTransition.animateFloat(
                initialValue = 1f,
                targetValue = 1.15f,
                animationSpec = infiniteRepeatable(
                    animation = tween(1200, easing = FastOutSlowInEasing),
                    repeatMode = RepeatMode.Reverse
                ),
                label = "pulse"
            )

            FloatingActionButton(
                onClick = { viewModel.triggerSos() },
                containerColor = MaterialTheme.colorScheme.error,
                contentColor = Color.White,
                modifier = Modifier
                    .padding(bottom = 12.dp)
                    .scale(pulseScale)
                    .testTag("emergency_fab")
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 14.dp, vertical = 6.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Campaign,
                        contentDescription = "SOS Alert Trigger"
                    )
                    Text(
                        text = "SOS",
                        fontWeight = FontWeight.ExtraBold,
                        fontSize = 14.sp
                    )
                }
            }
        }
    ) { innerPadding ->
        Surface(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .background(MaterialTheme.colorScheme.background)
        ) {
            when (currentTab) {
                AppTab.HOME -> HomeScreen(
                    viewModel = viewModel,
                    reports = reports,
                    isBangla = isBangla,
                    onNavigate = { viewModel.selectTab(it) }
                )
                AppTab.MISSING_PERSONS -> MissingPersonsScreen(
                    viewModel = viewModel,
                    reports = reports,
                    isBangla = isBangla
                )
                AppTab.DISASTER_GUARDIAN -> DisasterGuardianScreen(
                    viewModel = viewModel,
                    checklistItems = checklistItems,
                    isBangla = isBangla
                )
                AppTab.DOCUMENTS -> DocumentsScreen(
                    viewModel = viewModel,
                    docList = documents,
                    isBangla = isBangla
                )
                AppTab.FATHER_AI -> FatherAIScreen(
                    viewModel = viewModel,
                    messages = chatMessages,
                    isBangla = isBangla
                )
                AppTab.ANALYTICS -> AnalyticsDashboardScreen(
                    viewModel = viewModel,
                    analytics = analytics,
                    isBangla = isBangla
                )
            }
        }
    }
}
