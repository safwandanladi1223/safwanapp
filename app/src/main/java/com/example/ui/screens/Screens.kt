package com.example.ui.screens

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
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.Calculate
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.CreditCard
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.NetworkCheck
import androidx.compose.material.icons.filled.Payments
import androidx.compose.material.icons.filled.PhoneAndroid
import androidx.compose.material.icons.filled.PriceChange
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Speed
import androidx.compose.material.icons.filled.TrendingUp
import androidx.compose.material.icons.filled.Wifi
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableDoubleStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.DataPlan
import com.example.data.model.DataType
import com.example.data.model.Network
import com.example.data.model.Transaction
import com.example.ui.TransactionFilter
import com.example.ui.VtuViewModel
import com.example.ui.components.BeneficiarySelector
import com.example.ui.components.NetworkSelector
import com.example.ui.components.ReceiptRow
import com.example.ui.components.TransactionItemCard
import com.example.ui.components.WalletBalanceCard
import com.example.ui.theme.AccentCyan
import com.example.ui.theme.BorderLight
import com.example.ui.theme.PrimaryBlue
import com.example.ui.theme.PrimaryBlueLight
import com.example.ui.theme.SecondaryTeal
import com.example.ui.theme.SuccessGreen
import com.example.ui.theme.SuccessGreenBg

@Composable
fun DashboardScreen(
  viewModel: VtuViewModel,
  onNavigateToData: () -> Unit,
  onNavigateToAirtime: () -> Unit,
  onNavigateToWallet: () -> Unit,
  onNavigateToHistory: () -> Unit,
  onNavigateToPricing: () -> Unit,
  modifier: Modifier = Modifier
) {
  val wallet by viewModel.wallet.collectAsState()
  val isBalanceVisible by viewModel.isBalanceVisible.collectAsState()
  val recentTxns by viewModel.recentTransactions.collectAsState()

  LazyColumn(
    modifier = modifier
      .fillMaxSize()
      .padding(horizontal = 16.dp),
    contentPadding = PaddingValues(top = 16.dp, bottom = 24.dp),
    verticalArrangement = Arrangement.spacedBy(16.dp)
  ) {
    // 1. Header & Wallet Card
    item {
      WalletBalanceCard(
        wallet = wallet,
        isBalanceVisible = isBalanceVisible,
        onToggleVisibility = { viewModel.toggleBalanceVisibility() },
        onFundClick = { viewModel.showFundModal() }
      )
    }

    // 2. Metrics Bar (Total Spent, Orders, Savings)
    item {
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(10.dp)
      ) {
        MetricCard(
          title = "Total Spent",
          value = "₦" + String.format("%,.0f", wallet.totalSpent),
          icon = Icons.Default.Payments,
          color = PrimaryBlue,
          modifier = Modifier.weight(1f)
        )
        MetricCard(
          title = "Total Orders",
          value = "${wallet.totalOrders}",
          icon = Icons.Default.Speed,
          color = SecondaryTeal,
          modifier = Modifier.weight(1f)
        )
        MetricCard(
          title = "Discounts Earned",
          value = "₦" + String.format("%,.0f", wallet.totalCashback),
          icon = Icons.Default.TrendingUp,
          color = SuccessGreen,
          modifier = Modifier.weight(1f)
        )
      }
    }

    // 3. Quick Action Grid
    item {
      Text(
        text = "Quick Services",
        style = MaterialTheme.typography.titleMedium,
        fontWeight = FontWeight.Bold,
        color = MaterialTheme.colorScheme.onSurface
      )
      Spacer(modifier = Modifier.height(10.dp))
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(10.dp)
      ) {
        QuickActionCard(
          title = "Buy Data",
          subtitle = "From ₦140/GB",
          icon = Icons.Default.Wifi,
          tint = PrimaryBlue,
          onClick = onNavigateToData,
          modifier = Modifier.weight(1f),
          testTag = "dashboard_buy_data_btn"
        )
        QuickActionCard(
          title = "Airtime Top-Up",
          subtitle = "Up to 3.5% Off",
          icon = Icons.Default.PhoneAndroid,
          tint = SecondaryTeal,
          onClick = onNavigateToAirtime,
          modifier = Modifier.weight(1f),
          testTag = "dashboard_buy_airtime_btn"
        )
      }
      Spacer(modifier = Modifier.height(10.dp))
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(10.dp)
      ) {
        QuickActionCard(
          title = "Fund Wallet",
          subtitle = "Instant Transfer",
          icon = Icons.Default.CreditCard,
          tint = AccentCyan,
          onClick = onNavigateToWallet,
          modifier = Modifier.weight(1f),
          testTag = "dashboard_fund_wallet_btn"
        )
        QuickActionCard(
          title = "Price Guide",
          subtitle = "Reseller Margins",
          icon = Icons.Default.PriceChange,
          tint = SuccessGreen,
          onClick = onNavigateToPricing,
          modifier = Modifier.weight(1f),
          testTag = "dashboard_price_guide_btn"
        )
      }
    }

    // 4. Telecom Server Status Live Bar
    item {
      Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
      ) {
        Column(modifier = Modifier.padding(14.dp)) {
          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
          ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
              Icon(
                imageVector = Icons.Default.NetworkCheck,
                contentDescription = null,
                tint = SuccessGreen,
                modifier = Modifier.size(18.dp)
              )
              Spacer(modifier = Modifier.width(6.dp))
              Text(
                text = "Live Server Network Status",
                style = MaterialTheme.typography.labelMedium,
                fontWeight = FontWeight.Bold
              )
            }
            Text(
              text = "All Gateways Active",
              style = MaterialTheme.typography.labelSmall,
              color = SuccessGreen,
              fontWeight = FontWeight.SemiBold
            )
          }

          Spacer(modifier = Modifier.height(8.dp))

          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
          ) {
            Network.entries.forEach { net ->
              Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                  modifier = Modifier
                    .size(6.dp)
                    .clip(CircleShape)
                    .background(SuccessGreen)
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                  text = "${net.displayName} (99%)",
                  style = MaterialTheme.typography.labelSmall,
                  color = MaterialTheme.colorScheme.onSurfaceVariant
                )
              }
            }
          }
        }
      }
    }

    // 5. Recent Transactions
    item {
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Text(
          text = "Recent Transactions",
          style = MaterialTheme.typography.titleMedium,
          fontWeight = FontWeight.Bold,
          color = MaterialTheme.colorScheme.onSurface
        )
        TextButton(onClick = onNavigateToHistory) {
          Text("View All", fontWeight = FontWeight.SemiBold)
          Spacer(modifier = Modifier.width(4.dp))
          Icon(Icons.AutoMirrored.Filled.ArrowForward, contentDescription = null, modifier = Modifier.size(16.dp))
        }
      }
    }

    items(recentTxns.take(4)) { txn ->
      TransactionItemCard(
        transaction = txn,
        onClick = { viewModel.openReceipt(txn) }
      )
    }
  }
}

