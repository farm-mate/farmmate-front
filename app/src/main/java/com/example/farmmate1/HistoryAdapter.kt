package com.example.farmmate1

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*

class HistoryAdapter (val context: Context, val historyList: ArrayList<History>) : BaseAdapter()
{
    override fun getCount(): Int {
        return historyList.size
    }

    override fun getItem(position: Int): Any {
        return historyList[position]
    }

    override fun getItemId(position: Int): Long {
        return 0
    }

    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        val view: View = LayoutInflater.from(context).inflate(R.layout.list_item_history, null)
        val sequence = view.findViewById<ImageView>(R.id.list_item_ib_number)
        val date = view.findViewById<TextView>(R.id.list_item_tv_date)
        val result = view.findViewById<TextView>(R.id.list_item_tv_result)

        val history = historyList[position]

        // 이미지 리소스 아이디 배열
        val imageResources = arrayOf(
            R.drawable.number_one,
            R.drawable.number_two,
            R.drawable.number_three,
            R.drawable.number_four,
            R.drawable.number_five,
            R.drawable.number_six,
            R.drawable.number_seven,
            R.drawable.number_eight,
            R.drawable.number_nine,
            R.drawable.number_ten,
            R.drawable.number_eleven,
            R.drawable.number_twelve
        )

        // position에 해당하는 이미지 리소스 아이디를 가져와서 sequence에 설정
        if (position < imageResources.size) {
            sequence.setImageResource(imageResources[position])
        }

        history.disease?.let { diseaseInfo ->
            result.text = diseaseInfo.diseaseName
            Log.d("HistoryAdapter", "Disease name: ${diseaseInfo.diseaseName}")
        }

        return view
    }


}