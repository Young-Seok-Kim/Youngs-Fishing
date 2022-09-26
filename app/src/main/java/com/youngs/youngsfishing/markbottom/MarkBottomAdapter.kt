package com.youngs.youngsfishing.markbottom

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.youngs.common.recyclerview.RecyclerViewAdapter
import com.youngs.youngsfishing.databinding.RecyclerviewAppearFishListBinding

class MarkBottomAdapter: RecyclerViewAdapter<MarkBottomModel, MarkBottomAdapter.MarkBottomViewHolder>() {

//    val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")

    inner class MarkBottomViewHolder(private val binding: RecyclerviewAppearFishListBinding): RecyclerView.ViewHolder(binding.root) {
        var fishSpecies = binding.fishSpeciesTextView
    }


    //만들어진 뷰홀더 없을때 뷰홀더(레이아웃) 생성하는 함수
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MarkBottomViewHolder {
        val binding= RecyclerviewAppearFishListBinding.inflate(LayoutInflater.from(parent.context),parent,false)

        return MarkBottomViewHolder(binding)
    }

    //recyclerview가 viewholder를 가져와 데이터 연결할때 호출
    //적절한 데이터를 가져와서 그 데이터를 사용하여 뷰홀더의 레이아웃 채움
    override fun onBindViewHolder(holder: MarkBottomViewHolder, position: Int) {

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