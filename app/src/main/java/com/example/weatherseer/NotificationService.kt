package com.example.weatherseer
import android.Manifest
import android.annotation.SuppressLint
import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.IBinder
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat

class NotificationService : Service() {

    private val notificationId = 1
    private val channelId = "weather_channel"

    @SuppressLint("MissingPermission")
    private fun showWeatherNotification(
    city: String?,
    country: String?,
    desc: String?,
    icon: String?,
    temp: String?
    ): Notification {
        val intent = Intent(this, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }
        val pendingIntent: PendingIntent =
            PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_IMMUTABLE)

        val notification = NotificationCompat.Builder(this, channelId)
            .setSmallIcon(when (icon) {
                "01d", "01n" -> (R.drawable.sunny)
                "02d", "02n" -> (R.drawable.fewclouds)
                "03d", "03n" -> (R.drawable.scatclouds)
                "04d", "04n" -> (R.drawable.brokenclouds)
                "09d", "09n" -> (R.drawable.showerrain)
                "10d", "10n" -> (R.drawable.rain)
                "11d", "11n" -> (R.drawable.storm)
                "13d", "13n" -> (R.drawable.snow)
                "50d", "50n" -> (R.drawable.mist)
                else -> {(R.drawable.sunny)}
            })
            .setContentTitle(city + this.getString(R.string.comma) + country)
            .setContentText(temp + this.getString(R.string.degree) + this.getString(R.string.slash) + (desc?.capitalizeFirstLetter()))
            .setContentIntent(pendingIntent)
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setAutoCancel(true)
            .build()

        with(NotificationManagerCompat.from(this)) {
            notify(notificationId, notification)
        }
    return notification
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val name = "Weather Notifications"
            val descriptionText = "Current Weather"
            val importance = NotificationManager.IMPORTANCE_LOW
            val channel = NotificationChannel(channelId, name, importance).apply {
                description = descriptionText
            }
            val notificationManager: NotificationManager =
                getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
    }

    override fun onBind(p0: Intent?): IBinder? {
        return null
    }

    override fun onCreate() {
        super.onCreate()
        createNotificationChannel()
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION)
            == PackageManager.PERMISSION_GRANTED && ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS)
            == PackageManager.PERMISSION_GRANTED) {
            val city = intent?.getStringExtra("cityName")
            val country = intent?.getStringExtra("countryName")
            val desc = intent?.getStringExtra("desc")
            val icon = intent?.getStringExtra("icon")
            val temp = intent?.getStringExtra("temp")

            startForeground(notificationId, showWeatherNotification(city, country, desc, icon, temp))
        }
        return START_STICKY
    }

}


