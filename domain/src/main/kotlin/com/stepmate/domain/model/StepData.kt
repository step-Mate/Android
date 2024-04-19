package com.stepmate.domain.model

data class StepData(
    val current: Long,
    val last: Long,
    val yesterday: Long,
    val stepAfterReboot: Long,
) {
    /**
     * 오늘의 걸음수를 계산하여 가져오는 함수
     * @param stepBySensor 센서로 부터 측정된 전체 걸음수
     * @return 오늘의 걸음수
     */
    fun getTodayStep(
        stepBySensor: Long,
        isReCreated: Boolean
    ): StepData =
        when (isRebootDevice(stepBySensor)) {
            true -> {
                copy(
                    yesterday = 0L,
                )
            }

            false -> {
                val today = stepBySensor - yesterday + stepAfterReboot

                if (isReCreated)
                    copy(
                        yesterday = stepBySensor,
                    )
                else
                    copy(current = today)
            }
        }

    /**
     * 휴대폰 재부팅이 되었는지 확인하는 함수
     * @param stepBySensor 센서로 부터 측정된 전체 걸음수
     * @return 재부팅이면 true
     */
    private fun isRebootDevice(stepBySensor: Long): Boolean {
        return stepBySensor == 0L
    }

    companion object {
        fun getInitValues() = StepData(
            current = 0L,
            last = 0L,
            yesterday = 0L,
            stepAfterReboot = 0L
        )
    }
}