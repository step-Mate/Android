package com.stepmate.data.local.database

import androidx.room.Database
import androidx.room.RoomDatabase
import com.stepmate.data.local.database.dao.MissionLocal
import com.stepmate.data.local.database.entity.Mission
import com.stepmate.data.local.database.entity.MissionLeaf

@Database(entities = [Mission::class, MissionLeaf::class], version = 1, exportSchema = false)
abstract class Database : RoomDatabase() {
    abstract fun missionDao(): MissionLocal
}