@Composable
fun BuyDataScreen(
  viewModel: VtuViewModel,
  modifier: Modifier = Modifier
) {
  val selectedNetwork by viewModel.selectedDataNetwork.collectAsState()
  val selectedDataType by viewModel.selectedDataType.collectAsState()
  val selectedPlan by viewModel.selectedPlan.collectAsState()
  val phone by viewModel.dataPhone.collectAsState()
  val bypassPorted by viewModel.bypassPorted.collectAsState()
  val beneficiaries by viewModel.beneficiaries.collectAsState()
  val wallet by viewModel.wallet.collectAsState()
  val availablePlans = viewModel.getPlansForCurrentSelection()

  LazyColumn(
    modifier = modifier
      .fillMaxSize()
      .padding(horizontal = 16.dp),
    contentPadding = PaddingValues(top = 16.dp, bottom = 32.dp),
    verticalArrangement = Arrangement.spacedBy(16.dp)
  ) {
    // 1. Screen Header
    item {
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Column {
          Text(
            text = "Buy Data Bundle",
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onSurface
          )
          Text(
            text = "Select network, bundle, and recipient",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
          )
        }
        Surface(
          shape = RoundedCornerShape(12.dp),
          color = PrimaryBlue.copy(alpha = 0.1f)
        ) {
          Text(
            text = "Bal: ₦" + String.format("%,.0f", wallet.balance),
            style = MaterialTheme.typography.labelMedium,
            fontWeight = FontWeight.Bold,
            color = PrimaryBlue,
            modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
          )
        }
      }
    }

    // 2. Select Network
    item {
      Text(
        text = "1. Select Network",
        style = MaterialTheme.typography.titleMedium,
        fontWeight = FontWeight.SemiBold
      )
      Spacer(modifier = Modifier.height(8.dp))
      NetworkSelector(
        selectedNetwork = selectedNetwork,
        onNetworkSelected = { viewModel.selectDataNetwork(it) }
      )
    }

    // 3. Select Data Type Tabs (SME, Gifting, Corporate)
    item {
      Text(
        text = "2. Select Data Type",
        style = MaterialTheme.typography.titleMedium,
        fontWeight = FontWeight.SemiBold
      )
      Spacer(modifier = Modifier.height(8.dp))
      TabRow(
        selectedTabIndex = selectedDataType.ordinal,
        containerColor = MaterialTheme.colorScheme.surfaceVariant,
        contentColor = PrimaryBlue,
        indicator = { tabPositions ->
          TabRowDefaults.SecondaryIndicator(
            modifier = Modifier.tabIndicatorOffset(tabPositions[selectedDataType.ordinal]),
            color = selectedNetwork.brandColor
          )
        },
        modifier = Modifier.clip(RoundedCornerShape(12.dp))
      ) {
        DataType.entries.forEach { type ->
          Tab(
            selected = selectedDataType == type,
            onClick = { viewModel.selectDataType(type) },
            text = {
              Text(
                text = "${type.displayName} (${type.badge})",
                fontWeight = if (selectedDataType == type) FontWeight.Bold else FontWeight.Medium,
                fontSize = 12.sp
              )
            }
          )
        }
      }
    }

    // 4. Available Plans Grid
    item {
      Text(
        text = "3. Select Plan (${availablePlans.size} Available)",
        style = MaterialTheme.typography.titleMedium,
        fontWeight = FontWeight.SemiBold
      )
      Spacer(modifier = Modifier.height(8.dp))

      Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        availablePlans.chunked(2).forEach { rowPlans ->
          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
          ) {
            rowPlans.forEach { plan ->
              val isSelected = plan == selectedPlan
              Card(
                modifier = Modifier
                  .weight(1f)
                  .clip(RoundedCornerShape(16.dp))
                  .clickable { viewModel.selectPlan(plan) }
                  .border(
                    width = if (isSelected) 2.5.dp else 1.dp,
                    color = if (isSelected) selectedNetwork.brandColor else MaterialTheme.colorScheme.outline.copy(alpha = 0.3f),
                    shape = RoundedCornerShape(16.dp)
                  )
                  .testTag("plan_${plan.id}"),
                colors = CardDefaults.cardColors(
                  containerColor = if (isSelected) selectedNetwork.brandColor.copy(alpha = 0.1f) else MaterialTheme.colorScheme.surface
                ),
                elevation = CardDefaults.cardElevation(defaultElevation = if (isSelected) 2.dp else 0.dp)
              ) {
                Column(modifier = Modifier.padding(12.dp)) {
                  Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                  ) {
                    Text(
                      text = plan.size,
                      style = MaterialTheme.typography.titleLarge,
                      fontWeight = FontWeight.ExtraBold,
                      color = MaterialTheme.colorScheme.onSurface
                    )
                    if (isSelected) {
                      Box(
                        modifier = Modifier
                          .size(20.dp)
                          .clip(CircleShape)
                          .background(selectedNetwork.brandColor),
                        contentAlignment = Alignment.Center
                      ) {
                        Icon(
                          imageVector = Icons.Default.Check,
                          contentDescription = null,
                          tint = selectedNetwork.textColor,
                          modifier = Modifier.size(14.dp)
                        )
                      }
                    }
                  }

                  Spacer(modifier = Modifier.height(4.dp))

                  Text(
                    text = "₦" + String.format("%,.0f", plan.price),
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = PrimaryBlue
                  )

                  Text(
                    text = "Retail: ₦${plan.retailPrice.toInt()} | ${plan.validity}",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                  )

                  Spacer(modifier = Modifier.height(4.dp))

                  Surface(
                    shape = RoundedCornerShape(6.dp),
                    color = SuccessGreenBg
                  ) {
                    Text(
                      text = "Profit: ₦${plan.profit.toInt()}",
                      style = MaterialTheme.typography.labelSmall,
                      color = SuccessGreen,
                      fontWeight = FontWeight.Bold,
                      modifier = Modifier.padding(horizontal = 4.dp, vertical = 2.dp)
                    )
                  }
                }
              }
            }
            if (rowPlans.size == 1) {
              Spacer(modifier = Modifier.weight(1f))
            }
          }
        }
      }
    }

    // 5. Phone Number Input & Beneficiaries
    item {
      Text(
        text = "4. Recipient Phone Number",
        style = MaterialTheme.typography.titleMedium,
        fontWeight = FontWeight.SemiBold
      )
      Spacer(modifier = Modifier.height(6.dp))

      BeneficiarySelector(
        beneficiaries = beneficiaries,
        onSelect = { viewModel.selectBeneficiaryForData(it) }
      )

      Spacer(modifier = Modifier.height(10.dp))

      OutlinedTextField(
        value = phone,
        onValueChange = { viewModel.updateDataPhone(it) },
        label = { Text("Phone Number (e.g., 08031234567)") },
        placeholder = { Text("0803XXXXXXX") },
        leadingIcon = {
          Icon(Icons.Default.PhoneAndroid, contentDescription = null, tint = selectedNetwork.brandColor)
        },
        trailingIcon = {
          if (phone.isNotEmpty()) {
            IconButton(onClick = { viewModel.updateDataPhone("") }) {
              Icon(Icons.Default.Clear, contentDescription = "Clear")
            }
          }
        },
        singleLine = true,
        modifier = Modifier
          .fillMaxWidth()
          .testTag("data_phone_input"),
        shape = RoundedCornerShape(14.dp),
        colors = OutlinedTextFieldDefaults.colors(
          focusedBorderColor = selectedNetwork.brandColor,
          focusedLabelColor = selectedNetwork.brandColor
        )
      )

      Spacer(modifier = Modifier.height(8.dp))

      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Column(modifier = Modifier.weight(1f)) {
          Text(
            text = "Ported Number / Bypass Auto-Detect",
            style = MaterialTheme.typography.labelMedium,
            fontWeight = FontWeight.Medium
          )
          Text(
            text = "Enable if recipient ported from another network",
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
          )
        }
        Switch(
          checked = bypassPorted,
          onCheckedChange = { viewModel.setBypassPorted(it) },
          colors = SwitchDefaults.colors(checkedThumbColor = PrimaryBlue)
        )
      }
    }

    // 6. Order Summary & Big Buy Button
    item {
      selectedPlan?.let { plan ->
        Card(
          modifier = Modifier.fillMaxWidth(),
          shape = RoundedCornerShape(16.dp),
          colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
          )
        ) {
          Column(modifier = Modifier.padding(14.dp)) {
            Text(
              text = "Purchase Summary",
              style = MaterialTheme.typography.labelMedium,
              fontWeight = FontWeight.Bold,
              color = PrimaryBlue
            )
            Spacer(modifier = Modifier.height(8.dp))
            ReceiptRow(label = "Network", value = selectedNetwork.displayName)
            ReceiptRow(label = "Package", value = "${plan.size} (${plan.type.displayName})")
            ReceiptRow(label = "Recipient", value = phone.ifBlank { "Not provided" })
            ReceiptRow(
              label = "Wallet Charge",
              value = "₦" + String.format("%,.2f", plan.price),
              isHighlight = true
            )
            ReceiptRow(
              label = "Est. Profit Margin",
              value = "₦" + String.format("%,.2f", plan.profit),
              valueColor = SuccessGreen
            )
          }
        }
      }

      Spacer(modifier = Modifier.height(14.dp))

      Button(
        onClick = { viewModel.initiateBuyData() },
        modifier = Modifier
          .fillMaxWidth()
          .height(54.dp)
          .testTag("buy_data_submit_button"),
        shape = RoundedCornerShape(14.dp),
        colors = ButtonDefaults.buttonColors(containerColor = PrimaryBlue),
        elevation = ButtonDefaults.buttonElevation(defaultElevation = 3.dp)
      ) {
        Icon(Icons.Default.Wifi, contentDescription = null, modifier = Modifier.size(20.dp))
        Spacer(modifier = Modifier.width(8.dp))
        Text(
          text = if (selectedPlan != null) "Buy Now (₦${selectedPlan!!.price.toInt()})" else "Select Bundle & Buy",
          style = MaterialTheme.typography.titleMedium,
          fontWeight = FontWeight.Bold
        )
      }
    }
  }
}

