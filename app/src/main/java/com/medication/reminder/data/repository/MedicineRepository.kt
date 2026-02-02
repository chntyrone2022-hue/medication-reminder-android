package com.medication.reminder.data.repository

import com.medication.reminder.data.dao.MedicineDao
import com.medication.reminder.data.dao.MedicationRecordDao
import com.medication.reminder.data.entity.Medicine
import com.medication.reminder.data.entity.MedicationRecord
import kotlinx.coroutines.flow.Flow
import java.text.SimpleDateFormat
import java.util.*

/**
 * 药品仓库
 */
class MedicineRepository(
    private val medicineDao: MedicineDao,
    private val recordDao: MedicationRecordDao
) {
    
    fun getAllActiveMedicines(): Flow<List<Medicine>> = medicineDao.getAllActiveMedicines()
    
    fun getAllMedicines(): Flow<List<Medicine>> = medicineDao.getAllMedicines()
    
    suspend fun getMedicineById(id: Long): Medicine? = medicineDao.getMedicineById(id)
    
    suspend fun insertMedicine(medicine: Medicine): Long {
        val id = medicineDao.insertMedicine(medicine)
        // 创建初始用药记录
        createInitialRecords(medicine.copy(id = id))
        return id
    }
    
    suspend fun updateMedicine(medicine: Medicine) {
        medicineDao.updateMedicine(medicine)
        // 更新记录
        recordDao.deleteRecordsByMedicineId(medicine.id)
        createInitialRecords(medicine)
    }
    
    suspend fun deleteMedicine(medicine: Medicine) {
        medicineDao.deleteMedicine(medicine)
        recordDao.deleteRecordsByMedicineId(medicine.id)
    }
    
    suspend fun updateActiveStatus(id: Long, isActive: Boolean) {
        medicineDao.updateActiveStatus(id, isActive)
    }
    
    fun getRecordsByDate(date: String): Flow<List<MedicationRecord>> = 
        recordDao.getRecordsByDate(date)
    
    fun getRecordsByDateRange(startDate: String, endDate: String): Flow<List<MedicationRecord>> =
        recordDao.getRecordsByDateRange(startDate, endDate)
    
    suspend fun updateRecordTakenStatus(id: Long, isTaken: Boolean) {
        val takenAt = if (isTaken) System.currentTimeMillis() else null
        recordDao.updateTakenStatus(id, isTaken, takenAt)
    }
    
    /**
     * 创建初始用药记录
     */
    private suspend fun createInitialRecords(medicine: Medicine) {
        val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        val timeFormat = SimpleDateFormat("HH:mm", Locale.getDefault())
        
        val startDate = Date(medicine.startDate)
        val endDate = medicine.endDate?.let { Date(it) } ?: Date(System.currentTimeMillis() + 365L * 24 * 60 * 60 * 1000) // 默认一年
        
        val calendar = Calendar.getInstance()
        calendar.time = startDate
        
        // 解析提醒时间
        val reminderTimes = parseReminderTimes(medicine.reminderTimes)
        
        while (calendar.time <= endDate) {
            val dateStr = dateFormat.format(calendar.time)
            
            for (timeStr in reminderTimes) {
                val record = MedicationRecord(
                    medicineId = medicine.id,
                    date = dateStr,
                    reminderTime = timeStr,
                    isTaken = false
                )
                recordDao.insertRecord(record)
            }
            
            calendar.add(Calendar.DAY_OF_MONTH, 1)
        }
    }
    
    /**
     * 解析提醒时间字符串（JSON格式或逗号分隔）
     */
    private fun parseReminderTimes(times: String): List<String> {
        return try {
            // 尝试解析JSON数组
            if (times.startsWith("[")) {
                times.removePrefix("[").removeSuffix("]")
                    .split(",")
                    .map { it.trim().removeSurrounding("\"") }
            } else {
                // 逗号分隔
                times.split(",").map { it.trim() }
            }
        } catch (e: Exception) {
            listOf(times)
        }
    }
}
