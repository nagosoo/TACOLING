package com.eundmswlji.tacoling.ui

import androidx.lifecycle.*
import com.eundmswlji.tacoling.Const.USER_ID
import com.eundmswlji.tacoling.data.repository.user.UserRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    private val userRepository: UserRepository
) : ViewModel() {

    private val _userId = MutableLiveData<String?>()
    val userId: LiveData<String?> = _userId

    fun getUserId() {
        viewModelScope.launch {
            val userId = userRepository.getUserId().first()
            USER_ID = userId
            _userId.postValue(userId)
        }
    }
}