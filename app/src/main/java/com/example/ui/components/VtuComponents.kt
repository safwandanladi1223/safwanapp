package com.example.ui.components

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.AccountBalance
import androidx.compose.material.icons.filled.AddCard
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.CreditCard
import androidx.compose.material.icons.filled.Error
import androidx.compose.material.icons.filled.HourglassEmpty
import androidx.compose.material.icons.filled.PhoneAndroid
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material.icons.filled.Wallet
import androidx.compose.material.icons.filled.Wifi
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.data.model.Beneficiary
import com.example.data.model.Network
import com.example.data.model.Transaction
import com.example.data.model.TransactionStatus
import com.example.data.model.TransactionType
import com.example.data.model.UserWallet
import com.example.ui.PurchaseConfirmation
import com.example.ui.theme.AccentCyan
import com.example.ui.theme.FailedRed
import com.example.ui.theme.FailedRedBg
import com.example.ui.theme.PendingOrange
import com.example.ui.theme.PendingOrangeBg
import com.example.ui.theme.PrimaryBlue
import com.example.ui.theme.PrimaryBlueDark
import com.example.ui.theme.PrimaryBlueLight
import com.example.ui.theme.SuccessGreen
import com.example.ui.theme.SuccessGreenBg

@Composable
fun WalletBalanceCard(
  wallet: UserWallet,
  isBalanceVisible: Boolean,
  onToggleVisibility: () -> Unit,
  onFundClick: () -> Unit,
  modifier: Modifier = Modifier
) {
  val context = LocalContext.current
  Card(
    modifier = modifier
      .fillMaxWidth()
      .testTag("wallet_balance_card"),
    shape = RoundedCornerShape(24.dp),
    colors = CardDefaults.cardColors(containerColor = Color.Transparent),
    elevation = CardDefaults.cardElevation(defaultElevation = 6.dp)
  ) {
    Box(
      modifier = Modifier
        .background(
          brush = Brush.linearGradient(
            colors = listOf(PrimaryBlue, PrimaryBlueLight, AccentCyan.copy(alpha = 0.8f))
          )
        )
        .padding(20.dp)
    ) {
      Column {
        // Top Row: Reseller Tier & Greeting
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
                .background(Color.White.copy(alpha = 0.2f)),
              contentAlignment = Alignment.Center
            ) {
              Icon(
                imageVector = Icons.Default.Wallet,
                contentDescription = "Wallet",
                tint = Color.White,
                modifier = Modifier.size(20.dp)
              )
            }
            Spacer(modifier = Modifier.width(10.dp))
            Column {
              Text(
                text = "Welcome, ${wallet.resellerName.split(" ").firstOrNull() ?: "Reseller"}",
                style = MaterialTheme.typography.titleMedium,
                color = Color.White,
                fontWeight = FontWeight.Bold
              )
              Text(
                text = wallet.resellerTier,
                style = MaterialTheme.typography.labelSmall,
                color = AccentCyan,
                fontWeight = FontWeight.SemiBold
              )
            }
          }

          // Balance Eye Toggle
          IconButton(
            onClick = onToggleVisibility,
            modifier = Modifier
              .size(36.dp)
              .testTag("toggle_balance_button")
          ) {
            Icon(
              imageVector = if (isBalanceVisible) Icons.Default.Visibility else Icons.Default.VisibilityOff,
              contentDescription = "Toggle Balance",
              tint = Color.White.copy(alpha = 0.9f)
            )
          }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Balance display
        Text(
          text = "Available Wallet Balance",
          style = MaterialTheme.typography.labelMedium,
          color = Color.White.copy(alpha = 0.8f)
        )
        Spacer(modifier = Modifier.height(4.dp))
        Row(
          verticalAlignment = Alignment.Bottom,
          horizontalArrangement = Arrangement.SpaceBetween,
          modifier = Modifier.fillMaxWidth()
        ) {
          Text(
            text = if (isBalanceVisible) "₦" + String.format("%,.2f", wallet.balance) else "₦ ••••••••",
            style = MaterialTheme.typography.headlineLarge,
            color = Color.White,
            fontWeight = FontWeight.ExtraBold,
            modifier = Modifier.testTag("wallet_balance_text")
          )

          Button(
            onClick = onFundClick,
            shape = RoundedCornerShape(12.dp),
            colors = ButtonDefaults.buttonColors(
              containerColor = Color.White,
              contentColor = PrimaryBlue
            ),
            elevation = ButtonDefaults.buttonElevation(defaultElevation = 2.dp),
            modifier = Modifier.testTag("fund_wallet_button")
          ) {
            Icon(
              imageVector = Icons.Default.AddCard,
              contentDescription = null,
              modifier = Modifier.size(16.dp)
            )
            Spacer(modifier = Modifier.width(6.dp))
            Text(
              text = "Fund Wallet",
              fontWeight = FontWeight.Bold,
              style = MaterialTheme.typography.labelLarge
            )
          }
        }

        Spacer(modifier = Modifier.height(18.dp))
        HorizontalDivider(color = Color.White.copy(alpha = 0.2f))
        Spacer(modifier = Modifier.height(14.dp))

        // Virtual Account Quick Bar
        Row(
          modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(Color.Black.copy(alpha = 0.15f))
            .clickable {
              val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
              val clip = ClipData.newPlainText("Account Number", wallet.virtualAccountNo)
              clipboard.setPrimaryClip(clip)
              Toast.makeText(context, "Account number copied: ${wallet.virtualAccountNo}", Toast.LENGTH_SHORT).show()
            }
            .padding(horizontal = 12.dp, vertical = 8.dp),
          verticalAlignment = Alignment.CenterVertically,
          horizontalArrangement = Arrangement.SpaceBetween
        ) {
          Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(
              imageVector = Icons.Default.AccountBalance,
              contentDescription = null,
              tint = AccentCyan,
              modifier = Modifier.size(18.dp)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Column {
              Text(
                text = "${wallet.virtualBank}: ${wallet.virtualAccountNo}",
                style = MaterialTheme.typography.labelMedium,
                fontWeight = FontWeight.Bold,
                color = Color.White
              )
              Text(
                text = "Instant 24/7 Automated Funding (Tap to copy)",
                style = MaterialTheme.typography.labelSmall,
                color = Color.White.copy(alpha = 0.7f)
              )
            }
          }
          Icon(
            imageVector = Icons.Default.ContentCopy,
            contentDescription = "Copy Account",
            tint = Color.White.copy(alpha = 0.9f),
            modifier = Modifier.size(18.dp)
          )
        }
      }
    }
  }
}

