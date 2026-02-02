package com.medication.reminder.receiver

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.medication.reminder.R
import com.medication.reminder.util.NotificationHelper

/**
 * 用药提醒接收器
 */
class MedicationReminderReceiver : BroadcastReceiver() {
    
    override fun onReceive(context: Context, intent: Intent) {
        val medicineId = intent.getLongExtra("medicineId", -1)
        val medicineName = intent.getStringExtra("medicineName") ?: "药品"
        val dosage = intent.getStringExtra("dosage") ?: ""
        val date = intent.getStringExtra("date") ?: ""
        val time = intent.getStringExtra("time") ?: ""
        val customRingtone = intent.getStringExtra("customRingtone")
        
        NotificationHelper.showMedicationReminder(
            context = context,
            medicineId = medicineId,
            medicineName = medicineName,
            dosage = dosage,
            customRingtone = customRingtone
        )
    }
}
