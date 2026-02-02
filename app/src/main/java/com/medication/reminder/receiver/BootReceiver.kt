package com.medication.reminder.receiver

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.medication.reminder.data.database.AppDatabase
import com.medication.reminder.data.repository.MedicineRepository
import com.medication.reminder.util.ReminderManager
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

/**
 * 开机启动接收器
 * 用于在设备重启后重新设置提醒
 */
class BootReceiver : BroadcastReceiver() {
    
    override fun onReceive(context: Context, intent: Intent) {
        if (intent.action == Intent.ACTION_BOOT_COMPLETED) {
            val database = AppDatabase.getDatabase(context)
            val repository = MedicineRepository(
                database.medicineDao(),
                database.medicationRecordDao()
            )
            val reminderManager = ReminderManager(context)
            
            CoroutineScope(Dispatchers.IO).launch {
                val medicines = repository.getAllMedicines().first()
                medicines.filter { it.isActive }.forEach { medicine ->
                    reminderManager.scheduleReminders(medicine)
                }
            }
        }
    }
}
