package com.eundmswlji.tacoling.ui.map

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.pm.PackageManager
import androidx.activity.result.ActivityResultLauncher
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.eundmswlji.tacoling.MainApplication

object LocationPermissionUtil {

    fun onlyCheckPermissions(requireContext: Context): Boolean {
        val coarsePermissionGranted = ContextCompat.checkSelfPermission(
            requireContext,
            Manifest.permission.ACCESS_COARSE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED
        val finePermissionGranted = ContextCompat.checkSelfPermission(
            requireContext,
            Manifest.permission.ACCESS_FINE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED
        return coarsePermissionGranted && finePermissionGranted
    }

    fun shouldShowRationale(requireActivity: Activity): Boolean {
        //사용자가 권한 요청을 명시적으로 거부한 경우 true를 반환한다.
        //사용자가 권한 요청을 처음 보거나, 다시 묻지 않음 선택한 경우, 권한을 허용한 경우 false를 반환한다.
        return ActivityCompat.shouldShowRequestPermissionRationale(
            requireActivity,
            Manifest.permission.ACCESS_COARSE_LOCATION
        )
                || ActivityCompat.shouldShowRequestPermissionRationale(
            requireActivity,
            Manifest.permission.ACCESS_FINE_LOCATION
        )
    }

    fun launchLocationLauncher(launcher: ActivityResultLauncher<Array<String>>) {
        launcher.launch(
            arrayOf(
                Manifest.permission.ACCESS_COARSE_LOCATION,
                Manifest.permission.ACCESS_FINE_LOCATION
            )
        )
    }

    private val isFirstRequest = MainApplication.sp.getBoolean("firstRequest", false)

    fun requestPermission(
        requireContext: Context,
        trackingModeOn: () -> Unit,
        launcher: ActivityResultLauncher<Array<String>>
    ) {
        if (isFirstRequest) {//앱 처음 사용시 무조건 퍼미션을 요청한다.
            MainApplication.sp.setBoolean("firstRequest", true)
            launchLocationLauncher(launcher)
        } else { // 앱 처음 사용이 아니면, 퍼미션 허락 되어있을 때만 tracking 하고, 따로 퍼미션 요청 메시지는 띄우지 x
            if (onlyCheckPermissions(requireContext)) {
                trackingModeOn()
            }
        }
    }

}