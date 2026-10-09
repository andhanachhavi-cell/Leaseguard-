package com.example.ui.components

import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.AmberWarning
import com.example.ui.theme.AmberWarningBg
import com.example.ui.theme.NeonEmerald
import com.example.ui.theme.NeonEmeraldBg
import com.example.ui.theme.NeonEmeraldPulse
import com.example.ui.theme.RubyRed
import com.example.ui.theme.RubyRedBg

@Composable
fun ActiveEnterpriseNodePill(
    modifier: Modifier = Modifier
) {
    val infiniteTransition = rememberInfiniteTransition(label = "pulse")
    val alphaAnim by infiniteTransition.animateFloat(
        initialValue = 0.35f,
        targetValue = 1.0f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 900),
            repeatMode = RepeatMode.Reverse
        ),
        label = "alpha"
    )

    Box(
        modifier = modifier
            .clip(RoundedCornerShape(20.dp))
            .background(NeonEmeraldBg)
            .border(1.dp, NeonEmerald.copy(alpha = 0.25f), RoundedCornerShape(20.dp))
            .padding(horizontal = 10.dp, vertical = 4.dp),
        contentAlignment = Alignment.Center
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Box(
                modifier = Modifier
                    .size(7.dp)
                    .alpha(alphaAnim)
                    .clip(CircleShape)
                    .background(NeonEmeraldPulse)
            )
            Spacer(modifier = Modifier.width(6.dp))
            Text(
                text = "Active Enterprise Node",
                color = NeonEmerald,
                fontSize = 11.sp,
                fontWeight = FontWeight.SemiBold,
                letterSpacing = 0.02.sp
            )
        }
    }
}

@Composable
fun MatchScoreBadge(
    score: Int,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(12.dp))
            .background(NeonEmeraldBg)
            .border(1.dp, NeonEmerald.copy(alpha = 0.2f), RoundedCornerShape(12.dp))
            .padding(horizontal = 8.dp, vertical = 3.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = "$score% Match",
            color = NeonEmerald,
            fontSize = 11.sp,
            fontWeight = FontWeight.Bold,
            letterSpacing = 0.02.sp
        )
    }
}

@Composable
fun FlaggedBadge(
    modifier: Modifier = Modifier,
    label: String = "FLAGGED"
) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(12.dp))
            .background(RubyRedBg)
            .border(1.dp, RubyRed.copy(alpha = 0.25f), RoundedCornerShape(12.dp))
            .padding(horizontal = 8.dp, vertical = 3.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = label,
            color = RubyRed,
            fontSize = 10.sp,
            fontWeight = FontWeight.Bold,
            letterSpacing = 0.05.sp
        )
    }
}

@Composable
fun StatusSeverityPill(
    status: String,
    modifier: Modifier = Modifier
) {
    val (bgColor, borderColor, textColor) = when (status.uppercase()) {
        "CLEAR", "COMPLIANT", "VERIFIED" -> Triple(NeonEmeraldBg, NeonEmerald.copy(alpha = 0.2f), NeonEmerald)
        "WARNING", "PENDING" -> Triple(AmberWarningBg, AmberWarning.copy(alpha = 0.25f), AmberWarning)
        else -> Triple(RubyRedBg, RubyRed.copy(alpha = 0.25f), RubyRed)
    }

    Box(
        modifier = modifier
            .clip(RoundedCornerShape(12.dp))
            .background(bgColor)
            .border(1.dp, borderColor, RoundedCornerShape(12.dp))
            .padding(horizontal = 8.dp, vertical = 3.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = status.uppercase(),
            color = textColor,
            fontSize = 10.sp,
            fontWeight = FontWeight.Bold,
            letterSpacing = 0.04.sp
        )
    }
}
