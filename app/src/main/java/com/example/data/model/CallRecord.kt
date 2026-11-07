package com.example.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

enum class CallStatus {
  COMPLETED,
  MISSED,
  FAILED,
  CANCELLED
}

@Entity(tableName = "call_records")
data class CallRecord(
  @PrimaryKey(autoGenerate = true)
  val id: Long = 0,
  val peerCallId: String,
  val peerUsername: String,
  val timestamp: Long = System.currentTimeMillis(),
  val durationSeconds: Int = 0,
  val status: CallStatus = CallStatus.COMPLETED,
  val avgMosScore: Float = 4.4f,
  val avgLatencyMs: Int = 24,
  val connectionType: String = "Direct UDP/SRTP (P2P)",
  val codec: String = "Opus (48kHz/HD Stereo)"
)
