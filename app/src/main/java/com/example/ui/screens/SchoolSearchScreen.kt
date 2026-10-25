package com.example.ui.screens

import android.annotation.SuppressLint
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Language
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Public
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Verified
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.SuggestionChip
import androidx.compose.material3.SuggestionChipDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import com.example.data.model.SchoolEntity
import com.example.ui.theme.AcademicNavy
import com.example.ui.theme.EmeraldTeal
import java.net.URLEncoder

@SuppressLint("SetJavaScriptEnabled")
@Composable
fun SchoolSearchScreen(
    searchQuery: String,
    onSearchQueryChange: (String) -> Unit,
    onSearchGoogleNow: (String) -> Unit,
    isSearchingGoogle: Boolean,
    googleSuggestions: List<String>,
    recentGoogleSearches: List<String>,
    activeGoogleWebQuery: String,
    aiSearchSummary: String? = null,
    onGoogleWebViewResultsExtracted: (String, String) -> Unit,
    selectedDivision: String,
    onSelectDivision: (String) -> Unit,
    selectedType: String,
    onSelectType: (String) -> Unit,
    searchResults: List<SchoolEntity>,
    unlockedSchoolIds: Set<String>,
    remainingCredits: Int,
    onSelectSchool: (SchoolEntity) -> Unit,
    onCopyEiin: (String, String, SchoolEntity?) -> Unit,
    onNavigateToBilling: () -> Unit
) {
    val focusManager = LocalFocusManager.current
    val divisions = listOf("All", "Dhaka", "Chattogram", "Rajshahi", "Khulna", "Barishal", "Sylhet", "Rangpur", "Mymensingh")
    val types = listOf("All", "College", "High School", "School & College", "Madrasah", "Cadet College")

    // View mode: "CARDS" (Extracted Google Search Cards) or "WEBVIEW" (Live Interactive Google Search Page)
    var viewMode by remember { mutableStateOf("CARDS") }
    var isWebViewLoading by remember { mutableStateOf(false) }

    val effectiveWebQuery = activeGoogleWebQuery.ifBlank { searchQuery.trim() }
    val googleSearchUrl = remember(effectiveWebQuery) {
        if (effectiveWebQuery.isBlank()) {
            ""
        } else {
            val q = if (effectiveWebQuery.contains("eiin", ignoreCase = true)) {
                "$effectiveWebQuery Bangladesh"
            } else {
                "$effectiveWebQuery school EIIN number Bangladesh"
            }
            "https://www.google.com/search?q=${URLEncoder.encode(q, "UTF-8")}&hl=en"
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .testTag("school_search_screen")
    ) {
        // Sticky Google Search & Filter Header
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(MaterialTheme.colorScheme.surface)
                .padding(horizontal = 16.dp, vertical = 10.dp)
        ) {
            // Credit entitlement & Live Google Search indicator banner
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
                        modifier = Modifier.size(15.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = if (remainingCredits > 0) "$remainingCredits School Credits • AI + Google EIIN Search" else "0 Credits Remaining",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = if (remainingCredits > 0) EmeraldTeal else Color.Red
                    )
                }

                if (remainingCredits == 0) {
                    Text(
                        text = "Buy Credits",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = AcademicNavy,
                        modifier = Modifier
                            .clickable { onNavigateToBilling() }
                            .testTag("search_top_buy_credits_btn")
                    )
                } else {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.Public,
                            contentDescription = null,
                            tint = AcademicNavy,
                            modifier = Modifier.size(13.dp)
                        )
                        Spacer(modifier = Modifier.width(3.dp))
                        Text(
                            text = "AI + Google Live",
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            color = AcademicNavy
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Search Bar + "Search Google" Button
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                OutlinedTextField(
                    value = searchQuery,
                    onValueChange = onSearchQueryChange,
                    placeholder = {
                        Text(
                            "Search any school or EIIN on Google...",
                            fontSize = 13.sp,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    },
                    leadingIcon = {
                        Icon(imageVector = Icons.Default.Search, contentDescription = "Search Google", tint = AcademicNavy)
                    },
                    trailingIcon = {
                        if (searchQuery.isNotEmpty()) {
                            IconButton(onClick = { onSearchQueryChange("") }) {
                                Icon(imageVector = Icons.Default.Clear, contentDescription = "Clear")
                            }
                        }
                    },
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
                    keyboardActions = KeyboardActions(
                        onSearch = {
                            focusManager.clearFocus()
                            onSearchGoogleNow(searchQuery)
                        }
                    ),
                    shape = RoundedCornerShape(14.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = AcademicNavy,
                        unfocusedBorderColor = MaterialTheme.colorScheme.outlineVariant
                    ),
                    modifier = Modifier
                        .weight(1f)
                        .testTag("school_search_text_input")
                )

                Button(
                    onClick = {
                        focusManager.clearFocus()
                        onSearchGoogleNow(searchQuery)
                    },
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = AcademicNavy),
                    contentPadding = PaddingValues(horizontal = 12.dp, vertical = 14.dp),
                    modifier = Modifier.testTag("search_google_submit_btn")
                ) {
                    Icon(
                        imageVector = Icons.Default.Public,
                        contentDescription = "AI Search EIIN",
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("AI Search", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                }
            }

            // Live AI & Google Search Progress Bar
            if (isSearchingGoogle || isWebViewLoading) {
                Spacer(modifier = Modifier.height(6.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "AI Searching & Extracting EIIN for '${searchQuery.ifBlank { effectiveWebQuery }}'...",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = AcademicNavy
                    )
                }
                Spacer(modifier = Modifier.height(4.dp))
                LinearProgressIndicator(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(3.dp)
                        .clip(RoundedCornerShape(2.dp)),
                    color = EmeraldTeal
                )
            }

            // Live Google Autocomplete Suggestions Row (while typing)
            if (googleSuggestions.isNotEmpty() && searchQuery.isNotBlank()) {
                Spacer(modifier = Modifier.height(6.dp))
                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    item {
                        Text(
                            text = "Google Suggests:",
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                    items(googleSuggestions) { suggestion ->
                        SuggestionChip(
                            onClick = {
                                focusManager.clearFocus()
                                onSearchGoogleNow(suggestion)
                            },
                            label = { Text(suggestion, fontSize = 11.sp) },
                            icon = {
                                Icon(
                                    imageVector = Icons.Default.Search,
                                    contentDescription = null,
                                    tint = AcademicNavy,
                                    modifier = Modifier.size(13.dp)
                                )
                            },
                            colors = SuggestionChipDefaults.suggestionChipColors(
                                containerColor = Color(0xFFE3F2FD)
                            )
                        )
                    }
                }
            } else if (searchQuery.isBlank() && recentGoogleSearches.isNotEmpty()) {
                // Recent Google Searches chips when search bar is empty
                Spacer(modifier = Modifier.height(6.dp))
                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    item {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.History,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.onSurfaceVariant,
                                modifier = Modifier.size(13.dp)
                            )
                            Spacer(modifier = Modifier.width(3.dp))
                            Text(
                                text = "Quick Google Search:",
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                    items(recentGoogleSearches) { recent ->
                        SuggestionChip(
                            onClick = {
                                focusManager.clearFocus()
                                onSearchGoogleNow(recent)
                            },
                            label = { Text(recent, fontSize = 11.sp) }
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Mode Toggle: Extracted Google Cards vs Live Google Web Page + Division Filter
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    modifier = Modifier
                        .clip(RoundedCornerShape(10.dp))
                        .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.6f))
                        .padding(2.dp),
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(8.dp))
                            .background(if (viewMode == "CARDS") AcademicNavy else Color.Transparent)
                            .clickable { viewMode = "CARDS" }
                            .padding(horizontal = 10.dp, vertical = 5.dp)
                            .testTag("mode_google_cards_btn")
                    ) {
                        Text(
                            text = "Google EIIN Cards (${searchResults.size})",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = if (viewMode == "CARDS") Color.White else MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }

                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(8.dp))
                            .background(if (viewMode == "WEBVIEW") EmeraldTeal else Color.Transparent)
                            .clickable {
                                viewMode = "WEBVIEW"
                                if (effectiveWebQuery.isBlank() && searchQuery.isNotBlank()) {
                                    onSearchGoogleNow(searchQuery)
                                }
                            }
                            .padding(horizontal = 10.dp, vertical = 5.dp)
                            .testTag("mode_google_webview_btn")
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.Language,
                                contentDescription = null,
                                tint = if (viewMode == "WEBVIEW") Color.White else MaterialTheme.colorScheme.onSurfaceVariant,
                                modifier = Modifier.size(13.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = "Live Google Web Page",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = if (viewMode == "WEBVIEW") Color.White else MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }

                if (selectedDivision != "All" || selectedType != "All") {
                    TextButton(
                        onClick = {
                            onSelectDivision("All")
                            onSelectType("All")
                        },
                        contentPadding = PaddingValues(horizontal = 6.dp, vertical = 0.dp)
                    ) {
                        Text("Reset Filters", fontSize = 11.sp, color = AcademicNavy)
                    }
                }
            }

            if (viewMode == "CARDS") {
                // Division Filters Row
                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                    modifier = Modifier.padding(top = 4.dp)
                ) {
                    items(divisions) { div ->
                        val isSelected = selectedDivision.equals(div, ignoreCase = true)
                        FilterChip(
                            selected = isSelected,
                            onClick = { onSelectDivision(div) },
                            label = { Text(div, fontSize = 11.sp) },
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
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
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
        }

        // Main Content Area: Holds both the Google Search Cards and the live Google Search WebView
        // (When in CARDS mode, the WebView runs with 1dp size in the background to extract live Google DOM results!)
        Box(modifier = Modifier.fillMaxSize()) {
            if (googleSearchUrl.isNotBlank()) {
                AndroidView(
                    factory = { context ->
                        WebView(context).apply {
                            settings.javaScriptEnabled = true
                            settings.domStorageEnabled = true
                            settings.userAgentString =
                                "Mozilla/5.0 (Linux; Android 14; Pixel 8) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/124.0.0.0 Mobile Safari/537.36"
                            webViewClient = object : WebViewClient() {
                                override fun onPageStarted(view: WebView?, url: String?, favicon: android.graphics.Bitmap?) {
                                    super.onPageStarted(view, url, favicon)
                                    isWebViewLoading = true
                                }

                                override fun onPageFinished(view: WebView?, url: String?) {
                                    super.onPageFinished(view, url)
                                    isWebViewLoading = false
                                    // Extract Google Search headings, snippets, and 6-digit EIINs from live DOM
                                    val jsExtractor = """
                                        (function() {
                                            var items = [];
                                            var headings = document.querySelectorAll('h3, [role="heading"]');
                                            for (var i = 0; i < headings.length; i++) {
                                                var h = headings[i];
                                                var title = (h.innerText || '').trim();
                                                if (title.length < 4) continue;
                                                var container = h.closest('div.g') || h.parentElement.parentElement.parentElement || h.parentElement;
                                                var snippet = container ? (container.innerText || '').replace(title, '').trim() : '';
                                                var linkEl = h.closest('a') || (container ? container.querySelector('a') : null);
                                                var link = linkEl ? linkEl.href : 'https://www.google.com/search';
                                                items.push({
                                                    title: title.substring(0, 140),
                                                    snippet: snippet.substring(0, 320),
                                                    link: link
                                                });
                                            }
                                            if (items.length === 0 && document.body) {
                                                var bodyText = document.body.innerText || '';
                                                items.push({
                                                    title: document.title || 'Google Search Result',
                                                    snippet: bodyText.substring(0, 500),
                                                    link: window.location.href
                                                });
                                            }
                                            return JSON.stringify(items.slice(0, 12));
                                        })();
                                    """.trimIndent()

                                    view?.evaluateJavascript(jsExtractor) { jsonResult ->
                                        if (!jsonResult.isNullOrBlank() && jsonResult != "null") {
                                            onGoogleWebViewResultsExtracted(effectiveWebQuery, jsonResult)
                                        }
                                    }
                                }
                            }
                        }
                    },
                    update = { webView ->
                        if (webView.url != googleSearchUrl && googleSearchUrl.isNotBlank()) {
                            webView.loadUrl(googleSearchUrl)
                        }
                    },
                    modifier = if (viewMode == "WEBVIEW") {
                        Modifier
                            .fillMaxSize()
                            .testTag("google_live_webview")
                    } else {
                        // Keep 1dp invisible WebView active so live Google DOM extraction works automatically in CARDS mode
                        Modifier
                            .size(1.dp)
                            .alpha(0f)
                    }
                )
            }

            if (viewMode == "WEBVIEW") {
                if (googleSearchUrl.isBlank()) {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(24.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Icon(
                                imageVector = Icons.Default.Public,
                                contentDescription = null,
                                tint = AcademicNavy,
                                modifier = Modifier.size(56.dp)
                            )
                            Spacer(modifier = Modifier.height(12.dp))
                            Text(
                                text = "Type any school name in the search bar above",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold
                            )
                            Spacer(modifier = Modifier.height(6.dp))
                            Text(
                                text = "We will search Google live and display both extracted EIIN cards and the full Google Search page.",
                                fontSize = 13.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }
            } else {
                // CARDS Mode: Structured Google Search EIIN Results
                Column(modifier = Modifier.fillMaxSize()) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 6.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = if (searchQuery.isNotBlank()) {
                                "${searchResults.size} AI & Web Extracted EIIN Results for '$searchQuery'"
                            } else {
                                "${searchResults.size} Schools Available (Type above for AI EIIN Search)"
                            },
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )

                        if (searchQuery.isNotBlank()) {
                            Text(
                                text = "Open Google Web View →",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = EmeraldTeal,
                                modifier = Modifier.clickable { viewMode = "WEBVIEW" }
                            )
                        }
                    }

                    if (!aiSearchSummary.isNullOrBlank() && searchQuery.isNotBlank()) {
                        Card(
                            shape = RoundedCornerShape(12.dp),
                            colors = CardDefaults.cardColors(containerColor = Color(0xFFE8F5E9)),
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 16.dp, vertical = 4.dp)
                                .testTag("ai_search_summary_card")
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Verified,
                                    contentDescription = null,
                                    tint = EmeraldTeal,
                                    modifier = Modifier.size(16.dp)
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = aiSearchSummary,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    color = AcademicNavy,
                                    lineHeight = 15.sp
                                )
                            }
                        }
                    }

                    if (searchResults.isEmpty() && !isSearchingGoogle) {
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(28.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Icon(
                                    imageVector = Icons.Default.Public,
                                    contentDescription = null,
                                    tint = AcademicNavy,
                                    modifier = Modifier.size(60.dp)
                                )
                                Spacer(modifier = Modifier.height(12.dp))
                                Text(
                                    text = "Search Google for '$searchQuery'",
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                                Spacer(modifier = Modifier.height(6.dp))
                                Text(
                                    text = "Tap below to run a live Google Web Search or switch to the Live Google Web Page tab to see all Google results.",
                                    fontSize = 13.sp,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                                Spacer(modifier = Modifier.height(14.dp))
                                Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                                    Button(
                                        onClick = { onSearchGoogleNow(searchQuery) },
                                        colors = ButtonDefaults.buttonColors(containerColor = AcademicNavy)
                                    ) {
                                        Icon(imageVector = Icons.Default.Search, contentDescription = null, modifier = Modifier.size(16.dp))
                                        Spacer(modifier = Modifier.width(6.dp))
                                        Text("Search Google Now")
                                    }
                                    Button(
                                        onClick = {
                                            onSearchGoogleNow(searchQuery)
                                            viewMode = "WEBVIEW"
                                        },
                                        colors = ButtonDefaults.buttonColors(containerColor = EmeraldTeal)
                                    ) {
                                        Icon(imageVector = Icons.Default.Language, contentDescription = null, modifier = Modifier.size(16.dp))
                                        Spacer(modifier = Modifier.width(6.dp))
                                        Text("Google Web Page")
                                    }
                                }
                            }
                        }
                    } else {
                        LazyColumn(
                            modifier = Modifier.fillMaxSize(),
                            contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
                            verticalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            items(searchResults, key = { it.id }) { school ->
                                val isUnlocked = unlockedSchoolIds.contains(school.id) ||
                                    unlockedSchoolIds.contains("eiin_${school.eiin}")
                                val canViewEiin = isUnlocked || remainingCredits > 0
                                val isFromGoogle = school.id.startsWith("google_") || school.dataSource.contains("Google", ignoreCase = true)

                                Card(
                                    shape = RoundedCornerShape(16.dp),
                                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .then(
                                            if (isFromGoogle) {
                                                Modifier.border(
                                                    width = 1.dp,
                                                    color = EmeraldTeal.copy(alpha = 0.4f),
                                                    shape = RoundedCornerShape(16.dp)
                                                )
                                            } else {
                                                Modifier
                                            }
                                        )
                                        .clickable { onSelectSchool(school) }
                                        .testTag("school_card_${school.id}")
                                ) {
                                    Column(modifier = Modifier.padding(16.dp)) {
                                        // Top Row: Division, Google Source Badge & Credit Status
                                        Row(
                                            modifier = Modifier.fillMaxWidth(),
                                            horizontalArrangement = Arrangement.SpaceBetween,
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            Row(
                                                verticalAlignment = Alignment.CenterVertically,
                                                horizontalArrangement = Arrangement.spacedBy(6.dp)
                                            ) {
                                                if (isFromGoogle) {
                                                    Row(
                                                        modifier = Modifier
                                                            .clip(RoundedCornerShape(6.dp))
                                                            .background(Color(0xFFE3F2FD))
                                                            .padding(horizontal = 6.dp, vertical = 2.dp),
                                                        verticalAlignment = Alignment.CenterVertically
                                                    ) {
                                                        Icon(
                                                            imageVector = Icons.Default.Public,
                                                            contentDescription = null,
                                                            tint = AcademicNavy,
                                                            modifier = Modifier.size(11.dp)
                                                        )
                                                        Spacer(modifier = Modifier.width(3.dp))
                                                        Text(
                                                            text = "AI + GOOGLE EXTRACTED",
                                                            fontSize = 9.sp,
                                                            fontWeight = FontWeight.ExtraBold,
                                                            color = AcademicNavy
                                                        )
                                                    }
                                                }

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
                                                    .background(if (canViewEiin) Color(0xFFE8F5E9) else Color(0xFFFFEBEE))
                                                    .padding(horizontal = 8.dp, vertical = 2.dp),
                                                verticalAlignment = Alignment.CenterVertically
                                            ) {
                                                Icon(
                                                    imageVector = if (canViewEiin) Icons.Default.CheckCircle else Icons.Default.Lock,
                                                    contentDescription = null,
                                                    tint = if (canViewEiin) EmeraldTeal else Color.Red,
                                                    modifier = Modifier.size(12.dp)
                                                )
                                                Spacer(modifier = Modifier.width(4.dp))
                                                Text(
                                                    text = when {
                                                        isUnlocked -> "UNLOCKED"
                                                        remainingCredits > 0 -> "CREDIT ACTIVE"
                                                        else -> "LIMIT REACHED"
                                                    },
                                                    fontSize = 10.sp,
                                                    fontWeight = FontWeight.Bold,
                                                    color = if (canViewEiin) EmeraldTeal else Color.Red
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

                                        // Google Search Snippet / Bangla Name
                                        if (school.address.isNotBlank()) {
                                            Text(
                                                text = school.address,
                                                fontSize = 12.sp,
                                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                                maxLines = 2,
                                                overflow = TextOverflow.Ellipsis,
                                                modifier = Modifier.padding(top = 3.dp)
                                            )
                                        }

                                        Spacer(modifier = Modifier.height(12.dp))

                                        // EIIN Banner inside Card — Visible immediately while credits remain!
                                        Row(
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .clip(RoundedCornerShape(10.dp))
                                                .background(if (canViewEiin) Color(0xFFE3F2FD) else MaterialTheme.colorScheme.surfaceVariant)
                                                .padding(horizontal = 12.dp, vertical = 8.dp),
                                            horizontalArrangement = Arrangement.SpaceBetween,
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            Column {
                                                Text(
                                                    text = "EIIN NUMBER (FROM GOOGLE SEARCH)",
                                                    fontSize = 9.sp,
                                                    fontWeight = FontWeight.Bold,
                                                    color = if (canViewEiin) AcademicNavy else MaterialTheme.colorScheme.onSurfaceVariant,
                                                    letterSpacing = 0.5.sp
                                                )
                                                Text(
                                                    text = if (canViewEiin) school.eiin else "•••••• (Limit Reached)",
                                                    fontSize = 16.sp,
                                                    fontWeight = FontWeight.ExtraBold,
                                                    letterSpacing = if (canViewEiin) 2.sp else 1.sp,
                                                    color = if (canViewEiin) AcademicNavy else Color.Gray
                                                )
                                            }

                                            if (canViewEiin) {
                                                IconButton(
                                                    onClick = { onCopyEiin(school.eiin, school.name, school) },
                                                    modifier = Modifier
                                                        .size(32.dp)
                                                        .testTag("copy_eiin_${school.id}")
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
                                                    text = "Buy Credits to Unlock",
                                                    fontSize = 11.sp,
                                                    fontWeight = FontWeight.SemiBold,
                                                    color = Color.Red,
                                                    modifier = Modifier.clickable { onNavigateToBilling() }
                                                )
                                            }
                                        }

                                        Spacer(modifier = Modifier.height(10.dp))

                                        // Bottom Tags: Type & Data Source
                                        Row(
                                            modifier = Modifier.fillMaxWidth(),
                                            horizontalArrangement = Arrangement.SpaceBetween
                                        ) {
                                            Text(
                                                text = "${school.institutionType} • ${school.board} Board",
                                                fontSize = 11.sp,
                                                color = MaterialTheme.colorScheme.onSurfaceVariant
                                            )
                                            Text(
                                                text = school.dataSource.take(28),
                                                fontSize = 10.sp,
                                                fontWeight = FontWeight.Medium,
                                                color = EmeraldTeal
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
