package jinproject.stepwalk.data.database.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class Mission(
    @PrimaryKey val title : String,
    val image : Int,
    val mode : Int
)
