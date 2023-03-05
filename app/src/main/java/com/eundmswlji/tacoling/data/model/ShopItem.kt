package com.eundmswlji.tacoling.data.model


import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class ShopItem(
    val id: Int,
    val location: List<Location>,
    val menu: List<MenuX>,
    val name: String,
    @SerialName("phone_number")
    val phoneNumber: Long,
    @SerialName("zero_waste")
    val zeroWaste: Boolean
)