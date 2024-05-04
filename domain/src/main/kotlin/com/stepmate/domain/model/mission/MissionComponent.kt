package com.stepmate.domain.model.mission

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
interface MissionCommon : MissionComponent {
    val designation: String
    val intro: String
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
) : MissionCommon {
    override fun getMissionAchieved(): Int = achieved
    override fun getMissionGoal(): Int = goal
    override fun getReward(): Int = goal / 1000
}

/**
 * 공통화된 단일 미션의 칼로리 미션 구현체
 */
data class CalorieMission(
    override val designation: String,
    override val intro: String,
    val achieved: Int,
    val goal: Int,
) : MissionCommon {
    override fun getMissionAchieved(): Int = achieved
    override fun getMissionGoal(): Int = goal
    override fun getReward(): Int = goal / 10
}