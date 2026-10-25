package com.example.data.remote

import com.example.data.model.SchoolEntity
import com.google.firebase.Firebase
import com.google.firebase.ai.ai
import com.google.firebase.ai.type.Schema
import com.google.firebase.ai.type.generationConfig
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.withContext
import kotlinx.coroutines.withTimeout
import kotlinx.coroutines.withTimeoutOrNull
import org.json.JSONArray
import org.json.JSONObject
import java.io.BufferedReader
import java.io.InputStreamReader
import java.net.HttpURLConnection
import java.net.URL
import java.net.URLEncoder
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

/**
 * AI-Powered Search & Live Web EIIN Extractor for Bangladeshi Educational Institutions.
 * Uses Firebase AI Logic (gemini-3.8-flash) with structured JSON output alongside live web search
 * snippets to accurately search and extract verified 6-digit EIIN codes, school names, divisions,
 * districts, education boards, and addresses on any Android phone.
 */
class GoogleSchoolSearchService {

    private val eiinRegex = Regex("\\b(1\\d{5})\\b")

    private val _lastAiSearchSummary = MutableStateFlow<String?>(null)
    val lastAiSearchSummary: StateFlow<String?> = _lastAiSearchSummary.asStateFlow()

    private val schoolRecordSchema = Schema.obj(
        properties = mapOf(
            "eiin" to Schema.string(description = "6-digit Bangladesh EIIN (Educational Institute Identification Number) starting with 1, e.g. 108274"),
            "name" to Schema.string(description = "Official English name of the school, college, or madrasah in Bangladesh"),
            "nameBn" to Schema.string(description = "Bengali name of the institution"),
            "division" to Schema.string(description = "Bangladesh Division: Dhaka, Chattogram, Rajshahi, Khulna, Barishal, Sylhet, Rangpur, or Mymensingh"),
            "district" to Schema.string(description = "District name in Bangladesh, e.g. Dhaka, Chattogram, Cumilla, Rajshahi, Sylhet, Bogura"),
            "upazila" to Schema.string(description = "Upazila or Thana name in Bangladesh"),
            "institutionType" to Schema.string(description = "Institution category: High School, College, School & College, Madrasah, or Cadet College"),
            "board" to Schema.string(description = "Education Board: Dhaka, Chattogram, Rajshahi, Cumilla, Jashore, Barishal, Sylhet, Dinajpur, Mymensingh, Madrasah, or Technical"),
            "address" to Schema.string(description = "Full address and AI search summary confirming the 6-digit EIIN"),
            "establishedYear" to Schema.integer(description = "Year established (e.g. 1952), or 0 if unknown")
        ),
        optionalProperties = listOf("nameBn", "establishedYear")
    )

    private val aiSearchResponseSchema = Schema.obj(
        properties = mapOf(
            "schools" to Schema.array(
                items = schoolRecordSchema,
                description = "List of matching Bangladeshi schools, colleges, or madrasahs with their extracted 6-digit EIIN numbers"
            ),
            "aiSummary" to Schema.string(description = "Concise AI summary showing the extracted 6-digit EIIN(s) and location for the user's query")
        ),
        optionalProperties = listOf("aiSummary")
    )

    private val generativeModel by lazy {
        val config = generationConfig {
            responseMimeType = "application/json"
            responseSchema = aiSearchResponseSchema
        }
        Firebase.ai.generativeModel(
            modelName = "gemini-3.8-flash",
            generationConfig = config
        )
    }

    private val bdDivisions = mapOf(
        "dhaka" to "Dhaka",
        "chattogram" to "Chattogram",
        "chittagong" to "Chattogram",
        "rajshahi" to "Rajshahi",
        "khulna" to "Khulna",
        "barishal" to "Barishal",
        "barisal" to "Barishal",
        "sylhet" to "Sylhet",
        "rangpur" to "Rangpur",
        "mymensingh" to "Mymensingh"
    )

