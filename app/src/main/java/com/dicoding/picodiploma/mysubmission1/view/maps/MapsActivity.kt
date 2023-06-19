package com.dicoding.picodiploma.mysubmission1.view.maps

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.content.res.Resources
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import androidx.lifecycle.lifecycleScope
import com.dicoding.picodiploma.mysubmission1.R
import com.dicoding.picodiploma.mysubmission1.data.Result
import com.dicoding.picodiploma.mysubmission1.databinding.ActivityMapsBinding
import com.dicoding.picodiploma.mysubmission1.model.UserPreference
import com.dicoding.picodiploma.mysubmission1.networking.ListStories
import com.dicoding.picodiploma.mysubmission1.utils.ShowLoading
import com.dicoding.picodiploma.mysubmission1.view.ViewModelFactory
import com.dicoding.picodiploma.mysubmission1.view.main.MainViewModel
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.*

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "settings")

class MapsActivity : AppCompatActivity(), OnMapReadyCallback {

    private lateinit var mMap: GoogleMap
    private lateinit var binding: ActivityMapsBinding
    private lateinit var showLoading: ShowLoading
    private lateinit var progressBar: View
    private val mapsViewModel by viewModels<MapsViewModel> {
        ViewModelFactory(UserPreference.getInstance(dataStore), this)
    }
    private val mainViewModel by viewModels<MainViewModel> {
        ViewModelFactory(UserPreference.getInstance(dataStore), this)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMapsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)

        showLoading = ShowLoading()
        progressBar = binding.progressBar
        supportActionBar?.title = getString(R.string.title_maps)
    }

    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap

        mMap.uiSettings.apply {
            isZoomControlsEnabled = true
            isIndoorLevelPickerEnabled = true
            isCompassEnabled = true
            isMapToolbarEnabled = true
        }

        mMap.setOnPoiClickListener { pointOfInterest ->
            val poiMarker = mMap.addMarker(
                MarkerOptions()
                    .position(pointOfInterest.latLng)
                    .title(pointOfInterest.name)
                    .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE))
            )
            poiMarker?.showInfoWindow()
        }

        getMyLocation()
        setMapStyle()
        setMapsLocation()
    }

    private val requestPermissionLauncher =
        registerForActivityResult(
            ActivityResultContracts.RequestPermission()
        ) { isGranted: Boolean ->
            if (isGranted) {
                getMyLocation()
            }
        }

    private fun getMyLocation() {
        if (ContextCompat.checkSelfPermission(
                this.applicationContext,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            mMap.isMyLocationEnabled = true
        } else {
            requestPermissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION)
        }
    }

    private fun setMapStyle() {
        try {
            val success =
                mMap.setMapStyle(MapStyleOptions.loadRawResourceStyle(this, R.raw.map_style))
            if (!success) {
                Toast.makeText(this, getString(R.string.failed_style_maps), Toast.LENGTH_SHORT)
                    .show()
            }
        } catch (exception: Resources.NotFoundException) {
            Log.e(TAG, getString(R.string.failed_find_style), exception)
        }
    }

    private fun setMapsLocation() {
        lifecycleScope.launchWhenCreated {
            mainViewModel.getUser().collect {
                mapsViewModel.getStoriesMaps(it.token).observe(this@MapsActivity) { result ->
                    if (result != null) {
                        when (result) {
                            is Result.Loading -> {
                                showLoading.showLoading(true, progressBar)
                            }

                            is Result.Error -> {
                                showLoading.showLoading(false, progressBar)
                                Toast.makeText(this@MapsActivity, getString(R.string.failed_map_loc, result.error),
                                    Toast.LENGTH_SHORT).show()
                            }

                            is Result.Success -> {
                                showLoading.showLoading(false, progressBar)
                                val dataLoc = result.data.story
                                pinMarker(dataLoc)
                            }
                        }
                    }
                }
            }
        }

    }

    private val boundsBuilder = LatLngBounds.Builder()
    private fun pinMarker(loc: List<ListStories>) {
        loc.forEach{ item ->
            if (item.lat != null && item.lon != null) {
                val marker = LatLng(item.lat, item.lon)
                mMap.addMarker(MarkerOptions()
                    .position(marker)
                    .title(item.name)
                    .icon(
                        BitmapDescriptorFactory.defaultMarker(
                            BitmapDescriptorFactory.HUE_AZURE
                        )
                    ))
                boundsBuilder.include(marker)
            }

            val bounds: LatLngBounds = boundsBuilder.build()
            mMap.animateCamera(
                CameraUpdateFactory.newLatLngBounds(
                    bounds,
                    resources.displayMetrics.widthPixels,
                    resources.displayMetrics.heightPixels,
                    300
                )
            )
        }

    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.map_options, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.normal_type -> {
                mMap.mapType = GoogleMap.MAP_TYPE_NORMAL
                true
            }
            R.id.satellite_type -> {
                mMap.mapType = GoogleMap.MAP_TYPE_SATELLITE
                true
            }
            R.id.terrain_type -> {
                mMap.mapType = GoogleMap.MAP_TYPE_TERRAIN
                true
            }
            R.id.hybrid_type -> {
                mMap.mapType = GoogleMap.MAP_TYPE_HYBRID
                true
            }
            else -> {
                super.onOptionsItemSelected(item)
            }
        }
    }

    companion object {
        private const val TAG = "MapsActivity"
    }
}