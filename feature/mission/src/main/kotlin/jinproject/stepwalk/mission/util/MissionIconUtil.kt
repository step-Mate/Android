package jinproject.stepwalk.mission.util

import jinproject.stepwalk.design.R
import jinproject.stepwalk.domain.model.mission.CalorieMission
import jinproject.stepwalk.domain.model.mission.CalorieMissionLeaf
import jinproject.stepwalk.domain.model.mission.MissionComposite
import jinproject.stepwalk.domain.model.mission.MissionFigure
import jinproject.stepwalk.domain.model.mission.StepMission
import jinproject.stepwalk.domain.model.mission.StepMissionLeaf

fun MissionFigure.getIcon() = when(this){
    is StepMissionLeaf, is StepMission -> R.drawable.ic_shoes
    is CalorieMissionLeaf, is CalorieMission -> R.drawable.ic_calorie
    is MissionComposite -> R.drawable.ic_mission_composite
    else -> R.drawable.ic_mission_composite
}

fun MissionFigure.getString() = when(this){
    is StepMissionLeaf -> "걸음수"
    is CalorieMissionLeaf -> "칼로리"
    else ->""
}