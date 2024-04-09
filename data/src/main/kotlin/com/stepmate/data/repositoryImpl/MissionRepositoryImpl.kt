package com.stepmate.data.repositoryImpl

import com.stepmate.data.di.RetrofitWithTokenModule
import com.stepmate.data.local.database.dao.MissionLocal
import com.stepmate.data.local.database.entity.Mission
import com.stepmate.data.local.database.entity.MissionLeaf
import com.stepmate.data.local.database.entity.toMissionDataList
import com.stepmate.data.local.datasource.BodyDataSource
import com.stepmate.data.remote.api.MissionApi
import com.stepmate.data.remote.dto.request.DesignationRequest
import com.stepmate.data.remote.dto.response.mission.toMissionList
import com.stepmate.data.remote.dto.response.user.toDesignationModel
import com.stepmate.data.remote.utils.stepMateDataFlow
import com.stepmate.data.remote.utils.suspendAndCatchStepMateData
import com.stepmate.domain.model.DesignationState
import com.stepmate.domain.model.mission.CalorieMission
import com.stepmate.domain.model.mission.CalorieMissionLeaf
import com.stepmate.domain.model.mission.MissionCommon
import com.stepmate.domain.model.mission.MissionComposite
import com.stepmate.domain.model.mission.MissionFigure
import com.stepmate.domain.model.mission.MissionList
import com.stepmate.domain.model.mission.MissionType
import com.stepmate.domain.model.mission.StepMission
import com.stepmate.domain.model.mission.StepMissionLeaf
import com.stepmate.domain.repository.MissionRepository
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
    private val bodyDataSource: BodyDataSource,
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

    override suspend fun updateMissionList(): List<String> = withContext(Dispatchers.IO) {
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
                            achieved = when {
                                missionResponse.detail.size >= 2 && detail.currentValue.toInt() == 0 -> 0
                                localAchieved >= detail.currentValue.toInt() -> localAchieved
                                else -> detail.currentValue.toInt()
                            },
                            goal = detail.goal
                        )
                    )
                }
            }
            val complete = checkUpdateMission(
                missionLocal.getAllMissionList().first().toMissionDataList()
                    .sortedBy { it.title })
            complete.forEach { designation ->
                completeMission(designation)
            }
            complete
        } else {
            val complete = checkUpdateMission(originalList.await())
            complete.forEach { designation ->
                completeMission(designation)
            }
            complete
        }
    }

    override suspend fun updateMission(achieved: Int) = withContext(Dispatchers.IO) {
        val step = getMissionAchieved(MissionType.Step).first() + achieved
        val timeStep = async { getMissionTimeAchieved(MissionType.Step).first() + achieved }
        missionLocal.updateMissionAchieved(MissionType.Step, step)
        missionLocal.updateMissionAchieved(MissionType.Calorie, getCalories(step).toInt())
        missionLocal.updateMissionTimeAchieved(MissionType.Step, timeStep.await())
        missionLocal.updateMissionTimeAchieved(
            MissionType.Calorie,
            getCalories(timeStep.await()).toInt()
        )
    }

    override suspend fun selectDesignation(designation: String) {
        suspendAndCatchStepMateData(retrofit) {
            missionApi.selectDesignation(DesignationRequest(designation))
        }
    }

    override fun getDesignation(): Flow<DesignationState> = stepMateDataFlow {
        missionApi.getDesignations().toDesignationModel()
    }

    override suspend fun checkUpdateMission(): List<String> {
        val complete = checkUpdateMission(
            missionLocal.getAllMissionList().first().toMissionDataList()
                .sortedBy { it.title })
        complete.forEach { designation ->
            completeMission(designation)
        }
        return complete
    }

    override suspend fun resetMissionTime() =
        missionLocal.resetMissionTime()

    private fun getMissionAchieved(missionType: MissionType): Flow<Int> =
        missionLocal.getMissionAchieved(missionType)

    private fun getMissionTimeAchieved(missionType: MissionType): Flow<Int> =
        missionLocal.getMissionTimeAchieved(missionType)

    private suspend fun completeMission(designation: String) {
        suspendAndCatchStepMateData(retrofit) {
            missionApi.completeMission(designation = designation)
        }
    }

    private suspend fun checkUpdateMission(missionList: List<MissionList>): List<String> =
        withContext(Dispatchers.IO) {
            val designationList = getDesignation().first().list.sorted()
            val localDesignationList = async(Dispatchers.Default) {
                missionList.map { missions ->
                    val complete = arrayListOf<String>()
                    missions.list.forEach { missionCommon ->
                        if (missionCommon.getMissionProgress() == 1f)
                            complete.add(missionCommon.designation)
                        else
                            return@forEach
                    }
                    complete
                }.flatten().sorted()
            }
            localDesignationList.await().subtract(designationList.toSet()).toList()
        }

    private suspend fun getCalories(step: Int) =
        3.0 * (3.5 * bodyDataSource.getBodyData().map { it.weight }
            .first() * step * 0.0008 * 15) * 5 / 1000
}