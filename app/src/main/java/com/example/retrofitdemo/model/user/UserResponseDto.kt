package com.example.retrofitdemo.model.user

import com.squareup.moshi.Json

data class UserResponseDto(
    @Json(name = "id")
    val userId: Int,
    val nickname: String,
    @Json(name = "first_name")
    val firstName: String?,
    @Json(name = "last_name")
    val lastName: String,
    val age: Int,
    val description: String?,
    @Json(name = "delete_flg")
    val deleteFlg: Boolean,
    @Json(name = "created_at")
    val createdAt: String,
    @Json(name = "updated_at")
    val updatedAt: String
)
