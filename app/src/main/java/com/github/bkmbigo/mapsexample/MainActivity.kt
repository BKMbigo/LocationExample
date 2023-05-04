package com.github.bkmbigo.mapsexample

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import android.net.Uri
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.Settings
import android.view.View
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import com.github.bkmbigo.mapsexample.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    private val locationPermissionRequest = registerForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->

        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.N) {
            // The method getOrDefault is only available to Android N and later devices
            when {
                permissions.getOrDefault(Manifest.permission.ACCESS_FINE_LOCATION, false) -> {
                    // Fine Location granted
                    setUpUI(true)
                }

                permissions.getOrDefault(Manifest.permission.ACCESS_COARSE_LOCATION, false) -> {
                    // Coarse Location Granted
                    setUpUI(false)
                }

                else -> {
                    // Permission Denied
                    setUpUI(false, isPermissionFullyDenied = true)
                }
            }
        } else {
            if (permissions.get(Manifest.permission.ACCESS_FINE_LOCATION) == true) {
                // Fine Location Granted
                setUpUI(true)
            } else if (permissions.get(Manifest.permission.ACCESS_FINE_LOCATION) == true) {
                // Coarse Location Granted
                setUpUI(true)
            } else {
                // Permission Denied
                setUpUI(false, isPermissionFullyDenied = true)
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setButtonListeners()
        setUpUI()
    }

    private fun setUpUI(currentLocation: Location) {
        setUpUI(true, currentLocation)
    }

    private fun setUpUI(isPermissionGranted: Boolean = isCoarsePermissionGranted(), currentLocation: Location? = null, isPermissionFullyDenied: Boolean = false){
        binding.cvCurrentLocation.visibility = visibility(currentLocation != null)
        binding.btRequestPermission.visibility = visibility(!isPermissionGranted && !isPermissionFullyDenied)
        binding.btCurrentLocation.visibility = visibility(isPermissionGranted)
        binding.btLastKnownLocation.visibility = visibility(isPermissionGranted)
        binding.cvPermissionDenied.visibility = visibility(isPermissionFullyDenied)

        currentLocation?.let { location ->
            binding.tvLongitudeValue.text = location.longitude.toString()
            binding.tvLatitudeValue.text = location.latitude.toString()
            binding.tvAltitudeValue.text = location.altitude.toString()
            binding.tvAccuracyValue.text = location.accuracy.toString()

            binding.tvLabelCurrentLocation.text = generateHeading(isFinePermissionGranted(), true)
        }
    }

    private fun visibility(condition: Boolean): Int = if(condition) View.VISIBLE else View.GONE
    private fun visibility(condition: () -> Boolean): Int = if(condition.invoke()) View.VISIBLE else View.GONE

    private fun generateHeading(isFineLocation: Boolean, isCurrentLocation: Boolean): String =
        "${if(isCurrentLocation) "Current" else "Last Known"} Location (${if(isFineLocation) "Fine" else "Coarse"})"

    private fun setButtonListeners() {
        binding.btRequestPermission.setOnClickListener {
            requestPermissions()
        }

        binding.btLastKnownLocation.setOnClickListener {

        }

        binding.btCurrentLocation.setOnClickListener {

        }
        binding.btGoToSettings.setOnClickListener {
            goToSettings()
        }
    }

    private fun requestPermissions() {
        locationPermissionRequest.launch(
            arrayOf(
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION
            )
        )
    }

    private fun goToSettings() {
        startActivity(
            Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS).apply {
                data = Uri.fromParts("package", packageName, null)
            }
        )
    }

    private fun isFinePermissionGranted(): Boolean {
        return ContextCompat.checkSelfPermission(
            this,
            Manifest.permission.ACCESS_FINE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED
    }

    private fun isCoarsePermissionGranted(): Boolean {
        return ContextCompat.checkSelfPermission(
            this,
            Manifest.permission.ACCESS_FINE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED
    }
}