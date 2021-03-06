package com.example.hae_wo

import android.annotation.SuppressLint
import android.graphics.Color
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.android.gms.location.*
import com.google.android.gms.location.LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY
import com.google.android.gms.location.LocationRequest.PRIORITY_HIGH_ACCURACY
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.CircleOptions
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.PolylineOptions
import com.google.android.gms.tasks.OnSuccessListener
import com.google.android.material.floatingactionbutton.FloatingActionButton
import org.json.JSONArray
import org.json.JSONObject
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*

class ShowMapsActivity : AppCompatActivity(), OnMapReadyCallback {

    private val hardCode = arrayOf(
        LatLng(51.42779, 6.88152),       
        LatLng(51.42844, 6.88298),
        LatLng(51.42894, 6.88485),
        LatLng(51.42953, 6.88671),
        LatLng(51.43044, 6.88661),
        LatLng(51.43176, 6.88676),
        LatLng(51.43183, 6.8886),
        LatLng(51.43321, 6.88873) 
    )

    private lateinit var mMap: GoogleMap
    private lateinit var locationManager : LocationManager
    private lateinit var fusedLocationProviderClient: FusedLocationProviderClient
    private lateinit var locationRequest: LocationRequest
    private lateinit var locationCallback: LocationCallback
    private lateinit var addLocBTN: FloatingActionButton
    private lateinit var changeBTN: FloatingActionButton
    private lateinit var setHighBTN: FloatingActionButton
    private lateinit var setBalancedBTN: FloatingActionButton
    private lateinit var setGPSBTN: FloatingActionButton
    private lateinit var saveBTN: FloatingActionButton
    private var clicked = false
    private var accuracy = "HIGH"
    private var locationsHigh: ArrayList<Location> = arrayListOf()
    private var locationsBalanced: ArrayList<Location> = arrayListOf()
    private var locationsLow: ArrayList<Location> = arrayListOf()
    private var currentLocation: Location = Location("dummyprovider")
    private var lastLocation: Location = Location("dummyprovider")
    private var firstPoint = true
    private val rotateOpen: Animation by lazy {
        AnimationUtils.loadAnimation(
            this,
            R.anim.rotate_open
        )}
    private val rotateClose: Animation by lazy {
        AnimationUtils.loadAnimation(
            this,
            R.anim.rotate_close
        )}
    private val fromBottom: Animation by lazy {
        AnimationUtils.loadAnimation(
            this,
            R.anim.from_bottom
        )}
    private val toBottom: Animation by lazy {
        AnimationUtils.loadAnimation(
            this,
            R.anim.to_bottom
        )}


