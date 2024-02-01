package jinproject.stepwalk.domain.model

interface MissionFigure {
    fun getMissionAchieved(): Int
    fun getMissionGoal(): Int
    fun getMissionProgress() = (getMissionAchieved().toFloat() / getMissionGoal().toFloat()).coerceAtMost(1f)
}

abstract class MissionLeaf(
    open val achieved: Int,
    open val goal: Int,
) : MissionFigure {
    override fun getMissionAchieved(): Int = achieved
    override fun getMissionGoal(): Int = goal
}

data class StepLeafMission(
    override val achieved: Int,
    override val goal: Int,
): MissionLeaf(achieved = achieved, goal = goal)

data class CalorieLeafMission(
    override val achieved: Int,
    override val goal: Int,
): MissionLeaf(achieved = achieved, goal = goal)

interface MissionComponent: MissionFigure {
    fun getDesignation(): String
    fun getIntro(): String
}

data class MissionCommon(
    val designation: String,
    val intro: String,
)

data class StepMission(
    val mission: MissionCommon,
    val achieved: Int,
    val goal: Int,
): MissionComponent {
    override fun getMissionAchieved(): Int = achieved
    override fun getMissionGoal(): Int = goal
    override fun getDesignation(): String = mission.designation
    override fun getIntro(): String = mission.intro
}

data class MissionComposite(
    val mission: MissionCommon,
    private val missions: List<MissionFigure> = emptyList(),
) : MissionComponent {

    private val fraction = missions.map { mission ->
        Fraction(
            son = mission.getMissionAchieved(),
            mother = mission.getMissionGoal()
        )
    }.sum()

    override fun getMissionAchieved(): Int = fraction.son
    override fun getMissionGoal(): Int = fraction.mother
    override fun getDesignation(): String = mission.designation
    override fun getIntro(): String = mission.intro
}