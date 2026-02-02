package com.medication.reminder.data.dao

import androidx.room.*
import com.medication.reminder.data.entity.MedicationRecord
import kotlinx.coroutines.flow.Flow

/**
 * 用药记录数据访问对象
 */
@Dao
interface MedicationRecordDao {
    
    @Query("SELECT * FROM medication_records WHERE date = :date ORDER BY reminderTime")
    fun getRecordsByDate(date: String): Flow<List<MedicationRecord>>
    
    @Query("SELECT * FROM medication_records WHERE medicineId = :medicineId AND date = :date")
    suspend fun getRecordByMedicineAndDate(medicineId: Long, date: String): List<MedicationRecord>
    
    @Query("SELECT * FROM medication_records WHERE medicineId = :medicineId AND date BETWEEN :startDate AND :endDate ORDER BY date, reminderTime")
    fun getRecordsByDateRange(medicineId: Long, startDate: String, endDate: String): Flow<List<MedicationRecord>>
    
    @Query("SELECT * FROM medication_records WHERE date BETWEEN :startDate AND :endDate ORDER BY date, reminderTime")
    fun getRecordsByDateRange(startDate: String, endDate: String): Flow<List<MedicationRecord>>
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertRecord(record: MedicationRecord): Long
    
    @Update
    suspend fun updateRecord(record: MedicationRecord)
    
    @Query("UPDATE medication_records SET isTaken = :isTaken, takenAt = :takenAt WHERE id = :id")
    suspend fun updateTakenStatus(id: Long, isTaken: Boolean, takenAt: Long?)
    
    @Query("DELETE FROM medication_records WHERE medicineId = :medicineId")
    suspend fun deleteRecordsByMedicineId(medicineId: Long)
}
