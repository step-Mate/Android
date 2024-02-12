package jinproject.stepwalk.data.local.database.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class Mission(
    val title : String,
    @PrimaryKey val designation : String,
    val intro : String
)
