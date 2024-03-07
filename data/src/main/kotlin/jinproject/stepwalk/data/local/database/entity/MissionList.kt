package jinproject.stepwalk.data.local.database.entity

import androidx.room.Embedded
import androidx.room.Relation
import jinproject.stepwalk.domain.model.mission.CalorieMission
import jinproject.stepwalk.domain.model.mission.CalorieMissionLeaf
import jinproject.stepwalk.domain.model.mission.MissionCommon
import jinproject.stepwalk.domain.model.mission.MissionComposite
import jinproject.stepwalk.domain.model.mission.MissionFigure
import jinproject.stepwalk.domain.model.mission.MissionType
import jinproject.stepwalk.domain.model.mission.StepMission
import jinproject.stepwalk.domain.model.mission.StepMissionLeaf

data class MissionList(
    @Embedded val mission: Mission,
    @Relation(
        parentColumn = "designation",
        entityColumn = "designation"
    )
    val leaf: List<MissionLeaf>
)

internal fun List<MissionList>.toMissionDataList() : List<jinproject.stepwalk.domain.model.mission.MissionList>{
    val missionList = HashMap<String, ArrayList<MissionCommon>>()
    this.forEach {
        if (it.leaf.size == 1) {
            when (it.leaf.first().type) {
                MissionType.Step -> {
                    val missions = missionList.getOrDefault(
                        it.mission.title,
                        arrayListOf()
                    )
                    missions.add(
                        StepMission(
                            designation = it.mission.designation,
                            intro = it.mission.intro,
                            achieved = it.leaf.first().achieved,
                            goal = it.leaf.first().goal
                        )
                    )
                    missionList[it.mission.title] = missions
                }
                MissionType.Calorie -> {
                    val missions = missionList.getOrDefault(
                        it.mission.title,
                        arrayListOf()
                    )
                    missions.add(
                        CalorieMission(
                            designation = it.mission.designation,
                            intro = it.mission.intro,
                            achieved = it.leaf.first().achieved,
                            goal = it.leaf.first().goal
                        )
                    )
                    missionList[it.mission.title] = missions
                }
            }
        } else {
            val leafList = ArrayList<MissionFigure>()
            it.leaf.forEach { leaf ->
                when (leaf.type) {
                    MissionType.Step -> {
                        leafList.add(
                            StepMissionLeaf(
                                achieved = leaf.achieved,
                                goal = leaf.goal
                            )
                        )
                    }
                    MissionType.Calorie -> {
                        leafList.add(
                            CalorieMissionLeaf(
                                achieved = leaf.achieved,
                                goal = leaf.goal
                            )
                        )
                    }
                }
            }
            val missions = missionList.getOrDefault(
                it.mission.title,
                arrayListOf()
            )
            missions.add(
                MissionComposite(
                    designation = it.mission.designation,
                    intro = it.mission.intro,
                    missions = leafList
                )
            )
            missionList[it.mission.title] = missions
        }
    }
    return missionList.map {
        jinproject.stepwalk.domain.model.mission.MissionList(it.key, it.value)
    }
}