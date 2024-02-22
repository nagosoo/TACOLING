package com.eundmswlji.tacoling.presentation.ui.search

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.eundmswlji.tacoling.domain.model.AddressModel
import com.eundmswlji.tacoling.domain.repository.AddressRepository
import com.eundmswlji.tacoling.domain.usecase.address.GetAddressUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AddressSearchViewModel @Inject constructor(
    private val getAddressUseCase: GetAddressUseCase,
) : ViewModel() {

    private val _searchedAddress = MutableStateFlow<PagingData<AddressModel>>(PagingData.empty())
    val searchedAddress: StateFlow<PagingData<AddressModel>> = _searchedAddress.asStateFlow()


    fun getAddress(query: String) {
        viewModelScope.launch {
            getAddressUseCase(query)
                .cachedIn(viewModelScope)
                .collectLatest { _searchedAddress.value = it }
        }
    }

}