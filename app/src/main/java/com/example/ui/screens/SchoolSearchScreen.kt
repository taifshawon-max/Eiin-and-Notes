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
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.School
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.SearchOff
import androidx.compose.material.icons.filled.Verified
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.SchoolEntity
import com.example.ui.theme.AcademicNavy
import com.example.ui.theme.EmeraldTeal
import com.example.ui.theme.GoldenAmber

@Composable
fun SchoolSearchScreen(
    searchQuery: String,
    onSearchQueryChange: (String) -> Unit,
    selectedDivision: String,
    onSelectDivision: (String) -> Unit,
    selectedType: String,
    onSelectType: (String) -> Unit,
    searchResults: List<SchoolEntity>,
    unlockedSchoolIds: Set<String>,
    remainingCredits: Int,
    onSelectSchool: (SchoolEntity) -> Unit,
    onCopyEiin: (String, String) -> Unit,
    onNavigateToBilling: () -> Unit
) {
    val divisions = listOf("All", "Dhaka", "Chattogram", "Rajshahi", "Khulna", "Barishal", "Sylhet", "Rangpur", "Mymensingh")
    val types = listOf("All", "College", "High School", "School & College", "Cadet College")

    Column(
        modifier = Modifier
            .fillMaxSize()
            .testTag("school_search_screen")
    ) {
        // Sticky Search & Filter Header
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(MaterialTheme.colorScheme.surface)
                .padding(horizontal = 16.dp, vertical = 12.dp)
        ) {
            // Credit entitlement indicator banner
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(10.dp))
                    .background(if (remainingCredits > 0) Color(0xFFE8F5E9) else Color(0xFFFFEBEE))
                    .padding(horizontal = 12.dp, vertical = 6.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = if (remainingCredits > 0) Icons.Default.Verified else Icons.Default.Lock,
                        contentDescription = null,
                        tint = if (remainingCredits > 0) EmeraldTeal else Color.Red,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = if (remainingCredits > 0) "$remainingCredits School Credits Available" else "0 Credits Remaining",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = if (remainingCredits > 0) EmeraldTeal else Color.Red
                    )
                }

                if (remainingCredits == 0) {
                    Text(
                        text = "Buy Credits",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = AcademicNavy,
                        modifier = Modifier
                            .clickable { onNavigateToBilling() }
                            .testTag("search_top_buy_credits_btn")
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Search Text Field
            OutlinedTextField(
                value = searchQuery,
                onValueChange = onSearchQueryChange,
                placeholder = { Text("Search by name or 6-digit EIIN (e.g. 108274)...", fontSize = 13.sp) },
                leadingIcon = {
                    Icon(imageVector = Icons.Default.Search, contentDescription = "Search", tint = AcademicNavy)
                },
                trailingIcon = {
                    if (searchQuery.isNotEmpty()) {
                        IconButton(onClick = { onSearchQueryChange("") }) {
                            Icon(imageVector = Icons.Default.Clear, contentDescription = "Clear")
                        }
                    }
                },
                singleLine = true,
                shape = RoundedCornerShape(14.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = AcademicNavy,
                    unfocusedBorderColor = MaterialTheme.colorScheme.outlineVariant
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("school_search_text_input")
            )

            Spacer(modifier = Modifier.height(10.dp))

            // Division Filters Row
            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(divisions) { div ->
                    val isSelected = selectedDivision.equals(div, ignoreCase = true)
                    FilterChip(
                        selected = isSelected,
                        onClick = { onSelectDivision(div) },
                        label = { Text(div, fontSize = 12.sp) },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = AcademicNavy,
                            selectedLabelColor = Color.White
                        ),
                        modifier = Modifier.testTag("filter_division_$div")
                    )
                }
            }

            // Institution Type Filters Row
            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.padding(top = 4.dp)
            ) {
                items(types) { t ->
                    val isSelected = selectedType.equals(t, ignoreCase = true)
                    FilterChip(
                        selected = isSelected,
                        onClick = { onSelectType(t) },
                        label = { Text(t, fontSize = 11.sp) },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = EmeraldTeal,
                            selectedLabelColor = Color.White
                        ),
                        modifier = Modifier.testTag("filter_type_$t")
                    )
                }
            }
        }

        // Result count header
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 6.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "${searchResults.size} Verified Institutes Found",
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            if (selectedDivision != "All" || selectedType != "All") {
                TextButton(onClick = {
                    onSelectDivision("All")
                    onSelectType("All")
                }) {
                    Text("Clear Filters", fontSize = 11.sp, color = AcademicNavy)
                }
            }
        }

        // Results List / Empty State
        if (searchResults.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(32.dp),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(
                        imageVector = Icons.Default.SearchOff,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.size(64.dp)
                    )
                    Spacer(modifier = Modifier.height(14.dp))
                    Text(
                        text = "No schools found for '$searchQuery'",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = "Try checking the spelling, searching by 6-digit EIIN number, or resetting the division/type filters.",
                        fontSize = 13.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.padding(horizontal = 24.dp)
                    )
                }
            }
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                items(searchResults, key = { it.id }) { school ->
                    val isUnlocked = unlockedSchoolIds.contains(school.id)

                    Card(
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { onSelectSchool(school) }
                            .testTag("school_card_${school.id}")
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            // Top Row: Division & Status Badge
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Text(
                                        text = "${school.division.uppercase()} • ${school.district}",
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = EmeraldTeal,
                                        letterSpacing = 0.5.sp
                                    )
                                }

                                Row(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(6.dp))
                                        .background(if (isUnlocked) Color(0xFFE8F5E9) else Color(0xFFF5F5F5))
                                        .padding(horizontal = 8.dp, vertical = 2.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Icon(
                                        imageVector = if (isUnlocked) Icons.Default.CheckCircle else Icons.Default.Lock,
                                        contentDescription = null,
                                        tint = if (isUnlocked) EmeraldTeal else Color.Gray,
                                        modifier = Modifier.size(12.dp)
                                    )
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text(
                                        text = if (isUnlocked) "UNLOCKED" else "LOCKED",
                                        fontSize = 10.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = if (isUnlocked) EmeraldTeal else Color.Gray
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.height(8.dp))

                            // School Name
                            Text(
                                text = school.name,
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            if (school.nameBn.isNotBlank()) {
                                Text(
                                    text = school.nameBn,
                                    fontSize = 12.sp,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                                    modifier = Modifier.padding(top = 2.dp)
                                )
                            }

                            Spacer(modifier = Modifier.height(12.dp))

                            // EIIN Banner inside Card
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(10.dp))
                                    .background(if (isUnlocked) Color(0xFFE3F2FD) else MaterialTheme.colorScheme.surfaceVariant)
                                    .padding(horizontal = 12.dp, vertical = 8.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column {
                                    Text(
                                        text = "EIIN NUMBER",
                                        fontSize = 9.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = if (isUnlocked) AcademicNavy else MaterialTheme.colorScheme.onSurfaceVariant,
                                        letterSpacing = 0.5.sp
                                    )
                                    Text(
                                        text = if (isUnlocked) school.eiin else "•••••• (Locked)",
                                        fontSize = 16.sp,
                                        fontWeight = FontWeight.ExtraBold,
                                        letterSpacing = if (isUnlocked) 2.sp else 1.sp,
                                        color = if (isUnlocked) AcademicNavy else Color.Gray
                                    )
                                }

                                if (isUnlocked) {
                                    IconButton(
                                        onClick = { onCopyEiin(school.eiin, school.name) },
                                        modifier = Modifier.size(32.dp).testTag("copy_eiin_${school.id}")
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.ContentCopy,
                                            contentDescription = "Copy EIIN",
                                            tint = AcademicNavy,
                                            modifier = Modifier.size(18.dp)
                                        )
                                    }
                                } else {
                                    Text(
                                        text = "Tap to unlock",
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.SemiBold,
                                        color = AcademicNavy
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.height(10.dp))

                            // Bottom Tags: Type & Board
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text(
                                    text = "${school.institutionType} • ${school.upazila}",
                                    fontSize = 11.sp,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                                Text(
                                    text = "${school.board} Board",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Medium,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}
