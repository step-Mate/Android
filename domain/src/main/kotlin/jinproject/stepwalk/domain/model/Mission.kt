package jinproject.stepwalk.domain.model

import androidx.annotation.DrawableRes

/**
 * 미션의 수치(달성도, 목표)를 추상화한 인터페이스
 *
 * 수치를 별도로 추상화한 이유는 단일미션이 모여서 복합미션을 이루되, 복합미션과 단일미션을 동등하게 표현해야 하는데(Composite 패턴)
 *
 * 수치를 제외한 공통화된 것들은 단일 미션과 복합 미션 모두가 하나씩 가지고, 단일 미션의 수치들을 모두 더하여 복합미션의 수치로 반환하기 위함
 */
interface MissionFigure {
    fun getMissionAchieved(): Int
    fun getMissionGoal(): Int
    fun getMissionProgress() =
        (getMissionAchieved().toFloat() / getMissionGoal().toFloat()).coerceAtMost(1f)
}

/**
 * 미션의 수치를 구현하면서, 수치값을 가지는 수치화된 미션 자체를 추상화한 클래스
 * 걸음수 수치에 대한 미션인지, 칼로리 수치에 대한 미션인지 에 대한 구체적인 것에 의존하지 않아 열린 확장의 가능
 * @sample StepMissionLeaf
 * @sample CalorieMissionLeaf
 */
abstract class MissionLeaf(
    open val achieved: Int,
    open val goal: Int,
) : MissionFigure {
    override fun getMissionAchieved(): Int = achieved
    override fun getMissionGoal(): Int = goal
}

/**
 * 걸음수의 수치화된 미션의 구현체
 */
data class StepMissionLeaf(
    override val achieved: Int,
    override val goal: Int,
) : MissionLeaf(achieved = achieved, goal = goal)

/**
 * 칼로리의 수치화된 미션의 구현체
 */
data class CalorieMissionLeaf(
    override val achieved: Int,
    override val goal: Int,
) : MissionLeaf(achieved = achieved, goal = goal)

/**
 * 미션의 수치를 decorate 하여 공통화된 것들(칭호, 설명)을 추상화한 인터페이스
 */
interface MissionComponent : MissionFigure {
    fun getMissionDesignation(): String
    fun getMissionIntro(): String
}

/**
 * 공통화된 미션 자체를 추상화한 클래스
 * @param designation 칭호
 * @param intro 설명
 */
abstract class MissionCommon(
    open val designation: String,
    open val intro: String,
) : MissionComponent {
    override fun getMissionDesignation(): String = designation
    override fun getMissionIntro(): String = intro
}

/**
 * 공통화된 단일 미션의 걸음수 미션 구현체
 */
data class StepMission(
    override val designation: String,
    override val intro: String,
    val achieved: Int,
    val goal: Int,
) : MissionCommon(
    designation = designation,
    intro = intro,
) {
    override fun getMissionAchieved(): Int = achieved
    override fun getMissionGoal(): Int = goal
}

/**
 * 공통화된 복합 미션의 구현체
 * @param getMissionDesignation 칭호
 * @param getMissionIntro 설명
 * @param missions 복합 미션을 구성하는 단일 미션들
 */
data class MissionComposite(
    override val designation: String,
    override val intro: String,
    private val missions: List<MissionFigure> = emptyList(),
) : MissionCommon(
    designation = designation,
    intro = intro,
) {

    /**
     * 여러 단일 미션들의 수치값들을 합하여 하나의 복합체로써의 합계된 수치(달성도, 목표)를 표현하기 위해 분수(Fraction) 을 이용
     */
    private val fraction = missions.map { mission ->
        Fraction(
            son = mission.getMissionAchieved(),
            mother = mission.getMissionGoal()
        )
    }.sumOrNull() ?: Fraction(son = 0, mother = 0)

    override fun getMissionAchieved(): Int = fraction.son
    override fun getMissionGoal(): Int = fraction.mother
}

data class MissionList(
    val title : String,
    @DrawableRes val icon : Int,
    val mode : MissionMode,
    val list : List<MissionCommon>
)

enum class MissionMode {
    repeat,time
}