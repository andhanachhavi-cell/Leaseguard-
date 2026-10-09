package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.CloudUpload
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.LeaseItem
import com.example.ui.LeaseGuardViewModel
import com.example.ui.components.FlaggedBadge
import com.example.ui.components.GlassCard
import com.example.ui.components.GoldButton
import com.example.ui.components.MatchScoreBadge
import com.example.ui.components.StatusSeverityPill
import com.example.ui.theme.GoldHover
import com.example.ui.theme.GoldPrimary
import com.example.ui.theme.NeonEmerald
import com.example.ui.theme.RubyRed
import com.example.ui.theme.SlateCardBorder
import com.example.ui.theme.SlateCardBorderSubtle
import com.example.ui.theme.SlateCardSurface
import com.example.ui.theme.SlateSurfaceDark
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextNavyLight
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LeaseVaultScreen(
    viewModel: LeaseGuardViewModel,
    modifier: Modifier = Modifier
) {
    val leases by viewModel.allLeases.collectAsState()
    val portfolio by viewModel.portfolioState.collectAsState()
    val isAiParsing by viewModel.isAiParsing.collectAsState()
    val stageIndex by viewModel.parsingStageIndex.collectAsState()
    val stageMessage by viewModel.parsingStageMessage.collectAsState()
    val lastResult by viewModel.lastExtractedResult.collectAsState()

    var searchQuery by remember { mutableStateOf("") }
    var selectedLeaseForDetail by remember { mutableStateOf<LeaseItem?>(null) }
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    val displayedLeases = leases.filter {
        searchQuery.isBlank() ||
                it.tenantName.contains(searchQuery, ignoreCase = true) ||
                it.propertyAddress.contains(searchQuery, ignoreCase = true) ||
                it.cpiIndex.contains(searchQuery, ignoreCase = true)
    }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Header
        item {
            Spacer(modifier = Modifier.height(4.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "AI EXTRACTION PIPELINE",
                    color = GoldHover,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 0.08.sp
                )
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(10.dp))
                        .background(NeonEmerald.copy(alpha = 0.12f))
                        .border(1.dp, NeonEmerald.copy(alpha = 0.25f), RoundedCornerShape(10.dp))
                        .padding(horizontal = 10.dp, vertical = 4.dp)
                ) {
                    Text(
                        text = "● ${portfolio.parsedDocCount} Parsed Docs",
                        color = NeonEmerald,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = "Lease Vault (AI Parser)",
                color = TextPrimary,
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                letterSpacing = (-0.02).sp
            )
            Text(
                text = "Multi-modal OCR & clause parser powered by Gemini 3.5 Flash neural models.",
                color = TextSecondary,
                fontSize = 13.sp
            )
        }

        // Interactive Ingestion Drop Zone
        item {
            GlassCard(
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("file_drop_zone_card"),
                borderStroke = BorderStroke(1.5.dp, if (isAiParsing) GoldHover else SlateCardBorder)
            ) {
                Column(
                    modifier = Modifier.padding(18.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    if (isAiParsing) {
                        // 3-Second Neural Timeline Animation
                        AiParsingTimelineView(stageIndex = stageIndex, stageMessage = stageMessage)
                    } else {
                        // Normal Ingestion Drop Zone
                        Box(
                            modifier = Modifier
                                .size(48.dp)
                                .clip(CircleShape)
                                .background(SlateCardSurface)
                                .border(1.dp, SlateCardBorderSubtle, CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.CloudUpload,
                                contentDescription = null,
                                tint = GoldHover,
                                modifier = Modifier.size(24.dp)
                            )
                        }

                        Spacer(modifier = Modifier.height(10.dp))
                        Text(
                            text = "Drop lease documents here (PDF / TXT)",
                            color = TextPrimary,
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "Or tap any sample enterprise agreement below to run live AI OCR",
                            color = TextSecondary,
                            fontSize = 11.sp
                        )

                        Spacer(modifier = Modifier.height(14.dp))
                        // Sample Enterprise Lease Quick Actions
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            SampleDocChip(
                                label = "Tower_One_Master.pdf",
                                modifier = Modifier.weight(1f),
                                onClick = {
                                    viewModel.triggerAiDocumentIngestion(
                                        "Tower_One_Master.pdf",
                                        "Tenant: Sovereign Tower LLC. Premise: 55,000 RSF. Base rent: \$319k/yr. CPI Escalation cap 4.5%."
                                    )
                                }
                            )

                            SampleDocChip(
                                label = "Vertex_Floor14.pdf",
                                modifier = Modifier.weight(1f),
                                onClick = {
                                    viewModel.triggerAiDocumentIngestion(
                                        "Vertex_Floor14.pdf",
                                        "Tenant: Vertex Capital Partners. Term expires in 42 days. CPI uncapped escalation adjustment."
                                    )
                                }
                            )
                        }

                        Spacer(modifier = Modifier.height(8.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            SampleDocChip(
                                label = "Horizon_Biotech.pdf",
                                modifier = Modifier.weight(1f),
                                onClick = {
                                    viewModel.triggerAiDocumentIngestion(
                                        "Horizon_Biotech.pdf",
                                        "Tenant: Horizon Biotech Labs. 42,500 RSF. Full GL compliance \$10M umbrella. CPI-W +1.8%."
                                    )
                                }
                            )

                            SampleDocChip(
                                label = "Apex_TowerA_Lease.pdf",
                                modifier = Modifier.weight(1f),
                                onClick = {
                                    viewModel.triggerAiDocumentIngestion(
                                        "Apex_TowerA_Lease.pdf",
                                        "Tenant: Apex Global Logistics. Fixed 3.2% escalation. 24,100 RSF."
                                    )
                                }
                            )
                        }
                    }
                }
            }
        }

        // Search Bar
        item {
            OutlinedTextField(
                value = searchQuery,
                onValueChange = { searchQuery = it },
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Default.Search,
                        contentDescription = "Search",
                        tint = TextSecondary,
                        modifier = Modifier.size(18.dp)
                    )
                },
                placeholder = { Text("Search parsed leases, tenants, or CPI index...", color = TextMuted, fontSize = 13.sp) },
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = GoldHover,
                    unfocusedBorderColor = SlateCardBorder,
                    focusedTextColor = TextPrimary,
                    unfocusedTextColor = TextPrimary
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("lease_vault_search_input"),
                singleLine = true
            )
        }

        // Extracted Lease Data Table Section
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Extracted Lease Matrix (${displayedLeases.size})",
                    color = TextPrimary,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "Live Neural Ingestion",
                    color = NeonEmerald,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Medium
                )
            }
        }

        // Items
        items(displayedLeases, key = { it.id }) { lease ->
            GlassCard(
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("parsed_lease_item_${lease.id}")
                    .clickable { selectedLeaseForDetail = lease }
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.Top
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = Icons.Default.Description,
                                    contentDescription = null,
                                    tint = GoldHover,
                                    modifier = Modifier.size(14.dp)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = lease.tenantName,
                                    color = TextPrimary,
                                    fontSize = 15.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                            Text(
                                text = lease.propertyAddress,
                                color = TextSecondary,
                                fontSize = 11.sp,
                                maxLines = 1
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

                    Spacer(modifier = Modifier.height(10.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(text = "TERM EXPIRATION", color = TextMuted, fontSize = 9.sp)
                            Text(text = lease.expirationDate, color = TextPrimary, fontSize = 11.sp, fontWeight = FontWeight.SemiBold)
                        }
                        Column {
                            Text(text = "BASE RENT", color = TextMuted, fontSize = 9.sp)
                            Text(text = "$${"%,.2f".format(lease.monthlyRent)}/mo", color = TextPrimary, fontSize = 11.sp, fontWeight = FontWeight.SemiBold)
                        }
                        Column {
                            Text(text = "CPI INDEX", color = TextMuted, fontSize = 9.sp)
                            Text(text = lease.cpiIndex, color = GoldHover, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                        }
                        StatusSeverityPill(status = lease.coiStatus)
                    }
                }
            }
        }

        item {
            Spacer(modifier = Modifier.height(24.dp))
        }
    }

    // Modal Bottom Sheet for Detailed Lease Inspector
    if (selectedLeaseForDetail != null) {
        val lease = selectedLeaseForDetail!!
        ModalBottomSheet(
            onDismissRequest = { selectedLeaseForDetail = null },
            sheetState = sheetState,
            containerColor = SlateSurfaceDark
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = "LEASE CONTRACT DOSSIER",
                            color = GoldHover,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 0.08.sp
                        )
                        Text(
                            text = lease.tenantName,
                            color = TextPrimary,
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                    MatchScoreBadge(score = lease.matchScore)
                }

                Spacer(modifier = Modifier.height(14.dp))
                GlassCard(modifier = Modifier.fillMaxWidth()) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Text(text = "Address: ${lease.propertyAddress}", color = TextNavyLight, fontSize = 12.sp)
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(text = "Square Footage: ${"%,d".format(lease.squareFootage)} RSF", color = TextNavyLight, fontSize = 12.sp)
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(text = "Commencement: ${lease.commencementDate} ➔ Expiration: ${lease.expirationDate}", color = TextNavyLight, fontSize = 12.sp)
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(text = "Escalation Index: ${lease.cpiIndex} (Cap: ${lease.escalationCapPercent}%)", color = GoldHover, fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(text = "COI Policy Level: \$${"%,.0f".format(lease.glCoverage)} General Liability", color = NeonEmerald, fontSize = 12.sp)
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(text = "Cryptographic Audit Hash: ${lease.auditHash}", color = TextMuted, fontSize = 10.sp)
                    }
                }

                if (lease.flagReason != null) {
                    Spacer(modifier = Modifier.height(12.dp))
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(8.dp))
                            .background(RubyRed.copy(alpha = 0.12f))
                            .border(1.dp, RubyRed.copy(alpha = 0.3f), RoundedCornerShape(8.dp))
                            .padding(12.dp)
                    ) {
                        Text(
                            text = "⚠ Identified Legal Risk: ${lease.flagReason}",
                            color = RubyRed,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Medium
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))
                GoldButton(
                    text = "Close Dossier",
                    onClick = { selectedLeaseForDetail = null },
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(16.dp))
            }
        }
    }
}

