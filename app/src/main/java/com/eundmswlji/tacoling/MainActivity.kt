package com.eundmswlji.tacoling

import android.Manifest
import android.content.Context
import android.content.Intent
import android.location.LocationManager
import android.os.Bundle
import android.provider.Settings
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.navigation.fragment.NavHostFragment
import com.eundmswlji.tacoling.databinding.ActivityMainBinding


class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        installSplashScreen()
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val navHostFragment =
            supportFragmentManager.findFragmentById(R.id.nav_host_fragment) as NavHostFragment
        val navController = navHostFragment.navController
        //  navController.navigate(startdes)
        checkGPSOn()
        val sharedPref = this.getPreferences(Context.MODE_PRIVATE) ?: return
        val doneFirstRequest = sharedPref.getBoolean("firstRequest", false)
        if (!doneFirstRequest) checkPermission()
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
        val lm = this.getSystemService(LOCATION_SERVICE) as LocationManager
        if (!lm.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            val intent = Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS)
            startActivity(intent)
        }
    }

    private fun applyFirstRequest() {
        val sharedPref = this.getPreferences(Context.MODE_PRIVATE)
        with(sharedPref.edit()) {
            this.putBoolean("firstRequest", true)
            apply()
        }
    }
}


