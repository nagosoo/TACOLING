package com.eundmswlji.tacoling.data.model


import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class LikedShop(
    @SerialName("id")
    val id: Int,
    @SerialName("name")
    val name: String
)