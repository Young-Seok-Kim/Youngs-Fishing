package com.youngs.youngsfishing.markbottom

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.youngs.common.recyclerview.RecyclerViewAdapter
import com.youngs.youngsfishing.databinding.AppearFishListBinding

class MarkBottomAdapter: RecyclerViewAdapter<MarkBottomModel, MarkBottomAdapter.MyViewHolder>() {

//    val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")

    inner class MyViewHolder(private val binding: AppearFishListBinding): RecyclerView.ViewHolder(binding.root) {
        var fishSpecies = binding.fishSpeciesButton
    }


    //만들어진 뷰홀더 없을때 뷰홀더(레이아웃) 생성하는 함수
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val binding= AppearFishListBinding.inflate(LayoutInflater.from(parent.context),parent,false)

        return MyViewHolder(binding)
    }

    //recyclerview가 viewholder를 가져와 데이터 연결할때 호출
    //적절한 데이터를 가져와서 그 데이터를 사용하여 뷰홀더의 레이아웃 채움
    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {

        holder.fishSpecies.text = instance._arrayList[position].fish_name.toString()

    }

    private object SingletonHolder {
        val INSTANCE = MarkBottomAdapter()
    }

    companion object {
        val instance: MarkBottomAdapter
            get() = SingletonHolder.INSTANCE
    }
}