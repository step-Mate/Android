package jinproject.stepwalk.home

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.util.Log
import androidx.compose.runtime.Stable
import androidx.health.connect.client.HealthConnectClient
import androidx.health.connect.client.aggregate.AggregateMetric
import androidx.health.connect.client.permission.HealthPermission
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
import jinproject.stepwalk.home.screen.state.HealthCare
import jinproject.stepwalk.home.screen.state.HealthCareExtras
import jinproject.stepwalk.home.screen.state.HealthCareFactory
import jinproject.stepwalk.home.screen.state.HeartRate
import jinproject.stepwalk.home.screen.state.HeartRateFactory
import jinproject.stepwalk.home.screen.state.Step
import jinproject.stepwalk.home.screen.state.StepFactory
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
    @ApplicationContext private val context: Context
) {
    private val healthConnectClient by lazy {
        getHealthClient()
    }

    suspend fun checkPermissions(): Boolean {
        return healthConnectClient?.permissionController?.getGrantedPermissions()
            ?.containsAll(healthPermissions) ?: false
    }

    private fun getHealthClient(): HealthConnectClient? =
        if (checkAvailability() == HealthConnectClient.SDK_AVAILABLE) {
            HealthConnectClient.getOrCreate(context)
        } else
            null

    private fun checkAvailability() = HealthConnectClient.sdkStatus(context)

    init {
        if (checkAvailability() == HealthConnectClient.SDK_UNAVAILABLE_PROVIDER_UPDATE_REQUIRED)
            requireInstallHealthApk()
    }

    private fun requireInstallHealthApk() {
        val providerPackageName = "com.google.android.apps.healthdata"
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
    }

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

    internal suspend fun readStepsByPeriods(
        startTime: LocalDateTime,
        endTime: LocalDateTime,
        type: METs,
        period: Period
    ): List<Step>? = readHealthCareByPeriods(
        startTime = startTime,
        endTime = endTime,
        period = period,
        metrics = stepMetrics,
        factory = StepFactory.instance
    )

    internal suspend fun readStepsByHours(
        startTime: LocalDateTime,
        endTime: LocalDateTime,
        type: METs,
        duration: Duration
    ): List<Step>? = readHealthCareByDurations(
        startTime = startTime,
        endTime = endTime,
        duration = duration,
        metrics = stepMetrics,
        factory = StepFactory.instance
    )

    internal suspend fun getTodayTotalStep(
        type: METs
    ): Long = kotlin.run {
        val instant = Instant
            .now()
            .onKorea()
            .truncatedTo(ChronoUnit.DAYS)
            .toLocalDateTime()

        val steps = readStepsByPeriods(
            startTime = instant,
            endTime = instant
                .plus(23, ChronoUnit.HOURS)
                .plus(59, ChronoUnit.MINUTES)
                .plus(59, ChronoUnit.SECONDS),
            type = type,
            period = Period.ofDays(1)
        )

        steps?.sumOf { it.distance } ?: 0L
    }

    internal suspend fun readHeartRatesByHours(
        startTime: LocalDateTime,
        endTime: LocalDateTime,
        duration: Duration
    ): List<HeartRate>? = readHealthCareByDurations(
        startTime = startTime,
        endTime = endTime,
        duration = duration,
        metrics = heartRateMetrics,
        factory = HeartRateFactory.instance
    )

    internal suspend fun readHeartRatesByPeriods(
        startTime: LocalDateTime,
        endTime: LocalDateTime,
        period: Period
    ): List<HeartRate>? =
        readHealthCareByPeriods(
            startTime = startTime,
            endTime = endTime,
            period = period,
            metrics = heartRateMetrics,
            factory = HeartRateFactory.instance
        )

    private suspend fun <T : HealthCare> readHealthCareByPeriods(
        startTime: LocalDateTime,
        endTime: LocalDateTime,
        period: Period,
        metrics: MutableMap<String, AggregateMetric<Long>>,
        factory: HealthCareFactory<T>,
    ): List<T>? = kotlin.runCatching {
        val response =
            healthConnectClient?.aggregateGroupByPeriod(
                AggregateGroupByPeriodRequest(
                    metrics = metrics.values.toSet(),
                    timeRangeFilter = TimeRangeFilter.between(startTime, endTime),
                    timeRangeSlicer = period,
                    dataOriginFilter = setOf(DataOrigin(DATA_ORIGIN))
                )
            )

        response?.let { result ->
            result.map { record ->
                val extras = HealthCareExtras().apply {
                    metrics.forEach { metric ->
                        set(metric.key, record.result[metric.value] ?: 0L)
                    }
                }
                factory.create(
                    startTime = record.startTime.onKorea().toEpochSecond(),
                    endTime = record.endTime.onKorea().toEpochSecond(),
                    extras = extras,
                )
            }
        }
    }.onFailure { e ->
        Log.d("test", "byPeriods error: ${e.message}")
    }.getOrNull()

    private suspend fun <T : HealthCare> readHealthCareByDurations(
        startTime: LocalDateTime,
        endTime: LocalDateTime,
        duration: Duration,
        metrics: MutableMap<String, AggregateMetric<Long>>,
        factory: HealthCareFactory<T>,
    ): List<T>? = kotlin.runCatching {
        val response = healthConnectClient?.aggregateGroupByDuration(
            AggregateGroupByDurationRequest(
                metrics = metrics.values.toSet(),
                timeRangeFilter = TimeRangeFilter.between(startTime, endTime),
                timeRangeSlicer = duration,
                dataOriginFilter = setOf(DataOrigin(DATA_ORIGIN))
            )
        )

        response?.let { result ->
            result.map { record ->
                val extras = HealthCareExtras().apply {
                    metrics.forEach { metric ->
                        set(metric.key, record.result[metric.value] ?: 0L)
                    }
                }
                factory.create(
                    startTime = record.startTime.onKorea().toEpochSecond(),
                    endTime = record.endTime.onKorea().toEpochSecond(),
                    extras = extras,
                )
            }
        }
    }.onFailure { e ->
        Log.d("test", "byDurations error: ${e.message}")
    }.getOrNull()

    companion object {
        private const val DATA_ORIGIN = "jinproject.stepwalk.app"
        private val stepMetrics =
            mutableMapOf<String, AggregateMetric<Long>>().apply {
                put(HealthCareExtras.KEY_STEP, StepsRecord.COUNT_TOTAL)
            }
        private val heartRateMetrics =
            mutableMapOf<String, AggregateMetric<Long>>().apply {
                put(HealthCareExtras.KEY_HEART_RATE_MAX, HeartRateRecord.BPM_MAX)
                put(HealthCareExtras.KEY_HEART_RATE_MIN, HeartRateRecord.BPM_MIN)
                put(HealthCareExtras.KEY_HEART_RATE_AVG, HeartRateRecord.BPM_AVG)
            }
        val healthPermissions =
            setOf(
                HealthPermission.getReadPermission(StepsRecord::class),
                HealthPermission.getWritePermission(StepsRecord::class),
                HealthPermission.getReadPermission(HeartRateRecord::class),
                HealthPermission.getWritePermission(HeartRateRecord::class)
            )
    }
}