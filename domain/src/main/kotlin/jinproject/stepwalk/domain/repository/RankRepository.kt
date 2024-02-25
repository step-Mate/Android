package jinproject.stepwalk.domain.repository

import jinproject.stepwalk.domain.model.StepRankBoard
import kotlinx.coroutines.flow.Flow

interface RankRepository {
    fun getMonthRankBoard(page: Int): Flow<StepRankBoard>
    fun getFriendRankBoard(): Flow<StepRankBoard>

}