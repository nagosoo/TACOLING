package com.eundmswlji.tacoling.ui.map

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.view.View
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
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
            ) -> {
                setMapCenter()
            }
            else -> {
                toast(getString(R.string.location_permission_request))
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

        when {
            ///권한 허용 되었을 때
            ContextCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED -> {
                setMapCenter()
            }

            //사용자에게 권한이 필요한 이유를 다시 알려주고 권한요청을 다시한다.
            ActivityCompat.shouldShowRequestPermissionRationale(
                requireActivity(),
                Manifest.permission.ACCESS_FINE_LOCATION
            ) -> {
                toast(getString(R.string.location_permission_request))
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                    locationResultLauncher.launch(
                        arrayOf(
                            Manifest.permission.ACCESS_FINE_LOCATION,
                            Manifest.permission.ACCESS_COARSE_LOCATION
                        )
                    )
                }
            }

            //권한이 허용되지도 않고, 사용자가 명시적으로 거부하지도 않은 경우 - 권한 요청
            else -> {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                    locationResultLauncher.launch(
                        arrayOf(
                            Manifest.permission.ACCESS_FINE_LOCATION,
                            Manifest.permission.ACCESS_COARSE_LOCATION
                        )
                    )
                }
            }
        }
    }


    private fun setObserver() {
        viewModel.toastEvent.observe(viewLifecycleOwner, EventObserver {
            toast(it)
        })

        viewModel.currentGeoCord.observe(viewLifecycleOwner) {
            getAddressFromGeoCord()
        }
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

    private fun setOnClickListener() {
        binding.buttonLocation.setOnClickListener {
            checkGPS(requireActivity())
            requestLocationPermission()
        }

        binding.textViewSearch.setOnClickListener {
            viewModel.currentAddress.value?.let { address ->
                val action = MapFragmentDirections.actionMapFragmentToAddressSearchFragment(address)
                findNavController().navigate(action)
            }
        }

        binding.buttonResearch.setOnClickListener {
            val centerPoint = mapView?.mapCenterPoint
            centerPoint?.let {
                viewModel.setCurrentGeoCord(
                    it.mapPointGeoCoord.latitude,
                    it.mapPointGeoCoord.longitude
                )
            }
        }

        binding.buttonZeroWaste.setOnClickListener {
            viewModel.toggleZeroWasteShop()
        }
    }

    private fun setMapCenter() {
        val selectedAddress =
            findNavController().currentBackStackEntry?.savedStateHandle?.getLiveData<Map<String, String>>(
                "address"
            )

        selectedAddress?.value?.let {
            trackingModeOff()
            val latitude = it["latitude"]!!.toDouble()
            val longitude = it["longitude"]!!.toDouble()
            mapView?.setMapCenterPoint(MapPoint.mapPointWithGeoCoord(latitude, longitude), true)
            viewModel.setCurrentGeoCord(latitude, longitude)
            findNavController().currentBackStackEntry?.savedStateHandle?.remove<Map<String, String>>(
                "address"
            )
            return
        }
        trackingModeOn()
    }

    private fun trackingModeOff() {
        mapView?.currentLocationTrackingMode =
            MapView.CurrentLocationTrackingMode.TrackingModeOff
    }

    private fun trackingModeOn() {
        mapView?.currentLocationTrackingMode =
            MapView.CurrentLocationTrackingMode.TrackingModeOnWithoutHeading
    }

    private fun getAddressFromGeoCord() {
        viewModel.currentGeoCord.value?.get("latitude")?.let { lat ->
            viewModel.currentGeoCord.value?.get("longitude")?.let { lon ->
                val currentMapPoint = MapPoint.mapPointWithGeoCoord(lat, lon)
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

    //단말의 위치가 변화 되었을 때, trackingModeOn
    override fun onCurrentLocationUpdate(p0: MapView?, currentPoint: MapPoint?, p2: Float) {
        currentPoint?.let {
            viewModel.setCurrentGeoCord(
                it.mapPointGeoCoord.latitude,
                it.mapPointGeoCoord.longitude
            )
            setPOIItemsIn3Km(it)
        }
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
        initMap()
        requestLocationPermission()
        test()
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