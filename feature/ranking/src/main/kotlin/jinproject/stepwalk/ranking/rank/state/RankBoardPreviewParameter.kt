package jinproject.stepwalk.ranking.rank.state

import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import jinproject.stepwalk.ranking.rank.Rank
import jinproject.stepwalk.ranking.rank.RankBoard

internal class RankBoardPreviewParameter : PreviewParameterProvider<RankBoard> {
    val data = listOf(
        Rank(
            name = "홍길동",
            character = "ic_anim_running_1.json",
            level = 10,
            step = 5000,
            designation = "멀리 안나가는 자",
            rankNumber = 7,
            dailyIncreasedRank = 4,
        ),
        Rank(
            name = "김지현",
            character = "ic_anim_running_1.json",
            level = 20,
            step = 8000,
            designation = "무법자",
            rankNumber = 5,
            dailyIncreasedRank = 8,
        ),
        Rank(
            name = "박신지",
            character = "ic_anim_running_1.json",
            level = 55,
            step = 55000,
            designation = "슈퍼맨",
            rankNumber = 2,
            dailyIncreasedRank = -1,
        ),
        Rank(
            name = "이지훈",
            character = "ic_anim_running_1.json",
            level = 130,
            step = 71000,
            designation = "전광석화",
            rankNumber = 1,
            dailyIncreasedRank = 3,
        ),
        Rank(
            name = "홍박사",
            character = "ic_anim_running_1.json",
            level = 33,
            step = 8000,
            designation = "님을 아세요?",
            rankNumber = 5,
            dailyIncreasedRank = -3,
        ),
        Rank(
            name = "김땡땡",
            character = "ic_anim_running_1.json",
            level = 22,
            step = 6000,
            designation = "소파에서 멀어지기",
            rankNumber = 6,
            dailyIncreasedRank = -6,
        ),
        Rank(
            name = "박땡땡",
            character = "ic_anim_running_1.json",
            level = 56,
            step = 13000,
            designation = "강남 건물주",
            rankNumber = 4,
            dailyIncreasedRank = 1,
        ),
        Rank(
            name = "이땡땡",
            character = "ic_anim_running_1.json",
            level = 66,
            step = 21000,
            designation = "약탈자",
            rankNumber = 3,
            dailyIncreasedRank = 0,
        ),
    )
    override val values: Sequence<RankBoard>
        get() = sequenceOf(
            RankBoard(data.sorted())
        )
}