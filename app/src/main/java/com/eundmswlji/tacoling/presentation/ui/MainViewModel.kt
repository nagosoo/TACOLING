package com.eundmswlji.tacoling.presentation.ui

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.eundmswlji.tacoling.domain.usecase.user.GetUserKeyUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    private val getUserKeyUseCase: GetUserKeyUseCase,
) : ViewModel() {

    private val _userKey = MutableLiveData<String?>()
    val userKey: LiveData<String?> = _userKey

    fun getUserKey() {
        viewModelScope.launch {
            val userId = getUserKeyUseCase()
            _userKey.postValue(userId)
        }
    }

    fun saveUserKey(userKey: String) {
        _userKey.value = userKey
    }

}