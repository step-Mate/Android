package jinproject.stepwalk.data.repositoryImpl

import jinproject.stepwalk.data.remote.api.RankBoardApi
import jinproject.stepwalk.data.remote.dto.response.rank.toStepRankBoard
import jinproject.stepwalk.data.remote.utils.stepMateDataFlow
import jinproject.stepwalk.domain.model.StepRankBoard
import jinproject.stepwalk.domain.repository.RankRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class RankRepositoryImpl @Inject constructor(
    private val rankBoardApi: RankBoardApi,
) : RankRepository {
    override fun getMonthRankBoard(page: Int): Flow<StepRankBoard> = stepMateDataFlow {
        val response = rankBoardApi.getMonthRankBoard(page)
        response.toStepRankBoard()
    }

    override fun getFriendRankBoard(): Flow<StepRankBoard> = stepMateDataFlow {
        val response = rankBoardApi.getFriendRankBoard()
        response.toStepRankBoard()
    }
}