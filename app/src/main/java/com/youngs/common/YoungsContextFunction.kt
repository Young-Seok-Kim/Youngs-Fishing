package com.youngs.common

import android.content.Context
import com.google.gson.JsonObject
import com.youngs.common.network.NetworkConnect
import com.youngs.common.network.NetworkProgressDialog
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import net.daum.mf.map.api.MapPOIItem

object YoungsContextFunction {

    fun insertFishingSpot(context: Context, poiItem: MapPOIItem, onSuccess : () -> Unit) {
        val jsonObject: JsonObject = JsonObject()
        jsonObject.addProperty("spot_name", poiItem.itemName)
        jsonObject.addProperty("latitude", poiItem.mapPoint.mapPointGeoCoord.latitude)
        jsonObject.addProperty("longitude", poiItem.mapPoint.mapPointGeoCoord.longitude)
        jsonObject.addProperty("address", poiItem.userObject.toString())
        NetworkProgressDialog.start(context)
        CoroutineScope(Dispatchers.Default).launch {
            NetworkConnect.connectHTTPS("insertFishingSpot.do",
                jsonObject,
                context = context  // 실패했을때 Toast 메시지를 띄워주기 위한 Context
                , onSuccess = { ->
                    onSuccess()
                    NetworkProgressDialog.end()
                }, onFailure = {
                    NetworkProgressDialog.end()
                }
            )
        }
    }
}