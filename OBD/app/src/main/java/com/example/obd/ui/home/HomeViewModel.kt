package com.example.obd.ui.home

import android.Manifest
import android.bluetooth.BluetoothAdapter
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.pm.PackageManager
import android.location.Location
import android.os.Build
import android.widget.ArrayAdapter
import androidx.annotation.RequiresApi
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat.startForegroundService
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.data.firebase.CO2Repository
import com.example.domain.CO2Val
import com.example.gps.GPSService
import com.example.obd.R
import com.example.obd.StartActivity
import com.sohrab.obd.reader.application.ObdPreferences
import com.sohrab.obd.reader.constants.DefineObdReader
import com.sohrab.obd.reader.obdCommand.ObdCommand
import com.sohrab.obd.reader.obdCommand.ObdConfiguration
import com.sohrab.obd.reader.obdCommand.SpeedCommand
import com.sohrab.obd.reader.obdCommand.engine.LoadCommand
import com.sohrab.obd.reader.obdCommand.engine.MassAirFlowCommand
import com.sohrab.obd.reader.obdCommand.engine.ThrottlePositionCommand
import com.sohrab.obd.reader.obdCommand.fuel.WidebandAirFuelRatioCommand
import com.sohrab.obd.reader.service.ObdReaderService
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import ro.upt.ac.chiuitter.data.firebase.FirebaseCO2Store
import java.lang.Exception
import java.util.ArrayList

class HomeViewModel(var context: Context) : ViewModel( ) {

    private val _text = MutableLiveData<String>().apply {
        value = "This is home Fragment"
    }

    private val dbStore: CO2Repository = FirebaseCO2Store()
    val isRecordingRunning ={
        isRecording
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun startRecording(obdDeviceAddress: String, broadcastReceiver: BroadcastReceiver, activityCompat: FragmentActivity?): Boolean{

        try
        {
            val obdCommands = ArrayList<ObdCommand>()
            obdCommands.add(SpeedCommand())
            obdCommands.add(LoadCommand())
            obdCommands.add(ThrottlePositionCommand())
            obdCommands.add(WidebandAirFuelRatioCommand())
            obdCommands.add(MassAirFlowCommand())

            ObdConfiguration.setmObdCommands(context, obdCommands)

            val gasPrice = 7f // per litre, you should initialize according to your requirement.
            ObdPreferences.get(context).gasPrice = gasPrice

            val intentFilter = IntentFilter()
            intentFilter.addAction(DefineObdReader.ACTION_READ_OBD_REAL_TIME_DATA)
            intentFilter.addAction(DefineObdReader.ACTION_OBD_CONNECTION_STATUS)
            intentFilter.addAction(GPSService.GPS_LOCATION)
            activityCompat?.registerReceiver(broadcastReceiver, intentFilter)

            //start service which will execute in background for connecting and execute command until you stop
            val intent = Intent(context, ObdReaderService::class.java)
            intent.putExtra(StartActivity.EXTRA_DEVICE_ADDRESS,obdDeviceAddress)
            activityCompat?.startService(intent)

            activityCompat?.startForegroundService(Intent(context, GPSService::class.java))
            isRecording = true
        }
        catch (e: Exception)
        {
            isRecording = false
        }

        return isRecording
    }

    companion object{
        var isRecording: Boolean = false
    }

    fun addVal(location: Location, cO2Val: Float)
    {
        GlobalScope.launch {
            dbStore.addVal(
                CO2Val(
                    System.currentTimeMillis(),
                    location.latitude,
                    location.longitude,
                    cO2Val
                )
            )
        }
    }
}