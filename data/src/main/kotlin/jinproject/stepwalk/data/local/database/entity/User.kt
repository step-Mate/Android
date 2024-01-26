package jinproject.stepwalk.data.local.database.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class User(
    @PrimaryKey val id : String,
    val nickname : String,
    val email : String,
    val age : Int,
    val height : Int,
    val weight : Int,
    val refreshToken : String = ""
)
