package com.eundmswlji.tacoling.ui.shop

import android.annotation.SuppressLint
import android.content.ActivityNotFoundException
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.navigation.fragment.findNavController
import com.eundmswlji.tacoling.R
import com.eundmswlji.tacoling.data.model.Menu
import com.eundmswlji.tacoling.databinding.FragmentShopBinding
import com.eundmswlji.tacoling.ui.BaseFragment
import com.eundmswlji.tacoling.ui.MainActivity
import com.eundmswlji.tacoling.ui.decoration.VerticalItemDecoration
import com.eundmswlji.tacoling.ui.dialog.NormalDialog
import com.eundmswlji.tacoling.util.LinkUtil
import com.eundmswlji.tacoling.util.MapUtil
import com.eundmswlji.tacoling.util.Util.dp
import com.eundmswlji.tacoling.util.Util.toast
import net.daum.mf.map.api.MapPoint
import net.daum.mf.map.api.MapView


class ShopFragment : BaseFragment<FragmentShopBinding>(FragmentShopBinding::inflate) {
    private var mapView: MapView? = null
    private val shopId by lazy { arguments?.getInt("shopId") }

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
            share()
//           val dialog = ShareDialogFactory(shopId!!).instantiate(
//                classLoader = ClassLoader.getSystemClassLoader(),
//                ShareDialog::class.java.name
//            )
//            (dialog as DialogFragment).show(childFragmentManager, null)

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

    private fun share() {
        LinkUtil.setDynamicLinks(shopId!!, "타코왕")?.let { shortLink ->

            val share = Intent.createChooser(Intent().apply {
                action = Intent.ACTION_SEND
                putExtra(Intent.EXTRA_TEXT, shortLink.toString())
                type = "text/plain"

                // (Optional) Here we're setting the title of the content
                putExtra(Intent.EXTRA_TITLE, "'타코왕'을 소개해보세요!")

                // (Optional) Here we're passing a content URI to an image to be displayed
                //  data = contentUri
                // flags = Intent.FLAG_GRANT_READ_URI_PERMISSION
            }, null)

            try {
                startActivity(share)
            } catch (e: ActivityNotFoundException) {
                toast("공유 가능한 앱이 없습니다.")
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
        setPOIItem()
    }


    private fun setPOIItem() {
        mapView?.removeAllPOIItems()
        mapView?.addPOIItems(
            arrayOf(
                MapUtil.getMapPOIItem(
                    "ㅌㅅㅌ",
                    35.86401751026963,
                    128.6485239265323
                )
            )
        )
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