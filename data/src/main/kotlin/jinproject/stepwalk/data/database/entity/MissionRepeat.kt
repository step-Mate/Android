package jinproject.stepwalk.data.database.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class MissionRepeat(
    @PrimaryKey(autoGenerate = true) val id : Int,
    val title : String,
    val target : Int,
    val reward : Boolean
)