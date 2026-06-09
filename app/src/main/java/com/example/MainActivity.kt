package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
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
            // Floating emergency action button which leads directly to Father AI chat or emergency guidelines
            if (currentTab != AppTab.FATHER_AI) {
                FloatingActionButton(
                    onClick = {
                        viewModel.selectTab(AppTab.FATHER_AI)
                        viewModel.sendChatMessage("আমি একটি বড় বিপদে পড়েছি! আমাকে দ্রুত বাঁচান!")
                    },
                    containerColor = MaterialTheme.colorScheme.error,
                    contentColor = Color.White,
                    modifier = Modifier
                        .padding(bottom = 12.dp)
                        .testTag("emergency_fab")
                ) {
                    Icon(
                        imageVector = Icons.Default.Campaign,
                        contentDescription = "Emergency SOS alarm alert"
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
