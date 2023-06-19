package com.example.farmmate1

import android.content.Context
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter

class WriteDiayrSpinnerAdapter(private val context: Context, private val items: List<String>) : ArrayAdapter<String>(context, android.R.layout.simple_spinner_item, items) {
    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val view = super.getView(position, convertView, parent)
        // 스피너 뷰의 모양을 커스터마이징할 수 있는 코드 작성
        return view
    }

    override fun getDropDownView(position: Int, convertView: View?, parent: ViewGroup): View {
        val view = super.getDropDownView(position, convertView, parent)
        // 스피너의 드롭다운 뷰의 모양을 커스터마이징할 수 있는 코드 작성
        return view
    }
}