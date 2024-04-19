package com.stepmate.data.remote.api

import com.stepmate.data.remote.dto.request.DesignationRequest
import com.stepmate.data.remote.dto.response.mission.MissionsResponse
import com.stepmate.data.remote.dto.response.user.DesignationResponse
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.PATCH
import retrofit2.http.POST
import retrofit2.http.Query

interface MissionApi {
    @GET("missions")
    suspend fun getMissionList(): List<MissionsResponse>

    @POST("missions/complete")
    suspend fun completeMission(@Query("designation") designation: String): Response<Any>

    @PATCH("select-designation")
    suspend fun selectDesignation(@Body designationRequest: DesignationRequest): Response<Any>

    @GET("designations")
    suspend fun getDesignations(): List<DesignationResponse>
}