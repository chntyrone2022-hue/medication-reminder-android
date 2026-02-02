package com.medication.reminder.data.dao

import androidx.room.*
import com.medication.reminder.data.entity.Medicine
import kotlinx.coroutines.flow.Flow

/**
 * 药品数据访问对象
 */
@Dao
interface MedicineDao {
    
    @Query("SELECT * FROM medicines WHERE isActive = 1 ORDER BY createdAt DESC")
    fun getAllActiveMedicines(): Flow<List<Medicine>>
    
    @Query("SELECT * FROM medicines ORDER BY createdAt DESC")
    fun getAllMedicines(): Flow<List<Medicine>>
    
    @Query("SELECT * FROM medicines WHERE id = :id")
    suspend fun getMedicineById(id: Long): Medicine?
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMedicine(medicine: Medicine): Long
    
    @Update
    suspend fun updateMedicine(medicine: Medicine)
    
    @Delete
    suspend fun deleteMedicine(medicine: Medicine)
    
    @Query("UPDATE medicines SET isActive = :isActive WHERE id = :id")
    suspend fun updateActiveStatus(id: Long, isActive: Boolean)
}
