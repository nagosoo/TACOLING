package com.eundmswlji.tacoling

import android.content.Context
import android.content.Intent
import android.location.LocationManager
import android.provider.Settings
import androidx.appcompat.app.AppCompatActivity
import net.daum.mf.map.api.MapPOIItem
import net.daum.mf.map.api.MapPoint

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

    fun checkGPS(context: Context) {
        val lm = context.getSystemService(AppCompatActivity.LOCATION_SERVICE) as LocationManager
        if (!lm.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            val intent = Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS)
            context.startActivity(intent)
        }
    }

}

