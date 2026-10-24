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
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AdminPanelSettings
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Restore
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
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
import com.example.data.model.ClassNoteEntity
import com.example.data.model.PurchaseTransactionEntity
import com.example.data.model.QuestionAnswerEntity
import com.example.data.model.SchoolEntity
import com.example.data.model.UserEntitlementEntity
import com.example.ui.theme.AcademicNavy
import com.example.ui.theme.EmeraldTeal
import com.example.ui.theme.GoldenAmber
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun ProfileScreen(
    entitlements: UserEntitlementEntity,
    unlockedSchoolIds: Set<String>,
    unlockedNoteIds: Set<String>,
    unlockedQaIds: Set<String>,
    allSchools: List<SchoolEntity>,
    allNotes: List<ClassNoteEntity>,
    allQuestions: List<QuestionAnswerEntity>,
    transactions: List<PurchaseTransactionEntity>,
    onSelectSchool: (SchoolEntity) -> Unit,
    onSelectNote: (ClassNoteEntity) -> Unit,
    onSelectQa: (QuestionAnswerEntity) -> Unit,
    onRestorePurchases: () -> Unit,
    onNavigateToAdmin: () -> Unit
) {
    var selectedProfileTab by remember { mutableIntStateOf(0) }
    var showHelpDialog by remember { mutableStateOf(false) }
    var showPrivacyDialog by remember { mutableStateOf(false) }
    var showTermsDialog by remember { mutableStateOf(false) }
    var showDeleteAccountDialog by remember { mutableStateOf(false) }

    val dateFormat = remember { SimpleDateFormat("dd MMM yyyy, hh:mm a", Locale.US) }

    val unlockedSchoolsList = allSchools.filter { unlockedSchoolIds.contains(it.id) }
    val unlockedNotesList = allNotes.filter { unlockedNoteIds.contains(it.id) }
    val unlockedQaList = allQuestions.filter { unlockedQaIds.contains(it.id) || it.isUserAsked }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .testTag("profile_screen"),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // User Profile Card
        item {
            Card(
                shape = RoundedCornerShape(18.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(18.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(56.dp)
                                .clip(CircleShape)
                                .background(AcademicNavy),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(imageVector = Icons.Default.Person, contentDescription = null, tint = Color.White, modifier = Modifier.size(32.dp))
                        }
                        Spacer(modifier = Modifier.width(14.dp))
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = "Taif Shawon",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            Text(
                                text = "taifshawon@gmail.com",
                                fontSize = 12.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Row(
                                modifier = Modifier.padding(top = 4.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(8.dp)
                                        .clip(CircleShape)
                                        .background(EmeraldTeal)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = "Google Play Verified • Bangladesh",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Medium,
                                    color = EmeraldTeal
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))
                    Divider()
                    Spacer(modifier = Modifier.height(14.dp))

                    // Credit summary row
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceEvenly
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(
                                text = "${entitlements.schoolCreditsRemaining}",
                                fontSize = 20.sp,
                                fontWeight = FontWeight.Bold,
                                color = AcademicNavy
                            )
                            Text(
                                text = "School Credits",
                                fontSize = 11.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                        Box(
                            modifier = Modifier
                                .height(32.dp)
                                .width(1.dp)
                                .background(MaterialTheme.colorScheme.outlineVariant)
                        )
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(
                                text = "${entitlements.noteCreditsRemaining}",
                                fontSize = 20.sp,
                                fontWeight = FontWeight.Bold,
                                color = EmeraldTeal
                            )
                            Text(
                                text = "Note Credits",
                                fontSize = 11.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                        Box(
                            modifier = Modifier
                                .height(32.dp)
                                .width(1.dp)
                                .background(MaterialTheme.colorScheme.outlineVariant)
                        )
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(
                                text = "${entitlements.qaCreditsRemaining}",
                                fontSize = 20.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF01875F)
                            )
                            Text(
                                text = "Q&A Credits",
                                fontSize = 11.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }
            }
        }

        // Segmented Tabs: 0 = Unlocked Items, 1 = Transaction History
        item {
            val totalLibraryCount = unlockedSchoolsList.size + unlockedNotesList.size + unlockedQaList.size
            TabRow(
                selectedTabIndex = selectedProfileTab,
                containerColor = MaterialTheme.colorScheme.surface,
                contentColor = AcademicNavy
            ) {
                Tab(
                    selected = selectedProfileTab == 0,
                    onClick = { selectedProfileTab = 0 },
                    text = { Text("My Library ($totalLibraryCount)", fontWeight = FontWeight.Bold) },
                    modifier = Modifier.testTag("profile_tab_library")
                )
                Tab(
                    selected = selectedProfileTab == 1,
                    onClick = { selectedProfileTab = 1 },
                    text = { Text("Google Play Orders (${transactions.size})", fontWeight = FontWeight.Bold) },
                    modifier = Modifier.testTag("profile_tab_transactions")
                )
            }
        }

        if (selectedProfileTab == 0) {
            // Unlocked Schools Section
            item {
                Text(
                    text = "Unlocked Schools (${unlockedSchoolsList.size})",
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold,
                    color = AcademicNavy
                )
            }

            if (unlockedSchoolsList.isEmpty()) {
                item {
                    Text(
                        text = "No schools unlocked yet. Search and unlock an institute to view its permanent EIIN record here.",
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.padding(vertical = 4.dp)
                    )
                }
            } else {
                items(unlockedSchoolsList) { school ->
                    Card(
                        shape = RoundedCornerShape(12.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { onSelectSchool(school) }
                            .testTag("unlocked_school_item_${school.id}")
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(12.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text(text = school.name, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                                Text(text = "${school.district} • ${school.board} Board", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            }
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(Color(0xFFE8F5E9))
                                    .padding(horizontal = 8.dp, vertical = 4.dp)
                            ) {
                                Text(text = "EIIN ${school.eiin}", fontSize = 12.sp, fontWeight = FontWeight.ExtraBold, color = EmeraldTeal)
                            }
                        }
                    }
                }
            }

            // Unlocked Notes Section
            item {
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "Unlocked Class Notes (${unlockedNotesList.size})",
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold,
                    color = EmeraldTeal
                )
            }

            if (unlockedNotesList.isEmpty()) {
                item {
                    Text(
                        text = "No class notes unlocked yet. Browse notes marketplace to unlock academic guides.",
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.padding(vertical = 4.dp)
                    )
                }
            } else {
                items(unlockedNotesList) { note ->
                    Card(
                        shape = RoundedCornerShape(12.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { onSelectNote(note) }
                            .testTag("unlocked_note_item_${note.id}")
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(12.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text(text = note.title, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                                Text(text = "${note.subject} • ${note.totalPages} Pages PDF", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            }
                            Button(
                                onClick = { onSelectNote(note) },
                                shape = RoundedCornerShape(8.dp),
                                colors = ButtonDefaults.buttonColors(containerColor = EmeraldTeal),
                                contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp)
                            ) {
                                Text("Read", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }
            }

            // Unlocked Q&A Section
            item {
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "My Q&A Questions & Unlocked Answers (${unlockedQaList.size})",
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF01875F)
                )
            }

            if (unlockedQaList.isEmpty()) {
                item {
                    Text(
                        text = "No Q&A solutions unlocked yet. Ask a question or unlock verified board answers in the Q&A tab.",
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.padding(vertical = 4.dp)
                    )
                }
            } else {
                items(unlockedQaList) { qa ->
                    Card(
                        shape = RoundedCornerShape(12.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { onSelectQa(qa) }
                            .testTag("unlocked_qa_item_${qa.id}")
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(12.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text(text = qa.questionTitle, fontWeight = FontWeight.Bold, fontSize = 13.sp, maxLines = 2)
                                Text(text = "${qa.category} • ${qa.academicLevel}", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            }
                            Button(
                                onClick = { onSelectQa(qa) },
                                shape = RoundedCornerShape(8.dp),
                                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF01875F)),
                                contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp)
                            ) {
                                Text("Answer", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }
            }
        } else {
            // Transactions History
            if (transactions.isEmpty()) {
                item {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(24.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "No Google Play purchase transactions recorded yet.",
                            fontSize = 13.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            } else {
                items(transactions) { txn ->
                    val label = when (txn.productType) {
                        "SCHOOL_CREDITS" -> "${txn.quantity} School Credits"
                        "NOTE_CREDITS" -> "${txn.quantity} Class Notes"
                        "QA_CREDITS" -> "${txn.quantity} Q&A Questions Pack"
                        else -> "${txn.quantity} Credits"
                    }
                    Card(
                        shape = RoundedCornerShape(12.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                        modifier = Modifier.fillMaxWidth().testTag("transaction_item_${txn.transactionId}")
                    ) {
                        Column(modifier = Modifier.padding(14.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = label,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 14.sp
                                )
                                Text(
                                    text = String.format(Locale.US, "$%.2f", txn.priceUsd),
                                    fontWeight = FontWeight.ExtraBold,
                                    color = AcademicNavy,
                                    fontSize = 15.sp
                                )
                            }
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = "Google Play Order: ${txn.transactionId} • Provider: ${txn.paymentProvider}",
                                fontSize = 11.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(top = 4.dp),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text(
                                    text = dateFormat.format(Date(txn.timestamp)),
                                    fontSize = 11.sp,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                                Text(
                                    text = txn.status,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = if (txn.status == "COMPLETED") EmeraldTeal else Color.Red
                                )
                            }
                        }
                    }
                }
            }
        }

        // Account & Security Actions
        item {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text(
                    text = "Account & System",
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )

                // Restore Purchases
                OutlinedButton(
                    onClick = onRestorePurchases,
                    shape = RoundedCornerShape(10.dp),
                    modifier = Modifier.fillMaxWidth().testTag("restore_purchases_btn")
                ) {
                    Icon(imageVector = Icons.Default.Restore, contentDescription = null, modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Restore Google Play Purchases")
                }

                // Admin Dashboard Entrance
                Button(
                    onClick = onNavigateToAdmin,
                    shape = RoundedCornerShape(10.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = AcademicNavy),
                    modifier = Modifier.fillMaxWidth().testTag("admin_dashboard_btn")
                ) {
                    Icon(imageVector = Icons.Default.AdminPanelSettings, contentDescription = null, modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Admin Console (Data & Content Management)")
                }

                // Policy & Terms Rows
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    TextButton(onClick = { showHelpDialog = true }) {
                        Text("Help & FAQ", fontSize = 12.sp)
                    }
                    TextButton(onClick = { showPrivacyDialog = true }) {
                        Text("Privacy Policy", fontSize = 12.sp)
                    }
                    TextButton(onClick = { showTermsDialog = true }) {
                        Text("Terms", fontSize = 12.sp)
                    }
                    TextButton(onClick = { showDeleteAccountDialog = true }) {
                        Text("Delete Account", fontSize = 12.sp, color = Color.Red)
                    }
                }
            }
        }
    }

    // Help Dialog
    if (showHelpDialog) {
        AlertDialog(
            onDismissRequest = { showHelpDialog = false },
            title = { Text("Help & Support", fontWeight = FontWeight.Bold) },
            text = {
                Text(
                    "School EIIN Finder allows you to look up Educational Institute Identification Numbers issued by the Ministry of Education & BANBEIS, download Class Notes, and ask questions via Q&A Packs ($1.01 for 1 question up to $20.01 for 20 questions). All payments are processed exclusively through Google Play Billing."
                )
            },
            confirmButton = {
                TextButton(onClick = { showHelpDialog = false }) { Text("Close") }
            }
        )
    }

    // Privacy Dialog
    if (showPrivacyDialog) {
        AlertDialog(
            onDismissRequest = { showPrivacyDialog = false },
            title = { Text("Privacy Policy", fontWeight = FontWeight.Bold) },
            text = {
                Text(
                    "We respect student and educator privacy. We do not sell personal data or collect external card information. All purchases use Google Play In-App Billing."
                )
            },
            confirmButton = {
                TextButton(onClick = { showPrivacyDialog = false }) { Text("Close") }
            }
        )
    }

    // Terms Dialog
    if (showTermsDialog) {
        AlertDialog(
            onDismissRequest = { showTermsDialog = false },
            title = { Text("Terms of Service", fontWeight = FontWeight.Bold) },
            text = {
                Text(
                    "School credit packages, class note packages, and Q&A packs are digital entitlements processed exclusively via Google Play Billing. Unlocked items remain accessible for the lifetime of your account."
                )
            },
            confirmButton = {
                TextButton(onClick = { showTermsDialog = false }) { Text("Close") }
            }
        )
    }

    // Delete Account Dialog
    if (showDeleteAccountDialog) {
        AlertDialog(
            onDismissRequest = { showDeleteAccountDialog = false },
            title = { Text("Request Account Deletion", fontWeight = FontWeight.Bold, color = Color.Red) },
            text = {
                Text("Are you sure you want to request account deletion? All purchased credits and library records will be revoked.")
            },
            confirmButton = {
                Button(
                    onClick = { showDeleteAccountDialog = false },
                    colors = ButtonDefaults.buttonColors(containerColor = Color.Red)
                ) {
                    Text("Confirm Deletion Request")
                }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteAccountDialog = false }) { Text("Cancel") }
            }
        )
    }
}
