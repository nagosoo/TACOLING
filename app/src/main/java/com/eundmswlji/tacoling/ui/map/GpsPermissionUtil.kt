package com.eundmswlji.tacoling.ui.map

import android.app.Activity
import android.content.IntentSender
import android.location.LocationManager
import androidx.appcompat.app.AppCompatActivity
import com.google.android.gms.common.api.ResolvableApiException
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.LocationSettingsRequest

object GpsPermissionUtil {

    fun checkGPS(requireActivity: Activity) {
        val lm =
            requireActivity.getSystemService(AppCompatActivity.LOCATION_SERVICE) as LocationManager
        if (!lm.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            turnOnGPS(requireActivity)
        }
    }

    private fun turnOnGPS(activity: Activity) {
        activity.let {
            val locationRequest = LocationRequest.create()
            locationRequest.priority = LocationRequest.PRIORITY_HIGH_ACCURACY

            val builder = LocationSettingsRequest.Builder()
                .addLocationRequest(locationRequest)

            val task = LocationServices.getSettingsClient(it)
                .checkLocationSettings(builder.build())

            //gps 꺼져 있을 때
            task.addOnFailureListener { e ->
                if (e is ResolvableApiException) {
                    try {
                        //다이어로그 띄움
                        e.startResolutionForResult(
                            it,
                            999
                        )
                    } catch (sendEx: IntentSender.SendIntentException) {
                    }
                }
            }
        }
    }

}