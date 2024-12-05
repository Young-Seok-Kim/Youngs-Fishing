package com.youngs.youngsfishing

import android.Manifest
import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationManager
import android.net.Uri
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.google.gson.JsonObject
import com.youngs.common.Define.PERMISSIONS_REQUEST_CODE
import com.youngs.common.Define.REQUIRED_PERMISSIONS
import com.youngs.common.YoungsFunction
import com.youngs.common.kakao.MarkerEventListener
import com.youngs.common.network.NetworkConnect
import com.youngs.common.network.NetworkProgressDialog
import com.youngs.common.network.YoungsProgressBar
import com.youngs.youngsfishing.databinding.ActivityMainBinding
import com.youngs.youngsfishing.markbottom.MarkBottomCustomListener
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import net.daum.mf.map.api.MapPOIItem
import net.daum.mf.map.api.MapPoint
import net.daum.mf.map.api.MapView
import org.json.JSONArray
import org.json.JSONObject


class MainActivity : AppCompatActivity(), MarkBottomCustomListener {
    lateinit var binding : ActivityMainBinding

    private val eventListener = MarkerEventListener(this@MainActivity,this, supportFragmentManager)
    val youngsProgressBar: YoungsProgressBar by lazy { YoungsProgressBar(this@MainActivity) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater,null,false)
        setContentView(binding.root)

//        binding.mapView.setCalloutBalloonAdapter(CustomBalloonAdapter(layoutInflater))
        binding.mapView.setPOIItemEventListener(eventListener)

        goToNowLocation(true)

        initButton()

        selectFishingSpot()

