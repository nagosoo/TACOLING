package com.eundmswlji.tacoling.data.model


import com.eundmswlji.tacoling.domain.model.UserKeyModel
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class UserKey(
    @SerialName("name")
    val userKey: String
) : DataModelInterface<UserKeyModel> {
    override fun toDomainModel(): UserKeyModel {
        return UserKeyModel(userKey)
    }
}