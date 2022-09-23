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

    fun insertFishingSpot(context: Context, poiItem: MapPOIItem, onSuccess : () -> Unit, spotName : String?) : MapPOIItem?{
        var mPoiItem : MapPOIItem? = null
        val jsonObject: JsonObject = JsonObject()
        jsonObject.addProperty("spot_name", if(spotName.isNullOrBlank()) poiItem.itemName else spotName)
        jsonObject.addProperty("latitude", poiItem.mapPoint.mapPointGeoCoord.latitude)
        jsonObject.addProperty("longitude", poiItem.mapPoint.mapPointGeoCoord.longitude)
        jsonObject.addProperty("address", if (poiItem?.userObject.toString().isBlank()) "" else poiItem.userObject.toString())
        jsonObject.addProperty("like", "0")
        jsonObject.addProperty("bad", "1")
        NetworkProgressDialog.start(context)
        CoroutineScope(Dispatchers.Default).launch {
            NetworkConnect.connectHTTPS("insertFishingSpot.do",
                jsonObject,
                context = context  // 실패했을때 Toast 메시지를 띄워주기 위한 Context
                , onSuccess = { ->

                    val insertSpotNo : Int = YoungsFunction.stringIntToJson(NetworkConnect.resultString)

                    onSuccess()

                    poiItem.itemName = spotName
                    poiItem.tag = insertSpotNo
                    mPoiItem = poiItem

                    NetworkProgressDialog.end()
                }, onFailure = {
                    NetworkProgressDialog.end()
                }
            )
        }
        return mPoiItem
    }

    fun updateFishingSpot(context: Context, poiItem: MapPOIItem, onSuccess : () -> Unit, spotName : String?) {
        val jsonObject: JsonObject = JsonObject()
        jsonObject.addProperty("spot_no", poiItem.tag)
        jsonObject.addProperty("spot_name", if(spotName.isNullOrBlank()) poiItem.itemName else spotName)
        jsonObject.addProperty("latitude", poiItem.mapPoint.mapPointGeoCoord.latitude)
        jsonObject.addProperty("longitude", poiItem.mapPoint.mapPointGeoCoord.longitude)
        jsonObject.addProperty("address", poiItem.userObject.toString())
        jsonObject.addProperty("like", "0")
        jsonObject.addProperty("bad", "1")
        NetworkProgressDialog.start(context)
        CoroutineScope(Dispatchers.Default).launch {
            NetworkConnect.connectHTTPS("updateFishingSpot.do",
                jsonObject,
                context = context  // 실패했을때 Toast 메시지를 띄워주기 위한 Context
                , onSuccess = { ->
                    val insertSpotNo : Int = YoungsFunction.stringIntToJson(NetworkConnect.resultString)

                    onSuccess()

                    poiItem.itemName = spotName
                    poiItem.tag = insertSpotNo

//                    val mapView : TextView = findViewById<MapView>(R.id.mapView)

                    NetworkProgressDialog.end()
                }, onFailure = {
                    NetworkProgressDialog.end()
                }
            )
        }
    }

    fun deleteFishingSpot(context: Context, poiItem: MapPOIItem, onSuccess : () -> Unit) {
        if (poiItem.tag <= 0)
            return

        val jsonObject: JsonObject = JsonObject()
        jsonObject.addProperty("spot_no", poiItem.tag)
        NetworkProgressDialog.start(context)
        CoroutineScope(Dispatchers.Default).launch {
            NetworkConnect.connectHTTPS("deleteFishingSpot.do",
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