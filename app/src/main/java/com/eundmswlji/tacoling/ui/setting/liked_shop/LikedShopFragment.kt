package com.eundmswlji.tacoling.ui.setting.liked_shop

import android.os.Bundle
import android.view.View
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import com.eundmswlji.tacoling.EventObserver
import com.eundmswlji.tacoling.databinding.FragmentLikedShopBinding
import com.eundmswlji.tacoling.ui.BaseFragment
import com.eundmswlji.tacoling.ui.MainActivity
import com.eundmswlji.tacoling.ui.decoration.VerticalItemDecoration
import com.eundmswlji.tacoling.util.Util.dp
import com.eundmswlji.tacoling.util.Util.toast
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@AndroidEntryPoint
class LikedShopFragment :
    BaseFragment<FragmentLikedShopBinding>(FragmentLikedShopBinding::inflate) {
    private val adapter by lazy { LikedShopAdapter(::itemClickListener) }
    private val viewModel: LikedShopViewModel by viewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setAdapter()
        setAppBar()
        (requireActivity() as? MainActivity)?.hideBottomNav()
        viewModel.getLikedShops()
        observer()
        setupTouchHelper(binding.root)
    }

    private fun observer() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.likedList.collectLatest { list ->
                    adapter.updateList(list)
                }
            }
        }

        viewModel.toastHelper.observe(viewLifecycleOwner, EventObserver {
            toast(it)
        })
    }

    private fun setAppBar() {
        binding.appBar.tv.text = "내가 찜한 가게"
    }

    private fun setAdapter() {
        val itemDecoration = VerticalItemDecoration(
            top = requireContext().dp(30),
            bottom = 0,
            between = requireContext().dp(12),
            left = 0,
            right = 0
        )
        binding.recyclerView.apply {
            adapter = this@LikedShopFragment.adapter
            addItemDecoration(itemDecoration)
        }
    }

    private fun itemClickListener(shopId: Int) {
        val action =
            LikedShopFragmentDirections.actionLikedShopFragmentToShopFragment(shopId)
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
                val position = viewHolder.bindingAdapterPosition
                val shop = viewModel.likedList.value[position]
                viewModel.removeMyLikedList(shop.id)
                Snackbar.make(view, "찜한 가게가 삭제되었습니다.", Snackbar.LENGTH_SHORT).apply {
                    setAction("되돌리기") {
                        viewModel.addMyLikedList(position, shop.id, shop.name)
                    }
                }.show()
            }
        }
        ItemTouchHelper(itemTouchHelperCallback).apply {
            attachToRecyclerView(binding.recyclerView)
        }
    }

}
