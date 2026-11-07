package com.example.data.repository

import android.content.Context
import android.content.SharedPreferences
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class NetworkSettingsRepository(context: Context) {
  private val prefs: SharedPreferences =
    context.getSharedPreferences("voxfree_network_settings", Context.MODE_PRIVATE)

  private val _signalingUrl = MutableStateFlow(
    prefs.getString(KEY_SIGNALING_URL, DEFAULT_SIGNALING_URL) ?: DEFAULT_SIGNALING_URL
  )
  val signalingUrl: StateFlow<String> = _signalingUrl.asStateFlow()

  private val _audioRoutingMode = MutableStateFlow(
    prefs.getString(KEY_AUDIO_MODE, DEFAULT_AUDIO_MODE) ?: DEFAULT_AUDIO_MODE
  )
  val audioRoutingMode: StateFlow<String> = _audioRoutingMode.asStateFlow()

  private val _stunServerUrl = MutableStateFlow(
    prefs.getString(KEY_STUN_SERVER, DEFAULT_STUN_SERVER) ?: DEFAULT_STUN_SERVER
  )
  val stunServerUrl: StateFlow<String> = _stunServerUrl.asStateFlow()

  fun saveSignalingUrl(url: String) {
    val cleanUrl = url.trim()
    prefs.edit().putString(KEY_SIGNALING_URL, cleanUrl).apply()
    _signalingUrl.value = cleanUrl
  }

  fun saveAudioRoutingMode(mode: String) {
    prefs.edit().putString(KEY_AUDIO_MODE, mode).apply()
    _audioRoutingMode.value = mode
  }

  fun saveStunServerUrl(stun: String) {
    val cleanStun = stun.trim()
    prefs.edit().putString(KEY_STUN_SERVER, cleanStun).apply()
    _stunServerUrl.value = cleanStun
  }

  fun getSignalingUrlSync(): String {
    return prefs.getString(KEY_SIGNALING_URL, DEFAULT_SIGNALING_URL) ?: DEFAULT_SIGNALING_URL
  }

  companion object {
    const val DEFAULT_SIGNALING_URL = "wss://signaling.voxfree.org/webrtc"
    const val LAN_PRESET_URL = "ws://192.168.1.100:8080/ws"
    const val DEFAULT_AUDIO_MODE = "Auto (Adaptive)"
    const val DEFAULT_STUN_SERVER = "stun:stun.l.google.com:19302"

    private const val KEY_SIGNALING_URL = "webrtc_signaling_url"
    private const val KEY_AUDIO_MODE = "webrtc_audio_mode"
    private const val KEY_STUN_SERVER = "webrtc_stun_server"
  }
}
