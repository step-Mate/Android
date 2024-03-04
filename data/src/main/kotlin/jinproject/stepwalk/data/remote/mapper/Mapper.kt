package jinproject.stepwalk.data.remote.mapper

import jinproject.stepwalk.data.remote.dto.request.SignUpRequest
import jinproject.stepwalk.domain.model.SignUpData

fun SignUpData.toSignUpRequest() = SignUpRequest(
    userId = id,
    password = password,
    nickname = nickname,
    email = email,
    age = age,
    height = height,
    weight = weight
)
