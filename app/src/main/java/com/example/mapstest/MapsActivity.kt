package com.example.mapstest



import android.content.Context

import android.graphics.Color
import android.location.Address
import android.location.Geocoder
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.core.content.ContentProviderCompat.requireContext
import androidx.fragment.app.FragmentActivity
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.*
import com.google.maps.android.data.geojson.GeoJsonLayer
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json
import org.json.JSONException
import org.json.JSONObject
import java.io.File

import java.io.IOException
import java.io.InputStream
import java.nio.file.*
import java.nio.file.attribute.BasicFileAttributes
import java.util.*
import kotlin.collections.ArrayList
import kotlin.io.path.Path


class MapsActivity : FragmentActivity(), OnMapReadyCallback {
    private var mMap: GoogleMap? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_maps)
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.map) as SupportMapFragment?
        mapFragment!!.getMapAsync(this)


    }




    @RequiresApi(Build.VERSION_CODES.O)
    @OptIn(ExperimentalSerializationApi::class)
    override fun onMapReady(googleMap: GoogleMap) {

        mMap = googleMap
        mMap!!.uiSettings.isZoomControlsEnabled = true


        val success = googleMap.setMapStyle(
            MapStyleOptions.loadRawResourceStyle(
                this, R.raw.style_json
            )
        )
        val capitalCity = LatLng(40.416775, -3.70379)
        mMap!!.moveCamera(CameraUpdateFactory.newLatLngZoom(capitalCity, 3f))

        //var newLayer : GeoJsonLayer = GeoJsonLayer(mMap, R.raw.us_geojson, applicationContext)

        var layerArray  = ArrayList<GeoJsonLayer>()
        var countryArray = arrayOf("United States", "Spain")


        // List of JSON files
        val jsonFiles = listOf("es_geojson.json", "us_geojson.json")
        // List of JSON objects
        val jsonObjects = mutableListOf<JSONObject>()
        // Loop through the list of files and import JSON objects from each file
        for (jsonFile in jsonFiles) {
            //System.out.print(File("assets/json/es_geojson.json"))

            println("DIRECTORY" + System.getProperty("user.dir"))


            val jsonString = File("app\\src\\androidTest\\assets\\json\\" + jsonFile).readText()
            val jsonObject = Json.decodeFromString<JSONObject>(jsonString)
            val tmpLayer = GeoJsonLayer(mMap, jsonObject)
            val style = tmpLayer.defaultPolygonStyle
            style.fillColor = Color.BLUE
            style.strokeColor = Color.BLUE
            style.strokeWidth = 1f
            style.isClickable = false
            layerArray.add(tmpLayer)
            jsonObjects.add(jsonObject)
        }

        for (layer in layerArray){
            layer.addLayerToMap()
        }

        /*for (selectedCountry in countryArray)
        {
            try {
                val layer = GeoJsonLayer(mMap, R.raw.us_geojson, applicationContext)
                val style = layer.defaultPolygonStyle
                style.fillColor = Color.BLUE
                style.strokeColor = Color.BLUE
                style.strokeWidth = 1f
                style.isClickable = false
                layer.addLayerToMap()
                newLayer = layer
            } catch (ex: IOException) {
                Log.e("IOException", ex.localizedMessage)
            } catch (ex: JSONException) {
                Log.e("JSONException", ex.localizedMessage)
            }
        }*/




        val markerList = mutableListOf<Marker>()
        //newLayer.map = null
        mMap!!.setOnMapClickListener{


            //Here, clear list of markers on click
            markerList.forEach { it.remove() }
            markerList.clear()


            //Here, get country from click location
            val geocoder = Geocoder(this, Locale.getDefault())
            val addresses: List<Address> = geocoder.getFromLocation(it.latitude, it.longitude, 1) as List<Address>
            var country: String = ""
            if (addresses.isNotEmpty()) {
                country = addresses[0].getCountryName()
            }

            //Here, check if country is part of displayed countries
            val displayedCountries = ArrayList<String>()
            displayedCountries.add("Spain")
            displayedCountries.add("United States")


            //Here, get Information based on country clicked

            val displayedInformation : String = ""

            //Here, create marker

            if(displayedCountries.contains(country)){
                //newLayer.map = mMap
                val clickedCountryLatLng = LatLng(it.latitude, it.longitude)
                val informationWindow = mMap?.addMarker(
                    MarkerOptions()
                        .position(clickedCountryLatLng)
                        .title(country.toString())
                        .icon(BitmapDescriptorFactory.fromResource(R.drawable.marker_icon))
                        .snippet(displayedInformation)//Will contain breed repartition in
                )
                informationWindow?.alpha = 0.0f;
                informationWindow?.setInfoWindowAnchor(.5f, 1.0f);

                if (informationWindow != null) {
                    markerList.add(informationWindow)
                }
                informationWindow?.showInfoWindow()
            } else { //newLayer.map = null }

        }

    }



    }
}