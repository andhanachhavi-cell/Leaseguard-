package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.Crossfade
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Calculate
import androidx.compose.material.icons.filled.Dashboard
import androidx.compose.material.icons.filled.FolderShared
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.ui.LeaseGuardViewModel
import com.example.ui.screens.AcquisitionModal
import com.example.ui.screens.CoiMatrixScreen
import com.example.ui.screens.CpiCalculatorScreen
import com.example.ui.screens.DashboardEngineScreen
import com.example.ui.screens.LeaseVaultScreen
import com.example.ui.screens.SecurityRbacScreen
import com.example.ui.theme.GoldHover
import com.example.ui.theme.GoldPrimary
import com.example.ui.theme.LeaseGuardTheme
import com.example.ui.theme.NeonEmerald
import com.example.ui.theme.SlateCanvasDark
import com.example.ui.theme.SlateCanvasNavy
import com.example.ui.theme.SlateCardBorderSubtle
import com.example.ui.theme.SlateCardSurface
import com.example.ui.theme.SlateSurfaceDark
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary

enum class AppNavTab(val label: String, val icon: ImageVector) {
    DASHBOARD("Engine", Icons.Default.Dashboard),
    LEASE_VAULT("Vault", Icons.Default.FolderShared),
    CPI_CALCULATOR("CPI Calc", Icons.Default.Calculate),
    COI_MATRIX("COI Matrix", Icons.Default.Shield),
    SECURITY("RBAC", Icons.Default.Security)
}

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            LeaseGuardTheme {
                val viewModel: LeaseGuardViewModel = viewModel()
                MainAppScreen(viewModel = viewModel)
            }
        }
    }
}

@Composable
fun MainAppScreen(viewModel: LeaseGuardViewModel) {
    var selectedTab by remember { mutableStateOf(AppNavTab.DASHBOARD) }
    val acquisitionModalOpen by viewModel.acquisitionModalOpen.collectAsState()

    val backgroundGradient = Brush.verticalGradient(
        colors = listOf(SlateCanvasDark, SlateCanvasNavy)
    )

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(backgroundGradient)
    ) {
        Scaffold(
            containerColor = Color.Transparent,
            topBar = {
                LeaseGuardTopBar(
                    onAcquireClick = { viewModel.setAcquisitionModalOpen(true) }
                )
            },
            bottomBar = {
                LeaseGuardBottomNavigation(
                    selectedTab = selectedTab,
                    onTabSelected = { selectedTab = it },
                    modifier = Modifier.navigationBarsPadding()
                )
            }
        ) { innerPadding ->
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
            ) {
                Crossfade(targetState = selectedTab, label = "tab_transition") { tab ->
                    when (tab) {
                        AppNavTab.DASHBOARD -> DashboardEngineScreen(
                            viewModel = viewModel,
                            onNavigateToLeaseVault = { selectedTab = AppNavTab.LEASE_VAULT }
                        )
                        AppNavTab.LEASE_VAULT -> LeaseVaultScreen(viewModel = viewModel)
                        AppNavTab.CPI_CALCULATOR -> CpiCalculatorScreen(viewModel = viewModel)
                        AppNavTab.COI_MATRIX -> CoiMatrixScreen(viewModel = viewModel)
                        AppNavTab.SECURITY -> SecurityRbacScreen(viewModel = viewModel)
                    }
                }
            }
        }

        // Acquisition Hub Modal
        if (acquisitionModalOpen) {
            AcquisitionModal(
                viewModel = viewModel,
                onDismiss = { viewModel.setAcquisitionModalOpen(false) }
            )
        }
    }
}

@Composable
fun LeaseGuardTopBar(
    onAcquireClick: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(SlateCanvasDark.copy(alpha = 0.95f))
            .windowInsetsPadding(WindowInsets.statusBars)
            .padding(horizontal = 16.dp, vertical = 8.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            // App Branding & Crest
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(34.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .background(SlateCardSurface)
                        .border(1.dp, GoldHover.copy(alpha = 0.4f), RoundedCornerShape(8.dp)),
                    contentAlignment = Alignment.Center
                ) {
                    androidx.compose.foundation.Image(
                        painter = painterResource(id = R.drawable.ic_leaseguard_logo),
                        contentDescription = "LeaseGuard Logo",
                        modifier = Modifier.size(24.dp)
                    )
                }

                Spacer(modifier = Modifier.width(10.dp))
                Column {
                    Text(
                        text = "LEASEGUARD AI",
                        color = TextPrimary,
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 0.04.sp
                    )
                    Text(
                        text = "CRE Enterprise Platform",
                        color = GoldHover,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.SemiBold,
                        letterSpacing = 0.05.sp
                    )
                }
            }

            // Global Call-To-Action: ⚡ Acquire Software IP & Source Code
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(10.dp))
                    .background(
                        Brush.horizontalGradient(
                            listOf(GoldPrimary, GoldHover)
                        )
                    )
                    .clickable { onAcquireClick() }
                    .padding(horizontal = 10.dp, vertical = 7.dp)
                    .testTag("top_nav_acquire_ip_button"),
                contentAlignment = Alignment.Center
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = "⚡",
                        fontSize = 11.sp
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "Acquire IP",
                        color = Color(0xFF0F172A),
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(6.dp))
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(1.dp)
                .background(SlateCardBorderSubtle)
        )
    }
}

@Composable
fun LeaseGuardBottomNavigation(
    selectedTab: AppNavTab,
    onTabSelected: (AppNavTab) -> Unit,
    modifier: Modifier = Modifier
) {
    NavigationBar(
        modifier = modifier
            .fillMaxWidth()
            .border(1.dp, SlateCardBorderSubtle, RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp)),
        containerColor = SlateSurfaceDark,
        tonalElevation = 8.dp
    ) {
        AppNavTab.values().forEach { tab ->
            val isSelected = selectedTab == tab
            NavigationBarItem(
                selected = isSelected,
                onClick = { onTabSelected(tab) },
                icon = {
                    Icon(
                        imageVector = tab.icon,
                        contentDescription = tab.label,
                        modifier = Modifier.size(20.dp)
                    )
                },
                label = {
                    Text(
                        text = tab.label,
                        fontSize = 10.sp,
                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium
                    )
                },
                colors = NavigationBarItemDefaults.colors(
                    selectedIconColor = GoldHover,
                    selectedTextColor = GoldHover,
                    unselectedIconColor = TextSecondary,
                    unselectedTextColor = TextMuted,
                    indicatorColor = SlateCardSurface
                ),
                modifier = Modifier.testTag("nav_tab_${tab.name.lowercase()}")
            )
        }
    }
}
