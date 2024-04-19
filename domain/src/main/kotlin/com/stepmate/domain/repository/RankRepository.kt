package com.stepmate.domain.repository

import com.stepmate.domain.model.rank.StepRankBoard
import kotlinx.coroutines.flow.Flow

interface RankRepository {
    fun getMonthRankBoard(page: Int): Flow<StepRankBoard>
    fun getFriendRankBoard(page: Int): Flow<StepRankBoard>

}