        if (checkLocationService()) {
            // GPS가 켜져있을 경우
            permissionCheck(MapView.CurrentLocationTrackingMode.TrackingModeOnWithoutHeading)
        } else {
            // GPS가 꺼져있을 경우
            Toast.makeText(this, "GPS를 켜주세요", Toast.LENGTH_SHORT).show()
        }
    }

    private fun selectFishingSpot() {
        val jsonObject : JsonObject = JsonObject()
//        jsonObject.addProperty("CODE", Define.NOW_LOGIN_USER_CODE)
        youngsProgressBar.show()
        CoroutineScope(Dispatchers.Default).launch {
            NetworkConnect.connectHTTPS("selectFishingSpot.do",
                jsonObject,
                this@MainActivity // 실패했을때 Toast 메시지를 띄워주기 위한 Context
                , onSuccess = { ->

                    val permissionCheck = ContextCompat.checkSelfPermission(this@MainActivity, Manifest.permission.ACCESS_FINE_LOCATION)

                    val jsonArray : JSONArray = YoungsFunction.stringArrayToJson(NetworkConnect.resultString)

                    if(permissionCheck == PackageManager.PERMISSION_GRANTED) { // 권한 확인
                        for (i in 0 until (jsonArray.length()) ) {
                            if (jsonArray.get(i).toString().isBlank()) {
                                continue
                            }
                            val uLatitude : Double = (jsonArray.get(i) as JSONObject).get("latitude").toString().toDouble()
                            val uLongitude : Double = (jsonArray.get(i) as JSONObject).get("longitude").toString().toDouble()
                            val fishingSpot : MapPoint = MapPoint.mapPointWithGeoCoord(uLatitude, uLongitude)

                            val marker: MapPOIItem = MapPOIItem()
                            marker.apply {
                                isShowCalloutBalloonOnTouch = false // 해당값을 false로 놓으면 마커 클릭시 말풍선이 나오지않는다.
                                itemName = (jsonArray.get(i) as JSONObject?)?.get("spot_name")?.toString()
                                tag = (jsonArray.get(i) as JSONObject?)?.get("spot_no").toString().toInt()
                                userObject = (jsonArray.get(i) as JSONObject?)?.get("address")?.toString()
                                mapPoint = fishingSpot
                                markerType = MapPOIItem.MarkerType.BluePin // 기본으로 제공하는 BluePin 마커11 모양.
                                selectedMarkerType = MapPOIItem.MarkerType.RedPin // 마커를 클릭했을때, 기본으로 제공하는 RedPin 마커 모양.
                                isDraggable = false
                            }
                            binding.mapView.addPOIItem(marker)
                        }
                    }
                    else{
                        Toast.makeText(this@MainActivity, "위치 권한이 없습니다.", Toast.LENGTH_SHORT).show()
                        ActivityCompat.requestPermissions(this@MainActivity, REQUIRED_PERMISSIONS, PERMISSIONS_REQUEST_CODE )
                    }

                    youngsProgressBar.dismiss()
                }
                , onFailure = {
                    youngsProgressBar.dismiss()
                }
            )
        }
    }



    private fun initButton() {
        binding.nowLocation.setOnClickListener(object : View.OnClickListener{
            override fun onClick(p0: View?) {
                binding.mapView.removeAllPOIItems()
                goToNowLocation(true)
                selectFishingSpot()

                if (checkLocationService()) {
                    // GPS가 켜져있을 경우
                    permissionCheck(MapView.CurrentLocationTrackingMode.TrackingModeOnWithHeading)
                } else {
                    // GPS가 꺼져있을 경우
                    Toast.makeText(applicationContext, "GPS를 켜주세요", Toast.LENGTH_SHORT).show()
                }
            }
        })

        binding.addPin.setOnClickListener(){
            setPin()
        }
    }

    private fun setPin(){
        val permissionCheck = ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)

        if(permissionCheck == PackageManager.PERMISSION_GRANTED) {
            val lm: LocationManager = getSystemService(Context.LOCATION_SERVICE) as LocationManager
            val userNowLocation: Location = lm.getLastKnownLocation(LocationManager.NETWORK_PROVIDER)!!

            val uLatitude = userNowLocation.latitude
            val uLongitude = userNowLocation.longitude
            val myLocation : MapPoint = MapPoint.mapPointWithGeoCoord(uLatitude, uLongitude)

            val nowMapPoint = MapPoint.mapPointWithGeoCoord(uLatitude, uLongitude)

            binding.mapView.setMapCenterPoint(nowMapPoint, true)

            val marker : MapPOIItem = MapPOIItem()

            marker.apply {
                isShowCalloutBalloonOnTouch = false
                itemName = "신규 마커"
                tag = 0
                mapPoint = myLocation
                markerType = MapPOIItem.MarkerType.YellowPin // 기본으로 제공하는 BluePin 마커 모양.
                selectedMarkerType = MapPOIItem.MarkerType.RedPin // 마커를 클릭했을때, 기본으로 제공하는 RedPin 마커 모양.
                isDraggable = true
            }
            binding.mapView.addPOIItem(marker)
        }
    }



    private fun goToNowLocation(goLocation : Boolean) : MapPoint?
    {
        // goLocation 파라미터가 false면 단순히 현재 위치만 MapPoint로 반환해주고 True면 현재위치로 지도를 이동시켜준다.

        val permissionCheck = ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
        if(permissionCheck == PackageManager.PERMISSION_GRANTED) {
            val lm: LocationManager = getSystemService(Context.LOCATION_SERVICE) as LocationManager
            val locationManager = getSystemService(LOCATION_SERVICE) as LocationManager
            try {
                val userNowLocation: Location = lm.getLastKnownLocation(LocationManager.NETWORK_PROVIDER) ?: return null

                val uLatitude = userNowLocation.latitude
                val uLongitude = userNowLocation.longitude
                val uNowPosition = MapPoint.mapPointWithGeoCoord(uLatitude, uLongitude)

                if (goLocation) {
                    binding.mapView.setMapCenterPoint(uNowPosition, true)
                }

                return uNowPosition

            }catch(e: NullPointerException){
                Log.e("LOCATION_ERROR", e.toString())
                ActivityCompat.finishAffinity(this)

//                val intent = Intent(this, MainActivity::class.java)
//                startActivity(intent)
                System.exit(0)
            }
        }else{
            Toast.makeText(this, "위치 권한이 없습니다.", Toast.LENGTH_SHORT).show()
            ActivityCompat.requestPermissions(this, REQUIRED_PERMISSIONS, PERMISSIONS_REQUEST_CODE)
        }
        return null
    }

    private fun permissionCheck(trackingMode: MapView.CurrentLocationTrackingMode) {

        val preference = getPreferences(MODE_PRIVATE)
        val isFirstCheck = preference.getBoolean("isFirstPermissionCheck", true)
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // 권한이 없는 상태
            if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.ACCESS_FINE_LOCATION)) {
                // 권한 거절 (다시 한 번 물어봄)
                val builder = AlertDialog.Builder(this)
                builder.setMessage("현재 위치를 확인하시려면 위치 권한을 허용해주세요.")
                builder.setPositiveButton("확인") { _, _ ->
                    ActivityCompat.requestPermissions(this, REQUIRED_PERMISSIONS, PERMISSIONS_REQUEST_CODE)
                }
                builder.setNegativeButton("취소") { _, _ ->

                }
                builder.show()
            } else {
                if (isFirstCheck) {
                    // 최초 권한 요청
                    preference.edit().putBoolean("isFirstPermissionCheck", false).apply()
                    ActivityCompat.requestPermissions(this, REQUIRED_PERMISSIONS, PERMISSIONS_REQUEST_CODE)
                } else {
                    // 다시 묻지 않음 클릭 (앱 정보 화면으로 이동)
                    val builder = AlertDialog.Builder(this)
                    builder.setMessage("현재 위치를 확인하시려면 설정에서 위치 권한을 허용해주세요.")
                    builder.setPositiveButton("설정으로 이동") { _, _ ->
                        val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS, Uri.parse("package:$packageName"))
                        startActivity(intent)
                    }
                    builder.setNegativeButton("취소") { _, _ ->

                    }
                    builder.show()
                }
            }
        } else {
            // 권한이 있는 상태
            startTracking(trackingMode)
        }
    }
    // GPS가 켜져있는지 확인
    private fun checkLocationService(): Boolean {
        val locationManager = getSystemService(Context.LOCATION_SERVICE) as LocationManager
        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)
    }

    // 위치추적 시작
    private fun startTracking(trackingMode: MapView.CurrentLocationTrackingMode) {
        if(binding.mapView.currentLocationTrackingMode == MapView.CurrentLocationTrackingMode.TrackingModeOff)
            binding.mapView.currentLocationTrackingMode = trackingMode
        else
            stopTracking()
    }

    // 위치추적 중지
    private fun stopTracking() {
        binding.mapView.currentLocationTrackingMode = MapView.CurrentLocationTrackingMode.TrackingModeOff
    }

    private fun howTogoSpot(poiItem: MapPOIItem){
        try {
            val nowLocation = goToNowLocation(false)

            if (nowLocation?.mapPointGeoCoord?.latitude.toString().isBlank() || nowLocation?.mapPointGeoCoord?.longitude.toString().isBlank()){
                return
            }

            val url : String ="kakaomap://route?sp=${nowLocation?.mapPointGeoCoord?.latitude},${nowLocation?.mapPointGeoCoord?.longitude}&ep=${poiItem.mapPoint.mapPointGeoCoord.latitude},${poiItem.mapPoint.mapPointGeoCoord.longitude}&by=CAR"

            val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
            intent.addCategory(Intent.CATEGORY_BROWSABLE)

            startActivity(intent)

        }catch (e : ActivityNotFoundException){ // 카카오맵이 설치되어있지 않은경우 설치유도
            startActivity(
                Intent(
                    Intent.ACTION_VIEW,
                    Uri.parse("market://details?id=net.daum.android.map")
                )
            )
        }
    }

    override fun sendPoiItem(poiItem: MapPOIItem) {
        howTogoSpot(poiItem)
    }
}