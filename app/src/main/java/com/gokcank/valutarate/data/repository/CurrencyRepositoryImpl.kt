package com.gokcank.valutarate.data.repository

import com.gokcank.valutarate.data.local.dao.CurrencyDao
import com.gokcank.valutarate.data.local.dao.RateDao
import com.gokcank.valutarate.data.local.entity.CurrencyEntity
import com.gokcank.valutarate.data.local.entity.OfficialRateEntity
import com.gokcank.valutarate.data.remote.tcmb.TcmbService
import com.gokcank.valutarate.domain.model.Currency
import com.gokcank.valutarate.domain.model.OfficialRate
import com.gokcank.valutarate.domain.model.OfficialRatesResult
import com.gokcank.valutarate.domain.repository.CurrencyRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject

class CurrencyRepositoryImpl @Inject constructor(
    private val tcmbService: TcmbService,
    private val currencyDao: CurrencyDao,
    private val rateDao: RateDao
) : CurrencyRepository {

    override fun getAllCurrencies(): Flow<List<Currency>> {
        return currencyDao.getAllCurrencies().map { entities ->
            entities.map { Currency(it.code, it.name, it.isFavorite, it.isCrypto) }
        }
    }

    override fun getFavoriteCurrencies(): Flow<List<Currency>> {
        return currencyDao.getFavoriteCurrencies().map { entities ->
            entities.map { Currency(it.code, it.name, it.isFavorite, it.isCrypto) }
        }
    }

    override suspend fun toggleFavoriteStatus(code: String, isFavorite: Boolean) {
        currencyDao.updateFavoriteStatus(code, isFavorite)
    }

    override suspend fun syncCurrencies() = withContext(Dispatchers.IO) {
        try {
            val cachedRates = rateDao.getOfficialRates()
            val currenciesToInsert = if (cachedRates.isNotEmpty()) {
                cachedRates.map {
                    CurrencyEntity(
                        code = it.code,
                        name = it.name,
                        isFavorite = false,
                        isCrypto = false
                    )
                }
            } else {
                val response = tcmbService.getTodayRates()
                response.currencies.map { tcmbRate ->
                    CurrencyEntity(
                        code = tcmbRate.code,
                        name = tcmbRate.name,
                        isFavorite = false,
                        isCrypto = false
                    )
                }
            }
            
            // Add TRY as a base currency
            val tryCurrency = CurrencyEntity("TRY", "TÜRK LİRASI", false, false)
            currencyDao.insertCurrencies(currenciesToInsert + tryCurrency)
        } catch (e: Exception) {
            throw e
        }
    }

    override suspend fun getOfficialRates(forceRefresh: Boolean): OfficialRatesResult = withContext(Dispatchers.IO) {
        val cached = rateDao.getOfficialRates()
        
        if (!forceRefresh && cached.isNotEmpty()) {
            val date = cached.first().date
            return@withContext OfficialRatesResult(date, cached.map { it.toDomain() })
        }

        try {
            val response = tcmbService.getTodayRates()
            val entities = response.currencies.map {
                OfficialRateEntity(
                    code = it.code,
                    name = it.name,
                    forexBuying = it.forexBuying,
                    forexSelling = it.forexSelling,
                    banknoteBuying = it.banknoteBuying,
                    banknoteSelling = it.banknoteSelling,
                    crossRateUSD = it.crossRateUSD,
                    date = response.date,
                    lastUpdated = System.currentTimeMillis()
                )
            }
            rateDao.insertOfficialRates(entities)
            
            val historicalEntities = entities.mapNotNull {
                val rate = it.forexBuying ?: return@mapNotNull null
                com.gokcank.valutarate.data.local.entity.HistoricalRateEntity(
                    code = it.code,
                    date = response.date,
                    rate = rate,
                    timestamp = System.currentTimeMillis()
                )
            }
            rateDao.insertHistoricalRates(historicalEntities)
            

            OfficialRatesResult(response.date, entities.map { it.toDomain() })
        } catch (e: Exception) {
            if (cached.isNotEmpty()) {
                val date = cached.first().date
                OfficialRatesResult(date, cached.map { it.toDomain() })
            } else {
                throw e
            }
        }
    }

    override fun getHistoricalRatesByCode(code: String): Flow<List<com.gokcank.valutarate.data.local.entity.HistoricalRateEntity>> {
        return rateDao.getHistoricalRatesByCode(code)
    }

    private fun OfficialRateEntity.toDomain() = OfficialRate(
        code = code,
        name = name,
        forexBuying = forexBuying,
        forexSelling = forexSelling,
        banknoteBuying = banknoteBuying,
        banknoteSelling = banknoteSelling,
        crossRateUSD = crossRateUSD,
        date = date
    )


}
