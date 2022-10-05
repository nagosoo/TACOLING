package com.eundmswlji.tacoling.util

import com.eundmswlji.tacoling.R
import net.daum.mf.map.api.MapPOIItem
import net.daum.mf.map.api.MapPoint
import kotlin.math.abs
import kotlin.math.pow

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

    fun geoToKm(
        myLatitude: Double,
        itemLatitude: Double,
        myLongitude: Double,
        itemLongitude: Double
    ) = abs(myLatitude - itemLatitude).times(110.574)
        .pow(2) + abs(myLongitude - itemLongitude).times(111).pow(2)

}

