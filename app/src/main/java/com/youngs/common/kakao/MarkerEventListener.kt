package com.youngs.common.kakao

import android.app.Activity
import android.content.Context
import android.widget.EditText
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.FragmentManager
import com.youngs.common.Define
import com.youngs.common.YoungsContextFunction.deleteFishingSpot
import com.youngs.common.YoungsContextFunction.insertFishingSpot
import com.youngs.common.YoungsContextFunction.updateFishingSpot
import com.youngs.youngsfishing.markbottom.MarkBottom
import com.youngs.youngsfishing.newspot.NewSpot
import net.daum.mf.map.api.MapPOIItem
import net.daum.mf.map.api.MapPoint
import net.daum.mf.map.api.MapReverseGeoCoder
import net.daum.mf.map.api.MapView

class MarkerEventListener(
    val context: Context,
    private val contextActivity: Activity,
    private val fragmentManagerParam: FragmentManager
) : MapView.POIItemEventListener {
    override fun onPOIItemSelected(mapView: MapView, poiItem: MapPOIItem) {
        // 마커 클릭 시
        findAddress(poiItem)

        if (poiItem.tag <= 0){ // tag가 0 이하면 신규 마커이다.
            dialogEdit(mapView,poiItem)
        }
        else{
            markBottomPopup(poiItem)
        }

    }

    override fun onCalloutBalloonOfPOIItemTouched(mapView: MapView?, poiItem: MapPOIItem?) {
        // 말풍선 클릭 시 (Deprecated)
        // 이 함수도 작동하지만 그냥 아래 있는 함수에 작성하자
    }

    override fun onCalloutBalloonOfPOIItemTouched(
        mapView: MapView,
        poiItem: MapPOIItem,
        buttonType: MapPOIItem.CalloutBalloonButtonType?
    ) {
        // 말풍선 클릭 시
        findAddress(poiItem)
    }

    override fun onDraggablePOIItemMoved(p0: MapView?, p1: MapPOIItem?, p2: MapPoint?) {
        // 드래그해서 마커 이동했을때 이벤트
        p1?.mapPoint = p2 // 드래그하고 저장을 했을때 마커를 이동하기 전 위치가 나와서 위치를 바꿔줌
    }



    private fun dialogEdit(mapView: MapView, poiItem: MapPOIItem){
        findAddress(poiItem)

        val builder = AlertDialog.Builder(context)
        val txtEditText : EditText = EditText(context)
        val itemList = arrayOf("포획가능 어종 선택", "마커 삭제", "취소"/*,"포획가능 어종 선택"*/)

        txtEditText.hint = "스팟명 변경"

        builder.setCancelable(false)
        builder.setTitle(poiItem.itemName)
//        builder.setView(txtEditText)
        builder.setItems(itemList) { dialog, which ->
            when (which) {
                0 -> NewSpot(poiItem,contextActivity).let {
                    it.showNow(fragmentManagerParam, "")
                    it.isCancelable = true
                }

                1 -> {
                    deleteFishingSpot(context, poiItem, onSuccess = { ->
                        if (poiItem.markerType.name == "YellowPin") {
                            poiItem.markerType = MapPOIItem.MarkerType.BluePin
                        }
                    })

                    mapView.removePOIItem(poiItem)    // 마커 삭제
                }
                2 -> dialog.dismiss()   // 대화상자 닫기
                3 -> { // 저장버튼 클릭, 현재는 안씀
                    if (poiItem.tag <= 0) {
                        val insertPOI = insertFishingSpot(context, poiItem, onSuccess = { ->

                        }, txtEditText.text.toString())

                        insertPOI.let {
                            if (it?.markerType?.name == "YellowPin") {
                                it.markerType = MapPOIItem.MarkerType.BluePin
                            }
                            if (it != null) {
                                markBottomPopup(it)
                            }
                        }

                    } else {
                        updateFishingSpot(context, poiItem, onSuccess = { ->
                            if (poiItem.markerType.name == "YellowPin") {
                                poiItem.markerType = MapPOIItem.MarkerType.BluePin
                            }
                        }, txtEditText.text.toString())
                    }
                }
            }
        }
        builder.show()

    }

    private fun markBottomPopup(poiItem: MapPOIItem){
        MarkBottom(poiItem).showNow(fragmentManagerParam, "")
    }

    private fun findAddress(poiItem: MapPOIItem) {
        val reverseGeoCoder: MapReverseGeoCoder = MapReverseGeoCoder(
            Define.KAKAO_NATIVE_KEY,
            poiItem.mapPoint,
            FindGeoToAddressListener(poiItem), // 위도, 경도로 주소찾기, poiItem의 userObject에 주소가 저장된다.
            contextActivity
        )
        reverseGeoCoder.startFindingAddress()
    }
}