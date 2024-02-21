package jinproject.stepwalk.domain.usecase

import jinproject.stepwalk.domain.UserDetailData
import jinproject.stepwalk.domain.asUserStepRank
import jinproject.stepwalk.domain.model.RankModel
import jinproject.stepwalk.domain.model.StepModel
import jinproject.stepwalk.domain.model.StepRank
import jinproject.stepwalk.domain.model.StepRankBoard
import jinproject.stepwalk.domain.model.User
import jinproject.stepwalk.domain.model.UserStepRank
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import java.time.ZonedDateTime
import javax.inject.Inject

class GetRankBoardUseCase @Inject constructor() {
    operator fun invoke(page: Int): Flow<StepRankBoard> = flow {
        //TODO 해당 page 의 RankBoard 정보를 Fetch

        val data = UserDetailData.map { it.asUserStepRank() }
        val arrayList = arrayListOf<UserStepRank>().apply {
            if (page == 1)
                addAll(data)
        }
        val prevRankNumber = data.maxOfOrNull { it.stepRank.rank.rankNumber }

        repeat(50) {
            arrayList.add(
                UserStepRank(
                    user = User(
                        name = "홍길동${(page - 1) * 50 + it + 1}",
                        character = "ic_anim_running_1.json",
                        level = 10,
                        designation = "멀리 안나가는 자",
                    ),
                    stepRank = StepRank(
                        rank = RankModel(
                            rankNumber = (page - 1) * 50 + (it + 1) + (prevRankNumber ?: 0),
                            dailyIncreasedRank = 4,
                        ),
                        data = arrayListOf<StepModel>(
                        ).apply {
                            repeat(10) {
                                add(
                                    StepModel(
                                        startTime = ZonedDateTime.now(),
                                        endTime = ZonedDateTime.now(),
                                        figure = 500,
                                    )
                                )
                            }
                        },
                    )
                )
            )
        }

        emit(StepRankBoard.createRankBoardModel(arrayList))
    }
}