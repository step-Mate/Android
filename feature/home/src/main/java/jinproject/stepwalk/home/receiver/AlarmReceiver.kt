package jinproject.stepwalk.home.receiver

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import androidx.work.Data
import androidx.work.ExistingWorkPolicy
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.OutOfQuotaPolicy
import androidx.work.WorkManager
import dagger.hilt.android.AndroidEntryPoint
import jinproject.stepwalk.home.service.StepSensorModule
import jinproject.stepwalk.home.service.StepService
import jinproject.stepwalk.home.worker.StepInsertWorker

@AndroidEntryPoint
internal class AlarmReceiver : BroadcastReceiver() {

    override fun onReceive(p0: Context?, p1: Intent?) {
        p0?.startForegroundService(Intent(p0, StepService::class.java).apply {
            putExtra("alarm", true)
        })
    }
}