package jinproject.stepwalk.ranking.detail

import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import jinproject.stepwalk.domain.model.mission.CalorieMissionLeaf
import jinproject.stepwalk.domain.model.mission.MissionComposite
import jinproject.stepwalk.domain.model.mission.StepMission
import jinproject.stepwalk.domain.model.mission.StepMissionLeaf
import jinproject.stepwalk.ranking.rank.User
import jinproject.stepwalk.ranking.rank.state.RankBoardPreviewParameter
import java.time.ZonedDateTime
import java.time.temporal.TemporalAdjusters

internal class UserDetailPreviewParameter : PreviewParameterProvider<User> {
    override val values: Sequence<User>
        get() = sequenceOf(
            User(
                info = RankBoardPreviewParameter().data.first(),
                steps = run {
                    mutableListOf<Long>().apply {
                        repeat(
                            ZonedDateTime.now().with(TemporalAdjusters.lastDayOfMonth()).dayOfMonth
                        ) { idx ->
                            add(
                                1000 + idx.toLong() * 50
                            )
                        }
                    }
                },
                maxStep = 71000,
                latestMissions = listOf(
                    StepMission(
                        designation = "거침없이 걷는 자",
                        intro = "누적 1000 걸음수를 달성하면 'A' 칭호를 획득하실 수 있어요.",
                        achieved = 500,
                        goal = 1000,
                    ),
                    MissionComposite(
                        designation = "슈퍼맨",
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