package com.medication.reminder.util

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.media.RingtoneManager
import android.os.Build
import androidx.core.app.NotificationCompat
import com.medication.reminder.MainActivity
import com.medication.reminder.R

/**
 * 通知助手类
 */
object NotificationHelper {
    
    private const val CHANNEL_ID = "medication_reminder_channel"
    private const val CHANNEL_NAME = "用药提醒"
    
    fun createNotificationChannel(context: Context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                CHANNEL_ID,
                CHANNEL_NAME,
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                description = "药品服用提醒通知"
                enableVibration(true)
                enableLights(true)
            }
            
            val notificationManager = context.getSystemService(NotificationManager::class.java)
            notificationManager.createNotificationChannel(channel)
        }
    }
    
    fun showMedicationReminder(
        context: Context,
        medicineId: Long,
        medicineName: String,
        dosage: String,
        customRingtone: String? = null
    ) {
        createNotificationChannel(context)
        
        val intent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }
        
        val pendingIntent = PendingIntent.getActivity(
            context,
            medicineId.toInt(),
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
        
        val defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)
        val soundUri = customRingtone?.let {
            android.net.Uri.parse(it)
        } ?: defaultSoundUri
        
        val notification = NotificationCompat.Builder(context, CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_notification)
            .setContentTitle("用药提醒")
            .setContentText("该吃药了：$medicineName ($dosage)")
            .setStyle(NotificationCompat.BigTextStyle()
                .bigText("该吃药了：$medicineName\n剂量：$dosage"))
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setSound(soundUri)
            .setVibrate(longArrayOf(0, 500, 500, 500))
            .setAutoCancel(true)
            .setContentIntent(pendingIntent)
            .build()
        
        val notificationManager = NotificationManagerCompat.from(context)
        notificationManager.notify(medicineId.toInt(), notification)
    }
}
