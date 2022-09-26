package com.youngs.youngsfishing.markbottom

import android.Manifest
import android.app.Dialog
import android.content.pm.PackageManager
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.gson.Gson
import com.google.gson.JsonObject
import com.youngs.common.Define
import com.youngs.common.YoungsFunction
import com.youngs.common.network.NetworkConnect
import com.youngs.common.network.NetworkProgressDialog
import com.youngs.common.recyclerview.RecyclerViewAdapter
import com.youngs.youngsfishing.databinding.MarkBottomFragmentBinding
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import net.daum.mf.map.api.MapPOIItem
import net.daum.mf.map.api.MapPoint
import org.json.JSONArray
import org.json.JSONObject

class MarkBottom : BottomSheetDialogFragment() {

    private lateinit var binding: MarkBottomFragmentBinding
    private lateinit var dlg : BottomSheetDialog

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = MarkBottomFragmentBinding.inflate(layoutInflater,null,false)

        binding.spotNameTextView.text = arguments?.getString("spotName")
        binding.spotAddressTextView.text = arguments?.getString("spotAddress")


        initList()
        updateList()

    }

    private fun updateList() {
        val jsonObject : JsonObject = JsonObject()
        jsonObject.addProperty("spot_no", arguments?.getString("spotNo"))
        NetworkProgressDialog.start(requireContext())
        CoroutineScope(Dispatchers.Default).launch {
            NetworkConnect.connectHTTPS("selectAppearFish.do",
                jsonObject,
                requireContext() // 실패했을때 Toast 메시지를 띄워주기 위한 Context
                , onSuccess = { ->
                    MarkBottomAdapter.instance.clear()
                    val jsonArray : JSONArray = YoungsFunction.stringArrayToJson(NetworkConnect.resultString)

                    if (jsonArray.toString() != "[\"\"]") {
                        val list = Gson().fromJson(
                            jsonArray.toString(),
                            Array<MarkBottomModel>::class.java
                        )

                        for (item in list) {
                            MarkBottomAdapter.instance.addItem(item)
                        }
                    }

                    NetworkProgressDialog.end()
                }
                , onFailure = {
                    NetworkProgressDialog.end()
                }
            )
        }
    }

    private fun initList() {

            val recyclerView = binding.listview

            val mLayoutManager: LinearLayoutManager

            MarkBottomAdapter.instance.setOnItemTapListener(object : RecyclerViewAdapter.OnItemTapListener{
                override fun onDoubleTap(position: Int) {
                }

                override fun onLongTap(position: Int){

                }

                override fun onSingleTap(position: Int) {

                }
            })

            recyclerView.addOnScrollListener(object : RecyclerView.OnScrollListener() {


                override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
//                    if (dy > 0) //check for scroll down
//                    {
//                        visibleItemCount = mLayoutManager.childCount
//                        totalItemCount = mLayoutManager.itemCount
//                        pastVisiblesItems = mLayoutManager.findFirstVisibleItemPosition()
//
//                        if (visibleItemCount + pastVisiblesItems > totalItemCount) {
//
//                        }
//
//                    }
                }

                override fun onScrollStateChanged(view: RecyclerView, scrollState: Int) {
                    //OnScrollListener.SCROLL_STATE_IDLE은 스크롤이 이동하다가 멈추었을때 발생되는 스크롤 상태입니다.
                    //즉 스크롤이 바닥에 닿아 멈춘 상태에 처리를 하겠다는
                }
            })
            MarkBottomAdapter.instance.listView = recyclerView

    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        dlg = (super.onCreateDialog(savedInstanceState).apply {
            window?.setDimAmount(0.2f)
            setOnShowListener{
                val bottomSheet = findViewById<View>(com.google.android.material.R.id.design_bottom_sheet) as FrameLayout
                bottomSheet.setBackgroundResource(android.R.color.transparent)
            }
        }) as BottomSheetDialog

        return dlg
    }
    override fun onStart() {
        super.onStart()

        // 시작하자마자 Expanded 한다.
        dlg.behavior.state = BottomSheetBehavior.STATE_EXPANDED
    }

    override fun onResume() {
        super.onResume()

    }


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {

        initButton()

        return binding.root
    }


    private fun initButton() {

    }
}