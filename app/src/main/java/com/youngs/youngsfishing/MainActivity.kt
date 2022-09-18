package com.youngs.youngsfishing

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationManager
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContentProviderCompat.requireContext
import androidx.core.content.ContextCompat
import com.google.gson.Gson
import com.google.gson.JsonObject
import com.youngs.common.Define.PERMISSIONS_REQUEST_CODE
import com.youngs.common.Define.REQUIRED_PERMISSIONS
import com.youngs.common.YoungsFunction
import com.youngs.common.network.NetworkConnect
import com.youngs.common.network.NetworkProgressDialog
import com.youngs.youngsfishing.databinding.ActivityMainBinding
import net.daum.mf.map.api.MapPOIItem
import net.daum.mf.map.api.MapPoint
import net.daum.mf.map.api.MapView
import org.json.JSONArray
import kotlinx.coroutines.*

class MainActivity : AppCompatActivity() {
    lateinit var binding : ActivityMainBinding
    private val eventListener = MarkerEventListener(this)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater,null,false)
        setContentView(binding.root)

        binding.mapView.setCalloutBalloonAdapter(CustomBalloonAdapter(layoutInflater,this@MainActivity))
        binding.mapView.setPOIItemEventListener(eventListener)

        goToNowLocation()

        setPin()

        initButton()

        selectFishingSpot()

    }

    private fun selectFishingSpot() {
        val jsonObject : JsonObject = JsonObject()
//        jsonObject.addProperty("CODE", Define.NOW_LOGIN_USER_CODE)
        NetworkProgressDialog.start(this@MainActivity)
        CoroutineScope(Dispatchers.Default).launch {
            NetworkConnect.connectHTTPS("selectFishingSpot.do",
                jsonObject,
                this@MainActivity // 실패했을때 Toast 메시지를 띄워주기 위한 Context
                , onSuccess = { ->

//                    MyBookListAdapter.instance.clear()
                    val jsonArray : JSONArray = YoungsFunction.stringArrayToJson(NetworkConnect.resultString)
                    val test = ""
//
//                    if (jsonArray.toString() != "[\"\"]") {
//                        val list = Gson().fromJson(
//                            jsonArray.toString(),
//                            Array<MyBookListModel>::class.java
//                        )
//
//                        for (item in list) {
//                            MyBookListAdapter.instance.addItem(item)
//                        }
//                    }
//                    binding.title.text = "현재 책을 ${MyBookListAdapter.instance.itemCount}권 읽었어요"
//
                    NetworkProgressDialog.end()
                }
                , onFailure = {
                    NetworkProgressDialog.end()
                }
            )
        }
    }



    private fun initButton() {
        binding.nowLocation.setOnClickListener(){
            goToNowLocation()
            selectFishingSpot()
        }

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

            val marker: MapPOIItem = MapPOIItem()
            marker.apply {
                itemName = "Default Marker"
                tag = 0
                mapPoint = myLocation
                markerType = MapPOIItem.MarkerType.BluePin // 기본으로 제공하는 BluePin 마커 모양.
                selectedMarkerType = MapPOIItem.MarkerType.RedPin // 마커를 클릭했을때, 기본으로 제공하는 RedPin 마커 모양.
                isDraggable = true
            }
            binding.mapView.addPOIItem(marker)
        }
    }

    private fun goToNowLocation()
    {
        val permissionCheck = ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
        if(permissionCheck == PackageManager.PERMISSION_GRANTED) {
            val lm: LocationManager = getSystemService(Context.LOCATION_SERVICE) as LocationManager
            try {
                val userNowLocation: Location = lm.getLastKnownLocation(LocationManager.NETWORK_PROVIDER)!!
                val uLatitude = userNowLocation.latitude
                val uLongitude = userNowLocation.longitude
                val uNowPosition = MapPoint.mapPointWithGeoCoord(uLatitude, uLongitude)

                binding.mapView.setMapCenterPoint(uNowPosition, true)

            }catch(e: NullPointerException){
                Log.e("LOCATION_ERROR", e.toString())
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                    ActivityCompat.finishAffinity(this)
                }else{
                    ActivityCompat.finishAffinity(this)
                }

                val intent = Intent(this, MainActivity::class.java)
                startActivity(intent)
                System.exit(0)
            }
        }else{
            Toast.makeText(this, "위치 권한이 없습니다.", Toast.LENGTH_SHORT).show()
            ActivityCompat.requestPermissions(this, REQUIRED_PERMISSIONS, PERMISSIONS_REQUEST_CODE )
        }
    }
}