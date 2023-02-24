package com.eundmswlji.tacoling.ui.map

import android.Manifest
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.eundmswlji.tacoling.BuildConfig
import com.eundmswlji.tacoling.EventObserver
import com.eundmswlji.tacoling.R
import com.eundmswlji.tacoling.databinding.FragmentMapBinding
import com.eundmswlji.tacoling.ui.BaseFragment
import com.eundmswlji.tacoling.ui.MainActivity
import com.eundmswlji.tacoling.util.GpsPermissionUtil.checkGPS
import com.eundmswlji.tacoling.util.MapUtil.getMapPOIItem
import com.eundmswlji.tacoling.util.Util.toast
import dagger.hilt.android.AndroidEntryPoint
import net.daum.mf.map.api.MapPOIItem
import net.daum.mf.map.api.MapPoint
import net.daum.mf.map.api.MapReverseGeoCoder
import net.daum.mf.map.api.MapView


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
    private var mapView: MapView? = null
    private val mapPOIItem = mutableListOf<MapPOIItem>()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.apply {
            lifecycleOwner = viewLifecycleOwner
            viewModel = this@MapFragment.viewModel
        }

        (requireActivity() as? MainActivity)?.showBottomNav()
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
    }

    private fun test() {
        mapPOIItem.add(getMapPOIItem("ㅌㅅㅌ", 35.86401751026963, 128.6485239265323))
        mapPOIItem.add(getMapPOIItem("ㅌㅅㅌ", 35.85881638638933, 128.6356195137821))
    }

    private fun initMap() {
        MapView.setMapTilePersistentCacheEnabled(true)
        mapView = MapView(requireActivity())
        binding.mapviewContainer.addView(mapView)
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
//
//    private fun itemClickListener(x: Double, y: Double, address: String) {
//        trackingModeOff()
//        binding.recyclerView.isVisible = false
//        viewModel.setCurrentAddress(address)
//        val mapPoint = MapPoint.mapPointWithGeoCoord(y, x)
//        mapView?.setMapCenterPoint(mapPoint, true)
//    }

    private fun setOnClickListener() {
        binding.buttonLocation.setOnClickListener {
            checkGPS(requireActivity())
            requestLocationPermission()
        }

        binding.textViewSearch.setOnClickListener {
            val address = viewModel.currentAddress.value!!
            val action = MapFragmentDirections.actionMapFragmentToAddressSearchFragment(address)
            findNavController().navigate(action)
        }

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
        mapPoint?.let {
            val currentMapPoint = MapPoint.mapPointWithGeoCoord(
                mapPoint.mapPointGeoCoord.latitude,
                mapPoint.mapPointGeoCoord.longitude
            )
            MapReverseGeoCoder(
                BuildConfig.appKey,
                currentMapPoint,
                object : MapReverseGeoCoder.ReverseGeoCodingResultListener {
                    override fun onReverseGeoCoderFoundAddress(
                        p0: MapReverseGeoCoder?,
                        address: String
                    ) {
                        viewModel.setCurrentAddress(address)
                    }

                    override fun onReverseGeoCoderFailedToFindAddress(p0: MapReverseGeoCoder?) {
                        toast("주소를 찾을 수 없습니다.")
                    }
                },
                requireActivity()
            ).startFindingAddress()
        }
    }


    private fun getMapPOIItemsIn3Km(currentPoint: MapPoint?): List<MapPOIItem> {
        if (currentPoint == null) return emptyList()
        val myLatitude = currentPoint.mapPointGeoCoord.latitude
        val myLongitude = currentPoint.mapPointGeoCoord.longitude
        return mapPOIItem
//        return mapPOIItem.filter {
//            val itemLatitude = it.mapPoint.mapPointGeoCoord.latitude //위도
//            val itemLongitude = it.mapPoint.mapPointGeoCoord.longitude //경도
//            val distance = getKmFromHereToShop(myLatitude, itemLatitude, myLongitude, itemLongitude)
//            distance <= 9 //현재위치에서 3km 이내인것 보여주기
//        }
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

        findNavController().currentBackStackEntry?.savedStateHandle?.getLiveData<String>("address")
            ?.observe(viewLifecycleOwner) {
                Log.d("logging", it)
            }
    }

    override fun onStop() {
        super.onStop()
        binding.mapviewContainer.removeView(mapView)
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