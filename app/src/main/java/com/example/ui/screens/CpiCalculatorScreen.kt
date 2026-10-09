package com.example.ui.screens

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.filled.TrendingUp
import androidx.compose.material3.Icon
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.LeaseGuardViewModel
import com.example.ui.components.GlassCard
import com.example.ui.theme.AmberWarning
import com.example.ui.theme.GoldHover
import com.example.ui.theme.GoldPrimary
import com.example.ui.theme.NeonEmerald
import com.example.ui.theme.RubyRed
import com.example.ui.theme.SlateCardBorderSubtle
import com.example.ui.theme.SlateCardSurface
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextNavyLight
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import java.text.DecimalFormat

@Composable
fun CpiCalculatorScreen(
    viewModel: LeaseGuardViewModel,
    modifier: Modifier = Modifier
) {
    val portfolio by viewModel.portfolioState.collectAsState()
    val cpiRate by viewModel.annualCpiRate.collectAsState()
    val escalationCap by viewModel.escalationCap.collectAsState()
    val projections by viewModel.cpiProjections.collectAsState()

    val effectiveRate = minOf(cpiRate, escalationCap)
    val isCapExceeded = cpiRate > escalationCap
    val annualInflationDelta = (portfolio.portfolioSizeSf * portfolio.rentalWeightPerSf) * (effectiveRate / 100.0)

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Header
        item {
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = "COMMERCIAL INFLATION HEDGING",
                color = GoldHover,
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                letterSpacing = 0.08.sp
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = "CPI Calculator Workspace",
                color = TextPrimary,
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                letterSpacing = (-0.02).sp
            )
            Text(
                text = "Dynamic 12-month variable cost-of-living projection linked to portfolio run rate.",
                color = TextSecondary,
                fontSize = 13.sp
            )
        }

        // Summary Metric Banner ($ Delta + Effective Rate)
        item {
            GlassCard(
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("cpi_summary_banner")
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(
                                text = "PROJECTED INFLATION DELTA",
                                color = TextSecondary,
                                fontSize = 10.sp,
                                fontWeight = FontWeight.SemiBold,
                                letterSpacing = 0.05.sp
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = "+$${"%,.2f".format(annualInflationDelta)}",
                                color = if (isCapExceeded) AmberWarning else NeonEmerald,
                                fontSize = 24.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }

                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(12.dp))
                                .background(SlateCardSurface)
                                .border(1.dp, SlateCardBorderSubtle, RoundedCornerShape(12.dp))
                                .padding(horizontal = 12.dp, vertical = 6.dp)
                        ) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Text(
                                    text = "EFFECTIVE CAP",
                                    color = TextMuted,
                                    fontSize = 9.sp,
                                    fontWeight = FontWeight.Bold
                                )
                                Text(
                                    text = "${"%.1f".format(effectiveRate)}%",
                                    color = GoldHover,
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    }

                    if (isCapExceeded) {
                        Spacer(modifier = Modifier.height(12.dp))
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(8.dp))
                                .background(AmberWarning.copy(alpha = 0.12f))
                                .border(1.dp, AmberWarning.copy(alpha = 0.3f), RoundedCornerShape(8.dp))
                                .padding(10.dp)
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = Icons.Default.Shield,
                                    contentDescription = null,
                                    tint = AmberWarning,
                                    modifier = Modifier.size(16.dp)
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = "Cap Protection Active: CPI (${"%.1f".format(cpiRate)}%) exceeds ${"%.1f".format(escalationCap)}% lease ceiling. Tenant saved \$${"%,.2f".format(((portfolio.portfolioSizeSf * portfolio.rentalWeightPerSf) * ((cpiRate - escalationCap) / 100.0)))}/yr.",
                                    color = AmberWarning,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Medium
                                )
                            }
                        }
                    }
                }
            }
        }

        // Dual Interactive Sliders Control Card
        item {
            GlassCard(
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("cpi_sliders_card")
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    // Slider 1: Annual CPI Rate
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Annual CPI Rate (Inflation)",
                            color = TextPrimary,
                            fontSize = 13.sp,
                            fontWeight = FontWeight.SemiBold
                        )
                        Text(
                            text = "${"%.1f".format(cpiRate)}%",
                            color = GoldHover,
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    Slider(
                        value = cpiRate,
                        onValueChange = { viewModel.setAnnualCpiRate(it) },
                        valueRange = 0.0f..12.0f,
                        steps = 119, // 0.1 increments
                        colors = SliderDefaults.colors(
                            thumbColor = GoldHover,
                            activeTrackColor = GoldPrimary,
                            inactiveTrackColor = SlateCardBorderSubtle
                        ),
                        modifier = Modifier.testTag("slider_annual_cpi")
                    )

                    Spacer(modifier = Modifier.height(14.dp))

                    // Slider 2: Lease Escalation Cap
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Lease Escalation Cap (Contract Ceiling)",
                            color = TextPrimary,
                            fontSize = 13.sp,
                            fontWeight = FontWeight.SemiBold
                        )
                        Text(
                            text = "${"%.1f".format(escalationCap)}%",
                            color = NeonEmerald,
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    Slider(
                        value = escalationCap,
                        onValueChange = { viewModel.setEscalationCap(it) },
                        valueRange = 0.0f..10.0f,
                        steps = 99,
                        colors = SliderDefaults.colors(
                            thumbColor = NeonEmerald,
                            activeTrackColor = NeonEmerald,
                            inactiveTrackColor = SlateCardBorderSubtle
                        ),
                        modifier = Modifier.testTag("slider_escalation_cap")
                    )
                }
            }
        }

        // 12-Month Projection Bar Chart
        item {
            GlassCard(
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("projection_chart_card")
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "12-Month Escalation Trendline",
                            color = TextPrimary,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "Monthly Increments",
                            color = TextSecondary,
                            fontSize = 11.sp
                        )
                    }

                    Spacer(modifier = Modifier.height(16.dp))
                    MonthlyBarChart(projections = projections)
                }
            }
        }

        // Tabular 12-Month Matrix
        item {
            Text(
                text = "12-Month Projection Matrix",
                color = TextPrimary,
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold
            )
        }

        items(projections, key = { it.monthNumber }) { month ->
            GlassCard(modifier = Modifier.fillMaxWidth()) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 14.dp, vertical = 10.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = "Month ${month.monthNumber} • ${month.monthName}",
                            color = TextPrimary,
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "Applied Rate: ${"%.1f".format(month.escalationPercent)}%",
                            color = TextSecondary,
                            fontSize = 10.sp
                        )
                    }

                    Column(horizontalAlignment = Alignment.End) {
                        Text(
                            text = "$${"%,.2f".format(month.projectedRent)}/mo",
                            color = TextPrimary,
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "+$${"%,.2f".format(month.inflationDelta)} delta",
                            color = GoldHover,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                }
            }
        }

        item {
            Spacer(modifier = Modifier.height(28.dp))
        }
    }
}

@Composable
fun MonthlyBarChart(
    projections: List<com.example.data.model.MonthlyImpact>,
    modifier: Modifier = Modifier
        .fillMaxWidth()
        .height(120.dp)
) {
    Canvas(modifier = modifier) {
        if (projections.isEmpty()) return@Canvas

        val maxDelta = projections.maxOfOrNull { it.inflationDelta }?.toFloat()?.coerceAtLeast(100f) ?: 100f
        val barWidth = (size.width / (projections.size * 1.5f)).coerceAtMost(22.dp.toPx())
        val spacing = (size.width - (barWidth * projections.size)) / (projections.size + 1)

        projections.forEachIndexed { index, item ->
            val barHeight = ((item.inflationDelta.toFloat() / maxDelta) * (size.height - 20f)).coerceAtLeast(8f)
            val left = spacing + index * (barWidth + spacing)
            val top = size.height - barHeight - 4f

            drawRoundRect(
                brush = Brush.verticalGradient(
                    colors = listOf(GoldHover, GoldPrimary)
                ),
                topLeft = Offset(left, top),
                size = Size(barWidth, barHeight),
                cornerRadius = CornerRadius(4.dp.toPx(), 4.dp.toPx())
            )
        }
    }
}
