package com.eundmswlji.tacoling.retrofit.interceptor

import android.os.Build
import com.eundmswlji.tacoling.BuildConfig
import okhttp3.Interceptor
import okhttp3.Response

class UserAgentInterceptor() : Interceptor {

    private val userAgent by lazy { buildUserAgent() }

    override fun intercept(chain: Interceptor.Chain): Response {
        val request = chain.request()
            .newBuilder()
            .addHeader("userAgent", userAgent)
            .build()
        return chain.proceed(request)
    }

    private fun buildUserAgent(): String {
        val versionName = BuildConfig.VERSION_NAME
        val versionCode = BuildConfig.VERSION_CODE
        val packageName = BuildConfig.APPLICATION_ID
        val manufacturer = Build.MANUFACTURER
        val model = Build.MODEL
        val version = Build.VERSION.SDK_INT
        val versionRelease = Build.VERSION.RELEASE

        return "$packageName / $versionName($versionCode); ($manufacturer; $model; SDK $version; Android $versionRelease)"
    }

}