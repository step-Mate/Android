package jinproject.stepwalk.data.remote.dto.request

import com.google.gson.annotations.SerializedName

data class SignUpRequest(
    @SerializedName("userId") val id : String,
    @SerializedName("password") val password : String,
    @SerializedName("nickname") val nickname : String,
    @SerializedName("email") val email : String,
    @SerializedName("age") val age : Int,
    @SerializedName("height") val height : Int,
    @SerializedName("weight") val weight : Int,
)
