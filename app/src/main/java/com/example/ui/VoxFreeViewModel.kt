package com.example.ui

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.model.CallRecord
import com.example.data.model.CallStatus
import com.example.data.model.ContactRecord
import com.example.data.model.UserProfile
import com.example.data.repository.NetworkSettingsRepository
import com.example.data.repository.VoxFreeRepository
import com.example.telephony.ActiveCallState
import com.example.telephony.CallPhase
import com.example.telephony.ToneManager
import com.example.telephony.VoipTelemetry
import com.example.telephony.webrtc.SignalingState
import com.example.telephony.webrtc.WebRtcCallEventListener
import com.example.telephony.webrtc.WebRtcIceState
import com.example.telephony.webrtc.WebRtcManager
import com.example.telephony.webrtc.WebRtcSignalingClient
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

enum class AppTab {
  DIALER,
  CONTACTS,
  RECENTS,
  NETWORK
}

data class IncomingCallData(
  val senderId: String,
  val callerName: String,
  val sdp: String
)

class VoxFreeViewModel(application: Application) : AndroidViewModel(application), WebRtcCallEventListener {
  private val repository = VoxFreeRepository(application)
  val networkSettingsRepository = NetworkSettingsRepository(application)
  private val toneManager = ToneManager()

  // Real WebRTC & Signaling Client with persisted URL on cold start
  val signalingClient = WebRtcSignalingClient(
    serverUrl = networkSettingsRepository.getSignalingUrlSync()
  )
  val webRtcManager = WebRtcManager(application, signalingClient)

  private val _selectedTab = MutableStateFlow(AppTab.DIALER)
  val selectedTab: StateFlow<AppTab> = _selectedTab.asStateFlow()

  val userProfile: StateFlow<UserProfile> = repository.userProfile
  val savedSignalingUrl: StateFlow<String> = networkSettingsRepository.signalingUrl
  val audioRoutingMode: StateFlow<String> = networkSettingsRepository.audioRoutingMode

  private val _dialerInput = MutableStateFlow("")
  val dialerInput: StateFlow<String> = _dialerInput.asStateFlow()

  val contacts: StateFlow<List<ContactRecord>> = repository.allContacts.stateIn(
    scope = viewModelScope,
    started = SharingStarted.WhileSubscribed(5000),
    initialValue = emptyList()
  )

  val calls: StateFlow<List<CallRecord>> = repository.allCalls.stateIn(
    scope = viewModelScope,
    started = SharingStarted.WhileSubscribed(5000),
    initialValue = emptyList()
  )

  private val _activeCallState = MutableStateFlow(ActiveCallState())
  val activeCallState: StateFlow<ActiveCallState> = _activeCallState.asStateFlow()

  private val _incomingCall = MutableStateFlow<IncomingCallData?>(null)
  val incomingCall: StateFlow<IncomingCallData?> = _incomingCall.asStateFlow()

  val signalingState: StateFlow<SignalingState> = signalingClient.state
  val iceState: StateFlow<WebRtcIceState> = webRtcManager.iceState

  private var durationJob: Job? = null

  init {
    webRtcManager.setEventListener(this)
    val profile = userProfile.value
    signalingClient.connect(profile.callId, profile.username)
  }

  fun selectTab(tab: AppTab) {
    _selectedTab.value = tab
  }

  fun onDigitPress(digit: Char) {
    toneManager.playDtmf(digit)
    if (_dialerInput.value.length < 5) {
      _dialerInput.value += digit
    }
  }

  fun onDigitLongPress(digit: Char) {
    toneManager.playDtmf(digit)
    if (_dialerInput.value.length < 5) {
      _dialerInput.value += digit
    }
  }

  fun onBackspace() {
    if (_dialerInput.value.isNotEmpty()) {
      _dialerInput.value = _dialerInput.value.dropLast(1)
    }
  }

  fun onTextInput(text: String) {
    _dialerInput.value = text.trim()
  }

  fun selectPeerToCall(peerId: String) {
    _dialerInput.value = peerId
    _selectedTab.value = AppTab.DIALER
  }

  fun initiateCall() {
    val target = _dialerInput.value.trim()
    if (target.isEmpty()) return

    val matchedContact = contacts.value.find { it.callId == target || it.username.equals(target, ignoreCase = true) }
    val peerName = matchedContact?.displayName ?: if (target.startsWith("@")) target else "Caller #$target"
    val peerIdentifier = matchedContact?.callId ?: target

    toneManager.playRinging()

    _activeCallState.value = ActiveCallState(
      peerIdentifier = peerIdentifier,
      peerName = peerName,
      phase = CallPhase.SIGNALING_OFFER,
      signalingLog = listOf(
        "WebSocket Connected: ${signalingClient.getServerUrl()}",
        "Audio Mode: ${audioRoutingMode.value}",
        "Configuring Google STUN: stun:stun.l.google.com:19302"
      )
    )

    // Launch WebRTC call pipeline with preferred audio routing mode
    webRtcManager.startOutgoingCall(peerIdentifier, audioRoutingMode.value)
  }

  fun acceptIncomingCall() {
    val call = _incomingCall.value ?: return
    toneManager.stopTone()
    _incomingCall.value = null

    _activeCallState.value = ActiveCallState(
      peerIdentifier = call.senderId,
      peerName = call.callerName,
      phase = CallPhase.STUN_CANDIDATES,
      signalingLog = listOf(
        "Accepted incoming call from ${call.callerName}",
        "Generated SDP Answer (OfferToReceiveAudio = true)",
        "Exchanging Google STUN candidates..."
      )
    )

    webRtcManager.acceptIncomingCall(call.senderId, call.sdp, audioRoutingMode.value)
  }

