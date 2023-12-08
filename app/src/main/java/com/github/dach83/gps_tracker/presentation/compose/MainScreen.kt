package com.github.dach83.gps_tracker.presentation.compose

import android.Manifest
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.github.dach83.gps_tracker.domain.model.SharedLocation
import com.github.dach83.gps_tracker.service.LocationService

@Composable
fun MainScreen(
    location: SharedLocation?
) {
    val context = LocalContext.current
    val serviceLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestMultiplePermissions()
    ) { results ->
        val isAllGranted = results.map { it.value }.all { it }
        if (isAllGranted) {
            sendCommandToLocationService(context, LocationService.ACTION_START)
        }
    }

    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.BottomCenter
    ) {
        LocationOnMap(
            location = location,
            modifier = Modifier.fillMaxSize()
        )
        StartAndStopButtons(
            onStartClick = {
                serviceLauncher.launch(
                    neededPermissions()
                )
            },
            onStopClick = {
                sendCommandToLocationService(context, LocationService.ACTION_STOP)
            }
        )
    }
}

@Composable
private fun StartAndStopButtons(
    onStartClick: () -> Unit,
    onStopClick: () -> Unit
) {
    Row(
        horizontalArrangement = Arrangement.SpaceEvenly,
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = 24.dp)
    ) {
        Button(onClick = onStartClick) {
            Text(text = "Start tracking")
        }
        Button(onClick = onStopClick) {
            Text(text = "Stop tracking")
        }
    }
}

private fun sendCommandToLocationService(context: Context, action: String) {
    val intent = Intent(context, LocationService::class.java)
    intent.action = action
    context.startService(intent)
}

private fun neededPermissions() = buildList {
    add(Manifest.permission.ACCESS_COARSE_LOCATION)
    add(Manifest.permission.ACCESS_FINE_LOCATION)
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
        add(Manifest.permission.POST_NOTIFICATIONS)
    }
}.toTypedArray()
