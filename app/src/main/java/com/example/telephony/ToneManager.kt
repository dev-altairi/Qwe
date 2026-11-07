package com.example.telephony

import android.media.AudioManager
import android.media.ToneGenerator
import android.util.Log

class ToneManager {
  private var toneGenerator: ToneGenerator? = null

  init {
    try {
      toneGenerator = ToneGenerator(AudioManager.STREAM_VOICE_CALL, 80)
    } catch (e: Exception) {
      Log.e("ToneManager", "Error initializing ToneGenerator: ${e.message}")
    }
  }

  fun playDtmf(char: Char) {
    val tone = when (char) {
      '0' -> ToneGenerator.TONE_DTMF_0
      '1' -> ToneGenerator.TONE_DTMF_1
      '2' -> ToneGenerator.TONE_DTMF_2
      '3' -> ToneGenerator.TONE_DTMF_3
      '4' -> ToneGenerator.TONE_DTMF_4
      '5' -> ToneGenerator.TONE_DTMF_5
      '6' -> ToneGenerator.TONE_DTMF_6
      '7' -> ToneGenerator.TONE_DTMF_7
      '8' -> ToneGenerator.TONE_DTMF_8
      '9' -> ToneGenerator.TONE_DTMF_9
      '*' -> ToneGenerator.TONE_DTMF_S
      '#' -> ToneGenerator.TONE_DTMF_P
      else -> ToneGenerator.TONE_PROP_BEEP
    }
    try {
      toneGenerator?.startTone(tone, 120)
    } catch (e: Exception) {
      Log.e("ToneManager", "Error playing tone: ${e.message}")
    }
  }

  fun playRinging() {
    try {
      toneGenerator?.startTone(ToneGenerator.TONE_SUP_RINGTONE, 1000)
    } catch (e: Exception) {
      Log.e("ToneManager", "Error playing ringtone: ${e.message}")
    }
  }

  fun playDisconnect() {
    try {
      toneGenerator?.startTone(ToneGenerator.TONE_SUP_BUSY, 400)
    } catch (e: Exception) {
      Log.e("ToneManager", "Error playing disconnect tone: ${e.message}")
    }
  }

  fun stopTone() {
    try {
      toneGenerator?.stopTone()
    } catch (e: Exception) {
      Log.e("ToneManager", "Error stopping tone: ${e.message}")
    }
  }

  fun release() {
    try {
      toneGenerator?.release()
      toneGenerator = null
    } catch (e: Exception) {
      Log.e("ToneManager", "Error releasing ToneGenerator: ${e.message}")
    }
  }
}
