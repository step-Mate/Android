package jinproject.stepwalk.data.remote.api

import jinproject.stepwalk.data.remote.dto.response.rank.FriendRankBoardResponse
import jinproject.stepwalk.data.remote.dto.response.rank.MonthRankBoardResponse
import retrofit2.http.GET
import retrofit2.http.Query

interface RankBoardApi {

    @GET("rank-board")
    suspend fun getMonthRankBoard(@Query("page") page: Int): List<MonthRankBoardResponse>

    @GET("rank-board/friends")
    suspend fun getFriendRankBoard(): List<FriendRankBoardResponse>
}