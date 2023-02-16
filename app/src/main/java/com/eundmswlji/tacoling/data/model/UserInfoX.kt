package com.eundmswlji.tacoling.data.model


import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class UserInfoX(
    @SerialName("liked_shops")
    val likedShops: List<LikedShopX> = emptyList(),
    val notification: Boolean
)