package jinproject.stepwalk.data.remote.dto.response

import com.google.gson.annotations.SerializedName

data class ApiResponse<T>(
    @SerializedName("code") val code : Int,
    @SerializedName("message") val message : String,
    @SerializedName("result") val result : T?
)

data class ErrorResponse(
    @SerializedName("code") val code : Int,
    @SerializedName("message") val message : String
)