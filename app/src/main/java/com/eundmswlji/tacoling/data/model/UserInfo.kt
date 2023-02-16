package com.eundmswlji.tacoling.data.model


import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class UserInfo(
    @SerialName("liked_shops")
    val likedShops: List<LikedShopX>,
    val notification: Boolean = true
)