package com.eundmswlji.tacoling.data.model


import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class Address(
    @SerialName("address_name")
    val addressName: String,
    @SerialName("b_code")
    val bCode: String,
    @SerialName("h_code")
    val hCode: String,
    @SerialName("main_address_no")
    val mainAddressNo: String,
    @SerialName("mountain_yn")
    val mountainYn: String,
    @SerialName("region_1depth_name")
    val region1depthName: String,
    @SerialName("region_2depth_name")
    val region2depthName: String,
    @SerialName("region_3depth_h_name")
    val region3depthHName: String,
    @SerialName("region_3depth_name")
    val region3depthName: String,
    @SerialName("sub_address_no")
    val subAddressNo: String,
    @SerialName("x")
    val x: String,
    @SerialName("y")
    val y: String
)