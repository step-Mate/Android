package jinproject.stepwalk.mission.screen.mission.state

import androidx.annotation.DrawableRes
import androidx.compose.runtime.Stable
import jinproject.stepwalk.design.R

@Stable
internal data class Mission(
    val title: MissionTitle,
    val value: MissionValue
)

@Stable
internal data class MissionTitle(
    val title : String = "",
    @DrawableRes val image : Int,
    val mode : MissionMode = MissionMode.time
)

@Stable
internal data class MissionValue(
    val now : Int = 0,
    val max : Int = 3
){
    fun progress() = now / max.toFloat()
    fun isMatched() = now == max
}

internal fun mergerToMission(title: List<MissionTitle>, value: List<MissionValue>) : List<Mission> = title.zip(value){ t, v ->
    Mission(t,v)
}

enum class MissionMode {
    time,repeat
}

internal fun Int.toMissionMode() : MissionMode = when(this){
    1 -> MissionMode.repeat
    else -> MissionMode.time
}


//임시 테스트용 -> room으로 이전예정
internal object MissionList {
    val list = listOf(
        MissionTitle(
            title = "주간 미션",
            image = R.drawable.ic_fire,
        ),
        MissionTitle(
            title = "월간 미션",
            image = R.drawable.ic_fire,
        ),
        MissionTitle(
            title = "걸음수 미션",
            image = R.drawable.ic_fire,
            mode = MissionMode.repeat
        ),
        MissionTitle(
            title = "칼로리 미션",
            image = R.drawable.ic_fire,
            mode = MissionMode.repeat
        )
    )
}
