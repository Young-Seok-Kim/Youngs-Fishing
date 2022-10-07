package com.youngs.youngsfishing.fishinformation

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.CheckBox
import androidx.recyclerview.widget.RecyclerView
import com.youngs.common.recyclerview.RecyclerViewAdapter
import com.youngs.youngsfishing.databinding.RecyclerviewFishInformationBinding
import com.youngs.youngsfishing.databinding.RecyclerviewNewSpotAppearFishListBinding

class FishInformationAdapter: RecyclerViewAdapter<FishInformationModel, FishInformationAdapter.FishInformationViewHolder>() {

    inner class FishInformationViewHolder(private val binding: RecyclerviewFishInformationBinding): RecyclerView.ViewHolder(binding.root) {
//        var fishCheckBox : CheckBox = binding.fishCheckBox
        val context = binding.imageView.context
    }


    //만들어진 뷰홀더 없을때 뷰홀더(레이아웃) 생성하는 함수
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FishInformationViewHolder {
        val binding= RecyclerviewFishInformationBinding.inflate(LayoutInflater.from(parent.context),parent,false)

        return FishInformationViewHolder(binding)
    }

    //recyclerview가 viewholder를 가져와 데이터 연결할때 호출
    //적절한 데이터를 가져와서 그 데이터를 사용하여 뷰홀더의 레이아웃 채움
    override fun onBindViewHolder(holder: FishInformationViewHolder, position: Int) {


    }

    private object SingletonHolder {
        val INSTANCE = FishInformationAdapter()
    }

    companion object {
        val instance: FishInformationAdapter
            get() = SingletonHolder.INSTANCE
    }
}