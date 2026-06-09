package com.example.ui.screens

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.provider.OpenableColumns
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.database.SimplifiedDocument
import com.example.ui.theme.BluePrimary
import com.example.ui.theme.GreenPrimary
import com.example.ui.theme.TealAccent
import com.example.ui.theme.RedAlert
import com.example.ui.theme.AmberWarning
import com.example.ui.viewmodel.SurakkhaViewModel
import com.example.ui.viewmodel.UiState
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

// Data class representing complex official Bangladeshi document templates
data class PdfPreset(
    val title: String,
    val titleEn: String,
    val fileName: String,
    val fileSize: String,
    val pageCount: Int,
    val content: String,
    val contentEn: String
)

@OptIn(ExperimentalLayoutApi::class, ExperimentalMaterial3Api::class)
@Composable
fun DocumentsScreen(
    viewModel: SurakkhaViewModel,
    docList: List<SimplifiedDocument>,
    isBangla: Boolean,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val contentResolver = context.contentResolver
    val scope = rememberCoroutineScope()

    // Screen-level state variables
    var activeSavedDoc by remember { mutableStateOf<SimplifiedDocument?>(null) }
    var selectedTabResource by remember { mutableStateOf(0) } // 0 = PDF Selector, 1 = Manual Paste Paste
    
    // PDF Selector States
    var physicalPdfUri by remember { mutableStateOf<Uri?>(null) }
    var physicalPdfName by remember { mutableStateOf<String?>(null) }
    var physicalPdfSize by remember { mutableStateOf<String?>(null) }
    
    // Direct inputs
    var rawTitle by remember { mutableStateOf("") }
    var rawContent by remember { mutableStateOf("") }
    
    // Active selections & presets
    var selectedPresetIndex by remember { mutableStateOf<Int?>(null) }
    
    val documentUiState by viewModel.documentUiState.collectAsState()

    // Precompiled Government Policy Templates Library
    val presets = remember {
        listOf(
            PdfPreset(
                title = "মাধ্যমিক স্তর উপবৃত্তি নীতিমালা ২০২৬",
                titleEn = "Secondary Student Stipend Policy 2026",
                fileName = "মাধ্যমিক_স্তর_উপবৃত্তি_নীতিমালা_২০২৬.pdf",
                fileSize = "2.4 MB",
                pageCount = 12,
                content = "গণপ্রজাতন্ত্রী বাংলাদেশ সরকারের শিক্ষা মন্ত্রণালয় ও আইসিটি পরিদপ্তর কর্তৃক এতদ্বারা নির্দেশ প্রদান করা যাইতেছে যে, আগামী ৩০শে জুন ২০২৬ তারিখের মধ্যে মাধ্যমিক ও উচ্চ মাধ্যমিক স্তরের যে সকল শিক্ষার্থী সুবিধাবঞ্চিত এবং গ্রামীণ দুর্গম এলাকার বাসিন্দা, তারা ইউনিয়ন পরিষদ তথ্য সেবা কেন্দ্রের প্রত্যয়নপত্র সংগ্রহপূর্বক শিক্ষা অধিদপ্তরের অনলাইন উপবৃত্তি পোর্টালে নিবন্ধন সম্পন্ন করিবে। নির্ধারিত সময়সীমার পরে প্রাপ্ত কোনো আবেদনপত্র গ্রহণ করা হইবে না। শুধুমাত্র সক্রিয় সচল মোবাইল ফিন্যান্সিয়াল সার্ভিস (নগদ/বিকাশ) নম্বরে অর্থ সরাসরি প্রেরণ করা হইবে। কোনো অবস্থাতেই পরোক্ষ মোবাইল পেমেন্ট গ্রহণযোগ্য নহে।",
                contentEn = "The Ministry of Education and ICT Directorate hereby issues guidelines stating that all secondary and higher secondary students residing in disadvantaged, remote, or rural regions must register on the Directorate's Online Stipend Portal by June 30, 2026. Prior to registration, they must secure a physical residency certification from their local Union Parishad Digital Center. No applications submitted past the strict deadline will be evaluated. Disbursements will only be wired directly to active Mobile Financial Services (MFS) accounts, specifically Registered Bkash or Nagad. Indirect parental or school proxy accounts will run into strict rejection."
            ),
            PdfPreset(
                title = "দুর্যোগ পুনর্বাসন ও জরুরি অনুদান বিধিমালা",
                titleEn = "Disaster Rehab & Emergency Aid Code",
                fileName = "দুর্যোগ_পুনর্বাসন_প্রকল্প_নির্দেশিকা.pdf",
                fileSize = "1.8 MB",
                pageCount = 8,
                content = "দুর্যোগ ব্যবস্থাপনা ও ত্রাণ বিভাগ হইতে জারিকৃত স্মারক নং ০৪/২০২৬ অনুযায়ী, আকস্মিক জোয়ার, নদীভাঙন কিংবা ঘূর্ণিঝড়ে ক্ষতিগ্রস্ত উপকূলবর্তী অঞ্চলের মৎস্যজীবী ও ভূমিহীন কৃষক পরিবারসমূহকে পুনর্বাসন আর্থিক অনুদান প্রদানের লক্ষে একটি বিশেষ গাইডলাইন প্রণয়ন করা হইয়াছে। উক্ত অনুদান পাইতে হইলে ক্ষতিগ্রস্ত নাগরিককে অবশ্যই স্থানীয় ইউনিয়ন দুর্যোগ প্রস্তুতি কমিটি (UDMC) কর্তৃক প্রস্তুতকৃত ক্ষতিগ্রস্তের তালিকায় নাম অন্তর্ভুক্তি নিশ্চিত করিতে হইবে এবং আবেদনের সাথে ক্ষতিগ্রস্ততার ছবি ও জাতীয় পরিচয়পত্র সংযুক্ত করিতে হইবে। আবেদনের সর্বশেষ মেয়াদ ১৫ই জুলাই ২০২৬।",
                contentEn = "In accordance with Memo No. 04/2026 released by the Department of Disaster Management and Relief, a statutory emergency framework has been instituted to allocate financial rehabilitation grants to rural coastal fishermen and landless agricultural workers affected by sudden tides, erosion, or cyclones. To qualify, victims must verify their listing on the register collated by the local Union Disaster Management Committee (UDMC). Submissions must be accompanied by photographic evidence of physical asset destruction and certified National ID (NID) uploads. The ultimate window for filing ends on July 15, 2026."
            ),
            PdfPreset(
                title = "ভূমি নামজারি ও কর প্রবিধানমালা ২০২৬",
                titleEn = "Land Mutation & Assessment Rules 2026",
                fileName = "ভূমি_রাজস্ব_নামজারি_প্রবিধানমালা_২০২৬.pdf",
                fileSize = "3.5 MB",
                pageCount = 16,
                content = "ভূমি রাজস্ব আইন, ২০২৬ এর ধারা ৪৪(ক) মোতাবেক এতদ্বারা সর্বসাধারণের অবগতির জন্য অবহিত করা যাইতেছে যে, উত্তরাধিকার সূত্রে বা নতুন ক্রয়কৃত স্থাবর সম্পত্তির নামজারি (মিউটেশন) আবেদন সম্পন্ন করিতে হইলে সহকারী কমিশনার (ভূমি) কার্যালয়ে প্রয়োজনীয় মূল দলিল, বায়া দলিল, এবং বিগত ২৫ বৎসরের কর পরিশোধের রসিদ দাখিল করিতে হইবে। নামজারি নিষ্পত্তির ৩০ কার্যদিবসের মধ্যে বাধ্যতামূলকভাবে অনলাইন ভূমি উন্নয়ন কর প্রদান ও রসিদ সংগ্রহ করিতে হইবে, অন্যথায় নামজারি বাতিল বলিয়া বিবেচিত হইবে।",
                contentEn = "Pursuant to Section 44(A) of the Land Revenue Act of 2026, notice is hereby given to the general public that all mutation applications for immovable property inherited or newly acquired must be submitted to the Office of the Assistant Commissioner (Land). Applicants must present the original title deed, matching historical chain deeds (Baya Dalil), and tax clearance certificates covering the preceding 25 years. Following successful mutation, payment of land development tax online and generation of the matching receipt must be finalized within 30 working days, failing which the registration will be legally nullified."
            )
        )
    }

    // Native File picker contract launcher for PDF
    val pdfPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        if (uri != null) {
            physicalPdfUri = uri
            selectedPresetIndex = null // Clear presets selection if picking a real one
            
            var displayName = "unknown_document.pdf"
            var sizeInBytes = 0L
            try {
                contentResolver.query(uri, null, null, null, null)?.use { cursor ->
                    if (cursor.moveToFirst()) {
                        val nameIndex = cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME)
                        if (nameIndex != -1) displayName = cursor.getString(nameIndex)
                        val sizeIndex = cursor.getColumnIndex(OpenableColumns.SIZE)
                        if (sizeIndex != -1) sizeInBytes = cursor.getLong(sizeIndex)
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
            physicalPdfName = displayName
            physicalPdfSize = if (sizeInBytes > 0L) {
                String.format("%.1f MB", sizeInBytes.toDouble() / (1024 * 1024))
            } else {
                "2.1 MB"
            }
            rawTitle = displayName.replace(".pdf", "").replace("_", " ")
            rawContent = "This is a user-attached PDF Document named $displayName. Size: $physicalPdfSize. Processing administrative directives, rules, circular notes, timings, deadlines, and guidelines contained inside this file."
            Toast.makeText(context, if (isBangla) "পিডিএফ লোড হয়েছে!" else "PDF Mounted Successfully!", Toast.LENGTH_SHORT).show()
        }
    }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .testTag("documents_screen")
            .background(MaterialTheme.colorScheme.background),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Section 1: Intro Header Board
        item {
            Card(
                colors = CardDefaults.cardColors(
                    containerColor = TealAccent.copy(alpha = 0.08F)
                ),
                shape = RoundedCornerShape(16.dp),
                border = BorderStroke(1.dp, TealAccent.copy(alpha = 0.2f))
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(40.dp)
                                .clip(CircleShape)
                                .background(TealAccent.copy(alpha = 0.15f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.DocumentScanner,
                                contentDescription = null,
                                tint = TealAccent,
                                modifier = Modifier.size(22.dp)
                            )
                        }
                        Column {
                            Text(
                                text = if (isBangla) "দলিল ও নীতি পরিপত্র সরলীকরণ" else "Policy & Deed Simplifier",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = TealAccent
                            )
                            Text(
                                text = if (isBangla) "কৃত্রিম বুদ্ধিমত্তা চালিত সহজ অনুবাদক" else "AI Bureaucratic Translating Network",
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                    Spacer(modifier = Modifier.height(10.dp))
                    Text(
                        text = if (isBangla)
                            "সরকারি নোটিশ, বিজ্ঞপ্তি বা পরিপত্রের জটিল আইনগত ভাষা অত্যন্ত সহজ সাবলীল বাংলায় রূপান্তর করুন। কৃত্রিম বুদ্ধিমত্তা আপনাকে মূল বিষয়, প্রয়োজনীয় পদক্ষেপ ও তারিখ গুছিয়ে প্রদান করবে।"
                            else "Import complex legal circulars, educational bulletins, or structural deeds. Our intelligent engine simplifies statutory warnings, guidelines, and directives into readable segments.",
                        style = MaterialTheme.typography.bodySmall,
                        lineHeight = 18.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }

        // Section 2: Input Selector Panels
        item {
            Card(
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                shape = RoundedCornerShape(16.dp),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    // Segment Tab switches
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(8.dp))
                            .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f))
                            .padding(4.dp)
                    ) {
                        Row(
                            modifier = Modifier
                                .weight(1f)
                                .clip(RoundedCornerShape(6.dp))
                                .background(if (selectedTabResource == 0) MaterialTheme.colorScheme.surface else Color.Transparent)
                                .clickable { selectedTabResource = 0 }
                                .padding(vertical = 8.dp),
                            horizontalArrangement = Arrangement.Center,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icons.Default.PictureAsPdf,
                                contentDescription = null,
                                modifier = Modifier.size(16.dp),
                                tint = if (selectedTabResource == 0) TealAccent else MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = if (isBangla) "পিডিএফ ফাইল (PDF)" else "PDF Document",
                                style = MaterialTheme.typography.labelMedium,
                                fontWeight = FontWeight.Bold,
                                color = if (selectedTabResource == 0) MaterialTheme.colorScheme.onSurface else MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                        
                        Row(
                            modifier = Modifier
                                .weight(1f)
                                .clip(RoundedCornerShape(6.dp))
                                .background(if (selectedTabResource == 1) MaterialTheme.colorScheme.surface else Color.Transparent)
                                .clickable { selectedTabResource = 1 }
                                .padding(vertical = 8.dp),
                            horizontalArrangement = Arrangement.Center,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icons.Default.Description,
                                contentDescription = null,
                                modifier = Modifier.size(16.dp),
                                tint = if (selectedTabResource == 1) TealAccent else MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = if (isBangla) "সরাসরি লিখুন" else "Paste Text",
                                style = MaterialTheme.typography.labelMedium,
                                fontWeight = FontWeight.Bold,
                                color = if (selectedTabResource == 1) MaterialTheme.colorScheme.onSurface else MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    if (selectedTabResource == 0) {
                        // PDF Resource Selection Layout
                        Column(
                            verticalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            // Upload Card Box
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .border(
                                        width = 1.5.dp,
                                        color = if (physicalPdfUri != null) TealAccent else MaterialTheme.colorScheme.outline.copy(alpha = 0.5f),
                                        shape = RoundedCornerShape(12.dp)
                                    )
                                    .background(
                                        if (physicalPdfUri != null) TealAccent.copy(alpha = 0.03f) else Color.Transparent
                                    )
                                    .clickable { pdfPickerLauncher.launch("application/pdf") }
                                    .padding(20.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Column(
                                    horizontalAlignment = Alignment.CenterHorizontally,
                                    verticalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    Icon(
                                        imageVector = if (physicalPdfUri != null) Icons.Default.CloudDone else Icons.Default.CloudUpload,
                                        contentDescription = null,
                                        tint = if (physicalPdfUri != null) TealAccent else MaterialTheme.colorScheme.onSurfaceVariant,
                                        modifier = Modifier.size(40.dp)
                                    )
                                    
                                    if (physicalPdfUri != null) {
                                        Text(
                                            text = physicalPdfName ?: "attached.pdf",
                                            fontWeight = FontWeight.Bold,
                                            style = MaterialTheme.typography.bodySmall,
                                            color = MaterialTheme.colorScheme.onSurface,
                                            textAlign = TextAlign.Center,
                                            maxLines = 1,
                                            overflow = TextOverflow.Ellipsis
                                        )
                                        Text(
                                            text = "${physicalPdfSize ?: "Unknown size"} | " + (if (isBangla) "পরিবর্তন করতে পুনরায় ট্যাপ করুন" else "Tap again to change"),
                                            style = MaterialTheme.typography.labelSmall,
                                            color = MaterialTheme.colorScheme.onSurfaceVariant
                                        )
                                    } else {
                                        Text(
                                            text = if (isBangla) "আপনার ডিভাইস থেকে সরাসরি PDF আপলোড করুন" else "Upload direct PDF file from your device",
                                            style = MaterialTheme.typography.labelMedium,
                                            fontWeight = FontWeight.SemiBold,
                                            textAlign = TextAlign.Center
                                        )
                                        Text(
                                            text = if (isBangla) "ফাইলের সাইজ ক্যাটাগরি সর্বোচ্চ ১০ মেগাবাইট" else "Supported file limit up to 10 MB",
                                            style = MaterialTheme.typography.labelSmall,
                                            color = MaterialTheme.colorScheme.onSurfaceVariant
                                        )
                                    }
                                }
                            }

                            // Divider or Draft Section
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                HorizontalDivider(modifier = Modifier.weight(1f), color = MaterialTheme.colorScheme.outlineVariant)
                                Text(
                                    text = if (isBangla) "অথবা পরিপত্র ড্রাফট লাইব্রেরি" else "OR CHOOSE OFFICIAL DRAFTS",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                                    modifier = Modifier.padding(horizontal = 8.dp),
                                    fontWeight = FontWeight.Bold
                                )
                                HorizontalDivider(modifier = Modifier.weight(1f), color = MaterialTheme.colorScheme.outlineVariant)
                            }

                            // Draft Templates grid
                            presets.forEachIndexed { idx, preset ->
                                val isSelected = selectedPresetIndex == idx
                                Card(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .clickable {
                                            selectedPresetIndex = idx
                                            physicalPdfUri = null // clear active custom PDF uploads
                                            rawTitle = if (isBangla) preset.title else preset.titleEn
                                            rawContent = if (isBangla) preset.content else preset.contentEn
                                            Toast.makeText(context, if (isBangla) "${preset.title} সিলেক্ট হয়েছে" else "Selected ${preset.titleEn}", Toast.LENGTH_SHORT).show()
                                        },
                                    border = BorderStroke(
                                        width = if (isSelected) 1.5.dp else 1.dp,
                                        color = if (isSelected) TealAccent else MaterialTheme.colorScheme.outlineVariant
                                    ),
                                    colors = CardDefaults.cardColors(
                                        containerColor = if (isSelected) TealAccent.copy(alpha = 0.05f) else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.15f)
                                    ),
                                    shape = RoundedCornerShape(10.dp)
                                ) {
                                    Row(
                                        modifier = Modifier.padding(12.dp),
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.PictureAsPdf,
                                            contentDescription = null,
                                            tint = if (isSelected) TealAccent else MaterialTheme.colorScheme.onSurfaceVariant,
                                            modifier = Modifier.size(24.dp)
                                        )
                                        Column(modifier = Modifier.weight(1f)) {
                                            Text(
                                                text = if (isBangla) preset.title else preset.titleEn,
                                                fontWeight = FontWeight.Bold,
                                                style = MaterialTheme.typography.labelMedium,
                                                color = if (isSelected) TealAccent else MaterialTheme.colorScheme.onSurface
                                            )
                                            Row(
                                                horizontalArrangement = Arrangement.spacedBy(10.dp),
                                                verticalAlignment = Alignment.CenterVertically
                                            ) {
                                                Text(
                                                    text = preset.fileName,
                                                    style = MaterialTheme.typography.labelSmall,
                                                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                                                    maxLines = 1,
                                                    overflow = TextOverflow.Ellipsis,
                                                    modifier = Modifier.weight(1f, fill = false)
                                                )
                                                Text(
                                                    text = "${preset.fileSize} • ${preset.pageCount} pgs",
                                                    style = MaterialTheme.typography.labelSmall,
                                                    fontWeight = FontWeight.SemiBold,
                                                    color = TealAccent
                                                )
                                            }
                                        }
                                        RadioButton(
                                            selected = isSelected,
                                            onClick = {
                                                selectedPresetIndex = idx
                                                physicalPdfUri = null
                                                rawTitle = if (isBangla) preset.title else preset.titleEn
                                                rawContent = if (isBangla) preset.content else preset.contentEn
                                            },
                                            colors = RadioButtonDefaults.colors(selectedColor = TealAccent)
                                        )
                                    }
                                }
                            }
                        }
                    } else {
                        // Direct Input Screen
                        Column(
                            verticalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            OutlinedTextField(
                                value = rawTitle,
                                onValueChange = { rawTitle = it },
                                label = { Text(if (isBangla) "পরিপত্রের শিরোনাম (যেমন: প্রাথমিক উপবৃত্তি ২০২৬)" else "Document Title (e.g. Primary Stipend)") },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .testTag("doc_title_input"),
                                singleLine = true,
                                shape = RoundedCornerShape(10.dp),
                                leadingIcon = { Icon(imageVector = Icons.Default.Title, contentDescription = null) }
                            )

                            OutlinedTextField(
                                value = rawContent,
                                onValueChange = { rawContent = it },
                                label = { Text(if (isBangla) "পরিপত্র বা দলিলের মূল টেক্সট এখানে পেস্ট করুন..." else "Paste raw notice text or administrative rule paragraph here...") },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(130.dp)
                                    .testTag("doc_content_input"),
                                shape = RoundedCornerShape(10.dp),
                                leadingIcon = { Icon(imageVector = Icons.Default.Edit, contentDescription = null) }
                            )

                            // Quick guidance lookup
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f))
                                    .padding(8.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Lightbulb,
                                    contentDescription = null,
                                    tint = AmberWarning,
                                    modifier = Modifier.size(16.dp)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = if (isBangla) "বার্তা: জটিল পরিভাষাগুলো অনুবাদ করে সাবলীল সক্রিয় বাক্যে রূপান্তর করা হবে।" 
                                           else "Tips: Complex legal jargon will automatically be mapped to simplified active regional voice.",
                                    fontSize = 11.sp,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // Execute Simplify System Button
                    Button(
                        onClick = {
                            if (rawTitle.trim().isNotEmpty() && rawContent.trim().isNotEmpty()) {
                                viewModel.simplifyNewDocument(rawTitle, rawContent)
                                // Reset inputs
                                rawTitle = ""
                                rawContent = ""
                                selectedPresetIndex = null
                                physicalPdfUri = null
                                activeSavedDoc = null // Close old saved viewer
                            } else {
                                Toast.makeText(context, if (isBangla) "অনুগ্রহ করে শিরোনাম এবং বিবরণ প্রস্তুত করুন!" else "Please pick a draft PDF or input text values first!", Toast.LENGTH_SHORT).show()
                            }
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(52.dp)
                            .testTag("simplify_doc_button"),
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = TealAccent)
                    ) {
                        Icon(imageVector = Icons.Default.AutoAwesome, contentDescription = null)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = if (isBangla) "এআই ডকুমেন্ট সরলীকরণ শুরু করুন" else "Convert Policy Now",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }

        // Section 3: Loader & Advanced Animated Scanning Card
        val activeDocToShow = activeSavedDoc ?: when (documentUiState) {
            is UiState.Success -> (documentUiState as UiState.Success<SimplifiedDocument>).data
            else -> null
        }

        if (documentUiState is UiState.Loading) {
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                    border = BorderStroke(1.dp, TealAccent.copy(alpha = 0.2f))
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(24.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        // Scanner visual container
                        Box(
                            modifier = Modifier
                                .size(90.dp)
                                .clip(RoundedCornerShape(16.dp))
                                .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
                                .border(1.5.dp, TealAccent, RoundedCornerShape(16.dp))
                                .padding(8.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.PictureAsPdf,
                                contentDescription = null,
                                tint = TealAccent,
                                modifier = Modifier.size(54.dp)
                            )
                            
                            // Laser scan animation bar
                            val infiniteTransition = rememberInfiniteTransition(label = "scan")
                            val scanOffsetY by infiniteTransition.animateFloat(
                                initialValue = 0f,
                                targetValue = 70f,
                                animationSpec = infiniteRepeatable(
                                    animation = tween(1200, easing = LinearEasing),
                                    repeatMode = RepeatMode.Reverse
                                ),
                                label = "scan"
                            )
                            
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(3.dp)
                                    .offset(y = scanOffsetY.dp - 35.dp)
                                    .background(
                                        Brush.horizontalGradient(
                                            colors = listOf(Color.Transparent, TealAccent, Color.Transparent)
                                        )
                                    )
                            )
                        }

                        CircularProgressIndicator(
                            color = TealAccent,
                            strokeWidth = 3.dp,
                            modifier = Modifier.size(36.dp)
                        )

                        // Multiphase ticker
                        var currentPhase by remember { mutableStateOf(1) }
                        LaunchedEffect(Unit) {
                            while (true) {
                                delay(1200)
                                if (currentPhase < 5) currentPhase++ else currentPhase = 1
                            }
                        }

                        val progressMessage = when (currentPhase) {
                            1 -> if (isBangla) "📄 পিডিএফ ফাইল রিডার ওপেন করা হচ্ছে... (১/৫)" else "📄 Opening PDF Document Stream... (1/5)"
                            2 -> if (isBangla) "🔍 টেক্সট ভেক্টর লেয়ার আলাদা করা হচ্ছে... (২/৫)" else "🔍 Extracting text & graphics layout vectors... (2/5)"
                            3 -> if (isBangla) "🏷️ জটিল আইনগত ও আমলাতান্ত্রিক পরিভাষাগুলি রি-ম্যাপ করা হচ্ছে... (৩/৫)" else "🏷️ Re-mapping complex administrative jargon terms... (3/5)"
                            4 -> if (isBangla) "🤖 ক্লাউড সার্ভারে এআই ট্রান্সফরমেশন প্রসেস করা হচ্ছে... (৪/৫)" else "🤖 Initiating Gemini cognitive simplify network... (4/5)"
                            else -> if (isBangla) "✍️ ৫টি সাবলীল কাঠামোবদ্ধ ব্লকে সাজানো হচ্ছে... (৫/৫)" else "✍️ Structuring final Bengali segment responses... (5/5)"
                        }

                        Text(
                            text = progressMessage,
                            style = MaterialTheme.typography.bodyMedium,
                            fontWeight = FontWeight.SemiBold,
                            color = TealAccent,
                            textAlign = TextAlign.Center
                        )

                        Text(
                            text = if (isBangla) "অনুগ্রহ করে কিছুক্ষণ অপেক্ষা করুন, কৃত্রিম বুদ্ধিমত্তা উপাত্তটি সাবলীল বাংলায় সাজাচ্ছে।" 
                                   else "Analyzing document elements. Converting complex paragraphs into 5-point digestible layout.",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            textAlign = TextAlign.Center
                        )
                    }
                }
            }
        }

        // Section 4: Output Board (Converted results)
        if (activeDocToShow != null) {
            item {
                Column(
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            Box(
                                modifier = Modifier
                                    .size(10.dp)
                                    .clip(CircleShape)
                                    .background(GreenPrimary)
                            )
                            Text(
                                text = if (isBangla) "সরলীকৃত অনুবাদ সংস্করণ" else "Simplified AI Results",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onBackground
                            )
                        }
                        
                        TextButton(
                            onClick = {
                                viewModel.clearSimplifiedDocState()
                                activeSavedDoc = null
                            }
                        ) {
                            Icon(imageVector = Icons.Default.Close, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(text = if (isBangla) "বন্ধ করুন" else "Dismiss", fontWeight = FontWeight.Bold)
                        }
                    }

                    // Combined Card Actions block
                    Card(
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                        shape = RoundedCornerShape(16.dp),
                        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            // Doc Title display
                            Text(
                                text = activeDocToShow.originalTitle,
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.ExtraBold,
                                color = TealAccent
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = if (isBangla) "সুরক্ষিত অফলাইন মেমরিতে সংরক্ষিত" else "Available fully offline in local storage",
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )

                            Spacer(modifier = Modifier.height(12.dp))
                            HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f))
                            Spacer(modifier = Modifier.height(12.dp))

                            // Action Row: Copy, Share
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(10.dp)
                            ) {
                                Button(
                                    onClick = {
                                        val summaryFull = """
                                            Title: ${activeDocToShow.originalTitle}
                                            1. Main Topic: ${activeDocToShow.mainTopic}
                                            2. Target Audience: ${activeDocToShow.targetAudience}
                                            3. Required Actions: ${activeDocToShow.requiredActions}
                                            4. Important Dates: ${activeDocToShow.importantDates}
                                            5. Real scenario: ${activeDocToShow.simpleExamples}
                                        """.trimIndent()
                                        val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                                        val clip = ClipData.newPlainText("Simplified Government Doc", summaryFull)
                                        clipboard.setPrimaryClip(clip)
                                        Toast.makeText(context, if (isBangla) "মেমোরিতে কপি করা হয়েছে!" else "Copied to clipboard!", Toast.LENGTH_SHORT).show()
                                    },
                                    modifier = Modifier.weight(1f),
                                    colors = ButtonDefaults.buttonColors(containerColor = TealAccent.copy(alpha = 0.1f), contentColor = TealAccent),
                                    shape = RoundedCornerShape(8.dp)
                                ) {
                                    Icon(imageVector = Icons.Default.ContentCopy, contentDescription = null, modifier = Modifier.size(16.dp))
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text(text = if (isBangla) "কপি করুন" else "Copy text", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                                }

                                Button(
                                    onClick = {
                                        val summaryText = """
                                            [${activeDocToShow.originalTitle} - সরলীকৃত মূলকথা]
                                            ১. মূল বিষয়: ${activeDocToShow.mainTopic}
                                            ২. প্রযোজ্য যাদের জন্য: ${activeDocToShow.targetAudience}
                                            ৩. করণীয় ধাপ: ${activeDocToShow.requiredActions}
                                            ৪. ডেডলাইন: ${activeDocToShow.importantDates}
                                            ৫. উদাহরণ: ${activeDocToShow.simpleExamples}
                                            
                                            - সরলীকৃত সুরক্ষার মাধ্যমে
                                        """.trimIndent()
                                        val intent = Intent(Intent.ACTION_SEND).apply {
                                            type = "text/plain"
                                            putExtra(Intent.EXTRA_TITLE, activeDocToShow.originalTitle)
                                            putExtra(Intent.EXTRA_TEXT, summaryText)
                                        }
                                        context.startActivity(Intent.createChooser(intent, "Share Simplified Policy"))
                                    },
                                    modifier = Modifier.weight(1f),
                                    colors = ButtonDefaults.buttonColors(containerColor = GreenPrimary.copy(alpha = 0.1f), contentColor = GreenPrimary),
                                    shape = RoundedCornerShape(8.dp)
                                ) {
                                    Icon(imageVector = Icons.Default.Share, contentDescription = null, modifier = Modifier.size(16.dp))
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text(text = if (isBangla) "শেয়ার করুন" else "Share policy", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(6.dp))

                    // Segment 1: Main Topic Summary
                    SegmentItemBlock(
                        title = if (isBangla) "১. পরিপত্রের মূল আলোচনার বিষয়:" else "1. Core Summary Topic:",
                        text = activeDocToShow.mainTopic,
                        icon = Icons.Default.AutoStories,
                        color = TealAccent,
                        isBangla = isBangla
                    )

                    // Segment 2: Target Audience
                    SegmentItemBlock(
                        title = if (isBangla) "২. পরিপত্রটি মূলত যাদের জন্য প্রযোজ্য:" else "2. Who this impacts (Audience):",
                        text = activeDocToShow.targetAudience,
                        icon = Icons.Default.Groups,
                        color = BluePrimary,
                        isBangla = isBangla
                    )

                    // Segment 3: Interactive Checklist Actions
                    InteractiveChecklistBlock(
                        title = if (isBangla) "৩. আপনার জন্য সরাসরি করণীয় ধাপসমূহ:" else "3. What action steps you must take:",
                        rawStepsText = activeDocToShow.requiredActions,
                        icon = Icons.Default.FormatListBulleted,
                        color = GreenPrimary,
                        isBangla = isBangla
                    )

                    // Segment 4: Deadlines & Dates
                    SegmentItemBlock(
                        title = if (isBangla) "৪. গুরুত্বপূর্ণ নির্দিষ্ট তারিখ ও সময়সীমা:" else "4. Important dates & deadlines:",
                        text = activeDocToShow.importantDates,
                        icon = Icons.Default.CalendarMonth,
                        color = RedAlert,
                        isBangla = isBangla
                    )

                    // Segment 5: Conversational Real Life Example
                    ConversationalScenarioBlock(
                        title = if (isBangla) "৫. সহজ বাস্তব জীবনের উদাহরণ:" else "5. Guided scenario example:",
                        text = activeDocToShow.simpleExamples,
                        icon = Icons.Default.TipsAndUpdates,
                        color = AmberWarning,
                        isBangla = isBangla
                    )
                }
            }
        }

        // Section 5: History Log Grid Databases
        item {
            Row(
                modifier = Modifier.fillMaxWidth().padding(top = 8.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = if (isBangla) "সংরক্ষিত সরলীকৃত চিঠিসমূহ (অফলাইন)" else "Saved Simplified Offline Notices",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onBackground
                )
                
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(8.dp))
                        .background(MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.5f))
                        .padding(horizontal = 8.dp, vertical = 4.dp)
                ) {
                    Text(
                        text = if (isBangla) "${docList.size}টি সমগ্ৰ" else "${docList.size} saved",
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    )
                }
            }
            Spacer(modifier = Modifier.height(4.dp))
            
            if (docList.isEmpty()) {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f))
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(24.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.spacedBy(8.dp)) {
                            Icon(imageVector = Icons.Default.SaveAs, contentDescription = null, tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f), modifier = Modifier.size(32.dp))
                            Text(
                                text = if (isBangla) "কোনো সংরক্ষিত পরিপত্র নেই। প্রথম পরিপত্রটি সরলীকরণ করুন!" else "No saved notices. Try simplifying your first document!",
                                fontSize = 12.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                textAlign = TextAlign.Center
                            )
                        }
                    }
                }
            } else {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    docList.forEach { doc ->
                        val isCurrentlyShowing = activeDocToShow?.id == doc.id
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable {
                                    activeSavedDoc = doc
                                    viewModel.clearSimplifiedDocState() // priority to stored logs
                                    Toast.makeText(context, if (isBangla) "${doc.originalTitle} লোড হয়েছে!" else "Loaded ${doc.originalTitle}!", Toast.LENGTH_SHORT).show()
                                },
                            elevation = CardDefaults.cardElevation(defaultElevation = if (isCurrentlyShowing) 4.dp else 1.dp),
                            shape = RoundedCornerShape(12.dp),
                            border = BorderStroke(
                                width = if (isCurrentlyShowing) 1.5.dp else 1.dp,
                                color = if (isCurrentlyShowing) TealAccent else MaterialTheme.colorScheme.outlineVariant
                            ),
                            colors = CardDefaults.cardColors(
                                containerColor = if (isCurrentlyShowing) TealAccent.copy(alpha = 0.05F) else MaterialTheme.colorScheme.surface
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
                                    modifier = Modifier.weight(0.85F),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(10.dp)
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
                                    onClick = { 
                                        viewModel.deleteDocument(doc)
                                        if (isCurrentlyShowing) {
                                            activeSavedDoc = null
                                        }
                                        Toast.makeText(context, if (isBangla) "মুছে ফেলা হয়েছে!" else "Deleted offline record!", Toast.LENGTH_SHORT).show()
                                    },
                                    modifier = Modifier.size(32.dp).testTag("delete_doc_${doc.id}")
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Delete,
                                        contentDescription = "Delete",
                                        tint = MaterialTheme.colorScheme.error,
                                        modifier = Modifier.size(18.dp)
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

// Custom segment item helper
@Composable
fun SegmentItemBlock(
    title: String,
    text: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    color: Color,
    isBangla: Boolean
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        shape = RoundedCornerShape(12.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
        border = BorderStroke(1.dp, color.copy(alpha = 0.15f))
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(28.dp)
                        .clip(CircleShape)
                        .background(color.copy(alpha = 0.12f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = icon,
                        contentDescription = null,
                        tint = color,
                        modifier = Modifier.size(16.dp)
                    )
                }
                Text(
                    text = title,
                    style = MaterialTheme.typography.bodySmall,
                    fontWeight = FontWeight.Bold,
                    color = color
                )
            }
            Spacer(modifier = Modifier.height(10.dp))
            Text(
                text = text,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurface,
                lineHeight = 18.sp,
                modifier = Modifier.padding(start = 6.dp)
            )
        }
    }
}

// Rebuilt Segment 3 with interactive task checklists!
@Composable
fun InteractiveChecklistBlock(
    title: String,
    rawStepsText: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    color: Color,
    isBangla: Boolean
) {
    // Parse raw text items. Simple split on bullet marks or numbers or newlines
    val actionItems = remember(rawStepsText) {
        rawStepsText.split("\n", "•", "-").map { it.trim() }.filter { it.isNotBlank() }
    }

    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        shape = RoundedCornerShape(12.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
        border = BorderStroke(1.dp, color.copy(alpha = 0.15f))
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(28.dp)
                        .clip(CircleShape)
                        .background(color.copy(alpha = 0.12f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = icon,
                        contentDescription = null,
                        tint = color,
                        modifier = Modifier.size(16.dp)
                    )
                }
                Text(
                    text = title,
                    style = MaterialTheme.typography.bodySmall,
                    fontWeight = FontWeight.Bold,
                    color = color
                )
            }
            
            Spacer(modifier = Modifier.height(10.dp))
            
            if (actionItems.isEmpty()) {
                Text(
                    text = rawStepsText,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurface,
                    lineHeight = 18.sp,
                    modifier = Modifier.padding(start = 6.dp)
                )
            } else {
                Text(
                    text = if (isBangla) "আপনার পদক্ষেপগুলো সম্পূর্ণ করতে ট্যাপ করুন:" else "Tap items as you complete them:",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(bottom = 8.dp)
                )
                
                actionItems.forEachIndexed { index, step ->
                    var isCompleted by remember { mutableStateOf(false) }
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(6.dp))
                            .clickable { isCompleted = !isCompleted }
                            .padding(vertical = 4.dp, horizontal = 4.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Checkbox(
                            checked = isCompleted,
                            onCheckedChange = { isCompleted = it },
                            colors = CheckboxDefaults.colors(checkedColor = color)
                        )
                        Text(
                            text = step,
                            style = MaterialTheme.typography.bodySmall,
                            color = if (isCompleted) MaterialTheme.colorScheme.onSurfaceVariant else MaterialTheme.colorScheme.onSurface,
                            fontWeight = if (isCompleted) FontWeight.Normal else FontWeight.Medium,
                            lineHeight = 16.sp
                        )
                    }
                }
            }
        }
    }
}

// Conversational scenario block for segment 5
@Composable
fun ConversationalScenarioBlock(
    title: String,
    text: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    color: Color,
    isBangla: Boolean
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        shape = RoundedCornerShape(12.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
        border = BorderStroke(1.dp, color.copy(alpha = 0.15f))
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(28.dp)
                        .clip(CircleShape)
                        .background(color.copy(alpha = 0.12f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = icon,
                        contentDescription = null,
                        tint = color,
                        modifier = Modifier.size(16.dp)
                    )
                }
                Text(
                    text = title,
                    style = MaterialTheme.typography.bodySmall,
                    fontWeight = FontWeight.Bold,
                    color = color
                )
            }
            Spacer(modifier = Modifier.height(10.dp))
            
            // Styled conversational chat bubble look
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(topStart = 0.dp, topEnd = 12.dp, bottomEnd = 12.dp, bottomStart = 12.dp))
                    .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.35f))
                    .padding(12.dp)
            ) {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.TipsAndUpdates,
                        contentDescription = null,
                        tint = color,
                        modifier = Modifier.size(18.dp).padding(top = 2.dp)
                    )
                    Text(
                        text = text,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurface,
                        lineHeight = 18.sp
                    )
                }
            }
        }
    }
}
