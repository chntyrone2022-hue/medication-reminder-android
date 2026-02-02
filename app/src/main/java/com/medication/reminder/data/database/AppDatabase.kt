package com.medication.reminder.data.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.medication.reminder.data.dao.MedicationRecordDao
import com.medication.reminder.data.dao.MedicineDao
import com.medication.reminder.data.entity.MedicationRecord
import com.medication.reminder.data.entity.Medicine

/**
 * 应用数据库
 */
@Database(
    entities = [Medicine::class, MedicationRecord::class],
    version = 1,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    
    abstract fun medicineDao(): MedicineDao
    abstract fun medicationRecordDao(): MedicationRecordDao
    
    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null
        
        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "medication_database"
                )
                    .fallbackToDestructiveMigration()
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}