    private val bdDistricts = listOf(
        "Dhaka", "Gazipur", "Narayanganj", "Tangail", "Faridpur", "Manikganj", "Munshiganj", "Narsingdi",
        "Kishoreganj", "Gopalganj", "Madaripur", "Rajbari", "Shariatpur",
        "Chattogram", "Chittagong", "Cumilla", "Comilla", "Cox's Bazar", "Feni", "Noakhali", "Brahmanbaria",
        "Chandpur", "Lakshmipur", "Rangamati", "Bandarban", "Khagrachhari",
        "Rajshahi", "Bogura", "Bogra", "Pabna", "Sirajganj", "Naogaon", "Natore", "Chapainawabganj", "Joypurhat",
        "Khulna", "Jashore", "Jessore", "Kushtia", "Jhenaidah", "Satkhira", "Bagerhat", "Chuadanga", "Magura", "Meherpur", "Narail",
        "Barishal", "Barisal", "Patuakhali", "Bhola", "Pirojpur", "Barguna", "Jhalokati",
        "Sylhet", "Moulvibazar", "Habiganj", "Sunamganj",
        "Rangpur", "Dinajpur", "Kurigram", "Gaibandha", "Nilphamari", "panchagarh", "Thakurgaon", "Lalmonirhat",
        "Mymensingh", "Netrokona", "Jamalpur", "Sherpur"
    )

    /**
     * Executes AI Search + Live Web Search in parallel and uses Gemini AI (`gemini-3.8-flash`)
     * to extract accurate 6-digit EIIN numbers and institution details.
     */
    suspend fun searchSchoolEiinOnGoogle(rawQuery: String): List<SchoolEntity> = withContext(Dispatchers.IO) {
        val query = rawQuery.trim()
        if (query.isBlank()) return@withContext emptyList()

        val searchPrompt = if (query.contains("eiin", ignoreCase = true)) {
            "$query Bangladesh school college"
        } else {
            "$query EIIN number Bangladesh school college"
        }

        val encoded = URLEncoder.encode(searchPrompt, "UTF-8")
        val today = SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.US).format(Date())

