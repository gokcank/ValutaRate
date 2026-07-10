package com.gokcank.valutarate.domain.util

import java.util.Currency
import java.util.Locale

object CurrencyUtils {
    
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
}
