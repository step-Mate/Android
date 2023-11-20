package jinproject.stepwalk.home.receiver

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import dagger.hilt.android.AndroidEntryPoint
import jinproject.stepwalk.home.service.StepService

@AndroidEntryPoint
internal class AlarmReceiver : BroadcastReceiver() {

    override fun onReceive(p0: Context?, p1: Intent?) {
        p0?.startService(Intent(p0, StepService::class.java).apply {
            putExtra("alarm", true)
        })
    }
}