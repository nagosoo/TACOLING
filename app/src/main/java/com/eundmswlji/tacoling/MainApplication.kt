package com.eundmswlji.tacoling

import android.app.Application
import androidx.appcompat.app.AppCompatDelegate
import com.eundmswlji.tacoling.util.SharedPreferencesUtil
import com.kakao.sdk.common.KakaoSdk
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class MainApplication : Application() {

    companion object {
        lateinit var sp: SharedPreferencesUtil
        lateinit var appPackageName : String
        lateinit var appLabel : String
    }

    override fun onCreate() {
        sp = SharedPreferencesUtil(applicationContext)
        appPackageName = applicationContext.packageName
        appLabel = applicationContext.getString(R.string.app_name)
        super.onCreate()
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
        KakaoSdk.init(this, BuildConfig.appKey)
        //  Log.d("keyhash","${  KakaoSdk.keyHash}")
    }
}