package jinproject.stepwalk.home

import io.kotest.core.spec.style.BehaviorSpec
import io.kotest.matchers.shouldBe
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import jinproject.stepwalk.domain.model.StepData
import jinproject.stepwalk.domain.usecase.step.SetUserDayStepUseCase
import jinproject.stepwalk.home.service.StepSensorViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.TestDispatcher
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.setMain

@OptIn(ExperimentalCoroutinesApi::class)
internal abstract class StepSensorViewModelTest: BehaviorSpec({
    val testDispatcher: TestDispatcher = UnconfinedTestDispatcher()
    Dispatchers.setMain(testDispatcher)
}) {

    val setUserDayStepUseCase: SetUserDayStepUseCase = mockk(relaxed = true)
    val healthConnector: HealthConnector = mockk(relaxed = true)

    val viewModel: StepSensorViewModel = StepSensorViewModel(
        setUserDayStepUseCase = setUserDayStepUseCase,
        healthConnector = healthConnector,
    )
}

internal class ScenarioOnFirstInstall: StepSensorViewModelTest() {

    init {
        given("앱을 처음 설치하고") {

            `when`("걸음수 합계 센서의 값이 100일 때") {
                val sensorStep = 100L

                viewModel.onSensorChanged(sensorStep)

                then("어제 값에 센서값이 저장 된다.") {
                    viewModel.step.value.yesterday shouldBe sensorStep
                }
            }
        }
    }
}

internal class ScenarioAfterInstall: StepSensorViewModelTest() {
    init {
        given("앱 설치 이후") {
            val todayStep = 100L

            val stepData = StepData(
                current = todayStep,
                last = 50L,
                yesterday = 1200L,
                isReboot = false,
                stepAfterReboot = 0L
            )

            `when`("디바이스 리부팅을 한다면") {
                val sensorStep = 0L

                viewModel.onSensorChanged(sensorStep)

                then("어제값은 0이 된다.") {
                    viewModel.step.value.yesterday shouldBe 0L
                }

                then("저장된 오늘값은 유지된다.") {
                    viewModel.step.value.current shouldBe todayStep
                }
            }
        }
    }
}

internal class ScenarioAfterInstall2: StepSensorViewModelTest() {
    init {
        given("앱 설치 이후") {
            val todayStep = 100L
            val yesterdayStep = 1200L

            val stepData = StepData(
                current = todayStep,
                last = 50L,
                yesterday = yesterdayStep,
                isReboot = false,
                stepAfterReboot = 0L
            )

            `when`("하루가 지났을 때") {
                val verifyStepData: StepData = stepData.copy(
                    yesterday = stepData.current + stepData.yesterday,
                    last = 0L,
                )


                val sensorStep = todayStep + yesterdayStep + 1L

                viewModel.onSensorChanged(sensorStep)

                then("전날의 센서값이 다음날의 어제값이 된다.") {

                }
            }
        }
    }
}

internal class ScenarioReInstall: StepSensorViewModelTest() {
    init {
        val todayStep = 100L
        val yesterdayStep = 1200L

        val stepDataReInstall = StepData.getInitValues()

        given("걷고 나서 삭제한 뒤") {

            `when`("재설치 했을 때") {
                val sensorStep = todayStep + yesterdayStep

                coEvery { healthConnector.getTodayTotalStep() } returns todayStep

                viewModel.onSensorChanged(sensorStep)

                then("헬스커넥트로 부터 값을 가져온다.") {
                    coVerify(exactly = 1) {
                        healthConnector.getTodayTotalStep()
                    }
                }

                then("헬스커넥트로 부터 얻은 걸음수가 오늘 걸음수가 된다.") {

                }
            }
        }
    }
}