@Composable
fun AirtimeScreen(
  viewModel: VtuViewModel,
  modifier: Modifier = Modifier
) {
  val selectedNetwork by viewModel.selectedAirtimeNetwork.collectAsState()
  val phone by viewModel.airtimePhone.collectAsState()
  val amount by viewModel.airtimeAmount.collectAsState()
  val beneficiaries by viewModel.beneficiaries.collectAsState()
  val wallet by viewModel.wallet.collectAsState()

  val amountNum = amount.toDoubleOrNull() ?: 0.0
  val discount = amountNum * (selectedNetwork.airtimeDiscountPercent / 100.0)
  val payable = amountNum - discount

  LazyColumn(
    modifier = modifier
      .fillMaxSize()
      .padding(horizontal = 16.dp),
    contentPadding = PaddingValues(top = 16.dp, bottom = 32.dp),
    verticalArrangement = Arrangement.spacedBy(16.dp)
  ) {
    // 1. Title
    item {
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Column {
          Text(
            text = "Airtime Top-Up",
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onSurface
          )
          Text(
            text = "Instant airtime for all Nigerian networks",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
          )
        }
        Surface(
          shape = RoundedCornerShape(12.dp),
          color = PrimaryBlue.copy(alpha = 0.1f)
        ) {
          Text(
            text = "Bal: ₦" + String.format("%,.0f", wallet.balance),
            style = MaterialTheme.typography.labelMedium,
            fontWeight = FontWeight.Bold,
            color = PrimaryBlue,
            modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
          )
        }
      }
    }

    // 2. Network Selector
    item {
      Text(
        text = "Select Network",
        style = MaterialTheme.typography.titleMedium,
        fontWeight = FontWeight.SemiBold
      )
      Spacer(modifier = Modifier.height(8.dp))
      NetworkSelector(
        selectedNetwork = selectedNetwork,
        onNetworkSelected = { viewModel.selectAirtimeNetwork(it) }
      )
    }

    // 3. Phone Number & Beneficiaries
    item {
      Text(
        text = "Recipient Phone Number",
        style = MaterialTheme.typography.titleMedium,
        fontWeight = FontWeight.SemiBold
      )
      Spacer(modifier = Modifier.height(6.dp))

      BeneficiarySelector(
        beneficiaries = beneficiaries,
        onSelect = { viewModel.selectBeneficiaryForAirtime(it) }
      )

      Spacer(modifier = Modifier.height(10.dp))

      OutlinedTextField(
        value = phone,
        onValueChange = { viewModel.updateAirtimePhone(it) },
        label = { Text("Phone Number") },
        placeholder = { Text("080XXXXXXXX") },
        leadingIcon = {
          Icon(Icons.Default.PhoneAndroid, contentDescription = null, tint = selectedNetwork.brandColor)
        },
        trailingIcon = {
          if (phone.isNotEmpty()) {
            IconButton(onClick = { viewModel.updateAirtimePhone("") }) {
              Icon(Icons.Default.Clear, contentDescription = "Clear")
            }
          }
        },
        singleLine = true,
        modifier = Modifier
          .fillMaxWidth()
          .testTag("airtime_phone_input"),
        shape = RoundedCornerShape(14.dp)
      )
    }

    // 4. Quick Amount Presets & Custom Input
    item {
      Text(
        text = "Select or Enter Amount",
        style = MaterialTheme.typography.titleMedium,
        fontWeight = FontWeight.SemiBold
      )
      Spacer(modifier = Modifier.height(8.dp))

      val presets = listOf("100", "200", "500", "1000", "2000", "5000")
      Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        presets.chunked(3).forEach { rowPresets ->
          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
          ) {
            rowPresets.forEach { preset ->
              val isSelected = amount == preset
              OutlinedButton(
                onClick = { viewModel.updateAirtimeAmount(preset) },
                modifier = Modifier
                  .weight(1f)
                  .testTag("airtime_preset_$preset"),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.outlinedButtonColors(
                  containerColor = if (isSelected) PrimaryBlue.copy(alpha = 0.1f) else Color.Transparent
                ),
                border = BorderStroke(
                  width = if (isSelected) 2.dp else 1.dp,
                  color = if (isSelected) PrimaryBlue else MaterialTheme.colorScheme.outline.copy(alpha = 0.4f)
                )
              ) {
                Text(
                  text = "₦$preset",
                  fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                  color = if (isSelected) PrimaryBlue else MaterialTheme.colorScheme.onSurface
                )
              }
            }
          }
        }
      }

      Spacer(modifier = Modifier.height(10.dp))

      OutlinedTextField(
        value = amount,
        onValueChange = { viewModel.updateAirtimeAmount(it) },
        label = { Text("Custom Amount (₦)") },
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
          .testTag("airtime_amount_input"),
        shape = RoundedCornerShape(14.dp)
      )
    }

    // 5. Reseller CashBack Card
    item {
      Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = SuccessGreenBg)
      ) {
        Column(modifier = Modifier.padding(14.dp)) {
          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
          ) {
            Text(
              text = "${selectedNetwork.displayName} Reseller Discount (${selectedNetwork.airtimeDiscountPercent}%)",
              style = MaterialTheme.typography.titleMedium,
              fontWeight = FontWeight.Bold,
              color = SuccessGreen
            )
            Surface(
              shape = RoundedCornerShape(8.dp),
              color = SuccessGreen
            ) {
              Text(
                text = "Save ₦${discount.toInt()}",
                style = MaterialTheme.typography.labelSmall,
                color = Color.White,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
              )
            }
          }
          Spacer(modifier = Modifier.height(8.dp))
          ReceiptRow(label = "Airtime Face Value", value = "₦" + String.format("%,.2f", amountNum))
          ReceiptRow(
            label = "Instant Discount",
            value = "-₦" + String.format("%,.2f", discount),
            valueColor = SuccessGreen
          )
          ReceiptRow(
            label = "You Pay from Wallet",
            value = "₦" + String.format("%,.2f", payable),
            isHighlight = true
          )
        }
      }
    }

    // 6. Recharge Button
    item {
      Button(
        onClick = { viewModel.initiateBuyAirtime() },
        modifier = Modifier
          .fillMaxWidth()
          .height(54.dp)
          .testTag("buy_airtime_submit_button"),
        shape = RoundedCornerShape(14.dp),
        colors = ButtonDefaults.buttonColors(containerColor = PrimaryBlue),
        elevation = ButtonDefaults.buttonElevation(defaultElevation = 3.dp)
      ) {
        Icon(Icons.Default.PhoneAndroid, contentDescription = null, modifier = Modifier.size(20.dp))
        Spacer(modifier = Modifier.width(8.dp))
        Text(
          text = "Recharge Airtime (Pay ₦${payable.toInt()})",
          style = MaterialTheme.typography.titleMedium,
          fontWeight = FontWeight.Bold
        )
      }
    }
  }
}

