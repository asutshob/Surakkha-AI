package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.database.MissingPersonReport
import androidx.compose.foundation.BorderStroke
import com.example.ui.theme.*
import com.example.ui.theme.RedAlert
import com.example.ui.viewmodel.AppTab
import com.example.ui.viewmodel.SurakkhaViewModel

@Composable
fun HomeScreen(
    viewModel: SurakkhaViewModel,
    reports: List<MissingPersonReport>,
    isBangla: Boolean,
    onNavigate: (AppTab) -> Unit,
    modifier: Modifier = Modifier
) {
    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .testTag("home_screen"),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Welcome and Greeting Header
        item {
            HeaderSection(isBangla)
        }

        // Hero Banner with beautiful gradient
        item {
            HeroBanner(isBangla)
        }

        // Live Alerts Segment
        item {
            LiveQuickAlerts(isBangla)
        }

        // Quick Action Cards
        item {
            Text(
                text = if (isBangla) "জরুরি মডিউলসমূহ" else "Core Modules",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onBackground
            )
            Spacer(modifier = Modifier.height(8.dp))
            QuickActionGrid(isBangla, onNavigate)
        }

        // Recent Missing Persons preview
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = if (isBangla) "সাম্প্রতিক নিখোঁজ সন্ধান" else "Recent Missing Tracking",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onBackground
                )
                TextButton(onClick = { onNavigate(AppTab.MISSING_PERSONS) }) {
                    Text(text = if (isBangla) "সবগুলো দেখুন" else "View All")
                }
            }
            Spacer(modifier = Modifier.height(4.dp))
            
            if (reports.isEmpty()) {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(24.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = if (isBangla) "কোনো নিখোঁজ রিপোর্ট পাওয়া যায়নি।" else "No active reports found",
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            } else {
                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    items(reports.take(5)) { report ->
                        RecentPersonCard(report, isBangla) {
                            onNavigate(AppTab.MISSING_PERSONS)
                        }
                    }
                }
            }
        }

        item {
            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}

@Composable
fun HeaderSection(isBangla: Boolean) {
    Column {
        Text(
            text = if (isBangla) "নিরাপদ বাংলাদেশ গড়ার লক্ষ্যে কৃত্রিম বুদ্ধিমত্তা দিয়ে জীবন রক্ষা করা" else "Saving lives with Artificial Intelligence for a safer Bangladesh",
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.primary,
            fontWeight = FontWeight.SemiBold,
            lineHeight = 16.sp
        )
        Spacer(modifier = Modifier.height(2.dp))
        Text(
            text = if (isBangla) "সুরক্ষা এআই (Surakkha AI)" else "Surakkha AI",
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.ExtraBold,
            color = MaterialTheme.colorScheme.onBackground
        )
    }
}

@Composable
fun HeroBanner(isBangla: Boolean) {
    val gradientBrush = Brush.linearGradient(
        colors = listOf(BluePrimary, GreenPrimary)
    )
    Card(
        shape = RoundedCornerShape(16.dp),
        modifier = Modifier
            .fillMaxWidth()
            .height(145.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(gradientBrush)
                .padding(16.dp),
            contentAlignment = Alignment.CenterStart
        ) {
            Column(modifier = Modifier.fillMaxWidth(0.7F)) {
                Text(
                    text = if (isBangla) "নিরাপদ বাংলাদেশ গড়ার লক্ষ্যে" else "Towards a safer Bangladesh",
                    style = MaterialTheme.typography.labelSmall,
                    color = Color.White.copy(alpha = 0.85F),
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = if (isBangla) "কৃত্রিম বুদ্ধিমত্তা দিয়ে জীবন রক্ষা করা আমাদের লক্ষ্য" else "Saving Lives Through Intelligent Technology",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = Color.White,
                    lineHeight = 22.sp
                )
            }
            Icon(
                imageVector = Icons.Default.Shield,
                contentDescription = null,
                tint = Color.White.copy(alpha = 0.15F),
                modifier = Modifier
                    .size(100.dp)
                    .align(Alignment.CenterEnd)
                    .offset(x = 10.dp, y = 10.dp)
            )
        }
    }
}

