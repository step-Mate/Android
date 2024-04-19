package com.stepmate.mission.util

import com.stepmate.design.R
import com.stepmate.domain.model.mission.CalorieMission
import com.stepmate.domain.model.mission.CalorieMissionLeaf
import com.stepmate.domain.model.mission.MissionComposite
import com.stepmate.domain.model.mission.MissionFigure
import com.stepmate.domain.model.mission.StepMission
import com.stepmate.domain.model.mission.StepMissionLeaf

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