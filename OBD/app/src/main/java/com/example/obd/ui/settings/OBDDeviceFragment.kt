package com.example.obd.ui.settings

import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.example.obd.R
import android.content.Context.MODE_PRIVATE
import android.content.Intent
import android.content.SharedPreferences
import kotlinx.android.synthetic.main.fragment_obd_device.paired


class OBDDeviceFragment : Fragment() {

    private lateinit var OBDDeviceViewModel: OBDDeviceViewModel
    private lateinit var sharedPref: SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        sharedPref = activity!!.getSharedPreferences(DEVICE_ADDRESS, MODE_PRIVATE)
    }
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        OBDDeviceViewModel = OBDDeviceViewModel(this.requireContext())

        return inflater.inflate(R.layout.fragment_obd_device, container, false)
    }
    override fun onResume() {
        super.onResume()
        OBDDeviceViewModel.mPairedDevicesArrayAdapter.clear()
        checkBTState()

        val pairedListView = paired
        pairedListView.adapter = OBDDeviceViewModel.mPairedDevicesArrayAdapter
        pairedListView.onItemClickListener = mDeviceClickListener

        var pairedDevices = mutableSetOf<BluetoothDevice>()
        if (OBDDeviceViewModel.mBtAdapter != null)
            pairedDevices = OBDDeviceViewModel.mBtAdapter!!.bondedDevices

        if (pairedDevices.size > 0) {
            for (device in pairedDevices) {
                OBDDeviceViewModel.mPairedDevicesArrayAdapter.add(device.name + "\n" + device.address)
            }
        } else {
            val noDevices = resources.getText(R.string.no_devices).toString()
            OBDDeviceViewModel.mPairedDevicesArrayAdapter.add(noDevices)
        }

//        if(!deviceAddress.isNullOrEmpty()){
//            val element = pairedDevices.find { it.address == deviceAdress }
//            val index = pairedDevices.indexOf(element)
//            val row = OBDDeviceViewModel.mPairedDevicesArrayAdapter.getView(index,)
//            row.colo
//        }
        //TODO("Make item a different color")
    }

    private val mDeviceClickListener =
        AdapterView.OnItemClickListener { _, view, _, _ ->
            Toast.makeText(this.requireContext(), "Saving...", Toast.LENGTH_SHORT).show()

            val info = (view as TextView).text.toString()
            val address = info.substring(info.length - 17)
            sharedPref.edit()?.putString(DEVICE_ADDRESS, address)?.apply()
            Toast.makeText(this.requireContext(), "Saved!", Toast.LENGTH_SHORT).show()
        }

    private fun checkBTState() {

        if (OBDDeviceViewModel.mBtAdapter == null) {
            Toast.makeText(context, "Device does not support Bluetooth", Toast.LENGTH_SHORT)
                .show()
        } else {
            if (OBDDeviceViewModel.mBtAdapter!!.isEnabled)
            {
            }
            else
            {
                val enableBtIntent = Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE)
                startActivityForResult(enableBtIntent, 1)
            }
        }
    }

    companion object{
        const val  DEVICE_ADDRESS = "device_address"
        const val REQUEST_LOC = 177
    }
}