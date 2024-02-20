package jinproject.stepwalk.data.repositoryimpl

import jinproject.stepwalk.data.local.database.dao.MissionLocal
import jinproject.stepwalk.data.local.database.entity.MissionType
import jinproject.stepwalk.domain.model.mission.CalorieMission
import jinproject.stepwalk.domain.model.mission.CalorieMissionLeaf
import jinproject.stepwalk.domain.model.mission.MissionCommon
import jinproject.stepwalk.domain.model.mission.MissionComposite
import jinproject.stepwalk.domain.model.mission.MissionFigure
import jinproject.stepwalk.domain.model.mission.MissionList
import jinproject.stepwalk.domain.model.mission.StepMission
import jinproject.stepwalk.domain.model.mission.StepMissionLeaf
import jinproject.stepwalk.domain.repository.MissionRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class MissionRepositoryImpl @Inject constructor(
    private val missionLocal: MissionLocal
) : MissionRepository {
    override suspend fun getAllMissionList(): Flow<List<MissionList>> =
        missionLocal.getAllMissionList().map { list ->
            val missionList = HashMap<String, ArrayList<MissionCommon>>()
            list.forEach {
                if (it.leaf.size == 1) {
                    when (it.leaf.first().type) {
                        MissionType.Step -> {
                            (missionList.getOrDefault(
                                it.mission.title,
                                emptyArray<MissionCommon>()
                            ) as ArrayList<MissionCommon>).add(
                                StepMission(
                                    designation = it.mission.designation,
                                    intro = it.mission.intro,
                                    achieved = it.leaf.first().achieved,
                                    goal = it.leaf.first().goal
                                )
                            )
                        }

                        MissionType.Calorie -> {
                            (missionList.getOrDefault(
                                it.mission.title,
                                emptyArray<MissionCommon>()
                            ) as ArrayList<MissionCommon>).add(
                                CalorieMission(
                                    designation = it.mission.designation,
                                    intro = it.mission.intro,
                                    achieved = it.leaf.first().achieved,
                                    goal = it.leaf.first().goal
                                )
                            )
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
                        (missionList.getOrDefault(
                            it.mission.title,
                            emptyArray<MissionCommon>()
                        ) as ArrayList<MissionCommon>).add(
                            MissionComposite(
                                designation = it.mission.designation,
                                intro = it.mission.intro,
                                missions = leafList
                            )
                        )
                    }
                }
            }

            missionList.map {
                MissionList(it.key, it.value)
            }
        }

    override suspend fun getMissionList(title: String): Flow<MissionList> =
        missionLocal.getMissionList(title).map { list ->
            val missionList = ArrayList<MissionCommon>()
            list.forEach {
                if (it.leaf.size == 1) {
                    when (it.leaf.first().type) {
                        MissionType.Step -> {
                            missionList.add(
                                StepMission(
                                    designation = it.mission.designation,
                                    intro = it.mission.intro,
                                    achieved = it.leaf.first().achieved,
                                    goal = it.leaf.first().goal
                                )
                            )
                        }

                        MissionType.Calorie -> {
                            missionList.add(
                                CalorieMission(
                                    designation = it.mission.designation,
                                    intro = it.mission.intro,
                                    achieved = it.leaf.first().achieved,
                                    goal = it.leaf.first().goal
                                )
                            )
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
                        missionList.add(
                            MissionComposite(
                                designation = it.mission.designation,
                                intro = it.mission.intro,
                                missions = leafList
                            )
                        )
                    }
                }
            }
            MissionList(title, missionList)
        }

    override suspend fun updateMissionList() {

    }

    override suspend fun updateMission() {

    }


}