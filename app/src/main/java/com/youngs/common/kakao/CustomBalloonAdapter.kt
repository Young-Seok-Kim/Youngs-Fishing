package com.youngs.common.kakao

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.widget.TextView
import com.youngs.youngsfishing.R
import net.daum.mf.map.api.CalloutBalloonAdapter
import net.daum.mf.map.api.MapPOIItem

class CustomBalloonAdapter(inflater: LayoutInflater,context : Context): CalloutBalloonAdapter {
    // 해당 클래스에서는 글씨만 띄워주자

    private val mContext = context
    private val mCalloutBalloon: View = inflater.inflate(R.layout.balloon_layout, null)
    private val name: TextView = mCalloutBalloon.findViewById(R.id.ball_tv_name)
    private val address: TextView = mCalloutBalloon.findViewById(R.id.ball_tv_address)

    override fun getCalloutBalloon(poiItem: MapPOIItem?): View {
        // 마커 클릭 시 나오는 말풍선
        name.text = poiItem?.itemName   // 해당 마커의 정보 이용 가능
//        address.text = "마커 클릭"
        return mCalloutBalloon
    }

    override fun getPressedCalloutBalloon(poiItem: MapPOIItem?): View {
        // 말풍선 클릭 시
//        address.text = "말풍선 클릭"
        return mCalloutBalloon
    }
}