package com.eundmswlji.tacoling.ui.shop

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.eundmswlji.tacoling.databinding.FragmentShopBinding
import net.daum.mf.map.api.MapView

class ShopFragment : Fragment() {
    private lateinit var binding: FragmentShopBinding
    private lateinit var mapView: MapView
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = FragmentShopBinding.inflate(inflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setAppBar()
        initMap()
    }

    private fun setAppBar() {
        binding.appBar.tv.text = "가게 상세 보기"
    }

    private fun initMap() {
        mapView = MapView(requireContext())
        binding.mapViewContainer.addView(mapView)
    }

    override fun onStop() {
        super.onStop()
        binding.mapViewContainer.removeView(mapView)
    }
}