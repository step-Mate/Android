package jinproject.stepwalk.home.worker

import android.app.NotificationManager
import android.content.Context
import android.content.Context.NOTIFICATION_SERVICE
import androidx.core.app.NotificationCompat
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.Data
import androidx.work.WorkerParameters
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import jinproject.stepwalk.domain.model.mission.MissionType
import jinproject.stepwalk.domain.usecase.mission.CheckUpdateMissionUseCases
import jinproject.stepwalk.domain.usecase.mission.GetMissionAchievedUseCases
import jinproject.stepwalk.domain.usecase.mission.UpdateMissionUseCases
import jinproject.stepwalk.home.service.StepSensorViewModel
import jinproject.stepwalk.home.utils.StepWalkChannelId
import jinproject.stepwalk.home.utils.createChannel
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.withContext

@HiltWorker
internal class MissionUpdateWorker @AssistedInject constructor(
    @Assisted context: Context,
    @Assisted workerParams: WorkerParameters,
    private val getMissionAchievedUseCases: GetMissionAchievedUseCases,
    private val updateMissionUseCases: UpdateMissionUseCases,
    private val checkUpdateMissionUseCases: CheckUpdateMissionUseCases
) : CoroutineWorker(context, workerParams) {
    private var notificationManager: NotificationManager? = null
    private val coroutineExceptionHandler = CoroutineExceptionHandler { _, t ->

    }

    private fun setNotificationManager() {
        notificationManager =
            applicationContext.getSystemService(NOTIFICATION_SERVICE) as NotificationManager
        notificationManager?.createChannel()
    }

    private fun setNotification(designation: String) {
        val notification =
            NotificationCompat.Builder(applicationContext, StepWalkChannelId)
                .setSmallIcon(jinproject.stepwalk.design.R.drawable.ic_person_walking)
                .setStyle(NotificationCompat.DecoratedCustomViewStyle())
                .setContentTitle("미션 달성")
                .setContentText("$designation 을 완료하였습니다.")
                .setOngoing(true)
                .build()
        notificationManager?.notify(NOTIFICATION_MISSION_ID, notification)
    }

    override suspend fun doWork(): Result {
        setNotificationManager()
        val distance = inputData.getLong(StepSensorViewModel.KEY_DISTANCE, 0L)

        if (distance == 0L)
            return Result.failure(Data.Builder().putString("fail", "걸음수는 0이 될 수 없습니다.").build())

        withContext(Dispatchers.IO + coroutineExceptionHandler) {
            val step = getMissionAchievedUseCases(MissionType.Step).first()
            val calorie = ((step + distance) * 0.003f).toInt()
            updateMissionUseCases(MissionType.Step, (step + distance).toInt())
            updateMissionUseCases(MissionType.Calorie, calorie)
            checkUpdateMissionUseCases().forEach { designation ->
                setNotification(designation)
            }
        }
        return Result.success()
    }

    companion object {
        private const val NOTIFICATION_MISSION_ID = 100
    }
}