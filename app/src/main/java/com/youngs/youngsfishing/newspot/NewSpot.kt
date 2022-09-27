package com.youngs.youngsfishing.newspot

import android.app.Activity
import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.DialogFragment
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.gson.Gson
import com.google.gson.JsonObject
import com.youngs.common.Define
import com.youngs.common.YoungsFunction
import com.youngs.common.kakao.FindGeoToAddressListener
import com.youngs.common.network.NetworkConnect
import com.youngs.common.network.NetworkProgress
import com.youngs.common.recyclerview.RecyclerViewAdapter
import com.youngs.youngsfishing.databinding.FragmentNewSpotBinding
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import net.daum.mf.map.api.MapPOIItem
import net.daum.mf.map.api.MapReverseGeoCoder
import org.json.JSONArray
import java.lang.ClassCastException

class NewSpot(val poiItem: MapPOIItem,val activity : Activity) : DialogFragment() {

    private lateinit var binding: FragmentNewSpotBinding
    var sendEvenListener: SendEventListener? = null

    override fun onAttach(context: Context) {
        super.onAttach(context)

        try {
            sendEvenListener = context as SendEventListener
        } catch (e: ClassCastException) {
            throw ClassCastException(context.toString() + "must be implement SendEventListener")
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = FragmentNewSpotBinding.inflate(layoutInflater, null, false)


        initList()
        updateList()

        binding.saveButton.setOnClickListener(View.OnClickListener {
            if (binding.spotNameEditText.text.toString().isBlank()) {
                Toast.makeText(context,"스팟명을 입력해주세요",Toast.LENGTH_SHORT).show()
                return@OnClickListener
            }

            MapReverseGeoCoder(
                Define.KAKAO_NATIVE_KEY,
                poiItem.mapPoint,
                FindGeoToAddressListener(poiItem), // 위도, 경도로 주소찾기, poiItem의 userObject에 주소가 저장된다.
                activity
            )

            val selectFishList: ArrayList<String> = arrayListOf()

            runBlocking {
                val insertPOI = insertFishingSpot(this@NewSpot.requireContext(), poiItem, onSuccess = { -> }, binding.spotNameEditText.text.toString())
                Log.d("test","저장버튼 클릭")

                for (i in 0 until (NewSpotAdapter.instance.itemCount)) {
                    if (NewSpotAdapter.instance._arrayList[i].isChecked) {
                        selectFishList.add(NewSpotAdapter.instance._arrayList[i].fish_name)
                        insertPOI?.let { it ->
                            insertAppearFish(this@NewSpot.requireContext(), it, onSuccess = { -> }, NewSpotAdapter.instance._arrayList[i])
                        }
                    }
                }
            }
            dismiss()
        })
    }


    private fun updateList() {
        val jsonObject: JsonObject = JsonObject()
        startProgress()
        CoroutineScope(Dispatchers.Default).launch {
            NetworkConnect.connectHTTPS("selectFishList.do",
                jsonObject,
                requireContext() // 실패했을때 Toast 메시지를 띄워주기 위한 Context
                , onSuccess = { ->
                    NewSpotAdapter.instance.clear()
                    val jsonArray: JSONArray =
                        YoungsFunction.stringArrayToJson(NetworkConnect.resultString)

                    if (jsonArray.toString() != "[\"\"]") {
                        val list = Gson().fromJson(
                            jsonArray.toString(),
                            Array<NewSpotModel>::class.java
                        )

                        for (item in list) {
                            NewSpotAdapter.instance.addItem(item)
                        }
                    }
                    endProgress()
                }, onFailure = {
                    endProgress()
                    dismiss()
                }
            )
        }
    }

    private fun initList() {

        val recyclerView = binding.listview
        val mLayoutManager = GridLayoutManager(context, 2) // 그리드로 리사이클러뷰 표현
        recyclerView.layoutManager = mLayoutManager
        
        NewSpotAdapter.instance.setOnItemTapListener(object :
            RecyclerViewAdapter.OnItemTapListener {
            override fun onDoubleTap(position: Int) {
            }

            override fun onLongTap(position: Int) {

            }

            override fun onSingleTap(position: Int) {

            }
        })

        recyclerView.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
            }
            override fun onScrollStateChanged(view: RecyclerView, scrollState: Int) {
                //OnScrollListener.SCROLL_STATE_IDLE은 스크롤이 이동하다가 멈추었을때 발생되는 스크롤 상태입니다.
                //즉 스크롤이 바닥에 닿아 멈춘 상태에 처리를 하겠다는
            }
        })
        NewSpotAdapter.instance.listView = recyclerView
    }


    override fun onResume() {
        super.onResume()

    }


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return binding.root
    }

    private suspend fun insertFishingSpot(context: Context, poiItem: MapPOIItem, onSuccess : () -> Unit, spotName : String?) : MapPOIItem?{
        var mPoiItem : MapPOIItem?  = null
        startProgress()

        val jsonObject: JsonObject = JsonObject()
        jsonObject.addProperty("spot_name", if(spotName.isNullOrBlank()) poiItem.itemName else spotName)
        jsonObject.addProperty("latitude", poiItem.mapPoint.mapPointGeoCoord.latitude)
        jsonObject.addProperty("longitude", poiItem.mapPoint.mapPointGeoCoord.longitude)
        jsonObject.addProperty("address", if (poiItem.userObject?.toString().isNullOrBlank()) "주소없음" else poiItem.userObject.toString())
        jsonObject.addProperty("like", "0")
        jsonObject.addProperty("bad", "0")

                CoroutineScope(Dispatchers.IO).launch {
                    val test = NetworkConnect.connectHTTPSSync("insertFishingSpot.do",jsonObject,context)
                    val insertSpotNo: Int = YoungsFunction.stringIntToJson(NetworkConnect.resultString)
                    poiItem.itemName = spotName
                    poiItem.tag = insertSpotNo
                    mPoiItem = poiItem
                }.join()

        endProgress()
        return mPoiItem
    }

    private suspend fun insertAppearFish(context: Context, poiItem: MapPOIItem, onSuccess : () -> Unit, fish : NewSpotModel) : MapPOIItem?{
        MapReverseGeoCoder(
            Define.KAKAO_NATIVE_KEY,
            poiItem.mapPoint,
            FindGeoToAddressListener(poiItem), // 위도, 경도로 주소찾기, poiItem의 userObject에 주소가 저장된다.
            activity
        )
        startProgress()
        val mPoiItem : MapPOIItem? = null
        val jsonObject: JsonObject = JsonObject()
        jsonObject.addProperty("fish_no", fish.fish_no)
        jsonObject.addProperty("fish_name", fish.fish_name)
        jsonObject.addProperty("spot_no", poiItem.tag)

                CoroutineScope(Dispatchers.IO).launch {
                    NetworkConnect.connectHTTPSSync("insertAppearFish.do", jsonObject, context)
                }.join()

        endProgress()
        return mPoiItem
    }

    private fun startProgress()
    {
        val progress = NetworkProgress()
        dialog?.window?.let {
            progress.startProgress(binding.progressbar,it)
            binding.spotNameEditText.visibility = View.GONE
            binding.saveButton.visibility = View.GONE
            binding.listview.visibility = View.GONE
        }
    }
    private fun endProgress()
    {
        val progress = NetworkProgress()

        dialog?.window?.let {
            progress.endProgressBar(binding.progressbar,it)
            binding.spotNameEditText.visibility = View.VISIBLE
            binding.saveButton.visibility = View.VISIBLE
            binding.listview.visibility = View.VISIBLE
        }
    }
}