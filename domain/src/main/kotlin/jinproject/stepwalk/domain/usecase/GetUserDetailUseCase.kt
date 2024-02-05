package jinproject.stepwalk.domain.usecase

import jinproject.stepwalk.domain.model.CalorieMissionLeaf
import jinproject.stepwalk.domain.model.MissionComposite
import jinproject.stepwalk.domain.model.StepMissionLeaf
import jinproject.stepwalk.domain.model.StepMission
import jinproject.stepwalk.domain.UserDetailData
import jinproject.stepwalk.domain.model.UserDetailModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class GetUserDetailUseCase @Inject constructor() {
    operator fun invoke(userName: String): Flow<UserDetailModel> = flow {
        //TODO 토큰으로 서버에서 User 정보 fetch
        val data = UserDetailData

        val fetchedUser = data.find { it.user.name == "이지훈" }!!

        emit(
            UserDetailModel(
                user = fetchedUser.user,
                stepRank = fetchedUser.stepRank,
                mission = listOf(
                    StepMission(
                        designation = "A",
                        intro = "누적 1000 걸음수를 달성하면 'A' 칭호를 획득하실 수 있어요.",
                        achieved = 500,
                        goal = 1000,
                    ),
                    MissionComposite(
                        designation = "B",
                        intro = "누적 5000 걸음수 와 누적 10000 칼로리 소모를 달성하면 'B' 칭호를 획득하실 수 있어요.",
                        missions = listOf(
                            StepMissionLeaf(
                                achieved = 1000,
                                goal = 5000,
                            ),
                            CalorieMissionLeaf(
                                achieved = 1000,
                                goal = 10000,
                            ),
                        )
                    )
                ),
            )
        )
    }
}