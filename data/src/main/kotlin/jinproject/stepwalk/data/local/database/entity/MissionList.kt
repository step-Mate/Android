package jinproject.stepwalk.data.local.database.entity

import androidx.room.Embedded
import androidx.room.Relation

data class MissionList(
    @Embedded val mission: Mission,
    @Relation(
        parentColumn = "designation",
        entityColumn = "designation"
    )
    val leaf: List<MissionLeaf>
)
