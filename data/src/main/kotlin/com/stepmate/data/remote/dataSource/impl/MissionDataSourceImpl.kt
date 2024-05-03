package com.stepmate.data.remote.dataSource.impl

import com.stepmate.data.di.RetrofitWithTokenModule
import com.stepmate.data.remote.api.MissionApi
import com.stepmate.data.remote.dataSource.MissionDataSource
import com.stepmate.data.remote.dto.request.DesignationRequest
import com.stepmate.data.remote.dto.response.mission.toMissionList
import com.stepmate.data.remote.dto.response.user.toDesignationModel
import com.stepmate.data.remote.utils.stepMateDataFlow
import com.stepmate.data.remote.utils.suspendAndCatchStepMateData
import com.stepmate.domain.model.DesignationState
import com.stepmate.domain.model.mission.MissionList
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.withContext
import retrofit2.Retrofit
import javax.inject.Inject

internal class MissionDataSourceImpl @Inject constructor(
    private val missionApi: MissionApi,
    @RetrofitWithTokenModule.RetrofitWithInterceptor private val retrofit: Retrofit,
) : MissionDataSource {

    override suspend fun getMissionList(): List<MissionList> =
        missionApi.getMissionList().toMissionList().sortedBy { it.title }

    override suspend fun selectDesignation(designation: String) {
        suspendAndCatchStepMateData(retrofit) {
            missionApi.selectDesignation(DesignationRequest(designation))
        }
    }

    override fun getDesignation(): Flow<DesignationState> = stepMateDataFlow {
        missionApi.getDesignations().toDesignationModel()
    }

    override suspend fun completeMission(designation: String) {
        suspendAndCatchStepMateData(retrofit) {
            missionApi.completeMission(designation = designation)
        }
    }

    override suspend fun checkUpdateMission(missionList: List<MissionList>): List<String> =
        withContext(Dispatchers.IO) {
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
            val designationList = getDesignation().first().list.sorted()
            localDesignationList.await().subtract(designationList.toSet()).toList()
        }
}