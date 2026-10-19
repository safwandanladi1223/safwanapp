package com.example

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import com.example.data.model.DataType
import com.example.data.model.Network
import com.example.data.repository.VtuRepository
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [36])
class ExampleRobolectricTest {

  @Test
  fun `read string from context`() {
    val context = ApplicationProvider.getApplicationContext<Context>()
    val appName = context.getString(R.string.app_name)
    assertEquals("SwiftVTU", appName)
  }

  @Test
  fun `vtu repository buys data and deducts wallet balance`() = runBlocking {
    val repository = VtuRepository()
    val initialBalance = repository.wallet.value.balance
    val plan = repository.getPlans(Network.MTN, DataType.SME).first { it.size == "1.0GB" }

    val result = repository.buyData(Network.MTN, plan, "08031234567")
    assertTrue(result.isSuccess)

    val finalBalance = repository.wallet.value.balance
    assertEquals(initialBalance - plan.price, finalBalance, 0.01)
  }

  @Test
  fun `vtu repository buys airtime with cashback discount`() = runBlocking {
    val repository = VtuRepository()
    val initialBalance = repository.wallet.value.balance
    val airtimeAmount = 1000.0
    val discount = airtimeAmount * (Network.MTN.airtimeDiscountPercent / 100.0) // 25.0
    val payable = airtimeAmount - discount // 975.0

    val result = repository.buyAirtime(Network.MTN, airtimeAmount, "08031234567")
    assertTrue(result.isSuccess)

    val finalBalance = repository.wallet.value.balance
    assertEquals(initialBalance - payable, finalBalance, 0.01)
  }

  @Test
  fun `vtu repository funds wallet correctly`() = runBlocking {
    val repository = VtuRepository()
    val initialBalance = repository.wallet.value.balance
    val fundAmount = 5000.0

    val txn = repository.fundWallet(fundAmount, "Paystack Gateway")
    assertEquals(initialBalance + fundAmount, repository.wallet.value.balance, 0.01)
    assertEquals(fundAmount, txn.amount, 0.01)
  }
}
