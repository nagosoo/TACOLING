package com.eundmswlji.tacoling

import com.eundmswlji.tacoling.retrofit.RestClient
import com.eundmswlji.tacoling.retrofit.services.AddressService
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
class AppModule {
    @Provides
    fun provideAddressService(restClient: RestClient): AddressService =
        restClient.getRetrofitBuilder(BuildConfig.baseUrl).create(AddressService::class.java)
}