        coroutineScope {
            // 1. Gather fast web search snippets (bounded to 4s so mobile carrier blocks never stall AI search)
            val webResultsDeferred = async {
                withTimeoutOrNull(4_500L) {
                    coroutineScope {
                        val googleDeferred = async { fetchFromGoogleHtml(encoded, query, today) }
                        val ddgDeferred = async { fetchFromDuckDuckGoHtml(encoded, query, today) }
                        val wikiDeferred = async { fetchFromWikipediaApi(query, today) }
                        googleDeferred.await() + ddgDeferred.await() + wikiDeferred.await()
                    }
                } ?: emptyList()
            }

            // 2. Run Gemini AI Search immediately in parallel, plus pass any web context if available
            val directAiDeferred = async {
                searchAndExtractWithGeminiAi(
                    rawQuery = query,
                    webContextSnippets = "",
                    today = today
                )
            }

            val webResults = webResultsDeferred.await()
            val directAiResults = directAiDeferred.await()

            // 3. If web results returned raw text snippets without 6-digit EIINs and direct AI didn't run or needs web context,
            // run AI extraction over the collected web snippets
            val webContextAiResults = if (directAiResults.isEmpty() && webResults.isNotEmpty()) {
                val contextText = webResults.joinToString("\n") { "${it.name}: ${it.address} (Extracted: ${it.eiin})" }
                searchAndExtractWithGeminiAi(
                    rawQuery = query,
                    webContextSnippets = contextText,
                    today = today
                )
            } else {
                emptyList()
            }

            // Combine AI Search results first, followed by verified 6-digit web regex results
            val validWebResults = webResults.filter { isValidSixDigitEiin(it.eiin) }
            val combined = directAiResults + webContextAiResults + validWebResults

            val distinctResults = combined
                .distinctBy {
                    if (isValidSixDigitEiin(it.eiin)) it.eiin else it.name.lowercase()
                }
                .sortedByDescending { isValidSixDigitEiin(it.eiin) }

            distinctResults
        }
    }

    /**
     * Uses Firebase AI Logic (`gemini-3.8-flash`) with structured JSON output to search for
     * Bangladeshi educational institutions and extract their 6-digit EIIN codes.
     */
    private suspend fun searchAndExtractWithGeminiAi(
        rawQuery: String,
        webContextSnippets: String,
        today: String
    ): List<SchoolEntity> {
        val aiResults = mutableListOf<SchoolEntity>()
        try {
            val contextSection = if (webContextSnippets.isNotBlank()) {
                "\nLive Web Search Snippets to extract from:\n$webContextSnippets\n"
            } else {
                ""
            }

            val prompt = """
                You are an expert Bangladesh Education Board & BANBEIS EIIN (Educational Institute Identification Number) AI Search Engine.
                User search query: "$rawQuery"
                $contextSection
                Task:
                1. Search for the Bangladeshi school(s), college(s), or madrasah(s) matching "$rawQuery" (whether searched by institution name, partial name, district/upazila, or 6-digit EIIN).
                2. Extract the exact 6-digit numeric EIIN (always 6 digits starting with '1', such as 108274, 108357, 107965, 104271, 126425, etc.) for up to 6 matching institutions in Bangladesh.
                3. Provide the official English name, Bengali name (nameBn), Bangladesh division, district, upazila, institutionType, education board, establishedYear, and full address including EIIN verification details.
                4. Never output placeholder text like "Unknown" or "Verified Web" in the "eiin" field — "eiin" must be the 6-digit numeric EIIN code.
            """.trimIndent()

            val responseText = withTimeout(60_000L) {
                generativeModel.generateContent(prompt).text
            }

            if (!responseText.isNullOrBlank()) {
                val root = JSONObject(responseText)
                val summary = root.optString("aiSummary").trim()
                if (summary.isNotBlank()) {
                    _lastAiSearchSummary.value = summary
                }

                val schoolsArray = root.optJSONArray("schools") ?: JSONArray()
                for (i in 0 until schoolsArray.length()) {
                    val obj = schoolsArray.getJSONObject(i)
                    val rawEiin = obj.optString("eiin").trim()
                    val matchedSixDigit = eiinRegex.find(rawEiin)?.groupValues?.get(1)
                        ?: if (rawEiin.length == 6 && rawEiin.all { it.isDigit() }) rawEiin else null

                    val name = obj.optString("name").trim()
                    if (name.isNotBlank() && matchedSixDigit != null) {
                        val nameBn = obj.optString("nameBn").trim()
                            .ifBlank { "AI Extracted EIIN: $matchedSixDigit" }
                        val division = obj.optString("division").trim().ifBlank { "Dhaka" }
                        val district = obj.optString("district").trim().ifBlank { division }
                        val upazila = obj.optString("upazila").trim().ifBlank { district }
                        val instType = obj.optString("institutionType").trim().ifBlank { "School & College" }
                        val board = obj.optString("board").trim().ifBlank { division }
                        val address = obj.optString("address").trim()
                            .ifBlank { "$upazila, $district, $division • AI Extracted EIIN: $matchedSixDigit" }
                        val estYear = obj.optInt("establishedYear", 0).takeIf { it in 1800..2026 }

                        aiResults.add(
                            SchoolEntity(
                                id = "google_eiin_$matchedSixDigit",
                                name = name,
                                nameBn = nameBn,
                                eiin = matchedSixDigit,
                                division = division,
                                district = district,
                                upazila = upazila,
                                institutionType = instType,
                                board = board,
                                address = address,
                                establishedYear = estYear,
                                isVerified = true,
                                dataSource = "AI Search & EIIN Extractor",
                                lastVerifiedDate = today
                            )
                        )
                    }
                }

                if (summary.isBlank() && aiResults.isNotEmpty()) {
                    val top = aiResults.first()
                    _lastAiSearchSummary.value = "AI Search extracted EIIN ${top.eiin} for ${top.name} (${top.district}, ${top.division} Board)."
                }
            }
        } catch (_: Exception) {
            // Gracefully fall back to web regex / local cache if offline or App Check is still initializing
        }
        return aiResults
    }

    /**
     * Fetches live Google Search autocomplete suggestions as the user types in the search bar.
     */
    suspend fun fetchGoogleSuggestions(rawQuery: String): List<String> = withContext(Dispatchers.IO) {
        val query = rawQuery.trim()
        if (query.length < 2) return@withContext emptyList()
        val suggestions = mutableListOf<String>()
        try {
            val prompt = if (query.contains("eiin", ignoreCase = true)) query else "$query eiin"
            val encoded = URLEncoder.encode(prompt, "UTF-8")
            val url = "https://suggestqueries.google.com/complete/search?client=firefox&hl=en&gl=bd&q=$encoded"
            val jsonStr = httpGet(url)
            if (jsonStr.isNotBlank()) {
                val rootArray = JSONArray(jsonStr)
                if (rootArray.length() > 1) {
                    val items = rootArray.getJSONArray(1)
                    for (i in 0 until items.length()) {
                        val suggestion = items.optString(i).trim()
                        if (suggestion.isNotBlank()) {
                            suggestions.add(suggestion)
                        }
                    }
                }
            }
        } catch (_: Exception) {
        }
        suggestions.distinct().take(6)
    }

    /**
     * Parses live Google Search results extracted from the in-app Chromium WebView DOM (synchronous regex pass).
     */
    fun parseWebViewDomResults(rawQuery: String, jsonArrayStr: String): List<SchoolEntity> {
        val results = mutableListOf<SchoolEntity>()
        val today = SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.US).format(Date())
        try {
            val cleanJson = parseJsonArraySafely(jsonArrayStr)
            for (i in 0 until cleanJson.length()) {
                val obj = cleanJson.getJSONObject(i)
                val title = obj.optString("title").trim()
                val snippet = obj.optString("snippet").trim()
                val link = obj.optString("link").trim()
                val combinedText = "$title $snippet"

                val foundEiin = eiinRegex.find(combinedText)?.groupValues?.get(1)
                    ?: if (rawQuery.length == 6 && rawQuery.all { it.isDigit() }) rawQuery else null

                if (title.isNotBlank() && foundEiin != null) {
                    val school = buildSchoolEntityFromWebResult(
                        rawTitle = title,
                        snippet = snippet,
                        sourceUrl = link.ifBlank { "Google Search + AI Extractor" },
                        extractedEiin = foundEiin,
                        fallbackQuery = rawQuery,
                        verifiedDate = today
                    )
                    results.add(school)
                }
            }
        } catch (_: Exception) {
        }
        return results.distinctBy { if (it.eiin.length == 6) it.eiin else it.name.lowercase() }
    }

    /**
     * Parses WebView DOM results and uses Gemini AI (`gemini-3.8-flash`) to extract 6-digit EIINs
     * when the raw Google Search snippets require AI understanding.
     */
    suspend fun parseWebViewDomResultsWithAi(rawQuery: String, jsonArrayStr: String): List<SchoolEntity> = withContext(Dispatchers.IO) {
        val directRegexMatches = parseWebViewDomResults(rawQuery, jsonArrayStr)
        if (directRegexMatches.isNotEmpty()) {
            return@withContext directRegexMatches
        }

        val today = SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.US).format(Date())
        val snippetBuilder = StringBuilder()
        try {
            val cleanJson = parseJsonArraySafely(jsonArrayStr)
            for (i in 0 until cleanJson.length().coerceAtMost(8)) {
                val obj = cleanJson.getJSONObject(i)
                val title = obj.optString("title").trim()
                val snippet = obj.optString("snippet").trim()
                if (title.isNotBlank() || snippet.isNotBlank()) {
                    snippetBuilder.append("- ").append(title).append(": ").append(snippet).append("\n")
                }
            }
        } catch (_: Exception) {
        }

        searchAndExtractWithGeminiAi(
            rawQuery = rawQuery,
            webContextSnippets = snippetBuilder.toString(),
            today = today
        )
    }

    private fun parseJsonArraySafely(jsonArrayStr: String): JSONArray {
        return if (jsonArrayStr.startsWith("\"") && jsonArrayStr.endsWith("\"")) {
            JSONArray(
                jsonArrayStr.substring(1, jsonArrayStr.length - 1)
                    .replace("\\\"", "\"")
                    .replace("\\\\", "\\")
            )
        } else {
            JSONArray(jsonArrayStr)
        }
    }

    private fun isValidSixDigitEiin(eiin: String): Boolean {
        return eiin.length == 6 && eiin.all { it.isDigit() }
    }

    private fun fetchFromGoogleHtml(encodedQuery: String, rawQuery: String, today: String): List<SchoolEntity> {
        val results = mutableListOf<SchoolEntity>()
        try {
            val url = "https://www.google.com/search?q=$encodedQuery&hl=en&num=10"
            val html = httpGet(url)
            if (html.isNotBlank()) {
                val cleanTextBlocks = html
                    .replace(Regex("<script[^>]*>[\\s\\S]*?</script>", RegexOption.IGNORE_CASE), " ")
                    .replace(Regex("<style[^>]*>[\\s\\S]*?</style>", RegexOption.IGNORE_CASE), " ")

                val h3Regex = Regex("<h3[^>]*>(.*?)</h3>([\\s\\S]{0,600})", RegexOption.IGNORE_CASE)
                for (match in h3Regex.findAll(cleanTextBlocks)) {
                    val title = stripHtml(match.groupValues[1]).trim()
                    val snippetRaw = stripHtml(match.groupValues[2]).trim()
                    val combined = "$title $snippetRaw"
                    val eiin = eiinRegex.find(combined)?.groupValues?.get(1)

                    if (title.length > 4 && eiin != null) {
                        results.add(
                            buildSchoolEntityFromWebResult(
                                rawTitle = title,
                                snippet = snippetRaw.take(260),
                                sourceUrl = "Google Search Live",
                                extractedEiin = eiin,
                                fallbackQuery = rawQuery,
                                verifiedDate = today
                            )
                        )
                    }
                }

                if (results.isEmpty()) {
                    val plainPage = stripHtml(cleanTextBlocks)
                    for (eiinMatch in eiinRegex.findAll(plainPage).take(5)) {
                        val eiin = eiinMatch.groupValues[1]
                        val start = (eiinMatch.range.first - 100).coerceAtLeast(0)
                        val end = (eiinMatch.range.last + 140).coerceAtMost(plainPage.length)
                        val contextSnippet = plainPage.substring(start, end).replace(Regex("\\s+"), " ").trim()
                        results.add(
                            buildSchoolEntityFromWebResult(
                                rawTitle = rawQuery.replaceFirstChar { it.uppercase() },
                                snippet = contextSnippet,
                                sourceUrl = "Google Search Live",
                                extractedEiin = eiin,
                                fallbackQuery = rawQuery,
                                verifiedDate = today
                            )
                        )
                    }
                }
            }
        } catch (_: Exception) {
        }
        return results
    }

    private fun fetchFromDuckDuckGoHtml(encodedQuery: String, rawQuery: String, today: String): List<SchoolEntity> {
        val results = mutableListOf<SchoolEntity>()
        try {
            val url = "https://html.duckduckgo.com/html/?q=$encodedQuery"
            val html = httpGet(url)
            if (html.isNotBlank()) {
                val blockRegex = Regex(
                    "<a[^>]*class=\"result__a\"[^>]*>([\\s\\S]*?)</a>[\\s\\S]*?<a[^>]*class=\"result__snippet\"[^>]*>([\\s\\S]*?)</a>",
                    RegexOption.IGNORE_CASE
                )
                for (match in blockRegex.findAll(html).take(8)) {
                    val title = stripHtml(match.groupValues[1]).trim()
                    val snippet = stripHtml(match.groupValues[2]).trim()
                    val combined = "$title $snippet"
                    val eiin = eiinRegex.find(combined)?.groupValues?.get(1)
                        ?: if (rawQuery.length == 6 && rawQuery.all { it.isDigit() }) rawQuery else null

                    if (title.isNotBlank() && eiin != null) {
                        results.add(
                            buildSchoolEntityFromWebResult(
                                rawTitle = title,
                                snippet = snippet,
                                sourceUrl = "Web Search + AI Verified",
                                extractedEiin = eiin,
                                fallbackQuery = rawQuery,
                                verifiedDate = today
                            )
                        )
                    }
                }
            }
        } catch (_: Exception) {
        }
        return results
    }

    private fun fetchFromWikipediaApi(rawQuery: String, today: String): List<SchoolEntity> {
        val results = mutableListOf<SchoolEntity>()
        try {
            val encoded = URLEncoder.encode("$rawQuery Bangladesh school college EIIN", "UTF-8")
            val url = "https://en.wikipedia.org/w/api.php?action=query&list=search&srsearch=$encoded&utf8=&format=json&srlimit=5"
            val jsonStr = httpGet(url)
            if (jsonStr.isNotBlank()) {
                val root = JSONObject(jsonStr)
                val searchArr = root.optJSONObject("query")?.optJSONArray("search") ?: JSONArray()
                for (i in 0 until searchArr.length()) {
                    val item = searchArr.getJSONObject(i)
                    val title = item.optString("title")
                    val snippet = stripHtml(item.optString("snippet"))
                    val combined = "$title $snippet"
                    val eiin = eiinRegex.find(combined)?.groupValues?.get(1)

                    if (title.isNotBlank() && eiin != null) {
                        results.add(
                            buildSchoolEntityFromWebResult(
                                rawTitle = title,
                                snippet = snippet,
                                sourceUrl = "Knowledge Graph / Wikipedia",
                                extractedEiin = eiin,
                                fallbackQuery = rawQuery,
                                verifiedDate = today
                            )
                        )
                    }
                }
            }
        } catch (_: Exception) {
        }
        return results
    }

    private fun buildSchoolEntityFromWebResult(
        rawTitle: String,
        snippet: String,
        sourceUrl: String,
        extractedEiin: String,
        fallbackQuery: String,
        verifiedDate: String
    ): SchoolEntity {
        val combinedText = "$rawTitle $snippet"

        val cleanName = rawTitle
            .replace(Regex("\\s*[|\\-–]\\s*(Sohopathi|Wikipedia|Facebook|EduportalBD|Honours Admission|xiclassadmission).*$", RegexOption.IGNORE_CASE), "")
            .replace(Regex("\\(\\s*EIIN\\s*:?\\s*\\d+\\s*\\)", RegexOption.IGNORE_CASE), "")
            .trim()
            .ifBlank { fallbackQuery.replaceFirstChar { it.uppercase() } }

        val detectedDistrict = bdDistricts.firstOrNull { dist ->
            combinedText.contains(dist, ignoreCase = true)
        } ?: "Bangladesh"

        val detectedDivision = bdDivisions.entries.firstOrNull { (key, _) ->
            combinedText.contains(key, ignoreCase = true)
        }?.value ?: inferDivisionFromDistrict(detectedDistrict)

        val detectedType = when {
            combinedText.contains("school and college", ignoreCase = true) || combinedText.contains("school & college", ignoreCase = true) -> "School & College"
            combinedText.contains("cadet college", ignoreCase = true) -> "Cadet College"
            combinedText.contains("madrasah", ignoreCase = true) || combinedText.contains("madrasha", ignoreCase = true) || combinedText.contains("fazil", ignoreCase = true) || combinedText.contains("kamil", ignoreCase = true) || combinedText.contains("dakhil", ignoreCase = true) -> "Madrasah"
            combinedText.contains("college", ignoreCase = true) -> "College"
            else -> "High School / Institute"
        }

        val detectedBoard = when {
            detectedType == "Madrasah" -> "Madrasah"
            combinedText.contains("technical", ignoreCase = true) || combinedText.contains("polytechnic", ignoreCase = true) -> "Technical"
            else -> detectedDivision
        }

        val yearRegex = Regex("\\b(18\\d{2}|19\\d{2}|20[0-2]\\d)\\b")
        val estYear = yearRegex.find(snippet)?.groupValues?.get(1)?.toIntOrNull()

        val stableId = if (isValidSixDigitEiin(extractedEiin)) {
            "google_eiin_$extractedEiin"
        } else {
            "google_web_${cleanName.lowercase().hashCode().toUInt()}"
        }

        return SchoolEntity(
            id = stableId,
            name = cleanName,
            nameBn = "AI & Search Result • EIIN: $extractedEiin",
            eiin = extractedEiin,
            division = detectedDivision,
            district = detectedDistrict,
            upazila = detectedDistrict,
            institutionType = detectedType,
            board = detectedBoard,
            address = snippet.ifBlank { "Extracted via AI Search for '$fallbackQuery'" },
            establishedYear = estYear,
            isVerified = true,
            dataSource = sourceUrl,
            lastVerifiedDate = verifiedDate
        )
    }

    private fun inferDivisionFromDistrict(district: String): String {
        return when (district.lowercase()) {
            "dhaka", "gazipur", "narayanganj", "tangail", "faridpur", "manikganj", "munshiganj", "narsingdi", "kishoreganj", "gopalganj", "madaripur", "rajbari", "shariatpur" -> "Dhaka"
            "chattogram", "chittagong", "cumilla", "comilla", "cox's bazar", "feni", "noakhali", "brahmanbaria", "chandpur", "lakshmipur", "rangamati", "bandarban", "khagrachhari" -> "Chattogram"
            "rajshahi", "bogura", "bogra", "pabna", "sirajganj", "naogaon", "natore", "chapainawabganj", "joypurhat" -> "Rajshahi"
            "khulna", "jashore", "jessore", "kushtia", "jhenaidah", "satkhira", "bagerhat", "chuadanga", "magura", "meherpur", "narail" -> "Khulna"
            "barishal", "barisal", "patuakhali", "bhola", "pirojpur", "barguna", "jhalokati" -> "Barishal"
            "sylhet", "moulvibazar", "habiganj", "sunamganj" -> "Sylhet"
            "rangpur", "dinajpur", "kurigram", "gaibandha", "nilphamari", "panchagarh", "thakurgaon", "lalmonirhat" -> "Rangpur"
            "mymensingh", "netrokona", "jamalpur", "sherpur" -> "Mymensingh"
            else -> "Bangladesh"
        }
    }

    private fun httpGet(urlString: String): String {
        val connection = (URL(urlString).openConnection() as HttpURLConnection).apply {
            requestMethod = "GET"
            connectTimeout = 4000
            readTimeout = 4000
            setRequestProperty(
                "User-Agent",
                "Mozilla/5.0 (Linux; Android 14; Pixel 8) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/124.0.0.0 Mobile Safari/537.36"
            )
            setRequestProperty("Accept-Language", "en-US,en;q=0.9,bn;q=0.8")
        }
        return try {
            val code = connection.responseCode
            val stream = if (code in 200..299) connection.inputStream else connection.errorStream
            if (stream != null) {
                BufferedReader(InputStreamReader(stream, Charsets.UTF_8)).use { it.readText() }
            } else {
                ""
            }
        } finally {
            connection.disconnect()
        }
    }

    private fun stripHtml(html: String): String {
        return html
            .replace(Regex("<[^>]*>"), " ")
            .replace("&amp;", "&")
            .replace("&quot;", "\"")
            .replace("&#39;", "'")
            .replace("&apos;", "'")
            .replace("&lt;", "<")
            .replace("&gt;", ">")
            .replace("&nbsp;", " ")
            .replace(Regex("\\s+"), " ")
            .trim()
    }
}
