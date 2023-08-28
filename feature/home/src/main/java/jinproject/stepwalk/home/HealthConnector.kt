package jinproject.stepwalk.home

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.util.Log
import androidx.compose.runtime.Stable
import androidx.health.connect.client.HealthConnectClient
import androidx.health.connect.client.records.StepsRecord
import androidx.health.connect.client.request.ReadRecordsRequest
import androidx.health.connect.client.time.TimeRangeFilter
import jinproject.stepwalk.domain.METs
import jinproject.stepwalk.home.state.Step
import java.time.Instant
import java.time.ZoneOffset

@Stable
class HealthConnector(
    context: Context
) {
    val healthConnectClient = getHealthClient(context)

    suspend fun insertSteps(
        step: Long,
        startTime: Instant,
        endTime: Instant
    ) {
        kotlin.runCatching {
            val stepsRecord = StepsRecord(
                count = step,
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
    ): List<Step>? =
        kotlin.runCatching {
            val response =
                healthConnectClient?.readRecords(
                    ReadRecordsRequest(
                        StepsRecord::class,
                        timeRangeFilter = TimeRangeFilter.between(startTime, endTime)
                    )
                )

            response?.let {
                response.records.map { record ->
                    Step(
                        distance = record.count,
                        start = (record.startTime.epochSecond / 60).toInt(),
                        end = (record.endTime.epochSecond / 60).toInt(),
                        type = type
                    )
                }
            }
        }.onFailure { e ->

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