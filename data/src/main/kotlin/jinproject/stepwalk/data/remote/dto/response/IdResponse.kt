package jinproject.stepwalk.data.remote.dto.response

import com.google.gson.annotations.SerializedName

data class IdResponse(
    @SerializedName("code") val code : Int,
    @SerializedName("message") val message : String,
    @SerializedName("result") val result : UserId
)

data class UserId(
    @SerializedName("userId") val id : String
)
