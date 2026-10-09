package com.example.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.unit.dp
import com.example.ui.theme.NeonEmerald

@Composable
fun SparklineChart(
    dataPoints: List<Float> = listOf(20f, 25f, 22f, 30f, 28f, 35f, 40f, 38f, 45f),
    lineColor: Color = NeonEmerald,
    modifier: Modifier = Modifier
        .width(52.dp)
        .height(24.dp)
) {
    Canvas(modifier = modifier) {
        if (dataPoints.size < 2) return@Canvas

        val maxVal = dataPoints.maxOrNull() ?: 1f
        val minVal = dataPoints.minOrNull() ?: 0f
        val range = if (maxVal == minVal) 1f else maxVal - minVal

        val stepX = size.width / (dataPoints.size - 1)
        val path = Path()
        val fillPath = Path()

        dataPoints.forEachIndexed { index, value ->
            val x = index * stepX
            val normalizedY = (value - minVal) / range
            val y = size.height - (normalizedY * (size.height - 4f)) - 2f

            if (index == 0) {
                path.moveTo(x, y)
                fillPath.moveTo(x, size.height)
                fillPath.lineTo(x, y)
            } else {
                path.lineTo(x, y)
                fillPath.lineTo(x, y)
            }
        }

        fillPath.lineTo(size.width, size.height)
        fillPath.close()

        // Draw soft gradient fill
        drawPath(
            path = fillPath,
            brush = Brush.verticalGradient(
                colors = listOf(
                    lineColor.copy(alpha = 0.28f),
                    lineColor.copy(alpha = 0.02f)
                )
            )
        )

        // Draw glowing stroke
        drawPath(
            path = path,
            color = lineColor,
            style = Stroke(
                width = 2.dp.toPx(),
                cap = StrokeCap.Round,
                join = StrokeJoin.Round
            )
        )
    }
}
