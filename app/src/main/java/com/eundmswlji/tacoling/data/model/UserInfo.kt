package com.eundmswlji.tacoling.data.model


import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class UserInfo(
    @SerialName("liked_shops")
    val likedShops: List<LikedShop> = emptyList(),
    val notification: Boolean = true
)