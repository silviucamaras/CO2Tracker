package com.example.obd.ui.map

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.CameraUpdateFactory
import android.content.Context.LOCATION_SERVICE
import androidx.core.content.ContextCompat.getSystemService
import android.location.LocationManager
import android.util.Log

import kotlinx.android.synthetic.main.fragment_map.view.*

import android.graphics.Color
import android.location.Location
import android.os.Build
import android.widget.Toast
import androidx.annotation.RequiresApi
import com.example.domain.CO2Val
import com.example.obd.R
import com.google.android.gms.maps.model.*
import kotlinx.android.synthetic.main.fragment_map.*
import com.google.maps.android.SphericalUtil
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.PolylineOptions




class MapFragment : Fragment() {

    private lateinit var googleMap: GoogleMap

    private  var previousSelectedPolyline: Polyline? = null

    private  var previousSelectedColor: Int? = null

    private var pointsDict: MutableMap<LatLng,MutableList<CO2Val>> = mutableMapOf()

    private var totalGrams = 0f

    private var segmentsList : MutableList<Segment> = mutableListOf()

    private var getSegmentVal: (CO2Val,CO2Val) -> Float = {point, nextPoint ->

        point.gramsPerSec * getTimeInterval(point, nextPoint)

    }
    private var drawLine : ( Segment ) -> PolylineOptions = { segment ->

        val grPerKm = (1000f / segment.length) * segment.value
        val color: Int = if(grPerKm < 160)
            Color.GREEN
        else if(grPerKm >= 160 && grPerKm < 220)
            Color.YELLOW
        else if(grPerKm >=220 && grPerKm < 300)
            Color.RED
        else
            Color.rgb(41,0,0)

        totalGrams += segment.value

        PolylineOptions()
            .add(segment.start, segment.end)
            .clickable(true)
            .width(20f)
            .color(color)
            .startCap(RoundCap())
            .endCap(RoundCap())
    }

    private var getTimeInterval : ( CO2Val, CO2Val) -> Float = { point,nextPoint ->

        val interval =  (nextPoint.timestamp - point.timestamp).toFloat()  / 1000f
        interval

    }
    @RequiresApi(Build.VERSION_CODES.N)
    private val onMapReady : (GoogleMap?) -> Unit= { map ->


        map?.setOnPolylineClickListener {
            Toast.makeText(this.context,it.tag.toString(),Toast.LENGTH_SHORT).show()

            if(previousSelectedPolyline != null){
                if(previousSelectedColor != null) {
                    previousSelectedPolyline!!.color = previousSelectedColor!!
                }
            }
            previousSelectedPolyline = it
            previousSelectedColor = it.color
            it.color = Color.BLUE

        }

        map?.setOnCircleClickListener {
            Toast.makeText(this.context,it.tag.toString(),Toast.LENGTH_SHORT).show()
        }

        mapViewModel.fetchValues()
        mapViewModel.co2ValLiveData.observe(this, Observer { CO2List ->
            CO2List.forEachIndexed { index: Int, point: CO2Val ->


                if(index + 1 <= CO2List.lastIndex) {
                    val nextPoint = CO2List[index + 1]
                        if(getTimeInterval(point,nextPoint) <= 300)
                            if(point.latitude == nextPoint.latitude && point.longitude == nextPoint.longitude) {
                                if(pointsDict.containsKey(LatLng(point.latitude,point.longitude))) {
                                    val list = pointsDict.getValue(LatLng(point.latitude,point.longitude))
                                    list.add(point)
                                }
                                else{
                                    pointsDict[LatLng(point.latitude,point.longitude)] =
                                        mutableListOf(point)
                                }
                            }
                            else {
                                val startPoint = LatLng(point.latitude,point.longitude)
                                val endPoint =  LatLng(nextPoint.latitude, nextPoint.longitude)
                                segmentsList.add(Segment(start = startPoint, end = endPoint,value = getSegmentVal(point,nextPoint),length = distanceBetween(startPoint, endPoint).toFloat()))
                            }
                }
            }
            pointsDict.forEach{ (key, value) ->

                val stationaryGrams = getCircleVal(value)

                segmentsList.forEach{
                    if(it.end == key)
                    {
                        it.value += stationaryGrams
                    }
                }

            }

            segmentsList.forEach{
                map?.addPolyline(
                    drawLine(it)
                )?.tag = "Estimated " + (1000f / it.length) * it.value + " grams of CO2 / KM"
            }

            map?.moveCamera(CameraUpdateFactory.newLatLng(LatLng(CO2List[CO2List.lastIndex].latitude,CO2List[CO2List.lastIndex].longitude)))
            total_Grams.text = totalGrams.toString()
        })

    }

    private var getCircleVal : (MutableList<CO2Val>) -> Float = { list ->
        var sum = 0f
        list.forEachIndexed{index: Int, cO2Val: CO2Val ->

            if(index + 1 <= list.lastIndex){
                sum += getSegmentVal(cO2Val, list[index + 1])
            }
        }
        sum
    }

    private lateinit var mapViewModel: MapViewModel



    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        mapViewModel =
            ViewModelProviders.of(this).get(MapViewModel::class.java)


        val root = inflater.inflate(R.layout.fragment_map, container, false)

        return root
    }

    private fun distanceBetween(point1 : LatLng, point2 : LatLng) :Double{

        if (point1 == point2)
            return  0.0
        return SphericalUtil.computeDistanceBetween(point1, point2)
}

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        var supportMapFragment: SupportMapFragment? = (childFragmentManager.findFragmentById(R.id.inner_map_fragment) as SupportMapFragment?)
        supportMapFragment?.getMapAsync(onMapReady)

        }

}
data class Segment (val start : LatLng, val end : LatLng, var value : Float, var length : Float)


