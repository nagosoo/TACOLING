package com.eundmswlji.tacoling.data.model


import kotlinx.serialization.Serializable

@Serializable
data class Location(
    val latitude: Double,
    val longitude: Double,
    val name: String,
)