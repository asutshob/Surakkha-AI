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
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.database.DisasterChecklist
import com.example.ui.theme.AmberWarning
import com.example.ui.theme.BluePrimary
import com.example.ui.theme.GreenPrimary
import com.example.ui.theme.RedAlert
import com.example.ui.viewmodel.SurakkhaViewModel
import com.example.ui.viewmodel.UiState

@OptIn(ExperimentalLayoutApi::class, ExperimentalMaterial3Api::class)
@Composable
fun DisasterGuardianScreen(
    viewModel: SurakkhaViewModel,
    checklistItems: List<DisasterChecklist>,
    isBangla: Boolean,
    modifier: Modifier = Modifier
) {
    var selectedLocation by remember { mutableStateOf("সিলেট (Sylhet)") }
    var selectedType by remember { mutableStateOf("Flood") } // Flood, Cyclone, Lightning, RiverErosion
    
    val alertState by viewModel.disasterAlertState.collectAsState()
    
    // Custom Checkbox Input State
    var newGoalText by remember { mutableStateOf("") }

    val locations = listOf(
        "সিলেট (Sylhet)", "নোয়াখালী (Noakhali)", "কক্সবাজার (Cox's Bazar)", 
        "কুড়িগ্রাম (Kurigram)", "বান্দরবান (Bandarban)", "সাতক্ষীরা (Satkhira)"
    )

    val disasterTypes = listOf(
        Pair("Flood", if (isBangla) "বন্যা (Flood)" else "Floods"),
        Pair("Cyclone", if (isBangla) "ঘূর্ণিঝড় (Cyclone)" else "Cyclones"),
        Pair("Lightning", if (isBangla) "বজ্রপাত (Lightning)" else "Lightning"),
        Pair("RiverErosion", if (isBangla) "নদী ভাঙ্গন (Erosion)" else "Erosion")
    )

    // Fallback safe shelter dictionary depending on chosen location + type
    val simulatedShelters = when {
        selectedLocation.contains("সিলেট") -> listOf(
            Pair("সিলেট সদর সরকারি প্রাইমারী স্কুল আশ্রয়কেন্দ্র", "৩৫০ মিটার দূরে"),
            Pair("শাহজালাল বিজ্ঞান ও প্রযুক্তি বিশ্ববিদ্যালয় ভবন-বি", "১.২ কিমি দূরে")
        )
        selectedLocation.contains("নোয়াখালী") -> listOf(
            Pair("নোয়াখালী ডিগ্রি কলেজ সাইক্লোন শেল্টার সাইট", "৬০০ মিটার দূরে"),
            Pair("চর জাব্বার বহুমুখী আশ্রয় কেন্দ্র", "১.৮ কিমি দূরে")
        )
        selectedLocation.contains("কক্সবাজার") -> listOf(
            Pair("কক্সবাজার রেড ক্রিসেন্ট মা ও শিশু কেন্দ্র", "৪৫০ মিটার দূরে"),
            Pair("উখিয়া কুতুপালং উচ্চ বিদ্যালয় আশ্রয়স্থল", "২.০ কিমি দূরে")
        )
        else -> listOf(
            Pair("উপজেলা পরিষদ সরকারি সেফহাউস মিলনায়তন", "৭০০ মিটার দূরে"),
            Pair("স্থানীয় ইউনিয়ন কমপ্লেক্স ভবন-১", "১.৫ কিমি দূরে")
        )
    }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .testTag("disaster_guardian_screen"),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Section 1: Intro Header
        item {
            Card(
                colors = CardDefaults.cardColors(
                    containerColor = BluePrimary.copy(alpha = 0.08F)
                ),
                shape = RoundedCornerShape(12.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.Thunderstorm,
                            contentDescription = null,
                            tint = BluePrimary,
                            modifier = Modifier.size(24.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = if (isBangla) "দুর্যোগ অভিভাবক (Disaster Guardian)" else "Disaster Guardian",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = BluePrimary
                        )
                    }
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = if (isBangla)
                            "আপনার বর্তমান অবস্থান ও দুর্যোগ ধরণ নির্বাচন করে কৃত্তিম বুদ্ধিমত্তার মাধ্যমে লাইভ ঝুঁকি নিরুপণ করুন এবং অফলাইন জীবন রক্ষাকারী গাইডস দেখুন।"
                            else "Select your local district and risk type to generate live AI risk audits and read safety directions offline.",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }

        // Section 2: Input Controls Card (Location + Disaster selection)
        item {
            Card(
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = if (isBangla) "১. অবস্থান ও দুর্যোগের ধরণ নির্বাচন করুন" else "1. Select District & Risk Type",
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(12.dp))

                    // Location Picker Grid simulated with Scroll chips
                    Text(
                        text = if (isBangla) "জেলা বা উপজেলা:" else "District/Sub-district:",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    FlowRow(
                        horizontalArrangement = Arrangement.spacedBy(6.dp),
                        verticalArrangement = Arrangement.spacedBy(6.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        locations.forEach { loc ->
                            FilterChip(
                                selected = selectedLocation == loc,
                                onClick = { selectedLocation = loc },
                                label = { Text(loc, fontSize = 11.sp) },
                                colors = FilterChipDefaults.filterChipColors(
                                    selectedContainerColor = BluePrimary,
                                    selectedLabelColor = Color.White
                                )
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    // Disaster Picker Grid with icons
                    Text(
                        text = if (isBangla) "দুর্যোগের ধরণ:" else "Disaster Type:",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    FlowRow(
                        horizontalArrangement = Arrangement.spacedBy(6.dp),
                        verticalArrangement = Arrangement.spacedBy(6.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        disasterTypes.forEach { type ->
                            FilterChip(
                                selected = selectedType == type.first,
                                onClick = { selectedType = type.first },
                                label = { Text(type.second, fontSize = 11.sp) },
                                colors = FilterChipDefaults.filterChipColors(
                                    selectedContainerColor = BluePrimary,
                                    selectedLabelColor = Color.White
                                )
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // Perform AI Warning Assessment button
                    Button(
                        onClick = {
                            viewModel.loadDisasterAlert(selectedType, selectedLocation)
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(48.dp)
                            .testTag("load_disaster_button"),
                        colors = ButtonDefaults.buttonColors(containerColor = BluePrimary)
                    ) {
                        Icon(imageVector = Icons.Default.Cyclone, contentDescription = null)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = if (isBangla) "এআই সতর্কবার্তা জেনারেট করুন" else "Generate AI Emergency Warning",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }

        // Section 3: AI Warning Panel (Gemini Content)
        item {
            when (alertState) {
                is UiState.Loading -> {
                    Card(modifier = Modifier.fillMaxWidth()) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(24.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            CircularProgressIndicator(color = BluePrimary)
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = if (isBangla) "সুরক্ষা সিস্টেম আবহাওয়া ডাটা বিশ্লেষণ করছে..." else "Analyzing weather risk profiles...",
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }
                is UiState.Success -> {
                    val alertText = (alertState as UiState.Success<String>).data
                    Card(
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                        border = CardDefaults.outlinedCardBorder().copy(
                            brush = androidx.compose.ui.graphics.SolidColor(AmberWarning.copy(alpha = 0.5f))
                        ),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(
                                        imageVector = Icons.Default.OnlinePrediction,
                                        contentDescription = null,
                                        tint = AmberWarning
                                    )
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text(
                                        text = if (isBangla) "এআই ঝুঁকি বিশ্লেষণ" else "AI Risk Assessment Report",
                                        fontWeight = FontWeight.Bold,
                                        color = AmberWarning
                                    )
                                }
                                Box(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(4.dp))
                                        .background(AmberWarning.copy(alpha = 0.15F))
                                        .padding(horizontal = 6.dp, vertical = 2.dp)
                                ) {
                                    Text(
                                        text = if (isBangla) "সচল পূর্বাভাস" else "Live Report",
                                        color = AmberWarning,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 11.sp
                                    )
                                }
                            }
                            Spacer(modifier = Modifier.height(12.dp))
                            Text(
                                text = alertText,
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurface,
                                lineHeight = 18.sp
                            )
                        }
                    }
                }
                else -> {
                    // Informative Prompt to generate
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5F))
                    ) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = if (isBangla) "আপনার এলাকার সঠিক ঝুঁকির মাত্রা জানতে উপরের লাল বাটনে চাপ দিন।" 
                                       else "Tap the blue button above to request satellite AI warnings.",
                                fontSize = 12.sp,
                                textAlign = TextAlign.Center,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }
            }
        }

        // Section 4: Safe Shelters in Selected District
        item {
            Text(
                text = (if (isBangla) "নিকটস্থ আশ্রয়স্থল: " else "Nearest Safe Shelters: ") + selectedLocation,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onBackground
            )
            Spacer(modifier = Modifier.height(4.dp))
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                simulatedShelters.forEach { shelter ->
                    Card(
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(12.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Row(
                                modifier = Modifier.weight(0.75F),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(36.dp)
                                        .clip(CircleShape)
                                        .background(GreenPrimary.copy(alpha = 0.1F)),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Directions,
                                        contentDescription = null,
                                        tint = GreenPrimary,
                                        modifier = Modifier.size(18.dp)
                                    )
                                }
                                Spacer(modifier = Modifier.width(12.dp))
                                Column {
                                    Text(
                                        text = shelter.first,
                                        style = MaterialTheme.typography.bodySmall,
                                        fontWeight = FontWeight.Bold,
                                        color = MaterialTheme.colorScheme.onSurface,
                                        maxLines = 1,
                                        overflow = TextOverflow.Ellipsis
                                    )
                                    Text(
                                        text = if (isBangla) "সহজ নেভিগেশন ম্যাপ গাইড সক্রিয়" else "Satellite coordinates matches",
                                        style = MaterialTheme.typography.labelSmall,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                            }
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(4.dp))
                                    .background(MaterialTheme.colorScheme.primaryContainer)
                                    .padding(horizontal = 6.dp, vertical = 2.dp)
                            ) {
                                Text(
                                    text = shelter.second,
                                    color = MaterialTheme.colorScheme.primary,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    }
                }
            }
        }

        // Section 5: Personalized Disaster Checklist (linked database)
        item {
            Text(
                text = if (isBangla) "ব্যক্তিগত জরুরি প্রস্তুতি চেকলিস্ট" else "My Emergency Action Checklist",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onBackground
            )
            Spacer(modifier = Modifier.height(4.dp))
            Card(
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    
                    // Display checklist items
                    val filteredChecks = checklistItems.filter { 
                        it.category == selectedType || it.category == "General" 
                    }
                    
                    if (filteredChecks.isEmpty()) {
                        Text(
                            text = if (isBangla) "এই বিভাগে কোনো চেকলিস্ট নেই।" else "No items configured.",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    } else {
                        filteredChecks.forEach { check ->
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 4.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Checkbox(
                                    checked = check.isChecked,
                                    onCheckedChange = { viewModel.toggleChecklistItem(check) },
                                    modifier = Modifier.testTag("checklist_checkbox_${check.id}")
                                )
                                Text(
                                    text = check.itemText,
                                    style = MaterialTheme.typography.bodySmall,
                                    color = if (check.isChecked) MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6F) else MaterialTheme.colorScheme.onSurface,
                                    modifier = Modifier.weight(1F)
                                )
                                IconButton(
                                    onClick = { viewModel.deleteChecklistItem(check) },
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

                    Spacer(modifier = Modifier.height(12.dp))
                    HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.4F))
                    Spacer(modifier = Modifier.height(12.dp))

                    // Add Custom Task Input
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        OutlinedTextField(
                            value = newGoalText,
                            onValueChange = { newGoalText = it },
                            placeholder = { Text(if (isBangla) "নতুন কাজের বিবরণ লিখুন (যেমন: রেডিও জোগাড় করা)" else "Add custom checklist action...", fontSize = 11.sp) },
                            modifier = Modifier
                                .weight(1F)
                                .height(50.dp)
                                .testTag("checklist_input"),
                            singleLine = true,
                            textStyle = TextStyle(fontSize = 12.sp)
                        )
                        Button(
                            onClick = {
                                if (newGoalText.trim().isNotEmpty()) {
                                    viewModel.addNewChecklistItem(newGoalText.trim(), selectedType)
                                    newGoalText = ""
                                }
                            },
                            modifier = Modifier.height(50.dp).testTag("add_checklist_button"),
                            colors = ButtonDefaults.buttonColors(containerColor = GreenPrimary)
                        ) {
                            Text(text = if (isBangla) "যুক্ত করুন" else "Add")
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
