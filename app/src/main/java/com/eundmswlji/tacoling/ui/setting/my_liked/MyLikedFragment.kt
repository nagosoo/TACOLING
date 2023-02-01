package com.eundmswlji.tacoling.ui.setting.my_liked

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.eundmswlji.tacoling.R
import com.eundmswlji.tacoling.data.model.Shop
import com.eundmswlji.tacoling.databinding.FragmentMyLikedBinding
import com.eundmswlji.tacoling.ui.BaseFragment
import com.eundmswlji.tacoling.ui.MainActivity
import com.eundmswlji.tacoling.ui.decoration.VerticalItemDecoration
import com.eundmswlji.tacoling.util.Util.dp

class MyLikedFragment : BaseFragment() {
    private lateinit var binding: FragmentMyLikedBinding
    private val adapter by lazy { MyLikedAdapter(::itemClickListener, ::heartClickListener) }
    private val viewModel: MyLikedViewModel by viewModels()
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentMyLikedBinding.inflate(layoutInflater).apply {
            lifecycleOwner = viewLifecycleOwner
            viewModel = this@MyLikedFragment.viewModel
        }
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setAdapter()
        setAppBar()
        (requireActivity() as? MainActivity)?.hideBottomNav()

        //
        viewModel.setMyLikedList()
        //
    }


    private fun setAppBar() {
        binding.appBar.tv.text = "내가 찜한 가게"
    }

    private fun setAdapter() {
        binding.recyclerView.adapter = adapter
        val itemDecoration = VerticalItemDecoration(
            top = requireContext().dp(30),
            bottom = 0,
            between = requireContext().dp(12),
            left = 0,
            right = 0
        )
        binding.recyclerView.addItemDecoration(itemDecoration)
    }

    private fun itemClickListener(shop: Shop) {
        findNavController().navigate(R.id.shopFragment)
    }

    private fun heartClickListener(shop: Shop) {
        viewModel.removeMyLikedList(shop)
    }
}
