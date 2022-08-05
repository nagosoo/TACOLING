package com.eundmswlji.tacoling.retrofit

import com.eundmswlji.tacoling.retrofit.interceptor.AuthorizationInterceptor
import com.eundmswlji.tacoling.retrofit.interceptor.UserAgentInterceptor
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit
import javax.inject.Inject

class RestClient @Inject constructor() {
    private val httpLoggingInterceptor: HttpLoggingInterceptor by lazy { HttpLoggingInterceptor() }
    private val authorizationInterceptor: AuthorizationInterceptor by lazy { AuthorizationInterceptor() }
    private val userAgentInterceptor: UserAgentInterceptor by lazy { UserAgentInterceptor() }

    fun getRetrofitBuilder(baseurl: String): Retrofit {
        return Retrofit.Builder().baseUrl(baseurl)
            .client(getOkHttpClient())
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    private fun getOkHttpClient(): OkHttpClient {
        return OkHttpClient.Builder().apply {
            connectTimeout(2, TimeUnit.MINUTES)
            readTimeout(2, TimeUnit.MINUTES)
            writeTimeout(2, TimeUnit.MINUTES)
            interceptors().apply {
                add(httpLoggingInterceptor.apply {
                    level = HttpLoggingInterceptor.Level.BODY
                })
                add(authorizationInterceptor)
                add(userAgentInterceptor)
            }
        }.build()
    }
}