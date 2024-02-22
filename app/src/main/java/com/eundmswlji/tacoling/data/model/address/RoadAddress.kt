package com.eundmswlji.tacoling.data.model.address


import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class RoadAddress(
    @SerialName("address_name")
    val addressName: String,
    @SerialName("building_name")
    val buildingName: String,
    @SerialName("main_building_no")
    val mainBuildingNo: String,
    @SerialName("region_1depth_name")
    val region1depthName: String,
    @SerialName("region_2depth_name")
    val region2depthName: String,
    @SerialName("region_3depth_name")
    val region3depthName: String,
    @SerialName("road_name")
    val roadName: String,
    @SerialName("sub_building_no")
    val subBuildingNo: String,
    @SerialName("underground_yn")
    val undergroundYn: String,
    val x: String,
    val y: String,
    @SerialName("zone_no")
    val zoneNo: String
)