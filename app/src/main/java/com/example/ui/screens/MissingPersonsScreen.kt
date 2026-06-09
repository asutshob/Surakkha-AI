package com.example.ui.screens

import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.database.MissingPersonReport
import com.example.ui.theme.AmberWarning
import com.example.ui.theme.BluePrimary
import com.example.ui.theme.GreenPrimary
import com.example.ui.theme.RedAlert
import com.example.ui.viewmodel.SurakkhaViewModel
import com.example.ui.viewmodel.UiState

@Composable
fun MissingPersonsScreen(
    viewModel: SurakkhaViewModel,
    reports: List<MissingPersonReport>,
    isBangla: Boolean,
    modifier: Modifier = Modifier
) {
    var showForm by remember { mutableStateOf(false) }
    var selectedSubTab by remember { mutableStateOf(0) } // 0 = Active Searches, 1 = Found
    
    val faceMatchState by viewModel.faceMatchState.collectAsState()

    Box(modifier = modifier.fillMaxSize().testTag("missing_persons_screen")) {
        Column(modifier = Modifier.fillMaxSize()) {
            
            // Stats & Intro Header
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.3F))
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = if (isBangla) "এআই নিখোঁজ ব্যক্তি নেটওয়ার্ক" else "AI Missing Persons Network",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = if (isBangla) 
                            "এখানে নিখোঁজ ব্যক্তিদের রিপোর্ট জমা দেওয়া যায়। কৃত্তিম বুদ্ধিমত্তা দিয়ে আমাদের সিস্টেম সিসিটিভি ক্যামেরা ও স্বেচ্ছাসেবী ফিড স্ক্যান করে স্বয়ংক্রিয় ফেস ম্যাচিং ও উদ্ধার অভিযান পরিচালনা করে।" 
                            else "Report cases here. Our AI engine scans CCTV feeds and volunteer uploads, performing active face matching to locate lost citizens.",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        lineHeight = 16.sp
                    )
                }
            }

            // Tabs Bar: Active / Found
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Button(
                    onClick = { selectedSubTab = 0 },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (selectedSubTab == 0) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surfaceVariant,
                        contentColor = if (selectedSubTab == 0) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurfaceVariant
                    ),
                    modifier = Modifier.weight(1F).height(40.dp)
                ) {
                    Text(
                        text = if (isBangla) "অনুসন্ধানরত (${reports.count { it.status == "ACTIVE" }})" 
                               else "Active Searching (${reports.count { it.status == "ACTIVE" }})",
                        style = MaterialTheme.typography.labelMedium,
                        fontWeight = FontWeight.Bold
                    )
                }
                Button(
                    onClick = { selectedSubTab = 1 },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (selectedSubTab == 1) MaterialTheme.colorScheme.secondary else MaterialTheme.colorScheme.surfaceVariant,
                        contentColor = if (selectedSubTab == 1) MaterialTheme.colorScheme.onSecondary else MaterialTheme.colorScheme.onSurfaceVariant
                    ),
                    modifier = Modifier.weight(1F).height(40.dp)
                ) {
                    Text(
                        text = if (isBangla) "সন্ধানপ্রাপ্ত (${reports.count { it.status == "FOUND" }})" 
                               else "Found (${reports.count { it.status == "FOUND" }})",
                        style = MaterialTheme.typography.labelMedium,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Reports List representation based on sub tab selection
            val filteredReports = if (selectedSubTab == 0) {
                reports.filter { it.status == "ACTIVE" }
            } else {
                reports.filter { it.status == "FOUND" }
            }

            if (filteredReports.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1F),
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier.padding(32.dp)
                    ) {
                        Icon(
                            imageVector = if (selectedSubTab == 0) Icons.Default.CheckCircle else Icons.Default.Search,
                            contentDescription = null,
                            modifier = Modifier.size(60.dp),
                            tint = MaterialTheme.colorScheme.secondary.copy(alpha = 0.5F)
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                        Text(
                            text = if (isBangla) "এই বিভাগে কোনো নিখোঁজ রিপোর্ট নেই।" else "No cases found in this tab",
                            style = MaterialTheme.typography.bodyMedium,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = if (isBangla) "নতুন কোনো রিপোর্ট দাখিল করতে নিচে চাপ দিন।" else "Tap below to file a new report",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7F)
                        )
                    }
                }
            } else {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1F),
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(filteredReports) { report ->
                        DetailedReportCard(report, isBangla) {
                            // Quick toggle option for simulation demo
                            val nextStatus = if (report.status == "ACTIVE") "FOUND" else "ACTIVE"
                            viewModel.updateReportStatus(report, nextStatus)
                        }
                    }
                }
            }

            // Add report button
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            ) {
                Button(
                    onClick = { showForm = true },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(52.dp)
                        .testTag("add_report_button"),
                    colors = ButtonDefaults.buttonColors(containerColor = GreenPrimary)
                ) {
                    Icon(imageVector = Icons.Default.Add, contentDescription = null)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = if (isBangla) "নতুন নিখোঁজ রিপোর্ট দাখিল করুন" else "File New Missing Report",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }

        // 1. Interactive Form Dialog
        if (showForm) {
            ReportFormDialog(
                isBangla = isBangla,
                onDismiss = { showForm = false },
                onSubmit = { name, age, location, desc, phone, reporter, avatar ->
                    viewModel.submitMissingPersonReport(name, age, location, desc, phone, reporter, avatar)
                    showForm = false
                }
            )
        }

        // 2. Animated Face Match Scanning Overlay
        when (faceMatchState) {
            is UiState.Loading -> {
                FaceScanningOverlay(isBangla)
            }
            is UiState.Success -> {
                val foundReport = (faceMatchState as UiState.Success<MissingPersonReport?>).data
                if (foundReport != null) {
                    MatchFoundDialog(
                        report = foundReport,
                        isBangla = isBangla,
                        onDismiss = { viewModel.clearFaceMatchStatus() }
                    )
                } else {
                    MatchNotFoundDialog(
                        isBangla = isBangla,
                        onDismiss = { viewModel.clearFaceMatchStatus() }
                    )
                }
            }
            else -> {}
        }
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun DetailedReportCard(
    report: MissingPersonReport,
    isBangla: Boolean,
    onStatusToggle: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Large Avatar displaying illustration
                AvatarVectorDisplay(
                    photoUrl = report.photoUrl,
                    modifier = Modifier.size(60.dp)
                )
                Spacer(modifier = Modifier.width(16.dp))
                Column(modifier = Modifier.weight(1F)) {
                    Text(
                        text = report.name,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = (if (isBangla) "বয়স: " else "Age: ") + report.age,
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Box(
                            modifier = Modifier
                                .size(4.dp)
                                .clip(CircleShape)
                                .background(MaterialTheme.colorScheme.onSurfaceVariant)
                        )
                        Text(
                            text = (if (isBangla) "তারিখ: " else "Reported: ") + report.dateReported,
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
                
                // Status badge with demo click action
                Box(
                    modifier = Modifier.clickable(onClick = onStatusToggle)
                ) {
                    StatusBadge(status = report.status, isBangla = isBangla)
                }
            }

            Spacer(modifier = Modifier.height(12.dp))
            HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5F))
            Spacer(modifier = Modifier.height(12.dp))

            Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                Row(verticalAlignment = Alignment.Top) {
                    Icon(
                        imageVector = Icons.Default.LocationOn,
                        contentDescription = null,
                        tint = RedAlert,
                        modifier = Modifier.size(16.dp).offset(y = 2.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = (if (isBangla) "শেষবার দেখা গেছে: " else "Last Seen: ") + report.lastSeenLocation,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }

                Row(verticalAlignment = Alignment.Top) {
                    Icon(
                        imageVector = Icons.Default.Info,
                        contentDescription = null,
                        tint = BluePrimary,
                        modifier = Modifier.size(16.dp).offset(y = 2.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = report.description,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        lineHeight = 16.sp
                    )
                }

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.Phone,
                        contentDescription = null,
                        tint = GreenPrimary,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = (if (isBangla) "যোগাযোগ: " else "Contact: ") + report.contactPhone,
                        style = MaterialTheme.typography.bodySmall,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    )
                }
                
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.Person,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = (if (isBangla) "প্রতিবেদক: " else "Reporter: ") + report.reporterName,
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            if (report.matchScore > 0) {
                Spacer(modifier = Modifier.height(12.dp))
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(8.dp))
                        .background(GreenPrimary.copy(alpha = 0.08F))
                        .border(1.dp, GreenPrimary.copy(alpha = 0.2F), RoundedCornerShape(8.dp))
                        .padding(8.dp)
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.TagFaces,
                            contentDescription = null,
                            tint = GreenPrimary,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = if (isBangla) "এআই ফেস রিকগনিশন দ্বারা স্বয়ংক্রিয়ভাবে চিহ্নিত! মিলের হার: %.1f%%"
                                   .format(report.matchScore)
                                   else "AI Match Found! Verification Score: %.1f%%".format(report.matchScore),
                            color = GreenPrimary,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun ReportFormDialog(
    isBangla: Boolean,
    onDismiss: () -> Unit,
    onSubmit: (String, Int, String, String, String, String, String) -> Unit
) {
    var name by remember { mutableStateOf("") }
    var ageStr by remember { mutableStateOf("") }
    var location by remember { mutableStateOf("") }
    var desc by remember { mutableStateOf("") }
    var phone by remember { mutableStateOf("") }
    var reporter by remember { mutableStateOf("") }
    var selectedImgIdx by remember { mutableStateOf("avatar_boy_1") }

    val avatars = listOf("avatar_boy_1", "avatar_girl_1", "avatar_elderly_man", "avatar_elderly_woman")

    AlertDialog(
        onDismissRequest = onDismiss,
        confirmButton = {
            Button(
                onClick = {
                    val age = ageStr.toIntOrNull() ?: 10
                    if (name.isNotEmpty() && location.isNotEmpty() && phone.isNotEmpty()) {
                        onSubmit(name, age, location, desc, phone, reporter.ifEmpty { "নামহীন নাগরিক" }, selectedImgIdx)
                    }
                },
                colors = ButtonDefaults.buttonColors(containerColor = GreenPrimary)
            ) {
                Text(text = if (isBangla) "দাখিল করুন" else "Submit")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text(text = if (isBangla) "বাতিল" else "Cancel")
            }
        },
        title = {
            Text(
                text = if (isBangla) "নতুন নিখোঁজ রিপোর্ট" else "File Missing Report",
                fontWeight = FontWeight.Bold
            )
        },
        text = {
            LazyColumn(
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(max = 450.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                item {
                    Text(
                        text = if (isBangla) "১. নিখোঁজ ব্যক্তির ছবি (অবতার নির্বাচন)" else "1. Choose Avatar Icon",
                        style = MaterialTheme.typography.bodySmall,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(12.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        avatars.forEach { avatar ->
                            Box(
                                modifier = Modifier
                                    .size(52.dp)
                                    .clip(CircleShape)
                                    .background(if (selectedImgIdx == avatar) MaterialTheme.colorScheme.primary.copy(alpha = 0.2F) else Color.Transparent)
                                    .border(
                                        width = if (selectedImgIdx == avatar) 3.dp else 1.dp,
                                        color = if (selectedImgIdx == avatar) MaterialTheme.colorScheme.primary else Color.Gray,
                                        shape = CircleShape
                                    )
                                    .clickable { selectedImgIdx = avatar },
                                contentAlignment = Alignment.Center
                            ) {
                                AvatarVectorDisplay(photoUrl = avatar, modifier = Modifier.size(44.dp))
                            }
                        }
                    }
                }

                item {
                    OutlinedTextField(
                        value = name,
                        onValueChange = { name = it },
                        label = { Text(if (isBangla) "নিখোঁজ ব্যক্তির নাম" else "Missing Person Name") },
                        modifier = Modifier.fillMaxWidth()
                    )
                }

                item {
                    OutlinedTextField(
                        value = ageStr,
                        onValueChange = { ageStr = it },
                        label = { Text(if (isBangla) "বয়স" else "Age") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        modifier = Modifier.fillMaxWidth()
                    )
                }

                item {
                    OutlinedTextField(
                        value = location,
                        onValueChange = { location = it },
                        label = { Text(if (isBangla) "কোথা থেকে নিখোঁজ হয়েছে" else "Last Seen Location") },
                        modifier = Modifier.fillMaxWidth()
                    )
                }

                item {
                    OutlinedTextField(
                        value = desc,
                        onValueChange = { desc = it },
                        label = { Text(if (isBangla) "পোশাকের বিবরণ ও অতিরিক্ত তথ্য" else "Physical Description & Clothing") },
                        modifier = Modifier.fillMaxWidth(),
                        maxLines = 3
                    )
                }

                item {
                    OutlinedTextField(
                        value = phone,
                        onValueChange = { phone = it },
                        label = { Text(if (isBangla) "যোগাযোগের ফোন নম্বর" else "Contact Phone Number") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                        modifier = Modifier.fillMaxWidth()
                    )
                }

                item {
                    OutlinedTextField(
                        value = reporter,
                        onValueChange = { reporter = it },
                        label = { Text(if (isBangla) "রিপোর্টারের নাম ও সম্পর্ক" else "Reporter's Name & Relation") },
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }
        }
    )
}

@Composable
fun FaceScanningOverlay(isBangla: Boolean) {
    // Elegant background animation
    val infiniteTransition = rememberInfiniteTransition()
    val scanY by infiniteTransition.animateFloat(
        initialValue = 0.1f,
        targetValue = 0.9f,
        animationSpec = infiniteRepeatable(
            animation = tween(2000, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        )
    )

    Surface(
        color = Color.Black.copy(alpha = 0.82F),
        modifier = Modifier.fillMaxSize()
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Box(
                modifier = Modifier
                    .size(240.dp)
                    .border(3.dp, GreenPrimary, RoundedCornerShape(16.dp))
                    .padding(3.dp)
            ) {
                // Outer corners styling or just normal image mock scan
                Box(contentAlignment = Alignment.Center, modifier = Modifier.fillMaxSize()) {
                    Icon(
                        imageVector = Icons.Default.FilterCenterFocus,
                        contentDescription = null,
                        tint = Color.White.copy(alpha = 0.4F),
                        modifier = Modifier.size(100.dp)
                    )
                }

                // Laser scan line
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .fillMaxHeight(0.02F)
                        .align(Alignment.TopCenter)
                        .offset(y = (scanY * 240).dp)
                        .background(GreenPrimary)
                )
            }

            Spacer(modifier = Modifier.height(24.dp))
            CircularProgressIndicator(color = GreenPrimary)
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = if (isBangla) "এআই ফেসিয়াল পয়েন্ট স্ক্যান করা হচ্ছে..." else "AI Scanning Facial Coordinates...",
                style = MaterialTheme.typography.bodyLarge,
                color = Color.White,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = if (isBangla) "নিকটবর্তী সিসিটিভি ক্যামেরা ও স্বেচ্ছাসেবক ফিড স্ক্যানিং সচল" else "Analyzing active feeds, CCTV servers, and NGO databases",
                style = MaterialTheme.typography.labelSmall,
                color = Color.White.copy(alpha = 0.7F),
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(horizontal = 32.dp)
            )
        }
    }
}

@Composable
fun MatchFoundDialog(
    report: MissingPersonReport,
    isBangla: Boolean,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        confirmButton = {
            Button(
                onClick = onDismiss,
                colors = ButtonDefaults.buttonColors(containerColor = GreenPrimary)
            ) {
                Text(text = if (isBangla) "উদ্ধার কার্যক্রম শুরু করুন" else "Initiate Rescue operations")
            }
        },
        title = {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Default.Star,
                    contentDescription = null,
                    tint = GreenPrimary,
                    modifier = Modifier.size(28.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = if (isBangla) "মহাসাফল্য! এআই ফেস ম্যাচ সফল!" else "Success! AI Face Match Found!",
                    color = GreenPrimary,
                    fontWeight = FontWeight.ExtraBold
                )
            }
        },
        text = {
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                AvatarVectorDisplay(photoUrl = report.photoUrl, modifier = Modifier.size(72.dp))
                Spacer(modifier = Modifier.height(12.dp))
                Text(
                    text = report.name,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = if (isBangla) "মিলের হার: %.1f%% (ফেস রিকগনিশন)" else "Match confidence: %.1f%%".format(report.matchScore),
                    fontWeight = FontWeight.Bold,
                    color = GreenPrimary
                )
                Spacer(modifier = Modifier.height(12.dp))
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(8.dp))
                        .background(MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.4F))
                        .padding(12.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = if (isBangla) 
                            "স্বেচ্ছাসেবী সায়েম কর্তৃক মোহাম্মদপুর আশ্রয়কেন্দ্র সংলগ্ন এলাকা থেকে তার ছবি সার্ভারে আপলোড করা হয়েছিল। স্থানীয় স্বেচ্ছাসেবক ও পুলিশ বাহিনীকে মেসেজ অ্যালার্ট পাঠানো হয়েছে!"
                            else "Volunteer Sayem uploaded a verification trace matches adjacent to Mohammadpur block. Geo-alarms triggered automatically!",
                        style = MaterialTheme.typography.bodySmall,
                        textAlign = TextAlign.Center,
                        lineHeight = 16.sp
                    )
                }
            }
        }
    )
}

@Composable
fun MatchNotFoundDialog(
    isBangla: Boolean,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        confirmButton = {
            Button(onClick = onDismiss) {
                Text(text = if (isBangla) "ঠিক আছে" else "Ok")
            }
        },
        title = {
            Text(
                text = if (isBangla) "রিপোর্ট সফলভাবে গৃহিত হয়েছে" else "Report Filed Successfully",
                color = BluePrimary,
                fontWeight = FontWeight.Bold
            )
        },
        text = {
            Text(
                text = if (isBangla) 
                    "আপনার নিখোঁজ রিপোর্ট সফলভাবে ডাটাবেজে নথিবদ্ধ করা হয়েছে। আঞ্চলিক স্বেচ্ছাসেবক নেটওয়ার্ক ও পুলিশ বিভাগকে বার্তা পাঠানো হয়েছে। এআই ক্যামেরা ফিডগুলোতে সার্বক্ষণিক টহল স্ক্যানিং অব্যাহত রয়েছে।"
                    else "Your report was filed successfully and synced to the secure regional databases. AI background scanning is active. Check alerts panel regularly.",
                style = MaterialTheme.typography.bodyMedium,
                lineHeight = 20.sp
            )
        }
    )
}
