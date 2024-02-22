package com.eundmswlji.tacoling.data.model

import com.eundmswlji.tacoling.domain.model.ShopsModel
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class Shops(
    @SerialName("item")
    val shops: List<Shop>
):DataModelInterface<ShopsModel>{
    override fun toDomainModel(): ShopsModel {
        return ShopsModel(
            shops = shops.map { it.toDomainModel() }
        )
    }
}