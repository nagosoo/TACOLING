package com.eundmswlji.tacoling

import android.app.Application
import androidx.appcompat.app.AppCompatDelegate
import com.kakao.sdk.common.KakaoSdk
import com.kakao.sdk.common.KakaoSdk.appKey
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class MainApplication:Application()  {
    override fun onCreate() {
        super.onCreate()
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
        KakaoSdk.init(this, BuildConfig.appKey)
        //  Log.d("keyhash","${  KakaoSdk.keyHash}")
    }
}