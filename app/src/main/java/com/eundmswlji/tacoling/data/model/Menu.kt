package com.eundmswlji.tacoling.data.model

import kotlinx.serialization.Serializable

@Serializable
data class Menu(
    val id: Int,
    val name: String,
    val detail: String,
    val price: Int
)