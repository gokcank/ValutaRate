package com.gokcank.valutarate.presentation.screens.converter

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.gokcank.valutarate.domain.model.ConversionResult
import com.gokcank.valutarate.domain.model.Currency
import com.gokcank.valutarate.domain.usecase.ConvertCurrencyUseCase
import com.gokcank.valutarate.domain.usecase.GetCurrenciesUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ConverterViewModel @Inject constructor(
    private val convertCurrencyUseCase: ConvertCurrencyUseCase,
    private val getCurrenciesUseCase: GetCurrenciesUseCase,
    private val getRatesUseCase: com.gokcank.valutarate.domain.usecase.GetRatesUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow<ConverterUiState>(ConverterUiState.Idle)
    val uiState: StateFlow<ConverterUiState> = _uiState.asStateFlow()

    private val _amount = MutableStateFlow("100.0")
    val amount: StateFlow<String> = _amount.asStateFlow()

    private val _fromCurrency = MutableStateFlow("USD")
    val fromCurrency: StateFlow<String> = _fromCurrency.asStateFlow()

    private val _availableCurrencies = MutableStateFlow<List<Currency>>(emptyList())
    val availableCurrencies: StateFlow<List<Currency>> = _availableCurrencies.asStateFlow()

    private var _lastUpdated = 0L
    private var _isOffline = false

    init {
        loadCurrencies()
        convert()
    }

    private fun loadCurrencies() {
        viewModelScope.launch {
            try {
                // Ensure currencies are synced to local DB
                getCurrenciesUseCase.syncCurrencies()
            } catch (e: Exception) {
                // ignore, use cached
            }
            
            try {
                val rateResult = getRatesUseCase.getOfficialRates(false)
                _lastUpdated = rateResult.getOrNull()?.rates?.firstOrNull()?.lastUpdated ?: 0L
                _isOffline = rateResult.getOrNull()?.isFromCache ?: false
            } catch (e: Exception) {
            }
            getCurrenciesUseCase.getAllCurrencies().collect { list ->
                // Sort to have TRY, USD, EUR at top if desired, or just alphabetical
                _availableCurrencies.value = list.sortedBy { it.code }
                convert()
            }
        }
    }

    fun updateAmount(newAmount: String) {
        _amount.value = newAmount
        convert()
    }

    fun updateFromCurrency(currencyCode: String) {
        _fromCurrency.value = currencyCode
        convert()
    }

    private fun convert() {
        val currentAmount = _amount.value.toDoubleOrNull() ?: return
        viewModelScope.launch {
            _uiState.value = ConverterUiState.Loading
            
            val allCurrencies = _availableCurrencies.value
            if (allCurrencies.isEmpty()) {
                _uiState.value = ConverterUiState.Idle
                return@launch
            }

            val results = mutableListOf<ConversionResult>()
            var hasError = false
            var errorMessage = ""

            for (currency in allCurrencies) {
                if (currency.code == _fromCurrency.value) continue

                val result = convertCurrencyUseCase(
                    amount = currentAmount,
                    fromCurrency = _fromCurrency.value,
                    toCurrency = currency.code
                )
                
                result.onSuccess { conversion ->
                    results.add(conversion)
                }.onFailure {
                    // If a conversion fails (e.g., rate missing), we can skip it or log it
                    // but we shouldn't fail the whole list unless it's a critical error
                }
            }

            if (results.isEmpty() && allCurrencies.size > 1) {
                _uiState.value = ConverterUiState.Error("Çeviri yapılamadı.")
            } else {
                _uiState.value = ConverterUiState.Success(results.sortedBy { it.toCurrency }, _lastUpdated, _isOffline)
            }
        }
    }

    fun toggleFavorite(code: String, isFavorite: Boolean) {
        viewModelScope.launch {
            getCurrenciesUseCase.toggleFavorite(code, isFavorite)
        }
    }
}

sealed interface ConverterUiState {
    object Idle : ConverterUiState
    object Loading : ConverterUiState
    data class Success(
        val results: List<ConversionResult>,
        val lastUpdated: Long = 0L,
        val isOffline: Boolean = false
    ) : ConverterUiState
    data class Error(val message: String) : ConverterUiState
}
