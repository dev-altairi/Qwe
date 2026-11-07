package com.example.data.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.data.model.CallRecord
import com.example.data.model.ContactRecord

@Database(entities = [CallRecord::class, ContactRecord::class], version = 2, exportSchema = false)
abstract class VoxFreeDatabase : RoomDatabase() {
  abstract fun callDao(): CallDao
  abstract fun contactDao(): ContactDao

  companion object {
    @Volatile
    private var INSTANCE: VoxFreeDatabase? = null

    fun getDatabase(context: Context): VoxFreeDatabase {
      return INSTANCE ?: synchronized(this) {
        val instance = Room.databaseBuilder(
          context.applicationContext,
          VoxFreeDatabase::class.java,
          "voxfree_p2p_database"
        ).fallbackToDestructiveMigration().build()
        INSTANCE = instance
        instance
      }
    }
  }
}
