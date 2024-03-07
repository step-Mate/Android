package jinproject.stepwalk.data.local.database.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class Mission(
    @PrimaryKey val designation: String,
    val title: String,
    val intro: String
)