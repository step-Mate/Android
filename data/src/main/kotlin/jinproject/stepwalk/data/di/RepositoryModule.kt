package jinproject.stepwalk.data.di

import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import jinproject.stepwalk.data.repositoryimpl.AuthRepositoryImpl
import jinproject.stepwalk.data.repositoryimpl.MissionRepositoryImpl
import jinproject.stepwalk.data.repositoryimpl.StepRepositoryImpl
import jinproject.stepwalk.domain.repository.AuthRepository
import jinproject.stepwalk.domain.repository.MissionRepository
import jinproject.stepwalk.domain.repository.StepRepository
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {

    @Singleton
    @Binds
    abstract fun bindsStepRepository(stepRepositoryImpl: StepRepositoryImpl): StepRepository

    @Singleton
    @Binds
    abstract fun bindsAuthRepository(authRepositoryImpl: AuthRepositoryImpl) : AuthRepository

    @Singleton
    @Binds
    abstract fun bindsMissionRepository(missionRepositoryImpl: MissionRepositoryImpl) : MissionRepository
}