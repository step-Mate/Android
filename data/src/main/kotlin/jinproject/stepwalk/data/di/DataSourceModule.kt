package jinproject.stepwalk.data.di

import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import jinproject.stepwalk.data.local.datasource.BodyDataSource
import jinproject.stepwalk.data.local.datasource.CurrentAuthDataSource
import jinproject.stepwalk.data.local.datasource.impl.BodyDataSourceImpl
import jinproject.stepwalk.data.local.datasource.impl.CurrentAuthDataSourceImpl
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
internal abstract class DataSourceModule {

    @Singleton
    @Binds
    abstract fun bindsCurrentAuthDataSource(currentAuthDataSourceImpl: CurrentAuthDataSourceImpl) : CurrentAuthDataSource

    @Singleton
    @Binds
    abstract fun bindsBodyDataSource(bodyDataSourceImpl: BodyDataSourceImpl) : BodyDataSource
}