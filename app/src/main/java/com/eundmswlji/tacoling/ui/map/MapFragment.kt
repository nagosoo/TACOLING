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
import com.eundmswlji.tacoling.data.model.MapViewLocation
import com.eundmswlji.tacoling.databinding.FragmentMapBinding
import com.eundmswlji.tacoling.ui.BaseFragment
import com.eundmswlji.tacoling.ui.MainActivity
import com.eundmswlji.tacoling.ui.dialog.MapLoadingDialog
import com.eundmswlji.tacoling.util.GpsPermissionUtil.checkGPS
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

    private val loadingDialog by lazy { MapLoadingDialog() }
    private val viewModel: MapViewModel by viewModels()
    private var mapView: MapView? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel.getShopList()
    }

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
            viewModel.getDailyShopList()
        }

        viewModel.currentAddress.observe(viewLifecycleOwner) {
            dismissLoadingDialog()
        }

        viewModel.dailyShopList.observe(viewLifecycleOwner) {
            viewModel.getShopIn3Km()
        }

        viewModel.dailyShopIn3KmList.observe(viewLifecycleOwner) {
            showShopPOIITem()
        }

        viewModel.selectedDate.observe(viewLifecycleOwner) {
            val date = viewModel.setTodayDateIndex()
            viewModel.getDailyShopList(date)
        }

        viewModel.showOnlyZeroWasteShop.observe(viewLifecycleOwner) {
            showShopPOIITem()
        }
    }

    private fun showShopPOIITem() {
        filterShopByZeroWaste(viewModel.showOnlyZeroWasteShop.value ?: false)?.let {
            mapView?.removeAllPOIItems()
            mapView?.addPOIItems(it.toTypedArray())
        }
    }

    private fun filterShopByZeroWaste(enableZeroWaste: Boolean): List<MapPOIItem>? {
        return viewModel.dailyShopIn3KmList.value?.filter {
            val enableZeroWasteShop = it.userObject
            if (enableZeroWaste) enableZeroWasteShop == enableZeroWaste
            else true
        }
    }

    private fun initMap() {
        MapView.setMapTilePersistentCacheEnabled(true)
        mapView = MapView(requireActivity())
        binding.mapviewContainer.addView(mapView)
        mapView?.apply {
            setZoomLevel(5, true)
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
                val action =
                    MapFragmentDirections.actionMapFragmentToAddressSearchFragment(address)
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
        showLoadingDialog()
        val selectedAddress =
            findNavController().currentBackStackEntry?.savedStateHandle?.getLiveData<MapViewLocation>(
                "address"
            )

        selectedAddress?.value?.let {
            trackingModeOff()
            val latitude = it.coordinates.latitude
            val longitude = it.coordinates.longitude
            mapView?.setMapCenterPoint(MapPoint.mapPointWithGeoCoord(latitude, longitude), true)
            viewModel.setCurrentGeoCord(latitude, longitude)
            findNavController().currentBackStackEntry?.savedStateHandle?.remove<MapViewLocation>(
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
        viewModel.currentGeoCord.value?.let {
            val currentMapPoint = MapPoint.mapPointWithGeoCoord(it.latitude, it.longitude)
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
                        dismissLoadingDialog()
                        toast("주소를 찾을 수 없습니다.")
                    }
                },
                requireActivity()
            ).startFindingAddress()
        }
    }

    private fun dismissLoadingDialog() {
        if (loadingDialog.isResumed) {
            loadingDialog.dismiss()
        }
    }

    private fun showLoadingDialog() {
        if (!loadingDialog.isResumed) {
            loadingDialog.show(childFragmentManager, null)
        }
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
        }
    }

    override fun onCurrentLocationUpdateFailed(p0: MapView?) {
        dismissLoadingDialog()
        toast("주소를 찾을 수 없습니다.")
    }

    override fun onCalloutBalloonOfPOIItemTouched(
        p0: MapView?,
        shop: MapPOIItem,
        p2: MapPOIItem.CalloutBalloonButtonType?
    ) {
        val shopId = shop.tag
        val action = MapFragmentDirections.actionMapFragmentToShopFragment(shopId)
        findNavController().navigate(action)
    }

    override fun onStart() {
        super.onStart()
        initMap()
        requestLocationPermission()
    }

    override fun onStop() {
        super.onStop()
        dismissLoadingDialog()
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