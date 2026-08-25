package com.gokcank.valutarate.presentation.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.gokcank.valutarate.data.local.entity.HistoricalRateEntity

@Composable
fun Sparkline(
    rates: List<HistoricalRateEntity>,
    modifier: Modifier = Modifier,
    width: Dp = 54.dp,
    height: Dp = 26.dp
) {
    if (rates.size < 2) {
        Box(modifier = modifier.width(width).height(height))
        return
    }

    val samplePoints = rates.takeLast(7)
    val firstRate = samplePoints.first().rate
    val lastRate = samplePoints.last().rate

    val isUpward = lastRate >= firstRate
    val trendColor = if (isUpward) Color(0xFF4CAF50) else Color(0xFFEF5350)

    val maxRate = samplePoints.maxOf { it.rate }
    val minRate = samplePoints.minOf { it.rate }
    val range = maxRate - minRate
    val actualRange = if (range == 0.0) 1.0 else range

    Canvas(
        modifier = modifier
            .width(width)
            .height(height)
            .padding(horizontal = 2.dp, vertical = 3.dp)
    ) {
        val w = size.width
        val h = size.height

        val points = samplePoints.mapIndexed { index, item ->
            val x = index * (w / (samplePoints.size - 1))
            val y = h - ((item.rate - minRate) / actualRange) * (h * 0.8f) - (h * 0.1f)
            Offset(x = x, y = y.toFloat())
        }

        if (points.isNotEmpty()) {
            val strokePath = Path().apply {
                moveTo(points.first().x, points.first().y)
                for (i in 1 until points.size) {
                    val prev = points[i - 1]
                    val curr = points[i]
                    val cx1 = prev.x + (curr.x - prev.x) / 2f
                    val cy1 = prev.y
                    val cx2 = prev.x + (curr.x - prev.x) / 2f
                    val cy2 = curr.y
                    cubicTo(cx1, cy1, cx2, cy2, curr.x, curr.y)
                }
            }

            val fillPath = Path().apply {
                addPath(strokePath)
                lineTo(points.last().x, h)
                lineTo(points.first().x, h)
                close()
            }

            // Draw subtle vertical gradient fill
            drawPath(
                path = fillPath,
                brush = Brush.verticalGradient(
                    colors = listOf(
                        trendColor.copy(alpha = 0.25f),
                        trendColor.copy(alpha = 0.0f)
                    ),
                    startY = 0f,
                    endY = h
                )
            )

            // Draw stroke curve
            drawPath(
                path = strokePath,
                color = trendColor,
                style = Stroke(
                    width = 2.dp.toPx(),
                    cap = StrokeCap.Round,
                    join = StrokeJoin.Round
                )
            )

            // Small glowing dot on the last current point
            drawCircle(
                color = trendColor,
                radius = 2.5.dp.toPx(),
                center = points.last()
            )
        }
    }
}
