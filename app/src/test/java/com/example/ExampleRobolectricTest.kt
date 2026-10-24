package com.example

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import com.example.data.model.PricingCatalog
import com.example.data.remote.GoogleSchoolSearchService
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [34])
class ExampleRobolectricTest {

    @Test
    fun `read string from context`() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        val appName = context.getString(R.string.app_name)
        assertEquals("EIIN Finder & Notes", appName)
    }

    @Test
    fun `verify school credit pricing formula`() {
        val packages = PricingCatalog.schoolPackages
        assertEquals(20, packages.size)

        // 100 schools -> $1.01
        assertEquals(100, packages[0].schoolRecords)
        assertEquals(1.01, packages[0].priceUsd, 0.001)

        // 500 schools -> $5.01
        assertEquals(500, packages[4].schoolRecords)
        assertEquals(5.01, packages[4].priceUsd, 0.001)

        // 2000 schools -> $20.01
        assertEquals(2000, packages[19].schoolRecords)
        assertEquals(20.01, packages[19].priceUsd, 0.001)
    }

    @Test
    fun `verify class note pricing formula`() {
        val packages = PricingCatalog.notePackages
        assertEquals(20, packages.size)

        // 1 note -> $1.01
        assertEquals(1, packages[0].noteCount)
        assertEquals(1.01, packages[0].priceUsd, 0.001)

        // 10 notes -> $10.01
        assertEquals(10, packages[9].noteCount)
        assertEquals(10.01, packages[9].priceUsd, 0.001)

        // 20 notes -> $20.01
        assertEquals(20, packages[19].noteCount)
        assertEquals(20.01, packages[19].priceUsd, 0.001)
    }

    @Test
    fun `verify qa pack pricing formula`() {
        val packages = PricingCatalog.qaPackages
        assertEquals(20, packages.size)

        // 1 question -> $1.01
        assertEquals(1, packages[0].questionCount)
        assertEquals(1.01, packages[0].priceUsd, 0.001)
        assertEquals("qa_credits_1", packages[0].sku)

        // 5 questions -> $5.01
        assertEquals(5, packages[4].questionCount)
        assertEquals(5.01, packages[4].priceUsd, 0.001)

        // 20 questions -> $20.01
        assertEquals(20, packages[19].questionCount)
        assertEquals(20.01, packages[19].priceUsd, 0.001)
        assertEquals("qa_credits_20", packages[19].sku)
    }

    @Test
    fun `verify google search dom eiin extraction`() {
        val service = GoogleSchoolSearchService()
        val sampleGoogleDomJson = """
            [
                {
                    "title": "Cantonment English School & College - Sohopathi",
                    "snippet": "Cantonment English School & College is an academic institute located at Bayezid Bostami Chattogram. Its EIIN is 104271.",
                    "link": "https://www.google.com/search?q=Cantonment+English+School+EIIN"
                }
            ]
        """.trimIndent()

        val extracted = service.parseWebViewDomResults("Cantonment English School", sampleGoogleDomJson)
        assertTrue(extracted.isNotEmpty())
        assertEquals("104271", extracted.first().eiin)
        assertEquals("Chattogram", extracted.first().division)
    }
}
