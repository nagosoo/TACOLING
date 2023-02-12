package com.eundmswlji.tacoling.di

import com.eundmswlji.tacoling.BuildConfig
import com.eundmswlji.tacoling.retrofit.TacolingRetrofit
import com.eundmswlji.tacoling.retrofit.services.AddressService
import com.eundmswlji.tacoling.retrofit.services.ShopService
import com.eundmswlji.tacoling.retrofit.services.UserService
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
class AppModule {
    @Provides
    fun provideAddressService(tacolingRetrofit: TacolingRetrofit): AddressService =
        tacolingRetrofit.getRetrofitBuilder(BuildConfig.kakaoBaseUrl, true)
            .create(AddressService::class.java)

    @Provides
    fun provideUserService(tacolingRetrofit: TacolingRetrofit): UserService =
        tacolingRetrofit.getRetrofitBuilder(BuildConfig.firebaseBaseUrl, false)
            .create(UserService::class.java)

    @Provides
    fun provideShopService(tacolingRetrofit: TacolingRetrofit): ShopService =
        tacolingRetrofit.getRetrofitBuilder(BuildConfig.firebaseBaseUrl, false)
            .create(ShopService::class.java)
}