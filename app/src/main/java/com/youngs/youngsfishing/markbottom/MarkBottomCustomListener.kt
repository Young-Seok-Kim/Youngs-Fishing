package com.youngs.youngsfishing.markbottom

import net.daum.mf.map.api.MapPOIItem

interface MarkBottomCustomListener {
    fun sendPoiItem(poiItem: MapPOIItem)
}