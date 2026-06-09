package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.database.SimplifiedDocument
import com.example.ui.theme.BluePrimary
import com.example.ui.theme.GreenPrimary
import com.example.ui.theme.TealAccent
import com.example.ui.viewmodel.SurakkhaViewModel
import com.example.ui.viewmodel.UiState

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DocumentsScreen(
    viewModel: SurakkhaViewModel,
    docList: List<SimplifiedDocument>,
    isBangla: Boolean,
    modifier: Modifier = Modifier
) {
    var rawTitle by remember { mutableStateOf("") }
    var rawContent by remember { mutableStateOf("") }
    var activeSavedDoc by remember { mutableStateOf<SimplifiedDocument?>(null) }
    
    val documentUiState by viewModel.documentUiState.collectAsState()

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .testTag("documents_screen"),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Section 1: Intro Header
        item {
            Card(
                colors = CardDefaults.cardColors(
                    containerColor = TealAccent.copy(alpha = 0.08F)
                ),
                shape = RoundedCornerShape(12.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.DocumentScanner,
                            contentDescription = null,
                            tint = TealAccent,
                            modifier = Modifier.size(24.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = if (isBangla) "দলিল ও নীতি পরিপত্র সরলীকরণ (Document Simplifier)" else "Gov Document Simplifier",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = TealAccent
                        )
                    }
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = if (isBangla)
                            "সরকারি নোটিশ, বিজ্ঞপ্তি বা পরিপত্রের জটিল আইনগত ভাষা অত্যন্ত সহজ সাবলীল বাংলায় রূপান্তর করুন। কৃত্রিম বুদ্ধিমত্তা আপনাকে মূল বিষয়, প্রয়োজনীয় পদক্ষেপ ও তারিখ গুছিয়ে প্রদান করবে।"
                            else "Paste any complex gov circular, educational notices or law texts. AI converts legal jargon into extremely simple daily Bengali in 5 structured segments.",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }

        // Section 2: Paste New Documents Card
        item {
            Card(
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = if (isBangla) "নতুন সরকারি পরিপত্র বিশ্লেষণ" else "Simplify New Circular Notice",
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(12.dp))

                    OutlinedTextField(
                        value = rawTitle,
                        onValueChange = { rawTitle = it },
                        label = { Text(if (isBangla) "পরিপত্রের শিরোনাম (যেমন: মাধ্যমিক ষষ্ঠ শ্রেণীর বৃত্তি)" else "Circular Notice Title") },
                        modifier = Modifier.fillMaxWidth().testTag("doc_title_input"),
                        singleLine = true
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    OutlinedTextField(
                        value = rawContent,
                        onValueChange = { rawContent = it },
                        label = { Text(if (isBangla) "পরিপত্রের মূল লেখা এখানে কপি-পেস্ট করুন" else "Paste Notice / Circular content here...") },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(130.dp)
                            .testTag("doc_content_input"),
                        maxLines = 10
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    // Simplified Tips Panel click
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(6.dp))
                            .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5F))
                            .padding(8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.Lightbulb,
                            contentDescription = null,
                            tint = TealAccent,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = if (isBangla) "টিপস: 'অত্র কার্যালয় কর্তৃক' translates to 'আমাদের অফিস থেকে'."
                                   else "Insight: Bureaucratic Jargon translated to clear active local voice.",
                            fontSize = 11.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    Button(
                        onClick = {
                            if (rawTitle.trim().isNotEmpty() && rawContent.trim().isNotEmpty()) {
                                viewModel.simplifyNewDocument(rawTitle, rawContent)
                                rawTitle = ""
                                rawContent = ""
                                activeSavedDoc = null // reset view
                            }
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(48.dp)
                            .testTag("simplify_doc_button"),
                        colors = ButtonDefaults.buttonColors(containerColor = TealAccent)
                    ) {
                        Icon(imageVector = Icons.Default.AutoAwesome, contentDescription = null)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = if (isBangla) "এআই সরলীকরণ শুরু করুন" else "Convert Document Now",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }

        // Section 3: Active Output Rendering State (Selected Saved Doc or Live AI Results)
        val activeDocToShow = activeSavedDoc ?: when (documentUiState) {
            is UiState.Success -> (documentUiState as UiState.Success<SimplifiedDocument>).data
            else -> null
        }

        if (documentUiState is UiState.Loading) {
            item {
                Card(modifier = Modifier.fillMaxWidth()) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(24.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        CircularProgressIndicator(color = TealAccent)
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = if (isBangla) "এআই জটিল অনুশাসনসমূহ বিশ্লেষণ করছে..." else "AI translating legal terms into plain Bengali language...",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }
        }

        if (activeDocToShow != null) {
            item {
                Column {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = if (isBangla) "সরলীকৃত রূপ" else "Simplified AI Results",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onBackground
                        )
                        TextButton(
                            onClick = {
                                viewModel.clearSimplifiedDocState()
                                activeSavedDoc = null
                            }
                        ) {
                            Text(text = if (isBangla) "বন্ধ করুন" else "Close")
                        }
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                    OutputDetailsGrid(activeDocToShow, isBangla)
                }
            }
        }

        // Section 4: History log database cards
        item {
            Text(
                text = if (isBangla) "সংরক্ষিত সরলীকৃত চিঠিসমূহ (অফলাইন)" else "Saved Simplified Offline Notices",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onBackground
            )
            Spacer(modifier = Modifier.height(4.dp))
            
            if (docList.isEmpty()) {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(20.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = if (isBangla) "কোনো সংরক্ষিত পরিপত্র নেই। প্রথম পরিপত্রটি সরলীকরণ করুন!" else "No saved notices. Try simplifying your first document!",
                            fontSize = 11.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            } else {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    docList.forEach { doc ->
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable {
                                    activeSavedDoc = doc
                                    viewModel.clearSimplifiedDocState() // priority to saved
                                },
                            elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
                            colors = CardDefaults.cardColors(
                                containerColor = if (activeDocToShow?.id == doc.id) MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.4F) else MaterialTheme.colorScheme.surface
                            )
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(12.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(
                                    modifier = Modifier.weight(0.8F),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Box(
                                        modifier = Modifier
                                            .size(36.dp)
                                            .clip(CircleShape)
                                            .background(TealAccent.copy(alpha = 0.1F)),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.Task,
                                            contentDescription = null,
                                            tint = TealAccent,
                                            modifier = Modifier.size(18.dp)
                                        )
                                    }
                                    Spacer(modifier = Modifier.width(12.dp))
                                    Column {
                                        Text(
                                            text = doc.originalTitle,
                                            style = MaterialTheme.typography.bodySmall,
                                            fontWeight = FontWeight.Bold,
                                            maxLines = 1,
                                            overflow = TextOverflow.Ellipsis,
                                            color = MaterialTheme.colorScheme.onSurface
                                        )
                                        Text(
                                            text = if (isBangla) "অফলাইন ডাটাবেজে সংরক্ষিত" else "Available fully offline",
                                            style = MaterialTheme.typography.labelSmall,
                                            color = MaterialTheme.colorScheme.onSurfaceVariant
                                        )
                                    }
                                }
                                IconButton(
                                    onClick = { viewModel.deleteDocument(doc) },
                                    modifier = Modifier.size(24.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Delete,
                                        contentDescription = "Delete",
                                        tint = MaterialTheme.colorScheme.error,
                                        modifier = Modifier.size(16.dp)
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }

        item {
            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}

@Composable
fun OutputDetailsGrid(doc: SimplifiedDocument, isBangla: Boolean) {
    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
        
        // Topic Title Highlight
        Card(
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
        ) {
            Column(modifier = Modifier.padding(14.dp).fillMaxWidth()) {
                Text(
                    text = doc.originalTitle,
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = FontWeight.Bold,
                    color = TealAccent
                )
                Spacer(modifier = Modifier.height(6.dp))
                HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f))
                Spacer(modifier = Modifier.height(6.dp))
                
                // Segment 1: Main Topic
                SegmentItem(
                    title = if (isBangla) "১. পরিপত্রের মূল আলোচনার বিষয়:" else "1. Core Summary Topic:",
                    text = doc.mainTopic,
                    icon = Icons.Default.AutoStories,
                    color = TealAccent
                )

                Spacer(modifier = Modifier.height(10.dp))

                // Segment 2: Target Audience
                SegmentItem(
                    title = if (isBangla) "২. পরিপত্রটি মূলত যাদের জন্য প্রযোজ্য:" else "2. Who this impacts (Audience):",
                    text = doc.targetAudience,
                    icon = Icons.Default.Groups,
                    color = BluePrimary
                )

                Spacer(modifier = Modifier.height(10.dp))

                // Segment 3: Actions
                SegmentItem(
                    title = if (isBangla) "৩. আপনার জন্য সরাসরি করণীয় ধাপসমূহ:" else "3. What actions you must take:",
                    text = doc.requiredActions,
                    icon = Icons.Default.FormatListBulleted,
                    color = GreenPrimary
                )

                Spacer(modifier = Modifier.height(10.dp))

                // Segment 4: Dates
                SegmentItem(
                    title = if (isBangla) "৪. গুরুত্বপূর্ণ নির্দিষ্ট তারিখসমূহ:" else "4. Important dates & deadlines:",
                    text = doc.importantDates,
                    icon = Icons.Default.CalendarMonth,
                    color = MaterialTheme.colorScheme.error
                )

                Spacer(modifier = Modifier.height(10.dp))

                // Segment 5: Examples
                SegmentItem(
                    title = if (isBangla) "৫. সহজ বাস্তব জীবনের উদাহরণ:" else "5. Normal practical scenario example:",
                    text = doc.simpleExamples,
                    icon = Icons.Default.TipsAndUpdates,
                    color = MaterialTheme.colorScheme.secondary
                )
            }
        }
    }
}

@Composable
fun SegmentItem(
    title: String,
    text: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    color: Color
) {
    Column {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = color,
                modifier = Modifier.size(16.dp)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = title,
                style = MaterialTheme.typography.bodySmall,
                fontWeight = FontWeight.Bold,
                color = color
            )
        }
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = text,
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurface,
            lineHeight = 18.sp,
            modifier = Modifier.padding(start = 24.dp)
        )
    }
}
