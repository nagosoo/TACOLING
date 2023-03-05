package com.eundmswlji.tacoling.ui.search

import android.os.Bundle
import android.view.View
import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import androidx.paging.LoadState
import com.eundmswlji.tacoling.data.model.Coordinates
import com.eundmswlji.tacoling.data.model.MapViewLocation
import com.eundmswlji.tacoling.databinding.FragmentAddressSearchBinding
import com.eundmswlji.tacoling.ui.BaseFragment
import com.eundmswlji.tacoling.ui.MainActivity
import com.eundmswlji.tacoling.ui.map.MapPagingAdapter
import com.eundmswlji.tacoling.util.Ext.textChanges
import com.eundmswlji.tacoling.util.Util.toast
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

@AndroidEntryPoint
class AddressSearchFragment :
    BaseFragment<FragmentAddressSearchBinding>(FragmentAddressSearchBinding::inflate) {

    private val adapter by lazy { MapPagingAdapter(::itemClickListener) }
    private val viewModel: AddressSearchViewModel by viewModels()
    private val address by lazy { arguments?.getString("address") }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setAppbar()
        searchAddress()
        setRecyclerView()
        setAddressSearchResult()
        setOnClickListener()
        address?.let { binding.editTextSearch.setText(it) }
        (activity as? MainActivity)?.hideBottomNav()
    }

    private fun setAppbar() {
        binding.appBar.apply {
            buttonBack.setOnClickListener {
                findNavController().popBackStack()
            }
            tv.text = "주소 검색"
        }
    }

    private fun setOnClickListener() {
        binding.buttonClear.setOnClickListener {
            binding.editTextSearch.setText("")
        }
    }

    @OptIn(FlowPreview::class, ExperimentalCoroutinesApi::class)
    private fun searchAddress() {
        binding.editTextSearch.textChanges()
            .filterNot { it.isNullOrBlank() }
            .debounce(300)
            .onEach { if (!it.isNullOrEmpty()) viewModel.getAddress(it.toString()) }
            .catch { toast("error : ${it.message}") }
            .launchIn(viewLifecycleOwner.lifecycleScope)
    }

    private fun setRecyclerView() {
        binding.recyclerView.apply {
            adapter = this@AddressSearchFragment.adapter
        }
        adapter.addLoadStateListener { combinedLoadStates ->
            val loadState = combinedLoadStates.source

            val isListEmpty = adapter.itemCount < 1 &&
                    loadState.refresh is LoadState.NotLoading &&
                    loadState.append.endOfPaginationReached

            binding.recyclerView.isVisible = !isListEmpty
            binding.textViewEmpty.isVisible = isListEmpty

        }
    }

    private fun setAddressSearchResult() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.searchedAddress.collectLatest {
                    adapter.submitData(it)
                }
            }
        }
    }


    private fun itemClickListener(longitude: Double, latitude: Double, address: String) {
        val mapViewLocation = MapViewLocation(Coordinates(latitude, longitude), address)
        findNavController().previousBackStackEntry?.savedStateHandle?.set(
            "address",
            mapViewLocation
        )
        findNavController().popBackStack()
    }

}