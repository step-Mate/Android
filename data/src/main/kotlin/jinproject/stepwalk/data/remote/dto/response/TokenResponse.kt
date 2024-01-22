package jinproject.stepwalk.data.remote.dto.response

import com.google.gson.annotations.SerializedName

data class TokenResponse(
    @SerializedName("code") val code : Int,
    @SerializedName("message") val message : String,
    @SerializedName("result") val result : Token
)

data class Token(
    @SerializedName("accessToken") val token : String
)