  fun declineIncomingCall() {
    val call = _incomingCall.value ?: return
    toneManager.stopTone()
    toneManager.playDisconnect()
    signalingClient.sendBye(call.senderId)
    _incomingCall.value = null
  }

  override fun onIncomingCallReceived(senderId: String, sdp: String) {
    viewModelScope.launch {
      val matchedContact = contacts.value.find { it.callId == senderId || it.username.equals(senderId, ignoreCase = true) }
      val callerName = matchedContact?.displayName ?: "Peer #$senderId"
      toneManager.playRinging()
      _incomingCall.value = IncomingCallData(senderId = senderId, callerName = callerName, sdp = sdp)
    }
  }

  override fun onIceConnectionStateChanged(state: WebRtcIceState) {
    viewModelScope.launch {
      val phase = when (state) {
        WebRtcIceState.CHECKING -> CallPhase.STUN_CANDIDATES
        WebRtcIceState.CONNECTED, WebRtcIceState.COMPLETED -> CallPhase.CONNECTED_P2P
        WebRtcIceState.FAILED, WebRtcIceState.DISCONNECTED, WebRtcIceState.CLOSED -> CallPhase.ENDED
        else -> _activeCallState.value.phase
      }
      _activeCallState.value = _activeCallState.value.copy(phase = phase)
    }
  }

  override fun onRemoteAudioTrackReceived(trackId: String) {
    viewModelScope.launch {
      _activeCallState.value = _activeCallState.value.copy(
        signalingLog = _activeCallState.value.signalingLog + "Remote AudioTrack received ($trackId) -> Rendering active"
      )
    }
  }

  override fun onCallConnected() {
    viewModelScope.launch {
      toneManager.stopTone()
      _activeCallState.value = _activeCallState.value.copy(phase = CallPhase.CONNECTED_P2P)
      startDurationTicker()
    }
  }

  override fun onCallDisconnected(reason: String) {
    viewModelScope.launch {
      durationJob?.cancel()
      toneManager.stopTone()
      toneManager.playDisconnect()
      _incomingCall.value = null

      val current = _activeCallState.value
      if (current.phase != CallPhase.IDLE && current.phase != CallPhase.ENDED) {
        val record = CallRecord(
          peerCallId = current.peerIdentifier,
          peerUsername = current.peerName,
          durationSeconds = current.durationSeconds,
          status = if (current.durationSeconds > 0) CallStatus.COMPLETED else CallStatus.CANCELLED,
          avgMosScore = current.telemetry.mosScore,
          avgLatencyMs = current.telemetry.latencyMs
        )
        repository.logCall(record)
      }

      _activeCallState.value = _activeCallState.value.copy(
        phase = CallPhase.ENDED,
        endReason = reason
      )

      delay(1200)
      _activeCallState.value = ActiveCallState()
    }
  }

  override fun onTelemetryUpdated(telemetry: VoipTelemetry) {
    viewModelScope.launch {
      _activeCallState.value = _activeCallState.value.copy(telemetry = telemetry)
    }
  }

  override fun onAudioLevelsUpdated(amplitudes: List<Float>) {
    viewModelScope.launch {
      _activeCallState.value = _activeCallState.value.copy(waveAmplitudes = amplitudes)
    }
  }

  override fun onLog(message: String) {
    viewModelScope.launch {
      val logs = _activeCallState.value.signalingLog + message
      _activeCallState.value = _activeCallState.value.copy(signalingLog = logs.takeLast(10))
    }
  }

  private fun startDurationTicker() {
    durationJob?.cancel()
    durationJob = viewModelScope.launch {
      var seconds = 0
      while (_activeCallState.value.phase == CallPhase.CONNECTED_P2P) {
        delay(1000)
        seconds++
        _activeCallState.value = _activeCallState.value.copy(durationSeconds = seconds)
      }
    }
  }

  fun toggleMute() {
    val isMuted = webRtcManager.toggleMute()
    _activeCallState.value = _activeCallState.value.copy(isMuted = isMuted)
  }

  fun toggleSpeaker() {
    val isSpeakerOn = webRtcManager.toggleSpeaker()
    _activeCallState.value = _activeCallState.value.copy(isSpeakerOn = isSpeakerOn)
  }

  fun endCall(reason: String = "Normal Call End") {
    webRtcManager.endCall(reason)
  }

  fun saveSignalingServerUrl(url: String) {
    networkSettingsRepository.saveSignalingUrl(url)
    signalingClient.updateServerUrl(url.trim())
  }

  fun saveAudioRoutingMode(mode: String) {
    networkSettingsRepository.saveAudioRoutingMode(mode)
  }

  fun addContact(name: String, username: String, callId: String) {
    viewModelScope.launch {
      val contact = ContactRecord(
        displayName = name,
        username = username,
        callId = callId,
        avatarColorHex = listOf(0xFF10B981, 0xFF06B6D4, 0xFF8B5CF6, 0xFFF59E0B, 0xFFEC4899).random(),
        isOnline = true
      )
      repository.addContact(contact)
    }
  }

  fun deleteContact(contactId: Long) {
    viewModelScope.launch {
      repository.deleteContact(contactId)
    }
  }

  fun deleteCallRecord(callId: Long) {
    viewModelScope.launch {
      repository.deleteCall(callId)
    }
  }

  fun clearAllCalls() {
    viewModelScope.launch {
      repository.clearCallHistory()
    }
  }

  fun updateUserProfile(username: String, displayName: String) {
    repository.updateUserProfile(username, displayName)
    signalingClient.connect(userProfile.value.callId, username)
  }

  override fun onCleared() {
    super.onCleared()
    toneManager.release()
    webRtcManager.endCall("ViewModel destroyed")
    signalingClient.disconnect()
  }
}
