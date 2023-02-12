package com.eundmswlji.tacoling.data.repository

import com.eundmswlji.tacoling.data.repository.address.AddressRepository
import com.eundmswlji.tacoling.data.repository.address.AddressRepositoryImpl
import com.eundmswlji.tacoling.data.repository.shop.ShopRepository
import com.eundmswlji.tacoling.data.repository.shop.ShopRepositoryImpl
import com.eundmswlji.tacoling.data.repository.user.UserRepository
import com.eundmswlji.tacoling.data.repository.user.UserRepositoryImpl
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {
    @Binds
    @Singleton
    abstract fun bindAddressRepository(addressRepositoryImpl: AddressRepositoryImpl): AddressRepository

    @Binds
    @Singleton
    abstract fun bindUserRepository(userRepositoryImpl: UserRepositoryImpl): UserRepository

    @Binds
    @Singleton
    abstract fun bindShopRepository(shopRepositoryImpl: ShopRepositoryImpl): ShopRepository
}
