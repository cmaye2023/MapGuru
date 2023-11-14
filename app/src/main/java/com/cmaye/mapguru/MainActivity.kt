package com.cmaye.mapguru

import android.Manifest
import android.annotation.SuppressLint
import android.content.pm.PackageManager
import android.location.Location
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.cmaye.mapguru.databinding.ActivityMainBinding
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.gms.tasks.OnCompleteListener
import com.google.android.gms.tasks.Task

class MainActivity : AppCompatActivity(), OnMapReadyCallback {
    private lateinit var binding: ActivityMainBinding
    private lateinit var mGoogleMap: GoogleMap
    private lateinit var fusedLocationProviderClient: FusedLocationProviderClient
    @SuppressLint("MissingPermission")
    private var requestPermissionLauncher =
        registerForActivityResult(ActivityResultContracts.RequestPermission())
        { isGranted ->
            if (isGranted) {
                // Set the My Location button to be enabled
                mGoogleMap.isMyLocationEnabled = true

                // Set a click listener for the My Location button
                mGoogleMap.setOnMyLocationButtonClickListener {
                    getCurrentLocation()
                    // Return 'true' to consume the event and not move the camera to the default location
                    true
                }
                Toast.makeText(this, "Granted", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(this, "Denied", Toast.LENGTH_SHORT).show()
            }
        }


    override fun onResume() {
        super.onResume()
        if (isCheckPermission())
        {
            getCurrentLocation()
        }
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this)


        binding.mMap.onCreate(savedInstanceState)
        binding.mMap.getMapAsync(this)

    }

    private fun isCheckPermission() : Boolean
    {
        return ContextCompat.checkSelfPermission(this,android.Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED &&
                ContextCompat.checkSelfPermission(this,android.Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED
    }

    @SuppressLint("MissingPermission")
    override fun onMapReady(googleMap: GoogleMap) {
        mGoogleMap = googleMap
        if (isCheckPermission())
        {
            // Set the My Location button to be enabled
            mGoogleMap.isMyLocationEnabled = true

            // Set a click listener for the My Location button
            mGoogleMap.setOnMyLocationButtonClickListener {
                getCurrentLocation()
                // Return 'true' to consume the event and not move the camera to the default location
                true
            }
        }else{
            requestPermissionLauncher.launch(android.Manifest.permission.ACCESS_FINE_LOCATION)
        }
    }


    private fun getCurrentLocation() {
        try {
            fusedLocationProviderClient.lastLocation.addOnCompleteListener(this, OnCompleteListener<Location> { task ->
                if (task.isSuccessful && task.result != null) {
                    val lastKnownLocation: Location = task.result!!
                    val latitude = lastKnownLocation.latitude
                    val longitude = lastKnownLocation.longitude

                    //TODO
                    Log.i("CMAT_Lat",latitude.toString())
                    Log.i("CMAT_Long",longitude.toString())

                    val latLng = LatLng(latitude, longitude)
                    mGoogleMap.animateCamera(CameraUpdateFactory.newLatLng(latLng))
                    mGoogleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 16f))
                    val markerOption = MarkerOptions().position(latLng).title("Current Location")
                    mGoogleMap.addMarker(markerOption)
                } else {
                    // Handle the error
                }
            })

        } catch (ex: SecurityException) {
            Toast.makeText(this, ex.toString(), Toast.LENGTH_SHORT).show()
            Log.e("EXC",ex.toString())
        }
    }
}