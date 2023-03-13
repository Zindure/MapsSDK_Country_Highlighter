package com.example.mapstest


import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.FragmentActivity
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions
import com.google.maps.android.data.geojson.GeoJsonLayer
import org.json.JSONException
import java.io.IOException

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
    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap
        mMap!!.uiSettings.isZoomControlsEnabled = true

        val clickedCountryLatLng = LatLng(40.4167754, -3.7037902)
        val informationWindow = mMap?.addMarker(
            MarkerOptions()
                .position(clickedCountryLatLng)
                .title("Madrid")
                .snippet("Information")
        )
        informationWindow?.showInfoWindow()

        val madrid = LatLng(40.416775, -3.70379)
        mMap!!.moveCamera(CameraUpdateFactory.newLatLngZoom(madrid, 3f))
        try {
            val layer = GeoJsonLayer(mMap, R.raw.es_geojson, applicationContext)
            val style = layer.defaultPolygonStyle
            style.fillColor = Color.MAGENTA
            style.strokeColor = Color.MAGENTA
            style.strokeWidth = 1f
            layer.addLayerToMap()
        } catch (ex: IOException) {
            Log.e("IOException", ex.localizedMessage)
        } catch (ex: JSONException) {
            Log.e("JSONException", ex.localizedMessage)
        }
    }



}