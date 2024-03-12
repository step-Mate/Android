package com.stepmate.data.remote.dto.request

import com.stepmate.domain.model.BodyData

data class BodyRequest(
    val age: Int,
    val height: Int,
    val weight: Int
)

internal fun BodyData.toBodyRequest() = BodyRequest(
    age = age,
    height = height,
    weight = weight
)