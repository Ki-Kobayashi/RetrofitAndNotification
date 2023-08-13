# RetrofitAndNotification


# Retrofit
※細かい点は、実コードを参照   

　　【事前準備】
        ・Manifest （通信系の権限を3つ追加）
        ・build.gradle（【 proj / app 】.secrets-gradle-plugin）
        ・local.properties （API-Key, BASE_URL記載）

　　【実装クラス】
        ・Module （Retrofit / Moshi のインスタンスを生成）
        ・Model　（API通信結果を詰める箱）
                ※Modelクラスを楽に生成するために・・・・
                        ・ASの　**Pluginから、「JsonToKotlinClass」**　を入れる
                        ・ｍodelを生成したいPackageの上の 右クリック → 「JsonToKotlinClass」選択
                        ・通信結果となる、Jsonを貼り付ける。
                                ※細かい設定もできる
                                **※ただし、たまに変なモデル構造で生成してくるので、チェックは必須**
        ・Service（RetrofitのInterfece）
        ・Repository（Serviceを呼び、withContext（Dispatchers.IO）でtry処理の実行）
        ・ViewModel（Viewにセットするモデルは、クラス変数として、**StateFlow型**で定義する）

        **※API通信結果のErrorハンドリングは、後に追記予定**


# API33ターゲットでのNotificationの使い方（途中）
　※NotificationDemoFragmentを参照
　※README.md　も参照するので、開きっぱなしにしておく


# 権限リクエストの流れ

**１. すでに**権限許可**しているか（PackageManager.PERMISSION_GRANTED） or 権限許可** 未 **チェックか**

　　（ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.XXX_XXX)）

**２. 以前拒否していたために、許可させるための説明が必要か**

    **※｛今後（永久に）表示しない」を選択 or 以前許可した場合は、「false」が返る**
　　（shouldShowRequestPermissionRationale(Manifest.permission.XXX_XXX)

**３. if (「１」がPackageManager.PERMISSION_GRANTED)　なら、すでに許可済**

　　　→ ★ 許可が必要だった処理の実行（通知・カメラ起動など）

**４. else （「３」が許可されてない）なら、「２」の可否で分岐**

      ・説明が必要：なぜ許可する必要があるか**説明するダイアログ表示**
      ・説明が **不要 ：権限リクエスト**

**５. 権限リクエストをした結果で処理の分岐**

   5-1. 許可された： ★ 許可が必要だった処理の実行（通知・カメラ起動など）

   5-2. 拒否： 以前拒否したか（ゆえに説明が必要か）チェック（shouldShowRequestPermissionRationale(Manifest.permission.POST_NOTIFICATIONS)）
  
  ｛今後（永久に）表示しない」を選択 or 以前許可した場合は、「false」が返る
        
  5-2-1 ：　以前拒否：ダイアログ or SnackBarで、「通知を受け取るには許可が必要です」など表示 
     
  5-2-2. **｛今後（永久に）表示しない」を選択** ：
  
           1.「"設定"で許可してください」のような説明を表示
           2. デバイス設定から、アプリ設定に飛ばす

### Fragment の場合の例
```kotlin

@RequiresApi(Build.VERSION_CODES.TIRAMISU)
private fun checkNotificationPermission() {
  val xxxxPermission = ContextCompat
      .checkSelfPermission(requireContext(), Manifest.permission.XXX_XXX)
  // TODO★；shouldShowRequestPermissionRationale()：
  //                  ・ 以前ユーザーがリクエストを許可しなかった場合：true
  //                  ・ 「今後表示しない」を選択していた場合：false
  val notificationRationale =
      shouldShowRequestPermissionRationale(Manifest.permission.POST_NOTIFICATIONS)

  if (notificationPermission == PackageManager.PERMISSION_GRANTED) {
      // 既に通知許可済み（通知を送る処理を記載）
      displayNotification()
  } else {
      // TODO★★★:　リクエストする前に、UIに根拠を表示するかどうかを取得
      if (notificationRationale) {
          // TODO★★★: 過去に拒否している：また拒否される可能性がある → なぜ許可が必要か説明を挟み、リクエスト
          showConfirmDialog("通知の許可", "通知を受け取るには許可が必要です") {
              requestPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
          }
      } else {
          // TODO★★★: 過去拒否されてない：説明なくリクエストしてもいい（★★★：けど、基本ここの分岐なく、説明＋リクエストするのが正しい気がする）
          // 通知の許可をリクエストする
          requestPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
      }
  }
}

//TODO: Fragment・Activityでも、リクエスト後のユーザー回答処理は、以下のようにする（）
private val requestPermissionLauncher =
  registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted: Boolean ->
      if (isGranted) {
          // 通知許可リクエストで許可を選択
          displayNotification()
      } else {
          // 通知許可リクエストで許可しないを選択
          if (shouldShowRequestPermissionRationale(Manifest.permission.POST_NOTIFICATIONS)) {
              Snackbar
                  .make(binding.root, "通知を受け取るには許可が必要です", Snackbar.LENGTH_LONG)
                  .show()
          } else {
              // 権限を永続的に許可しない（今後表示しない）状態の場合は、
              // 説明ダイアログを表示してアプリ情報の通知設定画面から権限変更を案内する
              // TODO★：「今後表示しない」を選択したユーザーは、デバイスの「設定」で権限を許可するしかなく、再び権限リクエストのダイアログを表示することはできません
              //              このケースの場合、
              //              「今後表示しない」を選択していたときに何かアクションをしたい（「"設定"で許可してください」のような説明を表示するなど）場合は、
              //              checkSelfPermission()と、shouldShowRequestPermissionRationale()　の両方を確認する必要がある
              //              、
              showConfirmDialog("通知の許可", "デバイスの「設定」から通知の権限を許可してください。") {
                  val settingsIntent = Intent(
                      android.provider.Settings.ACTION_APPLICATION_DETAILS_SETTINGS,
                      Uri.parse("package:com.example.retrofitdemo")
                  )
                  settingsIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                  startActivity(settingsIntent)
              }
          }
      }
  }

```




**１. module/RetrofitModule.kt, MoshiModule.kt を作成
** 2.
** 3.
** 4.
** 5.
** 6.
** 7.

