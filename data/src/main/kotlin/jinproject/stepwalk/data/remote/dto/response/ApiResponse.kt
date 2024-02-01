package jinproject.stepwalk.data.remote.dto.response

data class ApiResponse<T>(
    val code : Int,
    val message : String,
    val result : T?
)

data class ErrorResponse(
    val code : Int,
    val message : String
)