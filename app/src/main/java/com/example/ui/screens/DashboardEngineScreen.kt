package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDownward
import androidx.compose.material.icons.filled.ArrowUpward
import androidx.compose.material.icons.filled.Business
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.LeaseItem
import com.example.ui.LeaseGuardViewModel
import com.example.ui.components.ActiveEnterpriseNodePill
import com.example.ui.components.AnimatedCountUpText
import com.example.ui.components.FlaggedBadge
import com.example.ui.components.GlassCard
import com.example.ui.components.GoldButton
import com.example.ui.components.MatchScoreBadge
import com.example.ui.components.SparklineChart
import com.example.ui.components.StatusSeverityPill
import com.example.ui.theme.AmberWarning
import com.example.ui.theme.GoldHover
import com.example.ui.theme.NeonEmerald
import com.example.ui.theme.RubyRed
import com.example.ui.theme.SlateCardBorder
import com.example.ui.theme.SlateCardBorderSubtle
import com.example.ui.theme.SlateCardSurface
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextNavyLight
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary

@Composable
fun DashboardEngineScreen(
    viewModel: LeaseGuardViewModel,
    modifier: Modifier = Modifier,
    onNavigateToLeaseVault: () -> Unit = {}
) {
    val portfolio by viewModel.portfolioState.collectAsState()
    val filteredLeases by viewModel.filteredLeases.collectAsState()
    val filterFlaggedOnly by viewModel.filterFlaggedOnly.collectAsState()
    val auditLogs by viewModel.auditLogs.collectAsState()
    val userRole by viewModel.currentRole.collectAsState()

    var showQuickDataForm by remember { mutableStateOf(false) }
    var inputSfText by remember(portfolio.portfolioSizeSf) {
        mutableStateOf(portfolio.portfolioSizeSf.toString())
    }
    var inputRateText by remember(portfolio.rentalWeightPerSf) {
        mutableStateOf(portfolio.rentalWeightPerSf.toString())
    }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Status Row & Subtitle
        item {
            Spacer(modifier = Modifier.height(4.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                ActiveEnterpriseNodePill(modifier = Modifier.testTag("node_status_pill"))
                
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(10.dp))
                        .background(SlateCardSurface)
                        .border(1.dp, SlateCardBorderSubtle, RoundedCornerShape(10.dp))
                        .padding(horizontal = 8.dp, vertical = 4.dp)
                ) {
                    Text(
                        text = userRole.badge,
                        color = GoldHover,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 0.05.sp
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))
            Text(
                text = "COMMERCIAL REAL ESTATE INTELLIGENCE",
                color = GoldHover,
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                letterSpacing = 0.08.sp
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = "Dashboard Engine",
                color = TextPrimary,
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                letterSpacing = (-0.02).sp
            )
            Text(
                text = "Autonomous lease ingestion, CPI delta projections & zero-trust compliance telemetry.",
                color = TextSecondary,
                fontSize = 13.sp
            )
        }

        // Kinetic Live Metric Cards (Grid of 4)
        item {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    // Metric 1: Portfolio Size (SF)
                    GlassCard(
                        modifier = Modifier
                            .weight(1f)
                            .testTag("metric_portfolio_sf")
                    ) {
                        Column(modifier = Modifier.padding(14.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = "PORTFOLIO SIZE",
                                    color = TextSecondary,
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    letterSpacing = 0.05.sp
                                )
                                SparklineChart(
                                    dataPoints = listOf(22f, 24f, 23f, 26f, 28f, 31f, 34f),
                                    lineColor = NeonEmerald
                                )
                            }
                            Spacer(modifier = Modifier.height(8.dp))
                            AnimatedCountUpText(
                                targetValue = portfolio.portfolioSizeSf.toDouble(),
                                suffix = " SF",
                                isInteger = true,
                                textStyle = androidx.compose.ui.text.TextStyle(
                                    fontSize = 20.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = TextPrimary
                                )
                            )
                            Spacer(modifier = Modifier.height(6.dp))
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = Icons.Default.ArrowUpward,
                                    contentDescription = null,
                                    tint = NeonEmerald,
                                    modifier = Modifier.size(12.dp)
                                )
                                Spacer(modifier = Modifier.width(3.dp))
                                Text(
                                    text = "+2.4% vs Q3 baseline",
                                    color = NeonEmerald,
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Medium
                                )
                            }
                        }
                    }

                    // Metric 2: Monthly Run Rate ($)
                    GlassCard(
                        modifier = Modifier
                            .weight(1f)
                            .testTag("metric_monthly_run_rate")
                    ) {
                        Column(modifier = Modifier.padding(14.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = "MONTHLY RUN RATE",
                                    color = TextSecondary,
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    letterSpacing = 0.05.sp
                                )
                                SparklineChart(
                                    dataPoints = listOf(15f, 17f, 16f, 20f, 23f, 22f, 28f),
                                    lineColor = GoldHover
                                )
                            }
                            Spacer(modifier = Modifier.height(8.dp))
                            AnimatedCountUpText(
                                targetValue = portfolio.monthlyRunRate,
                                prefix = "$",
                                isCurrency = true,
                                textStyle = androidx.compose.ui.text.TextStyle(
                                    fontSize = 20.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = TextPrimary
                                )
                            )
                            Spacer(modifier = Modifier.height(6.dp))
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = Icons.Default.ArrowUpward,
                                    contentDescription = null,
                                    tint = GoldHover,
                                    modifier = Modifier.size(12.dp)
                                )
                                Spacer(modifier = Modifier.width(3.dp))
                                Text(
                                    text = "+5.1% annualized",
                                    color = GoldHover,
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Medium
                                )
                            }
                        }
                    }
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    // Metric 3: Active Flags Count (Clickable: Filters Leases!)
                    GlassCard(
                        modifier = Modifier
                            .weight(1f)
                            .testTag("metric_active_flags")
                            .clickable { viewModel.toggleFlagFilter() },
                        borderStroke = if (filterFlaggedOnly) BorderStroke(1.5.dp, RubyRed) else BorderStroke(1.dp, SlateCardBorderSubtle)
                    ) {
                        Column(modifier = Modifier.padding(14.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = "ACTIVE FLAGS",
                                    color = if (filterFlaggedOnly) RubyRed else TextSecondary,
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    letterSpacing = 0.05.sp
                                )
                                Box(
                                    modifier = Modifier
                                        .size(18.dp)
                                        .clip(CircleShape)
                                        .background(if (filterFlaggedOnly) RubyRed else RubyRed.copy(alpha = 0.15f)),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.FilterList,
                                        contentDescription = "Filter flagged",
                                        tint = if (filterFlaggedOnly) Color.White else RubyRed,
                                        modifier = Modifier.size(11.dp)
                                    )
                                }
                            }
                            Spacer(modifier = Modifier.height(8.dp))
                            AnimatedCountUpText(
                                targetValue = portfolio.activeFlagsCount.toDouble(),
                                isInteger = true,
                                textStyle = androidx.compose.ui.text.TextStyle(
                                    fontSize = 20.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = RubyRed
                                )
                            )
                            Spacer(modifier = Modifier.height(6.dp))
                            Text(
                                text = if (filterFlaggedOnly) "Filtering active (Tap to clear)" else "Tap to filter flagged leases",
                                color = if (filterFlaggedOnly) RubyRed else TextMuted,
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Medium
                            )
                        }
                    }

                    // Metric 4: Compliance Rate
                    GlassCard(
                        modifier = Modifier
                            .weight(1f)
                            .testTag("metric_compliance_rate")
                    ) {
                        Column(modifier = Modifier.padding(14.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = "COI COMPLIANCE",
                                    color = TextSecondary,
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    letterSpacing = 0.05.sp
                                )
                                Icon(
                                    imageVector = Icons.Default.CheckCircle,
                                    contentDescription = null,
                                    tint = NeonEmerald,
                                    modifier = Modifier.size(16.dp)
                                )
                            }
                            Spacer(modifier = Modifier.height(8.dp))
                            AnimatedCountUpText(
                                targetValue = portfolio.complianceRate,
                                suffix = "%",
                                textStyle = androidx.compose.ui.text.TextStyle(
                                    fontSize = 20.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = NeonEmerald
                                )
                            )
                            Spacer(modifier = Modifier.height(6.dp))
                            Text(
                                text = "Verified Sovereign Tier",
                                color = TextMuted,
                                fontSize = 10.sp
                            )
                        }
                    }
                }
            }
        }

        // Quick Data Manager Control Box
        item {
            GlassCard(
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("quick_data_manager_card")
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(30.dp)
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(SlateCardSurface)
                                    .border(1.dp, SlateCardBorderSubtle, RoundedCornerShape(8.dp)),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Edit,
                                    contentDescription = null,
                                    tint = GoldHover,
                                    modifier = Modifier.size(16.dp)
                                )
                            }
                            Spacer(modifier = Modifier.width(10.dp))
                            Column {
                                Text(
                                    text = "Quick Data Manager",
                                    color = TextPrimary,
                                    fontSize = 15.sp,
                                    fontWeight = FontWeight.Bold
                                )
                                Text(
                                    text = "Real-time portfolio calculations & rental weights",
                                    color = TextSecondary,
                                    fontSize = 11.sp
                                )
                            }
                        }

                        IconButton(
                            onClick = { showQuickDataForm = !showQuickDataForm },
                            modifier = Modifier.size(32.dp)
                        ) {
                            Icon(
                                imageVector = if (showQuickDataForm) Icons.Default.Close else Icons.Default.Edit,
                                contentDescription = "Toggle manager",
                                tint = GoldHover,
                                modifier = Modifier.size(18.dp)
                            )
                        }
                    }

                    AnimatedVisibility(
                        visible = showQuickDataForm,
                        enter = fadeIn(),
                        exit = fadeOut()
                    ) {
                        Column(modifier = Modifier.padding(top = 14.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(10.dp)
                            ) {
                                OutlinedTextField(
                                    value = inputSfText,
                                    onValueChange = { inputSfText = it },
                                    label = { Text("Portfolio Size (SF)", fontSize = 11.sp) },
                                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                    colors = OutlinedTextFieldDefaults.colors(
                                        focusedBorderColor = GoldHover,
                                        unfocusedBorderColor = SlateCardBorder,
                                        focusedTextColor = TextPrimary,
                                        unfocusedTextColor = TextPrimary
                                    ),
                                    modifier = Modifier
                                        .weight(1.3f)
                                        .testTag("input_portfolio_sf"),
                                    singleLine = true
                                )

                                OutlinedTextField(
                                    value = inputRateText,
                                    onValueChange = { inputRateText = it },
                                    label = { Text("Rent/SF ($)", fontSize = 11.sp) },
                                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                                    colors = OutlinedTextFieldDefaults.colors(
                                        focusedBorderColor = GoldHover,
                                        unfocusedBorderColor = SlateCardBorder,
                                        focusedTextColor = TextPrimary,
                                        unfocusedTextColor = TextPrimary
                                    ),
                                    modifier = Modifier
                                        .weight(0.9f)
                                        .testTag("input_rental_weight"),
                                    singleLine = true
                                )
                            }

                            Spacer(modifier = Modifier.height(12.dp))
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = "Formula: (SF × $5.80) ÷ 12",
                                    color = TextMuted,
                                    fontSize = 11.sp
                                )

                                GoldButton(
                                    text = "Update Stats",
                                    onClick = {
                                        val sf = inputSfText.replace(",", "").toLongOrNull() ?: portfolio.portfolioSizeSf
                                        val rate = inputRateText.toDoubleOrNull() ?: 5.80
                                        viewModel.updatePortfolioStats(sf, rate)
                                        showQuickDataForm = false
                                    },
                                    modifier = Modifier.testTag("update_stats_button")
                                )
                            }
                        }
                    }
                }
            }
        }

        // Leases Header with Filter State
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = "Portfolio Leases",
                        color = TextPrimary,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(8.dp))
                            .background(SlateCardSurface)
                            .padding(horizontal = 6.dp, vertical = 2.dp)
                    ) {
                        Text(
                            text = "${filteredLeases.size} Nodes",
                            color = TextSecondary,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Medium
                        )
                    }
                }

                if (filterFlaggedOnly) {
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(8.dp))
                            .background(RubyRed.copy(alpha = 0.15f))
                            .border(1.dp, RubyRed.copy(alpha = 0.3f), RoundedCornerShape(8.dp))
                            .clickable { viewModel.setFlagFilter(false) }
                            .padding(horizontal = 8.dp, vertical = 4.dp)
                    ) {
                        Text(
                            text = "FLAGGED ONLY ✕",
                            color = RubyRed,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                } else {
                    Text(
                        text = "Drop Docs in Vault ➔",
                        color = GoldHover,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.SemiBold,
                        modifier = Modifier.clickable { onNavigateToLeaseVault() }
                    )
                }
            }
        }

        // Leases List
        items(filteredLeases, key = { it.id }) { lease ->
            LeaseSummaryCard(lease = lease)
        }

        // Live Audit Activity Stream
        item {
            Spacer(modifier = Modifier.height(4.dp))
            GlassCard(
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("audit_activity_card")
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.Security,
                                contentDescription = null,
                                tint = NeonEmerald,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "Zero-Trust Cryptographic Audit Trail",
                                color = TextPrimary,
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                        Text(
                            text = "SHA-256 Verified",
                            color = NeonEmerald,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Medium
                        )
                    }

                    Spacer(modifier = Modifier.height(10.dp))
                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        auditLogs.take(3).forEach { log ->
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(
                                    modifier = Modifier.weight(1f),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        text = log.timestamp,
                                        color = TextMuted,
                                        fontSize = 10.sp,
                                        fontWeight = FontWeight.Medium
                                    )
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text(
                                        text = log.action,
                                        color = TextNavyLight,
                                        fontSize = 11.sp,
                                        maxLines = 1
                                    )
                                }
                                Text(
                                    text = log.hash,
                                    color = GoldHover,
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    }
                }
            }
            Spacer(modifier = Modifier.height(20.dp))
        }
    }
}

