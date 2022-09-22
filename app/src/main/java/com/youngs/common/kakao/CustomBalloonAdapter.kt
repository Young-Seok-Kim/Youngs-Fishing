package com.youngs.common.kakao

import android.app.Activity
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.widget.EditText
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import com.youngs.common.Define
import com.youngs.common.YoungsContextFunction
import com.youngs.youngsfishing.R
import net.daum.mf.map.api.*

class CustomBalloonAdapter(inflater: LayoutInflater): CalloutBalloonAdapter
{
    // 해당 클래스에서는 글씨만 띄워주자

    private val mCalloutBalloon: View = inflater.inflate(R.layout.balloon_layout, null)
    private val name: TextView = mCalloutBalloon.findViewById(R.id.ball_tv_name)
    private val address: TextView = mCalloutBalloon.findViewById(R.id.ball_tv_address)


//    private lateinit var onChangeMarkerListener : OnChangeMarkerListener
//
//    interface OnChangeMarkerListener
//    {
//        fun updateMarker(poiItem: MapPOIItem?)
//    }
//
//    fun setOnChangeMarkerListener(listener: OnChangeMarkerListener)
//    {
//        this@CustomBalloonAdapter.onChangeMarkerListener = listener
//    }
//
//    init {
//        this@CustomBalloonAdapter.setOnChangeMarkerListener(object : OnChangeMarkerListener{
//            override fun updateMarker(poiItem: MapPOIItem?) {
//                name.text = poiItem?.itemName   // 해당 마커의 정보 이용 가능
//                address.text = poiItem?.userObject.toString().let { if (it.isBlank()) it else "" }
//            }
//        })
//    }

    override fun getCalloutBalloon(poiItem: MapPOIItem?): View {
        // 마커 클릭 시 나오는 말풍선
        name.text = poiItem?.itemName   // 해당 마커의 정보 이용 가능
        address.text = poiItem?.userObject.toString().let { it.ifBlank { "" } }

//        onChangeMarkerListener.updateMarker(poiItem)
        return mCalloutBalloon
    }

    override fun getPressedCalloutBalloon(poiItem: MapPOIItem?): View {
        // 말풍선 클릭 시
        return mCalloutBalloon
    }


}