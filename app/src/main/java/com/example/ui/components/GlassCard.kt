package com.example.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.example.ui.theme.SlateCardBorderSubtle
import com.example.ui.theme.SlateCardSurface
import com.example.ui.theme.SlateSurfaceDark

@Composable
fun GlassCard(
    modifier: Modifier = Modifier,
    shape: Shape = RoundedCornerShape(14.dp),
    borderStroke: BorderStroke? = BorderStroke(1.dp, SlateCardBorderSubtle),
    elevation: Dp = 0.dp,
    onClick: (() -> Unit)? = null,
    content: @Composable () -> Unit
) {
    val gradientBrush = Brush.verticalGradient(
        colors = listOf(
            SlateCardSurface.copy(alpha = 0.85f),
            SlateSurfaceDark.copy(alpha = 0.95f)
        )
    )

    Surface(
        modifier = modifier
            .clip(shape)
            .then(if (onClick != null) Modifier.clickable { onClick() } else Modifier),
        shape = shape,
        color = Color.Transparent,
        border = borderStroke,
        tonalElevation = elevation
    ) {
        Box(
            modifier = Modifier
                .background(gradientBrush)
        ) {
            content()
        }
    }
}
