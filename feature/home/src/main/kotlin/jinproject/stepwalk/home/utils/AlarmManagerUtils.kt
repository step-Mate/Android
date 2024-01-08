package jinproject.stepwalk.home.utils

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent

internal fun AlarmManager.setRepeating(
    context: Context,
    notifyIntent: () -> Intent,
    type: Int,
    time: Long,
    interval: Long
) {
    val notifyPendingIntent = PendingIntent.getBroadcast(
        context,
        1,
        notifyIntent(),
        PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
    )

    setRepeating(
        type,
        time,
        interval,
        notifyPendingIntent
    )
}