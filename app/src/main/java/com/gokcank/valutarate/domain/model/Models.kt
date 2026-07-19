package com.gokcank.valutarate.domain.model

data class Currency(
    val code: String,
    val name: String,
    val isFavorite: Boolean = false,
    val isCrypto: Boolean = false
)

data class OfficialRate(
    val code: String,
    val name: String,
    val forexBuying: Double?,
    val forexSelling: Double?,
    val banknoteBuying: Double?,
    val banknoteSelling: Double?,
    val crossRateUSD: Double?,
    val date: String,
    val lastUpdated: Long = 0L
)

data class OfficialRatesResult(
    val date: String,
    val rates: List<OfficialRate>,
    val isFromCache: Boolean = false
)

data class GlobalRate(
    val code: String,
    val rateAgainstUsd: Double,
    val date: String
)

data class ConversionResult(
    val amount: Double,
    val fromCurrency: String,
    val toCurrency: String,
    val result: Double,
    val rateUsed: Double,
    val isOfficialRate: Boolean
)