@Composable
fun NetworkSelector(
  selectedNetwork: Network,
  onNetworkSelected: (Network) -> Unit,
  modifier: Modifier = Modifier
) {
  Row(
    modifier = modifier.fillMaxWidth(),
    horizontalArrangement = Arrangement.spacedBy(10.dp)
  ) {
    Network.entries.forEach { network ->
      val isSelected = network == selectedNetwork
      Card(
        modifier = Modifier
          .weight(1f)
          .clip(RoundedCornerShape(16.dp))
          .clickable { onNetworkSelected(network) }
          .border(
            width = if (isSelected) 2.5.dp else 1.dp,
            color = if (isSelected) network.brandColor else MaterialTheme.colorScheme.outline.copy(alpha = 0.4f),
            shape = RoundedCornerShape(16.dp)
          )
          .testTag("network_${network.id}"),
        colors = CardDefaults.cardColors(
          containerColor = if (isSelected) network.brandColor.copy(alpha = 0.12f) else MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = if (isSelected) 2.dp else 0.dp)
      ) {
        Column(
          modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 12.dp, horizontal = 6.dp),
          horizontalAlignment = Alignment.CenterHorizontally
        ) {
          Box(
            modifier = Modifier
              .size(36.dp)
              .clip(CircleShape)
              .background(network.brandColor),
            contentAlignment = Alignment.Center
          ) {
            Text(
              text = network.displayName.take(1),
              color = network.textColor,
              fontWeight = FontWeight.Black,
              fontSize = 18.sp
            )
          }

          Spacer(modifier = Modifier.height(6.dp))

          Text(
            text = network.displayName,
            style = MaterialTheme.typography.labelLarge,
            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
            color = MaterialTheme.colorScheme.onSurface,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
          )

          Text(
            text = "${network.airtimeDiscountPercent}% Off",
            style = MaterialTheme.typography.labelSmall,
            color = SuccessGreen,
            fontWeight = FontWeight.SemiBold
          )
        }
      }
    }
  }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun BeneficiarySelector(
  beneficiaries: List<Beneficiary>,
  onSelect: (Beneficiary) -> Unit,
  modifier: Modifier = Modifier
) {
  if (beneficiaries.isNotEmpty()) {
    Column(modifier = modifier.fillMaxWidth()) {
      Text(
        text = "Quick Select Saved Number",
        style = MaterialTheme.typography.labelSmall,
        color = MaterialTheme.colorScheme.onSurfaceVariant
      )
      Spacer(modifier = Modifier.height(6.dp))
      FlowRow(
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalArrangement = Arrangement.spacedBy(6.dp),
        modifier = Modifier.fillMaxWidth()
      ) {
        beneficiaries.forEach { ben ->
          Surface(
            shape = RoundedCornerShape(20.dp),
            color = ben.network.brandColor.copy(alpha = 0.12f),
            border = BorderStroke(1.dp, ben.network.brandColor.copy(alpha = 0.4f)),
            modifier = Modifier
              .clickable { onSelect(ben) }
              .testTag("beneficiary_${ben.id}")
          ) {
            Row(
              modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
              verticalAlignment = Alignment.CenterVertically
            ) {
              Box(
                modifier = Modifier
                  .size(8.dp)
                  .clip(CircleShape)
                  .background(ben.network.brandColor)
              )
              Spacer(modifier = Modifier.width(6.dp))
              Text(
                text = "${ben.name} (${ben.phone.takeLast(4)})",
                style = MaterialTheme.typography.labelSmall,
                fontWeight = FontWeight.Medium,
                color = MaterialTheme.colorScheme.onSurface
              )
            }
          }
        }
      }
    }
  }
}

