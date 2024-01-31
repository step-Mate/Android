package jinproject.stepwalk.data.remote.dto.request

import com.google.gson.annotations.SerializedName

data class DuplicationIdRequest(
    @SerializedName("userId") val id : String
)

data class DuplicationNicknameRequest(
    @SerializedName("nickname") val nickname : String
)