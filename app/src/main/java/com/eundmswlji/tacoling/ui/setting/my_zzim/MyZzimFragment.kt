package com.eundmswlji.tacoling.ui.setting.my_zzim

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.eundmswlji.tacoling.R
import com.eundmswlji.tacoling.data.model.Shop
import com.eundmswlji.tacoling.databinding.FragmentMyZzimBinding
import com.eundmswlji.tacoling.ui.BaseFragment
import com.eundmswlji.tacoling.ui.MainActivity
import com.eundmswlji.tacoling.ui.decoration.VerticalItemDecoration
import splitties.dimensions.dip

class MyZzimFragment : BaseFragment() {
    private lateinit var binding: FragmentMyZzimBinding
    private val adapter by lazy { MyZzimAdapter(::itemClickListener, ::heartClickListener) }
    private val viewModel: MyZzimViewModel by viewModels()
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = FragmentMyZzimBinding.inflate(layoutInflater).apply {
            lifecycleOwner = viewLifecycleOwner
            viewModel = this@MyZzimFragment.viewModel
        }
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setAdapter()
        setAppBar()
        (requireActivity() as? MainActivity)?.hideBottomNav()

        //
        viewModel.setMyZzimList()
        //
    }


    private fun setAppBar() {
        binding.appBar.tv.text = "내가 찜한 가게"
    }

    private fun setAdapter() {
        binding.recyclerView.adapter = adapter
        val itemDecoration = VerticalItemDecoration(top = requireContext().dip(30), bottom = 0, between = requireContext().dip(12), left = 0, right = 0)
        binding.recyclerView.addItemDecoration(itemDecoration)
    }

    private fun itemClickListener(shop: Shop) {
        findNavController().navigate(R.id.shopFragment)
    }

    private fun heartClickListener(shop: Shop) {
        viewModel.removeMyZzimList(shop)
    }
}
