package com.gokcank.valutarate.data.preferences

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

import com.gokcank.valutarate.presentation.localization.AppLanguage

val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "settings")

enum class ThemePalette {
    PURPLE, OCEAN, FOREST, SUNSET, VICE_CITY
}

enum class AppTheme {
    SYSTEM, LIGHT, DARK
}

@Singleton
class ThemePreference @Inject constructor(
    @ApplicationContext private val context: Context
) {
    private val THEME_KEY = stringPreferencesKey("theme_palette")
    private val LANG_KEY = stringPreferencesKey("app_language")

    val themePaletteFlow: Flow<ThemePalette> = context.dataStore.data.map { preferences ->
        val themeName = preferences[THEME_KEY] ?: ThemePalette.PURPLE.name
        try {
            ThemePalette.valueOf(themeName)
        } catch (e: Exception) {
            ThemePalette.PURPLE
        }
    }

    private val APP_THEME_KEY = stringPreferencesKey("app_theme")
    val appThemeFlow: Flow<AppTheme> = context.dataStore.data.map { preferences ->
        val themeName = preferences[APP_THEME_KEY] ?: AppTheme.SYSTEM.name
        try {
            AppTheme.valueOf(themeName)
        } catch (e: Exception) {
            AppTheme.SYSTEM
        }
    }

    val appLanguageFlow: Flow<AppLanguage> = context.dataStore.data.map { preferences ->
        val langCode = preferences[LANG_KEY] ?: AppLanguage.EN.name
        try {
            AppLanguage.valueOf(langCode)
        } catch (e: Exception) {
            AppLanguage.EN
        }
    }

    suspend fun saveThemePalette(palette: ThemePalette) {
        context.dataStore.edit { preferences ->
            preferences[THEME_KEY] = palette.name
        }
    }

    suspend fun saveAppLanguage(language: AppLanguage) {
        context.dataStore.edit { preferences ->
            preferences[LANG_KEY] = language.name
        }
    }

    suspend fun saveAppTheme(theme: AppTheme) {
        context.dataStore.edit { preferences ->
            preferences[APP_THEME_KEY] = theme.name
        }
    }
}
