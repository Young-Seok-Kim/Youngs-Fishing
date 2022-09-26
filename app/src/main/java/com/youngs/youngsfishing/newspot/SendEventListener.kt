package com.youngs.youngsfishing.newspot

import net.daum.mf.map.api.MapPOIItem

interface SendEventListener {
    fun sendFishList(s : ArrayList<String>)
    fun sendPoiItemInfo(poiItem: MapPOIItem)
}