package com.eundmswlji.tacoling

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.location.LocationManager
import android.provider.Settings
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import net.daum.mf.map.api.MapPOIItem
import net.daum.mf.map.api.MapPoint
import org.json.JSONObject

object MapUtil {
    fun getMapPOIItem(name: String, latitude: Double, longitude: Double): MapPOIItem {
        return MapPOIItem().apply {
            this.itemName = name
            this.mapPoint = MapPoint.mapPointWithGeoCoord(latitude, longitude)
            this.markerType = MapPOIItem.MarkerType.CustomImage
            this.customImageResourceId = R.drawable.ic_takoyaki
            this.isShowDisclosureButtonOnCalloutBalloon = false
        }
    }

    fun Fragment.checkPermissions(): Boolean {
        val coarsePermissionGranted = ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED
        val finePermissionGranted = ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
        return coarsePermissionGranted && finePermissionGranted
    }

    fun checkGPS(context: Context) {
        val lm = context.getSystemService(AppCompatActivity.LOCATION_SERVICE) as LocationManager
        if (!lm.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            val intent = Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS)
            context.startActivity(intent)
        }
    }

}

