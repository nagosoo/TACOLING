package com.eundmswlji.tacoling.data.model

import kotlinx.serialization.Serializable


@Serializable
data class Shop(
    val id : Int,
    val name : String,
    val menu : List<Menu>,
    val liked : Boolean = false
)