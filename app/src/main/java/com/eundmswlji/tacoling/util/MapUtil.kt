package com.eundmswlji.tacoling.util

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.location.Location
import androidx.core.app.ActivityCompat
import com.eundmswlji.tacoling.R
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import net.daum.mf.map.api.MapPOIItem
import net.daum.mf.map.api.MapPoint
import kotlin.math.abs
import kotlin.math.pow
import kotlin.math.roundToInt

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

    suspend fun getKmFromHereToShop(
        myLatitude: Double,
        itemLatitude: Double,
        myLongitude: Double,
        itemLongitude: Double
    ): Int =
        withContext(Dispatchers.Default) {
            val km = abs(myLatitude - itemLatitude).times(110.574)
                .pow(2) + abs(myLongitude - itemLongitude).times(111).pow(2)
            km.roundToInt()
        }

    fun getCurrentLocation(context: Context): Pair<Double?, Double?> {

        var latitude: Double? = null
        var longitude: Double? = null

        val fusedLocationClient: FusedLocationProviderClient =
            LocationServices.getFusedLocationProviderClient(context)

        val permissionList = arrayOf(
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.ACCESS_COARSE_LOCATION
        )

        permissionList.forEach { permission ->
            val result = ActivityCompat.checkSelfPermission(context, permission)
            if (result != PackageManager.PERMISSION_GRANTED) return Pair(latitude, longitude)
        }

        fusedLocationClient.lastLocation.addOnSuccessListener { location: Location? ->
            if (location != null) {
                latitude = location.latitude
                longitude = location.longitude
            }
        }
        return Pair(latitude, longitude)

    }

}

