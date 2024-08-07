package com.stepmate.domain.model.exception

/**
 * HttpException 의 커스텀 클래스
 */
class StepMateHttpException(
    override val message: String,
    val code: Int,
) : Exception(message)