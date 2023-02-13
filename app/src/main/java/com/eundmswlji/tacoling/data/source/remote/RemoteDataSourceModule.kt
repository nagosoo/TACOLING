package com.eundmswlji.tacoling.data.source.remote

import com.eundmswlji.tacoling.data.source.remote.address.AddressDataSourceImpl
import com.eundmswlji.tacoling.data.source.remote.address.AddressDatasource
import com.eundmswlji.tacoling.data.source.remote.shop.ShopDataSource
import com.eundmswlji.tacoling.data.source.remote.shop.ShopDataSourceImpl
import com.eundmswlji.tacoling.data.source.remote.user.UserRemoteDataSource
import com.eundmswlji.tacoling.data.source.remote.user.UserRemoteDataSourceImpl
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class RemoteDataSourceModule {
    @Binds
    @Singleton
    abstract fun bindAddressDataSource(addressDataSourceImpl: AddressDataSourceImpl): AddressDatasource

    @Binds
    @Singleton
    abstract fun bindUserDataSource(userRemoteDataSourceImpl: UserRemoteDataSourceImpl): UserRemoteDataSource

    @Binds
    @Singleton
    abstract fun bindsShopDataSource(shopDataSourceImpl: ShopDataSourceImpl): ShopDataSource
}
