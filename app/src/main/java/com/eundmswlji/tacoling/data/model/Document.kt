package com.eundmswlji.tacoling.data.model


import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class Document(
    @SerialName("address")
    val address: Address,
    @SerialName("address_name")
    val addressName: String,
    @SerialName("address_type")
    val addressType: String,
    @SerialName("road_address")
    val roadAddress: RoadAddress?,
    @SerialName("x")
    val x: String,
    @SerialName("y")
    val y: String
)