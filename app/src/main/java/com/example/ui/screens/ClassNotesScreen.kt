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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.MenuBook
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.PictureAsPdf
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.Visibility
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
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.ClassNoteEntity
import com.example.ui.theme.AcademicNavy
import com.example.ui.theme.EmeraldTeal
import com.example.ui.theme.GoldenAmber

@Composable
fun ClassNotesScreen(
    notesSearchQuery: String,
    onNotesSearchChange: (String) -> Unit,
    selectedSubject: String,
    onSelectSubject: (String) -> Unit,
    selectedLevel: String,
    onSelectLevel: (String) -> Unit,
    notes: List<ClassNoteEntity>,
    unlockedNoteIds: Set<String>,
    remainingNoteCredits: Int,
    onSelectNote: (ClassNoteEntity) -> Unit,
    onUnlockNote: (ClassNoteEntity) -> Unit,
    onNavigateToBilling: () -> Unit
) {
    val subjects = listOf("All", "Physics", "Chemistry", "Higher Math", "Biology", "ICT")
    val levels = listOf("All", "Class 9-10 (SSC)", "Class 11-12 (HSC)")

    Column(
        modifier = Modifier
            .fillMaxSize()
            .testTag("class_notes_screen")
    ) {
        // Sticky Header: Credit indicator & Search
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(MaterialTheme.colorScheme.surface)
                .padding(horizontal = 16.dp, vertical = 12.dp)
        ) {
            // Note Credits banner
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(10.dp))
                    .background(if (remainingNoteCredits > 0) Color(0xFFE0F2F1) else Color(0xFFFFEBEE))
                    .padding(horizontal = 12.dp, vertical = 6.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = if (remainingNoteCredits > 0) Icons.AutoMirrored.Filled.MenuBook else Icons.Default.Lock,
                        contentDescription = null,
                        tint = if (remainingNoteCredits > 0) EmeraldTeal else Color.Red,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = if (remainingNoteCredits > 0) "$remainingNoteCredits Note Credits Available (1 Note = $0.99)" else "0 Note Credits Left",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = if (remainingNoteCredits > 0) EmeraldTeal else Color.Red
                    )
                }

                Text(
                    text = "Get Notes Pack ($0.99+)",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = EmeraldTeal,
                    modifier = Modifier
                        .clickable { onNavigateToBilling() }
                        .testTag("notes_top_buy_credits_btn")
                )
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Search Input
            OutlinedTextField(
                value = notesSearchQuery,
                onValueChange = onNotesSearchChange,
                placeholder = { Text("Search by topic, chapter, author...", fontSize = 13.sp) },
                leadingIcon = {
                    Icon(imageVector = Icons.Default.Search, contentDescription = "Search", tint = EmeraldTeal)
                },
                trailingIcon = {
                    if (notesSearchQuery.isNotEmpty()) {
                        IconButton(onClick = { onNotesSearchChange("") }) {
                            Icon(imageVector = Icons.Default.Clear, contentDescription = "Clear")
                        }
                    }
                },
                singleLine = true,
                shape = RoundedCornerShape(14.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = EmeraldTeal,
                    unfocusedBorderColor = MaterialTheme.colorScheme.outlineVariant
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("notes_search_text_input")
            )

            Spacer(modifier = Modifier.height(10.dp))

            // Academic Level selector
            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(levels) { lvl ->
                    val isSelected = selectedLevel.equals(lvl, ignoreCase = true)
                    FilterChip(
                        selected = isSelected,
                        onClick = { onSelectLevel(lvl) },
                        label = { Text(lvl, fontSize = 12.sp) },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = EmeraldTeal,
                            selectedLabelColor = Color.White
                        ),
                        modifier = Modifier.testTag("filter_level_$lvl")
                    )
                }
            }

            // Subject Filters
            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.padding(top = 4.dp)
            ) {
                items(subjects) { subj ->
                    val isSelected = selectedSubject.equals(subj, ignoreCase = true)
                    FilterChip(
                        selected = isSelected,
                        onClick = { onSelectSubject(subj) },
                        label = { Text(subj, fontSize = 11.sp) },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = AcademicNavy,
                            selectedLabelColor = Color.White
                        ),
                        modifier = Modifier.testTag("filter_subject_$subj")
                    )
                }
            }
        }

        // Notes List
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            items(notes, key = { it.id }) { note ->
                val isUnlocked = unlockedNoteIds.contains(note.id)

                Card(
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("note_card_${note.id}")
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        // Subject & Badge
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Box(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(6.dp))
                                        .background(AcademicNavy.copy(alpha = 0.1f))
                                        .padding(horizontal = 8.dp, vertical = 3.dp)
                                ) {
                                    Text(
                                        text = "${note.subject.uppercase()} • ${note.courseCode}",
                                        fontSize = 10.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = AcademicNavy
                                    )
                                }
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
                                    text = if (isUnlocked) "UNLOCKED" else "1 NOTE CREDIT",
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = if (isUnlocked) EmeraldTeal else GoldenAmber
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(10.dp))

                        // Title
                        Text(
                            text = note.title,
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface
                        )

                        Spacer(modifier = Modifier.height(4.dp))

                        Text(
                            text = "By ${note.author}",
                            fontSize = 12.sp,
                            color = EmeraldTeal,
                            fontWeight = FontWeight.SemiBold
                        )

                        Spacer(modifier = Modifier.height(6.dp))

                        Text(
                            text = note.description,
                            fontSize = 12.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            maxLines = 2,
                            lineHeight = 17.sp
                        )

                        Spacer(modifier = Modifier.height(12.dp))

                        // Meta details
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(imageVector = Icons.Default.PictureAsPdf, contentDescription = null, tint = Color.Red, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(text = "${note.totalPages} Pages", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                Spacer(modifier = Modifier.width(12.dp))
                                Icon(imageVector = Icons.Default.Star, contentDescription = null, tint = GoldenAmber, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(text = "${note.rating}", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSurface)
                            }

                            Text(
                                text = note.academicLevel,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Medium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }

                        Spacer(modifier = Modifier.height(14.dp))

                        // Action Buttons
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            OutlinedButton(
                                onClick = { onSelectNote(note) },
                                shape = RoundedCornerShape(10.dp),
                                modifier = Modifier
                                    .weight(1f)
                                    .testTag("preview_note_${note.id}")
                            ) {
                                Icon(imageVector = Icons.Default.Visibility, contentDescription = null, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(6.dp))
                                Text("Sample Preview", fontSize = 12.sp)
                            }

                            if (isUnlocked) {
                                Button(
                                    onClick = { onSelectNote(note) },
                                    shape = RoundedCornerShape(10.dp),
                                    colors = ButtonDefaults.buttonColors(containerColor = EmeraldTeal),
                                    modifier = Modifier
                                        .weight(1f)
                                        .testTag("read_note_${note.id}")
                                ) {
                                    Icon(imageVector = Icons.AutoMirrored.Filled.MenuBook, contentDescription = null, modifier = Modifier.size(16.dp))
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text("Read Full Note", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                                }
                            } else {
                                Button(
                                    onClick = { onUnlockNote(note) },
                                    shape = RoundedCornerShape(10.dp),
                                    colors = ButtonDefaults.buttonColors(containerColor = AcademicNavy),
                                    modifier = Modifier
                                        .weight(1f)
                                        .testTag("unlock_note_btn_${note.id}")
                                ) {
                                    Icon(imageVector = Icons.Default.Lock, contentDescription = null, modifier = Modifier.size(16.dp))
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text("Unlock (1 Credit)", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
