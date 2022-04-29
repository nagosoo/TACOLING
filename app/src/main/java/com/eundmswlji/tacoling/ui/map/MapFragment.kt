package com.eundmswlji.tacoling.ui.map

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.paging.LoadState
import androidx.paging.PagingData
import com.eundmswlji.tacoling.BuildConfig
import com.eundmswlji.tacoling.MapUtil.checkGPS
import com.eundmswlji.tacoling.MapUtil.getMapPOIItem
import com.eundmswlji.tacoling.R
import com.eundmswlji.tacoling.Util
import com.eundmswlji.tacoling.Util.hideKeyboard
import com.eundmswlji.tacoling.Util.toast
import com.eundmswlji.tacoling.databinding.FragmentMapBinding
import com.eundmswlji.tacoling.ui.dialog.NormalDialog
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import net.daum.mf.map.api.MapPOIItem
import net.daum.mf.map.api.MapPoint
import net.daum.mf.map.api.MapReverseGeoCoder
import net.daum.mf.map.api.MapView


@AndroidEntryPoint
class MapFragment : Fragment(), MapView.MapViewEventListener, MapView.CurrentLocationEventListener {
    private lateinit var binding: FragmentMapBinding
    private lateinit var locationResultLauncher: ActivityResultLauncher<Array<String>>
    private var job: Job? = null
    private val viewModel: MapViewModel by viewModels()
    private lateinit var mapView: MapView
    private val adapter by lazy { MapAdapter(::itemClickListener) }
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
        setRecyclerView()
        setOnClickListener()
        test()

