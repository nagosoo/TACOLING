package com.eundmswlji.tacoling.data.source.local

import com.eundmswlji.tacoling.data.source.local.user.UserLocalDataSource
import com.eundmswlji.tacoling.data.source.local.user.UserLocalDataSourceImpl
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton


@Module
@InstallIn(SingletonComponent::class)
abstract class LocalDataSourceModule {

    @Singleton
    @Binds
    abstract fun bindsUserLocalDataSource(userLocalDataSourceImpl: UserLocalDataSourceImpl): UserLocalDataSource

}