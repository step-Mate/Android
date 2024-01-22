package jinproject.stepwalk.data.remote.dto.request

import com.google.gson.annotations.SerializedName

data class AccountRequest(
    @SerializedName("userId") val id : String,
    @SerializedName("password") val password : String
)
