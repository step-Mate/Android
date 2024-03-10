package jinproject.stepwalk.data.local.database

import androidx.room.Database
import androidx.room.RoomDatabase
import jinproject.stepwalk.data.local.database.dao.MissionLocal
import jinproject.stepwalk.data.local.database.entity.Mission
import jinproject.stepwalk.data.local.database.entity.MissionLeaf

@Database(entities = [Mission::class,MissionLeaf::class], version = 1, exportSchema = false)
abstract class Database : RoomDatabase() {
    abstract fun missionDao() : MissionLocal
}