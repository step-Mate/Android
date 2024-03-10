package jinproject.stepwalk.data.remote.dto.response.user

import jinproject.stepwalk.domain.model.DesignationState

data class DesignationResponse(
    val designation: String
)

internal fun List<DesignationResponse>.toDesignationModel() = DesignationState(
    list = this.map { it.designation }.toList()
)
