package com.eundmswlji.tacoling.data.model


import kotlinx.serialization.Serializable

@Serializable
data class Menu(
    val name: String,
    val price: Int
)