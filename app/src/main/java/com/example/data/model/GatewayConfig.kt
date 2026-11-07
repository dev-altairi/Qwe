package com.example.data.model

data class GatewayConfig(
  val accountSid: String = "",
  val authToken: String = "",
  val callerId: String = "+12025550143",
  val twimlAppSid: String = "",
  val transportProtocol: String = "WebRTC (UDP/SRTP)",
  val preferredCodec: String = "Opus (32 kbps)",
  val stunTurnServer: String = "global.turn.twilio.com:3478",
  val jitterBufferLengthMs: Int = 40
)
