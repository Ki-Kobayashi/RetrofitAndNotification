package com.example.retrofitdemo.domain.repository

import com.example.retrofitdemo.domain.service.AlbumApiService
import com.example.retrofitdemo.model.AlbumsItem
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import timber.log.Timber
import java.io.IOException
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Created by K.Kobayashi on 2023/07/20.
 */
@Singleton
class AlbumDataRepository @Inject constructor(
    private val service: AlbumApiService,
) {
    /**
     *  全アルバム情報を取得
     */
    suspend fun getAlbumsData(): List<AlbumsItem>? {
        return withContext(Dispatchers.IO) {
            var albums: List<AlbumsItem>? = null
            try {
                val result = service.fetchAlbums()
                if (result.isSuccessful) {
                    albums =  result.body()
                } else {
                    Timber.d("@@@@@@@ err: ${result.errorBody()?.string()}")
                }
            } catch (e: IOException) {
                AlbumUIState.Error(e)
                e.printStackTrace()
            }
            albums
        }
    }

    suspend fun findAlbumListByUserId(userId: Int): List<AlbumsItem>?{
        return withContext(Dispatchers.IO) {
            var albumList: List<AlbumsItem>? = null
            try {
                val result = service.fetchAlubumList(userId)
                if (result.isSuccessful) {
                    albumList = result.body()
                } else {
                    Timber.d("@@@@@@@ err: ${result.errorBody()?.string()}")
                }
            } catch (e: IOException) {
                AlbumUIState.Error(e)
                e.printStackTrace()
            }
            albumList
        }
    }

    suspend fun getAlbumById(albumId :Int) : AlbumsItem? {
        return  withContext(Dispatchers.IO) {
            var album: AlbumsItem? = null
            try {
                val result = service.fetchAlbumById(albumId)
                Timber.d("${result.body()}")
                if (result.isSuccessful) {
                    album = result.body()
                } else {
                    Timber.d("@@@@@@@ err: ${result.errorBody()?.string()}")
                }
            } catch (e: IOException) {
                AlbumUIState.Error(e)
                e.printStackTrace()
            }
            album
        }
    }
}


sealed class AlbumUIState {
    data class Success(val albums: List<AlbumsItem>?) : AlbumUIState()

    // Throwable: すべてのエラー・例外を持つ基底クラス
    data class Error(val exception: Exception) : AlbumUIState()
}
