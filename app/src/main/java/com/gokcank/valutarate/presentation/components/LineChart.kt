package com.gokcank.valutarate.presentation.components

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.*
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.font.FontWeight
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
        Box(modifier = modifier, contentAlignment = Alignment.Center) {
            Text(
                text = "Geçmiş veri bulunamadı.",
                color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.5f),
                style = MaterialTheme.typography.bodyMedium
            )
        }
        return
    }

    var selectedIndex by remember(data) { mutableStateOf<Int?>(null) }
    val progress = remember(data) { Animatable(0f) }

    LaunchedEffect(data) {
        progress.snapTo(0f)
        progress.animateTo(1f, animationSpec = tween(durationMillis = 600))
    }

    val maxRate = remember(data) { data.maxOf { it.rate } }
    val minRate = remember(data) { data.minOf { it.rate } }

    Column(modifier = modifier) {
        // Active Point Tooltip / Info Badge
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(36.dp)
                .padding(horizontal = 8.dp),
            contentAlignment = Alignment.Center
        ) {
            if (selectedIndex != null && selectedIndex!! in data.indices) {
                val selectedItem = data[selectedIndex!!]
                GlassCard(
                    shape = RoundedCornerShape(12.dp),
                    useGradientBorder = true,
                    modifier = Modifier.wrapContentSize()
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 4.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Text(
                            text = selectedItem.date,
                            style = MaterialTheme.typography.labelMedium,
                            color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.7f)
                        )
                        Text(
                            text = String.format(Locale.getDefault(), "₺%.4f", selectedItem.rate),
                            style = MaterialTheme.typography.labelMedium,
                            fontWeight = FontWeight.Bold,
                            color = lineColor
                        )
                    }
                }
            } else {
                Text(
                    text = "Detay için grafiğe dokunun",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.4f)
                )
            }
        }

        Spacer(modifier = Modifier.height(4.dp))

        Canvas(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
                .padding(vertical = 8.dp, horizontal = 12.dp)
                .pointerInput(data) {
                    detectTapGestures(
                        onPress = { offset ->
                            val width = size.width
                            val step = if (data.size > 1) width / (data.size - 1) else width
                            val tappedIndex = ((offset.x + step / 2) / step).toInt().coerceIn(0, data.size - 1)
                            selectedIndex = tappedIndex
                        }
                    )
                }
                .pointerInput(data) {
                    detectDragGestures(
                        onDragStart = { offset ->
                            val width = size.width
                            val step = if (data.size > 1) width / (data.size - 1) else width
                            val draggedIndex = ((offset.x + step / 2) / step).toInt().coerceIn(0, data.size - 1)
                            selectedIndex = draggedIndex
                        },
                        onDrag = { change, _ ->
                            val width = size.width
                            val step = if (data.size > 1) width / (data.size - 1) else width
                            val draggedIndex = ((change.position.x + step / 2) / step).toInt().coerceIn(0, data.size - 1)
                            selectedIndex = draggedIndex
                        }
                    )
                }
        ) {
            val width = size.width
            val height = size.height

            val range = maxRate - minRate
            val actualRange = if (range == 0.0) 1.0 else range

            val points = data.mapIndexed { index, item ->
                val x = if (data.size > 1) index * (width / (data.size - 1)) else width / 2
                val targetY = height - ((item.rate - minRate) / actualRange) * (height * 0.75f) - (height * 0.12f)
                // Animate Y position with progress
                val y = height - (height - targetY) * progress.value
                Offset(x = x, y = y.toFloat())
            }

            if (points.isNotEmpty()) {
                // 1. Build Smooth Cubic Bezier Path
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

                // 2. Build and Draw Gradient Fill Under the Curve
                val fillPath = Path().apply {
                    addPath(strokePath)
                    lineTo(points.last().x, height)
                    lineTo(points.first().x, height)
                    close()
                }

                drawPath(
                    path = fillPath,
                    brush = Brush.verticalGradient(
                        colors = listOf(
                            lineColor.copy(alpha = 0.35f * progress.value),
                            lineColor.copy(alpha = 0.02f * progress.value)
                        ),
                        startY = 0f,
                        endY = height
                    )
                )

                // 3. Draw Stroke Line
                drawPath(
                    path = strokePath,
                    color = lineColor,
                    style = Stroke(
                        width = 3.5.dp.toPx(),
                        cap = StrokeCap.Round,
                        join = StrokeJoin.Round
                    )
                )

                // 4. Draw Selected Indicator Line & Points
                points.forEachIndexed { index, point ->
                    val isSelected = index == selectedIndex

                    if (isSelected) {
                        // Vertical guideline
                        drawLine(
                            color = lineColor.copy(alpha = 0.5f),
                            start = Offset(point.x, 0f),
                            end = Offset(point.x, height),
                            strokeWidth = 1.5.dp.toPx(),
                            pathEffect = PathEffect.dashPathEffect(floatArrayOf(10f, 10f), 0f)
                        )

                        // Glowing outer circle
                        drawCircle(
                            color = lineColor.copy(alpha = 0.3f),
                            radius = 9.dp.toPx(),
                            center = point
                        )
                        // Inner circle
                        drawCircle(
                            color = lineColor,
                            radius = 5.dp.toPx(),
                            center = point
                        )
                        // Core center dot
                        drawCircle(
                            color = Color.White,
                            radius = 2.5.dp.toPx(),
                            center = point
                        )
                    } else {
                        // Regular point
                        drawCircle(
                            color = lineColor,
                            radius = 3.dp.toPx(),
                            center = point
                        )
                    }
                }
            }
        }

        // X-Axis Dates
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 12.dp, vertical = 4.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            if (data.isNotEmpty()) {
                val inputFormat = SimpleDateFormat("dd.MM.yyyy", Locale.US)
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

                if (data.size > 2) {
                    val midItem = data[data.size / 2]
                    val midDateText = try {
                        inputFormat.parse(midItem.date)?.let { outputFormat.format(it) } ?: midItem.date
                    } catch (e: Exception) { midItem.date }

                    Text(
                        text = midDateText,
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.4f)
                    )
                }

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
