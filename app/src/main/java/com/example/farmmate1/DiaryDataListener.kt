package com.example.farmmate1

import java.util.Calendar

interface DiaryDataListener {
    fun onDiaryDataReceived(date: Calendar, data: String){


    }
}
