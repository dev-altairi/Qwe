package com.example.telephony

enum class CallPhase {
  IDLE,
  SIGNALING_OFFER,
  STUN_CANDIDATES,
  RINGING_PEER,
  CONNECTED_P2P,
  ENDED
}

data class VoipTelemetry(
  val latencyMs: Int = 22,
  val jitterMs: Int = 3,
  val packetLossPercent: Float = 0.0f,
  val mosScore: Float = 4.55f,
  val audioBitrateKbps: Int = 48,
  val transport: String = "Direct UDP / SRTP",
  val stunServer: String = "stun.l.google.com:19302",
  val connectionMode: String = "Peer-to-Peer (Zero Relay)",
  val audioCodec: String = "Opus HD (48kHz Stereo)"
) {
  val qualityGrade: String
    get() = when {
      mosScore >= 4.4f -> "Studio HD (P2P)"
      mosScore >= 3.8f -> "Good"
      else -> "Fair"
    }
}

data class ActiveCallState(
  val peerIdentifier: String = "",
  val peerName: String = "",
  val phase: CallPhase = CallPhase.IDLE,
  val durationSeconds: Int = 0,
  val isMuted: Boolean = false,
  val isSpeakerOn: Boolean = false,
  val telemetry: VoipTelemetry = VoipTelemetry(),
  val waveAmplitudes: List<Float> = emptyList(),
  val signalingLog: List<String> = emptyList(),
  val endReason: String = ""
)
