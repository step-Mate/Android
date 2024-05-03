package com.stepmate.data.di

import com.stepmate.data.repositoryImpl.AuthRepositoryImpl
import com.stepmate.data.repositoryImpl.MissionRepositoryImpl
import com.stepmate.data.repositoryImpl.RankRepositoryImpl
import com.stepmate.data.repositoryImpl.SettingsRepositoryImpl
import com.stepmate.data.repositoryImpl.StepRepositoryImpl
import com.stepmate.data.repositoryImpl.UserRepositoryImpl
import com.stepmate.domain.repository.AuthRepository
import com.stepmate.domain.repository.MissionRepository
import com.stepmate.domain.repository.RankRepository
import com.stepmate.domain.repository.SettingsRepository
import com.stepmate.domain.repository.StepRepository
import com.stepmate.domain.repository.UserRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
internal abstract class RepositoryModule {

    @Singleton
    @Binds
    abstract fun bindsAuthRepository(authRepositoryImpl: AuthRepositoryImpl): AuthRepository

    @Singleton
    @Binds
    abstract fun bindsRankRepository(rankRepositoryImpl: RankRepositoryImpl): RankRepository

    @Singleton
    @Binds
    abstract fun bindsUserRepository(userRepositoryImpl: UserRepositoryImpl): UserRepository

    @Singleton
    @Binds
    abstract fun bindsMissionRepository(missionRepositoryImpl: MissionRepositoryImpl): MissionRepository

    @Singleton
    @Binds
    abstract fun bindsStepRepository(stepRepositoryImpl: StepRepositoryImpl): StepRepository

    @Singleton
    @Binds
    abstract fun bindsSettingsRepository(settingsRepositoryImpl: SettingsRepositoryImpl): SettingsRepository

}