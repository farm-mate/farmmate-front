package com.example.farmmate1.network

import com.example.farmmate1.data.TodoItem
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query



interface ToDoListInterface {
    @GET("/api/todos")
    fun getTodos(@Query("date") date: String): Call<List<TodoItem>>
}