package com.eundmswlji.tacoling.retrofit.interceptor

import com.eundmswlji.tacoling.BuildConfig
import okhttp3.Interceptor
import okhttp3.Response

class AuthorizationInterceptor : Interceptor {

    override fun intercept(chain: Interceptor.Chain): Response {
        val newRequest =
            chain.request()
                .newBuilder()
                .addHeader("Authorization", "KakaoAK ${BuildConfig.restKey}")
                .build()
        return chain.proceed(newRequest)
    }
}