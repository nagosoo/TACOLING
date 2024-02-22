package com.eundmswlji.tacoling.data.model.address


import com.eundmswlji.tacoling.data.model.DataModelInterface
import com.eundmswlji.tacoling.domain.model.AddressModel
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class Document(
    val address: Address?,
    @SerialName("address_name")
    val addressName: String,
    @SerialName("address_type")
    val addressType: String,
    @SerialName("road_address")
    val roadAddress: RoadAddress?,
    val x: String,
    val y: String
) :DataModelInterface<AddressModel>{
    override fun toDomainModel(): AddressModel {
        return AddressModel(
            address = addressName,
            x = x,
            y = y,
        )
    }
}