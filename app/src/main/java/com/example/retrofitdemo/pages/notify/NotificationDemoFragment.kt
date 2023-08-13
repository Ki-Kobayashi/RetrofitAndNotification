package com.example.retrofitdemo.pages.notify

import android.Manifest
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.view.View
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AlertDialog
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.navigation.NavDeepLinkBuilder
import com.example.retrofitdemo.R
import com.example.retrofitdemo.databinding.FragmentNotificationDemoBinding
import com.example.retrofitdemo.pages.top.TopFragment
import com.google.android.material.snackbar.Snackbar
import timber.log.Timber

// TODO: 将来的に起動させる予定のIntentは、PendingIntentを使用する

/**
 * ## Notificationの大まかな流れ
 *
 *  【API33以上Targetの場合】
 * 　    1. ManifestFile；
 *              「android.permission.POST_NOTIFICATIONS"」追加
 *       2. 通知を ON にする "設定画面" などで、通知許可されているか、"権限チェック"
 *              ★やり方：「README.md」  or  「コード内 > checkNotificationPermission()」を参照　
 *
 *
 *   【定数クラス】
 *       3.  以下 を定義：
 *          ・　channeName
 *          ・　channelID 「パッケージ名.notification.channel」、
 *              （channelID：命名規則として、Package名を含めるのが通例）
 *
 *  【Fragment】
 *      4.  norificationManagetの箱を定義（初期値:null）
 *
 *      5. onCreate：NotificationChannelを生成する
 *              1. norification生成＋「手順4」に代入
 *              2. 重要度を指定し、NotificationChannel 作成
 *                       val importance = NotificationManager.IMPORTANCE_HIG
 *                       val channel = NotificationChannel(id, name, importance).apply {
 *                          this.description = channelDescription
 *                       }
 *              3. 「２」で生成したchannelを、notificationManager の createNotificationChannel にセット
 *
 *     6. （※必要なら）通知発行タイミングで、次回起動時に設定でOFFなら、アプリ設定（自作の）をONにするようダイアログ表示
 *     7. 通知表示処理を生成
 *          ※通知でできることは、以下の４種類
 *                  ★ テキストのみの表示（表示だけでAction無）
 *                  ★ テキスト表示＋通知自体タップで該当Fragment（Activity）に遷移（アプリが起動し表示される）
 *                  ★ アクションボタン設置 （ボタンによってActionの設定が可能）
 *                  ★ 通知から返信（例；LINEメッセージへの返信など）
 *                  
 *                  ※後は、実コード参照
 */

/**
 * Created by K.Kobayashi on 2023/08/02.
 */
class NotificationDemoFragment : Fragment(R.layout.fragment_notification_demo) {
    private var _binding: FragmentNotificationDemoBinding? = null
    private val binding: FragmentNotificationDemoBinding get() = _binding!!

    // 通知準備
    private val channelID = "com.example.notification.channel"
    private var notificationManager: NotificationManager? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        notificationManager =
            requireContext().getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        createNotificationChannel(channelID, "ChannelNameDemo", "ChannelDescriptionDemo")
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initViews(view)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onDestroy() {
        notificationManager = null
        super.onDestroy()
    }

    private fun initViews(view: View) {
        _binding = FragmentNotificationDemoBinding.bind(view)
        binding.buttonNotification.setOnClickListener {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                checkNotificationPermission()
            } else {
                displayNotification()
            }
        }
    }

    private fun displayNotification() {
        // TODO:■【1】■■ ただのテキスト通知
//        val notification = NotificationCompat.Builder(requireContext(), channelID)
//            .setContentTitle(NOTIFICATION_TITLE)
//            .setContentText(NOTIFICATION_TEXT)
//            .setSmallIcon(android.R.drawable.ic_dialog_info)
//            .setAutoCancel(true)
//            .setPriority(NotificationCompat.PRIORITY_HIGH)
//            .build()
//        notificationManager?.notify(NOTIFICATION_ID, notification)


        // TODO:■【２】■■　通知にタップアクションを追加する


        // TODO:■【２】：通知にタップアクションを追加
        //      ■■ ケース（Activity → 別Activity） Notifiction押下時、あるActivityを開くようにする場合は以下・■■■■■■■■■■■■
//        val tapResultIntent = Intent(this, MainActivity::class.java)
//        val pendingIntent = PendingIntent.getActivity(
//            this,
//            200, // ←リクエストコード
//            tapResultIntent,
//            PendingIntent.FLAG_UPDATE_CURRENT // TODO: PendingIntentを新たに作成するとき、メモリに既に存在する場合は上書きする設定
//        )

        // TODO:■【２】：通知にタップアクションを追加
        //      ■■ ケース（Fragment → 別Fragment）■■■■■■■■■■■■
        //               TODO;  Notifiction押下時、ある"Fragment"(Navigation利用)を開くようにする場合は以下
        //                    Navigation コンポーネントにおいては、ディープリンクが用意されている
        //                     明示的ディープリンクとしての PendingIntent を作成し、Notificationにセットする
        val pendingIntent = NavDeepLinkBuilder(requireContext())
            .setGraph(R.navigation.nav_main)
            .setDestination(R.id.topFragment)
            .createPendingIntent()

        val notification = NotificationCompat.Builder(requireContext(), channelID)
            .setContentTitle(NOTIFICATION_TITLE)
            .setContentText(NOTIFICATION_TEXT)
            .setSmallIcon(android.R.drawable.ic_dialog_info)
            .setAutoCancel(true)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setContentIntent(pendingIntent)
            .build()
        notificationManager?.notify(NOTIFICATION_ID, notification)


        // TODO:■【３】：通知にアクションボタンを追加

        // TODO:■【４】：通知から返信できるようにする
    }

    private fun createNotificationChannel(
        id: String,
        name: String,
        channelDescription: String,
    ) {
        // このAPPは、最低26（O）から、サポートしているため、本来は以下の分岐は不要
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val importance = NotificationManager.IMPORTANCE_HIGH
            val channel = NotificationChannel(id, name, importance).apply {
                this.description = channelDescription
            }
            notificationManager?.createNotificationChannel(channel)
        }
    }

    // TODO★★★:  以下アノテーション：指定されたAPIレベル以上でのみ呼び出されるべきであることを示す
    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    private fun checkNotificationPermission() {
        val notificationPermission = ContextCompat
            .checkSelfPermission(requireContext(), Manifest.permission.POST_NOTIFICATIONS)
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

    /**
     * 確認ダイアログの表示
     */
    private fun showConfirmDialog(
        title: String,
        message: String,
        okAction: () -> Unit,
    ) {
        AlertDialog.Builder(requireContext())
            .setTitle(title)
            .setMessage(message)
            .setPositiveButton("OK") { _, _ ->
                okAction.invoke()
            }
            .setNegativeButton("Cancel", null)
            .show()
    }

    companion object {
        private const val NOTIFICATION_ID = 45
        private const val NOTIFICATION_TITLE = "通知タイトルだよ～"
        private const val NOTIFICATION_TEXT = "これは通知のデモメッセージです。"
    }
}
