package jinproject.stepwalk.data.remote.dto.response.user

import jinproject.stepwalk.domain.model.user.StepModel
import java.text.SimpleDateFormat
import java.time.ZoneId
import java.time.ZonedDateTime
import java.time.temporal.TemporalAdjusters
import java.util.Locale

internal data class StepResponse(
    val step: Int,
    val date: String,
)

internal fun StepResponse.toStepModel(): StepModel {
    val date = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).parse(date)!!
    val zonedDateTime =
        ZonedDateTime.ofInstant(date.toInstant(), ZoneId.of("Asia/Seoul"))

    return StepModel( //TODO 시,분,초 가 할당될 필요가 있으면 해줘야 함
        startTime = zonedDateTime,
        endTime = zonedDateTime,
        figure = step
    )
}

internal fun List<StepResponse>.toStepModelList(): List<StepModel> {
    val dayOfMonth = ZonedDateTime.now().with(TemporalAdjusters.lastDayOfMonth()).dayOfMonth

    return Array<StepModel>(dayOfMonth) { StepModel(ZonedDateTime.now(), ZonedDateTime.now(), 0) }. apply {
        this@toStepModelList.forEachIndexed { idx, stepResponse ->
            set(idx, stepResponse.toStepModel())
        }
    }.toList()
}