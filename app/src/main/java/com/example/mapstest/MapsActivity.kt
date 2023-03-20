package com.example.mapstest



import android.content.Context

import android.graphics.Color
import android.location.Address
import android.location.Geocoder
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts.PickVisualMedia.ImageAndVideo.toString
import androidx.annotation.RequiresApi
import androidx.core.content.ContentProviderCompat.requireContext
import androidx.fragment.app.FragmentActivity
import com.github.doyaaaaaken.kotlincsv.dsl.csvReader
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
import java.net.URI
import java.nio.file.*
import java.nio.file.attribute.BasicFileAttributes
import java.util.*
import java.util.Objects.toString
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


        // read from CSV File that contains country geoJSONS and names
        val file = applicationContext.assets.open("data.csv")
            .bufferedReader()
            .use { it.readText()}
        val countries = arrayListOf<List<String>>()
        val rows: List<List<String>> = csvReader().readAll(file)
        for (row in rows){
            /*countries.add(listOf(row[0], row[4]))*/
            countries.add(listOf(row[0], row[1], row[2]))
        }

        // List of JSON objects
        val jsonObjects = mutableListOf<JSONObject>()
        val displayedCountries = ArrayList<String>()
        // Loop through the countries and create associated layers
        for (country in countries) {
            val jsonString = country[0]
            displayedCountries.add(country[1])
            Log.d("GeoJSON : ", jsonString)
            val jsonObject = JSONObject(jsonString)
            val tmpLayer = GeoJsonLayer(mMap, jsonObject)
            val style = tmpLayer.defaultPolygonStyle
            style.fillColor = Color.BLUE
            style.strokeColor = Color.BLUE
            style.strokeWidth = 1f
            style.isClickable = false
            layerArray.add(tmpLayer)
            jsonObjects.add(jsonObject)
        }

        //List of markers
        val markerList = mutableListOf<Marker>()

        //Listens for map Click Events
        mMap!!.setOnMapClickListener{

            //Here, clears the map
            mMap!!.clear()

            //Here, get country from click location
            val geocoder = Geocoder(this, Locale.getDefault())
            val addresses: List<Address> = geocoder.getFromLocation(it.latitude, it.longitude, 1) as List<Address>
            var country: String = ""
            if (addresses.isNotEmpty() && addresses[0].countryName != null) {
                country = addresses[0].countryName
            }

            // Marker Information String
            val displayedInformation : String = ""

            //Here, check if country is part of displayed countries
            if(displayedCountries.contains(country)){

                val clickedCountryLatLng = LatLng(it.latitude, it.longitude)
                val informationWindow = mMap?.addMarker(
                    MarkerOptions()
                        .position(clickedCountryLatLng)
                        .title(country.toString())
                        .icon(BitmapDescriptorFactory.fromResource(R.drawable.marker_icon))
                        .snippet(countries[displayedCountries.indexOf(country)][2])//Will contain breed repartition in
                )
                informationWindow?.alpha = 0.0f;
                informationWindow?.setInfoWindowAnchor(.5f, 1.0f);

                //Removes layer that has the same index as the country name from map and adds it again
                layerArray[displayedCountries.indexOf(country)].removeLayerFromMap()
                layerArray[displayedCountries.indexOf(country)].addLayerToMap()

                if (informationWindow != null) {
                    markerList.add(informationWindow)
                }
                informationWindow?.showInfoWindow()
            } else { //newLayer.map = null }

        }

    }

    }
}