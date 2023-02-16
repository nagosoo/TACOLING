package com.eundmswlji.tacoling.data.model


import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class LikedShopX(
    val id: Int, //파싱에러로 인해 임의
    val name: String
)