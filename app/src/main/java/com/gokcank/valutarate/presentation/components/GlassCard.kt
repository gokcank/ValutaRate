package com.gokcank.valutarate.presentation.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.unit.dp
import com.gokcank.valutarate.ui.theme.GlassBorderDark
import com.gokcank.valutarate.ui.theme.GlassBorderLight
import com.gokcank.valutarate.ui.theme.GlassSurfaceDark
import com.gokcank.valutarate.ui.theme.GlassSurfaceLight

@Composable
fun GlassCard(
    modifier: Modifier = Modifier,
    shape: Shape = RoundedCornerShape(16.dp),
    isDarkTheme: Boolean = isSystemInDarkTheme(),
    useGradientBorder: Boolean = true,
    content: @Composable BoxScope.() -> Unit
) {
    val surfaceColor = if (isDarkTheme) GlassSurfaceDark else GlassSurfaceLight

    val borderModifier = if (useGradientBorder) {
        val borderBrush = if (isDarkTheme) {
            Brush.linearGradient(
                colors = listOf(
                    Color.White.copy(alpha = 0.35f),
                    Color.White.copy(alpha = 0.15f),
                    Color.White.copy(alpha = 0.05f)
                )
            )
        } else {
            Brush.linearGradient(
                colors = listOf(
                    Color.White.copy(alpha = 0.95f),
                    Color.White.copy(alpha = 0.60f),
                    Color.White.copy(alpha = 0.25f)
                )
            )
        }
        Modifier.border(width = 1.dp, brush = borderBrush, shape = shape)
    } else {
        val borderColor = if (isDarkTheme) GlassBorderDark else GlassBorderLight
        Modifier.border(width = 1.dp, color = borderColor, shape = shape)
    }

    Box(
        modifier = modifier
            .clip(shape)
            .background(surfaceColor)
            .then(borderModifier),
        content = content
    )
}
