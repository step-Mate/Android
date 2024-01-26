package jinproject.stepwalk.data.di

import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import jinproject.stepwalk.data.local.datasource.CurrentAuthDataSource
import jinproject.stepwalk.data.local.datasource.UserDataSource
import jinproject.stepwalk.data.local.datasource.impl.CurrentAuthDataSourceImpl
import jinproject.stepwalk.data.local.datasource.impl.UserDataSourceImpl
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
internal abstract class DataSourceModule {

    @Singleton
    @Binds
    abstract fun bindsUserDataSource(userDataSourceImpl: UserDataSourceImpl) : UserDataSource

    @Singleton
    @Binds
    abstract fun bindsCurrentAuthDataSource(currentAuthDataSourceImpl: CurrentAuthDataSourceImpl) : CurrentAuthDataSource
}