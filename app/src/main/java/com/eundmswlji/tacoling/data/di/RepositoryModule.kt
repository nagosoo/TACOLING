package com.eundmswlji.tacoling.data.di

import com.eundmswlji.tacoling.data.repository.address.AddressRepositoryImpl
import com.eundmswlji.tacoling.data.repository.shop.ShopRepositoryImpl
import com.eundmswlji.tacoling.data.repository.user.UserRepositoryImpl
import com.eundmswlji.tacoling.domain.repository.AddressRepository
import com.eundmswlji.tacoling.domain.repository.ShopRepository
import com.eundmswlji.tacoling.domain.repository.UserRepository
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
