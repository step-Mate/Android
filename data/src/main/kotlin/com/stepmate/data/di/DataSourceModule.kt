package com.stepmate.data.di

import com.stepmate.data.local.datasource.BodyDataSource
import com.stepmate.data.local.datasource.CacheSettingsDataSource
import com.stepmate.data.local.datasource.CurrentAuthDataSource
import com.stepmate.data.local.datasource.impl.BodyDataSourceImpl
import com.stepmate.data.local.datasource.impl.CacheSettingsDataSourceImpl
import com.stepmate.data.local.datasource.impl.CurrentAuthDataSourceImpl
import com.stepmate.data.remote.dataSource.RemoteUserDataSource
import com.stepmate.data.remote.dataSource.impl.RemoteUserDataSourceImpl
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
internal abstract class DataSourceModule {

    @Singleton
    @Binds
    abstract fun bindsCurrentAuthDataSource(currentAuthDataSourceImpl: CurrentAuthDataSourceImpl): CurrentAuthDataSource

    @Singleton
    @Binds
    abstract fun bindsBodyDataSource(bodyDataSourceImpl: BodyDataSourceImpl): BodyDataSource

    @Singleton
    @Binds
    abstract fun bindsRemoteUserDataSource(userDataSourceImpl: RemoteUserDataSourceImpl): RemoteUserDataSource

    @Singleton
    @Binds
    abstract fun bindsCacheSettingsDataSource(settingsDataSourceImpl: CacheSettingsDataSourceImpl): CacheSettingsDataSource
}