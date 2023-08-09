package com.example.retrofitdemo.domain.service

import com.example.retrofitdemo.model.user.UserRequestDto
import com.example.retrofitdemo.model.user.UserResponseDto
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.Query

/**
 * Created by K.Kobayashi on 2023/07/18.
 */
interface ApiDogApiService {
    @POST("api/user")
    suspend fun fetchUserById(
        @Body req: UserRequestDto
    ): Response<UserResponseDto>

    @GET("api/user")
    suspend fun getUserById(
        @Query("id") id: Int
    ): Response<UserResponseDto>
}
