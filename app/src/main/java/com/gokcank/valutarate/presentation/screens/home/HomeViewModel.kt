package com.gokcank.valutarate.presentation.screens.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.gokcank.valutarate.domain.model.Currency
import com.gokcank.valutarate.domain.model.OfficialRate
import com.gokcank.valutarate.domain.usecase.GetCurrenciesUseCase
import com.gokcank.valutarate.domain.usecase.GetRatesUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val getRatesUseCase: GetRatesUseCase,
    private val getCurrenciesUseCase: GetCurrenciesUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow<HomeUiState>(HomeUiState.Loading)
    val uiState: StateFlow<HomeUiState> = _uiState.asStateFlow()

    private val _selectedCurrencyHistory = MutableStateFlow<List<com.gokcank.valutarate.data.local.entity.HistoricalRateEntity>?>(null)
    val selectedCurrencyHistory: StateFlow<List<com.gokcank.valutarate.data.local.entity.HistoricalRateEntity>?> = _selectedCurrencyHistory.asStateFlow()

    private var historyJob: kotlinx.coroutines.Job? = null

    init {
        loadData(forceRefresh = true)
    }

    fun loadData(forceRefresh: Boolean = false) {
        viewModelScope.launch {
            _uiState.value = HomeUiState.Loading
            
            // Try fetching official rates first
            val result = getRatesUseCase.getOfficialRates(forceRefresh)
            
            result.onSuccess { officialRatesResult ->
                _uiState.value = HomeUiState.Success(
                    officialRates = officialRatesResult.rates,
                    tcmbDate = officialRatesResult.date,
                    isFromCache = officialRatesResult.isFromCache,
                    lastUpdated = officialRatesResult.rates.firstOrNull()?.lastUpdated ?: 0L
                )
                
                // Collect favorite currencies to update the state
                launch {
                    getCurrenciesUseCase.getAllCurrencies().collect { currencies ->
                        val currentState = _uiState.value
                        if (currentState is HomeUiState.Success) {
                            _uiState.value = currentState.copy(favoriteCurrencies = currencies)
                        }
                    }
                }
                
                // Sync currencies in background so ConverterScreen has data
                launch {
                    try {
                        getCurrenciesUseCase.syncCurrencies()
                    } catch (e: Exception) {
                        // ignore
                    }
                }
            }.onFailure { error ->
                _uiState.value = HomeUiState.Error(error.message ?: "An error occurred")
            }
        }
    }

    fun toggleFavorite(code: String, isFavorite: Boolean) {
        viewModelScope.launch {
            getCurrenciesUseCase.toggleFavorite(code, isFavorite)
        }
    }

    fun selectCurrencyForHistory(code: String) {
        historyJob?.cancel()
        historyJob = viewModelScope.launch {
            getRatesUseCase.getHistoricalRatesByCode(code).collect { history ->
                _selectedCurrencyHistory.value = history
            }
        }
    }

    fun clearSelectedCurrency() {
        historyJob?.cancel()
        _selectedCurrencyHistory.value = null
    }
}

sealed interface HomeUiState {
    object Loading : HomeUiState
    data class Success(
        val officialRates: List<OfficialRate>,
        val tcmbDate: String,
        val favoriteCurrencies: List<Currency> = emptyList(),
        val isFromCache: Boolean = false,
        val lastUpdated: Long = 0L
    ) : HomeUiState
    data class Error(val message: String) : HomeUiState
}
