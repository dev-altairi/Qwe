package com.example.data.db

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.data.model.CallRecord
import kotlinx.coroutines.flow.Flow

@Dao
interface CallDao {
  @Query("SELECT * FROM call_records ORDER BY timestamp DESC")
  fun getAllCalls(): Flow<List<CallRecord>>

  @Insert(onConflict = OnConflictStrategy.REPLACE)
  suspend fun insertCall(call: CallRecord): Long

  @Query("DELETE FROM call_records WHERE id = :callId")
  suspend fun deleteCall(callId: Long)

  @Query("DELETE FROM call_records")
  suspend fun clearAll()
}
