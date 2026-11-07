package com.example.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "contacts")
data class ContactRecord(
  @PrimaryKey(autoGenerate = true)
  val id: Long = 0,
  val callId: String,
  val username: String,
  val displayName: String,
  val avatarColorHex: Long = 0xFF10B981,
  val isOnline: Boolean = true
)
