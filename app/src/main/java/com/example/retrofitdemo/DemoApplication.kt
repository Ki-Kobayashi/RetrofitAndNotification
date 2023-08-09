package com.example.retrofitdemo

import android.app.Application
import dagger.hilt.android.HiltAndroidApp
import timber.log.Timber


// TODO: secrets-gradle-pluginとは
//      API キーをバージョン管理システムにアップしてはいけない（セキュリティ的に）
//          local.properties ファイルに保存し、API キーの読み取りには Android 用 Secrets Gradle プラグイン（secrets-gradle-plugin）を使用
//          。
// TODO: TODO: secrets-gradle-pluginの追加 → sync Now
// TODO: local.propertiesにBASE＿KEY等を追加
// TODO: Make Project
//              ーー※注意※ーー
//                  Gradle Pluginを8.0.0にアップデートするとBuildConfigの自動生成はデフォルトでfalse担っている
//                      → app/build.gradle # buildFeature　内で、「buildConfig = true」を追記し、SyncNow
//                      ★上記後、改めてMakeProjectする
//              ーーーーーーーーーーー.
//          Buildした際に、app/Build/...内にBuildConfig.javaが自動生成される
//          コード内で使用する時は、「BuildConfig.WHEATER_API_KEY」のようにアクセスすれば、安全にAPIKEYなどを利用できるようになる
// TODO:
// TODO:Modelには、シリアライズする「@Parcelize」「: Parcelize」
//              app/build.gradleのpluginに追加後、使用可能
// TODO:
// TODO:



/**
 * Created by K.Kobayashi on 2023/07/06.
 */
@HiltAndroidApp
class DemoApplication : Application() {
    override fun onCreate() {
        super.onCreate()

        Timber.plant(Timber.DebugTree())
    }
}
