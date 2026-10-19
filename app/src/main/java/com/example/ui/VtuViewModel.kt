package com.example.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.model.Beneficiary
import com.example.data.model.DataPlan
import com.example.data.model.DataType
import com.example.data.model.Network
import com.example.data.model.Transaction
import com.example.data.model.TransactionType
import com.example.data.model.UserWallet
import com.example.data.repository.VtuRepository
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

enum class TransactionFilter(val label: String) {
  ALL("All"),
  DATA("Data"),
  AIRTIME("Airtime"),
  FUNDING("Funding")
}

data class PurchaseConfirmation(
  val title: String,
  val network: Network?,
  val planOrAmount: String,
  val recipient: String,
  val cost: Double,
  val discountOrProfit: Double,
  val onConfirm: () -> Unit
)

class VtuViewModel(
  private val repository: VtuRepository = VtuRepository()
) : ViewModel() {

  val wallet: StateFlow<UserWallet> = repository.wallet
  val beneficiaries: StateFlow<List<Beneficiary>> = repository.beneficiaries

  private val _isBalanceVisible = MutableStateFlow(true)
  val isBalanceVisible: StateFlow<Boolean> = _isBalanceVisible.asStateFlow()

  // Buy Data State
  private val _selectedDataNetwork = MutableStateFlow(Network.MTN)
  val selectedDataNetwork: StateFlow<Network> = _selectedDataNetwork.asStateFlow()

  private val _selectedDataType = MutableStateFlow(DataType.SME)
  val selectedDataType: StateFlow<DataType> = _selectedDataType.asStateFlow()

  private val _selectedPlan = MutableStateFlow<DataPlan?>(null)
  val selectedPlan: StateFlow<DataPlan?> = _selectedPlan.asStateFlow()

  private val _dataPhone = MutableStateFlow("")
  val dataPhone: StateFlow<String> = _dataPhone.asStateFlow()

  private val _bypassPorted = MutableStateFlow(false)
  val bypassPorted: StateFlow<Boolean> = _bypassPorted.asStateFlow()

  // Airtime State
  private val _selectedAirtimeNetwork = MutableStateFlow(Network.MTN)
  val selectedAirtimeNetwork: StateFlow<Network> = _selectedAirtimeNetwork.asStateFlow()

  private val _airtimePhone = MutableStateFlow("")
  val airtimePhone: StateFlow<String> = _airtimePhone.asStateFlow()

  private val _airtimeAmount = MutableStateFlow("1000")
  val airtimeAmount: StateFlow<String> = _airtimeAmount.asStateFlow()

  // Transaction Search & Filter State
  private val _searchQuery = MutableStateFlow("")
  val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

  private val _selectedFilter = MutableStateFlow(TransactionFilter.ALL)
  val selectedFilter: StateFlow<TransactionFilter> = _selectedFilter.asStateFlow()

  val filteredTransactions: StateFlow<List<Transaction>> =
    combine(repository.transactions, _searchQuery, _selectedFilter) { list, query, filter ->
      list.filter { txn ->
        val matchesQuery = query.isBlank() ||
          txn.recipient.contains(query, ignoreCase = true) ||
          txn.reference.contains(query, ignoreCase = true) ||
          txn.description.contains(query, ignoreCase = true)

        val matchesFilter = when (filter) {
          TransactionFilter.ALL -> true
          TransactionFilter.DATA -> txn.type == TransactionType.DATA
          TransactionFilter.AIRTIME -> txn.type == TransactionType.AIRTIME
          TransactionFilter.FUNDING -> txn.type == TransactionType.WALLET_FUNDING
        }

        matchesQuery && matchesFilter
      }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

  val recentTransactions: StateFlow<List<Transaction>> =
    repository.transactions

  // UI Flow & Dialog States
  private val _isProcessing = MutableStateFlow(false)
  val isProcessing: StateFlow<Boolean> = _isProcessing.asStateFlow()

  private val _confirmation = MutableStateFlow<PurchaseConfirmation?>(null)
  val confirmation: StateFlow<PurchaseConfirmation?> = _confirmation.asStateFlow()

  private val _latestReceipt = MutableStateFlow<Transaction?>(null)
  val latestReceipt: StateFlow<Transaction?> = _latestReceipt.asStateFlow()

  private val _showReceiptModal = MutableStateFlow(false)
  val showReceiptModal: StateFlow<Boolean> = _showReceiptModal.asStateFlow()

  private val _showFundModal = MutableStateFlow(false)
  val showFundModal: StateFlow<Boolean> = _showFundModal.asStateFlow()

  private val _snackbarMessage = MutableStateFlow<String?>(null)
  val snackbarMessage: StateFlow<String?> = _snackbarMessage.asStateFlow()

  init {
    // Select default plan
    val initialPlans = repository.getPlans(Network.MTN, DataType.SME)
    _selectedPlan.value = initialPlans.firstOrNull { it.size == "1.0GB" } ?: initialPlans.firstOrNull()
  }

  fun toggleBalanceVisibility() {
    _isBalanceVisible.value = !_isBalanceVisible.value
  }

  fun selectDataNetwork(network: Network) {
    _selectedDataNetwork.value = network
    refreshAvailablePlans()
  }

  fun selectDataType(type: DataType) {
    _selectedDataType.value = type
    refreshAvailablePlans()
  }

  private fun refreshAvailablePlans() {
    val plans = repository.getPlans(_selectedDataNetwork.value, _selectedDataType.value)
    if (_selectedPlan.value == null || !plans.contains(_selectedPlan.value)) {
      _selectedPlan.value = plans.firstOrNull()
    }
  }

  fun selectPlan(plan: DataPlan) {
    _selectedPlan.value = plan
  }

  fun updateDataPhone(phone: String) {
    val digitsOnly = phone.filter { it.isDigit() }.take(11)
    _dataPhone.value = digitsOnly

    if (!_bypassPorted.value && digitsOnly.length >= 4) {
      val detected = Network.detectFromPhone(digitsOnly)
      if (detected != null && detected != _selectedDataNetwork.value) {
        _selectedDataNetwork.value = detected
        refreshAvailablePlans()
      }
    }
  }

  fun setBypassPorted(bypass: Boolean) {
    _bypassPorted.value = bypass
  }

  fun selectAirtimeNetwork(network: Network) {
    _selectedAirtimeNetwork.value = network
  }

  fun updateAirtimePhone(phone: String) {
    val digitsOnly = phone.filter { it.isDigit() }.take(11)
    _airtimePhone.value = digitsOnly
    if (digitsOnly.length >= 4) {
      val detected = Network.detectFromPhone(digitsOnly)
      if (detected != null) {
        _selectedAirtimeNetwork.value = detected
      }
    }
  }

  fun updateAirtimeAmount(amount: String) {
    _airtimeAmount.value = amount.filter { it.isDigit() }.take(6)
  }

  fun updateSearchQuery(query: String) {
    _searchQuery.value = query
  }

  fun setFilter(filter: TransactionFilter) {
    _selectedFilter.value = filter
  }

  fun selectBeneficiaryForData(beneficiary: Beneficiary) {
    _dataPhone.value = beneficiary.phone
    _selectedDataNetwork.value = beneficiary.network
    refreshAvailablePlans()
  }

  fun selectBeneficiaryForAirtime(beneficiary: Beneficiary) {
    _airtimePhone.value = beneficiary.phone
    _selectedAirtimeNetwork.value = beneficiary.network
  }

  fun getPlansForCurrentSelection(): List<DataPlan> {
    return repository.getPlans(_selectedDataNetwork.value, _selectedDataType.value)
  }

  fun getAllPlans(): List<DataPlan> = repository.allPlans

  fun initiateBuyData() {
    val phone = _dataPhone.value
    val plan = _selectedPlan.value
    val network = _selectedDataNetwork.value

    if (phone.length != 11) {
      _snackbarMessage.value = "Please enter a valid 11-digit phone number"
      return
    }

    if (plan == null) {
      _snackbarMessage.value = "Please select a data bundle"
      return
    }

    if (wallet.value.balance < plan.price) {
      _snackbarMessage.value = "Insufficient wallet balance! Please fund your wallet."
      _showFundModal.value = true
      return
    }

    _confirmation.value = PurchaseConfirmation(
      title = "Confirm Data Bundle",
      network = network,
      planOrAmount = "${plan.size} ${plan.type.displayName}",
      recipient = phone,
      cost = plan.price,
      discountOrProfit = plan.profit,
      onConfirm = { executeBuyData(network, plan, phone) }
    )
  }

  private fun executeBuyData(network: Network, plan: DataPlan, phone: String) {
    _confirmation.value = null
    _isProcessing.value = true

    viewModelScope.launch {
      delay(1200) // Realistic network latency simulation
      val result = repository.buyData(network, plan, phone)
      _isProcessing.value = false

      result.onSuccess { txn ->
        _latestReceipt.value = txn
        _showReceiptModal.value = true
        _dataPhone.value = ""
      }.onFailure { err ->
        _snackbarMessage.value = err.message ?: "Transaction failed"
      }
    }
  }

  fun initiateBuyAirtime() {
    val phone = _airtimePhone.value
    val amountNum = _airtimeAmount.value.toDoubleOrNull() ?: 0.0
    val network = _selectedAirtimeNetwork.value

    if (phone.length != 11) {
      _snackbarMessage.value = "Please enter a valid 11-digit phone number"
      return
    }

    if (amountNum < 50.0) {
      _snackbarMessage.value = "Minimum airtime recharge is ₦50"
      return
    }

    val discount = amountNum * (network.airtimeDiscountPercent / 100.0)
    val payable = amountNum - discount

    if (wallet.value.balance < payable) {
      _snackbarMessage.value = "Insufficient wallet balance! Please fund your wallet."
      _showFundModal.value = true
      return
    }

    _confirmation.value = PurchaseConfirmation(
      title = "Confirm Airtime Top-Up",
      network = network,
      planOrAmount = "₦${amountNum.toInt()} Airtime",
      recipient = phone,
      cost = payable,
      discountOrProfit = discount,
      onConfirm = { executeBuyAirtime(network, amountNum, phone) }
    )
  }

  private fun executeBuyAirtime(network: Network, amount: Double, phone: String) {
    _confirmation.value = null
    _isProcessing.value = true

    viewModelScope.launch {
      delay(1000)
      val result = repository.buyAirtime(network, amount, phone)
      _isProcessing.value = false

      result.onSuccess { txn ->
        _latestReceipt.value = txn
        _showReceiptModal.value = true
        _airtimePhone.value = ""
      }.onFailure { err ->
        _snackbarMessage.value = err.message ?: "Airtime top-up failed"
      }
    }
  }

  fun fundWallet(amount: Double, channel: String = "Automated Bank Transfer") {
    _isProcessing.value = true
    viewModelScope.launch {
      delay(1000)
      val txn = repository.fundWallet(amount, channel)
      _isProcessing.value = false
      _showFundModal.value = false
      _latestReceipt.value = txn
      _showReceiptModal.value = true
      _snackbarMessage.value = "Wallet funded successfully with ₦${amount.toInt()}!"
    }
  }

  fun openReceipt(txn: Transaction) {
    _latestReceipt.value = txn
    _showReceiptModal.value = true
  }

  fun dismissReceipt() {
    _showReceiptModal.value = false
  }

  fun dismissConfirmation() {
    _confirmation.value = null
  }

  fun showFundModal() {
    _showFundModal.value = true
  }

  fun dismissFundModal() {
    _showFundModal.value = false
  }

  fun clearSnackbar() {
    _snackbarMessage.value = null
  }
}
