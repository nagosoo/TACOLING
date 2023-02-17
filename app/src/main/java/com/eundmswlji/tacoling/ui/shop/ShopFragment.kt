package com.eundmswlji.tacoling.ui.shop

import android.annotation.SuppressLint
import android.content.ActivityNotFoundException
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.MotionEvent
import android.view.View
import androidx.core.content.ContextCompat
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.eundmswlji.tacoling.EventObserver
import com.eundmswlji.tacoling.R
import com.eundmswlji.tacoling.databinding.FragmentShopBinding
import com.eundmswlji.tacoling.ui.BaseFragment
import com.eundmswlji.tacoling.ui.MainActivity
import com.eundmswlji.tacoling.ui.decoration.VerticalItemDecoration
import com.eundmswlji.tacoling.ui.dialog.NormalDialog
import com.eundmswlji.tacoling.util.LinkUtil
import com.eundmswlji.tacoling.util.MapUtil
import com.eundmswlji.tacoling.util.Util.dp
import com.eundmswlji.tacoling.util.Util.toast
import dagger.hilt.android.AndroidEntryPoint
import net.daum.mf.map.api.MapPoint
import net.daum.mf.map.api.MapView

@AndroidEntryPoint
class ShopFragment : BaseFragment<FragmentShopBinding>(FragmentShopBinding::inflate) {

    private var mapView: MapView? = null
    private val shopId by lazy { arguments?.getInt("shopId") }
    private val viewModel: ShopViewModel by viewModels()
    private val adapter by lazy { ShopAdapter() }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.apply {
            viewModel = this@ShopFragment.viewModel
        }

        if (shopId == null) return

        setAppBar()
        setOnClickListener()
        observer()
        setRecyclerView()
        viewModel.getShopInfo(shopId!!)
        (activity as? MainActivity)?.hideBottomNav()
    }

    private fun observer() {
        viewModel.toastHelper.observe(viewLifecycleOwner, EventObserver {
            toast(it)
        })

        viewModel.shopInfo.observe(viewLifecycleOwner) {
            adapter.updateList(it.menu)
            setPOIItem()
        }
    }

    private fun setOnClickListener() {
        binding.shopTop.buttonLike.setOnClickListener {
            NormalDialog(
                title = "단골가게 찜",
                message = getString(R.string.dialog_add_like),
                positiveMessage = getString(R.string.dialog_add_like),
                negativeMessage = "닫기",
                positiveButtonListener = {}).show(childFragmentManager, null)
        }

        binding.shopTop.buttonShare.setOnClickListener {
            share()
        }

        binding.shopTop.buttonCall.setOnClickListener {
            val intent = Intent(Intent.ACTION_DIAL).apply {
                data = Uri.parse("tel:" + viewModel.shopInfo.value?.phoneNumber)
            }
            intent.resolveActivity(requireActivity().packageManager)?.let {
                startActivity(intent)
            }
        }
    }

    private fun share() {
        LinkUtil.setDynamicLinks(shopId!!, "타코왕")?.let { shortLink ->

            val share = Intent.createChooser(Intent().apply {
                action = Intent.ACTION_SEND
                putExtra(Intent.EXTRA_TEXT, shortLink.toString())
                type = "text/plain"
                putExtra(Intent.EXTRA_TITLE, "'타코왕'을 소개해보세요!")
            }, null)

            try {
                startActivity(share)
            } catch (e: ActivityNotFoundException) {
                toast("공유 가능한 앱이 없습니다.")
            }
        }
    }


    private fun setRecyclerView() {
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
        binding.appBar.buttonBack.setOnClickListener { findNavController().popBackStack() }
    }

    @SuppressLint("ClickableViewAccessibility")
    private fun initMap() {
        val centerPoint = MapPoint.mapPointWithGeoCoord(35.86401751026963, 128.6485239265323)
        mapView = MapView(requireContext()).apply {
            setMapCenterPointAndZoomLevel(centerPoint, 2, false)
            setOnTouchListener { _, event ->
                when (event.action) {
                    MotionEvent.ACTION_MOVE -> parent.requestDisallowInterceptTouchEvent(true)
                    MotionEvent.ACTION_UP, MotionEvent.ACTION_CANCEL -> parent.requestDisallowInterceptTouchEvent(
                        false
                    )
                }
                onTouchEvent(event)
            }
            currentLocationTrackingMode = MapView.CurrentLocationTrackingMode.TrackingModeOff
        }
        binding.mapViewContainer.addView(mapView)
        //   setPOIItem()
    }


    private fun setPOIItem() {
        mapView?.let {
            it.removeAllPOIItems()
            it.addPOIItems(
                arrayOf(
                    MapUtil.getMapPOIItem(
                        viewModel.todayLocation.name,
                        viewModel.todayLocation.latitude,
                        viewModel.todayLocation.longitude,
                    )
                )
            )
        }
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

}