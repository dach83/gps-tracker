package com.github.dach83.gps_tracker.presentation

import android.Manifest
import android.content.Context
import android.content.Intent
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.github.dach83.gps_tracker.service.LocationService

@Composable
fun StartStopScreen() {
    val context = LocalContext.current
    val serviceLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestMultiplePermissions()
    ) { results ->
        val isGranted = results.map { it.value }.all { it }
        if (isGranted) {
            sendLocationServiceAction(context, LocationService.ACTION_START)
        }
    }

    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Button(onClick = {
            serviceLauncher.launch(
                arrayOf(
                    Manifest.permission.ACCESS_COARSE_LOCATION,
                    Manifest.permission.ACCESS_FINE_LOCATION
                )
            )
        }) {
            Text(text = "Start")
        }
        Spacer(modifier = Modifier.height(16.dp))
        Button(onClick = {
            sendLocationServiceAction(context, LocationService.ACTION_STOP)
        }) {
            Text(text = "Stop")
        }
    }
}

private fun sendLocationServiceAction(context: Context, action: String) {
    val intent = Intent(context, LocationService::class.java)
    intent.action = action
    context.startService(intent)
}
