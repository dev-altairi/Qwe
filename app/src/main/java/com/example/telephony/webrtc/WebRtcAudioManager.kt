package com.example.telephony.webrtc

import android.content.Context
import android.media.AudioAttributes
import android.media.AudioFocusRequest
import android.media.AudioManager
import android.os.Build
import android.util.Log

class WebRtcAudioManager(private val context: Context) {
  private val audioManager = context.getSystemService(Context.AUDIO_SERVICE) as? AudioManager
  private var audioFocusRequest: AudioFocusRequest? = null
  private var savedAudioMode: Int = AudioManager.MODE_NORMAL
  private var savedIsSpeakerPhoneOn: Boolean = false
  private var savedIsMicrophoneMute: Boolean = false
  private var activeAudioMode: Int = AudioManager.MODE_IN_COMMUNICATION

  fun startAudioMode(useSpeaker: Boolean = false, preferredMode: String = "Auto (Adaptive)") {
    val am = audioManager ?: return
    try {
      // 1. Save current system states safely
      savedAudioMode = am.mode
      savedIsSpeakerPhoneOn = am.isSpeakerphoneOn
      savedIsMicrophoneMute = am.isMicrophoneMute

      // 2. Request voice communication audio focus
      requestCallAudioFocus(am)

      // 3. Set hardware voice communication mode with OEM fallback ladder
      when (preferredMode) {
        "In-Call Mode" -> {
          setAudioModeWithFallback(am, AudioManager.MODE_IN_CALL)
        }
        "Communication Mode" -> {
          setAudioModeWithFallback(am, AudioManager.MODE_IN_COMMUNICATION)
        }
        else -> {
          // Auto (Adaptive): Try MODE_IN_COMMUNICATION first, fallback to MODE_IN_CALL on OEM failure
          setAudioModeWithFallback(am, AudioManager.MODE_IN_COMMUNICATION)
        }
      }

      // 4. Unmute microphone and configure speakerphone
      try {
        am.isMicrophoneMute = false
      } catch (e: Exception) {
        Log.w(TAG, "Could not explicitly clear isMicrophoneMute: ${e.message}")
      }

      setSpeakerphone(useSpeaker)

      Log.d(TAG, "WebRtcAudioManager started with mode: $activeAudioMode (Speaker: $useSpeaker)")
    } catch (e: Exception) {
      Log.e(TAG, "Error initializing WebRtcAudioManager: ${e.message}", e)
    }
  }

  /**
   * Resilient audio mode setter with OEM fallback ladder (Xiaomi / Huawei / Samsung / Android 8.0 - 15+).
   */
  private fun setAudioModeWithFallback(am: AudioManager, primaryMode: Int) {
    try {
      am.mode = primaryMode
      activeAudioMode = am.mode
      if (activeAudioMode != primaryMode) {
        Log.w(TAG, "Mode $primaryMode not accepted by OEM audio driver. Trying MODE_IN_CALL fallback.")
        am.mode = AudioManager.MODE_IN_CALL
        activeAudioMode = am.mode
      }
    } catch (e: Exception) {
      Log.w(TAG, "Primary mode $primaryMode failed: ${e.message}. Applying fallback to MODE_IN_CALL.")
      try {
        am.mode = AudioManager.MODE_IN_CALL
        activeAudioMode = am.mode
      } catch (ex: Exception) {
        Log.e(TAG, "Fallback to MODE_IN_CALL also failed: ${ex.message}. Retaining current mode.")
        activeAudioMode = am.mode
      }
    }
  }

  fun setSpeakerphone(enable: Boolean) {
    val am = audioManager ?: return
    try {
      am.isSpeakerphoneOn = enable
      Log.d(TAG, "Speakerphone set to: $enable (Verified: ${am.isSpeakerphoneOn})")
    } catch (e: Exception) {
      Log.e(TAG, "Error setting speakerphone: ${e.message}", e)
    }
  }

  fun isSpeakerphoneOn(): Boolean {
    return audioManager?.isSpeakerphoneOn ?: false
  }

  fun setMicrophoneMute(mute: Boolean) {
    val am = audioManager ?: return
    try {
      am.isMicrophoneMute = mute
      Log.d(TAG, "Microphone mute set to: $mute")
    } catch (e: Exception) {
      Log.e(TAG, "Error setting microphone mute: ${e.message}", e)
    }
  }

  fun isMicrophoneMute(): Boolean {
    return audioManager?.isMicrophoneMute ?: false
  }

  fun stopAudioMode() {
    val am = audioManager ?: return
    try {
      am.isSpeakerphoneOn = savedIsSpeakerPhoneOn
      am.isMicrophoneMute = savedIsMicrophoneMute

      try {
        am.mode = savedAudioMode
      } catch (e: Exception) {
        am.mode = AudioManager.MODE_NORMAL
      }

      abandonCallAudioFocus(am)
      Log.d(TAG, "WebRtcAudioManager stopped and restored to normal mode")
    } catch (e: Exception) {
      Log.e(TAG, "Error stopping WebRtcAudioManager: ${e.message}", e)
    }
  }

  private fun requestCallAudioFocus(am: AudioManager) {
    try {
      if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
        val playbackAttributes = AudioAttributes.Builder()
          .setUsage(AudioAttributes.USAGE_VOICE_COMMUNICATION)
          .setContentType(AudioAttributes.CONTENT_TYPE_SPEECH)
          .build()

        val request = AudioFocusRequest.Builder(AudioManager.AUDIOFOCUS_GAIN_TRANSIENT)
          .setAudioAttributes(playbackAttributes)
          .setAcceptsDelayedFocusGain(true)
          .setOnAudioFocusChangeListener { focusChange ->
            Log.d(TAG, "Audio focus changed: $focusChange")
          }
          .build()

        audioFocusRequest = request
        am.requestAudioFocus(request)
      } else {
        @Suppress("DEPRECATION")
        am.requestAudioFocus(
          null,
          AudioManager.STREAM_VOICE_CALL,
          AudioManager.AUDIOFOCUS_GAIN_TRANSIENT
        )
      }
    } catch (e: Exception) {
      Log.w(TAG, "Failed to request audio focus: ${e.message}")
    }
  }

  private fun abandonCallAudioFocus(am: AudioManager) {
    try {
      if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
        audioFocusRequest?.let { am.abandonAudioFocusRequest(it) }
        audioFocusRequest = null
      } else {
        @Suppress("DEPRECATION")
        am.abandonAudioFocus(null)
      }
    } catch (e: Exception) {
      Log.w(TAG, "Failed to abandon audio focus: ${e.message}")
    }
  }

  fun getActiveAudioModeName(): String {
    return when (activeAudioMode) {
      AudioManager.MODE_IN_COMMUNICATION -> "MODE_IN_COMMUNICATION"
      AudioManager.MODE_IN_CALL -> "MODE_IN_CALL (OEM Fallback)"
      AudioManager.MODE_RINGTONE -> "MODE_RINGTONE"
      else -> "MODE_NORMAL"
    }
  }

  companion object {
    private const val TAG = "WebRtcAudioManager"
  }
}
