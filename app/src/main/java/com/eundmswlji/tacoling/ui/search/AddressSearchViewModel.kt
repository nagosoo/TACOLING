package com.eundmswlji.tacoling.ui.search

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.eundmswlji.tacoling.data.model.Document
import com.eundmswlji.tacoling.data.repository.address.AddressRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AddressSearchViewModel @Inject constructor(
    private val addressRepository: AddressRepository
) : ViewModel() {

    private val _searchedAddress = MutableStateFlow<PagingData<Document>>(PagingData.empty())
    val searchedAddress: StateFlow<PagingData<Document>> = _searchedAddress.asStateFlow()


    fun getAddress(query: String) {
        viewModelScope.launch {
            addressRepository.getAddress(query)
                .cachedIn(viewModelScope)
                .collectLatest { _searchedAddress.value = it }
        }
    }

}