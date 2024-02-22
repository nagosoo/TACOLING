package com.eundmswlji.tacoling.data

import com.eundmswlji.tacoling.data.model.DataModelInterface
import com.eundmswlji.tacoling.domain.status.UiState

fun <T> UiState<DataModelInterface<T>?>.toDomainModelFlow(): UiState<T?> {
    return when (this) {
        is UiState.Success -> {
            val domainModel = this.value?.toDomainModel()
            UiState.Success(domainModel)
        }

        is UiState.Loading -> this
        is UiState.Error -> this
        is UiState.None -> this
    }
}