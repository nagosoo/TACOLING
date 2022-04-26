package com.eundmswlji.tacoling.ui.map

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.location.LocationManager
import android.os.Bundle
import android.provider.Settings
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.eundmswlji.tacoling.R
import com.eundmswlji.tacoling.databinding.FragmentMapBinding
import com.eundmswlji.tacoling.ui.dialog.NormalDialog
import net.daum.mf.map.api.MapPOIItem
import net.daum.mf.map.api.MapPoint
import net.daum.mf.map.api.MapView

class MapFragment : Fragment(), MapView.MapViewEventListener {
    private lateinit var binding: FragmentMapBinding
    private lateinit var locationResultLauncher: ActivityResultLauncher<Array<String>>
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentMapBinding.inflate(inflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        settingMap()
        checkGPSOn() //gps 는 항상체크
        setOnClickListener()
        if (onlyCheckPermissions()) {
            showMyLocation()
        }
        test()

        locationResultLauncher = registerForActivityResult(
            ActivityResultContracts.RequestMultiplePermissions()
        ) { map ->
            if (map["android.permission.ACCESS_FINE_LOCATION"] == true && map["android.permission.ACCESS_COARSE_LOCATION"] == true) {
                showMyLocation()
            }
        }


        val mapPoint = MapPoint.mapPointWithGeoCoord(35.85881638638933,128.6356195137821)
        binding.mapView.setMapCenterPoint(mapPoint, true)
    }

    private fun test() {
        val mapPOIItem = mutableListOf<MapPOIItem>()
        mapPOIItem.add(getMapPOIItem("ㅌㅅㅌ", 35.86401751026963, 128.6485239265323))
        mapPOIItem.add(getMapPOIItem("ㅌㅅㅌ", 35.85881638638933, 128.6356195137821))
        binding.mapView.addPOIItems(mapPOIItem.toTypedArray())
    }

    private fun getMapPOIItem(name: String, latitude: Double, longitude: Double): MapPOIItem {
        return MapPOIItem().apply {
            this.itemName = name
            this.mapPoint = MapPoint.mapPointWithGeoCoord(latitude, longitude)
            this.markerType = MapPOIItem.MarkerType.CustomImage
            this.customImageResourceId = R.drawable.ic_takoyaki
            this.isShowDisclosureButtonOnCalloutBalloon = false
        }
    }

    private fun settingMap() {
        binding.mapView.setZoomLevel(2, true)
        binding.mapView.setMapViewEventListener(this)
        binding.mapView.setCustomCurrentLocationMarkerImage(R.drawable.ic_my_location, MapPOIItem.ImageOffset(108, 0))
        binding.mapView.setCustomCurrentLocationMarkerTrackingImage(R.drawable.ic_my_location, MapPOIItem.ImageOffset(108, 0))
    }

    override fun onMapViewInitialized(p0: MapView?) {
        //     TODO("Not yet implemented")
    }

    override fun onMapViewCenterPointMoved(p0: MapView?, p1: MapPoint?) {
        binding.mapView.currentLocationTrackingMode = MapView.CurrentLocationTrackingMode.TrackingModeOff
    }

    override fun onMapViewZoomLevelChanged(p0: MapView?, p1: Int) {
        //    TODO("Not yet implemented")
    }

    override fun onMapViewSingleTapped(p0: MapView?, p1: MapPoint?) {
    }

    override fun onMapViewDoubleTapped(p0: MapView?, p1: MapPoint?) {
        // TODO("Not yet implemented")
    }

    override fun onMapViewLongPressed(p0: MapView?, p1: MapPoint?) {
        //   TODO("Not yet implemented")
    }

    override fun onMapViewDragStarted(p0: MapView?, p1: MapPoint?) {
    }

    override fun onMapViewDragEnded(p0: MapView?, p1: MapPoint?) {
        //   TODO("Not yet implemented")
    }

    override fun onMapViewMoveFinished(p0: MapView?, p1: MapPoint?) {

    }

    private fun setOnClickListener() {
        binding.buttonMyLocation.setOnClickListener {
            checkGPSOn()
            checkLocationPermission()
        }
    }


    private fun checkLocationPermission() {
        when {
            onlyCheckPermissions() -> {
                //둘다 혀용 되어 있음
                showMyLocation()
            }
            //사용자가 권한 요청을 명시적으로 거부한 경우 true를 반환한다.
            //사용자가 권한 요청을 처음 보거나, 다시 묻지 않음 선택한 경우, 권한을 허용한 경우 false를 반환한다.
            (shouldShowDialog()) -> {
                NormalDialog(title = "위치권한 설정", message = "위치권한 설정을 허용해주세요.", positiveMessage = "네", negativeMessage = "아니요", positiveButtonListener = ::turnOnLocationPermission).show(
                    childFragmentManager,
                    null
                )
            }
            else -> {
                // You can directly ask for the permission.
                // The registered ActivityResultCallback gets the result of this request.
                turnOnLocationPermission()
            }
        }
    }

    private fun shouldShowDialog(): Boolean {
        return shouldShowRequestPermissionRationale(Manifest.permission.ACCESS_COARSE_LOCATION) || shouldShowRequestPermissionRationale(Manifest.permission.ACCESS_FINE_LOCATION)
    }

    private fun turnOnLocationPermission() {
        locationResultLauncher.launch(
            arrayOf(
                Manifest.permission.ACCESS_COARSE_LOCATION,
                Manifest.permission.ACCESS_FINE_LOCATION
            )
        )
    }

    private fun onlyCheckPermissions(): Boolean {
        val coarsePermissionGranted = ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED
        val finePermissionGranted = ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
        return coarsePermissionGranted && finePermissionGranted
    }

    private fun checkGPSOn() {
        val lm = requireActivity().getSystemService(AppCompatActivity.LOCATION_SERVICE) as LocationManager
        if (!lm.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            val intent = Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS)
            startActivity(intent)
        }
    }

    private fun showMyLocation() {
        binding.mapView.currentLocationTrackingMode = MapView.CurrentLocationTrackingMode.TrackingModeOnWithoutHeading
    }

}