package jinproject.stepwalk.home

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.util.Log
import androidx.compose.runtime.Stable
import androidx.health.connect.client.HealthConnectClient
import androidx.health.connect.client.records.HeartRateRecord
import androidx.health.connect.client.records.StepsRecord
import androidx.health.connect.client.records.metadata.DataOrigin
import androidx.health.connect.client.request.AggregateGroupByDurationRequest
import androidx.health.connect.client.request.AggregateGroupByPeriodRequest
import androidx.health.connect.client.time.TimeRangeFilter
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import jinproject.stepwalk.domain.model.METs
import jinproject.stepwalk.home.state.HeartRate
import jinproject.stepwalk.home.state.Step
import jinproject.stepwalk.home.utils.onKorea
import java.time.Duration
import java.time.Instant
import java.time.LocalDateTime
import java.time.Period
import java.time.ZoneOffset
import java.time.temporal.ChronoUnit
import javax.inject.Inject
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object HealthConnectorModule {
    @Singleton
    @Provides
    fun providesHealthConnector(@ApplicationContext context: Context): HealthConnector =
        HealthConnector(context)
}

@Stable
class HealthConnector @Inject constructor(
    @ApplicationContext context: Context
) {
    val healthConnectClient = getHealthClient(context)

    private val stepMetrics = setOf(StepsRecord.COUNT_TOTAL)
    private val heartRateMetrics =
        setOf(HeartRateRecord.BPM_MAX, HeartRateRecord.BPM_MIN, HeartRateRecord.BPM_AVG)

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
        }.onFailure { e ->
            Log.e("test", "error occurred while inserting steps : ${e.message}")
        }
    }

    suspend fun insertHeartRates(
        heartRate: Long,
        startTime: Instant,
        endTime: Instant
    ) {
        kotlin.runCatching {
            val heartRateRecord = HeartRateRecord(
                startTime = startTime,
                endTime = endTime,
                startZoneOffset = ZoneOffset.of("+9"),
                endZoneOffset = ZoneOffset.of("+9"),
                samples = listOf(
                    HeartRateRecord.Sample(
                        time = startTime,
                        beatsPerMinute = heartRate
                    )
                )
            )
            healthConnectClient?.insertRecords(listOf(heartRateRecord))
        }.onFailure { e ->
            Log.e("test", "${e.printStackTrace()}")
        }
    }

    suspend fun readStepsByPeriods(
        startTime: LocalDateTime,
        endTime: LocalDateTime,
        type: METs,
        period: Period
    ): List<Step>? = kotlin.runCatching {
        val response =
            healthConnectClient?.aggregateGroupByPeriod(
                AggregateGroupByPeriodRequest(
                    metrics = stepMetrics,
                    timeRangeFilter = TimeRangeFilter.between(startTime, endTime),
                    timeRangeSlicer = period,
                    dataOriginFilter = setOf(DataOrigin("jinproject.stepwalk.app"))
                )
            )

        response?.let { result ->
            result.map { record ->
                Step(
                    distance = record.result[StepsRecord.COUNT_TOTAL] ?: 0L,
                    start = (record.startTime.onKorea().toInstant().epochSecond / 60).toInt(),
                    end = (record.endTime.onKorea().toInstant().epochSecond / 60).toInt(),
                    type = type
                )
            }
        }
    }.onFailure { e ->
        Log.d("test", "byPeriods error: ${e.message}")
    }.getOrNull()

    suspend fun readStepsByHours(
        startTime: Instant,
        endTime: Instant,
        type: METs
    ): List<Step>? = kotlin.runCatching {
        val response =
            healthConnectClient?.aggregateGroupByDuration(
                AggregateGroupByDurationRequest(
                    metrics = stepMetrics,
                    timeRangeFilter = TimeRangeFilter.between(startTime, endTime),
                    timeRangeSlicer = Duration.ofHours(1L),
                    dataOriginFilter = setOf(DataOrigin("jinproject.stepwalk.app"))
                )
            )

        response?.let { result ->
            result.map { record ->
                Step(
                    distance = record.result[StepsRecord.COUNT_TOTAL] ?: 0L,
                    start = (record.startTime.epochSecond / 60).toInt(),
                    end = (record.endTime.epochSecond / 60).toInt(),
                    type = type
                )
            }
        }
    }.onFailure { e ->
        Log.d("test", "byHours error: ${e.message}")
    }.getOrNull()

    suspend fun getTotalStepToday(
        type: METs
    ): Long = kotlin.run {
        val instant = Instant
            .now()
            .onKorea()
            .truncatedTo(ChronoUnit.DAYS)
            .toInstant()

        val steps = readStepsByHours(
            startTime = instant,
            endTime = instant
                .plus(23, ChronoUnit.HOURS)
                .plus(59, ChronoUnit.MINUTES),
            type = type
        )

        steps?.sumOf { it.distance } ?: 0L
    }

    suspend fun readHeartRatesByHours(
        startTime: Instant,
        endTime: Instant
    ): List<HeartRate>? = kotlin.runCatching {
        val response =
            healthConnectClient?.aggregateGroupByDuration(
                AggregateGroupByDurationRequest(
                    metrics = heartRateMetrics,
                    timeRangeFilter = TimeRangeFilter.between(startTime, endTime),
                    timeRangeSlicer = Duration.ofHours(1L),
                    dataOriginFilter = setOf(DataOrigin("jinproject.stepwalk.app"))
                )
            )
        response?.let { result ->
            result.map { record ->
                HeartRate(
                    startTime = record.startTime,
                    endTime = record.endTime,
                    min = record.result[HeartRateRecord.BPM_MAX]?.toInt() ?: 0,
                    max = record.result[HeartRateRecord.BPM_MIN]?.toInt() ?: 0,
                    avg = record.result[HeartRateRecord.BPM_AVG]?.toInt() ?: 0
                )
            }
        }
    }.onFailure { e ->

    }.getOrNull()

    suspend fun readHeartRatesByPeriods(
        startTime: LocalDateTime,
        endTime: LocalDateTime,
        period: Period
    ): List<HeartRate>? = kotlin.runCatching {
        val response =
            healthConnectClient?.aggregateGroupByPeriod(
                AggregateGroupByPeriodRequest(
                    metrics = heartRateMetrics,
                    timeRangeFilter = TimeRangeFilter.between(startTime, endTime),
                    timeRangeSlicer = period,
                    dataOriginFilter = setOf(DataOrigin("jinproject.stepwalk.app"))
                )
            )
        response?.let { result ->
            result.map { record ->
                HeartRate(
                    startTime = record.startTime.onKorea().toInstant(),
                    endTime = record.endTime.onKorea().toInstant(),
                    min = record.result[HeartRateRecord.BPM_MAX]?.toInt() ?: 0,
                    max = record.result[HeartRateRecord.BPM_MIN]?.toInt() ?: 0,
                    avg = record.result[HeartRateRecord.BPM_AVG]?.toInt() ?: 0
                )
            }
        }
    }.onFailure { e ->

    }.getOrNull()

    private fun getHealthClient(context: Context): HealthConnectClient? = run {
        val providerPackageName = "com.google.android.apps.healthdata"
        val availabilityStatus = HealthConnectClient.sdkStatus(context, providerPackageName)
        if (availabilityStatus == HealthConnectClient.SDK_UNAVAILABLE) {
            return null
        }
        if (availabilityStatus == HealthConnectClient.SDK_UNAVAILABLE_PROVIDER_UPDATE_REQUIRED) {

            val uriString =
                "market://details?id=$providerPackageName&url=healthconnect%3A%2F%2Fonboarding"
            context.startActivity(
                Intent(Intent.ACTION_VIEW).apply {
                    setPackage("com.android.vending")
                    data = Uri.parse(uriString)
                    putExtra("overlay", true)
                    putExtra("callerId", context.packageName)
                    addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                }
            )
            return null
        }

        HealthConnectClient.getOrCreate(context)
    }
}