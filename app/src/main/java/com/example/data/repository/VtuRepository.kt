package com.example.data.repository

import com.example.data.model.Beneficiary
import com.example.data.model.DataPlan
import com.example.data.model.DataType
import com.example.data.model.Network
import com.example.data.model.Transaction
import com.example.data.model.TransactionStatus
import com.example.data.model.TransactionType
import com.example.data.model.UserWallet
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.UUID

class VtuRepository {

  private val _wallet = MutableStateFlow(UserWallet())
  val wallet: StateFlow<UserWallet> = _wallet.asStateFlow()

  private val _transactions = MutableStateFlow<List<Transaction>>(createInitialTransactions())
  val transactions: StateFlow<List<Transaction>> = _transactions.asStateFlow()

  private val _beneficiaries = MutableStateFlow<List<Beneficiary>>(createInitialBeneficiaries())
  val beneficiaries: StateFlow<List<Beneficiary>> = _beneficiaries.asStateFlow()

  val allPlans: List<DataPlan> = createDataPlans()

  fun getPlans(network: Network, type: DataType): List<DataPlan> {
    return allPlans.filter { it.network == network && it.type == type }
  }

  suspend fun buyData(
    network: Network,
    plan: DataPlan,
    phone: String
  ): Result<Transaction> {
    val currentWallet = _wallet.value
    if (currentWallet.balance < plan.price) {
      return Result.failure(IllegalStateException("Insufficient wallet balance. Please fund your wallet."))
    }

    val newBalance = currentWallet.balance - plan.price
    _wallet.value = currentWallet.copy(
      balance = newBalance,
      totalSpent = currentWallet.totalSpent + plan.price,
      totalOrders = currentWallet.totalOrders + 1
    )

    val dateFormat = SimpleDateFormat("dd MMM yyyy", Locale.getDefault())
    val timeFormat = SimpleDateFormat("hh:mm a", Locale.getDefault())
    val now = Date()

    val txn = Transaction(
      id = UUID.randomUUID().toString(),
      reference = "VTU-DAT-" + System.currentTimeMillis().toString().takeLast(8),
      type = TransactionType.DATA,
      network = network,
      recipient = phone,
      description = "${network.displayName} ${plan.size} ${plan.type.displayName}",
      amount = plan.price,
      balanceAfter = newBalance,
      date = dateFormat.format(now),
      time = timeFormat.format(now),
      status = TransactionStatus.SUCCESSFUL,
      channel = "Wallet Balance"
    )

    _transactions.value = listOf(txn) + _transactions.value
    return Result.success(txn)
  }

  suspend fun buyAirtime(
    network: Network,
    amount: Double,
    phone: String
  ): Result<Transaction> {
    val currentWallet = _wallet.value
    val discount = amount * (network.airtimeDiscountPercent / 100.0)
    val payable = amount - discount

    if (currentWallet.balance < payable) {
      return Result.failure(IllegalStateException("Insufficient wallet balance. Please fund your wallet."))
    }

    val newBalance = currentWallet.balance - payable
    _wallet.value = currentWallet.copy(
      balance = newBalance,
      totalSpent = currentWallet.totalSpent + payable,
      totalOrders = currentWallet.totalOrders + 1,
      totalCashback = currentWallet.totalCashback + discount
    )

    val dateFormat = SimpleDateFormat("dd MMM yyyy", Locale.getDefault())
    val timeFormat = SimpleDateFormat("hh:mm a", Locale.getDefault())
    val now = Date()

    val txn = Transaction(
      id = UUID.randomUUID().toString(),
      reference = "VTU-AIR-" + System.currentTimeMillis().toString().takeLast(8),
      type = TransactionType.AIRTIME,
      network = network,
      recipient = phone,
      description = "${network.displayName} Airtime Top-Up (₦${amount.toInt()})",
      amount = payable,
      cashback = discount,
      balanceAfter = newBalance,
      date = dateFormat.format(now),
      time = timeFormat.format(now),
      status = TransactionStatus.SUCCESSFUL,
      channel = "Wallet Balance"
    )

    _transactions.value = listOf(txn) + _transactions.value
    return Result.success(txn)
  }

