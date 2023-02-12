package com.eundmswlji.tacoling.data.model


import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class ShopXX(
    val id: Int,
    val location: List<Location>,
    val menu: List<MenuX>,
    val name: String,
    @SerialName("phone_number")
    val phoneNumber: Int,
    @SerialName("zero_waste")
    val zeroWaste: Boolean
)