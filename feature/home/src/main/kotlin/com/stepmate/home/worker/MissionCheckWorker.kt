package com.stepmate.home.worker

import android.app.Notification
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Context.NOTIFICATION_SERVICE
import android.content.Intent
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.stepmate.domain.usecase.mission.CheckUpdateMissionUseCases
import com.stepmate.home.utils.StepMateChannelId
import com.stepmate.home.utils.createChannel
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

@HiltWorker
internal class MissionCheckWorker @AssistedInject constructor(
    @Assisted context: Context,
    @Assisted workerParams: WorkerParameters,
    private val checkUpdateMissionUseCases: CheckUpdateMissionUseCases
) : CoroutineWorker(context, workerParams) {
    private var notificationManager: NotificationManager? = null
    private val coroutineExceptionHandler = CoroutineExceptionHandler { _, t ->
        Log.d("test", "error has occurred : ${t.message}")
    }

    private fun setNotificationManager() {
        notificationManager =
            applicationContext.getSystemService(NOTIFICATION_SERVICE) as NotificationManager
        notificationManager?.createChannel()
    }

    private fun setMissionNotification(designation: String): Notification =
        NotificationCompat.Builder(applicationContext, StepMateChannelId)
            .setSmallIcon(com.stepmate.design.R.drawable.ic_person_walking)
            .setStyle(NotificationCompat.DecoratedCustomViewStyle())
            .setContentTitle("미션 달성")
            .setContentText("$designation 을 완료하였습니다.")
            .setContentIntent(
                PendingIntent.getActivity(
                    applicationContext,
                    NOTIFICATION_MISSION_ID,
                    Intent(applicationContext, Class.forName(StepMateActivity)),
                    PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
                )
            )
            .build()

    override suspend fun doWork(): Result {
        setNotificationManager()
        withContext(Dispatchers.IO + coroutineExceptionHandler) {
            checkUpdateMissionUseCases().forEach { designation ->
                notificationManager?.notify(
                    NOTIFICATION_MISSION_ID,
                    setMissionNotification(designation)
                )
            }
        }
        return Result.success()
    }

    companion object {
        private const val NOTIFICATION_MISSION_ID = 100
        private const val StepMateActivity = "com.stepmate.app.StepMateActivity"
    }
}