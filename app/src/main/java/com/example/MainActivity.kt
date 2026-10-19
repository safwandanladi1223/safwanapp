package com.example

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountBalanceWallet
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.HeadsetMic
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.PhoneAndroid
import androidx.compose.material.icons.filled.PriceChange
import androidx.compose.material.icons.filled.Wifi
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.ui.VtuViewModel
import com.example.ui.components.ConfirmationDialog
import com.example.ui.components.FundWalletModal
import com.example.ui.components.ReceiptDialog
import com.example.ui.screens.AirtimeScreen
import com.example.ui.screens.BuyDataScreen
import com.example.ui.screens.DashboardScreen
import com.example.ui.screens.HistoryScreen
import com.example.ui.screens.PricingScreen
import com.example.ui.screens.WalletScreen
import com.example.ui.theme.AccentCyan
import com.example.ui.theme.PrimaryBlue
import com.example.ui.theme.PrimaryBlueLight
import com.example.ui.theme.SwiftVTUTheme

enum class AppScreen(val label: String, val icon: ImageVector) {
  DASHBOARD("Home", Icons.Default.Home),
  BUY_DATA("Data", Icons.Default.Wifi),
  AIRTIME("Airtime", Icons.Default.PhoneAndroid),
  WALLET("Wallet", Icons.Default.AccountBalanceWallet),
  HISTORY("History", Icons.Default.History),
  PRICING("Pricing", Icons.Default.PriceChange)
}

