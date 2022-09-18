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
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.youngs.common.Define.PERMISSIONS_REQUEST_CODE
import com.youngs.common.Define.REQUIRED_PERMISSIONS
import com.youngs.youngsfishing.databinding.ActivityMainBinding
import net.daum.mf.map.api.CalloutBalloonAdapter
import net.daum.mf.map.api.MapPOIItem
import net.daum.mf.map.api.MapPoint
import net.daum.mf.map.api.MapView


class MainActivity : AppCompatActivity() {
    lateinit var binding : ActivityMainBinding
    lateinit var mapView : MapView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater,null,false)
        setContentView(binding.root)
        mapView = MapView(this)

        mapView.setCalloutBalloonAdapter(CustomBalloonAdapter(layoutInflater))

        val mapViewContainer = binding.frameLayout

        mapViewContainer.addView(mapView)
        goToNowLocation()

        setPin()

        initButton()

    }

    private fun initButton() {
        binding.nowLocation.setOnClickListener(){
            goToNowLocation()
        }

        binding.addPin.setOnClickListener(){
            setPin()
        }
    }

    private fun setPin(){
        val permissionCheck = ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
        if(permissionCheck == PackageManager.PERMISSION_GRANTED) {
            val lm: LocationManager = getSystemService(Context.LOCATION_SERVICE) as LocationManager
            val userNowLocation: Location =
                lm.getLastKnownLocation(LocationManager.NETWORK_PROVIDER)!!
            val uLatitude = userNowLocation.latitude
            val uLongitude = userNowLocation.longitude
            val myLocation: MapPoint = MapPoint.mapPointWithGeoCoord(uLatitude, uLongitude)

            val marker: MapPOIItem = MapPOIItem()
            marker.apply {
                setItemName("Default Marker")
                setTag(0)
                setMapPoint(myLocation)
                setMarkerType(MapPOIItem.MarkerType.BluePin) // 기본으로 제공하는 BluePin 마커 모양.
                setSelectedMarkerType(MapPOIItem.MarkerType.RedPin) // 마커를 클릭했을때, 기본으로 제공하는 RedPin 마커 모양.
                isDraggable = true
            }
            mapView.addPOIItem(marker)
        }
    }

    private fun goToNowLocation()
    {
        val permissionCheck = ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
        if(permissionCheck == PackageManager.PERMISSION_GRANTED) {
            val lm: LocationManager = getSystemService(Context.LOCATION_SERVICE) as LocationManager
            try {
                val userNowLocation: Location =
                    lm.getLastKnownLocation(LocationManager.NETWORK_PROVIDER)!!
                val uLatitude = userNowLocation.latitude
                val uLongitude = userNowLocation.longitude
                val uNowPosition = MapPoint.mapPointWithGeoCoord(uLatitude, uLongitude)
                mapView.setMapCenterPoint(uNowPosition, true)
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