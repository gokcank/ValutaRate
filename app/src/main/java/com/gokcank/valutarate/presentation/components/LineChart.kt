package com.gokcank.valutarate.presentation.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.*
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.unit.dp
import com.gokcank.valutarate.data.local.entity.HistoricalRateEntity
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun LineChart(
    data: List<HistoricalRateEntity>,
    modifier: Modifier = Modifier,
    lineColor: Color = MaterialTheme.colorScheme.primary
) {
    if (data.isEmpty()) {
        Box(modifier = modifier, contentAlignment = androidx.compose.ui.Alignment.Center) {
            Text("Geçmiş veri bulunamadı.", color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.5f))
        }
        return
    }

    val maxRate = remember(data) { data.maxOf { it.rate } }
    val minRate = remember(data) { data.minOf { it.rate } }

    Column(modifier = modifier) {
        Canvas(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
                .padding(vertical = 16.dp, horizontal = 8.dp)
        ) {
            val width = size.width
            val height = size.height

            val range = maxRate - minRate
            val actualRange = if (range == 0.0) 1.0 else range

            val points = data.mapIndexed { index, item ->
                val x = if (data.size > 1) index * (width / (data.size - 1)) else width / 2
                val y = height - ((item.rate - minRate) / actualRange) * height
                Offset(x = x, y = y.toFloat())
            }

            val path = Path().apply {
                points.forEachIndexed { index, point ->
                    if (index == 0) {
                        moveTo(point.x, point.y)
                    } else {
                        // A simple curve can be added here, but lines are okay for now
                        lineTo(point.x, point.y)
                    }
                }
            }

            drawPath(
                path = path,
                color = lineColor,
                style = Stroke(
                    width = 4.dp.toPx(),
                    cap = StrokeCap.Round,
                    join = StrokeJoin.Round
                )
            )

            // Draw points
            points.forEach { point ->
                drawCircle(
                    color = lineColor,
                    radius = 4.dp.toPx(),
                    center = point
                )
            }
        }
        
        // Draw X-axis dates (first and last only to save space)
        Row(
            modifier = Modifier.fillMaxWidth().padding(horizontal = 8.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            if (data.isNotEmpty()) {
                val inputFormat = SimpleDateFormat("dd/MM/yyyy", Locale.US)
                val outputFormat = SimpleDateFormat("dd MMM", Locale.getDefault())
                
                val firstDateStr = data.first().date
                val firstDateText = try {
                    inputFormat.parse(firstDateStr)?.let { outputFormat.format(it) } ?: firstDateStr
                } catch (e: Exception) { firstDateStr }
                
                val lastDateStr = data.last().date
                val lastDateText = try {
                    inputFormat.parse(lastDateStr)?.let { outputFormat.format(it) } ?: lastDateStr
                } catch (e: Exception) { lastDateStr }

                Text(
                    text = firstDateText,
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.6f)
                )
                if (data.size > 1) {
                    Text(
                        text = lastDateText,
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.6f)
                    )
                }
            }
        }
    }
}
