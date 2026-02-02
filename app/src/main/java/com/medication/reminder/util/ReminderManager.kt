package com.medication.reminder.util

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import com.medication.reminder.data.entity.Medicine
import com.medication.reminder.receiver.MedicationReminderReceiver
import java.text.SimpleDateFormat
import java.util.*

/**
 * 提醒管理器
 */
class ReminderManager(private val context: Context) {
    
    private val alarmManager: AlarmManager = 
        context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
    
    /**
     * 设置药品提醒
     */
    fun scheduleReminders(medicine: Medicine) {
        cancelReminders(medicine.id)
        
        if (!medicine.isActive) return
        
        val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        val timeFormat = SimpleDateFormat("HH:mm", Locale.getDefault())
        
        val startDate = Date(medicine.startDate)
        val endDate = medicine.endDate?.let { Date(it) } ?: Date(System.currentTimeMillis() + 365L * 24 * 60 * 60 * 1000)
        
        val reminderTimes = parseReminderTimes(medicine.reminderTimes)
        val now = System.currentTimeMillis()
        
        val dayCalendar = Calendar.getInstance()
        dayCalendar.time = startDate
        dayCalendar.set(Calendar.HOUR_OF_DAY, 0)
        dayCalendar.set(Calendar.MINUTE, 0)
        dayCalendar.set(Calendar.SECOND, 0)
        dayCalendar.set(Calendar.MILLISECOND, 0)
        
        while (dayCalendar.time <= endDate) {
            val dateStr = dateFormat.format(dayCalendar.time)
            
            for (timeStr in reminderTimes) {
                val parts = timeStr.split(":")
                if (parts.size < 2) continue
                val hour = parts[0].toIntOrNull() ?: continue
                val minute = parts[1].toIntOrNull() ?: 0
                
                val alarmCalendar = dayCalendar.clone() as Calendar
                alarmCalendar.set(Calendar.HOUR_OF_DAY, hour)
                alarmCalendar.set(Calendar.MINUTE, minute)
                alarmCalendar.set(Calendar.SECOND, 0)
                alarmCalendar.set(Calendar.MILLISECOND, 0)
                
                if (alarmCalendar.timeInMillis <= now) continue
                
                val intent = Intent(context, MedicationReminderReceiver::class.java).apply {
                    putExtra("medicineId", medicine.id)
                    putExtra("medicineName", medicine.name)
                    putExtra("dosage", medicine.dosage)
                    putExtra("date", dateStr)
                    putExtra("time", timeStr)
                    putExtra("customRingtone", medicine.customRingtone)
                }
                
                val pendingIntent = PendingIntent.getBroadcast(
                    context,
                    generateRequestCode(medicine.id, dateStr, timeStr),
                    intent,
                    PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
                )
                
                alarmManager.setExactAndAllowWhileIdle(
                    AlarmManager.RTC_WAKEUP,
                    alarmCalendar.timeInMillis,
                    pendingIntent
                )
            }
            
            dayCalendar.add(Calendar.DAY_OF_MONTH, 1)
        }
    }
    
    /**
     * 取消药品提醒
     */
    fun cancelReminders(medicineId: Long) {
        // 取消所有相关的提醒（最多1000个）
        for (i in 0 until 1000) {
            val intent = Intent(context, MedicationReminderReceiver::class.java)
            val pendingIntent = PendingIntent.getBroadcast(
                context,
                medicineId.toInt() * 1000 + i,
                intent,
                PendingIntent.FLAG_NO_CREATE or PendingIntent.FLAG_IMMUTABLE
            )
            pendingIntent?.let {
                alarmManager.cancel(it)
                it.cancel()
            }
        }
    }
    
    /**
     * 生成请求码
     */
    private fun generateRequestCode(medicineId: Long, date: String, time: String): Int {
        val hash = (medicineId.toString() + date.replace("-", "") + time.replace(":", "")).hashCode()
        return hash and 0x7FFFFFFF
    }
    
    /**
     * 解析提醒时间
     */
    private fun parseReminderTimes(times: String): List<String> {
        return try {
            if (times.startsWith("[")) {
                times.removePrefix("[").removeSuffix("]")
                    .split(",")
                    .map { it.trim().removeSurrounding("\"") }
            } else {
                times.split(",").map { it.trim() }
            }
        } catch (e: Exception) {
            listOf(times)
        }
    }
}
