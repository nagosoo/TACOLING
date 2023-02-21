package com.eundmswlji.tacoling.data.model


import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class AddressFromKakao(
    @SerialName("documents")
    val documents: List<Document>,
    @SerialName("meta")
    val meta: Meta
)