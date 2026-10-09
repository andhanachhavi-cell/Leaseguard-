package com.example.ui.screens

import android.content.Intent
import android.net.Uri
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
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Payment
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.filled.VerifiedUser
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.LeaseGuardViewModel
import com.example.ui.components.GlassCard
import com.example.ui.components.GoldButton
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
fun AcquisitionModal(
    viewModel: LeaseGuardViewModel,
    onDismiss: () -> Unit
) {
    val context = LocalContext.current
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    val isSubmitting by viewModel.isSubmittingLead.collectAsState()
    val leadSuccess by viewModel.leadSubmissionSuccess.collectAsState()

    var officerName by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var scaleOption by remember { mutableStateOf("100k - 1M SF") }
    var intentVerified by remember { mutableStateOf(false) }
    var scaleDropdownExpanded by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    val scaleOptions = listOf(
        "Under 100k SF (Growth Tier)",
        "100k - 1M SF (Enterprise Standard)",
        "1M+ SF Sovereign Tier"
    )

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        containerColor = SlateSurfaceDark,
        modifier = Modifier.testTag("acquisition_modal_sheet")
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp)
                .verticalScroll(rememberScrollState())
        ) {
            // Header Row
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
                            .background(GoldPrimary.copy(alpha = 0.2f))
                            .border(1.dp, GoldHover, CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(text = "⚡", fontSize = 16.sp)
                    }
                    Spacer(modifier = Modifier.width(10.dp))
                    Column {
                        Text(
                            text = "INSTITUTIONAL ACQUISITION HUB",
                            color = GoldHover,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 0.08.sp
                        )
                        Text(
                            text = "Acquire Software IP & Source",
                            color = TextPrimary,
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }

                IconButton(onClick = onDismiss) {
                    Icon(imageVector = Icons.Default.Close, contentDescription = "Close", tint = TextSecondary)
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            if (leadSuccess != null) {
                // Success Dossier Confirmation Modal
                GlassCard(
                    modifier = Modifier.fillMaxWidth(),
                    borderStroke = BorderStroke(1.dp, NeonEmerald.copy(alpha = 0.4f))
                ) {
                    Column(
                        modifier = Modifier.padding(20.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Box(
                            modifier = Modifier
                                .size(48.dp)
                                .clip(CircleShape)
                                .background(NeonEmerald.copy(alpha = 0.15f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(imageVector = Icons.Default.CheckCircle, contentDescription = null, tint = NeonEmerald, modifier = Modifier.size(28.dp))
                        }
                        Spacer(modifier = Modifier.height(10.dp))
                        Text(
                            text = "Institutional Dossier Logged",
                            color = TextPrimary,
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "Acquisition Token: ${leadSuccess!!.id}",
                            color = GoldHover,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.SemiBold
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = "Synchronized to Supabase Cloud & Local Ledger. Escrow documentation package routed to ${leadSuccess!!.email}.",
                            color = TextSecondary,
                            fontSize = 12.sp,
                            lineHeight = 17.sp
                        )

                        Spacer(modifier = Modifier.height(16.dp))
                        GoldButton(
                            text = "Proceed to Escrow Checkout ($20k USD)",
                            onClick = {
                                val intent = Intent(
                                    Intent.ACTION_VIEW,
                                    Uri.parse("https://buy.stripe.com/test_leaseguard_ip_transfer")
                                )
                                context.startActivity(intent)
                            },
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                }
            } else {
                // Core Sale Terms
                Text(
                    text = "SOFTWARE SALE TERMS & ASSET TRANSFER",
                    color = TextSecondary,
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 0.05.sp
                )
                Spacer(modifier = Modifier.height(8.dp))

                GlassCard(modifier = Modifier.fillMaxWidth()) {
                    Column(
                        modifier = Modifier.padding(14.dp),
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        SaleTermItem(title = "Immediate IP Transfer", description = "100% intellectual property, trademark and exclusive copyright assignment.")
                        SaleTermItem(title = "100% Data Sovereignty", description = "Self-hostable Docker / Kubernetes stack with zero external dependencies.")
                        SaleTermItem(title = "Clean Repository Portability", description = "Complete private GitHub / Git repository transfer with full Git history.")
                        SaleTermItem(title = "Zero Future Licensing Fees", description = "Perpetual commercial rights without seat limits or API royalties.")
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Stripe Direct Payment Button ($20k USD)
                GoldButton(
                    text = "Secure Full IP Transfer ($20k USD) via Verified Escrow/Stripe",
                    onClick = {
                        val stripeCheckoutUrl = "https://buy.stripe.com/test_leaseguard_ip_transfer"
                        val intent = Intent(Intent.ACTION_VIEW, Uri.parse(stripeCheckoutUrl))
                        context.startActivity(intent)
                    },
                    icon = {
                        Icon(imageVector = Icons.Default.Payment, contentDescription = null, tint = Color(0xFF0F172A), modifier = Modifier.size(16.dp))
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("stripe_escrow_checkout_button")
                )

                Spacer(modifier = Modifier.height(20.dp))

                // Institutional Intake Dossier Form
                Text(
                    text = "INSTITUTIONAL INTAKE DOSSIER",
                    color = TextSecondary,
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 0.05.sp
                )
                Spacer(modifier = Modifier.height(8.dp))

                GlassCard(modifier = Modifier.fillMaxWidth()) {
                    Column(
                        modifier = Modifier.padding(14.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        OutlinedTextField(
                            value = officerName,
                            onValueChange = { officerName = it },
                            label = { Text("Principal Corporate Officer Name", fontSize = 11.sp) },
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = GoldHover,
                                unfocusedBorderColor = SlateCardBorder,
                                focusedTextColor = TextPrimary,
                                unfocusedTextColor = TextPrimary
                            ),
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("input_officer_name"),
                            singleLine = true
                        )

                        OutlinedTextField(
                            value = email,
                            onValueChange = { email = it },
                            label = { Text("Enterprise Routing Email Address", fontSize = 11.sp) },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = GoldHover,
                                unfocusedBorderColor = SlateCardBorder,
                                focusedTextColor = TextPrimary,
                                unfocusedTextColor = TextPrimary
                            ),
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("input_officer_email"),
                            singleLine = true
                        )

                        // Dropdown for Infrastructure Scale
                        Box {
                            OutlinedTextField(
                                value = scaleOption,
                                onValueChange = {},
                                readOnly = true,
                                label = { Text("System Deployment Infrastructure Scale", fontSize = 11.sp) },
                                trailingIcon = {
                                    Icon(
                                        imageVector = Icons.Default.ArrowDropDown,
                                        contentDescription = "Select Scale",
                                        tint = GoldHover,
                                        modifier = Modifier.clickable { scaleDropdownExpanded = true }
                                    )
                                },
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedBorderColor = GoldHover,
                                    unfocusedBorderColor = SlateCardBorder,
                                    focusedTextColor = TextPrimary,
                                    unfocusedTextColor = TextPrimary
                                ),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable { scaleDropdownExpanded = true }
                            )

                            DropdownMenu(
                                expanded = scaleDropdownExpanded,
                                onDismissRequest = { scaleDropdownExpanded = false },
                                modifier = Modifier.background(SlateSurfaceDark)
                            ) {
                                scaleOptions.forEach { opt ->
                                    DropdownMenuItem(
                                        text = { Text(opt, color = TextPrimary, fontSize = 12.sp) },
                                        onClick = {
                                            scaleOption = opt
                                            scaleDropdownExpanded = false
                                        }
                                    )
                                }
                            }
                        }

                        // Purchase Intent Checkbox
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { intentVerified = !intentVerified }
                                .padding(vertical = 4.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(20.dp)
                                    .clip(RoundedCornerShape(4.dp))
                                    .background(if (intentVerified) GoldHover else SlateCardSurface)
                                    .border(1.dp, if (intentVerified) GoldHover else SlateCardBorder, RoundedCornerShape(4.dp)),
                                contentAlignment = Alignment.Center
                            ) {
                                if (intentVerified) {
                                    Icon(
                                        imageVector = Icons.Default.Check,
                                        contentDescription = null,
                                        tint = Color.Black,
                                        modifier = Modifier.size(14.dp)
                                    )
                                }
                            }
                            Spacer(modifier = Modifier.width(10.dp))
                            Text(
                                text = "I verify corporate authority to acquire software IP and review escrow covenants.",
                                color = TextNavyLight,
                                fontSize = 11.sp
                            )
                        }

                        if (errorMessage != null) {
                            Text(
                                text = errorMessage!!,
                                color = RubyRed,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Medium
                            )
                        }

                        GoldButton(
                            text = if (isSubmitting) "Synchronizing Dossier..." else "Submit Corporate Acquisition Dossier",
                            enabled = !isSubmitting,
                            onClick = {
                                val emailRegex = "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$".toRegex()
                                if (officerName.isBlank()) {
                                    errorMessage = "Please enter Principal Corporate Officer name."
                                    return@GoldButton
                                }
                                if (!email.matches(emailRegex)) {
                                    errorMessage = "Please enter a valid enterprise corporate email."
                                    return@GoldButton
                                }
                                if (!intentVerified) {
                                    errorMessage = "Please confirm corporate purchase intent verification."
                                    return@GoldButton
                                }
                                errorMessage = null
                                viewModel.submitAcquisitionLead(officerName, email, scaleOption, intentVerified)
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("submit_dossier_button")
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(30.dp))
        }
    }
}

@Composable
fun SaleTermItem(title: String, description: String) {
    Row(verticalAlignment = Alignment.Top) {
        Box(
            modifier = Modifier
                .padding(top = 2.dp)
                .size(6.dp)
                .clip(CircleShape)
                .background(GoldHover)
        )
        Spacer(modifier = Modifier.width(10.dp))
        Column {
            Text(
                text = title,
                color = TextPrimary,
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = description,
                color = TextSecondary,
                fontSize = 11.sp
            )
        }
    }
}
