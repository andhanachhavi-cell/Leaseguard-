package com.example.ui.screens

import android.widget.Toast
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AssignmentTurnedIn
import androidx.compose.material.icons.filled.MailOutline
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.CoiAuditItem
import com.example.ui.LeaseGuardViewModel
import com.example.ui.components.GlassCard
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
fun CoiMatrixScreen(
    viewModel: LeaseGuardViewModel,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val coiItems by viewModel.filteredCoiItems.collectAsState()
    val activeSeverityFilter by viewModel.coiFilterSeverity.collectAsState()
    val searchQuery by viewModel.coiSearchQuery.collectAsState()

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
                    text = "LIABILITY & RISK MITIGATION",
                    color = GoldHover,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 0.08.sp
                )
                Text(
                    text = "● Sovereign Standards",
                    color = NeonEmerald,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold
                )
            }

            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = "COI Matrix (Compliance Center)",
                color = TextPrimary,
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                letterSpacing = (-0.02).sp
            )
            Text(
                text = "Autonomous verification of Certificates of Insurance against \$5M umbrella criteria.",
                color = TextSecondary,
                fontSize = 13.sp
            )
        }

        // Search Input
        item {
            OutlinedTextField(
                value = searchQuery,
                onValueChange = { viewModel.setCoiSearchQuery(it) },
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Default.Search,
                        contentDescription = "Search",
                        tint = TextSecondary,
                        modifier = Modifier.size(18.dp)
                    )
                },
                placeholder = { Text("Filter tenant name, policy #, or carrier...", color = TextMuted, fontSize = 13.sp) },
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = GoldHover,
                    unfocusedBorderColor = SlateCardBorder,
                    focusedTextColor = TextPrimary,
                    unfocusedTextColor = TextPrimary
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("coi_search_input"),
                singleLine = true
            )
        }

        // Severity Filter Chips Row
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                listOf("ALL", "CRITICAL", "WARNING", "CLEAR").forEach { severity ->
                    val isSelected = activeSeverityFilter == severity
                    val chipBg = if (isSelected) {
                        when (severity) {
                            "CRITICAL" -> RubyRed.copy(alpha = 0.25f)
                            "WARNING" -> AmberWarning.copy(alpha = 0.25f)
                            "CLEAR" -> NeonEmerald.copy(alpha = 0.25f)
                            else -> GoldHover.copy(alpha = 0.25f)
                        }
                    } else SlateCardSurface

                    val chipBorder = if (isSelected) {
                        when (severity) {
                            "CRITICAL" -> RubyRed
                            "WARNING" -> AmberWarning
                            "CLEAR" -> NeonEmerald
                            else -> GoldHover
                        }
                    } else SlateCardBorderSubtle

                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(10.dp))
                            .background(chipBg)
                            .border(1.dp, chipBorder, RoundedCornerShape(10.dp))
                            .clickable { viewModel.setCoiFilterSeverity(severity) }
                            .padding(vertical = 8.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = severity,
                            color = if (isSelected) TextPrimary else TextSecondary,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }

        // COI Records
        items(coiItems, key = { it.id }) { item ->
            CoiRecordCard(
                item = item,
                onIssueNotice = {
                    viewModel.issueDeficiencyNotice(item.id, item.tenantName)
                    Toast.makeText(context, "Notice of Deficiency issued to ${item.tenantName}", Toast.LENGTH_SHORT).show()
                },
                onRequestCoi = {
                    viewModel.requestUpdatedCoi(item.id, item.tenantName)
                    Toast.makeText(context, "Updated COI recorded as Verified for ${item.tenantName}", Toast.LENGTH_SHORT).show()
                }
            )
        }

        item {
            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}

@Composable
fun CoiRecordCard(
    item: CoiAuditItem,
    onIssueNotice: () -> Unit,
    onRequestCoi: () -> Unit
) {
    val borderColor = when (item.severity) {
        "CRITICAL" -> RubyRed.copy(alpha = 0.35f)
        "WARNING" -> AmberWarning.copy(alpha = 0.35f)
        else -> SlateCardBorderSubtle
    }

    GlassCard(
        modifier = Modifier
            .fillMaxWidth()
            .testTag("coi_record_${item.id}"),
        borderStroke = BorderStroke(1.dp, borderColor)
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = item.tenantName,
                        color = TextPrimary,
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = "${item.carrierName} • Policy #${item.policyNumber}",
                        color = TextSecondary,
                        fontSize = 11.sp
                    )
                }

                StatusSeverityPill(status = item.severity)
            }

            Spacer(modifier = Modifier.height(10.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(text = "GL COVERAGE", color = TextMuted, fontSize = 9.sp, fontWeight = FontWeight.SemiBold)
                    Text(text = item.glLimit, color = TextPrimary, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                }
                Column {
                    Text(text = "UMBRELLA", color = TextMuted, fontSize = 9.sp, fontWeight = FontWeight.SemiBold)
                    Text(text = item.umbrellaLimit, color = GoldHover, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                }
                Column {
                    Text(text = "EXPIRY", color = TextMuted, fontSize = 9.sp, fontWeight = FontWeight.SemiBold)
                    Text(text = item.expirationDate, color = TextNavyLight, fontSize = 11.sp)
                }
            }

            Spacer(modifier = Modifier.height(8.dp))
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Default.Shield,
                    contentDescription = null,
                    tint = if (item.namedInsured.contains("Verified")) NeonEmerald else AmberWarning,
                    modifier = Modifier.size(13.dp)
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = "Named Insured Rider: ${item.namedInsured}",
                    color = if (item.namedInsured.contains("Verified")) NeonEmerald else AmberWarning,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Medium
                )
            }

            if (item.deficiencyNotes != null) {
                Spacer(modifier = Modifier.height(8.dp))
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(8.dp))
                        .background(RubyRed.copy(alpha = 0.08f))
                        .border(1.dp, RubyRed.copy(alpha = 0.2f), RoundedCornerShape(8.dp))
                        .padding(8.dp)
                ) {
                    Text(
                        text = item.deficiencyNotes,
                        color = RubyRed,
                        fontSize = 11.sp
                    )
                }
            }

            // Quick Resolution Action Buttons if Not Clear
            if (item.severity != "CLEAR") {
                Spacer(modifier = Modifier.height(10.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(8.dp))
                            .background(RubyRed.copy(alpha = 0.15f))
                            .border(1.dp, RubyRed.copy(alpha = 0.3f), RoundedCornerShape(8.dp))
                            .clickable { onIssueNotice() }
                            .padding(vertical = 7.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(imageVector = Icons.Default.MailOutline, contentDescription = null, tint = RubyRed, modifier = Modifier.size(12.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(text = "Issue Deficiency", color = RubyRed, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                        }
                    }

                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(8.dp))
                            .background(NeonEmerald.copy(alpha = 0.15f))
                            .border(1.dp, NeonEmerald.copy(alpha = 0.3f), RoundedCornerShape(8.dp))
                            .clickable { onRequestCoi() }
                            .padding(vertical = 7.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(imageVector = Icons.Default.AssignmentTurnedIn, contentDescription = null, tint = NeonEmerald, modifier = Modifier.size(12.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(text = "Verify COI", color = NeonEmerald, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }
    }
}
