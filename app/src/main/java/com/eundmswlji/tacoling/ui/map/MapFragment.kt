package com.eundmswlji.tacoling.ui.map

import android.Manifest
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.Settings
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RadioButton
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.paging.LoadState
import androidx.paging.PagingData
import com.eundmswlji.tacoling.EventObserver
import com.eundmswlji.tacoling.R
import com.eundmswlji.tacoling.databinding.FragmentMapBinding
import com.eundmswlji.tacoling.ui.BaseFragment
import com.eundmswlji.tacoling.ui.MainActivity
import com.eundmswlji.tacoling.ui.map.GpsPermissionUtil.checkGPS
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
class MapFragment : BaseFragment<FragmentMapBinding>(FragmentMapBinding::inflate),
    MapView.MapViewEventListener,
    MapView.CurrentLocationEventListener, MapView.POIItemEventListener {

    private val locationResultLauncher = registerForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        when {
            permissions.getOrDefault(
                Manifest.permission.ACCESS_FINE_LOCATION,
                false
            ) -> trackingModeOn()
            else -> {
                // No location access granted.
                Toast.makeText(
                    requireContext(),
                    "위치권한을 허용 하지 않으면 내 주변 타코야키 트럭을 찾을 수 없습니다.\n설정에서 대략적인 위치권한과 정확한 위치권한 모두 설정해주세요.",
                    Toast.LENGTH_SHORT
                ).show()
                goToSettings()
            }
        }
    }

    private val viewModel: MapViewModel by viewModels()
    private var job: Job? = null
    private var mapView: MapView? = null
    private val adapter by lazy { MapAdapter(::itemClickListener) }
    private val mapPOIItem = mutableListOf<MapPOIItem>()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.apply {
            lifecycleOwner = viewLifecycleOwner
            viewModel = this@MapFragment.viewModel
        }

        (requireActivity() as? MainActivity)?.showBottomNav()
        initDays()
        setRecyclerView()
        setOnClickListener()
        setObserver()
    }

    private fun goToSettings() {
        val pkg = "package:" + requireActivity().applicationContext.packageName
        val pkgUri = Uri.parse(pkg)
        val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS, pkgUri)
        startActivity(intent)
    }

    private fun requestLocationPermission(
    ) {
        locationResultLauncher.launch(
            arrayOf(
                Manifest.permission.ACCESS_COARSE_LOCATION,
                Manifest.permission.ACCESS_FINE_LOCATION
            )
        )
    }

    private fun setObserver() {
        viewModel.toastEvent.observe(viewLifecycleOwner, EventObserver {
            toast(it)
        })

        viewModel.currentAddress.observe(viewLifecycleOwner, EventObserver {
            binding.tvAddress.editText.clearFocus()
        })

        viewModel.showZeroWasteShop.observe(viewLifecycleOwner) {
//
        }
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
        mapView = MapView(requireActivity())
        binding.mapViewContainer.addView(mapView)
        //Log.d("LOGGING","${mapView.hashCode()}")
        mapView?.apply {
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
            setPOIItemEventListener(this@MapFragment)
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
        mapView?.setMapCenterPoint(mapPoint, true)
    }

    private fun setOnClickListener() {
        binding.buttonMyLocation.setOnClickListener {
            checkGPS(requireActivity())
            requestLocationPermission()
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
            override fun onTextChanged(
                query: CharSequence?,
                start: Int,
                before: Int,
                count: Int
            ) {
                if (!query.isNullOrEmpty()) debounce(query.toString())
            }

            override fun afterTextChanged(s: Editable?) {}
            override fun beforeTextChanged(
                s: CharSequence?,
                start: Int,
                count: Int,
                after: Int
            ) {
            }
        })

        binding.buttonResearch.setOnClickListener {
            val centerPoint = mapView?.mapCenterPoint
            getAddressFromGeoCord(centerPoint)
            setPOIItemsIn3Km(centerPoint)
        }

        binding.buttonZeroWaste.setOnClickListener {
            viewModel.toggleZeroWasteShop()
        }
    }

    private fun trackingModeOff() {
        mapView?.currentLocationTrackingMode =
            MapView.CurrentLocationTrackingMode.TrackingModeOff
    }

    private fun trackingModeOn() {
        mapView?.currentLocationTrackingMode =
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
        mapView?.removeAllPOIItems()
        mapView?.addPOIItems(mapPOIListIn3Km.toTypedArray())
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

    override fun onCalloutBalloonOfPOIItemTouched(
        p0: MapView?,
        p1: MapPOIItem?,
        p2: MapPOIItem.CalloutBalloonButtonType?
    ) {
        val action = MapFragmentDirections.actionMapFragmentToShopFragment(1)
        findNavController().navigate(action)
    }

    override fun onStart() {
        super.onStart()
        requestLocationPermission()
        initMap()
        test()
    }

    override fun onStop() {
        super.onStop()
        binding.mapViewContainer.removeView(mapView)
        mapView = null
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
    override fun onCalloutBalloonOfPOIItemTouched(p0: MapView?, p1: MapPOIItem?) {}
    override fun onDraggablePOIItemMoved(p0: MapView?, p1: MapPOIItem?, p2: MapPoint?) {}
    override fun onPOIItemSelected(p0: MapView?, p1: MapPOIItem?) {}
}