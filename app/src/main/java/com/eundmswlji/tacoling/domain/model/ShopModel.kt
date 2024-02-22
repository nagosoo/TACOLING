package com.eundmswlji.tacoling.domain.model

import com.eundmswlji.tacoling.data.model.Location
import com.eundmswlji.tacoling.data.model.Menu

data class ShopModel(
    val id: Int,
    val location: List<Location>,
    val menu: List<Menu>,
    val name: String,
    val phoneNumber: Long,
    val doZeroWaste: Boolean,
    val isFavoriteShop: Boolean? = null,
    val kmToShop: String? = null,
    val offDate: Int? = null,
)