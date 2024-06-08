package com.stepmate.data.local.database.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import com.stepmate.domain.model.mission.CalorieMission
import com.stepmate.domain.model.mission.CalorieMissionLeaf
import com.stepmate.domain.model.mission.MissionFigure
import com.stepmate.domain.model.mission.StepMission
import com.stepmate.domain.model.mission.StepMissionLeaf

@Entity(
    foreignKeys = [ForeignKey(
        entity = Mission::class,
        parentColumns = arrayOf("designation"),
        childColumns = arrayOf("designation"),
        onDelete = ForeignKey.CASCADE
    )],
    indices = [
        Index("designation")
    ]
)
data class MissionLeaf(
    @PrimaryKey(autoGenerate = true) val id: Int,
    val designation: String,
    val type: MissionType,
    val achieved: Int,
    val goal: Int,
)

enum class MissionType {
    Step, Calorie, Error
}

fun MissionFigure.getType() = when(this){
    is StepMissionLeaf, is StepMission -> MissionType.Step
    is CalorieMissionLeaf, is CalorieMission -> MissionType.Calorie
    else -> MissionType.Error
}