package com.example.retrofitdemo.module

import com.example.retrofitdemo.BuildConfig
import com.example.retrofitdemo.domain.service.AlbumApiService
import com.example.retrofitdemo.domain.service.ApiDogApiService
import com.squareup.moshi.Moshi
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import retrofit2.create
import java.util.concurrent.TimeUnit
import javax.inject.Qualifier
import javax.inject.Singleton

/**
 * Created by K.Kobayashi on 2023/07/13.
 *
 * 複数のBaseUrlが異なるAPIと通信する場合、
 *      １：（必要なら）通信の設定をするために、OkHttpClientを返すProviderを作成
 *          ※タイムアウトなどの設定
 *      ２：クラス下部のように、独自アノテーションを追加（BaseUrl ごとに生成する Retrofit インスタンスを区別するため）
 *      ３：独自アノテーションを使い、BaseUrlごとにRetrofitを返すproviderを作成
 *      ４：BaseUrlごとに、Service（Retrofit　Interface）を返す、Providerを作成
 *
 *      ※MoshiModuleも作成しておくこと
 *      ※上2つのModule生成が完了したら、通信結果を受け取るモデル作成に進む
 */
@Module
@InstallIn(SingletonComponent::class)
object RetrofitModule {

    /**
     * Retrofitでログを表示させるHttpInterceptorの生成
     */
    @Singleton
    @Provides
    fun provideHttpClient(): OkHttpClient {
        val interceptor = HttpLoggingInterceptor().apply {
            this.level = HttpLoggingInterceptor.Level.BODY
        }
        val client = OkHttpClient.Builder().apply {
            this.addInterceptor(interceptor)
               // 以下3つのタイムアウト：定義なし＝デフォルト10秒がセットされる（※ほとんどの場合、デフォルトで問題ない＝定義不要）
                // "接続タイムアウト"の設定：
                .connectTimeout(30, TimeUnit.SECONDS)
                //　読み取りタイムアウトの設定：個々の結果取得到着時間
                .readTimeout(20, TimeUnit.SECONDS)
                    // データ送信タイムアウト設定：個々の書き込み IO 操作時間
                .writeTimeout(25, TimeUnit.SECONDS)
        }

        return client.build()
    }

    @JsonApiRetrofit
    @Singleton
    @Provides
    fun provideJsonApiRetrofit(moshi: Moshi, client: OkHttpClient): Retrofit {
        return Retrofit.Builder()
            .baseUrl(BuildConfig.BASE_URL)
            .addConverterFactory(MoshiConverterFactory.create(moshi))
            .client(client)
            .build()
    }

    @ApiDogApiRetrofit
    @Singleton
    @Provides
    fun provideApiDogApiRetrofit(moshi: Moshi, client: OkHttpClient): Retrofit {
        return Retrofit.Builder()
            .baseUrl(BuildConfig.BASE_APIDOG_URL)
            .addConverterFactory(MoshiConverterFactory.create(moshi))
            .client(client)
            .build()
    }

    @Singleton
    @Provides
    fun provideAlbumApiService(
        @JsonApiRetrofit retrofit: Retrofit
    ): AlbumApiService =
        retrofit.create(AlbumApiService::class.java)

    @Singleton
    @Provides
    fun provideApiDogService(
        @ApiDogApiRetrofit retrofit: Retrofit
    ): ApiDogApiService {
        return retrofit.create(ApiDogApiService::class.java)
    }
}

@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class JsonApiRetrofit

@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class ApiDogApiRetrofit

