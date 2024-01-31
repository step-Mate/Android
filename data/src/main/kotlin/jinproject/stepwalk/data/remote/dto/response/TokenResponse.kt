package jinproject.stepwalk.data.remote.dto.response

import com.google.gson.annotations.SerializedName

data class Token(
    @SerializedName("refreshToken") val refreshToken : String,
    @SerializedName("accessToken") val accessToken : String
)

data class AccessToken(
    @SerializedName("accessToken") val accessToken : String
)