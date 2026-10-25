package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.MenuBook
import androidx.compose.material.icons.filled.QuestionAnswer
import androidx.compose.material.icons.filled.School
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.VerifiedUser
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
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
import com.example.data.model.NoteCreditPackage
import com.example.data.model.PricingCatalog
import com.example.data.model.QaCreditPackage
import com.example.data.model.SchoolCreditPackage
import com.example.data.model.UserEntitlementEntity
import com.example.ui.theme.AcademicNavy
import com.example.ui.theme.EmeraldTeal
import com.example.ui.theme.GoldenAmber

@Composable
fun MarketplaceBillingScreen(
    currentTab: String, // "SCHOOLS", "NOTES", or "QA"
    onTabSelected: (String) -> Unit,
    entitlements: UserEntitlementEntity,
    onPurchaseSchoolPackage: (SchoolCreditPackage) -> Unit,
    onPurchaseNotePackage: (NoteCreditPackage) -> Unit,
    onPurchaseQaPackage: (QaCreditPackage) -> Unit
) {
    val tabIndex = when (currentTab) {
        "SCHOOLS" -> 0
        "NOTES" -> 1
        else -> 2
    }

    val activeColor = when (tabIndex) {
        0 -> AcademicNavy
        1 -> EmeraldTeal
        else -> Color(0xFF01875F)
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .testTag("marketplace_billing_screen")
    ) {
        // Top Tab Selector (Schools, Notes, Q&A Packs)
        TabRow(
            selectedTabIndex = tabIndex,
            containerColor = MaterialTheme.colorScheme.surface,
            contentColor = activeColor,
            indicator = { tabPositions ->
                TabRowDefaults.SecondaryIndicator(
                    modifier = Modifier.tabIndicatorOffset(tabPositions[tabIndex]),
                    color = activeColor,
                    height = 3.dp
                )
            }
        ) {
            Tab(
                selected = tabIndex == 0,
                onClick = { onTabSelected("SCHOOLS") },
                text = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(imageVector = Icons.Default.School, contentDescription = null, modifier = Modifier.size(15.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("School EIIN", fontWeight = FontWeight.Bold, fontSize = 11.sp)
                    }
                },
                modifier = Modifier.testTag("billing_school_tab")
            )
            Tab(
                selected = tabIndex == 1,
                onClick = { onTabSelected("NOTES") },
                text = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(imageVector = Icons.AutoMirrored.Filled.MenuBook, contentDescription = null, modifier = Modifier.size(15.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Class Notes", fontWeight = FontWeight.Bold, fontSize = 11.sp)
                    }
                },
                modifier = Modifier.testTag("billing_notes_tab")
            )
            Tab(
                selected = tabIndex == 2,
                onClick = { onTabSelected("QA") },
                text = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(imageVector = Icons.Default.QuestionAnswer, contentDescription = null, modifier = Modifier.size(15.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Q&A Packs", fontWeight = FontWeight.Bold, fontSize = 11.sp)
                    }
                },
                modifier = Modifier.testTag("billing_qa_tab")
            )
        }

        // Google Play Exclusive Policy & Formula Banner
        Card(
            shape = RoundedCornerShape(14.dp),
            colors = CardDefaults.cardColors(
                containerColor = when (tabIndex) {
                    0 -> Color(0xFFE3F2FD)
                    1 -> Color(0xFFE0F2F1)
                    else -> Color(0xFFE8F5E9)
                }
            ),
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 12.dp)
        ) {
            Column(modifier = Modifier.padding(14.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.VerifiedUser,
                            contentDescription = null,
                            tint = activeColor,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "Google Play Billing Exclusive",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = activeColor
                        )
                    }
                    Text(
                        text = "NO EXTERNAL PAYMENTS",
                        fontSize = 9.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = activeColor
                    )
                }

                Spacer(modifier = Modifier.height(6.dp))

                Text(
                    text = when (tabIndex) {
                        0 -> "1 credit = unlock 1 unique school's EIIN & dossier. Formula: Units × $1.00 + $0.01 (100 to 2,000 schools)."
                        1 -> "1 note credit = unlock 1 full academic PDF note permanently. Pricing starts from $0.99, $1.99, $2.99 up to $19.99 (1 to 20 notes)."
                        else -> "1 Q&A credit = ask 1 question or unlock 1 verified solution. Formula: Questions × $1.00 + $0.01 (1 question = $1.01 up to 20 questions = $20.01)."
                    },
                    fontSize = 11.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    lineHeight = 16.sp
                )

                Spacer(modifier = Modifier.height(6.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "All purchases via Google Play In-App Billing only",
                        fontSize = 10.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = activeColor
                    )

                    Text(
                        text = when (tabIndex) {
                            0 -> "Balance: ${entitlements.schoolCreditsRemaining} credits"
                            1 -> "Balance: ${entitlements.noteCreditsRemaining} credits"
                            else -> "Balance: ${entitlements.qaCreditsRemaining} credits"
                        },
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }
            }
        }

        // Package Cards Grid
        when (tabIndex) {
            0 -> {
                // School Credit Packages (100 to 2,000 schools in 100 increments)
                LazyVerticalGrid(
                    columns = GridCells.Fixed(2),
                    contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                    modifier = Modifier.fillMaxSize().testTag("school_packages_grid")
                ) {
                    items(PricingCatalog.schoolPackages, key = { it.sku }) { pkg ->
                        Card(
                            shape = RoundedCornerShape(16.dp),
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                            modifier = Modifier
                                .fillMaxWidth()
                                .border(
                                    width = if (pkg.isPopular) 2.dp else 1.dp,
                                    color = if (pkg.isPopular) AcademicNavy else MaterialTheme.colorScheme.outlineVariant,
                                    shape = RoundedCornerShape(16.dp)
                                )
                                .testTag("package_${pkg.sku}")
                        ) {
                            Column(
                                modifier = Modifier.padding(14.dp),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                if (pkg.isPopular) {
                                    Box(
                                        modifier = Modifier
                                            .clip(RoundedCornerShape(6.dp))
                                            .background(AcademicNavy)
                                            .padding(horizontal = 8.dp, vertical = 2.dp)
                                    ) {
                                        Text(
                                            text = "MOST POPULAR",
                                            fontSize = 9.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = Color.White
                                        )
                                    }
                                    Spacer(modifier = Modifier.height(6.dp))
                                }

                                Text(
                                    text = "${pkg.schoolRecords}",
                                    fontSize = 26.sp,
                                    fontWeight = FontWeight.ExtraBold,
                                    color = AcademicNavy
                                )
                                Text(
                                    text = "School Records",
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    color = MaterialTheme.colorScheme.onSurface
                                )

                                Spacer(modifier = Modifier.height(8.dp))

                                Text(
                                    text = pkg.formattedPrice,
                                    fontSize = 19.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                                Text(
                                    text = "Google Play In-App",
                                    fontSize = 10.sp,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )

                                Spacer(modifier = Modifier.height(10.dp))

                                Button(
                                    onClick = { onPurchaseSchoolPackage(pkg) },
                                    shape = RoundedCornerShape(10.dp),
                                    colors = ButtonDefaults.buttonColors(containerColor = AcademicNavy),
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .testTag("buy_btn_${pkg.sku}")
                                ) {
                                    Text("Google Play Buy", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                }
                            }
                        }
                    }
                }
            }

            1 -> {
                // Notes Packages (1 to 20 notes)
                LazyVerticalGrid(
                    columns = GridCells.Fixed(2),
                    contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                    modifier = Modifier.fillMaxSize().testTag("notes_packages_grid")
                ) {
                    items(PricingCatalog.notePackages, key = { it.sku }) { pkg ->
                        Card(
                            shape = RoundedCornerShape(16.dp),
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                            modifier = Modifier
                                .fillMaxWidth()
                                .border(
                                    width = if (pkg.isPopular) 2.dp else 1.dp,
                                    color = if (pkg.isPopular) EmeraldTeal else MaterialTheme.colorScheme.outlineVariant,
                                    shape = RoundedCornerShape(16.dp)
                                )
                                .testTag("package_${pkg.sku}")
                        ) {
                            Column(
                                modifier = Modifier.padding(14.dp),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                if (pkg.isPopular) {
                                    Box(
                                        modifier = Modifier
                                            .clip(RoundedCornerShape(6.dp))
                                            .background(EmeraldTeal)
                                            .padding(horizontal = 8.dp, vertical = 2.dp)
                                    ) {
                                        Text(
                                            text = "BEST VALUE",
                                            fontSize = 9.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = Color.White
                                        )
                                    }
                                    Spacer(modifier = Modifier.height(6.dp))
                                }

                                Text(
                                    text = "${pkg.noteCount}",
                                    fontSize = 26.sp,
                                    fontWeight = FontWeight.ExtraBold,
                                    color = EmeraldTeal
                                )
                                Text(
                                    text = if (pkg.noteCount == 1) "Class Note" else "Class Notes",
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    color = MaterialTheme.colorScheme.onSurface
                                )

                                Spacer(modifier = Modifier.height(8.dp))

                                Text(
                                    text = pkg.formattedPrice,
                                    fontSize = 19.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                                Text(
                                    text = "Google Play In-App",
                                    fontSize = 10.sp,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )

                                Spacer(modifier = Modifier.height(10.dp))

                                Button(
                                    onClick = { onPurchaseNotePackage(pkg) },
                                    shape = RoundedCornerShape(10.dp),
                                    colors = ButtonDefaults.buttonColors(containerColor = EmeraldTeal),
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .testTag("buy_btn_${pkg.sku}")
                                ) {
                                    Text("Google Play Buy", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                }
                            }
                        }
                    }
                }
            }

            else -> {
                // Q&A Packages (1 to 20 questions, $1.01 to $20.01)
                LazyVerticalGrid(
                    columns = GridCells.Fixed(2),
                    contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                    modifier = Modifier.fillMaxSize().testTag("qa_packages_grid")
                ) {
                    items(PricingCatalog.qaPackages, key = { it.sku }) { pkg ->
                        Card(
                            shape = RoundedCornerShape(16.dp),
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                            modifier = Modifier
                                .fillMaxWidth()
                                .border(
                                    width = if (pkg.isPopular) 2.dp else 1.dp,
                                    color = if (pkg.isPopular) Color(0xFF01875F) else MaterialTheme.colorScheme.outlineVariant,
                                    shape = RoundedCornerShape(16.dp)
                                )
                                .testTag("package_${pkg.sku}")
                        ) {
                            Column(
                                modifier = Modifier.padding(14.dp),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                if (pkg.isPopular) {
                                    Box(
                                        modifier = Modifier
                                            .clip(RoundedCornerShape(6.dp))
                                            .background(Color(0xFF01875F))
                                            .padding(horizontal = 8.dp, vertical = 2.dp)
                                    ) {
                                        Text(
                                            text = "RECOMMENDED",
                                            fontSize = 9.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = Color.White
                                        )
                                    }
                                    Spacer(modifier = Modifier.height(6.dp))
                                }

                                Text(
                                    text = "${pkg.questionCount}",
                                    fontSize = 26.sp,
                                    fontWeight = FontWeight.ExtraBold,
                                    color = Color(0xFF01875F)
                                )
                                Text(
                                    text = if (pkg.questionCount == 1) "Question Pack" else "Questions Pack",
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    color = MaterialTheme.colorScheme.onSurface
                                )

                                Spacer(modifier = Modifier.height(8.dp))

                                Text(
                                    text = pkg.formattedPrice,
                                    fontSize = 19.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                                Text(
                                    text = "Google Play In-App",
                                    fontSize = 10.sp,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )

                                Spacer(modifier = Modifier.height(10.dp))

                                Button(
                                    onClick = { onPurchaseQaPackage(pkg) },
                                    shape = RoundedCornerShape(10.dp),
                                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF01875F)),
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .testTag("buy_btn_${pkg.sku}")
                                ) {
                                    Text("Google Play Buy", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
