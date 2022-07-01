package com.eundmswlji.tacoling.ui.map

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.location.LocationManager
import android.provider.Settings
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat.shouldShowRequestPermissionRationale
import androidx.core.content.ContextCompat
import androidx.fragment.app.FragmentActivity
import androidx.fragment.app.FragmentManager
import com.eundmswlji.tacoling.MainApplication
import com.eundmswlji.tacoling.MainApplication.Companion.sp
import com.eundmswlji.tacoling.SharedPreferencesUtil
import com.eundmswlji.tacoling.ui.dialog.NormalDialog

object LocationPermissionUtil {

    fun checkGPS(context: Context) {
        //Todo :: gps 처리
        val lm = context.getSystemService(AppCompatActivity.LOCATION_SERVICE) as LocationManager
        if (!lm.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            val intent = Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS)
            context.startActivity(intent)
        }
    }

    fun checkLocationPermission(cm: FragmentManager, requireActivity: FragmentActivity) {
        when {
            (shouldShowRationale(requireActivity)) -> {
                NormalDialog(
                    title = "위치권한 설정",
                    message = "내 주변의 타코야키 트럭을 찾기 위해 위치권한을 허용해주세요.",
                    positiveMessage = "네",
                    negativeMessage = "아니요",
                    positiveButtonListener = ::turnOnLocationPermission
                ).show(
                    cm,
                    null
                )
            }
            else -> {
                // You can directly ask for the permission.
                // The registered ActivityResultCallback gets the result of this request.
                turnOnLocationPermission()
            }
        }
    }

    fun onlyCheckPermissions(requireContext: Context): Boolean {
        val coarsePermissionGranted = ContextCompat.checkSelfPermission(requireContext, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED
        val finePermissionGranted = ContextCompat.checkSelfPermission(requireContext, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
        return coarsePermissionGranted && finePermissionGranted
    }

    fun requestPermission(requireContext: Context, callBack: () -> Unit) {
        if (!doneFirstRequest()) {//앱 처음 사용시 무조건 퍼미션을 요청한다.
            applyFirstRequest()
            turnOnLocationPermission()
        } else { // 앱 처음 사용이 아니면, 퍼미션 허락 되어있을 때만 tracking 하고, 따로 퍼미션 요청 메시지는 띄우지 x
            if (onlyCheckPermissions(requireContext)) {
                callBack()
            }
        }
    }

    private fun shouldShowRationale(requireActivity: FragmentActivity): Boolean {
        //사용자가 권한 요청을 명시적으로 거부한 경우 true를 반환한다.
        //사용자가 권한 요청을 처음 보거나, 다시 묻지 않음 선택한 경우, 권한을 허용한 경우 false를 반환한다.
        return shouldShowRequestPermissionRationale(requireActivity, Manifest.permission.ACCESS_COARSE_LOCATION)
                || shouldShowRequestPermissionRationale(requireActivity, Manifest.permission.ACCESS_FINE_LOCATION)
    }

    private fun turnOnLocationPermission() {
        MapFragment().setLocationResultLauncher().launch(
            arrayOf(
                Manifest.permission.ACCESS_COARSE_LOCATION,
                Manifest.permission.ACCESS_FINE_LOCATION
            )
        )
    }

    private fun doneFirstRequest(): Boolean =
        sp.getBoolean("firstRequest", false)

    private fun applyFirstRequest() {
        sp.setBoolean("firstRequest", true)
    }
}