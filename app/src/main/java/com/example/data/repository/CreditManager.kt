package com.example.data.repository

import android.content.Context
import android.content.SharedPreferences
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class CreditManager(context: Context) {
  private val prefs: SharedPreferences =
    context.getSharedPreferences("voxfree_credits_prefs", Context.MODE_PRIVATE)

  private val _balance = MutableStateFlow(loadBalance())
  val balance: StateFlow<Int> = _balance.asStateFlow()

  private val _dailyBonusClaimed = MutableStateFlow(isDailyBonusClaimedToday())
  val dailyBonusClaimed: StateFlow<Boolean> = _dailyBonusClaimed.asStateFlow()

  private val _totalBoostersClaimed = MutableStateFlow(prefs.getInt(KEY_BOOSTERS_CLAIMED, 0))
  val totalBoostersClaimed: StateFlow<Int> = _totalBoostersClaimed.asStateFlow()

  private fun loadBalance(): Int {
    if (!prefs.contains(KEY_BALANCE)) {
      // First install: welcome bonus of 120 free credits
      prefs.edit().putInt(KEY_BALANCE, 120).apply()
    }
    return prefs.getInt(KEY_BALANCE, 120)
  }

  private fun isDailyBonusClaimedToday(): Boolean {
    val lastEpoch = prefs.getLong(KEY_LAST_DAILY_CLAIM, 0L)
    val now = System.currentTimeMillis()
    // 24 hours window or calendar day check
    val diff = now - lastEpoch
    return diff < 24 * 60 * 60 * 1000L
  }

  fun claimDailyBonus(): Boolean {
    if (isDailyBonusClaimedToday()) return false
    val newBal = _balance.value + 100
    prefs.edit()
      .putInt(KEY_BALANCE, newBal)
      .putLong(KEY_LAST_DAILY_CLAIM, System.currentTimeMillis())
      .apply()
    _balance.value = newBal
    _dailyBonusClaimed.value = true
    return true
  }

  fun claimRewardedBoost(): Int {
    val boostAmount = 30
    val newBal = _balance.value + boostAmount
    val boosters = _totalBoostersClaimed.value + 1
    prefs.edit()
      .putInt(KEY_BALANCE, newBal)
      .putInt(KEY_BOOSTERS_CLAIMED, boosters)
      .apply()
    _balance.value = newBal
    _totalBoostersClaimed.value = boosters
    return boostAmount
  }

  fun deductCredits(amount: Int): Boolean {
    if (_balance.value < amount) return false
    val newBal = (_balance.value - amount).coerceAtLeast(0)
    prefs.edit().putInt(KEY_BALANCE, newBal).apply()
    _balance.value = newBal
    return true
  }

  companion object {
    private const val KEY_BALANCE = "credits_balance"
    private const val KEY_LAST_DAILY_CLAIM = "last_daily_claim_time"
    private const val KEY_BOOSTERS_CLAIMED = "boosters_claimed"
  }
}
