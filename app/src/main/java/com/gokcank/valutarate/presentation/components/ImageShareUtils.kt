package com.gokcank.valutarate.presentation.components

import android.content.ContentValues
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Typeface
import android.os.Environment
import android.provider.MediaStore
import com.gokcank.valutarate.domain.model.ConversionResult
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

object ImageShareUtils {

    fun shareImage(context: Context, amount: Double, fromCurrency: String, results: List<ConversionResult>, shareTitle: String, isEqualTo: String, languageCode: String) {
        val bitmap = createBitmap(amount, fromCurrency, results, isEqualTo, languageCode)
        
        val resolver = context.contentResolver
        val contentValues = ContentValues().apply {
            put(MediaStore.MediaColumns.DISPLAY_NAME, "valutarate_share_${System.currentTimeMillis()}.png")
            put(MediaStore.MediaColumns.MIME_TYPE, "image/png")
            put(MediaStore.MediaColumns.RELATIVE_PATH, Environment.DIRECTORY_PICTURES + "/ValutaRate")
        }

        val imageUri = resolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentValues)
        if (imageUri != null) {
            resolver.openOutputStream(imageUri)?.use { stream ->
                bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream)
            }

            val intent = Intent(Intent.ACTION_SEND).apply {
                type = "image/png"
                putExtra(Intent.EXTRA_STREAM, imageUri)
            }
            context.startActivity(Intent.createChooser(intent, shareTitle))
        }
    }

    private fun createBitmap(amount: Double, fromCurrency: String, results: List<ConversionResult>, isEqualTo: String, languageCode: String): Bitmap {
        val width = 1080
        val height = 600 + (results.size * 120)
        
        val bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(bitmap)
        
        // Background - Dark Deep Purple (MeshColor1)
        val bgPaint = Paint().apply {
            color = Color.parseColor("#1B113B")
            style = Paint.Style.FILL
        }
        canvas.drawRect(0f, 0f, width.toFloat(), height.toFloat(), bgPaint)
        
        // App Name
        val titlePaint = Paint().apply {
            color = Color.parseColor("#8EC5FC") // Light Pastel Blue
            textSize = 100f
            typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
            isAntiAlias = true
        }
        canvas.drawText("ValutaRate", 80f, 160f, titlePaint)
        
        // Date
        val datePaint = Paint().apply {
            color = Color.parseColor("#AAAAAA")
            textSize = 40f
            typeface = Typeface.create(Typeface.DEFAULT, Typeface.NORMAL)
            isAntiAlias = true
        }
        val dateStr = SimpleDateFormat("dd MMM yyyy, HH:mm", Locale(languageCode)).format(Date())
        canvas.drawText(dateStr, 80f, 240f, datePaint)

        // Conversion Header
        val headerPaint = Paint().apply {
            color = Color.WHITE
            textSize = 70f
            typeface = Typeface.create(Typeface.DEFAULT, Typeface.NORMAL)
            isAntiAlias = true
        }
        canvas.drawText("${String.format("%.2f", amount)} $fromCurrency $isEqualTo", 80f, 380f, headerPaint)
        
        // Results
        val resultPaint = Paint().apply {
            color = Color.WHITE
            textSize = 80f
            typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
            isAntiAlias = true
        }
        var yPos = 520f
        for (result in results) {
            val resText = "${String.format("%.2f", result.result)} ${result.toCurrency}"
            canvas.drawText("• $resText", 80f, yPos, resultPaint)
            yPos += 120f
        }
        
        return bitmap
    }
}
