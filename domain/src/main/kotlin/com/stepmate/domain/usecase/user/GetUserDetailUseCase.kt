package com.stepmate.domain.usecase.user

import com.stepmate.domain.model.user.UserDetailModel
import com.stepmate.domain.repository.UserRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetUserDetailUseCase @Inject constructor(
    private val userRepository: UserRepository,
) {
    operator fun invoke(userName: String): Flow<UserDetailModel> = userRepository.getUserDetail(userName)


        /*flow {
        val data = UserDetailData

        val fetchedUser = data.find { it.user.name == userName }!!

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
    }*/
}