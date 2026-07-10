package com.gokcank.valutarate.domain.usecase


import com.gokcank.valutarate.domain.model.OfficialRate
import com.gokcank.valutarate.domain.repository.CurrencyRepository
import javax.inject.Inject
import com.gokcank.valutarate.domain.model.OfficialRatesResult

class GetRatesUseCase @Inject constructor(
    private val repository: CurrencyRepository
) {
    suspend fun getOfficialRates(forceRefresh: Boolean = false): Result<OfficialRatesResult> {
        return try {
            val result = repository.getOfficialRates(forceRefresh)
            Result.success(result)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    fun getHistoricalRatesByCode(code: String): kotlinx.coroutines.flow.Flow<List<com.gokcank.valutarate.data.local.entity.HistoricalRateEntity>> {
        return repository.getHistoricalRatesByCode(code)
    }
}
