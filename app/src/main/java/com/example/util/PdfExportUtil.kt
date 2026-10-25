package com.example.util

import android.content.ContentValues
import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Typeface
import android.graphics.pdf.PdfDocument
import android.os.Build
import android.os.Environment
import android.provider.MediaStore
import java.io.File
import java.io.FileOutputStream

object PdfExportUtil {

    /**
     * Generates a real, multi-page PDF document from a Class Note or Q&A Solution
     * and saves it directly to the device's Downloads folder so the user can open,
     * print, or share it in real life without requiring storage permissions on Android 10+.
     */
    fun exportDocumentToDownloadsPdf(
        context: Context,
        fileNamePrefix: String,
        title: String,
        subtitle: String,
        bodyContent: String
    ): Result<String> {
        return try {
            val pdfDocument = PdfDocument()
            val pageWidth = 595 // A4 width in points
            val pageHeight = 842 // A4 height in points
            val margin = 42f
            val contentWidth = pageWidth - (margin * 2)

            val titlePaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
                color = Color.rgb(13, 71, 161)
                textSize = 16f
                typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
            }
            val subPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
                color = Color.rgb(0, 105, 92)
                textSize = 11f
                typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
            }
            val bodyPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
                color = Color.rgb(33, 33, 33)
                textSize = 11f
                typeface = Typeface.create(Typeface.DEFAULT, Typeface.NORMAL)
            }
            val footerPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
                color = Color.rgb(120, 120, 120)
                textSize = 9f
            }

            // Wrap lines to fit contentWidth
            val wrappedLines = mutableListOf<String>()
            for (paragraph in bodyContent.split("\n")) {
                if (paragraph.isBlank()) {
                    wrappedLines.add("")
                    continue
                }
                val words = paragraph.split(" ")
                var currentLine = StringBuilder()
                for (word in words) {
                    val testLine = if (currentLine.isEmpty()) word else "${currentLine} $word"
                    if (bodyPaint.measureText(testLine) <= contentWidth) {
                        currentLine = StringBuilder(testLine)
                    } else {
                        if (currentLine.isNotEmpty()) {
                            wrappedLines.add(currentLine.toString())
                        }
                        currentLine = StringBuilder(word)
                    }
                }
                if (currentLine.isNotEmpty()) {
                    wrappedLines.add(currentLine.toString())
                }
            }

            var lineIndex = 0
            var pageNumber = 1

            while (lineIndex < wrappedLines.size || pageNumber == 1) {
                val pageInfo = PdfDocument.PageInfo.Builder(pageWidth, pageHeight, pageNumber).create()
                val page = pdfDocument.startPage(pageInfo)
                val canvas: Canvas = page.canvas

                var y = margin + 16f

                if (pageNumber == 1) {
                    // Header Banner on Page 1
                    canvas.drawText(title.take(65), margin, y, titlePaint)
                    y += 18f
                    if (title.length > 65) {
                        canvas.drawText(title.substring(65).take(65), margin, y, titlePaint)
                        y += 18f
                    }
                    canvas.drawText(subtitle.take(85), margin, y, subPaint)
                    y += 14f
                    canvas.drawLine(margin, y, pageWidth - margin, y, subPaint)
                    y += 22f
                }

                val maxLineY = pageHeight - margin - 28f
                while (lineIndex < wrappedLines.size && y <= maxLineY) {
                    val line = wrappedLines[lineIndex]
                    val isHeading = line.startsWith("#") || line.startsWith("CHAPTER") || line.startsWith("SECTION") || line.startsWith("PART")
                    val paintToUse = if (isHeading) subPaint else bodyPaint
                    canvas.drawText(line.removePrefix("### ").removePrefix("# "), margin, y, paintToUse)
                    y += if (isHeading) 18f else 15f
                    lineIndex++
                }

                // Footer
                canvas.drawText(
                    "EIIN Finder & Notes • Verified Academic Document • Page $pageNumber",
                    margin,
                    pageHeight - 20f,
                    footerPaint
                )

                pdfDocument.finishPage(page)
                pageNumber++
                if (lineIndex >= wrappedLines.size) break
            }

            val safeName = fileNamePrefix.replace(Regex("[^a-zA-Z0-9_\\-]"), "_").take(40)
            val finalFileName = "${safeName}_${System.currentTimeMillis() % 10000}.pdf"

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                val resolver = context.contentResolver
                val contentValues = ContentValues().apply {
                    put(MediaStore.MediaColumns.DISPLAY_NAME, finalFileName)
                    put(MediaStore.MediaColumns.MIME_TYPE, "application/pdf")
                    put(MediaStore.MediaColumns.RELATIVE_PATH, Environment.DIRECTORY_DOWNLOADS)
                }
                val uri = resolver.insert(MediaStore.Downloads.EXTERNAL_CONTENT_URI, contentValues)
                    ?: throw IllegalStateException("Could not create PDF entry in Downloads")
                resolver.openOutputStream(uri)?.use { out ->
                    pdfDocument.writeTo(out)
                }
            } else {
                val outFile = File(context.getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS), finalFileName)
                FileOutputStream(outFile).use { out ->
                    pdfDocument.writeTo(out)
                }
            }

            pdfDocument.close()
            Result.success(finalFileName)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
