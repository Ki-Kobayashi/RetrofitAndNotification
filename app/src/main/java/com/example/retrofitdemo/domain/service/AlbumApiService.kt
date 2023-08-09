package com.example.retrofitdemo.domain.service

import com.example.retrofitdemo.model.AlbumsItem
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query


// TODO: ★エラー
//          BEGIN_OBJECT but was BEGIN_ARRAY at line 1 column 2 path
//          （ArrayからはじまるjsonをObjectから受け取ろうとしていたエラー）
//          リストだけのDataクラスを作成しない
//              data class NgClass(
//                  val aaaList: List<aaItem>
//              )
//              上記の場合、上記クラスは不要。Retrofit の interface の XxxService の戻り値では、List＜AaaItem＞とする.


/**
 * Created by K.Kobayashi on 2023/07/18.
 */
interface AlbumApiService {
    // TODO: Response<T>: Retrofitで用意されたネット取得結果を受け取るときの型
    @GET("/albums")
    suspend fun fetchAlbums(): Response<List<AlbumsItem>>

    @GET("/albums")
    suspend fun fetchAlubumList(
        @Query("userId") userId: Int
    ): Response<List<AlbumsItem>>

    @GET("/albums/{id}")
    suspend fun fetchAlbumById(
        @Path(value = "id") albumId: Int
    ): Response<AlbumsItem>
}
