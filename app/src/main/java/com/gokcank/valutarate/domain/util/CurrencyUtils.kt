package com.gokcank.valutarate.domain.util

import java.util.Currency
import java.util.Locale

object CurrencyUtils {
    var isAppOffline = false
    
    val popularOrder = listOf("USD", "EUR", "GBP", "CHF", "RUB", "SAR", "KWD")

    fun getPopularityIndex(code: String): Int {
        val index = popularOrder.indexOf(code.uppercase())
        return if (index != -1) index else Int.MAX_VALUE
    }

    fun getCurrencySymbol(code: String): String {
        return when (code.uppercase()) {
            "USD" -> "$"
            "EUR" -> "€"
            "TRY" -> "₺"
            "GBP" -> "£"
            "JPY" -> "¥"
            "CHF" -> "Fr"
            "CAD" -> "$"
            "AUD" -> "$"
            "CNY" -> "¥"
            "RUB" -> "₽"
            "INR" -> "₹"
            "BRL" -> "R$"
            "ZAR" -> "R"
            "SEK", "NOK", "DKK" -> "kr"
            "KRW" -> "₩"
            "MXN" -> "$"
            "SGD" -> "S$"
            "HKD" -> "HK$"
            "NZD" -> "NZ$"
            "PLN" -> "zł"
            "THB" -> "฿"
            "IDR" -> "Rp"
            "HUF" -> "Ft"
            "CZK" -> "Kč"
            "ILS" -> "₪"
            "CLP" -> "$"
            "PHP" -> "₱"
            "AED" -> "د.إ"
            "SAR" -> "ر.س"
            "MYR" -> "RM"
            "RON" -> "lei"
            "BGN" -> "лв"
            "KWD" -> "د.ك"
            "PKR" -> "₨"
            "QAR" -> "ر.ق"
            "JOD" -> "د.ا"
            "BHD" -> "ب.د"
            "OMR" -> "ر.ع."
            "AZN" -> "₼"
            "KZT" -> "₸"
            "XDR" -> "SDR"
            "IRR" -> "﷼"
            else -> code
        }
    }

    fun getCurrencyFlag(code: String): String {
        return when (code.uppercase()) {
            "USD" -> "🇺🇸"
            "EUR" -> "🇪🇺"
            "TRY" -> "🇹🇷"
            "GBP" -> "🇬🇧"
            "JPY" -> "🇯🇵"
            "CHF" -> "🇨🇭"
            "CAD" -> "🇨🇦"
            "AUD" -> "🇦🇺"
            "CNY" -> "🇨🇳"
            "RUB" -> "🇷🇺"
            "INR" -> "🇮🇳"
            "BRL" -> "🇧🇷"
            "ZAR" -> "🇿🇦"
            "SEK" -> "🇸🇪"
            "NOK" -> "🇳🇴"
            "DKK" -> "🇩🇰"
            "KRW" -> "🇰🇷"
            "MXN" -> "🇲🇽"
            "SGD" -> "🇸🇬"
            "HKD" -> "🇭🇰"
            "NZD" -> "🇳🇿"
            "PLN" -> "🇵🇱"
            "THB" -> "🇹🇭"
            "IDR" -> "🇮🇩"
            "HUF" -> "🇭🇺"
            "CZK" -> "🇨🇿"
            "ILS" -> "🇮🇱"
            "CLP" -> "🇨🇱"
            "PHP" -> "🇵🇭"
            "AED" -> "🇦🇪"
            "SAR" -> "🇸🇦"
            "MYR" -> "🇲🇾"
            "RON" -> "🇷🇴"
            "BGN" -> "🇧🇬"
            "KWD" -> "🇰🇼"
            "PKR" -> "🇵🇰"
            "QAR" -> "🇶🇦"
            "JOD" -> "🇯🇴"
            "BHD" -> "🇧🇭"
            "OMR" -> "🇴🇲"
            "AZN" -> "🇦🇿"
            "KZT" -> "🇰🇿"
            "IRR" -> "🇮🇷"
            "XDR" -> "🌐"
            else -> "🏳️"
        }
    }

    fun getCurrencyName(code: String, languageCode: String): String {
        return try {
            val currency = Currency.getInstance(code)
            currency.getDisplayName(Locale(languageCode))
        } catch (e: Exception) {
            code
        }
    }

    /**
     * Calculates the next TCMB rate publication timestamp (15:30 Istanbul Time).
     *
     * Approach 2 — Dynamic XML date detection:
     * If the current time is past 15:30 IST and the TCMB bulletin date (from XML)
     * does NOT match today's date, the system concludes that TCMB did not publish
     * rates today (e.g., public holiday or arife). The target is then advanced to
     * the next calendar day and weekends are skipped automatically.
     *
     * @param tcmbBulletinDate The date string from TCMB XML (format: "dd.MM.yyyy").
     *                         Pass null/empty to use simple next-15:30 logic.
     */
    fun getNextTcmbUpdateTimeMillis(tcmbBulletinDate: String? = null): Long {
        val istZone = java.time.ZoneId.of("Europe/Istanbul")
        val now = java.time.ZonedDateTime.now(istZone)
        val today = now.toLocalDate()
        val cutoff = now.withHour(15).withMinute(30).withSecond(0).withNano(0)

        // Parse the TCMB bulletin date from XML (format: "dd.MM.yyyy")
        val bulletinDate: java.time.LocalDate? = tcmbBulletinDate
            ?.takeIf { it.isNotBlank() }
            ?.let {
                runCatching {
                    java.time.LocalDate.parse(it, java.time.format.DateTimeFormatter.ofPattern("dd.MM.yyyy"))
                }.getOrNull()
            }

        // Determine starting point for the next update target
        var nextUpdate = today.atTime(15, 30).atZone(istZone)

        val isPast1530 = now.isAfter(cutoff)
        val tcmbPublishedToday = bulletinDate != null && bulletinDate == today

        when {
            // If bulletin already published today and it's past 15:30 — target tomorrow
            isPast1530 && tcmbPublishedToday -> {
                nextUpdate = nextUpdate.plusDays(1)
            }
            // If past 15:30 but TCMB did NOT publish today (holiday/arife) — skip today, target tomorrow
            isPast1530 && !tcmbPublishedToday && bulletinDate != null -> {
                nextUpdate = nextUpdate.plusDays(1)
            }
            // Before 15:30 on a weekday — target is today 15:30
            // (no change needed)
        }

        // Skip weekends — TCMB never publishes on Saturday or Sunday
        while (
            nextUpdate.dayOfWeek == java.time.DayOfWeek.SATURDAY ||
            nextUpdate.dayOfWeek == java.time.DayOfWeek.SUNDAY
        ) {
            nextUpdate = nextUpdate.plusDays(1)
        }

        return nextUpdate.toInstant().toEpochMilli()
    }

    /**
     * Extracts the actual bulletin publication time from the last updated timestamp.
     * Returns "15:30" as default fallback. Used to dynamically show the real
     * publication time on the TCMB info card (e.g., "12:05" on arife days).
     */
    fun extractTcmbTime(lastUpdatedMillis: Long): String {
        if (lastUpdatedMillis <= 0L) return "15:30"
        return try {
            val istZone = java.time.ZoneId.of("Europe/Istanbul")
            val zdt = java.time.Instant.ofEpochMilli(lastUpdatedMillis).atZone(istZone)
            String.format("%02d:%02d", zdt.hour, zdt.minute)
        } catch (e: Exception) {
            "15:30"
        }
    }
}
