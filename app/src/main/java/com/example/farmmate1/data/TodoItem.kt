package com.example.farmmate1.data

import android.content.ContentValues
import java.util.*

class TodoItem(
    val id : Int,
    val spinnerValues: ContentValues,
    val waterCheckedValue : Boolean,
    val fertleCheckedValue : Boolean,
    val fertleValue : String,
    val fertleUsageValue : Int,
    val pesCheckedValue : Boolean,
    val pesValue : String,
    val pesUsageValue : Int,
    val memo : String,
    val date : String
    )
