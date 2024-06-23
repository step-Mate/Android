package com.stepmate.home

import com.stepmate.domain.usecase.auth.CheckHasTokenUseCase
import com.stepmate.domain.usecase.step.ManageStepUseCase
import com.stepmate.domain.usecase.step.SetUserDayStepUseCase
import com.stepmate.home.fake.ManageStepUseCaseFake
import com.stepmate.home.service.StepSensorViewModel
import io.kotest.core.spec.style.BehaviorSpec
import io.kotest.matchers.shouldBe
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.test.TestDispatcher
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.setMain
import java.time.Instant

@OptIn(ExperimentalCoroutinesApi::class)
internal abstract class StepSensorViewModelTest : BehaviorSpec({
    val testDispatcher: TestDispatcher = UnconfinedTestDispatcher()
    Dispatchers.setMain(testDispatcher)
}) {

    val setUserDayStepUseCase: SetUserDayStepUseCase = mockk(relaxed = true)
    val healthConnector: HealthConnector = mockk(relaxed = true)
    val hasTokenUseCase: CheckHasTokenUseCase = mockk(relaxed = true)
    val manageStepUseCase: ManageStepUseCase = ManageStepUseCaseFake()

    var viewModel: StepSensorViewModel = StepSensorViewModel(
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
            viewModel = StepSensorViewModel(
                setUserDayStepUseCase = setUserDayStepUseCase,
                healthConnector = healthConnector,
                manageStepUseCase = manageStepUseCase,
                resetMissionTimeUseCases = mockk(relaxed = true),
                checkHasTokenUseCase = hasTokenUseCase,
            )

            if (!isReboot && !isDestroyedBySystem) {
                viewModel.initStepData(stepBySensor)
            }

            viewModel.onRebootDevice(isReboot)

            viewModel.initLastValueByHealthConnect()
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

                    then("앱 최초 실행시, 헬스커넥트에 저장된 걸음수가 없기 때문에 last 는 0 이다.") {
                        viewModel.step.value.last shouldBe 0L
                    }
                }
            }
        }
    }
}

internal class ScenarioReExecuteServiceAfterInstall : StepSensorViewModelTest() {

    init {
        given("앱 설치 이후") {
            coEvery { hasTokenUseCase() } returns flow { emit(false) }

            `when`("걸음수 센서의 값이 100일 때") {
                val step = 100L
                stepBySensor = step

                and("서비스 재실행 이전에 헬스커넥트에 저장되지 않은 걸음수가 50 일 때 (타이머 완료 이전에 서비스 종료)") {
                    val notAddedStep = 50L
                    coEvery { healthConnector.getTodayTotalStep() } returns 0L
                    coEvery { healthConnector.getSpecificDayTotalStep(any()) } returns 0L
                    manageStepUseCase.setTodayStep(notAddedStep)

                    and("서비스를 당일에 껏다가 재실행 하는 상황일 때") {
                        manageStepUseCase.setLatestEndTime(Instant.now().epochSecond)

                        viewModel = StepSensorViewModel(
                            setUserDayStepUseCase = setUserDayStepUseCase,
                            healthConnector = healthConnector,
                            manageStepUseCase = manageStepUseCase,
                            resetMissionTimeUseCases = mockk(relaxed = true),
                            checkHasTokenUseCase = hasTokenUseCase,
                        )

                        viewModel.initStepData(stepBySensor)

                        coEvery { healthConnector.getTodayTotalStep() } returns 50L

                        viewModel.onRebootDevice(false)

                        viewModel.initLastValueByHealthConnect()

                        and("100 걸음을 걸었다면") {
                            val walked = 100

                            testWalking(walked)

                            then("어제 값에 센서값이 저장 된다.") {
                                viewModel.step.value.yesterday shouldBe step
                            }

                            then("오늘 걸음수는 150 이다.") {
                                viewModel.step.value.current shouldBe walked + notAddedStep
                            }

                            then("서비스 실행 이전에 저장되지 않은 걸음수는 50 이다.") {
                                viewModel.step.value.missedTodayStepAfterReboot shouldBe notAddedStep
                            }

                            then("앱 재실행 이전에 저장되지 않은 걸음수가 헬스커넥트에 저장되므로 last 는 50 이다.") {
                                viewModel.step.value.last shouldBe 50L
                            }
                        }
                    }
                }
            }
        }
    }
}

