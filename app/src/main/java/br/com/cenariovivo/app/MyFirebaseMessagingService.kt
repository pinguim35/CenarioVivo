package br.com.cenariovivo.app

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.media.RingtoneManager
import android.os.Build
import android.util.Log
import androidx.core.app.NotificationCompat
//import androidx.core.content.getSystemService
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage

class MyFirebaseMessagingService : FirebaseMessagingService() {

    companion object {
        private const val TAG = "MyFirebaseMsgService"
    }

    /**
     * Chamado quando uma mensagem é recebida.
     *
     * @param remoteMessage Objeto representando a mensagem recebida do Firebase Cloud Messaging.
     */
    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        Log.d(TAG, "From: ${remoteMessage.from}")

        // Verifique se a mensagem contém uma carga de dados.
        remoteMessage.data.isNotEmpty().let {
            Log.d(TAG, "Message data payload: " + remoteMessage.data)
            // Aqui você pode processar os dados da notificação se precisar
            // Por exemplo, se você enviar dados personalizados junto com a notificação
        }

        // Verifique se a mensagem contém uma carga de notificação.
        // Esta parte é tratada automaticamente pelo sistema quando o app está em SEGUNDO PLANO.
        // Quando o app está em PRIMEIRO PLANO, você precisa tratar a exibição da notificação aqui.
        remoteMessage.notification?.let {
            Log.d(TAG, "Message Notification Body: ${it.body}")
            sendNotification(it.title, it.body)
        }
    }

    /**
     * Chamado quando um novo token de registro FCM é gerado.
     * Envie este token para o seu servidor para que você possa direcionar notificações para este dispositivo.
     */
    override fun onNewToken(token: String) {
        Log.d(TAG, "Refreshed token: $token")
        // Se você tiver um servidor que precisa do token, envie-o aqui.
        // Ex: sendRegistrationToServer(token)
    }

    /**
     * Cria e mostra uma notificação simples contendo a mensagem recebida.
     *
     * @param messageBody Corpo da mensagem FCM recebida.
     */
    private fun sendNotification(messageTitle: String?, messageBody: String?) {
        val intent = Intent(this, MainActivity::class.java) // O que abrir ao clicar na notificação
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
        val pendingIntentFlags = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            PendingIntent.FLAG_ONE_SHOT or PendingIntent.FLAG_IMMUTABLE
        } else {
            PendingIntent.FLAG_ONE_SHOT
        }
        val pendingIntent = PendingIntent.getActivity(this, 0 /* Request code */, intent,
            pendingIntentFlags)

        val channelId = getString(R.string.default_notification_channel_id) // Você precisará criar este string resource
        val defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)
        val notificationBuilder = NotificationCompat.Builder(this, channelId)
            .setSmallIcon(R.drawable.ic_stat_notification) // CRIE ESTE ÍCONE! (Veja abaixo)
            .setContentTitle(messageTitle ?: getString(R.string.app_name))
            .setContentText(messageBody)
            .setAutoCancel(true)
            .setSound(defaultSoundUri)
            .setContentIntent(pendingIntent)

        val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        // Desde o Android Oreo (API 26), o canal de notificação é necessário.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channelName = getString(R.string.default_notification_channel_name) // Crie este string resource
            val channel = NotificationChannel(channelId,
                channelName,
                NotificationManager.IMPORTANCE_DEFAULT)
            notificationManager.createNotificationChannel(channel)
        }

        notificationManager.notify(0 /* ID da notificação */, notificationBuilder.build())
    }
}
    