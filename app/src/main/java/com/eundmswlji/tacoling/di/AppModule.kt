package com.eundmswlji.tacoling.di

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.PreferenceDataStoreFactory
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStoreFile
import com.eundmswlji.tacoling.BuildConfig
import com.eundmswlji.tacoling.Const.DATASTORE_NAME
import com.eundmswlji.tacoling.retrofit.TacolingRetrofit
import com.eundmswlji.tacoling.retrofit.services.AddressService
import com.eundmswlji.tacoling.retrofit.services.ShopService
import com.eundmswlji.tacoling.retrofit.services.UserService
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class AppModule {
    @Provides
    @Singleton
    fun provideAddressService(tacolingRetrofit: TacolingRetrofit): AddressService =
        tacolingRetrofit.getRetrofitBuilder(BuildConfig.kakaoBaseUrl, true)
            .create(AddressService::class.java)

    @Provides
    @Singleton
    fun provideUserService(tacolingRetrofit: TacolingRetrofit): UserService =
        tacolingRetrofit.getRetrofitBuilder(BuildConfig.firebaseBaseUrl, false)
            .create(UserService::class.java)

    @Provides
    @Singleton
    fun provideShopService(tacolingRetrofit: TacolingRetrofit): ShopService =
        tacolingRetrofit.getRetrofitBuilder(BuildConfig.firebaseBaseUrl, false)
            .create(ShopService::class.java)

    @Provides
    @Singleton
    fun provideDataStore(@ApplicationContext context: Context): DataStore<Preferences> =
        PreferenceDataStoreFactory.create(
            produceFile = { context.preferencesDataStoreFile(DATASTORE_NAME) }
        )
}