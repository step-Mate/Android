package com.stepmate.home

import io.kotest.core.spec.style.BehaviorSpec
import io.kotest.matchers.shouldBe
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import com.stepmate.domain.model.StepData
import com.stepmate.domain.usecase.auth.CheckHasTokenUseCase
import com.stepmate.domain.usecase.step.ManageStepUseCase
import com.stepmate.domain.usecase.step.SetUserDayStepUseCase
import com.stepmate.home.service.StepSensorViewModel
import io.mockk.every
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.test.TestDispatcher
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.setMain

@OptIn(ExperimentalCoroutinesApi::class)
internal abstract class StepSensorViewModelTest : BehaviorSpec({
    val testDispatcher: TestDispatcher = UnconfinedTestDispatcher()
    Dispatchers.setMain(testDispatcher)
}) {

    val setUserDayStepUseCase: SetUserDayStepUseCase = mockk(relaxed = true)
    val healthConnector: HealthConnector = mockk(relaxed = true)
    val hasTokenUseCase: CheckHasTokenUseCase = mockk(relaxed = true)
    val manageStepUseCase: ManageStepUseCase = mockk(relaxed = true)

    val viewModel: StepSensorViewModel = StepSensorViewModel(
        setUserDayStepUseCase = setUserDayStepUseCase,
        healthConnector = healthConnector,
        manageStepUseCase = manageStepUseCase,
        resetMissionTimeUseCases = mockk(relaxed = true),
        checkHasTokenUseCase = hasTokenUseCase,
    )
}

internal class ScenarioOnFirstInstall : StepSensorViewModelTest() {

    init {
        given("앱을 처음 설치하고") {
            coEvery { hasTokenUseCase() } returns flow { emit(false) }

            coEvery { healthConnector.getTodayTotalStep() } returns 0L
            coEvery { manageStepUseCase.getTodayStep() } returns flow { emit(0L) }

            viewModel.initStep()

            `when`("걸음수 합계 센서의 값이 100일 때") {
                val step = 100L
                var sensorStep = step

                viewModel.onSensorChanged(stepBySensor = sensorStep, isCreated = true)
                viewModel.initYesterdayStep()

                and("100 걸음을 걸었다면") {
                    val walked = 100

                    for (i in 0..walked) {
                        viewModel.onSensorChanged(stepBySensor = sensorStep++, isCreated = false)
                    }

                    then("어제 값에 센서값이 저장 된다.") {
                        viewModel.step.value.yesterday shouldBe step
                    }

                    then("오늘 걸음수는 200 이다.") {
                        viewModel.step.value.current shouldBe walked
                    }

                    then("타이머내에 저장되지 않은 걸음수는 0 이다.") {
                        viewModel.step.value.stepAfterReboot shouldBe 0L
                    }
                }
            }
        }
    }
}

internal class ScenarioAfterInstallAndKilledBySystem : StepSensorViewModelTest() {
    private var stepBySensor = 0L

    init {
        given("앱 실행 이후") {
            coEvery { hasTokenUseCase() } returns flow { emit(false) }

            coEvery { healthConnector.getTodayTotalStep() } returns 0L
            every { manageStepUseCase.getTodayStep() } returns flow { emit(0L) }

            var isCreated = true
            viewModel.initStep()

            and("어제 걸음수는 1000 이고, 100걸음을 걷고") {
                val yesterdayStep = 1000L

                coEvery { manageStepUseCase.getYesterdayStep() } returns flow { emit(yesterdayStep) }

                viewModel.onSensorChanged(stepBySensor = yesterdayStep, isCreated = isCreated)
                viewModel.initYesterdayStep()

                isCreated = false

                stepBySensor = yesterdayStep
                addStepByWalk(100)

                and("걸음수가 저장이 된 후") {
                    delay(1100L)
                    coEvery { healthConnector.getTodayTotalStep() } returns 100L
                    every { manageStepUseCase.getTodayStep() } returns flow { emit(100L) }

                    `when`("서비스가 시스템에 의해 종료되었을 때") {
                        killedBySystem()

                        and("100걸음을 더 걸었다면") {
                            addStepByWalk(100)

                            then("어제 값은 1000, 오늘값은 200") {
                                val step = viewModel.step.value
                                step.yesterday shouldBe 1000L
                                step.current shouldBe 200L
                                step.stepAfterReboot shouldBe 0L
                            }
                        }
                    }
                }

                xand("걸음수가 저장이 되지 않은 후") {
                    `when`("서비스가 시스템에 의해 종료되었을 때") {
                        killedBySystem()

                        and("100걸음을 더 걸었다면") {
                            addStepByWalk(100)

                            then("어제 값은 1000, 오늘값은 200") {
                                val step = viewModel.step.value
                                step.yesterday shouldBe 1000L
                                step.current shouldBe 200L
                            }
                        }
                    }
                }
            }
        }
    }

    private suspend fun killedBySystem(isCreated: Boolean = true) {
        viewModel.initStep()
        viewModel.onSensorChanged(stepBySensor = stepBySensor, isCreated = isCreated)
        viewModel.getYesterdayStepIfKilledBySystem()
    }

    private suspend fun addStepByWalk(walked: Int) {
        for (i in 1..walked) {
            viewModel.onSensorChanged(stepBySensor = ++stepBySensor, isCreated = false)
        }
    }
}