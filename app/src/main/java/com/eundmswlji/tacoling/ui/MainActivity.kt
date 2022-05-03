package com.eundmswlji.tacoling.ui

import android.Manifest
import android.content.Context
import android.graphics.Rect
import android.os.Bundle
import android.view.MenuItem
import android.view.MotionEvent
import android.view.View
import android.widget.EditText
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import com.eundmswlji.tacoling.MapUtil
import com.eundmswlji.tacoling.R
import com.eundmswlji.tacoling.databinding.ActivityMainBinding
import com.google.android.material.navigation.NavigationBarView
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : AppCompatActivity(), NavigationBarView.OnItemSelectedListener, NavigationBarView.OnItemReselectedListener {
    private lateinit var binding: ActivityMainBinding
    private lateinit var navController: NavController
    private val sharedPref by lazy { this.getPreferences(Context.MODE_PRIVATE) }
    override fun onCreate(savedInstanceState: Bundle?) {
        installSplashScreen()
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val navHostFragment =
            supportFragmentManager.findFragmentById(R.id.nav_host_fragment) as NavHostFragment
        navController = navHostFragment.navController
        checkGPSOn()
        val doneFirstRequest = sharedPref.getBoolean("firstRequest", false)
        if (!doneFirstRequest) checkPermission()

        binding.bottomNavView.setOnItemSelectedListener(this)
        binding.bottomNavView.setOnItemReselectedListener(this)
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

    private fun checkPermission() {
        applyFirstRequest()
        val locationPermissionRequest = registerForActivityResult(
            ActivityResultContracts.RequestMultiplePermissions()
        ) { permissions ->
            when {
                permissions.getOrDefault(Manifest.permission.ACCESS_FINE_LOCATION, false) -> {
                    // Precise location access granted.
                }
                permissions.getOrDefault(Manifest.permission.ACCESS_COARSE_LOCATION, false) -> {
                    // Only approximate location access granted.
                    Toast.makeText(this, "정확한 위치권한을 허용해야 사용자 근처의 타코야키 트럭을 정확히 찾을 수 있습니다.", Toast.LENGTH_SHORT).show()
                }
                else -> {
                    Toast.makeText(this, "위치권한을 허용하지 않으면 사용자 근처의 타코야키 트럭을 찾을 수 없습니다.", Toast.LENGTH_SHORT).show()
                }
            }
        }

        //이미 권한이 있는지 체크
        locationPermissionRequest.launch(
            arrayOf(
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION,
            )
        )
    }

    private fun checkGPSOn() {
        MapUtil.checkGPS(this)
    }

    private fun applyFirstRequest() {
        with(sharedPref.edit()) {
            this.putBoolean("firstRequest", true)
            apply()
        }
    }
}


