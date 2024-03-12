package com.stepmate.data.local.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import com.stepmate.data.local.database.entity.Mission
import com.stepmate.data.local.database.entity.MissionLeaf
import com.stepmate.data.local.database.entity.MissionList
import com.stepmate.domain.model.mission.MissionType
import kotlinx.coroutines.flow.Flow

@Dao
interface MissionLocal {

    @Transaction
    @Query("SELECT * FROM mission")
    fun getAllMissionList(): Flow<List<MissionList>>

    @Transaction
    @Query("SELECT * FROM mission WHERE title = :title")
    fun getMissionList(title: String): Flow<List<MissionList>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun addMission(mission: Mission)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun addMissionLeaf(missionLeaf: MissionLeaf)

    @Query("UPDATE missionleaf SET achieved = :achieved WHERE achieved <= goal and type = :type")
    suspend fun updateMissionAchieved(type: MissionType, achieved: Int)

    @Query("SELECT MAX(achieved) FROM missionleaf WHERE type = :type")
    fun getMissionAchieved(type: MissionType): Flow<Int>

    @Query("DELETE FROM mission")
    suspend fun deleteMission()

}