@Composable
fun LiveQuickAlerts(isBangla: Boolean) {
    Card(
        colors = CardDefaults.cardColors(
            containerColor = RedAlert.copy(alpha = 0.08F)
        ),
        border = CardDefaults.outlinedCardBorder().copy(
            brush = Brush.linearGradient(listOf(RedAlert.copy(alpha = 0.4F), RedAlert.copy(alpha = 0.4F)))
        ),
        shape = RoundedCornerShape(12.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(36.dp)
                    .clip(CircleShape)
                    .background(RedAlert.copy(alpha = 0.15F)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Warning,
                    contentDescription = "Alert",
                    tint = RedAlert,
                    modifier = Modifier.size(20.dp)
                )
            }
            Spacer(modifier = Modifier.width(12.dp))
            Column {
                Text(
                    text = if (isBangla) "জরুরি বার্তা: নোয়াখালী ও সিলেট" else "Urgent Alert: Noakhali & Sylhet",
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Bold,
                    color = RedAlert
                )
                Text(
                    text = if (isBangla) "উজানের ঢলে বন্যার ঝুঁকি বৃদ্ধি পাচ্ছে। স্থানীয় আশ্রয়কেন্দ্র ও সতর্ক সংকেত ১০৯০ ডায়াল করে জানুন।"
                           else "Rising water levels upstream. Check nearest flood shelter, or dial 1090.",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.85F),
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )
            }
        }
    }
}

@Composable
fun QuickActionGrid(isBangla: Boolean, onNavigate: (AppTab) -> Unit) {
    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            QuickCard(
                title = if (isBangla) "নিখোঁজ সনাক্তকরণ" else "Missing Person",
                description = if (isBangla) "ছবি ম্যাচিং ও আঞ্চলিক সতর্কতা" else "Photo Search & Local Alerts",
                icon = Icons.Default.People,
                color = GreenPrimary,
                modifier = Modifier
                    .weight(1F)
                    .testTag("action_missing_person"),
                onClick = { onNavigate(AppTab.MISSING_PERSONS) }
            )
            QuickCard(
                title = if (isBangla) "দুর্যোগ অভিভাবক" else "Disaster Guardian",
                description = if (isBangla) "ঝুঁকি বার্তা ও জরুরি চেকলিস্ট" else "Risk Warnings & Checklists",
                icon = Icons.Default.Thunderstorm,
                color = BluePrimary,
                modifier = Modifier
                    .weight(1F)
                    .testTag("action_disaster_guardian"),
                onClick = { onNavigate(AppTab.DISASTER_GUARDIAN) }
            )
        }
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            QuickCard(
                title = if (isBangla) "দলিল সরলীকরণ" else "Doc Simplifier",
                description = if (isBangla) "সরকারি পরিপত্রের সহজ বাংলা" else "Simplifying complex guidelines",
                icon = Icons.Default.DocumentScanner,
                color = TealAccent,
                modifier = Modifier
                    .weight(1F)
                    .testTag("action_doc_simplifier"),
                onClick = { onNavigate(AppTab.DOCUMENTS) }
            )
            QuickCard(
                title = if (isBangla) "প্রভাব ড্যাশবোর্ড" else "Impact Tracker",
                description = if (isBangla) "রিয়েল টাইম প্রভাব ট্র্যাকিং" else "Analytical metrics overview",
                icon = Icons.Default.Analytics,
                color = AmberWarning,
                modifier = Modifier
                    .weight(1F)
                    .testTag("action_analytics"),
                onClick = { onNavigate(AppTab.ANALYTICS) }
            )
        }
    }
}

