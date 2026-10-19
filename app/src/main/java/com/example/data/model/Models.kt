package com.example.data.model

import androidx.compose.ui.graphics.Color
import com.example.ui.theme.AirtelRed
import com.example.ui.theme.GloGreen
import com.example.ui.theme.MtnTextDark
import com.example.ui.theme.MtnYellow
import com.example.ui.theme.NineMobileGreen

enum class Network(
  val id: String,
  val displayName: String,
  val brandColor: Color,
  val textColor: Color,
  val airtimeDiscountPercent: Double,
  val serverStatus: String,
  val prefixes: List<String>
) {
  MTN(
    id = "mtn",
    displayName = "MTN",
    brandColor = MtnYellow,
    textColor = MtnTextDark,
    airtimeDiscountPercent = 2.5,
    serverStatus = "99.9% Online",
    prefixes = listOf("0803", "0806", "0703", "0706", "0813", "0816", "0810", "0814", "0903", "0906", "0913", "0916")
  ),
  AIRTEL(
    id = "airtel",
    displayName = "Airtel",
    brandColor = AirtelRed,
    textColor = Color.White,
    airtimeDiscountPercent = 2.0,
    serverStatus = "99.8% Online",
    prefixes = listOf("0802", "0808", "0708", "0812", "0701", "0902", "0901", "0904", "0907", "0912")
  ),
  GLO(
    id = "glo",
    displayName = "Glo",
    brandColor = GloGreen,
    textColor = Color.White,
    airtimeDiscountPercent = 3.5,
    serverStatus = "98.7% Online",
    prefixes = listOf("0805", "0807", "0705", "0815", "0811", "0905", "0915")
  ),
  NINEMOBILE(
    id = "9mobile",
    displayName = "9mobile",
    brandColor = NineMobileGreen,
    textColor = Color.White,
    airtimeDiscountPercent = 3.0,
    serverStatus = "98.2% Online",
    prefixes = listOf("0809", "0818", "0817", "0909", "0908")
  );

  companion object {
    fun detectFromPhone(phone: String): Network? {
      val clean = phone.filter { it.isDigit() }
      if (clean.length >= 4) {
        val prefix = clean.substring(0, 4)
        return entries.firstOrNull { it.prefixes.contains(prefix) }
      }
      return null
    }
  }
}

enum class DataType(val displayName: String, val badge: String) {
  SME(displayName = "SME Data", badge = "Instant"),
  GIFTING(displayName = "Gifting", badge = "Direct"),
  CORPORATE(displayName = "Corporate", badge = "Best Value")
}

data class DataPlan(
  val id: String,
  val network: Network,
  val type: DataType,
  val size: String,
  val price: Double,
  val retailPrice: Double,
  val validity: String
) {
  val profit: Double
    get() = (retailPrice - price).coerceAtLeast(0.0)
}

enum class TransactionType(val displayName: String) {
  DATA("Data Bundle"),
  AIRTIME("Airtime Top-up"),
  WALLET_FUNDING("Wallet Funding")
}

enum class TransactionStatus(val displayName: String) {
  SUCCESSFUL("Successful"),
  PENDING("Pending"),
  FAILED("Failed")
}

data class Transaction(
  val id: String,
  val reference: String,
  val type: TransactionType,
  val network: Network?,
  val recipient: String,
  val description: String,
  val amount: Double,
  val fee: Double = 0.0,
  val cashback: Double = 0.0,
  val balanceAfter: Double,
  val date: String,
  val time: String,
  val status: TransactionStatus,
  val channel: String
)

data class UserWallet(
  val balance: Double = 14850.0,
  val totalSpent: Double = 86420.0,
  val totalOrders: Int = 142,
  val totalCashback: Double = 2150.0,
  val resellerName: String = "Safwan Danladi",
  val resellerPhone: String = "08148901234",
  val resellerTier: String = "Gold Reseller",
  val virtualBank: String = "Moniepoint MFB",
  val virtualAccountNo: String = "8234901289",
  val virtualAccountName: String = "SWIFTVTU - SAFWAN"
)

data class Beneficiary(
  val id: String,
  val name: String,
  val phone: String,
  val network: Network
)
