package jinproject.stepwalk.data.database

import androidx.room.Database
import androidx.room.RoomDatabase
import jinproject.stepwalk.data.database.entity.Mission
import jinproject.stepwalk.data.database.entity.MissionRepeat
import jinproject.stepwalk.data.database.entity.MissionTime

@Database(entities = [Mission::class,MissionRepeat::class,MissionTime::class], version = 1, exportSchema = true)
abstract class Database : RoomDatabase() {

}