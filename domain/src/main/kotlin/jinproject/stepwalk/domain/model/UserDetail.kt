package jinproject.stepwalk.domain.model

import java.time.ZonedDateTime

/**
 * 사용자의 정보
 *
 * @param name: 이름
 * @param character: 캐릭터 아이콘
 * @param level: 레벨
 * @param designation: 칭호
 */
data class User(
    val name: String,
    val character: String,
    val level: Int,
    val designation: String,
) {
    companion object {
        fun getInitValues() = User(
            name = "홍길동",
            character = "ic_anim_running_1.json",
            level = 1,
            designation = "거침없이 걷는 자",
        )
    }
}

/**
 * 헬스 케어 데이터를 추상화한 인터페이스
 * @property startTime 시작 시간
 * @property endTime 종료 시간
 * @property figure 수치값
 */
interface HealthCareModel {
    val startTime: ZonedDateTime
    val endTime: ZonedDateTime
    val figure: Int
}

/**
 * 추상화된 헬스 케어 데이터의 걸음수 구현체
 */
data class StepModel(
    override val startTime: ZonedDateTime,
    override val endTime: ZonedDateTime,
    override val figure: Int,
) : HealthCareModel

/**
 * 랭크 정보를 표현하는 클래스
 */
data class RankModel(
    val rankNumber: Int,
    val dailyIncreasedRank: Int,
)

/**
 * 추상화된 헬스 케어 데이터들과 랭크 정보를 가지는 랭킹을 추상화한 인터페이스
 */
interface HealthRank {
    val rank: RankModel
    val data: List<HealthCareModel>

    fun getTotalHealthFigure() = data.sumOf { it.figure }
}

/**
 * 추상화된 랭킹을 구현하는 걸음수 랭킹 클래스
 */
data class StepRank(
    override val rank: RankModel,
    override val data: List<StepModel>,
) : HealthRank

/**
 * 유저의 상세 정보 클래스
 * @param user 유저의 정보
 * @param stepRank 걸음수 랭킹 정보
 * @param mission 수행중인 5개의 미션 정보
 */
data class UserDetailModel(
    val user: User,
    val stepRank: StepRank,
    val mission: List<MissionComponent>,
)