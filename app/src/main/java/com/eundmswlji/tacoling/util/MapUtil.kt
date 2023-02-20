package com.eundmswlji.tacoling.util

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.location.Location
import android.util.Log
import androidx.core.app.ActivityCompat
import com.eundmswlji.tacoling.R
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import net.daum.mf.map.api.MapPOIItem
import net.daum.mf.map.api.MapPoint
import kotlin.math.*

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

    fun getCurrentLocation(
        context: Context,
        onSuccessListener: ((Double, Double) -> (Unit))?
    ) {

        val fusedLocationClient: FusedLocationProviderClient =
            LocationServices.getFusedLocationProviderClient(context)

        val permissionList = arrayOf(
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.ACCESS_COARSE_LOCATION
        )

        permissionList.forEach { permission ->
            val result = ActivityCompat.checkSelfPermission(context, permission)
            if (result != PackageManager.PERMISSION_GRANTED) return
        }

        fusedLocationClient.lastLocation.addOnSuccessListener { location: Location? ->
            if (location != null) {
                onSuccessListener?.let {
                    it(location.latitude, location.longitude)
                }
            }
        }

        fusedLocationClient.lastLocation.addOnFailureListener { exception ->
            Log.d("logging", "fusedLocationClient failed : ${exception.message}")
        }
    }

    suspend fun distanceCalculate(lat1: Double, lon1: Double, lat2: Double, lon2: Double) =
        withContext(Dispatchers.Default) {
            val r = 6_371_000.0 // radius of Earth in meters
            val phi1 = Math.toRadians(lat1)
            val phi2 = Math.toRadians(lat2)
            val deltaPhi = Math.toRadians(lat2 - lat1)
            val deltaLambda = Math.toRadians(lon2 - lon1)
            val a = sin(deltaPhi / 2).pow(2) + cos(phi1) * cos(phi2) * sin(deltaLambda / 2).pow(2)
            val c = 2 * atan2(sqrt(a), sqrt(1 - a))
            val d = r * c
            d.roundToInt()
        }

}

