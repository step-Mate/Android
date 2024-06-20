package com.stepmate.home

import com.stepmate.domain.model.StepData
import com.stepmate.domain.usecase.auth.CheckHasTokenUseCase
import com.stepmate.domain.usecase.step.ManageStepUseCase
import com.stepmate.domain.usecase.step.SetUserDayStepUseCase
import com.stepmate.home.service.StepSensorViewModel
import io.kotest.core.spec.style.BehaviorSpec
import io.kotest.matchers.shouldBe
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.test.TestDispatcher
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.setMain

class ManageStepUseCaseFake() : ManageStepUseCase {
    var stepData = StepData.getInitValues()

    override suspend fun setTodayStep(todayStep: Long) {
        stepData = stepData.copy(current = todayStep)
    }

    override fun getTodayStep(): Flow<Long> = flow {
        emit(stepData.current)
    }

    override suspend fun setYesterdayStep(step: Long) {
        stepData = stepData.copy(yesterday = step)
    }

    override fun getYesterdayStep(): Flow<Long> = flow {
        emit(stepData.yesterday)
    }

    override fun getMissedTodayStepAfterReboot(): Flow<Long> = flow {
        emit(stepData.missedTodayStepAfterReboot)
    }

    override suspend fun setMissedTodayStepAfterReboot(step: Long) {
        stepData = stepData.copy(missedTodayStepAfterReboot = step)
    }

}

@OptIn(ExperimentalCoroutinesApi::class)
internal abstract class StepSensorViewModelTest : BehaviorSpec({
    val testDispatcher: TestDispatcher = UnconfinedTestDispatcher()
    Dispatchers.setMain(testDispatcher)
}) {

    val setUserDayStepUseCase: SetUserDayStepUseCase = mockk(relaxed = true)
    val healthConnector: HealthConnector = mockk(relaxed = true)
    val hasTokenUseCase: CheckHasTokenUseCase = mockk(relaxed = true)
    val manageStepUseCase: ManageStepUseCase = ManageStepUseCaseFake()

    val viewModel: StepSensorViewModel = StepSensorViewModel(
        setUserDayStepUseCase = setUserDayStepUseCase,
        healthConnector = healthConnector,
        manageStepUseCase = manageStepUseCase,
        resetMissionTimeUseCases = mockk(relaxed = true),
        checkHasTokenUseCase = hasTokenUseCase,
    )

    var stepBySensor = 0L

    suspend fun sensorCallBack(
        isCreated: Boolean,
        isReboot: Boolean,
        isDestroyedBySystem: Boolean,
    ) {
        if (isCreated) {
            if (!isReboot && !isDestroyedBySystem) {
                viewModel.initStepData(stepBySensor)
            }

            viewModel.onRebootDevice(isReboot)

            if (isDestroyedBySystem)
                viewModel.addMissedStepDestroyedBySystem()
        }

        viewModel.onSensorChanged(
            stepBySensor = stepBySensor,
        )
    }

    suspend fun testWalking(walked: Int) {
        repeat(walked) {
            viewModel.onSensorChanged(stepBySensor = ++stepBySensor)
        }
    }
}

internal class ScenarioOnFirstInstall : StepSensorViewModelTest() {

    init {
        given("앱을 처음 설치하고") {
            coEvery { hasTokenUseCase() } returns flow { emit(false) }

            coEvery { healthConnector.getTodayTotalStep() } returns 0L

            `when`("걸음수 센서의 값이 100일 때") {
                val step = 100L
                stepBySensor = step

                sensorCallBack(
                    isCreated = true,
                    isReboot = false,
                    isDestroyedBySystem = false,
                )

                and("100 걸음을 걸었다면") {
                    val walked = 100

                    testWalking(walked)

                    then("어제 값에 센서값이 저장 된다.") {
                        viewModel.step.value.yesterday shouldBe step
                    }

                    then("오늘 걸음수는 100 이다.") {
                        viewModel.step.value.current shouldBe walked
                    }

                    then("서비스 실행 이전에 저장되지 않은 걸음수는 0 이다.") {
                        viewModel.step.value.missedTodayStepAfterReboot shouldBe 0L
                    }

                    then("시스템에 의해 종료되지 않았으므로 last 는 0 이다.") {
                        viewModel.step.value.last shouldBe 0L
                    }
                }
            }
        }
    }
}

internal class ScenarioAfterInstallAndKilledBySystem : StepSensorViewModelTest() {
    init {
        given("앱 실행 이후") {
            coEvery { hasTokenUseCase() } returns flow { emit(false) }

            coEvery { healthConnector.getTodayTotalStep() } returns 0L

            val isCreated = true

            and("어제 걸음수는 1000, 현재 센서값은 1000 이고, 100걸음을 걷고") {
                val yesterdayStep = 1000L
                stepBySensor = yesterdayStep
                val walked = 100

                sensorCallBack(
                    isCreated = isCreated,
                    isReboot = false,
                    isDestroyedBySystem = false,
                )

                testWalking(walked)

                xand("걸음수가 저장이 된 후") {
                    viewModel.storeStepDataBeforeDestroyByUser()
                    delay(10L)

                    coEvery { healthConnector.getTodayTotalStep() } returns 100L

                    `when`("서비스가 시스템에 의해 종료되었을 때") {
                        sensorCallBack(
                            isCreated = true,
                            isReboot = false,
                            isDestroyedBySystem = true
                        )

                        and("100걸음을 더 걸었다면") {
                            testWalking(walked)

                            then("어제 값은 1000, 오늘값은 200, last 는 100, missedTodayStepAfterReboot 은 0 이다.") {
                                val step = viewModel.step.value
                                step.yesterday shouldBe 1000L
                                step.current shouldBe 200L
                                step.last shouldBe 100L
                                step.missedTodayStepAfterReboot shouldBe 0L
                            }
                        }
                    }
                }

                and("걸음수가 저장이 되지 않은 후") {
                    `when`("서비스가 시스템에 의해 종료되었을 때") {
                        sensorCallBack(
                            isCreated = true,
                            isReboot = false,
                            isDestroyedBySystem = true
                        )

                        and("100걸음을 더 걸었다면") {
                            testWalking(walked)

                            then("어제 값은 1000, 오늘값은 200, last 는 100, missedTodayStepAfterReboot 은 0 이다.") {
                                val step = viewModel.step.value
                                step.yesterday shouldBe 1000L
                                step.current shouldBe 200L
                                step.last shouldBe 0L
                                step.missedTodayStepAfterReboot shouldBe 0L
                            }
                        }
                    }
                }
            }
        }
    }
}