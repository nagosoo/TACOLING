package com.eundmswlji.tacoling.ui.shop

import android.annotation.SuppressLint
import android.content.ActivityNotFoundException
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.MotionEvent
import android.view.View
import androidx.core.view.isVisible
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
import com.eundmswlji.tacoling.util.Util.toPx
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


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (shopId == null) return

        viewModel.getShopInLikedList(shopId!!)
        viewModel.getShopInfo(shopId!!)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.apply {
            viewModel = this@ShopFragment.viewModel
            lifecycleOwner = viewLifecycleOwner
        }

        setAppBar()
        setOnClickListener()
        observer()
        setRecyclerView()
        (activity as? MainActivity)?.hideBottomNav()
    }

    private fun observer() {
        viewModel.toastHelper.observe(viewLifecycleOwner, EventObserver {
            toast(it)
        })

        viewModel.isOff.observe(viewLifecycleOwner) { isOff ->
            binding.mapviewContainer.isVisible = !isOff
            binding.shopOff.root.isVisible = isOff
        }

        viewModel.kmToShop.observe(viewLifecycleOwner) {
            binding.shopTop.tvLocation.text = it
        }
    }

    private fun setOnClickListener() {
        binding.shopTop.buttonLike.setOnClickListener {
            showDialog(viewModel.isLikedShop.value ?: false)
        }
        binding.shopTop.buttonShare.setOnClickListener {
            share()
        }
        binding.shopTop.buttonCall.setOnClickListener {
            call()
        }
    }

    private fun call() {
        if (viewModel.shopInfo.value != null) {
            val intent = Intent(Intent.ACTION_DIAL).apply {
                data = Uri.parse("tel:" + viewModel.shopInfo.value!!.phoneNumber)
            }
            intent.resolveActivity(requireActivity().packageManager)?.let {
                startActivity(intent)
            }
        } else {
            toast("가게 정보를 불러오지 못했습니다.")
        }
    }

    private fun showDialog(isLikedShop: Boolean) {
        NormalDialog(
            title = if (isLikedShop) getString(R.string.dialog_remove_title)
            else getString(R.string.dialog_add_like_title),
            message = if (isLikedShop) getString(R.string.dialog_remove_like_message)
            else getString(R.string.dialog_add_like_message),
            positiveMessage = if (isLikedShop) getString(R.string.dialog_remove_like_button)
            else getString(R.string.dialog_add_like_button),
            negativeMessage = "닫기",
            positiveButtonListener = {
                if (isLikedShop) viewModel.removeMyLikedList() else viewModel.addLikedShop(shopId!!)
            })
            .show(childFragmentManager, null)
    }

    private fun share() {
        viewModel.shopInfo.value?.name?.let { shopName ->
            LinkUtil.setDynamicLinks(shopId!!, shopName)?.let { shortLink ->

                val share = Intent.createChooser(Intent().apply {
                    action = Intent.ACTION_SEND_MULTIPLE
                    putExtra(Intent.EXTRA_TITLE, "'$shopName'을 소개해보세요!")
                    putExtra(Intent.EXTRA_TEXT, shortLink.toString())
                    type = "text/plain"
                }, null)

                try {
                    startActivity(share)
                } catch (e: ActivityNotFoundException) {
                    toast("공유 가능한 앱이 없습니다.")
                }
            }
            return
        }
        toast("가게 정보를 불러오지 못했습니다.")
    }

    private fun setRecyclerView() {
        binding.shopBottom.recyclerView.apply {
            adapter = this@ShopFragment.adapter
            addItemDecoration(
                VerticalItemDecoration(
                    requireContext().toPx(16),
                    requireContext().toPx(16),
                    requireContext().toPx(5),
                    0, 0
                )
            )
        }
    }

    private fun setAppBar() {
        binding.appBar.tv.text = "가게 상세 보기"
        binding.appBar.buttonBack.setOnClickListener { findNavController().popBackStack() }
    }

    @SuppressLint("ClickableViewAccessibility")
    private fun initMap() {
        val mapPoint = MapPoint.mapPointWithGeoCoord(
            viewModel.todayLocation.latitude,
            viewModel.todayLocation.longitude
        )
        mapView = MapView(requireActivity()).apply {
            currentLocationTrackingMode = MapView.CurrentLocationTrackingMode.TrackingModeOff
            setMapCenterPointAndZoomLevel(mapPoint, 2, false)
            setOnTouchListener { _, event ->
                when (event.action) {
                    MotionEvent.ACTION_MOVE -> parent.requestDisallowInterceptTouchEvent(true)
                    MotionEvent.ACTION_UP, MotionEvent.ACTION_CANCEL -> parent.requestDisallowInterceptTouchEvent(
                        false
                    )
                }
                onTouchEvent(event)
            }
        }
        binding.mapviewContainer.addView(mapView)
        setPOIItem()
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
                        viewModel.shopInfo.value?.zeroWaste ?: false,
                        viewModel.shopInfo.value?.id ?: -100
                    )
                )
            )
        }
    }

    override fun onStart() {
        super.onStart()
        viewModel.shopInfo.observe(viewLifecycleOwner) {
            adapter.updateList(it.menu)
            initMap()
        }
    }

    override fun onStop() {
        super.onStop()
        binding.mapviewContainer.removeView(mapView)
        mapView = null
    }

}