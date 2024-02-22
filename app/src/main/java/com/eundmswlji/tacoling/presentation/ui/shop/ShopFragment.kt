package com.eundmswlji.tacoling.presentation.ui.shop

import android.annotation.SuppressLint
import android.content.ActivityNotFoundException
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.MotionEvent
import android.view.View
import androidx.core.view.isVisible
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import com.eundmswlji.tacoling.R
import com.eundmswlji.tacoling.data.model.Location
import com.eundmswlji.tacoling.databinding.FragmentShopBinding
import com.eundmswlji.tacoling.domain.model.ShopModel
import com.eundmswlji.tacoling.domain.status.UiState
import com.eundmswlji.tacoling.presentation.ui.BaseFragment
import com.eundmswlji.tacoling.presentation.ui.MainActivity
import com.eundmswlji.tacoling.presentation.ui.MainViewModel
import com.eundmswlji.tacoling.presentation.ui.decoration.VerticalItemDecoration
import com.eundmswlji.tacoling.presentation.ui.dialog.NormalDialog
import com.eundmswlji.tacoling.presentation.util.MapUtil
import com.eundmswlji.tacoling.util.LinkUtil
import com.eundmswlji.tacoling.util.Util.toPx
import com.eundmswlji.tacoling.util.Util.toast
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import net.daum.mf.map.api.MapPoint
import net.daum.mf.map.api.MapView

@AndroidEntryPoint
class ShopFragment : BaseFragment<FragmentShopBinding>(FragmentShopBinding::inflate) {

    private var mapView: MapView? = null
    private val shopId by lazy { arguments?.getInt("shopId") }
    private val viewModel: ShopViewModel by viewModels()
    private val adapter by lazy { ShopAdapter() }
    private val mainViewModel: MainViewModel by activityViewModels()
    private val userKey by lazy { mainViewModel.userKey.value }
    private lateinit var mapPoint: MapPoint
    private lateinit var todayLocation: Location

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (shopId == null || userKey == null) return
        viewModel.getShopInfo(shopId!!, userKey!!)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setAppBar()
        observer()
        setRecyclerView()
        (activity as? MainActivity)?.hideBottomNav()
        binding.apply {
            viewModel = this@ShopFragment.viewModel
            lifecycleOwner = viewLifecycleOwner
        }

    }

    private fun observer() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.uiState.collect { uiState ->
                    when (uiState) {
                        is UiState.None -> {}
                        is UiState.Loading -> {}
                        is UiState.Success -> {
                            uiState.value?.let { shop ->
                                binding.shopTop.tvName.text = shop.name
                                binding.shopTop.tvLocation.text = shop.kmToShop
                                binding.shopTop.tvZeroWaste.text =
                                    if (shop.doZeroWaste) "용기내챌린지 참여" else "용기내챌린지 미참여"
                                binding.shopTop.buttonLike.text =
                                    if (shop.isFavoriteShop == true) "찜 해지💔" else "찜 하기❤️"
                                binding.mapviewContainer.isVisible = !viewModel.isTodayOff()
                                binding.shopOff.root.isVisible = viewModel.isTodayOff()
                                adapter.updateList(shop.menu)
                                setOnClickListener(shop)
                                todayLocation = shop.location[viewModel.today]
                                mapPoint = MapPoint.mapPointWithGeoCoord(
                                    todayLocation.latitude,
                                    todayLocation.longitude,
                                )
                                initMap()
                                setPOIItem(shop)
                            }

                        }

                        is UiState.Error -> {
                            toast(uiState.errorMessage)
                        }
                    }
                }
            }
        }

    }

    private fun setOnClickListener(shop: ShopModel) {
        binding.shopTop.buttonLike.setOnClickListener {
            showDialog(shop)
        }
        binding.shopTop.buttonShare.setOnClickListener {
            share(shop)
        }
        binding.shopTop.buttonCall.setOnClickListener {
            call(shop)
        }
    }

    private fun call(shop: ShopModel) {
        val intent = Intent(Intent.ACTION_DIAL).apply {
            data = Uri.parse("tel:" + shop.phoneNumber)
        }
        intent.resolveActivity(requireActivity().packageManager)?.let {
            startActivity(intent)
        }
    }

    private fun showDialog(shop: ShopModel) {
        NormalDialog(
            title = if (shop.isFavoriteShop == true) getString(R.string.dialog_remove_title)
            else getString(R.string.dialog_add_like_title),
            message = if (shop.isFavoriteShop == true) getString(R.string.dialog_remove_like_message)
            else getString(R.string.dialog_add_like_message),
            positiveMessage = if (shop.isFavoriteShop == true) getString(R.string.dialog_remove_like_button)
            else getString(R.string.dialog_add_like_button),
            negativeMessage = "닫기",
            positiveButtonListener = {
                if (shop.isFavoriteShop == true) {
                    viewModel.deleteFavoriteShop(shop, userKey!!)
                } else {
                    viewModel.addFavoriteShop(shop, userKey!!)
                }
            })
            .show(parentFragmentManager, null)
    }

    private fun share(shop: ShopModel) {
        LinkUtil.setDynamicLinks(shopId!!, shop.name)?.let { shortLink ->
            val share = Intent.createChooser(Intent().apply {
                action = Intent.ACTION_SEND_MULTIPLE
                putExtra(Intent.EXTRA_TITLE, "'${shop.name}'을 소개해보세요!")
                putExtra(Intent.EXTRA_TEXT, shortLink.toString())
                type = "text/plain"
            }, null)

            try {
                startActivity(share)
            } catch (e: ActivityNotFoundException) {
                toast("공유 가능한 앱이 없습니다.")
            }
        }
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
    }

    private fun setPOIItem(shop: ShopModel) {
        mapView?.let {
            it.removeAllPOIItems()
            it.addPOIItems(
                arrayOf(
                    MapUtil.getMapPOIItem(
                        todayLocation.locationName,
                        todayLocation.latitude,
                        todayLocation.longitude,
                        shop.doZeroWaste ?: false,
                        shop.id
                    )
                )
            )
        }
    }

    override fun onStart() {
        super.onStart()
        //initMap()
    }

    override fun onStop() {
        super.onStop()
        binding.mapviewContainer.removeView(mapView)
        mapView = null
    }

}