@Composable
fun WalletScreen(
  viewModel: VtuViewModel,
  modifier: Modifier = Modifier
) {
  val wallet by viewModel.wallet.collectAsState()
  val isBalanceVisible by viewModel.isBalanceVisible.collectAsState()

  LazyColumn(
    modifier = modifier
      .fillMaxSize()
      .padding(horizontal = 16.dp),
    contentPadding = PaddingValues(top = 16.dp, bottom = 32.dp),
    verticalArrangement = Arrangement.spacedBy(16.dp)
  ) {
    item {
      Text(
        text = "Wallet & Deposit",
        style = MaterialTheme.typography.headlineSmall,
        fontWeight = FontWeight.Bold,
        color = MaterialTheme.colorScheme.onSurface
      )
      Text(
        text = "Manage your balance and automated funding channels",
        style = MaterialTheme.typography.bodyMedium,
        color = MaterialTheme.colorScheme.onSurfaceVariant
      )
    }

    item {
      WalletBalanceCard(
        wallet = wallet,
        isBalanceVisible = isBalanceVisible,
        onToggleVisibility = { viewModel.toggleBalanceVisibility() },
        onFundClick = { viewModel.showFundModal() }
      )
    }

    // Automated Virtual Account Funding Guide
    item {
      Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
      ) {
        Column(modifier = Modifier.padding(18.dp)) {
          Row(verticalAlignment = Alignment.CenterVertically) {
            Box(
              modifier = Modifier
                .size(36.dp)
                .clip(CircleShape)
                .background(PrimaryBlue.copy(alpha = 0.1f)),
              contentAlignment = Alignment.Center
            ) {
              Icon(Icons.Default.Payments, contentDescription = null, tint = PrimaryBlue, modifier = Modifier.size(20.dp))
            }
            Spacer(modifier = Modifier.width(10.dp))
            Text(
              text = "Automated Bank Transfer",
              style = MaterialTheme.typography.titleMedium,
              fontWeight = FontWeight.Bold
            )
          }

          Spacer(modifier = Modifier.height(12.dp))

          Text(
            text = "You have a dedicated virtual bank account linked to your SwiftVTU profile. Any transfer made to this account from any banking app (Kuda, OPay, GTB, Zenith, Access, etc.) reflects in your wallet in seconds.",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
          )

          Spacer(modifier = Modifier.height(14.dp))

          Surface(
            shape = RoundedCornerShape(14.dp),
            color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.6f)
          ) {
            Column(modifier = Modifier.padding(14.dp)) {
              ReceiptRow(label = "Bank Name", value = wallet.virtualBank)
              ReceiptRow(label = "Account Number", value = wallet.virtualAccountNo, isHighlight = true)
              ReceiptRow(label = "Account Name", value = wallet.virtualAccountName)
            }
          }
        }
      }
    }

    // Quick Test Deposit Simulator (Requested in Prompt)
    item {
      Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
      ) {
        Column(modifier = Modifier.padding(18.dp)) {
          Row(verticalAlignment = Alignment.CenterVertically) {
            Box(
              modifier = Modifier
                .size(36.dp)
                .clip(CircleShape)
                .background(SecondaryTeal.copy(alpha = 0.1f)),
              contentAlignment = Alignment.Center
            ) {
              Icon(Icons.Default.CreditCard, contentDescription = null, tint = SecondaryTeal, modifier = Modifier.size(20.dp))
            }
            Spacer(modifier = Modifier.width(10.dp))
            Column {
              Text(
                text = "Instant Wallet Funding (Simulator)",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
              )
              Text(
                text = "Simulates Paystack / Flutterwave instant gateway",
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
              )
            }
          }

          Spacer(modifier = Modifier.height(14.dp))

          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
          ) {
            listOf(1000.0, 2000.0, 5000.0, 10000.0).forEach { amt ->
              Button(
                onClick = { viewModel.fundWallet(amt, "Paystack Gateway") },
                modifier = Modifier
                  .weight(1f)
                  .testTag("fund_preset_${amt.toInt()}"),
                shape = RoundedCornerShape(10.dp),
                colors = ButtonDefaults.buttonColors(containerColor = PrimaryBlue)
              ) {
                Text(
                  text = "+₦${amt.toInt() / 1000}k",
                  fontSize = 11.sp,
                  fontWeight = FontWeight.Bold
                )
              }
            }
          }

          Spacer(modifier = Modifier.height(12.dp))

          Button(
            onClick = { viewModel.showFundModal() },
            modifier = Modifier
              .fillMaxWidth()
              .height(48.dp)
              .testTag("open_fund_modal_button"),
            colors = ButtonDefaults.buttonColors(containerColor = PrimaryBlueLight),
            shape = RoundedCornerShape(12.dp)
          ) {
            Icon(Icons.Default.Payments, contentDescription = null, modifier = Modifier.size(18.dp))
            Spacer(modifier = Modifier.width(8.dp))
            Text("Custom Amount & Card Checkout", fontWeight = FontWeight.Bold)
          }
        }
      }
    }
  }
}

