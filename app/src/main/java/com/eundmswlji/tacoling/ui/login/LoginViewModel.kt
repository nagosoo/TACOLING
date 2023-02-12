package com.eundmswlji.tacoling.ui.login

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.eundmswlji.tacoling.data.model.UserInfo
import com.eundmswlji.tacoling.data.repository.user.UserRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LoginViewModel @Inject constructor(
    private val userRepository: UserRepository,
) : ViewModel() {

    private val _loginSuccess = MutableLiveData<Boolean>()
    val loginSuccess: LiveData<Boolean> = _loginSuccess

    fun postUser(userInfo: UserInfo) {
        viewModelScope.launch {
            val response = userRepository.postUser(userInfo)
            if (response.isSuccessful) {
                _loginSuccess.postValue(true)
            } else _loginSuccess.postValue(false)
        }
    }

}