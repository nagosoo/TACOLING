package com.eundmswlji.tacoling.domain.model


import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class UserKeyModel(
    @SerialName("name")
    val userKey: String
)