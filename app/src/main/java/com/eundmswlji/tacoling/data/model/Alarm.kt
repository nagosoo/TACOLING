package com.eundmswlji.tacoling.data.model

import kotlinx.serialization.SerialName

@kotlinx.serialization.Serializable
data class Alarm(
    @SerialName("notification")
    val notification: Boolean
)