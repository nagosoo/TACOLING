package com.eundmswlji.tacoling.presentation.ui.setting.favorite_shop

import android.os.Bundle
import android.view.View
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import com.eundmswlji.tacoling.databinding.FragmentLikedShopBinding
import com.eundmswlji.tacoling.domain.status.UiState
import com.eundmswlji.tacoling.presentation.ui.BaseFragment
import com.eundmswlji.tacoling.presentation.ui.MainActivity
import com.eundmswlji.tacoling.presentation.ui.MainViewModel
import com.eundmswlji.tacoling.presentation.ui.decoration.VerticalItemDecoration
import com.eundmswlji.tacoling.util.Util.toast
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@AndroidEntryPoint
class FavoriteShopFragment :
    BaseFragment<FragmentLikedShopBinding>(FragmentLikedShopBinding::inflate) {
    private val adapter by lazy { FavoriteShopAdapter(::itemClickListener) }
    private val viewModel: FavoriteShopViewModel by viewModels()
    private val mainViewModel: MainViewModel by activityViewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (mainViewModel.userKey.value == null) return
        viewModel.getLikedShops(mainViewModel.userKey.value!!)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setAdapter()
        setAppBar()
        (requireActivity() as? MainActivity)?.hideBottomNav()
        observer()
        setupTouchHelper(binding.root)
    }

    private fun observer() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.favoriteShops.collectLatest { shops ->
                    when (shops) {
                        is UiState.None -> {}
                        is UiState.Loading -> {}
                        is UiState.Success -> {
                            adapter.updateList(shops.value!!)
                        }

                        is UiState.Error -> {
                            toast(shops.errorMessage)
                        }
                    }
                }
            }
        }
    }

    private fun setAppBar() {
        binding.appBar.tv.text = "내가 찜한 가게"
        binding.appBar.buttonBack.setOnClickListener {
            findNavController().popBackStack()
        }
    }

    private fun setAdapter() {
        val itemDecoration = VerticalItemDecoration(
            top = 0,
            bottom = 0,
            between = 12,
            left = 0,
            right = 0
        )
        binding.recyclerview.apply {
            adapter = this@FavoriteShopFragment.adapter
            addItemDecoration(itemDecoration)
        }
    }

    private fun itemClickListener(shopId: Int) {
        val action =
            FavoriteShopFragmentDirections.actionFavoriteShopFragmentToShopFragment(shopId)
        findNavController().navigate(action)
    }

    private fun setupTouchHelper(view: View) {
        val itemTouchHelperCallback = object : ItemTouchHelper.SimpleCallback(
            0, ItemTouchHelper.LEFT
        ) {
            override fun onMove(
                recyclerView: RecyclerView,
                viewHolder: RecyclerView.ViewHolder,
                target: RecyclerView.ViewHolder
            ): Boolean {
                return true
            }

            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
//                val position = viewHolder.bindingAdapterPosition
//                val shop = viewModel.likedList.value[position]
//                viewModel.removeFavoriteShop(shop.id, position)
//                Snackbar.make(view, "찜한 가게가 삭제되었습니다.", Snackbar.LENGTH_SHORT).apply {
//                    setAction("되돌리기") {
//                        viewModel.addFavoriteShop(position, shop.id, shop.name)
//                    }
//                }.show()
            }
        }
        ItemTouchHelper(itemTouchHelperCallback).apply {
            attachToRecyclerView(binding.recyclerview)
        }
    }

}
