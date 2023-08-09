package com.example.retrofitdemo.domain.repository

import com.example.retrofitdemo.domain.service.ApiDogApiService
import com.example.retrofitdemo.model.user.UserRequestDto
import com.example.retrofitdemo.model.user.UserResponseDto
import com.squareup.moshi.Moshi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import timber.log.Timber
import java.io.IOException
import javax.inject.Inject

/**
 * Created by K.Kobayashi on 2023/07/30.
 */
class ApiDogRepository @Inject constructor(
    private val service: ApiDogApiService,
    private val moshi: Moshi
) {

    /**
     * ユーザー情報の取得
     */
    suspend fun fetchUser(req: UserRequestDto): UserResponseDto? {
        var userInfo: UserResponseDto? = null
        withContext(Dispatchers.IO) {
            try {
                val result = service.fetchUserById(req)
//                val result = service.getUserById(3)
                if (result.isSuccessful) {
                    userInfo = result.body()
                } else {
                    Timber.d("@@@@@@@ err: ${result.errorBody()?.string()}")
                }
            } catch (e: IOException) {
                e.printStackTrace()
            }
        }
        return userInfo
    }
}

data class ErrorResponse(
    var message: String,
    var error: Error
)



