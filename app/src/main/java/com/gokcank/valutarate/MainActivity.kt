package com.gokcank.valutarate

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.material3.Text
import androidx.navigation.compose.rememberNavController
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.compose.material.icons.Icons
import androidx.compose.material3.MaterialTheme
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.List
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import com.google.android.gms.ads.MobileAds
import com.google.android.ump.ConsentRequestParameters
import com.google.android.ump.UserMessagingPlatform
import com.gokcank.valutarate.presentation.navigation.Screen
import com.gokcank.valutarate.ui.theme.ValutaRateTheme
import dagger.hilt.android.AndroidEntryPoint
import androidx.compose.foundation.layout.Column
import com.gokcank.valutarate.presentation.ads.AdMobBanner
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.ui.graphics.Color

import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import com.gokcank.valutarate.data.preferences.ThemePreference
import com.gokcank.valutarate.data.preferences.ThemePalette
import javax.inject.Inject

import androidx.compose.runtime.CompositionLocalProvider
import com.gokcank.valutarate.presentation.localization.*
import androidx.navigation.NavController
import androidx.compose.material.icons.filled.SwapHoriz
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.ui.graphics.vector.ImageVector

data class NavigationItem(val route: String, val title: String, val icon: ImageVector)

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    @Inject
    lateinit var themePreference: ThemePreference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // UMP Consent Flow
        val consentInformation = UserMessagingPlatform.getConsentInformation(this)
        val params = ConsentRequestParameters.Builder().build()

        consentInformation.requestConsentInfoUpdate(
            this,
            params,
            {
                UserMessagingPlatform.loadAndShowConsentFormIfRequired(
                    this@MainActivity,
                    { loadAndShowError ->
                        if (consentInformation.canRequestAds()) {
                            MobileAds.initialize(this@MainActivity) {}
                        }
                    }
                )
            },
            { requestConsentError ->
                // Consent gathering failed.
            }
        )

        // Initialize in parallel if possible
        if (consentInformation.canRequestAds()) {
            MobileAds.initialize(this) {}
        }
        setContent {
            val themePalette by themePreference.themePaletteFlow.collectAsState(initial = ThemePalette.PURPLE)
            val appLanguage by themePreference.appLanguageFlow.collectAsState(initial = AppLanguage.EN)
            
            val appStrings = when (appLanguage) {
                AppLanguage.TR -> trStrings
                AppLanguage.EN -> enStrings
                AppLanguage.DE -> deStrings
                AppLanguage.FR -> frStrings
            }

            CompositionLocalProvider(LocalAppStrings provides appStrings) {
                ValutaRateTheme {
                    ValutaRateAppContent(themePalette = themePalette)
                }
            }
        }
    }
}

@Composable
fun BottomNavigationBar(navController: NavController, currentRoute: String?) {
    val strings = LocalAppStrings.current
    
    val items = listOf(
        NavigationItem(Screen.Home.route, strings.tabHome, Icons.Default.Home),
        NavigationItem(Screen.Converter.route, strings.tabConvert, Icons.Default.SwapHoriz),
        NavigationItem(Screen.Settings.route, strings.tabSettings, Icons.Default.Settings)
    )

    NavigationBar(
        containerColor = Color.Transparent,
        contentColor = MaterialTheme.colorScheme.onBackground
    ) {
        items.forEach { item ->
            NavigationBarItem(
                icon = { Icon(item.icon, contentDescription = item.title) },
                label = { Text(item.title) },
                selected = currentRoute == item.route,
                onClick = {
                    navController.navigate(item.route) {
                        popUpTo(navController.graph.startDestinationId) { saveState = true }
                        launchSingleTop = true
                        restoreState = true
                    }
                },
                colors = NavigationBarItemDefaults.colors(
                    selectedIconColor = MaterialTheme.colorScheme.primary,
                    unselectedIconColor = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.6f),
                    selectedTextColor = MaterialTheme.colorScheme.primary,
                    unselectedTextColor = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.6f),
                    indicatorColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.3f)
                )
            )
        }
    }
}

@Composable
fun ValutaRateAppContent(themePalette: ThemePalette = ThemePalette.PURPLE) {
    val navController = rememberNavController()
    val navBackStackEntry = navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry.value?.destination?.route

    Box(modifier = Modifier.fillMaxSize()) {
        com.gokcank.valutarate.presentation.components.MeshBackground(themePalette = themePalette)
        
        Scaffold(
            containerColor = Color.Transparent,
            bottomBar = {
                Column {
                    AdMobBanner()
                    BottomNavigationBar(navController = navController, currentRoute = currentRoute)
                }
            }
        ) { innerPadding ->
            NavHost(
                navController = navController,
                startDestination = Screen.Home.route,
                modifier = Modifier.padding(innerPadding)
            ) {
                composable(Screen.Home.route) {
                    com.gokcank.valutarate.presentation.screens.home.HomeScreen()
                }
                composable(Screen.Converter.route) {
                    com.gokcank.valutarate.presentation.screens.converter.ConverterScreen()
                }
                composable(Screen.Settings.route) {
                    com.gokcank.valutarate.presentation.screens.settings.SettingsScreen()
                }
            }
        }
    }
}
