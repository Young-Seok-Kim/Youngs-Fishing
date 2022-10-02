package com.youngs.youngsfishing.newspot

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.CheckBox
import androidx.recyclerview.widget.RecyclerView
import com.youngs.common.recyclerview.RecyclerViewAdapter
import com.youngs.youngsfishing.databinding.RecyclerviewNewSpotAppearFishListBinding

class NewSpotAdapter: RecyclerViewAdapter<NewSpotModel, NewSpotAdapter.NewSpotViewHolder>() {

    inner class NewSpotViewHolder(private val binding: RecyclerviewNewSpotAppearFishListBinding): RecyclerView.ViewHolder(binding.root) {
        var fishCheckBox : CheckBox = binding.fishCheckBox
    }


    //만들어진 뷰홀더 없을때 뷰홀더(레이아웃) 생성하는 함수
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NewSpotViewHolder {
        val binding= RecyclerviewNewSpotAppearFishListBinding.inflate(LayoutInflater.from(parent.context),parent,false)

        return NewSpotViewHolder(binding)
    }

    //recyclerview가 viewholder를 가져와 데이터 연결할때 호출
    //적절한 데이터를 가져와서 그 데이터를 사용하여 뷰홀더의 레이아웃 채움
    override fun onBindViewHolder(holder: NewSpotViewHolder, position: Int) {

        holder.fishCheckBox.text = instance._arrayList[position].fish_name.toString()
        holder.fishCheckBox.setOnClickListener(){
            instance._arrayList[position].isChecked = true
        }

    }

    private object SingletonHolder {
        val INSTANCE = NewSpotAdapter()
    }

    companion object {
        val instance: NewSpotAdapter
            get() = SingletonHolder.INSTANCE
    }
}