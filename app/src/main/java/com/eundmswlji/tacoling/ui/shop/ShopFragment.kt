package com.eundmswlji.tacoling.ui.shop

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.eundmswlji.tacoling.databinding.FragmentShopBinding

class ShopFragment : Fragment() {
    private lateinit var binding: FragmentShopBinding
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = FragmentShopBinding.inflate(inflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setAppBar()
    }

    private fun setAppBar() {
        binding.appBar.tv.text = "가게 상세 보기"
    }
}