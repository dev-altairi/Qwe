package com.example.telephony.webrtc

import org.json.JSONObject

data class IceCandidatePayload(
  val sdpMid: String,
  val sdpMLineIndex: Int,
  val candidate: String
) {
  fun toJson(): JSONObject = JSONObject().apply {
    put("type", "candidate")
    put("sdpMid", sdpMid)
    put("sdpMLineIndex", sdpMLineIndex)
    put("candidate", candidate)
  }

  companion object {
    fun fromJson(json: JSONObject): IceCandidatePayload {
      return IceCandidatePayload(
        sdpMid = json.optString("sdpMid", "0"),
        sdpMLineIndex = json.optInt("sdpMLineIndex", 0),
        candidate = json.optString("candidate", "")
      )
    }
  }
}

data class SessionDescriptionPayload(
  val type: String, // "offer" or "answer"
  val sdp: String
) {
  fun toJson(): JSONObject = JSONObject().apply {
    put("type", type)
    put("sdp", sdp)
  }

  companion object {
    fun fromJson(json: JSONObject): SessionDescriptionPayload {
      return SessionDescriptionPayload(
        type = json.optString("type", "offer"),
        sdp = json.optString("sdp", "")
      )
    }
  }
}

enum class SignalingState {
  DISCONNECTED,
  CONNECTING,
  CONNECTED,
  REGISTERED,
  ERROR
}

enum class WebRtcIceState {
  NEW,
  CHECKING,
  CONNECTED,
  COMPLETED,
  FAILED,
  DISCONNECTED,
  CLOSED
}

interface WebRtcSignalingListener {
  fun onSignalingStateChanged(state: SignalingState)
  fun onOfferReceived(senderId: String, sdp: String)
  fun onAnswerReceived(senderId: String, sdp: String)
  fun onIceCandidateReceived(senderId: String, candidate: IceCandidatePayload)
  fun onPeerDisconnected(senderId: String)
  fun onLogMessage(message: String)
}
