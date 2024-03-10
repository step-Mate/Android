package jinproject.stepwalk.home.utils

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.core.app.NotificationCompat

internal const val StepMateChannelId = "StepMateChannel"

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

    val builder = NotificationCompat.Builder(context, StepMateChannelId)
        //.setSmallIcon()
        .setContentTitle("걸음수")
        .setPriority(NotificationCompat.PRIORITY_HIGH)
        .setContentIntent(backToAlarmPendingIntent)
        .setAutoCancel(true)

    notify(code, builder.build())

}

internal fun NotificationManager.createChannel() {
    val name = "만보기 알림"
    val descriptionText = "만보기 알림 채널"
    val importance = NotificationManager.IMPORTANCE_LOW
    val channel = NotificationChannel(StepMateChannelId, name, importance).apply {
        description = descriptionText
        enableVibration(false)
        setShowBadge(true)
        enableLights(false)
    }

    createNotificationChannel(channel)
}

internal fun NotificationManager.cancelNotification(id: Int) {
    cancel(id)
}