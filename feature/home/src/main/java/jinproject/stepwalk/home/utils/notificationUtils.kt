package jinproject.stepwalk.home.utils

import android.annotation.SuppressLint
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.net.Uri
import androidx.core.app.NotificationCompat

internal const val StepWalkChannelId = "StepWalkChannel"

internal fun NotificationManager.sendNotification(
    name: String,
    img: String,
    code: Int,
    context: Context,
) {
    val appUri = ""
    val intent = Intent(Intent.ACTION_VIEW, Uri.parse(appUri))

    val backToAlarmPendingIntent = PendingIntent.getActivity(
        context,
        code,
        intent,
        PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
    )

    val builder = NotificationCompat.Builder(context, StepWalkChannelId)
        //.setSmallIcon()
        .setContentTitle("걸음수")
        .setPriority(NotificationCompat.PRIORITY_HIGH)
        .setContentIntent(backToAlarmPendingIntent)
        .setAutoCancel(true)

    notify(code, builder.build())

}

internal fun NotificationManager.createChannel() {
    val name = "만보기"
    val descriptionText = "만보기의 채널이에요."
    val importance = NotificationManager.IMPORTANCE_HIGH
    val channel = NotificationChannel(StepWalkChannelId, name, importance).apply {
        description = descriptionText
        enableVibration(true)
        setShowBadge(true)
        enableLights(true)
        lightColor = Color.BLUE
    }

    createNotificationChannel(channel)
}

internal fun NotificationManager.cancelNotification(id: Int) {
    cancel(id)
}