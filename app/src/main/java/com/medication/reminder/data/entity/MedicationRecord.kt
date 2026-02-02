package com.medication.reminder.data.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

/**
 * 用药记录实体类
 */
@Entity(
    tableName = "medication_records",
    foreignKeys = [
        ForeignKey(
            entity = Medicine::class,
            parentColumns = ["id"],
            childColumns = ["medicineId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index(value = ["medicineId", "date", "reminderTime"])]
)
data class MedicationRecord(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val medicineId: Long,                // 药品ID
    val date: String,                    // 日期（格式：yyyy-MM-dd）
    val reminderTime: String,            // 提醒时间（格式：HH:mm）
    val isTaken: Boolean = false,        // 是否已服用
    val takenAt: Long? = null            // 服用时间（时间戳）
)
