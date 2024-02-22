package com.eundmswlji.tacoling

import android.app.Application
import androidx.appcompat.app.AppCompatDelegate
import com.kakao.sdk.common.KakaoSdk
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class TacolingApplication : Application() {

    companion object {
        lateinit var appPackageName : String
        lateinit var appLabel : String
    }

    override fun onCreate() {
        appPackageName = applicationContext.packageName
        appLabel = applicationContext.getString(R.string.app_name)
        super.onCreate()
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
        KakaoSdk.init(this, BuildConfig.appKey)
        //  Log.d("keyhash","${  KakaoSdk.keyHash}")
    }
}