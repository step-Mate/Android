package jinproject.stepwalk.data.remote.mapper

import jinproject.stepwalk.data.local.database.entity.User
import jinproject.stepwalk.data.remote.dto.request.SignUpRequest
import jinproject.stepwalk.domain.model.UserData

fun UserData.toSignUpRequest() = SignUpRequest(
    id = id,
    password = password,
    nickname = nickname,
    email = email,
    age = age,
    height = height,
    weight = weight
)

fun UserData.toUser(token : String) = User(
    id = id,
    nickname = nickname,
    email = email,
    age = age,
    height = height,
    weight = weight,
    refreshToken = token
)

fun User.toUserData() = UserData(
    id = id,
    nickname = nickname,
    email = email,
    age = age,
    height = height,
    weight = weight
)