    @SuppressLint("MissingPermission")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_show_maps)
        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)


        locationManager = getSystemService(LOCATION_SERVICE) as LocationManager
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this)
        locationRequest = LocationRequest.create()
        locationCallback = object: LocationCallback(){
            override fun onLocationResult(location_result: LocationResult?) {
                super.onLocationResult(location_result)
                if (location_result != null) {
                    currentLocation = location_result.lastLocation
                    Log.e("locationCallback", location_result.lastLocation.toString())
                }
            }
        }
        fusedLocationProviderClient.requestLocationUpdates(locationRequest, locationCallback, null)
        fusedLocationProviderClient.lastLocation.addOnSuccessListener(this) {
            OnSuccessListener<Location> { location ->
                if (location != null) {
                    currentLocation = location
                }
            }
        }
        lastLocation = getLocation()
        currentLocation = getLocation()

        addLocBTN = findViewById(R.id.fabAdd)
        setHighBTN = findViewById(R.id.fabHigh)
        setBalancedBTN = findViewById(R.id.fabBalanced)
        setGPSBTN = findViewById(R.id.fabGPS)
        changeBTN = findViewById(R.id.fabChange)
        saveBTN = findViewById(R.id.fabSave)

        //Buttons on Click Listeners
        addLocBTN.setOnClickListener{
            getLocation()

            if(accuracy == "BALANCED") {
                if(currentLocation.latitude != lastLocation.latitude) {
                    locationsBalanced.add(currentLocation)
                }
                mMap.addCircle(
                    CircleOptions().center(
                        LatLng(
                            currentLocation.latitude,
                            currentLocation.longitude
                        )
                    )
                        .radius(1.0)
                        .strokeColor(Color.GREEN)
                        .fillColor(Color.GREEN)
                )
                if (!firstPoint) {
                    mMap.addPolyline(
                        PolylineOptions().add(
                            LatLng(
                                lastLocation.latitude,
                                lastLocation.longitude
                            )
                        ).add(LatLng(currentLocation.latitude, currentLocation.longitude))
                            .color(Color.GREEN)
                    )
                }
            }else if(accuracy == "GPS"){
                if(currentLocation.latitude != lastLocation.latitude) {
                    locationsLow.add(currentLocation)
                }
                mMap.addCircle(
                    CircleOptions().center(
                        LatLng(
                            currentLocation.latitude,
                            currentLocation.longitude
                        )
                    )
                        .radius(1.0)
                        .strokeColor(Color.BLUE)
                        .fillColor(Color.BLUE)
                )
                if (!firstPoint) {
                    mMap.addPolyline(
                        PolylineOptions().add(
                            LatLng(
                                lastLocation.latitude,
                                lastLocation.longitude
                            )
                        ).add(LatLng(currentLocation.latitude, currentLocation.longitude))
                            .color(Color.BLUE)
                    )
                }
            }else {
                if(currentLocation.latitude != lastLocation.latitude) {
                    locationsHigh.add(currentLocation)
                }
                mMap.addCircle(
                    CircleOptions().center(
                        LatLng(
                            currentLocation.latitude,
                            currentLocation.longitude
                        )
                    )
                        .radius(1.0)
                        .strokeColor(Color.RED)
                        .fillColor(Color.RED)
                )
                if (!firstPoint) {
                    mMap.addPolyline(
                        PolylineOptions().add(
                            LatLng(
                                lastLocation.latitude,
                                lastLocation.longitude
                            )
                        ).add(LatLng(currentLocation.latitude, currentLocation.longitude))
                            .color(Color.RED)
                    )
                }
            }

            firstPoint = false
            //Log.e("lastLocation", lastLocation.toString())
            //Log.e("currentLocation", currentLocation.toString())
            lastLocation = currentLocation

            mMap.moveCamera(
                CameraUpdateFactory.newLatLng(
                    LatLng(
                        currentLocation.latitude,
                        currentLocation.longitude
                    )
                )
            )
            mMap.animateCamera(
                CameraUpdateFactory.newLatLngZoom(
                    LatLng(
                        currentLocation.latitude,
                        currentLocation.longitude
                    ), 19F
                )
            )
        }

        changeBTN.setOnClickListener{
            onAddButtonClicked()
        }

        setHighBTN.setOnClickListener{
            accuracy = "HIGH"
            locationRequest =
                LocationRequest().setFastestInterval(1000).setInterval(1000).setPriority(
                    PRIORITY_HIGH_ACCURACY
                )
            Log.e("accuracy", "HIGH")
            changeBTN.setImageResource(R.drawable.high)
        }

        setBalancedBTN.setOnClickListener{
            accuracy = "BALANCED"
            locationRequest =
                LocationRequest().setFastestInterval(1000).setInterval(1000).setPriority(
                    PRIORITY_BALANCED_POWER_ACCURACY
                )
            Log.e("accuracy", "BALANCED")
            changeBTN.setImageResource(R.drawable.balanced)
        }

        setGPSBTN.setOnClickListener {
            accuracy = "GPS"
            Log.e("accuracy", "GPS")
            changeBTN.setImageResource(R.drawable.gps)
        }

        saveBTN.setOnClickListener {
            dataToJSON()
        }
    }

    private fun onAddButtonClicked() {
        setVisibility(clicked)
        setAnimation(clicked)
        clicked = !clicked
    }

    private fun setAnimation(clicked: Boolean) {
        if(!clicked){
            setHighBTN.visibility = View.VISIBLE
            setBalancedBTN.visibility = View.VISIBLE
            setGPSBTN.visibility = View.VISIBLE
        }else{
            setHighBTN.visibility = View.INVISIBLE
            setBalancedBTN.visibility = View.INVISIBLE
            setGPSBTN.visibility = View.INVISIBLE
        }
    }

    private fun setVisibility(clicked: Boolean) {
        if(!clicked){
            setHighBTN.startAnimation(fromBottom)
            setBalancedBTN.startAnimation(fromBottom)
            setGPSBTN.startAnimation(fromBottom)
            changeBTN.startAnimation(rotateOpen)
        }else{
            setHighBTN.startAnimation(toBottom)
            setBalancedBTN.startAnimation(toBottom)
            setGPSBTN.startAnimation(toBottom)
            changeBTN.startAnimation(rotateClose)
        }
    }

    @SuppressLint("MissingPermission")
    private fun getLocation(): Location{

        if(accuracy == "GPS") {
            val locationListener = LocationListener { location -> currentLocation = location }
            locationManager.requestLocationUpdates(
                LocationManager.GPS_PROVIDER,
                0L,
                0f,
                locationListener
            )
            Log.e("GPS", "has been called")
        }else {
            fusedLocationProviderClient.requestLocationUpdates(locationRequest, locationCallback, null)
        }

        return currentLocation
    }

    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap

        for(location in hardCode){
            mMap.addCircle(
                CircleOptions().center(
                    LatLng(
                        location.latitude,
                        location.longitude
                    )
                )
                    .radius(1.0)
                    .strokeColor(Color.GRAY)
                    .fillColor(Color.GRAY)
            )
        }

        //mMap.addMarker(MarkerOptions().position(LatLng(currentLocation.latitude,currentLocation.longitude)))
        mMap.moveCamera(
            CameraUpdateFactory.newLatLng(
                LatLng(
                    currentLocation.latitude,
                    currentLocation.longitude
                )
            )
        )
        mMap.animateCamera(
            CameraUpdateFactory.newLatLngZoom(
                LatLng(
                    currentLocation.latitude,
                    currentLocation.longitude
                ), 2f
            )
        )
    }

    private fun dataToJSON(){
        var jsonObject: JSONObject
        var jsonArray = JSONArray()

        for(i in locationsHigh){
            jsonObject = JSONObject()
            jsonObject.put("Latitude", i.latitude)
            jsonObject.put("Longitude", i.longitude)
            jsonObject.put("Time", i.time)
            jsonArray.put(jsonObject)
        }
        saveFile(jsonArray, "High")

        jsonArray = JSONArray()
        for(i in locationsBalanced){
            jsonObject = JSONObject()
            jsonObject.put("Latitude", i.latitude)
            jsonObject.put("Longitude", i.longitude)
            jsonObject.put("Time", i.time)
            jsonArray.put(jsonObject)
        }
        saveFile(jsonArray, "Balanced")

        jsonArray = JSONArray()
        for(i in locationsLow){
            jsonObject = JSONObject()
            jsonObject.put("Latitude", i.latitude)
            jsonObject.put("Longitude", i.longitude)
            jsonObject.put("Time", i.time)
            jsonArray.put(jsonObject)
        }
        saveFile(jsonArray, "GPS")
    }

    private fun saveFile(jsonArray: JSONArray, typ: String){
        val fileName: String = typ + SimpleDateFormat( "_MMdd-HHmm'.json'").format(Date())
        val myExternalFile = File(getExternalFilesDir(""),  fileName)

        try {
            val fileOutPutStream = FileOutputStream(myExternalFile)
            fileOutPutStream.write(jsonArray.toString().toByteArray())
            fileOutPutStream.close()
        } catch (e: IOException) {
            e.printStackTrace()
        }

        Toast.makeText(applicationContext, "Data saved", Toast.LENGTH_SHORT).show()
    }
}