@Composable
fun HistoryScreen(
  viewModel: VtuViewModel,
  modifier: Modifier = Modifier
) {
  val transactions by viewModel.filteredTransactions.collectAsState()
  val query by viewModel.searchQuery.collectAsState()
  val filter by viewModel.selectedFilter.collectAsState()

  LazyColumn(
    modifier = modifier
      .fillMaxSize()
      .padding(horizontal = 16.dp),
    contentPadding = PaddingValues(top = 16.dp, bottom = 32.dp),
    verticalArrangement = Arrangement.spacedBy(12.dp)
  ) {
    item {
      Text(
        text = "Transaction History",
        style = MaterialTheme.typography.headlineSmall,
        fontWeight = FontWeight.Bold,
        color = MaterialTheme.colorScheme.onSurface
      )
      Text(
        text = "Search and track all VTU and funding orders",
        style = MaterialTheme.typography.bodyMedium,
        color = MaterialTheme.colorScheme.onSurfaceVariant
      )
    }

    // Search bar
    item {
      OutlinedTextField(
        value = query,
        onValueChange = { viewModel.updateSearchQuery(it) },
        placeholder = { Text("Search by phone, reference, or network") },
        leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
        trailingIcon = {
          if (query.isNotEmpty()) {
            IconButton(onClick = { viewModel.updateSearchQuery("") }) {
              Icon(Icons.Default.Clear, contentDescription = "Clear")
            }
          }
        },
        singleLine = true,
        modifier = Modifier
          .fillMaxWidth()
          .testTag("history_search_input"),
        shape = RoundedCornerShape(14.dp)
      )
    }

    // Filter Chips
    item {
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
      ) {
        TransactionFilter.entries.forEach { f ->
          FilterChip(
            selected = filter == f,
            onClick = { viewModel.setFilter(f) },
            label = { Text(f.label) },
            shape = RoundedCornerShape(20.dp),
            colors = FilterChipDefaults.filterChipColors(
              selectedContainerColor = PrimaryBlue,
              selectedLabelColor = Color.White
            )
          )
        }
      }
    }

    item {
      Text(
        text = "Showing ${transactions.size} records",
        style = MaterialTheme.typography.labelSmall,
        color = MaterialTheme.colorScheme.onSurfaceVariant
      )
    }

    if (transactions.isEmpty()) {
      item {
        Card(
          modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 32.dp),
          shape = RoundedCornerShape(16.dp),
          colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f))
        ) {
          Column(
            modifier = Modifier
              .fillMaxWidth()
              .padding(32.dp),
            horizontalAlignment = Alignment.CenterHorizontally
          ) {
            Icon(
              imageVector = Icons.Default.Search,
              contentDescription = null,
              modifier = Modifier.size(48.dp),
              tint = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Spacer(modifier = Modifier.height(12.dp))
            Text(
              text = "No transactions found",
              style = MaterialTheme.typography.titleMedium,
              fontWeight = FontWeight.Bold
            )
            Text(
              text = "Try adjusting your search query or filter",
              style = MaterialTheme.typography.bodySmall,
              color = MaterialTheme.colorScheme.onSurfaceVariant
            )
          }
        }
      }
    } else {
      items(transactions) { txn ->
        TransactionItemCard(
          transaction = txn,
          onClick = { viewModel.openReceipt(txn) }
        )
      }
    }
  }
}

