package jinproject.stepwalk.domain.model.exception

/**
 * HttpException 의 커스텀 클래스
 */
class StepMateHttpException(
    message: String,
    val code: Int,
) : Exception(message)