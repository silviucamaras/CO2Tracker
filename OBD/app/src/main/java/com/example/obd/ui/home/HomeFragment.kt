package com.example.obd.ui.home

import android.Manifest
import android.content.*
import android.content.pm.PackageManager
import android.location.Location
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.core.app.ActivityCompat
import androidx.fragment.app.Fragment
import com.example.gps.GPSService
import com.example.gps.LocationManagerCheck
import com.example.obd.R
import com.example.obd.StartActivity
import com.example.obd.ui.settings.OBDDeviceFragment
import com.sohrab.obd.reader.constants.DefineObdReader
import com.sohrab.obd.reader.service.ObdReaderService
import com.sohrab.obd.reader.trip.TripRecord
import kotlinx.android.synthetic.main.fragment_home.*
import kotlinx.android.synthetic.main.fragment_home.progress_bar
import kotlinx.android.synthetic.main.fragment_home.tv_obd_info

class HomeFragment : Fragment() {

    private lateinit var homeViewModel: HomeViewModel

    private var isGPSAllowed: Boolean = false
    private lateinit var  sharedPref: SharedPreferences
    private  lateinit  var deviceAddress: String
    private var isObdConnected: Boolean = false
    private var latestCO2val: Float = 0f
    private var latestLocation: Location? = null
    private lateinit var locationManagerCheck: LocationManagerCheck

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        if (ActivityCompat.checkSelfPermission(this.requireContext(), Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED)
        {
            isGPSAllowed = true
        }
        else {
            ActivityCompat.requestPermissions(this.requireActivity(),
                arrayOf(
                    Manifest.permission.ACCESS_FINE_LOCATION
                ),
                StartActivity.REQUEST_LOC
            )
        }

        homeViewModel = HomeViewModel(this.requireContext())
        sharedPref = activity!!.getSharedPreferences(
            OBDDeviceFragment.DEVICE_ADDRESS,
            Context.MODE_PRIVATE
        )
        locationManagerCheck = LocationManagerCheck.getInstance(this.requireContext())

    }
    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val root = inflater.inflate(R.layout.fragment_home, container, false)

        return root
    }

    @RequiresApi(Build.VERSION_CODES.O)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        if(homeViewModel.isRecordingRunning())
        {
            start_button.visibility = View.GONE
            tv_obd_info.visibility = View.VISIBLE
        }

        start_button.setOnClickListener{

            deviceAddress = sharedPref.getString(OBDDeviceFragment.DEVICE_ADDRESS,"").toString()

            if(!homeViewModel.isRecordingRunning())
                if(isGPSAllowed)
                    if(!deviceAddress.isNullOrEmpty())
                        if (!locationManagerCheck.isLocationServiceEnabled())
                        {
                            activity?.let { locationManagerCheck.createLocationServiceError(it) }
                        }
                        else
                        {
                            val result = homeViewModel.startRecording(deviceAddress,mObdReaderReceiver,activity)
                            if(result)
                            {
                                start_button.visibility = View.GONE
                                progress_bar.visibility = View.VISIBLE
                            }
                        }
                    else
                        Toast.makeText(context, "Please select a default BT device!", Toast.LENGTH_SHORT).show()
                else
                    Toast.makeText(context, "GPS permissions were not granted", Toast.LENGTH_SHORT).show()
            else
            {
                start_button.visibility = View.GONE
                tv_obd_info.visibility = View.VISIBLE
            }

        }
    }

    override fun onResume() {
        super.onResume()
        if (!locationManagerCheck.isLocationServiceEnabled())
            activity?.let { locationManagerCheck.createLocationServiceError(it) }
    }


    private val mObdReaderReceiver = object : BroadcastReceiver() {

        override fun onReceive(context: Context, intent: Intent) {

            if(isAdded && isVisible && userVisibleHint) {

                progress_bar.visibility = View.GONE
                tv_obd_info.visibility = View.VISIBLE

                val action = intent.action

                if (action == DefineObdReader.ACTION_OBD_CONNECTION_STATUS) {

                    val connectionStatusMsg =
                        intent.getStringExtra(ObdReaderService.INTENT_OBD_EXTRA_DATA)
                    tv_obd_info.text = connectionStatusMsg
                    Toast.makeText(context, connectionStatusMsg, Toast.LENGTH_SHORT).show()

                    if (connectionStatusMsg == getString(R.string.obd_connected)) {

                        isObdConnected = true

                    } else if (connectionStatusMsg == getString(R.string.connect_lost)) {
                        isObdConnected = false
                    } else if (connectionStatusMsg == getString(R.string.obd2_adapter_not_responding)) {

                        isObdConnected = false
                        activity?.stopService(Intent(context, ObdReaderService::class.java))
                        Toast.makeText(context, "OBD service stopped", Toast.LENGTH_SHORT).show()
                    }

                } else if (action == DefineObdReader.ACTION_READ_OBD_REAL_TIME_DATA) {
                    val tripRecord = TripRecord.getTripRecode(context)
                    tv_obd_info.text = tripRecord.toString()
                    latestCO2val = tripRecord.cO2gramsPerSecond
                    if (isObdConnected && latestLocation != null) {
                        homeViewModel.addVal(latestLocation!!, latestCO2val)
                    }
                } else if (action == GPSService.GPS_LOCATION) {
                    latestLocation = intent.extras?.get("location") as Location
                }
            }

        }
    }
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 177) {//response from popup settings
            isGPSAllowed = true
        }
    }


}