        locationResultLauncher = registerForActivityResult(
            ActivityResultContracts.RequestMultiplePermissions()
        ) { map ->
            if (map["android.permission.ACCESS_FINE_LOCATION"] == true && map["android.permission.ACCESS_COARSE_LOCATION"] == true) {
                showMyLocation()
            }
        }
    }

    private fun setRecyclerView() {
        binding.tvJuso.recyclerView.adapter = adapter
    }

    private fun test() {
        val mapPOIItem = mutableListOf<MapPOIItem>()
        mapPOIItem.add(getMapPOIItem("ㅌㅅㅌ", 35.86401751026963, 128.6485239265323))
        mapPOIItem.add(getMapPOIItem("ㅌㅅㅌ", 35.85881638638933, 128.6356195137821))
        mapView.addPOIItems(mapPOIItem.toTypedArray())
    }

    private fun settingMap() {
        MapView.setMapTilePersistentCacheEnabled(true)
        mapView = MapView(activity)
        binding.mapViewContainer.addView(mapView)
        mapView.setZoomLevel(2, true)
        mapView.setMapViewEventListener(this)
        mapView.setCurrentLocationEventListener(this)
        mapView.setCustomCurrentLocationMarkerImage(R.drawable.ic_my_location, MapPOIItem.ImageOffset(108, 0))
        mapView.setCustomCurrentLocationMarkerTrackingImage(R.drawable.ic_my_location, MapPOIItem.ImageOffset(108, 0))
    }

    private fun itemClickListener(x: Double, y: Double, address: String) {
        mapView.currentLocationTrackingMode = MapView.CurrentLocationTrackingMode.TrackingModeOff
        binding.tvJuso.recyclerView.isVisible = false
        with(binding.tvJuso.editText) {
            this.setText(address)
            this.clearFocus()
        }
        val mapPoint = MapPoint.mapPointWithGeoCoord(y, x)
        mapView.setMapCenterPoint(mapPoint, true)
    }

    private fun setOnClickListener() {
        binding.buttonMyLocation.setOnClickListener {
            checkGPSOn()
            checkLocationPermission()
        }

        val debounce = Util.debounce<String>(coroutineScope = lifecycleScope) { query ->
            job?.cancel()
            job = lifecycleScope.launch {
                viewModel.getJuso(query).collectLatest {
                    adapter.submitData(it)
                }
            }
            lifecycleScope.launch {
                adapter.loadStateFlow.collectLatest { loadState ->
                    val isListEmpty = loadState.refresh is LoadState.NotLoading && adapter.itemCount == 0
                    binding.tvNoData.isVisible = isListEmpty
                }
            }
        }

        binding.tvJuso.editText.setOnFocusChangeListener { v, hasFocus ->
            if (hasFocus) {
                job?.cancel()
                job = lifecycleScope.launch {
                    adapter.submitData(PagingData.empty())
                }
                binding.tvJuso.editText.setText("")
                binding.tvJuso.recyclerView.isVisible = true
            } else {
                hideKeyboard(v)
            }
        }

        binding.tvJuso.editText.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(query: CharSequence?, start: Int, before: Int, count: Int) {
                if (!query.isNullOrEmpty()) debounce(query.toString())
            }

            override fun afterTextChanged(s: Editable?) {
            }
        })

        binding.buttonResearch.setOnClickListener {
            val centerPoint = mapView.mapCenterPoint
            getJusoFromGeoCord(centerPoint)
        }
    }

    private fun checkLocationPermission() {
        when {
            onlyCheckPermissions() -> {
                //둘다 혀용 되어 있음
                showMyLocation()
            }
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
        //사용자가 권한 요청을 명시적으로 거부한 경우 true를 반환한다.
        //사용자가 권한 요청을 처음 보거나, 다시 묻지 않음 선택한 경우, 권한을 허용한 경우 false를 반환한다.
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
        checkGPS(requireContext())
    }

    private fun showMyLocation() {
        mapView.currentLocationTrackingMode = MapView.CurrentLocationTrackingMode.TrackingModeOnWithoutHeading
    }

    private fun getJusoFromGeoCord(mapPoint: MapPoint?) {
        mapPoint?.let {
            val currentMapPoint = MapPoint.mapPointWithGeoCoord(mapPoint.mapPointGeoCoord.latitude, mapPoint.mapPointGeoCoord.longitude)
            MapReverseGeoCoder(BuildConfig.appKey, currentMapPoint, object : MapReverseGeoCoder.ReverseGeoCodingResultListener {
                override fun onReverseGeoCoderFoundAddress(p0: MapReverseGeoCoder?, address: String) {
                    binding.tvJuso.editText.setText(address)
                    binding.tvJuso.editText.clearFocus()
                }

                override fun onReverseGeoCoderFailedToFindAddress(p0: MapReverseGeoCoder?) {
                    toast("주소를 찾을 수 없습니다.")
                }
            }, activity).startFindingAddress()
        }
    }

    override fun onMapViewInitialized(p0: MapView?) {
    }

    override fun onMapViewCenterPointMoved(p0: MapView?, p1: MapPoint?) {
        mapView.currentLocationTrackingMode = MapView.CurrentLocationTrackingMode.TrackingModeOff
    }

    override fun onMapViewZoomLevelChanged(p0: MapView?, p1: Int) {
    }

    override fun onMapViewSingleTapped(p0: MapView?, p1: MapPoint?) {
    }

    override fun onMapViewDoubleTapped(p0: MapView?, p1: MapPoint?) {
    }

    override fun onMapViewLongPressed(p0: MapView?, p1: MapPoint?) {
    }

    override fun onMapViewDragStarted(p0: MapView?, p1: MapPoint?) {
    }

    override fun onMapViewDragEnded(p0: MapView?, p1: MapPoint?) {
    }

    override fun onMapViewMoveFinished(p0: MapView?, p1: MapPoint?) {
    }

    override fun onCurrentLocationUpdate(p0: MapView?, currentPoint: MapPoint?, p2: Float) {
        getJusoFromGeoCord(currentPoint)
    }

    override fun onCurrentLocationDeviceHeadingUpdate(p0: MapView?, p1: Float) {
    }

    override fun onCurrentLocationUpdateFailed(p0: MapView?) {
        toast("주소를 찾을 수 없습니다.")
    }

    override fun onCurrentLocationUpdateCancelled(p0: MapView?) {
    }

    override fun onResume() {
        super.onResume()
        checkGPSOn() //gps 는 항상체크
        if (onlyCheckPermissions()) {
            showMyLocation()
        }
    }

}