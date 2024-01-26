package jinproject.stepwalk.data.local.database

import androidx.room.Database
import androidx.room.RoomDatabase
import jinproject.stepwalk.data.local.database.dao.UserLocal
import jinproject.stepwalk.data.local.database.entity.User

@Database(entities = [User::class], version = 1, exportSchema = true)
abstract class Database : RoomDatabase() {
    abstract fun userDao() : UserLocal
}