@Composable
fun TransactionItemCard(
  transaction: Transaction,
  onClick: () -> Unit,
  modifier: Modifier = Modifier
) {
  Card(
    modifier = modifier
      .fillMaxWidth()
      .clip(RoundedCornerShape(16.dp))
      .clickable { onClick() }
      .testTag("transaction_${transaction.id}"),
    colors = CardDefaults.cardColors(
      containerColor = MaterialTheme.colorScheme.surface
    ),
    elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
  ) {
    Row(
      modifier = Modifier
        .fillMaxWidth()
        .padding(14.dp),
      verticalAlignment = Alignment.CenterVertically,
      horizontalArrangement = Arrangement.SpaceBetween
    ) {
      Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.weight(1f)) {
        // Icon badge based on type
        val icon = when (transaction.type) {
          TransactionType.DATA -> Icons.Default.Wifi
          TransactionType.AIRTIME -> Icons.Default.PhoneAndroid
          TransactionType.WALLET_FUNDING -> Icons.Default.AddCard
        }
        val iconBg = transaction.network?.brandColor?.copy(alpha = 0.15f) ?: PrimaryBlue.copy(alpha = 0.12f)
        val iconColor = transaction.network?.brandColor ?: PrimaryBlue

        Box(
          modifier = Modifier
            .size(44.dp)
            .clip(RoundedCornerShape(12.dp))
            .background(iconBg),
          contentAlignment = Alignment.Center
        ) {
          Icon(
            imageVector = icon,
            contentDescription = null,
            tint = iconColor,
            modifier = Modifier.size(22.dp)
          )
        }

        Spacer(modifier = Modifier.width(12.dp))

        Column {
          Text(
            text = transaction.description,
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.SemiBold,
            color = MaterialTheme.colorScheme.onSurface,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
          )
          Spacer(modifier = Modifier.height(2.dp))
          Row(verticalAlignment = Alignment.CenterVertically) {
            Text(
              text = transaction.recipient,
              style = MaterialTheme.typography.bodyMedium,
              color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Text(
              text = " • ${transaction.date}",
              style = MaterialTheme.typography.labelSmall,
              color = MaterialTheme.colorScheme.onSurfaceVariant
            )
          }
        }
      }

      Spacer(modifier = Modifier.width(12.dp))

      Column(horizontalAlignment = Alignment.End) {
        val isCredit = transaction.type == TransactionType.WALLET_FUNDING
        Text(
          text = (if (isCredit) "+₦" else "-₦") + String.format("%,.0f", transaction.amount),
          style = MaterialTheme.typography.titleMedium,
          fontWeight = FontWeight.Bold,
          color = if (isCredit) SuccessGreen else MaterialTheme.colorScheme.onSurface
        )

        Spacer(modifier = Modifier.height(4.dp))

        // Status badge
        val (statusBg, statusTextColor, statusIcon) = when (transaction.status) {
          TransactionStatus.SUCCESSFUL -> Triple(SuccessGreenBg, SuccessGreen, Icons.Default.CheckCircle)
          TransactionStatus.PENDING -> Triple(PendingOrangeBg, PendingOrange, Icons.Default.HourglassEmpty)
          TransactionStatus.FAILED -> Triple(FailedRedBg, FailedRed, Icons.Default.Error)
        }

        Surface(
          shape = RoundedCornerShape(8.dp),
          color = statusBg
        ) {
          Row(
            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
            verticalAlignment = Alignment.CenterVertically
          ) {
            Icon(
              imageVector = statusIcon,
              contentDescription = null,
              tint = statusTextColor,
              modifier = Modifier.size(10.dp)
            )
            Spacer(modifier = Modifier.width(4.dp))
            Text(
              text = transaction.status.displayName,
              style = MaterialTheme.typography.labelSmall,
              color = statusTextColor,
              fontWeight = FontWeight.Bold,
              fontSize = 10.sp
            )
          }
        }
      }
    }
  }
}