@Composable
fun QuickCard(
    title: String,
    description: String,
    icon: ImageVector,
    color: Color,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    Card(
        modifier = modifier
            .height(110.dp)
            .clickable(onClick = onClick),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(12.dp),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Box(
                modifier = Modifier
                    .size(32.dp)
                    .clip(CircleShape)
                    .background(color.copy(alpha = 0.1F)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = color,
                    modifier = Modifier.size(18.dp)
                )
            }
            Column {
                Text(
                    text = title,
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Text(
                    text = description,
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
        }
    }
}

@Composable
fun SuggestionChipsRow(isBangla: Boolean, onChipClick: (String) -> Unit) {
    val suggestions = if (isBangla) {
        listOf(
            "বজ্রপাত হলে করণীয় কী?",
            "নিখোঁজ রিপোর্ট জমা দেওয়ার নিয়ম কী?",
            "উপবৃত্তি পরিপত্র ব্যাখ্যা করো",
            "বন্যা আশ্রয়কেন্দ্রের খোঁজ"
        )
    } else {
        listOf(
            "What to do during lightning?",
            "How to report a missing child?",
            "Explain stipends circular",
            "Find nearest cyclone shelter"
        )
    }

    LazyRow(
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        items(suggestions) { suggestion ->
            SuggestionChip(
                onClick = { onChipClick(suggestion) },
                label = { Text(text = suggestion) },
                colors = SuggestionChipDefaults.suggestionChipColors(
                    containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5F)
                ),
                border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant)
            )
        }
    }
}

@Composable
fun RecentPersonCard(
    report: MissingPersonReport,
    isBangla: Boolean,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .width(160.dp)
            .height(210.dp)
            .clickable(onClick = onClick),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Column(
            modifier = Modifier.fillMaxSize()
        ) {
            // Image Avatar Placeholder container
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(115.dp)
                    .background(MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.4F)),
                contentAlignment = Alignment.Center
            ) {
                AvatarVectorDisplay(
                    photoUrl = report.photoUrl,
                    modifier = Modifier.size(75.dp)
                )
                
                // Status Badge
                StatusBadge(
                    status = report.status,
                    isBangla = isBangla,
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .padding(6.dp)
                )
            }
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(8.dp),
                verticalArrangement = Arrangement.SpaceBetween
            ) {
                Column {
                    Text(
                        text = report.name,
                        style = MaterialTheme.typography.bodySmall,
                        fontWeight = FontWeight.Bold,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                        text = (if (isBangla) "বয়স: " else "Age: ") + report.age,
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Text(
                        text = report.lastSeenLocation,
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
                
                if (report.matchScore > 0) {
                    Text(
                        text = (if (isBangla) "এআই ম্যাচ: " else "AI Match: ") + "%.1f%%".format(report.matchScore),
                        style = MaterialTheme.typography.labelSmall,
                        color = GreenPrimary,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}

@Composable
fun StatusBadge(status: String, isBangla: Boolean, modifier: Modifier = Modifier) {
    val containerColor = when (status) {
        "FOUND" -> GreenPrimary
        "ACTIVE" -> RedAlert
        else -> AmberWarning
    }
    val text = when (status) {
        "FOUND" -> if (isBangla) "সন্ধানপ্রাপ্ত" else "FOUND"
        "ACTIVE" -> if (isBangla) "অনুসন্ধানরত" else "SEARCHING"
        else -> if (isBangla) "প্রক্রিয়াধীন" else "PENDING"
    }
    Surface(
        color = containerColor,
        shape = RoundedCornerShape(4.dp),
        modifier = modifier
    ) {
        Text(
            text = text,
            color = Color.White,
            style = MaterialTheme.typography.labelSmall,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
        )
    }
}

@Composable
fun AvatarVectorDisplay(photoUrl: String, modifier: Modifier = Modifier) {
    val bgColor = when (photoUrl) {
        "avatar_boy_1" -> Color(0xFF1E3A8A)
        "avatar_girl_1" -> Color(0xFF831843)
        "avatar_elderly_man" -> Color(0xFF134E5E)
        "avatar_elderly_woman" -> Color(0xFF4C1D95)
        else -> MaterialTheme.colorScheme.secondary
    }
    
    val icon = when (photoUrl) {
        "avatar_boy_1" -> Icons.Default.Face
        "avatar_girl_1" -> Icons.Default.FaceRetouchingNatural
        "avatar_elderly_man" -> Icons.Default.EmojiPeople
        "avatar_elderly_woman" -> Icons.Default.SupportAgent
        else -> Icons.Default.Person
    }

    Box(
        modifier = modifier
            .clip(CircleShape)
            .background(bgColor),
        contentAlignment = Alignment.Center
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = Color.White,
            modifier = Modifier.fillMaxSize(0.6F)
        )
    }
}
