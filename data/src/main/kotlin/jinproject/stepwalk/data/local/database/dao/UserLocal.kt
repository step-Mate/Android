package jinproject.stepwalk.data.local.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import jinproject.stepwalk.data.local.database.entity.User
import kotlinx.coroutines.flow.Flow

@Dao
interface UserLocal {
    @Insert(onConflict = OnConflictStrategy.ABORT)
    suspend fun setUserData(user: User)

    @Update(onConflict = OnConflictStrategy.REPLACE)
    suspend fun updateUserData(user: User)

    @Query("update User set refreshToken = :token where id = :id")
    suspend fun setRefreshToken(id : String, token : String)

    @Query("select * from User where id = :id")
    fun getUserData(id: String) : Flow<List<User>>

}