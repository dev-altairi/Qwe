package com.example.data.db

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.data.model.ContactRecord
import kotlinx.coroutines.flow.Flow

@Dao
interface ContactDao {
  @Query("SELECT * FROM contacts ORDER BY displayName ASC")
  fun getAllContacts(): Flow<List<ContactRecord>>

  @Insert(onConflict = OnConflictStrategy.REPLACE)
  suspend fun insertContact(contact: ContactRecord): Long

  @Insert(onConflict = OnConflictStrategy.REPLACE)
  suspend fun insertAll(contacts: List<ContactRecord>)

  @Query("DELETE FROM contacts WHERE id = :contactId")
  suspend fun deleteContact(contactId: Long)

  @Query("SELECT COUNT(*) FROM contacts")
  suspend fun getCount(): Int
}
