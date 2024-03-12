package com.stepmate.data.remote.mapper

import com.stepmate.data.remote.dto.request.SignUpRequest
import com.stepmate.domain.model.SignUpData

fun SignUpData.toSignUpRequest() = SignUpRequest(
    userId = id,
    password = password,
    nickname = nickname,
    email = email,
    age = age,
    height = height,
    weight = weight
)