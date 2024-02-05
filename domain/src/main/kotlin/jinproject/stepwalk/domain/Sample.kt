package jinproject.stepwalk.domain

import jinproject.stepwalk.domain.model.RankModel
import jinproject.stepwalk.domain.model.StepModel
import jinproject.stepwalk.domain.model.StepRank
import jinproject.stepwalk.domain.model.User
import jinproject.stepwalk.domain.model.UserDetailModel
import jinproject.stepwalk.domain.model.UserStepRank
import java.time.ZonedDateTime

val UserDetailData = listOf(
    UserDetailModel(
        user = User(
            name = "홍길동",
            character = "ic_anim_running_1.json",
            level = 10,
            designation = "멀리 안나가는 자",
        ),
        stepRank = StepRank(
            rank = RankModel(
                rankNumber = 7,
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
        ),
        mission = emptyList()
    ),
    UserDetailModel(
        user = User(
            name = "김지현",
            character = "ic_anim_running_1.json",
            level = 20,
            designation = "무법자",
        ),
        stepRank = StepRank(
            rank = RankModel(
                rankNumber = 5,
                dailyIncreasedRank = 8,
            ),
            data = arrayListOf<StepModel>(

            ).apply {
                repeat(10) {
                    add(
                        StepModel(
                            startTime = ZonedDateTime.now(),
                            endTime = ZonedDateTime.now(),
                            figure = 800,
                        )
                    )
                }
            },
        ),
        mission = emptyList()
    ),
    UserDetailModel(
        user = User(
            name = "박신지",
            character = "ic_anim_running_1.json",
            level = 55,
            designation = "슈퍼맨",
        ),
        stepRank = StepRank(
            rank = RankModel(
                rankNumber = 2,
                dailyIncreasedRank = -1,
            ),
            data = arrayListOf<StepModel>(

            ).apply {
                repeat(10) {
                    add(
                        StepModel(
                            startTime = ZonedDateTime.now(),
                            endTime = ZonedDateTime.now(),
                            figure = 5500,
                        )
                    )
                }
            },
        ),
        mission = emptyList()
    ),
    UserDetailModel(
        user = User(
            name = "이지훈",
            character = "ic_anim_running_1.json",
            level = 130,
            designation = "전광석화",
        ),
        stepRank = StepRank(
            rank = RankModel(
                rankNumber = 1,
                dailyIncreasedRank = 3,
            ),
            data = arrayListOf<StepModel>(

            ).apply {
                repeat(10) {
                    add(
                        StepModel(
                            startTime = ZonedDateTime.now(),
                            endTime = ZonedDateTime.now(),
                            figure = 7100,
                        )
                    )
                }
            },
        ),
        mission = emptyList()
    ),
    UserDetailModel(
        user = User(
            name = "홍박사",
            character = "ic_anim_running_1.json",
            level = 33,
            designation = "님을 아세요?",
        ),
        stepRank = StepRank(
            rank = RankModel(
                rankNumber = 5,
                dailyIncreasedRank = -3,
            ),
            data = arrayListOf<StepModel>(

            ).apply {
                repeat(10) {
                    add(
                        StepModel(
                            startTime = ZonedDateTime.now(),
                            endTime = ZonedDateTime.now(),
                            figure = 800,
                        )
                    )
                }
            },
        ),
        mission = emptyList()
    ),
    UserDetailModel(
        user = User(
            name = "김땡땡",
            character = "ic_anim_running_1.json",
            level = 22,
            designation = "소파에서 멀어지기",
        ),
        stepRank = StepRank(
            rank = RankModel(
                rankNumber = 6,
                dailyIncreasedRank = -6,
            ),
            data = arrayListOf<StepModel>(

            ).apply {
                repeat(10) {
                    add(
                        StepModel(
                            startTime = ZonedDateTime.now(),
                            endTime = ZonedDateTime.now(),
                            figure = 600,
                        )
                    )
                }
            },
        ),
        mission = emptyList()
    ),
    UserDetailModel(
        user = User(
            name = "박땡땡",
            character = "ic_anim_running_1.json",
            level = 56,
            designation = "강남 건물주",
        ),
        stepRank = StepRank(
            rank = RankModel(
                rankNumber = 4,
                dailyIncreasedRank = -1,
            ),
            data = arrayListOf<StepModel>(

            ).apply {
                repeat(10) {
                    add(
                        StepModel(
                            startTime = ZonedDateTime.now(),
                            endTime = ZonedDateTime.now(),
                            figure = 1300,
                        )
                    )
                }
            },
        ),
        mission = emptyList()
    ),
    UserDetailModel(
        user = User(
            name = "이땡땡",
            character = "ic_anim_running_1.json",
            level = 66,
            designation = "약탈자",
        ),
        stepRank = StepRank(
            rank = RankModel(
                rankNumber = 3,
                dailyIncreasedRank = 0,
            ),
            data = arrayListOf<StepModel>(

            ).apply {
                repeat(10) {
                    add(
                        StepModel(
                            startTime = ZonedDateTime.now(),
                            endTime = ZonedDateTime.now(),
                            figure = 2100,
                        )
                    )
                }
            },
        ),
        mission = emptyList()
    ),
)

fun UserDetailModel.asUserStepRank() = UserStepRank(
    user = user,
    stepRank = stepRank
)