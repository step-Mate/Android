package jinproject.stepwalk.home.worker

import android.content.Context
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

    private val coroutineExceptionHandler = CoroutineExceptionHandler { _, t ->

    }

    fun setNotification() {

    }


    override suspend fun doWork(): Result {
        val distance = inputData.getLong(StepSensorViewModel.KEY_DISTANCE, 0L)

        if (distance == 0L)
            return Result.failure(Data.Builder().putString("fail", "걸음수는 0이 될 수 없습니다.").build())

        withContext(Dispatchers.IO + coroutineExceptionHandler) {
            val step = getMissionAchievedUseCases(MissionType.Step).first()
            val calorie = ((step + distance) * 0.003f).toInt()
            updateMissionUseCases(MissionType.Step, (step + distance).toInt())
            updateMissionUseCases(MissionType.Calorie, calorie)
            checkUpdateMissionUseCases()
        }
        return Result.success()
    }
}