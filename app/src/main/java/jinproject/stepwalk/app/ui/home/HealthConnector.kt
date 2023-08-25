package jinproject.stepwalk.app.ui.home

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.util.Log
import androidx.compose.runtime.Stable
import androidx.health.connect.client.HealthConnectClient
import androidx.health.connect.client.records.StepsRecord
import androidx.health.connect.client.request.ReadRecordsRequest
import androidx.health.connect.client.time.TimeRangeFilter
import jinproject.stepwalk.app.ui.home.state.Step
import jinproject.stepwalk.domain.METs
import java.time.Instant
import java.time.ZoneOffset
import java.time.temporal.ChronoUnit

@Stable
class HealthConnector(
    context: Context
) {
    val healthConnectClient = getHealthClient(context)

    suspend fun insertSteps(
        startTime: Instant,
        endTime: Instant
    ) {
        kotlin.runCatching {
            val stepsRecord = StepsRecord(
                count = 120,
                startTime = startTime,
                endTime = endTime,
                startZoneOffset = ZoneOffset.of("+9"),
                endZoneOffset = ZoneOffset.of("+9"),
            )
            healthConnectClient?.insertRecords(listOf(stepsRecord))
        }.onFailure {

        }
    }

    suspend fun readStepsByTimeRange(
        startTime: Instant,
        endTime: Instant,
        type: METs
    ): Step? =
        kotlin.runCatching {
            var steps = 0L
            var minutes = 0

            val response =
                healthConnectClient?.readRecords(
                    ReadRecordsRequest(
                        StepsRecord::class,
                        timeRangeFilter = TimeRangeFilter.between(startTime, endTime)
                    )
                )
            response?.let {
                for (stepRecord in response.records) {
                    steps += stepRecord.count

                    val seconds = stepRecord.endTime.epochSecond - stepRecord.startTime.epochSecond
                    minutes += when(seconds < 60) {
                        true ->  1
                        false -> (seconds / 60).toInt()
                    }
                }
            }
            Step(
                distance = steps,
                minutes = minutes,
                type = type
            )
        }.onFailure {

        }.getOrNull()


    private fun getHealthClient(context: Context): HealthConnectClient? = run {
        val providerPackageName = "com.google.android.apps.healthdata"
        val availabilityStatus = HealthConnectClient.sdkStatus(context, providerPackageName)
        if (availabilityStatus == HealthConnectClient.SDK_UNAVAILABLE) {
            return null// early return as there is no viable integration
        }
        if (availabilityStatus == HealthConnectClient.SDK_UNAVAILABLE_PROVIDER_UPDATE_REQUIRED) {
            // Optionally redirect to package installer to find a provider, for example:
            val uriString = "market://details?id=$providerPackageName&url=healthconnect%3A%2F%2Fonboarding"
            context.startActivity(
                Intent(Intent.ACTION_VIEW).apply {
                    setPackage("com.android.vending")
                    data = Uri.parse(uriString)
                    putExtra("overlay", true)
                    putExtra("callerId", context.packageName)
                }
            )
            return null
        }

        HealthConnectClient.getOrCreate(context)
    }
}