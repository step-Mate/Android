package jinproject.stepwalk.data.database.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class MissionTime(
    @PrimaryKey(autoGenerate = true) val id : Int,
    val title : String,
    val content : String,
    val target : Int,
    val reward : Boolean,
    val startDay : Long,
    val endDay : Long
)
