package com.example.obd.ui.settings

import android.bluetooth.BluetoothAdapter
import android.content.Context
import android.widget.ArrayAdapter
import androidx.lifecycle.ViewModel
import com.example.obd.R

class OBDDeviceViewModel(var context: Context) : ViewModel() {

    var mBtAdapter: BluetoothAdapter? = BluetoothAdapter.getDefaultAdapter()
    var mPairedDevicesArrayAdapter: ArrayAdapter<String> = ArrayAdapter(context,R.layout.device_name)

}