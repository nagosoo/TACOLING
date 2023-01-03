package com.eundmswlji.tacoling.ui.shop

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import com.eundmswlji.tacoling.R
import com.eundmswlji.tacoling.data.model.Menu
import com.eundmswlji.tacoling.databinding.FragmentShopBinding
import com.eundmswlji.tacoling.ui.BaseFragment
import com.eundmswlji.tacoling.ui.MainActivity
import com.eundmswlji.tacoling.ui.decoration.VerticalItemDecoration
import com.eundmswlji.tacoling.ui.dialog.NormalDialog
import com.eundmswlji.tacoling.ui.dialog.ShareDialog
import com.eundmswlji.tacoling.util.Util.dp
import net.daum.mf.map.api.MapView

class ShopFragment : BaseFragment() {
    private var _binding: FragmentShopBinding? = null
    private val binding get() = _binding!!
    private var mapView: MapView? = null
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = DataBindingUtil.inflate(layoutInflater, R.layout.fragment_shop, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setAppBar()
        initArea()
        initMenu()
        setOnClickListener()
        (activity as? MainActivity)?.hideBottomNav()
    }

    private fun setOnClickListener() {
        binding.shopTop.buttonLike.setOnClickListener {
            // "단골가게 찜 해지" 더이상 가게의 상세 정보를 받지 않습니다.\n알림은 마이페이지에서 끌 수 있습니다. 찜 해지하기
            NormalDialog(
                title = "단골가게 찜",
                message = "가게의 상세 정보를 알려드립니다.\n알림은 마이페이지에서 끌 수 있습니다.",
                positiveMessage = "찜하기",
                negativeMessage = "닫기",
                positiveButtonListener = {},
                negativeButtonListener = {}).show(childFragmentManager, null)
        }
        binding.shopTop.buttonShare.setOnClickListener {
            ShareDialog().show(childFragmentManager, null)
        }
        binding.shopTop.buttonCall.setOnClickListener {
            val intent = Intent(Intent.ACTION_DIAL).apply {
                data = Uri.parse("tel:01000000000")
            }
            if (intent.resolveActivity(requireActivity().packageManager) != null) {
                startActivity(intent)
            }
        }
    }

    private fun initArea() {
        binding.shopMiddle.tvMon.text = "만촌아파트 앞 18:00~20:00"
        binding.shopMiddle.tvTue.text = "만촌아파트 앞 18:00~20:00"
        binding.shopMiddle.tvWed.text = "만촌아파트 앞 18:00~20:00"
        binding.shopMiddle.tvThu.text = "만촌아파트 앞 18:00~20:00"
        binding.shopMiddle.tvFri.text = "만촌아파트 앞 18:00~20:00"
        binding.shopMiddle.tvSat.text = "휴무"
        binding.shopMiddle.tvSun.text = "만촌아파트 앞 18:00~20:00"
    }

    private fun initMenu() {
        val adapter = ShopAdapter(
            listOf(
                Menu(id = 0, name = "타코야키 8알(기본맛, 매운맛)", detail = "", price = 3000),
                Menu(id = 0, name = "타코야키 8알(기본맛, 매운맛)", detail = "", price = 3000)
            )
        )
        binding.shopBottom.recyclerView.adapter = adapter
        binding.shopBottom.recyclerView.addItemDecoration(
            VerticalItemDecoration(
                requireContext().dp(
                    16
                ), requireContext().dp(16), requireContext().dp(8), 0, 0
            )
        )
    }

    private fun setAppBar() {
        binding.appBar.tv.text = "가게 상세 보기"
    }

    private fun initMap() {
        mapView = MapView(requireContext())
        binding.mapViewContainer.addView(mapView)
    }

    override fun onStart() {
        super.onStart()
        initMap()
    }

    override fun onStop() {
        super.onStop()
        binding.mapViewContainer.removeView(mapView)
        mapView = null
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}