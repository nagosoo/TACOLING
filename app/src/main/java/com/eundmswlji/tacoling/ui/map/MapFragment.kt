package com.eundmswlji.tacoling.ui.map

import android.Manifest
import android.content.Context
import android.content.IntentSender
import android.content.pm.PackageManager
import android.location.LocationManager
import android.os.Build
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RadioButton
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.fragment.app.FragmentActivity
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.paging.LoadState
import androidx.paging.PagingData
import com.eundmswlji.tacoling.EventObserver
import com.eundmswlji.tacoling.MainApplication
import com.eundmswlji.tacoling.R
import com.eundmswlji.tacoling.databinding.FragmentMapBinding
import com.eundmswlji.tacoling.ui.BaseFragment
import com.eundmswlji.tacoling.ui.MainActivity
import com.eundmswlji.tacoling.ui.dialog.NormalDialog
import com.eundmswlji.tacoling.util.MapUtil.getMapPOIItem
import com.eundmswlji.tacoling.util.Util
import com.eundmswlji.tacoling.util.Util.hideKeyboard
import com.eundmswlji.tacoling.util.Util.toast
import com.google.android.gms.common.api.ResolvableApiException
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.LocationSettingsRequest
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import net.daum.mf.map.api.MapPOIItem
import net.daum.mf.map.api.MapPoint
import net.daum.mf.map.api.MapView
import java.util.*
import kotlin.math.abs
import kotlin.math.pow


@AndroidEntryPoint
class MapFragment : BaseFragment(), MapView.MapViewEventListener, MapView.CurrentLocationEventListener {

