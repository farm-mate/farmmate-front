package com.example.farmmate1.network

import com.example.farmmate1.Plant
import com.example.farmmate1.data.TodoItem
import retrofit2.Call
import retrofit2.http.*


interface ToDoListInterface {
    @GET("/api/todos")
    @Headers("Content-Type: application/json")
    fun getTodos(@Body todo : TodoItem): Call<TodoItem>

    @POST("/api/todos")
    @Headers("Content-Type: application/json")
    fun postTodos(@Body todo : TodoItem):Call<TodoItem>

    @DELETE("/api/todos")
    @Headers("Content-Type: application/json")
    fun deleteTodos(@Body todo : TodoItem):Call<TodoItem>

}