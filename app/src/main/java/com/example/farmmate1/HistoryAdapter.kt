package com.example.farmmate1

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

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
        val dateTextView = view.findViewById<TextView>(R.id.list_item_tv_date)
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

        val dateTimeString = history?.created_at ?: ""
        val utcFormatter = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.getDefault())
        val kstFormatter = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())

// UTC 문자열을 Date 객체로 파싱
        val date: Date = utcFormatter.parse(dateTimeString) ?: Date()

// Date 객체를 KST로 변환
        val calendar = Calendar.getInstance(TimeZone.getTimeZone("UTC"))
        calendar.time = date
        calendar.add(Calendar.HOUR_OF_DAY, 9) // UTC 시간에서 KST로 변환

// KST 시간대로 변환하여 문자열로 출력
        val kstDateString = kstFormatter.format(calendar.time)

        dateTextView.text = kstDateString



        history.disease?.let { diseaseInfo ->
            result.text = diseaseInfo.diseaseName
            Log.d("HistoryAdapter", "Disease name: ${diseaseInfo.diseaseName}")
        }

        return view
    }


}