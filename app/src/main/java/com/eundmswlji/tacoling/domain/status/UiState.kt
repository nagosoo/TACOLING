package com.eundmswlji.tacoling.domain.status

sealed class UiState<out T> {
    object None : UiState<Nothing>()
    object Loading : UiState<Nothing>()
    data class Success<T>(val value: T) : UiState<T>()
    data class Error(val errorMessage: String) : UiState<Nothing>()
}