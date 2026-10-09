package com.example.ui.screens

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
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.CloudDone
import androidx.compose.material.icons.filled.Key
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material3.Icon
import androidx.compose.ui.graphics.Color
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.UserRole
import com.example.data.remote.SupabaseClient
import com.example.ui.LeaseGuardViewModel
import com.example.ui.components.GlassCard
import com.example.ui.theme.GoldHover
import com.example.ui.theme.NeonEmerald
import com.example.ui.theme.SlateCardBorder
import com.example.ui.theme.SlateCardBorderSubtle
import com.example.ui.theme.SlateCardSurface
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextNavyLight
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary

@Composable
fun SecurityRbacScreen(
    viewModel: LeaseGuardViewModel,
    modifier: Modifier = Modifier
) {
    val currentRole by viewModel.currentRole.collectAsState()
    val auditLogs by viewModel.auditLogs.collectAsState()

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
                text = "ZERO-TRUST GOVERNANCE & SOVEREIGNTY",
                color = GoldHover,
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                letterSpacing = 0.08.sp
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = "Security & RBAC Console",
                color = TextPrimary,
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                letterSpacing = (-0.02).sp
            )
            Text(
                text = "Hardware-enforced AES-256-GCM encryption, role-based access & live Supabase sync.",
                color = TextSecondary,
                fontSize = 13.sp
            )
        }

        // Active Security Posture Banner
        item {
            GlassCard(
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("security_posture_card")
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
                                    .size(36.dp)
                                    .clip(CircleShape)
                                    .background(NeonEmerald.copy(alpha = 0.15f))
                                    .border(1.dp, NeonEmerald.copy(alpha = 0.3f), CircleShape),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(imageVector = Icons.Default.Shield, contentDescription = null, tint = NeonEmerald, modifier = Modifier.size(20.dp))
                            }
                            Spacer(modifier = Modifier.width(10.dp))
                            Column {
                                Text(
                                    text = "Zero-Trust Active Mesh",
                                    color = TextPrimary,
                                    fontSize = 15.sp,
                                    fontWeight = FontWeight.Bold
                                )
                                Text(
                                    text = "TLS 1.3 Strict HSTS • Row-Level Tenant Isolation",
                                    color = NeonEmerald,
                                    fontSize = 11.sp
                                )
                            }
                        }
                        Icon(imageVector = Icons.Default.CheckCircle, contentDescription = null, tint = NeonEmerald, modifier = Modifier.size(18.dp))
                    }

                    Spacer(modifier = Modifier.height(14.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        SecurityPill(label = "AES-256-GCM", detail = "At Rest", modifier = Modifier.weight(1f))
                        SecurityPill(label = "Supabase Node", detail = "Active Connected", modifier = Modifier.weight(1f))
                        SecurityPill(label = "Gemini Flash", detail = "REST Bound", modifier = Modifier.weight(1f))
                    }
                }
            }
        }

        // RBAC Role Switcher
        item {
            Text(
                text = "ROLE-BASED ACCESS CONTROL (RBAC)",
                color = TextSecondary,
                fontSize = 10.sp,
                fontWeight = FontWeight.Bold,
                letterSpacing = 0.05.sp
            )
            Spacer(modifier = Modifier.height(6.dp))

            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                UserRole.values().forEach { role ->
                    val isSelected = currentRole == role
                    GlassCard(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { viewModel.setRole(role) }
                            .testTag("role_option_${role.name}"),
                        borderStroke = if (isSelected) BorderStroke(1.5.dp, GoldHover) else BorderStroke(1.dp, SlateCardBorderSubtle)
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(14.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Box(
                                    modifier = Modifier
                                        .size(32.dp)
                                        .clip(CircleShape)
                                        .background(if (isSelected) GoldHover.copy(alpha = 0.2f) else SlateCardSurface),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Person,
                                        contentDescription = null,
                                        tint = if (isSelected) GoldHover else TextSecondary,
                                        modifier = Modifier.size(16.dp)
                                    )
                                }
                                Spacer(modifier = Modifier.width(10.dp))
                                Column {
                                    Text(
                                        text = role.title,
                                        color = if (isSelected) TextPrimary else TextSecondary,
                                        fontSize = 13.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                    Text(
                                        text = role.clearance,
                                        color = if (isSelected) GoldHover else TextMuted,
                                        fontSize = 10.sp
                                    )
                                }
                            }

                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(if (isSelected) GoldHover else SlateCardSurface)
                                    .padding(horizontal = 8.dp, vertical = 4.dp)
                            ) {
                                Text(
                                    text = role.badge,
                                    color = if (isSelected) Color.Black else TextSecondary,
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    }
                }
            }
        }

        // Cryptographic Audit Trail Ledger
        item {
            Text(
                text = "LIVE CRYPTOGRAPHIC AUDIT LOG",
                color = TextSecondary,
                fontSize = 10.sp,
                fontWeight = FontWeight.Bold,
                letterSpacing = 0.05.sp
            )
        }

        items(auditLogs, key = { it.id }) { log ->
            GlassCard(modifier = Modifier.fillMaxWidth()) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(12.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = log.action,
                            color = TextPrimary,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.SemiBold
                        )
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(text = log.timestamp, color = TextMuted, fontSize = 10.sp)
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(text = "• ${log.officer}", color = TextNavyLight, fontSize = 10.sp)
                        }
                    }

                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(6.dp))
                            .background(SlateCardSurface)
                            .border(1.dp, SlateCardBorderSubtle, RoundedCornerShape(6.dp))
                            .padding(horizontal = 6.dp, vertical = 3.dp)
                    ) {
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

        item {
            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}

@Composable
fun SecurityPill(
    label: String,
    detail: String,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(8.dp))
            .background(SlateCardSurface)
            .border(1.dp, SlateCardBorderSubtle, RoundedCornerShape(8.dp))
            .padding(horizontal = 8.dp, vertical = 6.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(text = label, color = TextPrimary, fontSize = 11.sp, fontWeight = FontWeight.Bold)
            Text(text = detail, color = NeonEmerald, fontSize = 9.sp)
        }
    }
}