  suspend fun fundWallet(
    amount: Double,
    channel: String,
    reference: String = "FUND-" + System.currentTimeMillis().toString().takeLast(8)
  ): Transaction {
    val currentWallet = _wallet.value
    val newBalance = currentWallet.balance + amount

    _wallet.value = currentWallet.copy(
      balance = newBalance
    )

    val dateFormat = SimpleDateFormat("dd MMM yyyy", Locale.getDefault())
    val timeFormat = SimpleDateFormat("hh:mm a", Locale.getDefault())
    val now = Date()

    val txn = Transaction(
      id = UUID.randomUUID().toString(),
      reference = reference,
      type = TransactionType.WALLET_FUNDING,
      network = null,
      recipient = currentWallet.resellerPhone,
      description = "Wallet Deposit via $channel",
      amount = amount,
      balanceAfter = newBalance,
      date = dateFormat.format(now),
      time = timeFormat.format(now),
      status = TransactionStatus.SUCCESSFUL,
      channel = channel
    )

    _transactions.value = listOf(txn) + _transactions.value
    return txn
  }

  fun saveBeneficiary(name: String, phone: String, network: Network) {
    val newBeneficiary = Beneficiary(
      id = UUID.randomUUID().toString(),
      name = name,
      phone = phone,
      network = network
    )
    _beneficiaries.value = _beneficiaries.value + newBeneficiary
  }

  private fun createInitialBeneficiaries(): List<Beneficiary> {
    return listOf(
      Beneficiary("1", "My Personal SIM", "08148901234", Network.MTN),
      Beneficiary("2", "Mom Home", "08023456789", Network.AIRTEL),
      Beneficiary("3", "Office Router", "08051239876", Network.GLO),
      Beneficiary("4", "Junior Brother", "08098765432", Network.NINEMOBILE)
    )
  }

  private fun createInitialTransactions(): List<Transaction> {
    return listOf(
      Transaction(
        id = "tx1",
        reference = "VTU-DAT-98234102",
        type = TransactionType.DATA,
        network = Network.MTN,
        recipient = "08148901234",
        description = "MTN 2.0GB SME Data",
        amount = 550.0,
        balanceAfter = 14850.0,
        date = "18 Oct 2026",
        time = "02:14 PM",
        status = TransactionStatus.SUCCESSFUL,
        channel = "Wallet Balance"
      ),
      Transaction(
        id = "tx2",
        reference = "FUND-88234901",
        type = TransactionType.WALLET_FUNDING,
        network = null,
        recipient = "08148901234",
        description = "Wallet Top-up via Dedicated Account",
        amount = 10000.0,
        balanceAfter = 15400.0,
        date = "18 Oct 2026",
        time = "11:30 AM",
        status = TransactionStatus.SUCCESSFUL,
        channel = "Moniepoint MFB Transfer"
      ),
      Transaction(
        id = "tx3",
        reference = "VTU-AIR-77123908",
        type = TransactionType.AIRTIME,
        network = Network.AIRTEL,
        recipient = "08023456789",
        description = "Airtel Airtime (₦2,000)",
        amount = 1960.0,
        cashback = 40.0,
        balanceAfter = 5400.0,
        date = "17 Oct 2026",
        time = "07:45 PM",
        status = TransactionStatus.SUCCESSFUL,
        channel = "Wallet Balance"
      ),
      Transaction(
        id = "tx4",
        reference = "VTU-DAT-66324891",
        type = TransactionType.DATA,
        network = Network.GLO,
        recipient = "08051239876",
        description = "Glo 5.0GB Corporate Gifting",
        amount = 1225.0,
        balanceAfter = 7360.0,
        date = "17 Oct 2026",
        time = "04:12 PM",
        status = TransactionStatus.SUCCESSFUL,
        channel = "Wallet Balance"
      ),
      Transaction(
        id = "tx5",
        reference = "VTU-DAT-55214890",
        type = TransactionType.DATA,
        network = Network.MTN,
        recipient = "09038291029",
        description = "MTN 10.0GB SME Data",
        amount = 2750.0,
        balanceAfter = 8585.0,
        date = "16 Oct 2026",
        time = "09:20 AM",
        status = TransactionStatus.SUCCESSFUL,
        channel = "Wallet Balance"
      ),
      Transaction(
        id = "tx6",
        reference = "VTU-DAT-44129034",
        type = TransactionType.DATA,
        network = Network.NINEMOBILE,
        recipient = "08098765432",
        description = "9mobile 1.0GB Corporate",
        amount = 195.0,
        balanceAfter = 11335.0,
        date = "15 Oct 2026",
        time = "01:05 PM",
        status = TransactionStatus.SUCCESSFUL,
        channel = "Wallet Balance"
      )
    )
  }

