package com.eundmswlji.tacoling.data.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class MapViewLocation(
    val coordinates: Coordinates,
    val address: String
) : Parcelable