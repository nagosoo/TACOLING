package com.eundmswlji.tacoling.data.model.address


import kotlinx.serialization.Serializable

@Serializable
data class AddressPaging(
    val documents: List<Document>,
    val meta: Meta
)