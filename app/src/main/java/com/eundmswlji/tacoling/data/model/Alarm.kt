package com.eundmswlji.tacoling.data.model

import com.eundmswlji.tacoling.domain.model.AlarmModel
import kotlinx.serialization.Serializable

@Serializable
data class Alarm(
    val notification: Boolean
) : DataModelInterface<AlarmModel> {
    override fun toDomainModel(): AlarmModel {
        return AlarmModel(notification)
    }
}