@Composable
fun PricingScreen(
  viewModel: VtuViewModel,
  modifier: Modifier = Modifier
) {
  var selectedTabNetwork by remember { mutableStateOf(Network.MTN) }
  var salesPerDay by remember { mutableIntStateOf(10) }
  val plans = viewModel.getAllPlans().filter { it.network == selectedTabNetwork }

  LazyColumn(
    modifier = modifier
      .fillMaxSize()
      .padding(horizontal = 16.dp),
    contentPadding = PaddingValues(top = 16.dp, bottom = 32.dp),
    verticalArrangement = Arrangement.spacedBy(16.dp)
  ) {
    item {
      Text(
        text = "Reseller Price Guide",
        style = MaterialTheme.typography.headlineSmall,
        fontWeight = FontWeight.Bold,
        color = MaterialTheme.colorScheme.onSurface
      )
      Text(
        text = "Real-time competitive pricing and reseller profit margins",
        style = MaterialTheme.typography.bodyMedium,
        color = MaterialTheme.colorScheme.onSurfaceVariant
      )
    }

    // Network selector
    item {
      NetworkSelector(
        selectedNetwork = selectedTabNetwork,
        onNetworkSelected = { selectedTabNetwork = it }
      )
    }

    // Interactive Reseller Profit Calculator
    item {
      Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = PrimaryBlue.copy(alpha = 0.08f)),
        border = BorderStroke(1.dp, PrimaryBlue.copy(alpha = 0.2f))
      ) {
        Column(modifier = Modifier.padding(16.dp)) {
          Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(Icons.Default.Calculate, contentDescription = null, tint = PrimaryBlue)
            Spacer(modifier = Modifier.width(8.dp))
            Text(
              text = "Reseller Profit Estimator",
              style = MaterialTheme.typography.titleMedium,
              fontWeight = FontWeight.Bold,
              color = PrimaryBlue
            )
          }
          Spacer(modifier = Modifier.height(8.dp))
          Text(
            text = "Calculate how much you can earn reselling data bundles daily:",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
          )

          Spacer(modifier = Modifier.height(10.dp))

          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
          ) {
            Text(text = "Sales per day: $salesPerDay orders", fontWeight = FontWeight.SemiBold)
            Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
              OutlinedButton(
                onClick = { if (salesPerDay > 5) salesPerDay -= 5 },
                shape = RoundedCornerShape(8.dp),
                contentPadding = PaddingValues(horizontal = 10.dp)
              ) {
                Text("-5")
              }
              Button(
                onClick = { salesPerDay += 5 },
                shape = RoundedCornerShape(8.dp),
                contentPadding = PaddingValues(horizontal = 10.dp),
                colors = ButtonDefaults.buttonColors(containerColor = PrimaryBlue)
              ) {
                Text("+5")
              }
            }
          }

          Spacer(modifier = Modifier.height(10.dp))

          val avgProfitPerOrder = 100.0 // average ₦100 profit per data sale
          val dailyProfit = salesPerDay * avgProfitPerOrder
          val monthlyProfit = dailyProfit * 30

          Surface(
            shape = RoundedCornerShape(12.dp),
            color = Color.White
          ) {
            Row(
              modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
              horizontalArrangement = Arrangement.SpaceAround
            ) {
              Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(text = "Daily Profit", style = MaterialTheme.typography.labelSmall, color = Color.Gray)
                Text(
                  text = "₦" + String.format("%,.0f", dailyProfit),
                  style = MaterialTheme.typography.titleMedium,
                  fontWeight = FontWeight.Bold,
                  color = SuccessGreen
                )
              }
              Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(text = "Monthly Profit", style = MaterialTheme.typography.labelSmall, color = Color.Gray)
                Text(
                  text = "₦" + String.format("%,.0f", monthlyProfit),
                  style = MaterialTheme.typography.titleMedium,
                  fontWeight = FontWeight.ExtraBold,
                  color = PrimaryBlue
                )
              }
            }
          }
        }
      }
    }

    // Pricing Table Header
    item {
      Text(
        text = "${selectedTabNetwork.displayName} Data Bundles Pricing",
        style = MaterialTheme.typography.titleMedium,
        fontWeight = FontWeight.Bold
      )
    }

    // Table rows
    items(plans) { plan ->
      Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
      ) {
        Row(
          modifier = Modifier
            .fillMaxWidth()
            .padding(14.dp),
          horizontalArrangement = Arrangement.SpaceBetween,
          verticalAlignment = Alignment.CenterVertically
        ) {
          Column {
            Row(verticalAlignment = Alignment.CenterVertically) {
              Text(
                text = plan.size,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
              )
              Spacer(modifier = Modifier.width(6.dp))
              Surface(
                shape = RoundedCornerShape(6.dp),
                color = selectedTabNetwork.brandColor.copy(alpha = 0.2f)
              ) {
                Text(
                  text = plan.type.displayName,
                  style = MaterialTheme.typography.labelSmall,
                  fontWeight = FontWeight.Bold,
                  color = MaterialTheme.colorScheme.onSurface,
                  modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                )
              }
            }
            Spacer(modifier = Modifier.height(2.dp))
            Text(
              text = "Validity: ${plan.validity} | Retail: ₦${plan.retailPrice.toInt()}",
              style = MaterialTheme.typography.bodySmall,
              color = MaterialTheme.colorScheme.onSurfaceVariant
            )
          }

          Column(horizontalAlignment = Alignment.End) {
            Text(
              text = "₦" + String.format("%,.0f", plan.price),
              style = MaterialTheme.typography.titleMedium,
              fontWeight = FontWeight.ExtraBold,
              color = PrimaryBlue
            )
            Surface(
              shape = RoundedCornerShape(6.dp),
              color = SuccessGreenBg
            ) {
              Text(
                text = "+₦${plan.profit.toInt()} profit",
                style = MaterialTheme.typography.labelSmall,
                color = SuccessGreen,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
              )
            }
          }
        }
      }
    }
  }
}

