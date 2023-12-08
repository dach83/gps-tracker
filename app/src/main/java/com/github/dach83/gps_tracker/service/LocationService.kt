package com.github.dach83.gps_tracker.service

import android.app.NotificationManager
import android.app.Service
import android.content.Context
import android.content.Intent
import android.os.IBinder
import android.util.Log
import androidx.core.app.NotificationCompat
import com.github.dach83.gps_tracker.R
import com.github.dach83.gps_tracker.data.AndroidLocationClient
import com.github.dach83.gps_tracker.domain.LocationClient
import com.github.dach83.gps_tracker.domain.LocationStore
import com.github.dach83.gps_tracker.domain.errors.LocationException
import com.google.android.gms.location.LocationServices
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.retryWhen
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

class LocationService : Service(), KoinComponent {

    private val serviceScope = CoroutineScope(SupervisorJob() + Dispatchers.IO)
    private val locationClient: LocationClient by lazy {
        AndroidLocationClient(
            context = applicationContext,
            client = LocationServices.getFusedLocationProviderClient(applicationContext)
        )
    }
    private val locationStore: LocationStore by inject()

    override fun onBind(intent: Intent): IBinder? {
        return null
    }

    override fun onDestroy() {
        super.onDestroy()
        serviceScope.cancel()
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        when (intent?.action) {
            ACTION_START -> startLocationService()
            ACTION_STOP -> stopLocationService()
        }
        return super.onStartCommand(intent, flags, startId)
    }

    private fun startLocationService() {
        val notification = NotificationCompat.Builder(this, "location")
            .setContentTitle("Tracking location")
            .setContentText("Location is ...")
            .setSmallIcon(R.drawable.ic_launcher_background)
            .setOngoing(true)

        val notificationManager =
            getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        locationClient.getLocationUpdates(REQUEST_INTERVAL_MS)
            .retryWhen { cause, _ -> cause is LocationException }
            .catch { error -> error.printStackTrace() }
            .onEach { location ->
                val lat = location.latitude.toString()
                val long = location.longitude.toString()
                val updatedNotification = notification.setContentText("Location is $lat, $long")
                notificationManager.notify(SERVICE_ID, updatedNotification.build())
                locationStore.lastLocation = location
                Log.d("@@@", "startLocationService: $location")
            }
            .launchIn(serviceScope)

        startForeground(SERVICE_ID, notification.build())
    }

    private fun stopLocationService() {
        stopForeground(STOP_FOREGROUND_REMOVE)
        stopSelf()
    }

    companion object {
        const val SERVICE_ID = 1
        const val ACTION_START = "ACTION_START"
        const val ACTION_STOP = "ACTION_STOP"
        const val REQUEST_INTERVAL_MS = 10000L
    }
}