@Composable
fun LeaseSummaryCard(lease: LeaseItem) {
    GlassCard(
        modifier = Modifier
            .fillMaxWidth()
            .testTag("lease_card_${lease.id}"),
        borderStroke = if (lease.isFlagged) BorderStroke(1.dp, RubyRed.copy(alpha = 0.35f)) else BorderStroke(1.dp, SlateCardBorderSubtle)
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = lease.tenantName,
                        color = TextPrimary,
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = lease.propertyAddress,
                        color = TextSecondary,
                        fontSize = 11.sp
                    )
                }

                Row(
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    if (lease.isFlagged) {
                        FlaggedBadge()
                    }
                    MatchScoreBadge(score = lease.matchScore)
                }
            }

            Spacer(modifier = Modifier.height(12.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(text = "SPACE (SF)", color = TextMuted, fontSize = 9.sp, fontWeight = FontWeight.SemiBold)
                    Text(text = "${"%,d".format(lease.squareFootage)} SF", color = TextPrimary, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                }
                Column {
                    Text(text = "MONTHLY RENT", color = TextMuted, fontSize = 9.sp, fontWeight = FontWeight.SemiBold)
                    Text(text = "$${"%,.2f".format(lease.monthlyRent)}", color = TextPrimary, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                }
                Column {
                    Text(text = "INDEX / CAP", color = TextMuted, fontSize = 9.sp, fontWeight = FontWeight.SemiBold)
                    Text(text = "${lease.cpiIndex} (${lease.escalationCapPercent}%)", color = GoldHover, fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
                }
                Column {
                    Text(text = "COI STATUS", color = TextMuted, fontSize = 9.sp, fontWeight = FontWeight.SemiBold)
                    StatusSeverityPill(status = lease.coiStatus)
                }
            }

            if (lease.flagReason != null) {
                Spacer(modifier = Modifier.height(10.dp))
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(8.dp))
                        .background(RubyRed.copy(alpha = 0.08f))
                        .border(1.dp, RubyRed.copy(alpha = 0.2f), RoundedCornerShape(8.dp))
                        .padding(8.dp)
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.Warning,
                            contentDescription = null,
                            tint = RubyRed,
                            modifier = Modifier.size(13.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = lease.flagReason,
                            color = RubyRed,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Medium
                        )
                    }
                }
            }
        }
    }
}
