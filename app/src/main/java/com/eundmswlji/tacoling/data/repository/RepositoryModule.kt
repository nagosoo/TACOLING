package com.eundmswlji.tacoling.data.repository

import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ActivityRetainedComponent

@Module
@InstallIn(ActivityRetainedComponent::class)
abstract class RepositoryModule {
    @Binds
    abstract fun bindAddressRepository(addressRepositoryImpl: AddressRepositoryImpl): AddressRepository
}
