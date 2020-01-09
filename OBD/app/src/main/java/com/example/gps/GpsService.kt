package com.example.gps

import android.Manifest
import android.annotation.TargetApi
import android.app.Activity
import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager.GPS_PROVIDER
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.IBinder
import android.os.Looper
import android.util.Log
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import com.example.obd.R

class GPSService : Service(){
    private var myLocation: Location? = null
    private lateinit var locationManagerCheck: LocationManagerCheck
    private var isLocationChanged = false
    private var ss: Thread? = null
    private val GPSListener = object : LocationListener {
        override fun onLocationChanged(location: Location) {

            if (ActivityCompat.checkSelfPermission(baseContext, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED)
            {
                if (locationManagerCheck.locationManager.isProviderEnabled(GPS_PROVIDER)) {
                    myLocation =
                        locationManagerCheck.locationManager.getLastKnownLocation(GPS_PROVIDER)
                    if (myLocation != null) {
                        Log.d(
                            "Location Changed Listen",
                            myLocation!!.provider + " " + myLocation
                        )
                        isLocationChanged = true
                    }
                }
            }
        }

        override fun onProviderDisabled(provider: String) {}

        override fun onProviderEnabled(provider: String) {}

        override fun onStatusChanged(provider: String, status: Int, extras: Bundle) {}
    }


    override fun onBind(intent: Intent): IBinder? {
        return null
    }

    override fun onCreate() {
        startForeground(1, buildForegroundNotification())

        locationManagerCheck = LocationManagerCheck.getInstance(this)
        if (ActivityCompat.checkSelfPermission(baseContext, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED)
        {
            locationManagerCheck.locationManager.requestLocationUpdates(
                GPS_PROVIDER,
                GPS_TIME_INTERVAL, GPS_DISTANCE, GPSListener
            )
        }

    }

    private fun buildForegroundNotification(): Notification {
        val b = getNotificationBuilder(
            this@GPSService,
            "com.example.your_app.notification.CHANNEL_ID_FOREGROUND", // Channel id
            NotificationManagerCompat.IMPORTANCE_LOW
        )
        b.setOngoing(true)
            .setContentTitle("OBD")
            .setContentText("recording...")
        return b.build()
    }

    private fun sendMessageToActivity(location: Location) {
        val intent = Intent("LocationUpdate")
        intent.putExtra("location", location)
        intent.action = GPS_LOCATION
        Log.d("Broadcast Message", "$location!!!!!!!!!!!!")
        sendBroadcast(intent)
    }


    private fun obtainLocation(): Location? {
        if (ActivityCompat.checkSelfPermission(baseContext, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED)
        {
            if (isLocationChanged)
            {
                isLocationChanged = false
                return myLocation
            } else if (locationManagerCheck.locationManager.isProviderEnabled(GPS_PROVIDER))
            {
                myLocation = locationManagerCheck.locationManager.getLastKnownLocation(GPS_PROVIDER)
                if (myLocation != null && myLocation!!.accuracy < 10) {
                    Log.d("Location", " " + myLocation + " " + myLocation!!.speed * 3.6)

                    return myLocation
                }
            }
        }
        return null
    }

    @TargetApi(17)
    override fun onStartCommand(intent: Intent, flags: Int, startId: Int): Int {

        Toast.makeText(applicationContext, "Service started", Toast.LENGTH_SHORT).show()
        ss = Thread(object : Runnable
        {
            override fun run()
            {
                if (Thread.currentThread().isInterrupted)
                    return
                while (!Thread.currentThread().isInterrupted)
                {
                    if (ActivityCompat.checkSelfPermission(baseContext, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED)
                    {
                        val loc = obtainLocation()
                        if (loc != null) {
                            sendMessageToActivity(loc)
                        }
                    } else
                    {
                        Log.d("Error", "Service does not have permissions!!")
                        val handler2 = Handler(Looper.getMainLooper())
                        handler2.post {
                            Toast.makeText(
                                this@GPSService.applicationContext,
                                "Service needs location permissions",
                                Toast.LENGTH_SHORT
                            ).show()
                        }

                    }
                    try {
                        Thread.sleep(5000)
                    } catch (e: InterruptedException) {
                        e.printStackTrace()
                        return
                    }

                }

            }
        })
        ss!!.start()
        return START_NOT_STICKY
    }

    override fun onDestroy() {
        super.onDestroy()
        locationManagerCheck.locationManager.removeUpdates(GPSListener)
        ss!!.interrupt()
        stopForeground(true)
        stopSelf()

    }



    companion object
    {

        private const val GPS_TIME_INTERVAL: Long = 250
        private const val GPS_DISTANCE = 1f // set the distance value in meter
        const val GPS_LOCATION = "gps_location"
        @TargetApi(26)
        private fun prepareChannel(context: Context, id: String, importance: Int) {
            val appName = context.getString(R.string.app_name)
            val nm = context.getSystemService(Activity.NOTIFICATION_SERVICE) as NotificationManager


            var nChannel: NotificationChannel? = nm.getNotificationChannel(id)

            if (nChannel == null)
            {
                    nChannel = NotificationChannel(id, appName, importance)
                    nm.createNotificationChannel(nChannel)
            }

        }

        fun getNotificationBuilder(context: Context, channelId: String, importance: Int): NotificationCompat.Builder {
            return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                prepareChannel(context, channelId, importance)
                NotificationCompat.Builder(context, channelId)
            } else {
                NotificationCompat.Builder(context)
            }
        }
    }
}
