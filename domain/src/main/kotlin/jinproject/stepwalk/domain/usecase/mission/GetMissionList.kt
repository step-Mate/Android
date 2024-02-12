package jinproject.stepwalk.domain.usecase.mission

import jinproject.stepwalk.domain.model.ResponseState
import jinproject.stepwalk.domain.model.mission.CalorieMissionLeaf
import jinproject.stepwalk.domain.model.mission.MissionComposite
import jinproject.stepwalk.domain.model.mission.MissionList
import jinproject.stepwalk.domain.model.mission.StepMission
import jinproject.stepwalk.domain.model.mission.StepMissionLeaf
import jinproject.stepwalk.domain.repository.MissionRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class GetMissionList @Inject constructor(
    private val missionRepository: MissionRepository
) {
    suspend operator fun invoke(title: String): Flow<ResponseState<MissionList>> = flow {
        when (title) {
            "주간 미션" -> {
                emit(
                    ResponseState.Result(
                        MissionList(
                            title = "주간 미션",
                            list = listOf(
                                MissionComposite(
                                    designation = "주간 달성",
                                    intro = "주간동안 걸음과 칼로리를 달성하세요.",
                                    missions = listOf(
                                        StepMissionLeaf(
                                            achieved = 10,
                                            goal = 100
                                        ),
                                        CalorieMissionLeaf(
                                            achieved = 10,
                                            goal = 300
                                        )
                                    )
                                )
                            )
                        )
                    )
                )
            }

            "통합 미션" -> {
                emit(
                    ResponseState.Result(
                        MissionList(
                            title = "통합 미션",
                            list = listOf(
                                MissionComposite(
                                    designation = "1 통합 달성",
                                    intro = "주간동안 걸음과 칼로리를 달성하세요.",
                                    missions = listOf(
                                        StepMissionLeaf(
                                            achieved = 10,
                                            goal = 100
                                        ),
                                        CalorieMissionLeaf(
                                            achieved = 10,
                                            goal = 300
                                        )
                                    )
                                ),
                                MissionComposite(
                                    designation = "2 통합 달성",
                                    intro = "주간동안 걸음과 칼로리를 달성하세요.",
                                    missions = listOf(
                                        StepMissionLeaf(
                                            achieved = 0,
                                            goal = 1000
                                        ),
                                        CalorieMissionLeaf(
                                            achieved = 0,
                                            goal = 3000
                                        )
                                    )
                                ),
                                MissionComposite(
                                    designation = "3 통합 달성",
                                    intro = "주간동안 걸음과 칼로리를 달성하세요.",
                                    missions = listOf(
                                        StepMissionLeaf(
                                            achieved = 0,
                                            goal = 2000
                                        ),
                                        CalorieMissionLeaf(
                                            achieved = 0,
                                            goal = 4000
                                        )
                                    )
                                )
                            )
                        )
                    )
                )
            }

            "걸음수 미션" -> {
                emit(
                    ResponseState.Result(
                        MissionList(
                            title = "걸음수 미션",
                            list = listOf(
                                StepMission(
                                    designation = "100걸음",
                                    intro = "100걸음을 달성",
                                    achieved = 100,
                                    goal = 100
                                ),
                                StepMission(
                                    designation = "200걸음",
                                    intro = "200걸음을 달성",
                                    achieved = 100,
                                    goal = 200
                                ),
                                StepMission(
                                    designation = "300걸음",
                                    intro = "300걸음을 달성",
                                    achieved = 0,
                                    goal = 300
                                ),
                                StepMission(
                                    designation = "400걸음",
                                    intro = "400걸음을 달성",
                                    achieved = 0,
                                    goal = 400
                                ),
                            )
                        )
                    )
                )
            }

            else -> {
                emit(
                    ResponseState.Result(
                        MissionList(
                            title = "걸음수 미션",
                            list = listOf(
                                StepMission(
                                    designation = "100걸음",
                                    intro = "100걸음을 달성",
                                    achieved = 100,
                                    goal = 100
                                ),
                                StepMission(
                                    designation = "200걸음",
                                    intro = "200걸음을 달성",
                                    achieved = 100,
                                    goal = 200
                                ),
                                StepMission(
                                    designation = "300걸음",
                                    intro = "300걸음을 달성",
                                    achieved = 0,
                                    goal = 300
                                ),
                                StepMission(
                                    designation = "400걸음",
                                    intro = "400걸음을 달성",
                                    achieved = 0,
                                    goal = 400
                                ),
                            )
                        )
                    )
                )
            }
        }
    }

    //= missionRepository.getMissionList(title)
}