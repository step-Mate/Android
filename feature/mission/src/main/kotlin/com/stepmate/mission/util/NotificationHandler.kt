package com.stepmate.mission.util

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import androidx.core.app.NotificationCompat

class NotificationHandler(
    private val context: Context
) {
    private val notificationManager = context.getSystemService(NotificationManager::class.java)
    private val notificationChannelID = "StepMateChannel"

    init {
        notificationManager.createNotificationChannel(
            NotificationChannel(notificationChannelID,"만보기 알림", NotificationManager.IMPORTANCE_LOW).apply {
                description = "만보기 알림 채널"
                enableVibration(false)
                setShowBadge(true)
                enableLights(false)
            }
        )
    }

    fun showMissionNotification(designation : String){
        val notification = NotificationCompat.Builder(context, notificationChannelID)
            .setSmallIcon(com.stepmate.design.R.drawable.ic_person_walking)
            .setStyle(NotificationCompat.DecoratedCustomViewStyle())
            .setContentTitle("미션 달성")
            .setContentText("$designation 을 완료하였습니다.")
            .setContentIntent(
                PendingIntent.getActivity(
                context,
                NOTIFICATION_MISSION_ID,
                Intent(context, Class.forName("com.stepmate.app.StepMateActivity")),
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
            ))
            .build()
        notificationManager.notify(NOTIFICATION_MISSION_ID,notification)
    }

    companion object{
        private const val NOTIFICATION_MISSION_ID = 100
    }
}