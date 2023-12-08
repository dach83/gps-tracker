package com.github.dach83.gps_tracker.data

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationManager
import android.os.Looper
import androidx.core.content.ContextCompat
import com.github.dach83.gps_tracker.domain.LocationClient
import com.github.dach83.gps_tracker.domain.errors.LocationException
import com.github.dach83.gps_tracker.domain.model.SharedLocation
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.Granularity
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.Priority
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow

class AndroidLocationClient(
    private val context: Context,
    private val client: FusedLocationProviderClient
) : LocationClient {

    @SuppressLint("MissingPermission")
    override fun getLocationUpdates(timeInterval: Long): Flow<SharedLocation> = callbackFlow {
        if (!hasLocationPermissions) {
            throw LocationException("Location permissions not granted")
        }
        if (!hasLocationProviders()) {
            throw LocationException("Location providers is disabled")
        }

        val request = LocationRequest.Builder(Priority.PRIORITY_HIGH_ACCURACY, timeInterval)
            // setMinUpdateDistanceMeters(minimalDistance)
            .setGranularity(Granularity.GRANULARITY_PERMISSION_LEVEL)
            .setWaitForAccurateLocation(true)
            .build()

        val callback = object : LocationCallback() {
            override fun onLocationResult(result: LocationResult) {
                super.onLocationResult(result)
                result.lastLocation?.let {
                    trySend(it.toSharedLocation())
                }
            }
        }

        client.requestLocationUpdates(request, callback, Looper.getMainLooper())
        awaitClose {
            client.removeLocationUpdates(callback)
        }
    }

    private val hasLocationPermissions
        get() = listOf(
            Manifest.permission.ACCESS_COARSE_LOCATION,
            Manifest.permission.ACCESS_FINE_LOCATION
        ).map { permissionName ->
            ContextCompat.checkSelfPermission(context, permissionName)
        }.all { permissionStatus ->
            permissionStatus == PackageManager.PERMISSION_GRANTED
        }

    private fun hasLocationProviders(): Boolean {
        val locationManager = context.getSystemService(Context.LOCATION_SERVICE) as LocationManager
        val isGpsEnabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)
        val isNetworkEnabled = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)
        return isGpsEnabled || isNetworkEnabled
    }

    private fun Location.toSharedLocation() = SharedLocation(
        latitude = latitude,
        longitude = longitude
    )
}
