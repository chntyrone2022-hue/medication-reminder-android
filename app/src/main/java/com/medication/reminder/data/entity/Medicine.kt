package com.medication.reminder.data.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.Date

/**
 * 药品实体类
 */
@Entity(tableName = "medicines")
data class Medicine(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val name: String,                    // 药品名称
    val purpose: String,                 // 用途
    val dosage: String,                  // 每次剂量
    val timesPerDay: Int,                // 每日次数
    val reminderTimes: String,           // 提醒时间（JSON格式，存储多个时间点）
    val startDate: Long,                 // 开始日期（时间戳）
    val endDate: Long?,                  // 结束日期（时间戳，null表示长期服用）
    val isActive: Boolean = true,        // 是否激活
    val customRingtone: String? = null,  // 自定义铃声路径
    val createdAt: Long = System.currentTimeMillis()
)