@Composable
fun MetricCard(
  title: String,
  value: String,
  icon: ImageVector,
  color: Color,
  modifier: Modifier = Modifier
) {
  Card(
    modifier = modifier,
    shape = RoundedCornerShape(16.dp),
    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
    elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
  ) {
    Column(modifier = Modifier.padding(12.dp)) {
      Box(
        modifier = Modifier
          .size(28.dp)
          .clip(CircleShape)
          .background(color.copy(alpha = 0.12f)),
        contentAlignment = Alignment.Center
      ) {
        Icon(
          imageVector = icon,
          contentDescription = null,
          tint = color,
          modifier = Modifier.size(16.dp)
        )
      }
      Spacer(modifier = Modifier.height(8.dp))
      Text(
        text = value,
        style = MaterialTheme.typography.titleMedium,
        fontWeight = FontWeight.ExtraBold,
        color = MaterialTheme.colorScheme.onSurface,
        maxLines = 1,
        overflow = TextOverflow.Ellipsis
      )
      Text(
        text = title,
        style = MaterialTheme.typography.labelSmall,
        color = MaterialTheme.colorScheme.onSurfaceVariant,
        maxLines = 1,
        overflow = TextOverflow.Ellipsis
      )
    }
  }
}

@Composable
fun QuickActionCard(
  title: String,
  subtitle: String,
  icon: ImageVector,
  tint: Color,
  onClick: () -> Unit,
  modifier: Modifier = Modifier,
  testTag: String = ""
) {
  Card(
    modifier = modifier
      .clip(RoundedCornerShape(18.dp))
      .clickable { onClick() }
      .testTag(testTag),
    shape = RoundedCornerShape(18.dp),
    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
    elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
  ) {
    Row(
      modifier = Modifier
        .fillMaxWidth()
        .padding(14.dp),
      verticalAlignment = Alignment.CenterVertically
    ) {
      Box(
        modifier = Modifier
          .size(42.dp)
          .clip(RoundedCornerShape(12.dp))
          .background(tint.copy(alpha = 0.12f)),
        contentAlignment = Alignment.Center
      ) {
        Icon(
          imageVector = icon,
          contentDescription = null,
          tint = tint,
          modifier = Modifier.size(22.dp)
        )
      }

      Spacer(modifier = Modifier.width(10.dp))

      Column {
        Text(
          text = title,
          style = MaterialTheme.typography.titleMedium,
          fontWeight = FontWeight.Bold,
          color = MaterialTheme.colorScheme.onSurface
        )
        Text(
          text = subtitle,
          style = MaterialTheme.typography.labelSmall,
          color = MaterialTheme.colorScheme.onSurfaceVariant
        )
      }
    }
  }
}