  private fun createDataPlans(): List<DataPlan> {
    val plans = mutableListOf<DataPlan>()

    // MTN SME
    plans.add(DataPlan("mtn_sme_500mb", Network.MTN, DataType.SME, "500MB", 140.0, 200.0, "30 Days"))
    plans.add(DataPlan("mtn_sme_1gb", Network.MTN, DataType.SME, "1.0GB", 275.0, 350.0, "30 Days"))
    plans.add(DataPlan("mtn_sme_2gb", Network.MTN, DataType.SME, "2.0GB", 550.0, 700.0, "30 Days"))
    plans.add(DataPlan("mtn_sme_3gb", Network.MTN, DataType.SME, "3.0GB", 825.0, 1050.0, "30 Days"))
    plans.add(DataPlan("mtn_sme_5gb", Network.MTN, DataType.SME, "5.0GB", 1375.0, 1750.0, "30 Days"))
    plans.add(DataPlan("mtn_sme_10gb", Network.MTN, DataType.SME, "10.0GB", 2750.0, 3500.0, "30 Days"))

    // MTN Corporate
    plans.add(DataPlan("mtn_corp_1gb", Network.MTN, DataType.CORPORATE, "1.0GB", 285.0, 350.0, "30 Days"))
    plans.add(DataPlan("mtn_corp_2gb", Network.MTN, DataType.CORPORATE, "2.0GB", 570.0, 700.0, "30 Days"))
    plans.add(DataPlan("mtn_corp_5gb", Network.MTN, DataType.CORPORATE, "5.0GB", 1425.0, 1750.0, "30 Days"))
    plans.add(DataPlan("mtn_corp_10gb", Network.MTN, DataType.CORPORATE, "10.0GB", 2850.0, 3500.0, "30 Days"))
    plans.add(DataPlan("mtn_corp_20gb", Network.MTN, DataType.CORPORATE, "20.0GB", 5650.0, 6800.0, "30 Days"))

    // MTN Gifting
    plans.add(DataPlan("mtn_gift_1gb", Network.MTN, DataType.GIFTING, "1.0GB", 290.0, 360.0, "30 Days"))
    plans.add(DataPlan("mtn_gift_2gb", Network.MTN, DataType.GIFTING, "2.0GB", 580.0, 720.0, "30 Days"))
    plans.add(DataPlan("mtn_gift_5gb", Network.MTN, DataType.GIFTING, "5.0GB", 1450.0, 1800.0, "30 Days"))

    // AIRTEL Corporate
    plans.add(DataPlan("airtel_corp_500mb", Network.AIRTEL, DataType.CORPORATE, "500MB", 145.0, 200.0, "30 Days"))
    plans.add(DataPlan("airtel_corp_1gb", Network.AIRTEL, DataType.CORPORATE, "1.0GB", 280.0, 350.0, "30 Days"))
    plans.add(DataPlan("airtel_corp_2gb", Network.AIRTEL, DataType.CORPORATE, "2.0GB", 560.0, 700.0, "30 Days"))
    plans.add(DataPlan("airtel_corp_5gb", Network.AIRTEL, DataType.CORPORATE, "5.0GB", 1400.0, 1750.0, "30 Days"))
    plans.add(DataPlan("airtel_corp_10gb", Network.AIRTEL, DataType.CORPORATE, "10.0GB", 2800.0, 3500.0, "30 Days"))
    plans.add(DataPlan("airtel_corp_20gb", Network.AIRTEL, DataType.CORPORATE, "20.0GB", 5600.0, 6800.0, "30 Days"))

    // AIRTEL Gifting
    plans.add(DataPlan("airtel_gift_1gb", Network.AIRTEL, DataType.GIFTING, "1.0GB", 295.0, 360.0, "30 Days"))
    plans.add(DataPlan("airtel_gift_2gb", Network.AIRTEL, DataType.GIFTING, "2.0GB", 590.0, 720.0, "30 Days"))
    plans.add(DataPlan("airtel_gift_5gb", Network.AIRTEL, DataType.GIFTING, "5.0GB", 1475.0, 1800.0, "30 Days"))

    // AIRTEL SME
    plans.add(DataPlan("airtel_sme_1gb", Network.AIRTEL, DataType.SME, "1.0GB", 285.0, 350.0, "30 Days"))
    plans.add(DataPlan("airtel_sme_2gb", Network.AIRTEL, DataType.SME, "2.0GB", 570.0, 700.0, "30 Days"))
    plans.add(DataPlan("airtel_sme_5gb", Network.AIRTEL, DataType.SME, "5.0GB", 1425.0, 1750.0, "30 Days"))

    // GLO Corporate
    plans.add(DataPlan("glo_corp_500mb", Network.GLO, DataType.CORPORATE, "500MB", 130.0, 180.0, "30 Days"))
    plans.add(DataPlan("glo_corp_1gb", Network.GLO, DataType.CORPORATE, "1.0GB", 245.0, 300.0, "30 Days"))
    plans.add(DataPlan("glo_corp_2gb", Network.GLO, DataType.CORPORATE, "2.0GB", 490.0, 600.0, "30 Days"))
    plans.add(DataPlan("glo_corp_3gb", Network.GLO, DataType.CORPORATE, "3.0GB", 735.0, 900.0, "30 Days"))
    plans.add(DataPlan("glo_corp_5gb", Network.GLO, DataType.CORPORATE, "5.0GB", 1225.0, 1500.0, "30 Days"))
    plans.add(DataPlan("glo_corp_10gb", Network.GLO, DataType.CORPORATE, "10.0GB", 2450.0, 3000.0, "30 Days"))

    // GLO Gifting
    plans.add(DataPlan("glo_gift_1gb", Network.GLO, DataType.GIFTING, "1.0GB", 260.0, 320.0, "30 Days"))
    plans.add(DataPlan("glo_gift_2gb", Network.GLO, DataType.GIFTING, "2.0GB", 520.0, 650.0, "30 Days"))
    plans.add(DataPlan("glo_gift_5gb", Network.GLO, DataType.GIFTING, "5.0GB", 1300.0, 1600.0, "30 Days"))

    // GLO SME
    plans.add(DataPlan("glo_sme_1gb", Network.GLO, DataType.SME, "1.0GB", 250.0, 310.0, "30 Days"))
    plans.add(DataPlan("glo_sme_2gb", Network.GLO, DataType.SME, "2.0GB", 500.0, 620.0, "30 Days"))

    // 9MOBILE Corporate
    plans.add(DataPlan("9mob_corp_500mb", Network.NINEMOBILE, DataType.CORPORATE, "500MB", 110.0, 160.0, "30 Days"))
    plans.add(DataPlan("9mob_corp_1gb", Network.NINEMOBILE, DataType.CORPORATE, "1.0GB", 195.0, 250.0, "30 Days"))
    plans.add(DataPlan("9mob_corp_2gb", Network.NINEMOBILE, DataType.CORPORATE, "2.0GB", 390.0, 500.0, "30 Days"))
    plans.add(DataPlan("9mob_corp_3gb", Network.NINEMOBILE, DataType.CORPORATE, "3.0GB", 585.0, 750.0, "30 Days"))
    plans.add(DataPlan("9mob_corp_5gb", Network.NINEMOBILE, DataType.CORPORATE, "5.0GB", 975.0, 1250.0, "30 Days"))
    plans.add(DataPlan("9mob_corp_10gb", Network.NINEMOBILE, DataType.CORPORATE, "10.0GB", 1950.0, 2500.0, "30 Days"))

    // 9MOBILE Gifting
    plans.add(DataPlan("9mob_gift_1gb", Network.NINEMOBILE, DataType.GIFTING, "1.0GB", 215.0, 280.0, "30 Days"))
    plans.add(DataPlan("9mob_gift_2gb", Network.NINEMOBILE, DataType.GIFTING, "2.0GB", 430.0, 560.0, "30 Days"))

    return plans
  }
}
