package com.example.telephony

import android.content.Context
import android.media.AudioManager
import android.util.Log

class AudioRouteManager(private val context: Context) {
  private val audioManager = context.getSystemService(Context.AUDIO_SERVICE) as? AudioManager

  fun setCallMode(inCall: Boolean) {
    try {
      if (inCall) {
        audioManager?.mode = AudioManager.MODE_IN_COMMUNICATION
      } else {
        audioManager?.mode = AudioManager.MODE_NORMAL
        audioManager?.isSpeakerphoneOn = false
        audioManager?.isMicrophoneMute = false
      }
    } catch (e: Exception) {
      Log.e("AudioRouteManager", "Error updating audio mode: ${e.message}")
    }
  }

  fun toggleSpeaker(enable: Boolean): Boolean {
    return try {
      audioManager?.isSpeakerphoneOn = enable
      audioManager?.isSpeakerphoneOn ?: enable
    } catch (e: Exception) {
      Log.e("AudioRouteManager", "Error toggling speaker: ${e.message}")
      enable
    }
  }

  fun toggleMute(mute: Boolean): Boolean {
    return try {
      audioManager?.isMicrophoneMute = mute
      audioManager?.isMicrophoneMute ?: mute
    } catch (e: Exception) {
      Log.e("AudioRouteManager", "Error toggling mute: ${e.message}")
      mute
    }
  }
}