@Composable
fun ConfirmationDialog(
  confirmation: PurchaseConfirmation,
  onDismiss: () -> Unit
) {
  AlertDialog(
    onDismissRequest = onDismiss,
    title = {
      Row(verticalAlignment = Alignment.CenterVertically) {
        Box(
          modifier = Modifier
            .size(32.dp)
            .clip(CircleShape)
            .background(confirmation.network?.brandColor ?: PrimaryBlue),
          contentAlignment = Alignment.Center
        ) {
          Icon(
            imageVector = Icons.Default.CheckCircle,
            contentDescription = null,
            tint = Color.White,
            modifier = Modifier.size(20.dp)
          )
        }
        Spacer(modifier = Modifier.width(10.dp))
        Text(text = confirmation.title, fontWeight = FontWeight.Bold)
      }
    },
    text = {
      Column(modifier = Modifier.fillMaxWidth()) {
        Text(
          text = "Review purchase details before wallet deduction:",
          style = MaterialTheme.typography.bodyMedium,
          color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Spacer(modifier = Modifier.height(14.dp))

        Surface(
          shape = RoundedCornerShape(12.dp),
          color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
          modifier = Modifier.fillMaxWidth()
        ) {
          Column(modifier = Modifier.padding(12.dp)) {
            ReceiptRow(label = "Package / Value", value = confirmation.planOrAmount)
            ReceiptRow(label = "Recipient Phone", value = confirmation.recipient)
            confirmation.network?.let {
              ReceiptRow(label = "Network", value = it.displayName)
            }
            ReceiptRow(
              label = "Total Wallet Charge",
              value = "₦" + String.format("%,.2f", confirmation.cost),
              isHighlight = true
            )
            if (confirmation.discountOrProfit > 0) {
              ReceiptRow(
                label = "Profit / Cashback",
                value = "₦" + String.format("%,.2f", confirmation.discountOrProfit),
                valueColor = SuccessGreen
              )
            }
          }
        }
      }
    },
    confirmButton = {
      Button(
        onClick = confirmation.onConfirm,
        colors = ButtonDefaults.buttonColors(containerColor = PrimaryBlue),
        modifier = Modifier.testTag("confirm_purchase_button")
      ) {
        Text("Confirm & Pay")
      }
    },
    dismissButton = {
      TextButton(onClick = onDismiss) {
        Text("Cancel")
      }
    }
  )
}

@Composable
fun ReceiptDialog(
  transaction: Transaction,
  onDismiss: () -> Unit
) {
  val context = LocalContext.current

  Dialog(onDismissRequest = onDismiss) {
    Card(
      modifier = Modifier
        .fillMaxWidth()
        .clip(RoundedCornerShape(24.dp))
        .testTag("receipt_dialog"),
      colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
      elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
    ) {
      Column(
        modifier = Modifier
          .fillMaxWidth()
          .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
      ) {
        // Status Icon Badge
        Box(
          modifier = Modifier
            .size(60.dp)
            .clip(CircleShape)
            .background(
              when (transaction.status) {
                TransactionStatus.SUCCESSFUL -> SuccessGreenBg
                TransactionStatus.PENDING -> PendingOrangeBg
                TransactionStatus.FAILED -> FailedRedBg
              }
            ),
          contentAlignment = Alignment.Center
        ) {
          Icon(
            imageVector = when (transaction.status) {
              TransactionStatus.SUCCESSFUL -> Icons.Default.CheckCircle
              TransactionStatus.PENDING -> Icons.Default.HourglassEmpty
              TransactionStatus.FAILED -> Icons.Default.Error
            },
            contentDescription = null,
            tint = when (transaction.status) {
              TransactionStatus.SUCCESSFUL -> SuccessGreen
              TransactionStatus.PENDING -> PendingOrange
              TransactionStatus.FAILED -> FailedRed
            },
            modifier = Modifier.size(36.dp)
          )
        }

        Spacer(modifier = Modifier.height(12.dp))

        Text(
          text = "Transaction ${transaction.status.displayName}",
          style = MaterialTheme.typography.titleLarge,
          fontWeight = FontWeight.Bold,
          color = MaterialTheme.colorScheme.onSurface
        )

        Text(
          text = "₦" + String.format("%,.2f", transaction.amount),
          style = MaterialTheme.typography.headlineMedium,
          fontWeight = FontWeight.ExtraBold,
          color = PrimaryBlue
        )

        Spacer(modifier = Modifier.height(18.dp))
        HorizontalDivider(color = MaterialTheme.colorScheme.outline.copy(alpha = 0.3f))
        Spacer(modifier = Modifier.height(14.dp))

        Column(modifier = Modifier.fillMaxWidth()) {
          ReceiptRow(label = "Description", value = transaction.description)
          ReceiptRow(label = "Recipient", value = transaction.recipient)
          ReceiptRow(label = "Reference", value = transaction.reference)
          ReceiptRow(label = "Date & Time", value = "${transaction.date}, ${transaction.time}")
          ReceiptRow(label = "Payment Method", value = transaction.channel)
          if (transaction.cashback > 0) {
            ReceiptRow(
              label = "Cashback Earned",
              value = "₦" + String.format("%,.2f", transaction.cashback),
              valueColor = SuccessGreen
            )
          }
          ReceiptRow(
            label = "Wallet Balance After",
            value = "₦" + String.format("%,.2f", transaction.balanceAfter)
          )
        }

        Spacer(modifier = Modifier.height(20.dp))

        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
          OutlinedButton(
            onClick = {
              val shareText = """
                ===========================
                SWIFTVTU TRANSACTION RECEIPT
                ===========================
                Status: ${transaction.status.displayName}
                Amount: ₦${String.format("%,.2f", transaction.amount)}
                Service: ${transaction.description}
                Recipient: ${transaction.recipient}
                Ref: ${transaction.reference}
                Date: ${transaction.date} ${transaction.time}
                Thank you for using SwiftVTU!
              """.trimIndent()
              val sendIntent: Intent = Intent().apply {
                action = Intent.ACTION_SEND
                putExtra(Intent.EXTRA_TEXT, shareText)
                type = "text/plain"
              }
              val shareIntent = Intent.createChooser(sendIntent, "Share Receipt")
              context.startActivity(shareIntent)
            },
            modifier = Modifier.weight(1f),
            shape = RoundedCornerShape(12.dp)
          ) {
            Icon(Icons.Default.Share, contentDescription = null, modifier = Modifier.size(16.dp))
            Spacer(modifier = Modifier.width(6.dp))
            Text("Share")
          }

          Button(
            onClick = onDismiss,
            modifier = Modifier.weight(1f),
            colors = ButtonDefaults.buttonColors(containerColor = PrimaryBlue),
            shape = RoundedCornerShape(12.dp)
          ) {
            Text("Done")
          }
        }
      }
    }
  }
}

