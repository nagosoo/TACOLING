package com.eundmswlji.tacoling.presentation.ui

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.core.view.isGone
import androidx.core.view.isVisible
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import com.eundmswlji.tacoling.R
import com.eundmswlji.tacoling.databinding.ActivityMainBinding
import com.eundmswlji.tacoling.presentation.ui.dialog.NormalDialog
import com.eundmswlji.tacoling.util.Util.toast
import com.google.android.material.bottomnavigation.BottomNavigationView
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

        askNotificationPermission()
        observer()
        viewModel.getUserKey()
    }

    override fun onNewIntent(intent: Intent?) {
        super.onNewIntent(intent)
        intent?.let {
            val shopId = it.getBundleExtra("bundle")?.getString("shopId")
            shopId?.let {
                navController.navigate(R.id.shopFragment, Bundle().apply {
                    putInt("shopId", shopId.toInt())
                })
            }
            it.removeExtra("shopId")
        }
    }

    private val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted: Boolean ->
        if (isGranted) {
        } else {
            toast("알림을 받기 위해서는 알림 권한을 허용해야 합니다.")
        }
    }

    private fun askNotificationPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS) ==
                PackageManager.PERMISSION_GRANTED
            ) {
            } else if (shouldShowRequestPermissionRationale(Manifest.permission.POST_NOTIFICATIONS)) {
                NormalDialog("알림 권한",
                    "알림을 받기 위해서는 알림 권한을 허용해야 합니다.",
                    positiveMessage = "확인",
                    negativeMessage = "취소",
                    positiveButtonListener = {
                        requestPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
                    }).show(supportFragmentManager, null)
            } else {
                requestPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
            }
        }
    }

    private fun observer() {
        viewModel.userKey.observe(this) {
            if (it.isNullOrEmpty()) setNavGraph(false)
            else setNavGraph(true)
        }
    }

    private fun setNavGraph(doneLogin: Boolean) {
        val navGraph =
            navController.navInflater.inflate(R.navigation.nav_graph)
        if (doneLogin) {
            navGraph.setStartDestination(R.id.mapFragment)
            getDynamicLink()
            navController.setGraph(navGraph, null) //navController에 graph 설정
            manageBackgroundNotification()
        } //setStartDestination 설정
        else {
            navGraph.setStartDestination(R.id.loginFragment)
            navController.setGraph(navGraph, null) //navController에 graph 설정
        }
    }

    private fun manageBackgroundNotification() {
        val shopId = intent.getBundleExtra("bundle")?.getString("shopId")
        shopId?.let {
            intent.removeExtra("shopId")
            navController.navigate(R.id.shopFragment, Bundle().apply {
                putInt("shopId", shopId.toInt())
            })
        }
    }

//    override fun dispatchTouchEvent(event: MotionEvent?): Boolean {
//        if (event?.action == MotionEvent.ACTION_DOWN) {
//            val v: View? = currentFocus
//            if (v is EditText) {
//                val outRect = Rect()
//                v.getGlobalVisibleRect(outRect)
//                if (!outRect.contains(event.rawX.toInt(), event.rawY.toInt())) {
//                    v.clearFocus()
//                }
//            }
//        }
//        return super.dispatchTouchEvent(event)
//    }

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

    fun setBottomNavClicked(index: Int) {
        val bottomNav = binding.bottomNavView as BottomNavigationView
        binding.bottomNavView.selectedItemId = bottomNav.menu.getItem(index).itemId
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


