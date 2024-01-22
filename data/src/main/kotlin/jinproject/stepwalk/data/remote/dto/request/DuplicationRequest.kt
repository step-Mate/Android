package jinproject.stepwalk.data.remote.dto.request

import com.google.gson.annotations.SerializedName

data class DuplicationRequest(
    @SerializedName("userId") val id : String
)
