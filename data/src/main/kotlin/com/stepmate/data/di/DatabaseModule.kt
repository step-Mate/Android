package com.stepmate.data.di

import android.content.Context
import androidx.room.Room
import com.stepmate.data.local.database.Database
import com.stepmate.data.local.database.dao.MissionLocal
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton


@Module
@InstallIn(SingletonComponent::class)
internal object DatabaseModule {

    @Provides
    @Singleton
    fun providesMissionDao(database: Database): MissionLocal {
        return database.missionDao()
    }

    @Provides
    @Singleton
    fun providesDatabase(@ApplicationContext context: Context): Database {
        return Room.databaseBuilder(context, Database::class.java, "myDatabase")
            .build()
    }

}