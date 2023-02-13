package com.eundmswlji.tacoling

import android.app.Application
import androidx.appcompat.app.AppCompatDelegate
import androidx.lifecycle.viewModelScope
import com.eundmswlji.tacoling.data.repository.user.UserRepository
import com.eundmswlji.tacoling.util.SharedPreferencesUtil
import com.kakao.sdk.common.KakaoSdk
import dagger.hilt.android.HiltAndroidApp
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltAndroidApp
class TacolingApplication : Application() {

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