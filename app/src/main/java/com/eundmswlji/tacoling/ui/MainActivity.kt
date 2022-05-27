package com.eundmswlji.tacoling.ui

import android.graphics.Rect
import android.os.Bundle
import android.view.MenuItem
import android.view.MotionEvent
import android.view.View
import android.widget.EditText
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.core.view.isGone
import androidx.core.view.isVisible
import androidx.databinding.DataBindingUtil
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import com.eundmswlji.tacoling.R
import com.eundmswlji.tacoling.Util.toast
import com.eundmswlji.tacoling.databinding.ActivityMainBinding
import com.google.android.material.navigation.NavigationBarView
import com.kakao.sdk.auth.AuthApiClient
import com.kakao.sdk.common.model.KakaoSdkError
import com.kakao.sdk.user.UserApiClient
import com.kakao.sdk.user.model.AccessTokenInfo
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : AppCompatActivity(), NavigationBarView.OnItemSelectedListener, NavigationBarView.OnItemReselectedListener {

    private lateinit var binding: ActivityMainBinding
    private lateinit var navController: NavController

    override fun onCreate(savedInstanceState: Bundle?) {
        installSplashScreen()

        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main)

        val navHostFragment =
            supportFragmentManager.findFragmentById(R.id.nav_host_fragment) as NavHostFragment
        navController = navHostFragment.navController

        binding.bottomNavView.setOnItemSelectedListener(this)
        binding.bottomNavView.setOnItemReselectedListener(this)

        checkIsLogin()
    }

    private fun checkIsLogin() {
        if (AuthApiClient.instance.hasToken()) {
            UserApiClient.instance.accessTokenInfo { _, error ->
                if (error != null) {
                    if (error is KakaoSdkError && error.isInvalidTokenError()) {
                        //로그인 필요
                        setNavGraph(false)
                    } else {
                        toast("로그인 실패")
                    }
                } else {
                    //토큰 유효성 체크 성공(필요 시 토큰 갱신됨)
                    UserApiClient().accessTokenInfo(callback = object : (AccessTokenInfo?, Throwable?) -> (Unit) {
                        override fun invoke(token: AccessTokenInfo?, error: Throwable?) {
                            if (error != null) {
                                //로그인 필요
                                setNavGraph(false)
                            } else if (token != null) {
                                setNavGraph(true)
                            }
                        }
                    })
                }
            }
        } else {
            //  로그인 필요
            setNavGraph(false)
        }
    }

    private fun setNavGraph(isAlreadyLogin: Boolean) {
        val navGraph = navController.navInflater.inflate(R.navigation.nav_graph) // app:navGraph="@navigation/nav_graph" 로 설정했던 것
        if (isAlreadyLogin) navGraph.setStartDestination(R.id.mapFragment) //setStartDestination 설정
        else navGraph.setStartDestination(R.id.signInFragment)
        navController.setGraph(navGraph, null) //navController에 graph 설정
    }

    override fun dispatchTouchEvent(event: MotionEvent?): Boolean {
        if (event?.action == MotionEvent.ACTION_DOWN) {
            val v: View? = currentFocus
            if (v is EditText) {
                val outRect = Rect()
                v.getGlobalVisibleRect(outRect)
                if (!outRect.contains(event.rawX.toInt(), event.rawY.toInt())) {
                    v.clearFocus()
                }
            }
        }
        return super.dispatchTouchEvent(event)
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.menu_setting -> {
                navController.navigate(R.id.settingFragment)
                return true
            }
            R.id.menu_map -> {
                navController.navigate(R.id.mapFragment)
                return true
            }
        }
        return false
    }

    override fun onNavigationItemReselected(item: MenuItem) {
        when (item.itemId) {
            R.id.menu_setting -> {
                return
            }
            R.id.menu_map -> {
                return
            }
        }
    }

    fun hideBottomNav() {
        if (::binding.isInitialized) {
            binding.bottomNavView.isGone = true
        }
    }

    fun showBottomNav() {
        if (::binding.isInitialized) {
            binding.bottomNavView.isVisible = true
        }
    }

}


