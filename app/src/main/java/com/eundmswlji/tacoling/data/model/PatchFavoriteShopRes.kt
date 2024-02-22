package com.eundmswlji.tacoling.data.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class PatchFavoriteShopRes(
    @SerialName("id")
    val shopId: Int,
    @SerialName("name")
    val shopName: String
)