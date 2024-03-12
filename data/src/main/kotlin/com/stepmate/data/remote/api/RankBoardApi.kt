package com.stepmate.data.remote.api

import com.stepmate.data.remote.dto.response.rank.FriendRankBoardResponse
import com.stepmate.data.remote.dto.response.rank.MonthRankBoardResponse
import retrofit2.http.GET
import retrofit2.http.Query

interface RankBoardApi {

    @GET("rank-board")
    suspend fun getMonthRankBoard(@Query("page") page: Int): List<MonthRankBoardResponse>

    @GET("rank-board/friends")
    suspend fun getFriendRankBoard(@Query("page") page: Int): List<FriendRankBoardResponse>
}