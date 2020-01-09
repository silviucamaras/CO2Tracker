package com.example.gps

import android.app.Activity
import android.app.AlertDialog
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.location.LocationManager
import android.provider.Settings
import android.util.Log

class LocationManagerCheck private constructor(context: Context) {


    var locationManager: LocationManager = context.getSystemService(Context.LOCATION_SERVICE) as LocationManager
    private var builder: AlertDialog.Builder? = null
    private val intent = Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS)
    val isLocationServiceEnabled = {
        locationServiceBoolean = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)
        locationServiceBoolean
    }



    init {
        val gpsIsEnabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)

        if (gpsIsEnabled)
        {
            locationServiceBoolean = true
        }
    }


    fun createLocationServiceError(activityObj: Activity) {

        builder = AlertDialog.Builder(activityObj)
        Log.d("AlertCreator", "this is activity obj$activityObj")
        builder!!.setMessage("You need to activate location service to use this feature. Please turn on network or GPS mode in location settings")
        builder!!.setTitle("Location is required")
        builder!!.setCancelable(false)
        builder!!.setPositiveButton("Settings") { dialog, _ ->
            activityObj.startActivityForResult(intent, 1)
            dialog.dismiss()
        }
        builder!!.setNegativeButton("Cancel"
        ) { dialog, _ -> dialog.dismiss() }
        alert = builder!!.create()
        alert!!.show()
    }

    companion object {
        private var alert: AlertDialog? = null
        private var locationServiceBoolean: Boolean = false
        private var  instance: LocationManagerCheck? = null
        fun  getInstance(context: Context): LocationManagerCheck
        {
            if(instance == null)
            {
                instance = LocationManagerCheck(context)
            }
           return instance as LocationManagerCheck
        }
    }

}
