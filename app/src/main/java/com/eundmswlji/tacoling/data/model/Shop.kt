package com.eundmswlji.tacoling.data.model


import com.eundmswlji.tacoling.domain.model.ShopModel
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class Shop(
    val id: Int,
    val location: List<Location>,
    val menu: List<Menu>,
    val name: String,
    @SerialName("phone_number")
    val phoneNumber: Long,
    @SerialName("zero_waste")
    val zeroWaste: Boolean
) : DataModelInterface<ShopModel> {
    override fun toDomainModel(): ShopModel {
        return ShopModel(
            id = this.id,
            location = this.location,
            menu = this.menu,
            name = this.name,
            phoneNumber = this.phoneNumber,
            doZeroWaste = zeroWaste
        )
    }
}