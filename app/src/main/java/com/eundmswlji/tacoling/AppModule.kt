package com.eundmswlji.tacoling

import com.eundmswlji.tacoling.retrofit.RestClient
import com.eundmswlji.tacoling.retrofit.services.AddressService
import com.eundmswlji.tacoling.retrofit.services.UserService
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
class AppModule {
    @Provides
    fun provideAddressService(restClient: RestClient): AddressService =
        restClient.getRetrofitBuilder(BuildConfig.kakaoBaseUrl, true)
            .create(AddressService::class.java)

    @Provides
    fun provideUserService(restClient: RestClient) =
        restClient.getRetrofitBuilder(BuildConfig.firebaseBaseUrl, false)
            .create(UserService::class.java)
}