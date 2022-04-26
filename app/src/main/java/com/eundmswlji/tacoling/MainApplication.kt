package com.eundmswlji.tacoling

import android.app.Application
import androidx.appcompat.app.AppCompatDelegate
import com.kakao.sdk.common.KakaoSdk
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class MainApplication:Application()  {
    override fun onCreate() {
        super.onCreate()
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
        KakaoSdk.init(this, "563fe1f966e961f52948242b73c19cca")
        //  Log.d("keyhash","${  KakaoSdk.keyHash}")
    }
}