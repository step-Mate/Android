package jinproject.stepwalk.data.remote.api

import jinproject.stepwalk.data.remote.dto.response.ApiResponse
import jinproject.stepwalk.data.remote.dto.response.mission.MissionsResponse
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Query

interface MissionApi {
    @GET("missions")
    suspend fun getMissionList() : List<MissionsResponse>
    @POST("missions/complete")
    suspend fun completeMission(@Query("title") designation : String) : ApiResponse<Nothing>
}