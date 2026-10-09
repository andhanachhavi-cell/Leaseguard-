package com.example.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.data.model.AcquisitionLead
import com.example.data.model.LeaseItem

@Database(entities = [LeaseItem::class, AcquisitionLead::class], version = 1, exportSchema = false)
abstract class AppDatabase : RoomDatabase() {
    abstract fun leaseDao(): LeaseDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "leaseguard_database"
                ).fallbackToDestructiveMigration(true).build()
                INSTANCE = instance
                instance
            }
        }
    }
}
