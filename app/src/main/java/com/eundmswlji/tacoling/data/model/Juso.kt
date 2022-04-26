package com.eundmswlji.tacoling.data.model


import com.google.gson.annotations.SerializedName

data class Juso(
    @SerializedName("documents")
    val documents: List<Document>,
    @SerializedName("meta")
    val meta: Meta
)