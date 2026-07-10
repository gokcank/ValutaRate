package com.gokcank.valutarate.domain.repository

import com.gokcank.valutarate.domain.model.Currency

import com.gokcank.valutarate.domain.model.OfficialRate
import com.gokcank.valutarate.domain.model.OfficialRatesResult
import kotlinx.coroutines.flow.Flow

interface CurrencyRepository {
    fun getAllCurrencies(): Flow<List<Currency>>
    fun getFavoriteCurrencies(): Flow<List<Currency>>
    suspend fun toggleFavoriteStatus(code: String, isFavorite: Boolean)
    suspend fun syncCurrencies()

    suspend fun getOfficialRates(forceRefresh: Boolean = false): OfficialRatesResult
    fun getHistoricalRatesByCode(code: String): Flow<List<com.gokcank.valutarate.data.local.entity.HistoricalRateEntity>>
}
