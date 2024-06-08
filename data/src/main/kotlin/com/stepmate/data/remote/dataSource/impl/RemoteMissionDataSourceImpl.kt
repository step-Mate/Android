package com.stepmate.data.remote.dataSource.impl

import com.stepmate.data.di.RetrofitWithTokenModule
import com.stepmate.data.remote.api.MissionApi
import com.stepmate.data.remote.dataSource.RemoteMissionDataSource
import com.stepmate.data.remote.dto.request.DesignationRequest
import com.stepmate.data.remote.dto.response.mission.toMissionList
import com.stepmate.data.remote.utils.suspendAndCatchStepMateData
import com.stepmate.domain.model.mission.MissionCommon
import retrofit2.Retrofit
import javax.inject.Inject

internal class RemoteMissionDataSourceImpl @Inject constructor(
    private val missionApi: MissionApi,
    @RetrofitWithTokenModule.RetrofitWithInterceptor private val retrofit: Retrofit,
) : RemoteMissionDataSource {

    override suspend fun getMissionList(): Map<String, List<MissionCommon>> =
        missionApi.getMissionList().toMissionList()

    override suspend fun selectDesignation(designation: String) {
        suspendAndCatchStepMateData(retrofit) {
            missionApi.selectDesignation(DesignationRequest(designation))
        }
    }

    override suspend fun getDesignation(): List<String> =
        suspendAndCatchStepMateData(retrofit) {
            missionApi.getDesignations()
        }?.map { data -> data["designation"] ?: "" }?.sorted() ?: emptyList()


    override suspend fun completeMission(designation: String) {
        suspendAndCatchStepMateData(retrofit) {
            missionApi.completeMission(designation = designation)
        }
    }
}