    private lateinit var binding: FragmentMapBinding
    private val viewModel: MapViewModel by viewModels()
    private var job: Job? = null
    private lateinit var mapView: MapView
    private val adapter by lazy { MapAdapter(::itemClickListener) }

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
        super.onViewCreated(view, savedInstanceState)
        (requireActivity() as? MainActivity)?.showBottomNav()
        initMap()
        initDays()
        setRecyclerView()
        setOnClickListener()
        setObserver()
        test()
        requestPermission(requireContext(), ::trackingModeOn)
    }

    private fun setObserver() {
        viewModel.toastEvent.observe(viewLifecycleOwner, EventObserver {
            toast(it)
        })

        viewModel.currentAddress.observe(viewLifecycleOwner, EventObserver {
            binding.tvJuso.editText.clearFocus()
        })
    }

    private fun setRecyclerView() {
        binding.tvJuso.recyclerView.adapter = adapter
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
            setCustomCurrentLocationMarkerImage(R.drawable.ic_my_location, MapPOIItem.ImageOffset(30, 0))
            setCustomCurrentLocationMarkerTrackingImage(R.drawable.ic_my_location, MapPOIItem.ImageOffset(30, 0))
        }
    }

    private fun initDays() {
        val today = Calendar.getInstance().get(Calendar.DAY_OF_WEEK)
        binding.itemDays.root.findViewWithTag<RadioButton>("$today").isChecked = true
    }

    private fun itemClickListener(x: Double, y: Double, address: String) {
        trackingModeOff()
        binding.tvJuso.recyclerView.isVisible = false
        viewModel.setCurrentJuso(address)
        val mapPoint = MapPoint.mapPointWithGeoCoord(y, x)
        mapView.setMapCenterPoint(mapPoint, true)
    }

    private fun setOnClickListener() {
        binding.buttonMyLocation.setOnClickListener {
            checkGPS(requireContext())
            checkLocationPermission(childFragmentManager, requireActivity())
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
            override fun onTextChanged(query: CharSequence?, start: Int, before: Int, count: Int) {
                if (!query.isNullOrEmpty()) debounce(query.toString())
            }

            override fun afterTextChanged(s: Editable?) {}
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
        })

        binding.buttonResearch.setOnClickListener {
            val centerPoint = mapView.mapCenterPoint
            getJusoFromGeoCord(centerPoint)
            setPOIItemsIn3Km(centerPoint)
        }
    }

    private fun trackingModeOff() {
        if (!onlyCheckPermissions(requireContext())) return
        mapView.currentLocationTrackingMode = MapView.CurrentLocationTrackingMode.TrackingModeOff
    }

    private fun trackingModeOn() {
        if (!onlyCheckPermissions(requireContext())) return
        mapView.currentLocationTrackingMode = MapView.CurrentLocationTrackingMode.TrackingModeOnWithoutHeading
    }

    private fun getJusoFromGeoCord(mapPoint: MapPoint?) {
        viewModel.getJusoFromGeoCord(mapPoint, activity)
    }

    private fun getMapPOIItemsIn3Km(currentPoint: MapPoint?): List<MapPOIItem> {
        if (currentPoint == null) return emptyList()
        val myLatitude = currentPoint.mapPointGeoCoord.latitude
        val myLongitude = currentPoint.mapPointGeoCoord.longitude
        return mapPOIItem.filter {
            val itemLatitude = it.mapPoint.mapPointGeoCoord.latitude //위도
            val itemLongitude = it.mapPoint.mapPointGeoCoord.longitude //경도
            val distance = abs(myLatitude - itemLatitude).times(110.574).pow(2) + abs(myLongitude - itemLongitude).times(111).pow(2)
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
        getJusoFromGeoCord(currentPoint)
        setPOIItemsIn3Km(currentPoint)
    }

    override fun onCurrentLocationUpdateFailed(p0: MapView?) {
        toast("주소를 찾을 수 없습니다.")
    }

    override fun onResume() {
        super.onResume()
        checkGPS(requireContext())
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

    //위치권한 관련
    private fun turnOnLocationPermission() {
        locationResultLauncher.launch(
            arrayOf(
                Manifest.permission.ACCESS_COARSE_LOCATION,
                Manifest.permission.ACCESS_FINE_LOCATION
            )
        )
    }

    private fun checkGPS(context: Context) {
        val lm = context.getSystemService(AppCompatActivity.LOCATION_SERVICE) as LocationManager
        if (!lm.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            turnOnGPS()
        }
    }

    private fun checkLocationPermission(fm: FragmentManager, requireActivity: FragmentActivity) {
        when {
            (shouldShowRationale(requireActivity)) -> {
                NormalDialog(
                    title = "위치권한 설정",
                    message = "내 주변의 타코야키 트럭을 찾기 위해 위치권한을 허용해주세요.",
                    positiveMessage = "네",
                    negativeMessage = "아니요",
                    positiveButtonListener = { turnOnLocationPermission() }
                ).show(
                    fm,
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

    private fun onlyCheckPermissions(requireContext: Context): Boolean {
        val coarsePermissionGranted = ContextCompat.checkSelfPermission(requireContext, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED
        val finePermissionGranted = ContextCompat.checkSelfPermission(requireContext, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
        return coarsePermissionGranted && finePermissionGranted
    }

    private fun requestPermission(requireContext: Context, callBack: () -> Unit) {
        if (!doneFirstRequest()) {//앱 처음 사용시 무조건 퍼미션을 요청한다.
            applyFirstRequest()
            turnOnLocationPermission()
        } else { // 앱 처음 사용이 아니면, 퍼미션 허락 되어있을 때만 tracking 하고, 따로 퍼미션 요청 메시지는 띄우지 x
            if (onlyCheckPermissions(requireContext)) {
                callBack()
            }
        }
    }

    private fun shouldShowRationale(requireActivity: FragmentActivity): Boolean {
        //사용자가 권한 요청을 명시적으로 거부한 경우 true를 반환한다.
        //사용자가 권한 요청을 처음 보거나, 다시 묻지 않음 선택한 경우, 권한을 허용한 경우 false를 반환한다.
        return ActivityCompat.shouldShowRequestPermissionRationale(requireActivity, Manifest.permission.ACCESS_COARSE_LOCATION)
                || ActivityCompat.shouldShowRequestPermissionRationale(requireActivity, Manifest.permission.ACCESS_FINE_LOCATION)
    }

    private fun doneFirstRequest(): Boolean =
        MainApplication.sp.getBoolean("firstRequest", false)

    private fun applyFirstRequest() {
        MainApplication.sp.setBoolean("firstRequest", true)
    }

    private fun turnOnGPS() {
        requireActivity().let {
            val locationRequest = LocationRequest.create()
            locationRequest.priority = LocationRequest.PRIORITY_HIGH_ACCURACY

            val builder = LocationSettingsRequest.Builder()
                .addLocationRequest(locationRequest)

            val task = LocationServices.getSettingsClient(it)
                .checkLocationSettings(builder.build())

            //gps 꺼져 있을 때
            task.addOnFailureListener { e ->
                if (e is ResolvableApiException) {
                    try {
                        //다이어로그 띄움
                        e.startResolutionForResult(
                            it,
                            999
                        )
                    } catch (sendEx: IntentSender.SendIntentException) {
                    }
                }
            }
        }
    }
}