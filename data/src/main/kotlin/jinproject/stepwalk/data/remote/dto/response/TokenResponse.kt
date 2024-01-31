package jinproject.stepwalk.data.remote.dto.response

import com.google.gson.annotations.SerializedName

data class Token(
    @SerializedName("accessToken") val token : String
)
data class JwtToken(
    @SerializedName("jwt") val token : String
)
