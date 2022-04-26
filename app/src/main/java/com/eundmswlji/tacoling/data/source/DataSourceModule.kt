package com.eundmswlji.tacoling.data.source

import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ActivityRetainedComponent

@Module
@InstallIn(ActivityRetainedComponent::class)
abstract class DataSourceModule {
    @Binds
    abstract fun bindJusoDataSource(jusoDataSourceImpl: JusoDataSourceImpl): JusoDatasource
}
