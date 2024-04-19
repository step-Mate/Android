package com.stepmate.data.repositoryImpl

import com.stepmate.data.remote.api.RankBoardApi
import com.stepmate.data.remote.dto.response.rank.toStepRankBoard
import com.stepmate.data.remote.utils.stepMateDataFlow
import com.stepmate.domain.model.rank.StepRankBoard
import com.stepmate.domain.repository.RankRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class RankRepositoryImpl @Inject constructor(
    private val rankBoardApi: RankBoardApi,
) : RankRepository {
    override fun getMonthRankBoard(page: Int): Flow<StepRankBoard> = stepMateDataFlow {
        val response = rankBoardApi.getMonthRankBoard(page)
        response.toStepRankBoard()
    }

    override fun getFriendRankBoard(page: Int): Flow<StepRankBoard> = stepMateDataFlow {
        val response = rankBoardApi.getFriendRankBoard(page)
        response.toStepRankBoard()
    }
}