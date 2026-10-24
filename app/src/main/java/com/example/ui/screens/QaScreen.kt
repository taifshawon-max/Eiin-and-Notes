package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddComment
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.HelpCenter
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.QuestionAnswer
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.ThumbUp
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.QuestionAnswerEntity
import com.example.ui.theme.AcademicNavy
import com.example.ui.theme.EmeraldTeal
import com.example.ui.theme.GoldenAmber

@Composable
fun QaScreen(
    qaSearchQuery: String,
    onQaSearchChange: (String) -> Unit,
    selectedCategory: String,
    onSelectCategory: (String) -> Unit,
    selectedLevel: String,
    onSelectLevel: (String) -> Unit,
    questions: List<QuestionAnswerEntity>,
    unlockedQaIds: Set<String>,
    remainingQaCredits: Int,
    onSelectQa: (QuestionAnswerEntity) -> Unit,
    onUnlockQa: (QuestionAnswerEntity) -> Unit,
    onAskQuestion: (title: String, details: String, category: String, level: String) -> Unit,
    onNavigateToBilling: () -> Unit
) {
    val categories = listOf("All", "EIIN & Admission", "Physics", "Chemistry", "Higher Math", "Biology", "ICT")
    val levels = listOf("All", "Class 9-10 (SSC)", "Class 11-12 (HSC)", "College Admission")

    var showAskDialog by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .testTag("qa_screen")
    ) {
        // Sticky Header: Q&A Credits Banner, Ask Question CTA & Filters
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(MaterialTheme.colorScheme.surface)
                .padding(horizontal = 16.dp, vertical = 12.dp)
        ) {
            // Q&A Credit Entitlement Banner
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(10.dp))
                    .background(if (remainingQaCredits > 0) Color(0xFFE3F2FD) else Color(0xFFFFEBEE))
                    .padding(horizontal = 12.dp, vertical = 6.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = if (remainingQaCredits > 0) Icons.Default.QuestionAnswer else Icons.Default.Lock,
                        contentDescription = null,
                        tint = if (remainingQaCredits > 0) AcademicNavy else Color.Red,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = if (remainingQaCredits > 0) "$remainingQaCredits Q&A Credits Available (1 Q = $1.01)" else "0 Q&A Credits Left",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = if (remainingQaCredits > 0) AcademicNavy else Color.Red
                    )
                }

                Text(
                    text = "Buy Q&A Pack",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = EmeraldTeal,
                    modifier = Modifier
                        .clickable { onNavigateToBilling() }
                        .testTag("qa_top_buy_pack_btn")
                )
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Prominent Ask a Question CTA + Search Row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                OutlinedTextField(
                    value = qaSearchQuery,
                    onValueChange = onQaSearchChange,
                    placeholder = { Text("Search Q&A...", fontSize = 13.sp) },
                    leadingIcon = {
                        Icon(imageVector = Icons.Default.Search, contentDescription = "Search", tint = AcademicNavy)
                    },
                    trailingIcon = {
                        if (qaSearchQuery.isNotEmpty()) {
                            IconButton(onClick = { onQaSearchChange("") }) {
                                Icon(imageVector = Icons.Default.Clear, contentDescription = "Clear")
                            }
                        }
                    },
                    singleLine = true,
                    shape = RoundedCornerShape(12.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = AcademicNavy,
                        unfocusedBorderColor = MaterialTheme.colorScheme.outlineVariant
                    ),
                    modifier = Modifier
                        .weight(1f)
                        .testTag("qa_search_text_input")
                )

                Button(
                    onClick = {
                        if (remainingQaCredits > 0) {
                            showAskDialog = true
                        } else {
                            onNavigateToBilling()
                        }
                    },
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = AcademicNavy),
                    modifier = Modifier
                        .height(54.dp)
                        .testTag("ask_question_cta_btn")
                ) {
                    Icon(imageVector = Icons.Default.AddComment, contentDescription = null, modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Ask (1 Cr)", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Academic Level Chips
            LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                items(levels) { lvl ->
                    val isSelected = selectedLevel.equals(lvl, ignoreCase = true)
                    FilterChip(
                        selected = isSelected,
                        onClick = { onSelectLevel(lvl) },
                        label = { Text(lvl, fontSize = 11.sp) },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = EmeraldTeal,
                            selectedLabelColor = Color.White
                        ),
                        modifier = Modifier.testTag("qa_filter_level_$lvl")
                    )
                }
            }

            // Category Chips
            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.padding(top = 2.dp)
            ) {
                items(categories) { cat ->
                    val isSelected = selectedCategory.equals(cat, ignoreCase = true)
                    FilterChip(
                        selected = isSelected,
                        onClick = { onSelectCategory(cat) },
                        label = { Text(cat, fontSize = 11.sp) },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = AcademicNavy,
                            selectedLabelColor = Color.White
                        ),
                        modifier = Modifier.testTag("qa_filter_category_$cat")
                    )
                }
            }
        }

        // Q&A Cards List
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items(questions, key = { it.id }) { qa ->
                val isUnlocked = unlockedQaIds.contains(qa.id) || qa.isUserAsked

                Card(
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("qa_card_${qa.id}")
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        // Top Row: Category & Status Badge
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(6.dp))
                                    .background(AcademicNavy.copy(alpha = 0.1f))
                                    .padding(horizontal = 8.dp, vertical = 3.dp)
                            ) {
                                Text(
                                    text = "${qa.category.uppercase()} • ${qa.academicLevel}",
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = AcademicNavy
                                )
                            }

                            Row(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(6.dp))
                                    .background(if (isUnlocked) Color(0xFFE8F5E9) else Color(0xFFFFF3E0))
                                    .padding(horizontal = 8.dp, vertical = 2.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    imageVector = if (isUnlocked) Icons.Default.CheckCircle else Icons.Default.Lock,
                                    contentDescription = null,
                                    tint = if (isUnlocked) EmeraldTeal else GoldenAmber,
                                    modifier = Modifier.size(12.dp)
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    text = when {
                                        qa.isUserAsked -> "MY QUESTION"
                                        isUnlocked -> "UNLOCKED"
                                        else -> "1 Q&A CREDIT"
                                    },
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = if (isUnlocked) EmeraldTeal else GoldenAmber
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(10.dp))

                        Text(
                            text = qa.questionTitle,
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface
                        )

                        Spacer(modifier = Modifier.height(4.dp))

                        Text(
                            text = "Answered by ${qa.answeredBy}",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = EmeraldTeal
                        )

                        Spacer(modifier = Modifier.height(6.dp))

                        Text(
                            text = qa.answerPreview,
                            fontSize = 12.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            maxLines = 2,
                            lineHeight = 17.sp
                        )

                        Spacer(modifier = Modifier.height(12.dp))

                        // Action Buttons
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            OutlinedButton(
                                onClick = { onSelectQa(qa) },
                                shape = RoundedCornerShape(10.dp),
                                modifier = Modifier
                                    .weight(1f)
                                    .testTag("preview_qa_${qa.id}")
                            ) {
                                Icon(imageVector = Icons.Default.Visibility, contentDescription = null, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(6.dp))
                                Text("Preview", fontSize = 12.sp)
                            }

                            if (isUnlocked) {
                                Button(
                                    onClick = { onSelectQa(qa) },
                                    shape = RoundedCornerShape(10.dp),
                                    colors = ButtonDefaults.buttonColors(containerColor = EmeraldTeal),
                                    modifier = Modifier
                                        .weight(1f)
                                        .testTag("view_qa_answer_${qa.id}")
                                ) {
                                    Icon(imageVector = Icons.Default.QuestionAnswer, contentDescription = null, modifier = Modifier.size(16.dp))
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text("View Answer", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                                }
                            } else {
                                Button(
                                    onClick = { onUnlockQa(qa) },
                                    shape = RoundedCornerShape(10.dp),
                                    colors = ButtonDefaults.buttonColors(containerColor = AcademicNavy),
                                    modifier = Modifier
                                        .weight(1f)
                                        .testTag("unlock_qa_btn_${qa.id}")
                                ) {
                                    Icon(imageVector = Icons.Default.Lock, contentDescription = null, modifier = Modifier.size(16.dp))
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text("Unlock (1 Cr)", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    // Ask New Question Modal Dialog
    if (showAskDialog) {
        var questionTitle by remember { mutableStateOf("") }
        var questionDetails by remember { mutableStateOf("") }
        var category by remember { mutableStateOf("EIIN & Admission") }
        var level by remember { mutableStateOf("Class 11-12 (HSC)") }

        AlertDialog(
            onDismissRequest = { showAskDialog = false },
            title = {
                Text(
                    text = "Ask a New Question (1 Q&A Credit)",
                    fontWeight = FontWeight.Bold,
                    fontSize = 17.sp
                )
            },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text(
                        text = "Ask any Bangladeshi school EIIN, admission, or academic subject question. Uses 1 Q&A Credit ($remainingQaCredits remaining).",
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    OutlinedTextField(
                        value = questionTitle,
                        onValueChange = { questionTitle = it },
                        label = { Text("Your Question Title") },
                        placeholder = { Text("e.g., How to calculate road banking angle with friction?") },
                        modifier = Modifier.fillMaxWidth().testTag("ask_question_title_input")
                    )
                    OutlinedTextField(
                        value = questionDetails,
                        onValueChange = { questionDetails = it },
                        label = { Text("Details / Context (Optional)") },
                        modifier = Modifier.fillMaxWidth().testTag("ask_question_details_input")
                    )
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        OutlinedTextField(
                            value = category,
                            onValueChange = { category = it },
                            label = { Text("Subject / Topic") },
                            modifier = Modifier.weight(1f)
                        )
                        OutlinedTextField(
                            value = level,
                            onValueChange = { level = it },
                            label = { Text("Level") },
                            modifier = Modifier.weight(1f)
                        )
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        if (questionTitle.isNotBlank()) {
                            onAskQuestion(questionTitle, questionDetails, category, level)
                            showAskDialog = false
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = AcademicNavy),
                    modifier = Modifier.testTag("submit_question_btn")
                ) {
                    Text("Submit Question (1 Credit)")
                }
            },
            dismissButton = {
                TextButton(onClick = { showAskDialog = false }) {
                    Text("Cancel")
                }
            }
        )
    }
}