@Composable
fun FundWalletModal(
  wallet: UserWallet,
  onFundWallet: (Double, String) -> Unit,
  onDismiss: () -> Unit
) {
  val context = LocalContext.current
  var activeTab by remember { mutableStateOf(0) } // 0: Virtual Account, 1: Instant Card / Simulator
  var customAmount by remember { mutableStateOf("2000") }

  Dialog(onDismissRequest = onDismiss) {
    Card(
      modifier = Modifier
        .fillMaxWidth()
        .clip(RoundedCornerShape(24.dp))
        .testTag("fund_wallet_modal"),
      colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
      elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
    ) {
      Column(
        modifier = Modifier
          .fillMaxWidth()
          .padding(20.dp)
      ) {
        Text(
          text = "Fund Your Reseller Wallet",
          style = MaterialTheme.typography.titleLarge,
          fontWeight = FontWeight.Bold,
          color = MaterialTheme.colorScheme.onSurface
        )
        Text(
          text = "Choose your preferred deposit method",
          style = MaterialTheme.typography.bodyMedium,
          color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        Spacer(modifier = Modifier.height(14.dp))

        // Tabs
        Row(
          modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(MaterialTheme.colorScheme.surfaceVariant)
            .padding(4.dp)
        ) {
          Box(
            modifier = Modifier
              .weight(1f)
              .clip(RoundedCornerShape(10.dp))
              .background(if (activeTab == 0) MaterialTheme.colorScheme.surface else Color.Transparent)
              .clickable { activeTab = 0 }
              .padding(vertical = 8.dp),
            contentAlignment = Alignment.Center
          ) {
            Text(
              text = "Bank Transfer",
              fontWeight = if (activeTab == 0) FontWeight.Bold else FontWeight.Medium,
              color = MaterialTheme.colorScheme.onSurface,
              style = MaterialTheme.typography.labelMedium
            )
          }

          Box(
            modifier = Modifier
              .weight(1f)
              .clip(RoundedCornerShape(10.dp))
              .background(if (activeTab == 1) MaterialTheme.colorScheme.surface else Color.Transparent)
              .clickable { activeTab = 1 }
              .padding(vertical = 8.dp),
            contentAlignment = Alignment.Center
          ) {
            Text(
              text = "Card / Instant Pay",
              fontWeight = if (activeTab == 1) FontWeight.Bold else FontWeight.Medium,
              color = MaterialTheme.colorScheme.onSurface,
              style = MaterialTheme.typography.labelMedium
            )
          }
        }

        Spacer(modifier = Modifier.height(16.dp))

        if (activeTab == 0) {
          // Virtual Account Details
          Surface(
            shape = RoundedCornerShape(16.dp),
            color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
            border = BorderStroke(1.dp, PrimaryBlueLight.copy(alpha = 0.3f)),
            modifier = Modifier.fillMaxWidth()
          ) {
            Column(modifier = Modifier.padding(16.dp)) {
              Text(
                text = "Dedicated Funding Account",
                style = MaterialTheme.typography.labelSmall,
                color = PrimaryBlue,
                fontWeight = FontWeight.Bold
              )
              Spacer(modifier = Modifier.height(8.dp))

              ReceiptRow(label = "Bank Name", value = wallet.virtualBank)
              ReceiptRow(label = "Account Number", value = wallet.virtualAccountNo, isHighlight = true)
              ReceiptRow(label = "Account Name", value = wallet.virtualAccountName)

              Spacer(modifier = Modifier.height(8.dp))

              Button(
                onClick = {
                  val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                  val clip = ClipData.newPlainText("Account Number", wallet.virtualAccountNo)
                  clipboard.setPrimaryClip(clip)
                  Toast.makeText(context, "Copied ${wallet.virtualAccountNo}!", Toast.LENGTH_SHORT).show()
                },
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(containerColor = PrimaryBlue),
                shape = RoundedCornerShape(10.dp)
              ) {
                Icon(Icons.Default.ContentCopy, contentDescription = null, modifier = Modifier.size(16.dp))
                Spacer(modifier = Modifier.width(6.dp))
                Text("Copy Account Number")
              }
            }
          }

          Spacer(modifier = Modifier.height(12.dp))
          Text(
            text = "• Money transferred to this account is credited to your wallet in seconds automatically.\n• Standard 1% service fee capped at ₦50.",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
          )
        } else {
          // Card / Instant Simulator
          Column(modifier = Modifier.fillMaxWidth()) {
            Text(
              text = "Select or Enter Amount",
              style = MaterialTheme.typography.labelMedium,
              fontWeight = FontWeight.SemiBold
            )
            Spacer(modifier = Modifier.height(8.dp))

            // Preset Buttons
            Row(
              modifier = Modifier.fillMaxWidth(),
              horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
              listOf("1000", "2000", "5000", "10000").forEach { preset ->
                OutlinedButton(
                  onClick = { customAmount = preset },
                  modifier = Modifier.weight(1f),
                  shape = RoundedCornerShape(10.dp),
                  colors = ButtonDefaults.outlinedButtonColors(
                    containerColor = if (customAmount == preset) PrimaryBlue.copy(alpha = 0.1f) else Color.Transparent
                  )
                ) {
                  Text(
                    text = "₦$preset",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = if (customAmount == preset) PrimaryBlue else MaterialTheme.colorScheme.onSurface
                  )
                }
              }
            }

            Spacer(modifier = Modifier.height(12.dp))

            OutlinedTextField(
              value = customAmount,
              onValueChange = { customAmount = it.filter { ch -> ch.isDigit() } },
              label = { Text("Amount (₦)") },
              leadingIcon = {
                Text(
                  "₦",
                  fontWeight = FontWeight.Bold,
                  fontSize = 18.sp,
                  color = PrimaryBlue,
                  modifier = Modifier.padding(start = 12.dp)
                )
              },
              singleLine = true,
              modifier = Modifier
                .fillMaxWidth()
                .testTag("fund_amount_input"),
              shape = RoundedCornerShape(12.dp)
            )

            Spacer(modifier = Modifier.height(14.dp))

            Button(
              onClick = {
                val amt = customAmount.toDoubleOrNull() ?: 1000.0
                onFundWallet(amt, "Paystack Card Gateway")
              },
              modifier = Modifier
                .fillMaxWidth()
                .height(48.dp)
                .testTag("pay_fund_wallet_button"),
              colors = ButtonDefaults.buttonColors(containerColor = PrimaryBlue),
              shape = RoundedCornerShape(12.dp)
            ) {
              Icon(Icons.Default.CreditCard, contentDescription = null, modifier = Modifier.size(18.dp))
              Spacer(modifier = Modifier.width(8.dp))
              Text(
                text = "Pay ₦${customAmount.ifBlank { "0" }} & Credit Wallet",
                fontWeight = FontWeight.Bold
              )
            }
          }
        }

        Spacer(modifier = Modifier.height(14.dp))

        TextButton(
          onClick = onDismiss,
          modifier = Modifier.align(Alignment.CenterHorizontally)
        ) {
          Text("Close")
        }
      }
    }
  }
}

@Composable
fun ReceiptRow(
  label: String,
  value: String,
  isHighlight: Boolean = false,
  valueColor: Color = MaterialTheme.colorScheme.onSurface
) {
  Row(
    modifier = Modifier
      .fillMaxWidth()
      .padding(vertical = 4.dp),
    horizontalArrangement = Arrangement.SpaceBetween,
    verticalAlignment = Alignment.CenterVertically
  ) {
    Text(
      text = label,
      style = MaterialTheme.typography.bodyMedium,
      color = MaterialTheme.colorScheme.onSurfaceVariant
    )
    Text(
      text = value,
      style = if (isHighlight) MaterialTheme.typography.titleMedium else MaterialTheme.typography.bodyMedium,
      fontWeight = if (isHighlight) FontWeight.Bold else FontWeight.Medium,
      color = valueColor
    )
  }
}
