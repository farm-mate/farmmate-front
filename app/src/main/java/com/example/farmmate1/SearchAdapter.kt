package com.example.farmmate1

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class SearchAdapter(private var itemList: MutableList<Disease>, private val onItemClick: (Disease) -> Unit) :
    RecyclerView.Adapter<SearchAdapter.SearchViewHolder>() {

    inner class SearchViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        private val thumbImageView: ImageView = itemView.findViewById(R.id.thumbImageView)
        private val cropNameTextView: TextView = itemView.findViewById(R.id.cropNameTextView)
        private val sickNameKorTextView: TextView = itemView.findViewById(R.id.sickNameKorTextView)
        private val sickNameEngTextView: TextView = itemView.findViewById(R.id.sickNameEngTextView)

        fun bind(item: Disease) {
            cropNameTextView.text = item.cropName
            sickNameKorTextView.text = item.sickNameKor
            sickNameEngTextView.text = item.sickNameEng

            // 이미지 로딩
            val imageLoaderTask = ImageLoaderTask(thumbImageView)
            imageLoaderTask.execute(item.thumbImg)
        }
    }

    // updateData 함수 추가
    fun updateData(newList: List<Disease>) {
        Log.d("SearchAdapter", "기존 데이터: $itemList")
        itemList.clear()
        itemList.addAll(newList)
        Log.d("SearchAdapter", "업데이트된 데이터: $itemList")
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SearchViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.list_item_search, parent, false)
        return SearchViewHolder(view)
    }

    override fun onBindViewHolder(holder: SearchViewHolder, position: Int) {
        holder.bind(itemList[position])
        holder.itemView.setOnClickListener {
            onItemClick(itemList[position])
        }
    }

    override fun getItemCount(): Int {
        return itemList.size
    }
}
