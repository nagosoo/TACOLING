package com.eundmswlji.tacoling.ui.map

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RadioButton
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.paging.LoadState
import androidx.paging.PagingData
import com.eundmswlji.tacoling.EventObserver
import com.eundmswlji.tacoling.R
import com.eundmswlji.tacoling.databinding.FragmentMapBinding
import com.eundmswlji.tacoling.ui.BaseFragment
import com.eundmswlji.tacoling.ui.MainActivity
import com.eundmswlji.tacoling.ui.dialog.NormalDialog
import com.eundmswlji.tacoling.ui.dialog.NormalDialogFactory
import com.eundmswlji.tacoling.ui.map.GpsPermissionUtil.checkGPS
import com.eundmswlji.tacoling.ui.map.LocationPermissionUtil.onlyCheckPermissions
import com.eundmswlji.tacoling.ui.map.LocationPermissionUtil.requestPermission
import com.eundmswlji.tacoling.ui.map.LocationPermissionUtil.shouldShowRationale
import com.eundmswlji.tacoling.ui.map.LocationPermissionUtil.launchLocationLauncher
import com.eundmswlji.tacoling.util.MapUtil.geoToKm
import com.eundmswlji.tacoling.util.MapUtil.getMapPOIItem
import com.eundmswlji.tacoling.util.Util
import com.eundmswlji.tacoling.util.Util.hideKeyboard
import com.eundmswlji.tacoling.util.Util.toast
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import net.daum.mf.map.api.MapPOIItem
import net.daum.mf.map.api.MapPoint
import net.daum.mf.map.api.MapView
import java.util.*


