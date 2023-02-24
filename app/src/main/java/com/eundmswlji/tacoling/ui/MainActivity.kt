package com.eundmswlji.tacoling.ui

import android.graphics.Rect
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import android.view.MotionEvent
import android.view.View
import android.widget.EditText
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.core.view.isGone
import androidx.core.view.isVisible
import androidx.databinding.DataBindingUtil
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import com.eundmswlji.tacoling.R
import com.eundmswlji.tacoling.databinding.ActivityMainBinding
import com.google.android.material.navigation.NavigationBarView
import com.google.firebase.dynamiclinks.ktx.dynamicLinks
import com.google.firebase.ktx.Firebase
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : AppCompatActivity(), NavigationBarView.OnItemSelectedListener,
    NavigationBarView.OnItemReselectedListener {

    private lateinit var binding: ActivityMainBinding
    private lateinit var navController: NavController
    private val viewModel: MainViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        installSplashScreen()

        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val navHostFragment =
            supportFragmentManager.findFragmentById(R.id.nav_host_fragment) as NavHostFragment
        navController = navHostFragment.navController

        binding.bottomNavView.setOnItemSelectedListener(this)
        binding.bottomNavView.setOnItemReselectedListener(this)

        observer()
        viewModel.getUserId()

    }

    private fun observer() {
        viewModel.userId.observe(this) {
            if (it.isNullOrEmpty()) setNavGraph(false)
            else setNavGraph(true)
        }
    }

    private fun setNavGraph(doneLogin: Boolean) {
        val navGraph =
            navController.navInflater.inflate(R.navigation.nav_graph) // app:navGraph="@navigation/nav_graph" 로 설정했던 것
        if (doneLogin) {
            navGraph.setStartDestination(R.id.mapFragment)
            getDynamicLink()
        } //setStartDestination 설정
        else navGraph.setStartDestination(R.id.loginFragment)
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

    private fun getDynamicLink() {

        Firebase.dynamicLinks
            .getDynamicLink(intent)
            .addOnSuccessListener(this) { pendingDynamicLinkData ->
                pendingDynamicLinkData?.link?.let {
                    navController.navigate(it)
                }
            }
            .addOnFailureListener(this) { e ->
                Log.w(
                    "LOGGING",
                    "getDynamicLink:onFailure",
                    e
                )
            }
    }

}


