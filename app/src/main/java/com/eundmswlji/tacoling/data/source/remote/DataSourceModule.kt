package com.eundmswlji.tacoling.data.source.remote

import com.eundmswlji.tacoling.data.source.remote.address.AddressDataSourceImpl
import com.eundmswlji.tacoling.data.source.remote.address.AddressDatasource
import com.eundmswlji.tacoling.data.source.remote.user.UserDataSource
import com.eundmswlji.tacoling.data.source.remote.user.UserDataSourceImpl
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ActivityRetainedComponent
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class DataSourceModule {
    @Binds
    @Singleton
    abstract fun bindAddressDataSource(addressDataSourceImpl: AddressDataSourceImpl): AddressDatasource

    @Binds
    @Singleton
    abstract fun bindUserDataSource(userDataSourceImpl: UserDataSourceImpl): UserDataSource
}
