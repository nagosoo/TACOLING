package com.eundmswlji.tacoling.data.model


import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class Juso(
    @SerialName("documents")
    val documents: List<Document>,
    @SerialName("meta")
    val meta: Meta
)