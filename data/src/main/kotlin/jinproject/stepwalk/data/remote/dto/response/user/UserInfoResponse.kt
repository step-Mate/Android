package jinproject.stepwalk.data.remote.dto.response.user

import com.google.gson.annotations.SerializedName
import jinproject.stepwalk.domain.model.User

data class UserInfoResponse(
    @SerializedName("nickname") val name: String,
    val level: Int,
    @SerializedName("title") val designation: String?
)

internal fun UserInfoResponse.toUserModel() = User(
    name = name,
    character = "ic_anim_running_1.json",
    level = level,
    designation = designation ?: ""
)
