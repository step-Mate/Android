package jinproject.stepwalk.data.repositoryImpl

import jinproject.stepwalk.data.di.RetrofitWithTokenModule
import jinproject.stepwalk.data.local.database.dao.MissionLocal
import jinproject.stepwalk.data.local.database.entity.Mission
import jinproject.stepwalk.data.local.database.entity.MissionLeaf
import jinproject.stepwalk.data.local.database.entity.toMissionDataList
import jinproject.stepwalk.data.remote.api.MissionApi
import jinproject.stepwalk.data.remote.dto.request.DesignationRequest
import jinproject.stepwalk.data.remote.dto.response.mission.toMissionList
import jinproject.stepwalk.data.remote.dto.response.user.toDesignationModel
import jinproject.stepwalk.data.remote.utils.stepMateDataFlow
import jinproject.stepwalk.data.remote.utils.suspendAndCatchStepMateData
import jinproject.stepwalk.domain.model.DesignationState
import jinproject.stepwalk.domain.model.mission.CalorieMission
import jinproject.stepwalk.domain.model.mission.CalorieMissionLeaf
import jinproject.stepwalk.domain.model.mission.MissionCommon
import jinproject.stepwalk.domain.model.mission.MissionComposite
import jinproject.stepwalk.domain.model.mission.MissionFigure
import jinproject.stepwalk.domain.model.mission.MissionList
import jinproject.stepwalk.domain.model.mission.MissionType
import jinproject.stepwalk.domain.model.mission.StepMission
import jinproject.stepwalk.domain.model.mission.StepMissionLeaf
import jinproject.stepwalk.domain.repository.MissionRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import retrofit2.Retrofit
import javax.inject.Inject

class MissionRepositoryImpl @Inject constructor(
    private val missionLocal: MissionLocal,
    private val missionApi: MissionApi,
    @RetrofitWithTokenModule.RetrofitWithInterceptor private val retrofit: Retrofit,
) : MissionRepository {
    override fun getAllMissionList(): Flow<List<MissionList>> =
        missionLocal.getAllMissionList().map { it.toMissionDataList() }

    override fun getMissionList(title: String): Flow<MissionList> =
        missionLocal.getMissionList(title).map { list ->
            val missionList = ArrayList<MissionCommon>()
            list.forEach { missions ->
                if (missions.leaf.size == 1) {
                    when (missions.leaf.first().type) {
                        MissionType.Step -> {
                            missionList.add(
                                StepMission(
                                    designation = missions.mission.designation,
                                    intro = missions.mission.intro,
                                    achieved = missions.leaf.first().achieved,
                                    goal = missions.leaf.first().goal
                                )
                            )
                        }

                        MissionType.Calorie -> {
                            missionList.add(
                                CalorieMission(
                                    designation = missions.mission.designation,
                                    intro = missions.mission.intro,
                                    achieved = missions.leaf.first().achieved,
                                    goal = missions.leaf.first().goal
                                )
                            )
                        }
                    }
                } else {
                    val leafList = ArrayList<MissionFigure>()
                    missions.leaf.forEach { leaf ->
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
                    missionList.add(
                        MissionComposite(
                            designation = missions.mission.designation,
                            intro = missions.mission.intro,
                            missions = leafList
                        )
                    )
                }
            }
            MissionList(title, missionList)
        }

    override suspend fun updateMissionList() = withContext(Dispatchers.IO) {
        val list = missionApi.getMissionList()
        val apiList = list.toMissionList().sortedBy { it.title }
        val originalList = async {
            missionLocal.getAllMissionList().first().toMissionDataList().sortedBy { it.title }
        }
        if (originalList.await().isEmpty() || apiList != originalList) {
            list.forEach { missionResponse ->
                missionLocal.addMission(
                    Mission(
                        title = missionResponse.title,
                        designation = missionResponse.designation,
                        intro = missionResponse.contents
                    )
                )
                missionResponse.detail.forEach { detail ->
                    val type = when (detail.missionType) {
                        "STEP" -> MissionType.Step
                        "CALORIE" -> MissionType.Calorie
                        else -> throw IllegalArgumentException("알 수 없는 미션 타입: [${detail.missionType}] 입니다.")
                    }
                    val mission = originalList.await()
                        .find { it.title == missionResponse.title }?.list?.find { it.designation == missionResponse.designation }
                    val localAchieved = mission?.let { missionType ->
                        if (missionType is MissionComposite) {
                            when (type) {
                                MissionType.Step -> missionType.missions.find { it is StepMissionLeaf }
                                    ?.getMissionAchieved() ?: 0

                                MissionType.Calorie -> missionType.missions.find { it is CalorieMissionLeaf }
                                    ?.getMissionAchieved() ?: 0
                            }
                        } else {
                            missionType.getMissionAchieved()
                        }
                    } ?: 0
                    missionLocal.addMissionLeaf(
                        MissionLeaf(
                            id = 0,
                            designation = missionResponse.designation,
                            type = type,
                            achieved = if (localAchieved >= detail.currentValue) localAchieved else detail.currentValue.toInt(),
                            goal = detail.goal
                        )
                    )
                }
            }
            checkUpdateMission(
                missionLocal.getAllMissionList().first().toMissionDataList()
                    .sortedBy { it.title }).forEach { designation ->
                if (designation != "뉴비")
                    completeMission(designation)
            }

        } else {
            checkUpdateMission(originalList.await()).forEach { designation ->
                if (designation != "뉴비")
                    completeMission(designation)
            }
        }
    }

    override suspend fun updateMission(achieved: Int) {
        val step = getMissionAchieved(MissionType.Step).first() + achieved
        missionLocal.updateMissionAchieved(MissionType.Step,step)
        missionLocal.updateMissionAchieved(MissionType.Calorie,(step * 0.003f).toInt())
    }

    override suspend fun completeMission(designation: String) {
        suspendAndCatchStepMateData(retrofit) {
            missionApi.completeMission(title = designation)
        }
    }

    override suspend fun selectDesignation(designation: String) {
        suspendAndCatchStepMateData(retrofit) {
            missionApi.selectDesignation(DesignationRequest(designation))
        }
    }

    override fun getDesignation(): Flow<DesignationState> = stepMateDataFlow {
        missionApi.getDesignations().toDesignationModel()
    }

    override fun getMissionAchieved(missionType: MissionType): Flow<Int> =
        missionLocal.getMissionAchieved(missionType)

    override suspend fun checkUpdateMission(): List<String> =
        checkUpdateMission(
            missionLocal.getAllMissionList().first().toMissionDataList()
                .sortedBy { it.title }).filter { it != "뉴비" }


    private suspend fun checkUpdateMission(missionList: List<MissionList>): List<String> =
        withContext(Dispatchers.IO) {
            val designationList = getDesignation().first().list.sorted()
            val localDesignationList = async {
                missionList.map { missions ->
                    missions.list.filter {
                        it.getMissionAchieved() >= it.getMissionGoal()
                    }.map { it.designation }
                }.flatten().sorted()
            }
            localDesignationList.await().subtract(designationList.toSet()).toList()
        }
}