package com.dias.smartalarm

import android.app.AlarmManager
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.media.RingtoneManager
import android.os.Build
import android.util.Log
import android.widget.Toast
import androidx.core.app.NotificationCompat
import com.dias.smartalarm.data.Alarm
import java.util.*

class AlarmService : BroadcastReceiver() {

    /*
      Masalah:
          1. intent yang diambil pada fungsi onReceive() (sepertinya) berbeda dengan intent yang dikirim pada fungsi setOneTimeAlarm()
          2. gambar tidak muncul pada kotak notifikasi
     */

    override fun onReceive(context: Context, intent: Intent) {
//        Log.i("onReceive", "Message in intent is ${intent.getStringExtra(EXTRA_MESSAGE)}")
        val message = intent.getStringExtra(EXTRA_MESSAGE) as String
//        Log.i("onReceive", "Message is $message")
        val type = intent.getIntExtra(EXTRA_TYPE, 0)
        val title = when (type) {
            TYPE_ONE_TIME -> "One Time Alarm"
            TYPE_REPEATING -> "Repeating Alarm"
            else -> "Dah lah android ribet"
        }
        showAlarmNotification(
            context,
            title,
            message,
            if (type == TYPE_ONE_TIME) ID_ONE_TIME else ID_REPEATING_TIME
        )
    }

    fun setOneTimeAlarm(context: Context, type: Int, date: String, time: String, message: String) {
//        Log.i("setOneTimeAlarm", "Message is $message")
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager

        val intent = Intent(context, AlarmService::class.java)
        intent.putExtra(EXTRA_MESSAGE, message)
        intent.putExtra(EXTRA_TYPE, type)
//        Log.i("setOneTimeAlarm", "Message in intent is ${intent.getStringExtra(EXTRA_MESSAGE)}\nThe intent $intent")
        val calendar = Calendar.getInstance()

        // bagian date
        calendar.set(Calendar.DATE, date.split("-")[0].toInt())
        calendar.set(Calendar.MONTH, date.split("-")[1].toInt() - 1)
        calendar.set(Calendar.YEAR, date.split("-")[2].toInt())

        // bagian time
        calendar.set(Calendar.HOUR_OF_DAY, time.split(":")[0].toInt())
        calendar.set(Calendar.MINUTE, time.split(":")[1].toInt())
        calendar.set(Calendar.SECOND, 0)

        val pendingIntent = PendingIntent.getBroadcast(context, ID_ONE_TIME, intent, 0)

        alarmManager.set(AlarmManager.RTC_WAKEUP, calendar.timeInMillis, pendingIntent)

        Toast.makeText(context,
            "Success add alarm on $date $time${if (message != "") " with message: \"$message\"" else ""}",
            Toast.LENGTH_SHORT).show()
    }

    fun cancelAlarm(context: Context, type: Int) {
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val intent = Intent(context, AlarmService::class.java)
        val requestCode = when (type) {
            TYPE_ONE_TIME -> ID_ONE_TIME
            TYPE_REPEATING -> ID_REPEATING_TIME
            else -> Log.d("cancelAlarm","Unknown type of alarm")
        }
        val pendingIntent = PendingIntent.getBroadcast(context, requestCode, intent, 0)
        pendingIntent.cancel()
        alarmManager.cancel(pendingIntent)
        Toast.makeText(
            context,
            "${if (type == TYPE_ONE_TIME) "One Time" else "Repeating"} Alarm Cancelled",
            Toast.LENGTH_SHORT
        ).show()
    }

    private fun showAlarmNotification(
        context: Context,
        title: String,
        message: String,
        notificationId: Int,
    ) {
//        Log.i("showAlarmNotification", "Message is $message")
        val ringtone = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM)
        val channelName = "Alarm"
        val channelId = "smart_alarm"
        val notificationManager =
            context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        val builder = NotificationCompat.Builder(context, channelId)
            .setSmallIcon(R.drawable.ic_one_time)
            .setContentTitle(title)
            .setContentText(message)
            .setSound(ringtone)
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel =
                NotificationChannel(channelId, channelName, NotificationManager.IMPORTANCE_DEFAULT)
            channel.enableVibration(true)
            channel.vibrationPattern = longArrayOf(1000, 1000, 1000, 1000, 1000)
            builder.setChannelId(channelId)
            notificationManager.createNotificationChannel(channel)
        }
        val notif = builder.build()
        notificationManager.notify(notificationId, notif)

    }

    fun setRepeatingAlarm(context: Context, type: Int, time: String, message: String) {
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager

        val intent = Intent(context, AlarmService::class.java)
        intent.putExtra(EXTRA_MESSAGE, message)
        intent.putExtra(EXTRA_TYPE, type)

        val calendar = Calendar.getInstance()

        // bagian time
        calendar.set(Calendar.HOUR_OF_DAY, time.split(":")[0].toInt())
        calendar.set(Calendar.MINUTE, time.split(":")[1].toInt())
        calendar.set(Calendar.SECOND, 0)

        val pendingIntent = PendingIntent.getBroadcast(context, ID_REPEATING_TIME, intent, 0)

        alarmManager.set(AlarmManager.RTC_WAKEUP, calendar.timeInMillis, pendingIntent)
        Toast.makeText(context,
            "Success set RepeatingAlarm on $time${if (message != "") " with message: \"$message\"" else ""}",
            Toast.LENGTH_SHORT).show()
    }

    companion object {
        private const val EXTRA_MESSAGE = "message"
        private const val EXTRA_TYPE = "type"

        private const val ID_ONE_TIME = 101
        private const val ID_REPEATING_TIME = 102

        const val TYPE_ONE_TIME = 1
        const val TYPE_REPEATING = 0
    }


}