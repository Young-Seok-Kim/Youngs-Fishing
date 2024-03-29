package com.youngs.common.kakao

import android.util.Log
import net.daum.mf.map.api.MapPOIItem
import net.daum.mf.map.api.MapReverseGeoCoder

class FindGeoToAddressListener(val poiItem: MapPOIItem?) : MapReverseGeoCoder.ReverseGeoCodingResultListener {
    // 위도, 경도로 주소찾는 리스너
    override fun onReverseGeoCoderFoundAddress(p0: MapReverseGeoCoder?, p1: String?) {
        Log.d("주소찾기", p1.toString()) // 주소값

        poiItem?. userObject = p1.toString()
    }

    override fun onReverseGeoCoderFailedToFindAddress(p0: MapReverseGeoCoder?) {
        Log.d("주소찾기","실패")
    }
}