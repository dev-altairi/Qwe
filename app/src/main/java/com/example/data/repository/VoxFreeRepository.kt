package com.example.data.repository

import android.content.Context
import android.content.SharedPreferences
import com.example.data.db.VoxFreeDatabase
import com.example.data.model.CallRecord
import com.example.data.model.ContactRecord
import com.example.data.model.UserProfile
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlin.random.Random

class VoxFreeRepository(context: Context) {
  private val database = VoxFreeDatabase.getDatabase(context)
  private val callDao = database.callDao()
  private val contactDao = database.contactDao()
  private val prefs: SharedPreferences =
    context.getSharedPreferences("voxfree_p2p_prefs", Context.MODE_PRIVATE)

  private val _userProfile = MutableStateFlow(loadUserProfile())
  val userProfile: StateFlow<UserProfile> = _userProfile.asStateFlow()

  val allCalls: Flow<List<CallRecord>> = callDao.getAllCalls()
  val allContacts: Flow<List<ContactRecord>> = contactDao.getAllContacts()

  init {
    CoroutineScope(Dispatchers.IO).launch {
      if (contactDao.getCount() == 0) {
        val initialContacts = listOf(
          ContactRecord(
            callId = "10452",
            username = "sarah_k",
            displayName = "Sarah Jenkins",
            avatarColorHex = 0xFF10B981,
            isOnline = true
          ),
          ContactRecord(
            callId = "77319",
            username = "david_m",
            displayName = "David Miller",
            avatarColorHex = 0xFF06B6D4,
            isOnline = true
          ),
          ContactRecord(
            callId = "39012",
            username = "elena_voip",
            displayName = "Elena Rostova",
            avatarColorHex = 0xFF8B5CF6,
            isOnline = false
          ),
          ContactRecord(
            callId = "62890",
            username = "alex_dev",
            displayName = "Alex Carter",
            avatarColorHex = 0xFFF59E0B,
            isOnline = true
          )
        )
        contactDao.insertAll(initialContacts)
      }
    }
  }

  private fun loadUserProfile(): UserProfile {
    var callId = prefs.getString("call_id", null)
    if (callId == null) {
      callId = Random.nextInt(10000, 99999).toString()
      prefs.edit().putString("call_id", callId).apply()
    }
    val username = prefs.getString("username", "user_${callId.takeLast(3)}") ?: "user_123"
    val displayName = prefs.getString("display_name", "Vox Caller") ?: "Vox Caller"
    return UserProfile(username = username, callId = callId, displayName = displayName)
  }

  fun updateUserProfile(username: String, displayName: String) {
    prefs.edit()
      .putString("username", username)
      .putString("display_name", displayName)
      .apply()
    _userProfile.value = _userProfile.value.copy(username = username, displayName = displayName)
  }

  suspend fun logCall(callRecord: CallRecord): Long = callDao.insertCall(callRecord)

  suspend fun deleteCall(callId: Long) = callDao.deleteCall(callId)

  suspend fun clearCallHistory() = callDao.clearAll()

  suspend fun addContact(contact: ContactRecord): Long = contactDao.insertContact(contact)

  suspend fun deleteContact(contactId: Long) = contactDao.deleteContact(contactId)
}
