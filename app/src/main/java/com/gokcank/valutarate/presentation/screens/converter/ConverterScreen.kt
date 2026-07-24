package com.gokcank.valutarate.presentation.screens.converter

import android.app.Activity
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.SwapVert
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.StarBorder
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.ui.graphics.Color
import com.gokcank.valutarate.presentation.localization.LocalAppStrings
import com.gokcank.valutarate.domain.util.CurrencyUtils
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback
import com.gokcank.valutarate.presentation.components.shimmerEffect
import androidx.compose.ui.draw.clip
import androidx.compose.material.icons.filled.Share

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ConverterScreen(
    viewModel: ConverterViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val amount by viewModel.amount.collectAsState()
    val fromCurrency by viewModel.fromCurrency.collectAsState()
    val availableCurrencies by viewModel.availableCurrencies.collectAsState()

    val context = LocalContext.current
    val strings = LocalAppStrings.current
    val haptic = LocalHapticFeedback.current

    Scaffold(
        containerColor = Color.Transparent,
        topBar = {
            TopAppBar(
                title = { Text(strings.tabConvert, fontWeight = FontWeight.Bold) },
                actions = {
                    IconButton(onClick = { 
                        val amountVal = amount.toDoubleOrNull() ?: 1.0
                        if (uiState is ConverterUiState.Success) {
                            val results = (uiState as ConverterUiState.Success).results
                            com.gokcank.valutarate.presentation.components.ImageShareUtils.shareImage(
                                context = context,
                                amount = amountVal,
                                fromCurrency = fromCurrency,
                                results = results,
                                shareTitle = strings.shareTitle,
                                isEqualTo = strings.isEqualTo,
                                languageCode = strings.languageCode
                            )
                        }
                    }) {
                        Icon(Icons.Default.Share, contentDescription = "Share", tint = MaterialTheme.colorScheme.onBackground)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color.Transparent,
                    titleContentColor = MaterialTheme.colorScheme.onBackground
                )
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(horizontal = 16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            // Input Card
            com.gokcank.valutarate.presentation.components.GlassCard(
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    OutlinedTextField(
                        value = amount,
                        onValueChange = { viewModel.updateAmount(it) },
                        label = { Text(strings.amount) },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        modifier = Modifier.fillMaxWidth()
                    )

                    CurrencyDropdown(
                        label = strings.from,
                        selectedCurrency = fromCurrency,
                        currencies = availableCurrencies.map { it.code },
                        onCurrencySelected = { viewModel.updateFromCurrency(it) },
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Output List
            when (val state = uiState) {
                is ConverterUiState.Loading -> {
                    Column(
                        modifier = Modifier.fillMaxSize(),
                        verticalArrangement = Arrangement.spacedBy(0.dp)
                    ) {
                        for (i in 0..5) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = 16.dp, vertical = 10.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Box(modifier = Modifier.size(24.dp).clip(RoundedCornerShape(4.dp)).shimmerEffect())
                                    Spacer(modifier = Modifier.width(12.dp))
                                    Box(modifier = Modifier.width(48.dp).height(20.dp).clip(RoundedCornerShape(4.dp)).shimmerEffect())
                                }
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Box(modifier = Modifier.width(80.dp).height(20.dp).clip(RoundedCornerShape(4.dp)).shimmerEffect())
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Box(modifier = Modifier.size(24.dp).clip(RoundedCornerShape(4.dp)).shimmerEffect())
                                }
                            }
                            if (i < 5) {
                                HorizontalDivider(
                                    modifier = Modifier.padding(horizontal = 16.dp),
                                    color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.1f)
                                )
                            }
                        }
                    }
                }
                is ConverterUiState.Error -> {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        Text(
                            text = state.message,
                            color = MaterialTheme.colorScheme.error
                        )
                    }
                }
                is ConverterUiState.Success -> {
                    val favoritesCodes = availableCurrencies.filter { it.isFavorite }.map { it.code }
                    
                    val starredResults = state.results.filter { favoritesCodes.contains(it.toCurrency) }
                        .sortedWith(
                            compareBy<com.gokcank.valutarate.domain.model.ConversionResult> { CurrencyUtils.getPopularityIndex(it.toCurrency) }
                                .thenBy { it.toCurrency }
                        )

                    val nonStarredResults = state.results.filter { !favoritesCodes.contains(it.toCurrency) }
                        .sortedWith(
                            compareBy<com.gokcank.valutarate.domain.model.ConversionResult> { CurrencyUtils.getPopularityIndex(it.toCurrency) }
                                .thenBy { it.toCurrency }
                        )

                    val filteredResults = starredResults + nonStarredResults

                    if (filteredResults.isEmpty()) {
                        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                            Text(
                                text = "No conversions available.",
                                color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.5f)
                            )
                        }
                    } else {
                        androidx.compose.foundation.lazy.LazyColumn(
                            verticalArrangement = Arrangement.spacedBy(0.dp),
                            contentPadding = PaddingValues(top = 8.dp, bottom = 80.dp),
                            modifier = Modifier.fillMaxSize()
                        ) {
                            itemsIndexed(filteredResults) { index, result ->
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(horizontal = 16.dp, vertical = 10.dp),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Text(
                                            text = CurrencyUtils.getCurrencyFlag(result.toCurrency),
                                            style = MaterialTheme.typography.titleLarge
                                        )
                                        Spacer(modifier = Modifier.width(12.dp))
                                        Text(
                                            text = result.toCurrency,
                                            style = MaterialTheme.typography.titleMedium,
                                            fontWeight = FontWeight.Bold,
                                            color = MaterialTheme.colorScheme.onBackground
                                        )
                                    }
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Text(
                                            text = "${String.format("%.2f", result.result)} ${CurrencyUtils.getCurrencySymbol(result.toCurrency)}",
                                            style = MaterialTheme.typography.titleMedium,
                                            fontWeight = FontWeight.Bold,
                                            color = MaterialTheme.colorScheme.onBackground
                                        )
                                        Spacer(modifier = Modifier.width(8.dp))
                                        val isFav = availableCurrencies.find { it.code == result.toCurrency }?.isFavorite == true
                                        androidx.compose.material3.IconButton(onClick = { 
                                            haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                                            viewModel.toggleFavorite(result.toCurrency, !isFav) 
                                        }) {
                                            androidx.compose.material3.Icon(
                                                imageVector = if (isFav) androidx.compose.material.icons.Icons.Default.Star else androidx.compose.material.icons.Icons.Default.StarBorder,
                                                contentDescription = "Toggle Favorite",
                                                tint = if (isFav) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onBackground.copy(alpha = 0.5f)
                                            )
                                        }
                                    }
                                }
                                if (index < filteredResults.size - 1) {
                                    HorizontalDivider(
                                        modifier = Modifier.padding(horizontal = 16.dp),
                                        color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.1f)
                                    )
                                }
                            }
                        }
                    }
                }
                else -> {}
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CurrencyDropdown(
    label: String,
    selectedCurrency: String,
    currencies: List<String>,
    onCurrencySelected: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    var expanded by remember { mutableStateOf(false) }

    ExposedDropdownMenuBox(
        expanded = expanded,
        onExpandedChange = { expanded = !expanded },
        modifier = modifier
    ) {
        OutlinedTextField(
            value = selectedCurrency,
            onValueChange = {},
            readOnly = true,
            label = { Text(label) },
            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
            modifier = Modifier.menuAnchor()
        )

        ExposedDropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false }
        ) {
            currencies.forEach { currency ->
                DropdownMenuItem(
                    text = { Text(currency) },
                    onClick = {
                        onCurrencySelected(currency)
                        expanded = false
                    }
                )
            }
        }
    }
}
