package com.eundmswlji.tacoling.retrofit.interceptor

import okhttp3.Interceptor
import okhttp3.Response

class AuthorizationInterceptor : Interceptor {

    override fun intercept(chain: Interceptor.Chain): Response {
        val newRequest =
            chain.request()
                .newBuilder()
                .addHeader("Authorization", "KakaoAK 64e23bd337c873853926def07c5028fb")
                .build()
        return chain.proceed(newRequest)
    }
}