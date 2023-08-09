package com.example.retrofitdemo.model

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

data class AlbumsItem(
    val userId: Int,
    val id: Int,
    val title: String,
)