@AndroidEntryPoint
class MapFragment : BaseFragment(), MapView.MapViewEventListener,
    MapView.CurrentLocationEventListener {

    private lateinit var binding: FragmentMapBinding
    private val viewModel: MapViewModel by viewModels()
    private var job: Job? = null
    private lateinit var mapView: MapView
    private val adapter by lazy { MapAdapter(::itemClickListener) }
    private val normalDialogFactory by lazy {
        NormalDialogFactory(
            title = "위치권한 설정",
            message = "내 주변의 타코야키 트럭을 찾기 위해 위치권한을 허용해주세요.",
            positiveMessage = "네",
            negativeMessage = "아니요",
            positiveButtonListener = { launchLocationLauncher(locationResultLauncher) }
        )
    }

    private val mapPOIItem = mutableListOf<MapPOIItem>()

    private val locationResultLauncher = registerForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        if (!permissions.values.contains(false)) {
            trackingModeOn()
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentMapBinding.inflate(inflater).apply {
            lifecycleOwner = viewLifecycleOwner
            viewModel = this@MapFragment.viewModel
        }
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        childFragmentManager.fragmentFactory = normalDialogFactory
        super.onViewCreated(view, savedInstanceState)
        (requireActivity() as? MainActivity)?.showBottomNav()
        initMap()
        initDays()
        setRecyclerView()
        setOnClickListener()
        setObserver()
        test()
        requestPermission(requireContext(), ::trackingModeOn, locationResultLauncher)
    }

    private fun setObserver() {
        viewModel.toastEvent.observe(viewLifecycleOwner, EventObserver {
            toast(it)
        })

        viewModel.currentAddress.observe(viewLifecycleOwner, EventObserver {
            binding.tvAddress.editText.clearFocus()
        })
    }

    private fun setRecyclerView() {
        binding.tvAddress.recyclerView.adapter = adapter
    }

    private fun test() {
        mapPOIItem.add(getMapPOIItem("ㅌㅅㅌ", 35.86401751026963, 128.6485239265323))
        mapPOIItem.add(getMapPOIItem("ㅌㅅㅌ", 35.85881638638933, 128.6356195137821))
    }

    private fun initMap() {
        MapView.setMapTilePersistentCacheEnabled(true)
        mapView = MapView(activity)
        binding.mapViewContainer.addView(mapView)
        mapView.apply {
            setZoomLevel(2, true)
            setMapViewEventListener(this@MapFragment)
            setCurrentLocationEventListener(this@MapFragment)
            setCustomCurrentLocationMarkerImage(
                R.drawable.ic_my_location,
                MapPOIItem.ImageOffset(30, 0)
            )
            setCustomCurrentLocationMarkerTrackingImage(
                R.drawable.ic_my_location,
                MapPOIItem.ImageOffset(30, 0)
            )
        }
    }

    private fun initDays() {
        val today = Calendar.getInstance().get(Calendar.DAY_OF_WEEK)
        binding.itemDays.root.findViewWithTag<RadioButton>("$today").isChecked = true
    }

    private fun itemClickListener(x: Double, y: Double, address: String) {
        trackingModeOff()
        binding.tvAddress.recyclerView.isVisible = false
        viewModel.setCurrentAddress(address)
        val mapPoint = MapPoint.mapPointWithGeoCoord(y, x)
        mapView.setMapCenterPoint(mapPoint, true)
    }

    private fun setOnClickListener() {
        binding.buttonMyLocation.setOnClickListener {
            checkGPS(requireActivity())
            checkLocationPermission()
        }

        val debounce = Util.debounce<String>(coroutineScope = lifecycleScope) { query ->
            job?.cancel()
            job = lifecycleScope.launch {
                viewModel.getAddress(query).collectLatest {
                    adapter.submitData(it)
                }
            }
            lifecycleScope.launch {
                adapter.loadStateFlow.collectLatest { loadState ->
                    val isListEmpty =
                        loadState.refresh is LoadState.NotLoading && adapter.itemCount == 0
                    binding.tvNoData.isVisible = isListEmpty
                }
            }
        }

        binding.tvAddress.editText.setOnFocusChangeListener { v, hasFocus ->
            if (hasFocus) {
                job?.cancel()
                job = lifecycleScope.launch {
                    adapter.submitData(PagingData.empty())
                }
                binding.tvAddress.editText.setText("")
                binding.tvAddress.recyclerView.isVisible = true
            } else {
                hideKeyboard(v)
            }
        }

        binding.tvAddress.editText.addTextChangedListener(object : TextWatcher {
            override fun onTextChanged(query: CharSequence?, start: Int, before: Int, count: Int) {
                if (!query.isNullOrEmpty()) debounce(query.toString())
            }

            override fun afterTextChanged(s: Editable?) {}
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
        })

        binding.buttonResearch.setOnClickListener {
            val centerPoint = mapView.mapCenterPoint
            getAddressFromGeoCord(centerPoint)
            setPOIItemsIn3Km(centerPoint)
        }
    }

    private fun trackingModeOff() {
        if (!onlyCheckPermissions(requireContext())) return
        mapView.currentLocationTrackingMode = MapView.CurrentLocationTrackingMode.TrackingModeOff
    }

    private fun trackingModeOn() {
        if (!onlyCheckPermissions(requireContext())) return
        mapView.currentLocationTrackingMode =
            MapView.CurrentLocationTrackingMode.TrackingModeOnWithoutHeading
    }

    private fun getAddressFromGeoCord(mapPoint: MapPoint?) {
        viewModel.getAddressFromGeoCord(mapPoint, activity)
    }

    private fun getMapPOIItemsIn3Km(currentPoint: MapPoint?): List<MapPOIItem> {
        if (currentPoint == null) return emptyList()
        val myLatitude = currentPoint.mapPointGeoCoord.latitude
        val myLongitude = currentPoint.mapPointGeoCoord.longitude
        return mapPOIItem.filter {
            val itemLatitude = it.mapPoint.mapPointGeoCoord.latitude //위도
            val itemLongitude = it.mapPoint.mapPointGeoCoord.longitude //경도
            val distance = geoToKm(myLatitude, itemLatitude, myLongitude, itemLongitude)
            distance <= 9 //현재위치에서 3km 이내인것 보여주기
        }
    }

    private fun setPOIItemsIn3Km(centerPoint: MapPoint?) {
        val mapPOIListIn3Km = getMapPOIItemsIn3Km(centerPoint)
        mapView.removeAllPOIItems()
        mapView.addPOIItems(mapPOIListIn3Km.toTypedArray())
    }

    override fun onMapViewCenterPointMoved(p0: MapView?, currentPoint: MapPoint?) {
        trackingModeOff()
    }

    override fun onCurrentLocationUpdate(p0: MapView?, currentPoint: MapPoint?, p2: Float) {
        getAddressFromGeoCord(currentPoint)
        setPOIItemsIn3Km(currentPoint)
    }

    override fun onCurrentLocationUpdateFailed(p0: MapView?) {
        toast("주소를 찾을 수 없습니다.")
    }

    override fun onResume() {
        super.onResume()
        checkGPS(requireActivity())
    }

    override fun onDestroyView() {
        super.onDestroyView()
        binding.mapViewContainer.removeView(mapView)
    }

    override fun onMapViewInitialized(p0: MapView?) {}

    override fun onCurrentLocationDeviceHeadingUpdate(p0: MapView?, p1: Float) {}

    override fun onCurrentLocationUpdateCancelled(p0: MapView?) {}

    override fun onMapViewZoomLevelChanged(p0: MapView?, p1: Int) {}

    override fun onMapViewSingleTapped(p0: MapView?, p1: MapPoint?) {}

    override fun onMapViewDoubleTapped(p0: MapView?, p1: MapPoint?) {}

    override fun onMapViewLongPressed(p0: MapView?, p1: MapPoint?) {}

    override fun onMapViewDragStarted(p0: MapView?, p1: MapPoint?) {}

    override fun onMapViewDragEnded(p0: MapView?, p1: MapPoint?) {}

    override fun onMapViewMoveFinished(p0: MapView?, p1: MapPoint?) {}

    private fun checkLocationPermission() {
        when {
            (shouldShowRationale(requireActivity())) -> {
                //권한 요청을 이전에 거부 했을 시 , 다이어로그 띄움
                childFragmentManager.fragmentFactory.instantiate(
                    requireActivity().classLoader,
                    NormalDialog::class.java.name
                )
            }
            else -> {
                // You can directly ask for the permission.
                // The registered ActivityResultCallback gets the result of this request.
                launchLocationLauncher(locationResultLauncher)
            }
        }
    }

}