@Composable
fun AiParsingTimelineView(
    stageIndex: Int,
    stageMessage: String
) {
    val infiniteTransition = rememberInfiniteTransition(label = "pulse_scanner")
    val sweep by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 1000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "sweep"
    )

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 10.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(
                imageVector = Icons.Default.AutoAwesome,
                contentDescription = null,
                tint = GoldHover,
                modifier = Modifier.size(18.dp)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = "NEURAL OCR & CLAUSE PARSER ACTIVE",
                color = GoldHover,
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                letterSpacing = 0.05.sp
            )
        }

        Spacer(modifier = Modifier.height(12.dp))
        // Progress steps indicator (5 stages)
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Center
        ) {
            for (i in 0..4) {
                Box(
                    modifier = Modifier
                        .padding(horizontal = 4.dp)
                        .size(if (i == stageIndex) 10.dp else 7.dp)
                        .clip(CircleShape)
                        .background(
                            when {
                                i < stageIndex -> NeonEmerald
                                i == stageIndex -> GoldHover
                                else -> SlateCardBorder
                            }
                        )
                )
            }
        }

        Spacer(modifier = Modifier.height(12.dp))
        CircularProgressIndicator(
            color = GoldHover,
            strokeWidth = 3.dp,
            modifier = Modifier.size(32.dp)
        )

        Spacer(modifier = Modifier.height(12.dp))
        Text(
            text = stageMessage,
            color = TextPrimary,
            fontSize = 13.sp,
            fontWeight = FontWeight.SemiBold
        )
        Text(
            text = "Gemini 3.5 Flash • Vector Embedding Verification",
            color = TextMuted,
            fontSize = 11.sp
        )
    }
}

@Composable
fun SampleDocChip(
    label: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(8.dp))
            .background(SlateCardSurface)
            .border(1.dp, SlateCardBorderSubtle, RoundedCornerShape(8.dp))
            .clickable { onClick() }
            .padding(horizontal = 10.dp, vertical = 8.dp),
        contentAlignment = Alignment.Center
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(
                imageVector = Icons.Default.Description,
                contentDescription = null,
                tint = GoldHover,
                modifier = Modifier.size(13.dp)
            )
            Spacer(modifier = Modifier.width(6.dp))
            Text(
                text = label,
                color = TextPrimary,
                fontSize = 11.sp,
                fontWeight = FontWeight.Medium,
                maxLines = 1
            )
        }
    }
}
