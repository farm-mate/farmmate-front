package com.example.farmmate1

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*

class HistoryAdapter (val context: Context, val HistoryList: ArrayList<History>) : BaseAdapter()
{
    override fun getCount(): Int {
        return HistoryList.size
    }

    override fun getItem(position: Int): Any {
        return HistoryList[position]
    }

    override fun getItemId(position: Int): Long {
        return 0
    }

    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        val view: View = LayoutInflater.from(context).inflate(R.layout.list_item_history,null)
        val sequence = view.findViewById<ImageView>(R.id.list_item_ib_number)
        val date = view.findViewById<TextView>(R.id.list_item_tv_date)
        val result = view.findViewById<TextView>(R.id.list_item_tv_result)

        val history = HistoryList[position]

        sequence.setImageResource(history.sequence)
        date.text = history.date
        result.text = history.result

        return view
    }

}