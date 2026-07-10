package com.gokcank.valutarate.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.gokcank.valutarate.data.local.entity.OfficialRateEntity

@Dao
interface RateDao {
    @Query("SELECT * FROM official_rates ORDER BY code ASC")
    suspend fun getOfficialRates(): List<OfficialRateEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOfficialRates(rates: List<OfficialRateEntity>)

    @Query("DELETE FROM official_rates")
    suspend fun clearOfficialRates()

    @Query("SELECT * FROM historical_rates WHERE code = :code ORDER BY date ASC")
    fun getHistoricalRatesByCode(code: String): kotlinx.coroutines.flow.Flow<List<com.gokcank.valutarate.data.local.entity.HistoricalRateEntity>>

    @Query("SELECT * FROM historical_rates WHERE code = :code ORDER BY date ASC")
    suspend fun getHistoricalRatesListByCode(code: String): List<com.gokcank.valutarate.data.local.entity.HistoricalRateEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertHistoricalRates(rates: List<com.gokcank.valutarate.data.local.entity.HistoricalRateEntity>)
}
