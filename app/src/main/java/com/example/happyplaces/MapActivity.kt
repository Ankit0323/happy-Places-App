package com.example.happyplaces

import android.Manifest
import android.content.pm.PackageManager
import android.graphics.Color
import android.location.Location
import android.location.LocationListener
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.PersistableBundle
import android.view.animation.BounceInterpolator
import android.widget.Toast
import androidx.core.app.ActivityCompat


import com.example.happyplaces.databinding.ActivityMapBinding
import com.example.happyplaces.room.happyPlacesData
import com.mapbox.android.core.location.LocationEngine
import com.mapbox.android.core.permissions.PermissionsListener
import com.mapbox.android.core.permissions.PermissionsManager
import com.mapbox.geojson.Point
import com.mapbox.mapboxsdk.Mapbox
import com.mapbox.mapboxsdk.annotations.MarkerOptions
import com.mapbox.mapboxsdk.camera.CameraPosition
import com.mapbox.mapboxsdk.camera.CameraUpdateFactory
import com.mapbox.mapboxsdk.geometry.LatLng
import com.mapbox.mapboxsdk.location.LocationComponent
import com.mapbox.mapboxsdk.location.LocationComponentActivationOptions
import com.mapbox.mapboxsdk.location.LocationComponentOptions
import com.mapbox.mapboxsdk.location.modes.CameraMode
import com.mapbox.mapboxsdk.location.modes.RenderMode
import com.mapbox.mapboxsdk.maps.MapView
import com.mapbox.mapboxsdk.maps.MapboxMap
import com.mapbox.mapboxsdk.maps.OnMapReadyCallback
import com.mapbox.mapboxsdk.maps.Style
import com.mapbox.mapboxsdk.plugins.annotation.SymbolManager
import com.mapbox.mapboxsdk.plugins.annotation.SymbolOptions


class MapActivity : AppCompatActivity(),PermissionsListener,OnMapReadyCallback {
    private lateinit var mapView: MapView
    private lateinit var binding: ActivityMapBinding
    private lateinit var mMapboxMap: MapboxMap
    private lateinit var permissionsManager: PermissionsManager
    private lateinit var location: Location
    private var mapPlacesData:happyPlacesData?=null
    private var locationEngine: LocationEngine? = null
    private var locationComponent: LocationComponent? = null
    private lateinit var style: Style
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Mapbox.getInstance(this, getString(R.string.access_token))
        binding = ActivityMapBinding.inflate(layoutInflater)
        setContentView(binding.root)
        if(intent.hasExtra("FROM_DETAILS_TO_MAP")){
            mapPlacesData=intent.getParcelableExtra("FROM_DETAILS_TO_MAP") as happyPlacesData
        }
        if(mapPlacesData!=null){
            setSupportActionBar(binding.mapToolBar)
            supportActionBar!!.title=mapPlacesData!!.title
            supportActionBar!!.setDisplayHomeAsUpEnabled(true)
            binding.mapToolBar.setNavigationOnClickListener {
                onBackPressed()
            }
        }

        mapView = binding.map
        mapView.onCreate(savedInstanceState)
        mapView?.getMapAsync(this)


    }


    fun enableLocationComponent() {
        if (PermissionsManager.areLocationPermissionsGranted(this@MapActivity)) {

            // Get an instance of the component
            val locationComponent = mMapboxMap?.locationComponent

            // Activate with a built LocationComponentActivationOptions object
            locationComponent?.activateLocationComponent(LocationComponentActivationOptions.builder(this, style).build())

            // Enable to make component visible
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                    != PackageManager.PERMISSION_GRANTED &&
                    ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION)
                    != PackageManager.PERMISSION_GRANTED) {
                locationComponent.isLocationComponentEnabled = true

            }
            locationComponent?.isLocationComponentEnabled = true

            // Set the component's camera mode
            locationComponent?.cameraMode = CameraMode.TRACKING

            // Set the component's render mode
            locationComponent?.renderMode = RenderMode.COMPASS

        } else {

            permissionsManager = PermissionsManager(this)

            permissionsManager?.requestLocationPermissions(this)

        }
    }

    override fun onExplanationNeeded(permissionsToExplain: MutableList<String>?) {
        Toast.makeText(this, "You need to give permission in order to enable location marker", Toast.LENGTH_LONG).show()
    }

    override fun onPermissionResult(granted: Boolean) {
        if (granted) {
            enableLocationComponent()
        } else {
            Toast.makeText(applicationContext, "you denied the permisssion", Toast.LENGTH_SHORT).show()
        }
    }


    override fun onMapReady(mapboxMap: MapboxMap) {
        mMapboxMap = mapboxMap
        mapboxMap.setStyle(Style.MAPBOX_STREETS) {
            mapboxMap?.addMarker(MarkerOptions()
                    .position(LatLng(mapPlacesData!!.latitude, mapPlacesData!!.longitude))
                    .title(mapPlacesData!!.title))
            //style -> enableLocationComponent()
           /* var symbolManager = SymbolManager(mapView, mMapboxMap, style)


            symbolManager.iconAllowOverlap = true
            symbolManager.iconIgnorePlacement = true

// Create a symbol at the specified location.
            val symbol = symbolManager.create(SymbolOptions()
                    .withLatLng(LatLng(mapPlacesData!!.latitude, mapPlacesData!!.longitude)))*/

                    val position = CameraPosition.Builder()
                    .target(LatLng(mapPlacesData!!.latitude, mapPlacesData!!.longitude))
                            .zoom(5.0)
                      .tilt(20.0)
                    .build()
            mapboxMap.animateCamera(CameraUpdateFactory.newCameraPosition(position))

        }


    }


    override fun onStart() {
        super.onStart()
        mapView.onStart()
    }

    override fun onResume() {
        super.onResume()
        mapView.onResume()
    }

    override fun onPause() {
        super.onPause()
        mapView.onPause()
    }

    override fun onLowMemory() {
        super.onLowMemory()
        mapView.onLowMemory()
    }

    override fun onDestroy() {
        super.onDestroy()
        mapView.onDestroy()
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        if (outState != null) {
            mapView.onSaveInstanceState(outState)
        }
    }
}
//mapbox:mapbox_cameraTargetLat="-32.557013"
//mapbox:mapbox_cameraTargetLng="-56.149056"