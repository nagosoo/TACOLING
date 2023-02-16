package com.eundmswlji.tacoling.retrofit

import com.eundmswlji.tacoling.retrofit.interceptor.AuthorizationInterceptor
import com.eundmswlji.tacoling.retrofit.interceptor.UserAgentInterceptor
import com.jakewharton.retrofit2.converter.kotlinx.serialization.asConverterFactory
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.json.Json
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import java.util.concurrent.TimeUnit
import javax.inject.Inject

class TacolingRetrofit @Inject constructor() {

    private val httpLoggingInterceptor: HttpLoggingInterceptor by lazy { HttpLoggingInterceptor() }
    private val authorizationInterceptor: AuthorizationInterceptor by lazy { AuthorizationInterceptor() }
    private val userAgentInterceptor: UserAgentInterceptor by lazy { UserAgentInterceptor() }

    private val contentType = "application/json".toMediaType()

    private val json = Json {
        encodeDefaults = true
        ignoreUnknownKeys = true
    }

    @OptIn(ExperimentalSerializationApi::class)
    fun getRetrofitBuilder(baseurl: String, needAuthorization: Boolean): Retrofit {
        return Retrofit.Builder().baseUrl(baseurl)
            .client(getOkHttpClient(needAuthorization))
            .addConverterFactory(json.asConverterFactory(contentType))
            .build()
    }

    private fun getOkHttpClient(needAuthorization: Boolean): OkHttpClient {
        return OkHttpClient.Builder().apply {
            connectTimeout(2, TimeUnit.MINUTES)
            readTimeout(2, TimeUnit.MINUTES)
            writeTimeout(2, TimeUnit.MINUTES)
            interceptors().apply {
                add(httpLoggingInterceptor.apply {
                    level = HttpLoggingInterceptor.Level.BODY
                })
                add(userAgentInterceptor)
                if (needAuthorization) add(authorizationInterceptor)
            }
        }.build()
    }
}
