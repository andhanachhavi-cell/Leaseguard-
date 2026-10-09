package com.example.ui.components

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import com.example.ui.theme.TextPrimary
import java.text.DecimalFormat

@Composable
fun AnimatedCountUpText(
    targetValue: Double,
    prefix: String = "",
    suffix: String = "",
    isCurrency: Boolean = false,
    isInteger: Boolean = false,
    textStyle: TextStyle = TextStyle(
        fontSize = 22.sp,
        fontWeight = FontWeight.Bold,
        color = TextPrimary
    ),
    modifier: Modifier = Modifier
) {
    val animatable = remember { Animatable(targetValue.toFloat()) }

    LaunchedEffect(targetValue) {
        animatable.animateTo(
            targetValue = targetValue.toFloat(),
            animationSpec = tween(
                durationMillis = 800,
                easing = FastOutSlowInEasing
            )
        )
    }

    val currentVal = animatable.value.toDouble()
    val formattedString = when {
        isCurrency -> {
            val formatter = DecimalFormat("#,##0.00")
            "$prefix${formatter.format(currentVal)}$suffix"
        }
        isInteger -> {
            val formatter = DecimalFormat("#,##0")
            "$prefix${formatter.format(currentVal.toLong())}$suffix"
        }
        else -> {
            val formatter = DecimalFormat("#,##0.0")
            "$prefix${formatter.format(currentVal)}$suffix"
        }
    }

    Text(
        text = formattedString,
        style = textStyle,
        modifier = modifier
    )
}
