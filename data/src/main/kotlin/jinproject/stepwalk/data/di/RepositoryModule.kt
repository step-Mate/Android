package jinproject.stepwalk.data.di

import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import jinproject.stepwalk.data.repositoryimpl.AuthRepositoryImpl
import jinproject.stepwalk.data.repositoryimpl.MissionRepositoryImpl
import jinproject.stepwalk.data.repositoryimpl.StepRepositoryImpl
import jinproject.stepwalk.data.repositoryImpl.RankRepositoryImpl
import jinproject.stepwalk.data.repositoryImpl.UserRepositoryImpl
import jinproject.stepwalk.domain.repository.AuthRepository
import jinproject.stepwalk.domain.repository.MissionRepository
import jinproject.stepwalk.domain.repository.RankRepository
import jinproject.stepwalk.domain.repository.StepRepository
import jinproject.stepwalk.domain.repository.UserRepository
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
internal abstract class RepositoryModule {

    @Singleton
    @Binds
    abstract fun bindsStepRepository(stepRepositoryImpl: StepRepositoryImpl): StepRepository

    @Singleton
    @Binds
    abstract fun bindsAuthRepository(authRepositoryImpl: AuthRepositoryImpl) : AuthRepository

    @Singleton
    @Binds
    abstract fun bindsMissionRepository(missionRepositoryImpl: MissionRepositoryImpl) : MissionRepository

    @Singleton
    @Binds
    abstract fun bindsRankRepository(rankRepositoryImpl: RankRepositoryImpl): RankRepository

    @Singleton
    @Binds
    abstract fun bindsUserRepository(userRepositoryImpl: UserRepositoryImpl): UserRepository
}