package jinproject.stepwalk.data.remote.dto.request

import jinproject.stepwalk.domain.model.BodyData

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