class MainActivity : ComponentActivity() {
  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    enableEdgeToEdge()
    setContent {
      SwiftVTUTheme {
        MainAppContent()
      }
    }
  }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainAppContent(
  viewModel: VtuViewModel = viewModel()
) {
  val context = LocalContext.current
  var currentScreen by remember { mutableStateOf(AppScreen.DASHBOARD) }
  val snackbarHostState = remember { SnackbarHostState() }
  var showSupportDialog by remember { mutableStateOf(false) }

  val confirmation by viewModel.confirmation.collectAsState()
  val showReceiptModal by viewModel.showReceiptModal.collectAsState()
  val latestReceipt by viewModel.latestReceipt.collectAsState()
  val showFundModal by viewModel.showFundModal.collectAsState()
  val isProcessing by viewModel.isProcessing.collectAsState()
  val snackbarMessage by viewModel.snackbarMessage.collectAsState()
  val wallet by viewModel.wallet.collectAsState()

  // Handle Back Button: If on a subscreen, navigate back to Dashboard
  BackHandler(enabled = currentScreen != AppScreen.DASHBOARD) {
    currentScreen = AppScreen.DASHBOARD
  }

  // Handle Snackbars
  LaunchedEffect(snackbarMessage) {
    snackbarMessage?.let { msg ->
      snackbarHostState.showSnackbar(msg)
      viewModel.clearSnackbar()
    }
  }

  Scaffold(
    topBar = {
      TopAppBar(
        title = {
          Row(verticalAlignment = Alignment.CenterVertically) {
            Box(
              modifier = Modifier
                .size(32.dp)
                .clip(CircleShape)
                .background(PrimaryBlue),
              contentAlignment = Alignment.Center
            ) {
              Icon(
                imageVector = Icons.Default.Wifi,
                contentDescription = null,
                tint = Color.White,
                modifier = Modifier.size(18.dp)
              )
            }
            Spacer(modifier = Modifier.width(10.dp))
            Column {
              Text(
                text = "SwiftVTU",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.ExtraBold,
                color = MaterialTheme.colorScheme.onSurface
              )
              Text(
                text = "VTU & Data Reseller Hub",
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
              )
            }
          }
        },
        actions = {
          IconButton(
            onClick = { currentScreen = AppScreen.PRICING },
            modifier = Modifier.testTag("pricing_guide_icon_btn")
          ) {
            Icon(
              imageVector = Icons.Default.PriceChange,
              contentDescription = "Price Guide",
              tint = MaterialTheme.colorScheme.onSurfaceVariant
            )
          }

          IconButton(
            onClick = { showSupportDialog = true },
            modifier = Modifier.testTag("support_icon_btn")
          ) {
            Icon(
              imageVector = Icons.Default.HeadsetMic,
              contentDescription = "Help & Support",
              tint = PrimaryBlue
            )
          }
        },
        colors = TopAppBarDefaults.topAppBarColors(
          containerColor = MaterialTheme.colorScheme.surface
        )
      )
    },
    bottomBar = {
      NavigationBar(
        modifier = Modifier
          .windowInsetsPadding(WindowInsets.navigationBars)
          .testTag("bottom_navigation_bar"),
        containerColor = MaterialTheme.colorScheme.surface,
        tonalElevation = 6.dp
      ) {
        AppScreen.entries.forEach { screen ->
          val isSelected = currentScreen == screen
          NavigationBarItem(
            selected = isSelected,
            onClick = { currentScreen = screen },
            icon = {
              Icon(
                imageVector = screen.icon,
                contentDescription = screen.label
              )
            },
            label = {
              Text(
                text = screen.label,
                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                fontSize = 11.sp
              )
            },
            colors = NavigationBarItemDefaults.colors(
              selectedIconColor = Color.White,
              selectedTextColor = PrimaryBlue,
              indicatorColor = PrimaryBlue,
              unselectedIconColor = MaterialTheme.colorScheme.onSurfaceVariant,
              unselectedTextColor = MaterialTheme.colorScheme.onSurfaceVariant
            ),
            modifier = Modifier.testTag("nav_item_${screen.name.lowercase()}")
          )
        }
      }
    },
    snackbarHost = { SnackbarHost(snackbarHostState) }
  ) { innerPadding ->
    Box(
      modifier = Modifier
        .fillMaxSize()
        .padding(innerPadding)
    ) {
      AnimatedContent(
        targetState = currentScreen,
        transitionSpec = { fadeIn() togetherWith fadeOut() },
        label = "screen_transition"
      ) { targetScreen ->
        when (targetScreen) {
          AppScreen.DASHBOARD -> DashboardScreen(
            viewModel = viewModel,
            onNavigateToData = { currentScreen = AppScreen.BUY_DATA },
            onNavigateToAirtime = { currentScreen = AppScreen.AIRTIME },
            onNavigateToWallet = { currentScreen = AppScreen.WALLET },
            onNavigateToHistory = { currentScreen = AppScreen.HISTORY },
            onNavigateToPricing = { currentScreen = AppScreen.PRICING }
          )
          AppScreen.BUY_DATA -> BuyDataScreen(viewModel = viewModel)
          AppScreen.AIRTIME -> AirtimeScreen(viewModel = viewModel)
          AppScreen.WALLET -> WalletScreen(viewModel = viewModel)
          AppScreen.HISTORY -> HistoryScreen(viewModel = viewModel)
          AppScreen.PRICING -> PricingScreen(viewModel = viewModel)
        }
      }

      // Processing Modal
      if (isProcessing) {
        Dialog(onDismissRequest = { /* Prevent dismiss while processing */ }) {
          Surface(
            shape = RoundedCornerShape(20.dp),
            color = MaterialTheme.colorScheme.surface,
            tonalElevation = 8.dp
          ) {
            Column(
              modifier = Modifier.padding(28.dp),
              horizontalAlignment = Alignment.CenterHorizontally
            ) {
              CircularProgressIndicator(
                color = PrimaryBlue,
                strokeWidth = 3.dp,
                modifier = Modifier.size(48.dp)
              )
              Spacer(modifier = Modifier.height(18.dp))
              Text(
                text = "Processing Transaction...",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
              )
              Spacer(modifier = Modifier.height(4.dp))
              Text(
                text = "Connecting to Telecom VTU Gateway",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
              )
            }
          }
        }
      }

      // Confirmation Dialog
      confirmation?.let { conf ->
        ConfirmationDialog(
          confirmation = conf,
          onDismiss = { viewModel.dismissConfirmation() }
        )
      }

      // Receipt Dialog
      if (showReceiptModal && latestReceipt != null) {
        ReceiptDialog(
          transaction = latestReceipt!!,
          onDismiss = { viewModel.dismissReceipt() }
        )
      }

      // Fund Wallet Modal
      if (showFundModal) {
        FundWalletModal(
          wallet = wallet,
          onFundWallet = { amount, channel -> viewModel.fundWallet(amount, channel) },
          onDismiss = { viewModel.dismissFundModal() }
        )
      }

      // Customer Support Dialog
      if (showSupportDialog) {
        AlertDialog(
          onDismissRequest = { showSupportDialog = false },
          title = {
            Row(verticalAlignment = Alignment.CenterVertically) {
              Icon(Icons.Default.HeadsetMic, contentDescription = null, tint = PrimaryBlue)
              Spacer(modifier = Modifier.width(8.dp))
              Text("Reseller 24/7 Support", fontWeight = FontWeight.Bold)
            }
          },
          text = {
            Column {
              Text(
                text = "Need help with an order, API keys, or custom volume pricing? Reach out to our reseller desk:",
                style = MaterialTheme.typography.bodyMedium
              )
              Spacer(modifier = Modifier.height(12.dp))
              Text(
                text = "• WhatsApp: +234 814 890 1234\n• Email: support@swiftvtu.ng\n• Working Hours: 24/7 Automated System",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
              )
            }
          },
          confirmButton = {
            TextButton(
              onClick = {
                val intent = Intent(Intent.ACTION_VIEW, Uri.parse("https://wa.me/2348148901234"))
                try {
                  context.startActivity(intent)
                } catch (e: Exception) {
                  // Fallback
                }
                showSupportDialog = false
              }
            ) {
              Text("Chat on WhatsApp")
            }
          },
          dismissButton = {
            TextButton(onClick = { showSupportDialog = false }) {
              Text("Close")
            }
          }
        )
      }
    }
  }
}
