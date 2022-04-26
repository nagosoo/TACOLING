package com.eundmswlji.tacoling

import com.eundmswlji.tacoling.retrofit.RestClient
import com.eundmswlji.tacoling.retrofit.services.JusoService
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
class AppModule {
    @Provides
    fun provideJusoService(restClient: RestClient): JusoService = restClient.getRetrofitBuilder(BuildConfig.baseUrl).create(JusoService::class.java)
}