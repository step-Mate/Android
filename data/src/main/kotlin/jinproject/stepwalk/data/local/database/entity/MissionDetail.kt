package jinproject.stepwalk.data.local.database.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    foreignKeys = [ForeignKey(
        entity = Mission::class,
        parentColumns = arrayOf("designation"),
        childColumns = arrayOf("designation"),
        onDelete = ForeignKey.CASCADE
    )],
    indices = [
        Index("designation")
    ]
)
data class MissionLeaf(
    @PrimaryKey(autoGenerate = true) val id: Int,
    val designation: String,
    val type: MissionType,
    val achieved: Int,
    val goal: Int,
)

enum class MissionType {
    Step, Calorie
}