package com.eundmswlji.tacoling.data.model


import com.google.gson.annotations.SerializedName

data class Document(
    @SerializedName("address")
    val address: Any,
    @SerializedName("address_name")
    val addressName: String,
    @SerializedName("address_type")
    val addressType: String,
    @SerializedName("road_address")
    val roadAddress: RoadAddress,
    @SerializedName("x")
    val x: String,
    @SerializedName("y")
    val y: String
)