package com.eundmswlji.tacoling.presentation.ui.login

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.eundmswlji.tacoling.data.model.FavoriteShop
import com.eundmswlji.tacoling.data.model.UserInfo
import com.eundmswlji.tacoling.domain.model.UserKeyModel
import com.eundmswlji.tacoling.domain.status.UiState
import com.eundmswlji.tacoling.domain.usecase.user.SaveUserKeyUseCase
import com.eundmswlji.tacoling.domain.usecase.user.SignUpUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LoginViewModel @Inject constructor(
    private val signUpUseCase: SignUpUseCase,
    private val saveUserKeyUseCase: SaveUserKeyUseCase,
) : ViewModel() {

    private val _uiState = MutableStateFlow<UiState<UserKeyModel?>>(UiState.Loading)
    val uiState: StateFlow<UiState<UserKeyModel?>> = _uiState

    fun postUser() {
        viewModelScope.launch {
            signUpUseCase(
                UserInfo(
                    favoriteShops = listOf(
                        FavoriteShop(
                            shopId = -100,
                            shopName = "dummy"
                        )
                    ),
                    notification = true,

                    )
            ).stateIn(
                viewModelScope,
                SharingStarted.WhileSubscribed(5000),
                UiState.Loading
            )
                .collect { uiState ->
                    _uiState.value = uiState
                }
        }
    }

    fun saveUserKey(userKey: String, onSaveSuccessListener: () -> Unit) {
        viewModelScope.launch {
            saveUserKeyUseCase(userKey)
        }.invokeOnCompletion { throwable ->
            if (throwable == null) onSaveSuccessListener()
        }
    }
}