internal class ScenarioWhileWalkingKilledBySystem : StepSensorViewModelTest() {
    init {
        given("앱 실행 이후, 로그인은 하지 않은 상태에서") {
            coEvery { hasTokenUseCase() } returns flow { emit(false) }

            and("센서값은 100, 어제값은 50, 오늘값은 50, 헬스커넥트에 오늘 걸음수가 저장된 상태에서") {
                stepBySensor = 100L
                manageStepUseCase.setYesterdayStep(50L)
                manageStepUseCase.setTodayStep(50L)
                coEvery { healthConnector.getTodayTotalStep() } returns 50L

                `when`("걷는 중에 시스템에 의해 서비스가 종료되었을 때") {
                    val walked = 1

                    testWalking(walked)

                    sensorCallBack(
                        isCreated = true,
                        isReboot = false,
                        isDestroyedBySystem = true,
                    )

                    testWalking(walked)

                    val stepData = viewModel.step.value
                    then("오늘 걸음수는 52 이다.") {
                        stepData.current shouldBe 52L
                    }

                    then("어제 걸음수는 변화가 없어야 한다.") {
                        stepData.yesterday shouldBe 50L
                    }

                    then("타이머가 완료될 때 헬스커넥트에 저장될 걸음수는 2 이다.") {
                        stepData.current - stepData.last shouldBe 2L
                    }

                    viewModel.storeStepDataBeforeDestroyByUser()
                    delay(10L)

                    then("타이머가 완료된 후 last의 값은 current 의 값과 같다") {
                        viewModel.step.value.last shouldBe viewModel.step.value.current
                    }
                }
            }
        }
    }
}

internal class ScenarioStoredStepAndKilledBySystem : StepSensorViewModelTest() {
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

                and("걸음수가 저장이 된 후") {
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
            }
        }
    }
}

internal class ScenarioNotStoredStepAndKilledBySystem : StepSensorViewModelTest() {
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

internal class ScenarioRebootDevice : StepSensorViewModelTest() {
    init {
        given("앱 실행 이후, 로그인은 하지 않은 상태에서") {
            coEvery { hasTokenUseCase() } returns flow { emit(false) }

            and("센서값은 100, 어제값은 50, 오늘값은 50, 헬스커넥트에 오늘 걸음수가 저장된 상태에서") {
                stepBySensor = 100L
                manageStepUseCase.setYesterdayStep(50L)
                manageStepUseCase.setTodayStep(50L)
                coEvery { healthConnector.getTodayTotalStep() } returns 50L

                `when`("디바이스가 리부팅되었을 때") {
                    stepBySensor = 0
                    sensorCallBack(
                        isCreated = true,
                        isReboot = true,
                        isDestroyedBySystem = false,
                    )

                    and("1 걸음 걸었다면") {
                        val walked = 1
                        testWalking(walked)

                        val stepData = viewModel.step.value
                        then("리부팅 이전에 저장된 걸음수는 50 이다.") {
                            stepData.missedTodayStepAfterReboot shouldBe 50L
                        }

                        then("오늘 걸음수는 51 이다.") {
                            stepData.current shouldBe 51L
                        }

                        then("어제 걸음수는 0 이다.") {
                            stepData.yesterday shouldBe 0L
                        }

                        then("타이머가 완료될 때 헬스커넥트에 저장될 걸음수는 1 이다.") {
                            stepData.current - stepData.last shouldBe 1L
                        }

                        viewModel.storeStepDataBeforeDestroyByUser()
                        delay(10L)

                        then("타이머가 완료된 후 last의 값은 current 의 값과 같다") {
                            viewModel.step.value.last shouldBe viewModel.step.value.current
                        }
                    }
                }
            }
        }
    }
}