package com.eundmswlji.tacoling.data.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class AddLikedShopResponse(
    @SerialName("id") //서버에서 주는 필드명
    val id: Int
)