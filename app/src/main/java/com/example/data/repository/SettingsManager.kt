package com.example.data.repository

import android.content.Context
import android.content.SharedPreferences
import com.example.data.model.GatewayConfig
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class SettingsManager(context: Context) {
  private val prefs: SharedPreferences =
    context.getSharedPreferences("voxfree_gateway_prefs", Context.MODE_PRIVATE)

  private val _config = MutableStateFlow(loadConfig())
  val config: StateFlow<GatewayConfig> = _config.asStateFlow()

  private fun loadConfig(): GatewayConfig {
    return GatewayConfig(
      accountSid = prefs.getString("account_sid", "") ?: "",
      authToken = prefs.getString("auth_token", "") ?: "",
      callerId = prefs.getString("caller_id", "+12025550143") ?: "+12025550143",
      twimlAppSid = prefs.getString("twiml_app_sid", "") ?: "",
      transportProtocol = prefs.getString("transport", "WebRTC (UDP/SRTP)") ?: "WebRTC (UDP/SRTP)",
      preferredCodec = prefs.getString("codec", "Opus (32 kbps)") ?: "Opus (32 kbps)"
    )
  }

  fun saveConfig(newConfig: GatewayConfig) {
    prefs.edit()
      .putString("account_sid", newConfig.accountSid)
      .putString("auth_token", newConfig.authToken)
      .putString("caller_id", newConfig.callerId)
      .putString("twiml_app_sid", newConfig.twimlAppSid)
      .putString("transport", newConfig.transportProtocol)
      .putString("codec", newConfig.preferredCodec)
      .apply()
    _config.value = newConfig
  }
}
