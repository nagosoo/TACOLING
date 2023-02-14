package com.eundmswlji.tacoling.ui.setting.liked_shop

import android.os.Bundle
import android.view.View
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import com.eundmswlji.tacoling.databinding.FragmentLikedShopBinding
import com.eundmswlji.tacoling.ui.BaseFragment
import com.eundmswlji.tacoling.ui.MainActivity
import com.eundmswlji.tacoling.ui.decoration.VerticalItemDecoration
import com.eundmswlji.tacoling.util.Util.dp
import dagger.hilt.android.AndroidEntryPoint
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
    }


    private fun observer() {
        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.myLikedList.collect { list ->
                    adapter.submitList(list)
                }
            }
        }
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

//    private fun heartClickListener(shop: Int) {
//        viewModel.removeMyLikedList(shop)
//    }
}
