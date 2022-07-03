package com.eundmswlji.tacoling.util

import com.eundmswlji.tacoling.R
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

}

