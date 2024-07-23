package com.stepmate.app.ui.navigation.permission

import android.Manifest
import android.app.AlarmManager
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.core.content.ContextCompat

object PermissionRequester {
    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    fun checkNotification(context: Context): Boolean {
        val result = ContextCompat.checkSelfPermission(
            context,
            Manifest.permission.POST_NOTIFICATIONS
        )

        return result == PackageManager.PERMISSION_GRANTED
    }

    fun checkActivityRecognition(context: Context): Boolean {
        val result = ContextCompat.checkSelfPermission(
            context,
            Manifest.permission.ACTIVITY_RECOGNITION
        )

        return result == PackageManager.PERMISSION_GRANTED
    }

    @RequiresApi(Build.VERSION_CODES.S)
    fun checkExactAlarm(context: Context): Boolean {
        val alarmManager =
            context.getSystemService(Context.ALARM_SERVICE) as AlarmManager

        return alarmManager.canScheduleExactAlarms()
    }

    class PermissionResults(
        val notification: Boolean = false,
        val activityRecognition: Boolean = false,
        val exactAlarm: Boolean = false,
    )

    fun checkAllPermissions(applicationContext: Context): PermissionResults {
        val isNotificationGranted =
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU)
                checkNotification(applicationContext)
            else
                true

        val isActivityRecognitionGranted =
            checkActivityRecognition(applicationContext)

        val isExactAlarmGranted =
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S)
                checkExactAlarm(applicationContext)
            else
                true

        return PermissionResults(
            notification = isNotificationGranted,
            activityRecognition = isActivityRecognitionGranted,
            exactAlarm = isExactAlarmGranted,
        )
    }
}