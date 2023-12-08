package com.github.dach83.gps_tracker.presentation.compose

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.viewinterop.AndroidView
import com.github.dach83.gps_tracker.domain.model.SharedLocation
import com.yandex.mapkit.geometry.Circle
import com.yandex.mapkit.geometry.Point
import com.yandex.mapkit.map.CameraPosition
import com.yandex.mapkit.mapview.MapView

@Composable
fun LocationOnMap(
    location: SharedLocation?,
    modifier: Modifier = Modifier
) {
    AndroidView(
        modifier = modifier,
        factory = { context ->
            MapView(context)
        },
        update = { mapView ->
            mapView.mapWindow.map.mapObjects.clear()
            if (location == null) return@AndroidView

            val point = Point(location.latitude, location.longitude)
            val defaultZoom = 12f
            val azimuth = 6f
            val tilt = 0f
            val radius = 250f

            // move map center to current location
            mapView.mapWindow.map.move(
                CameraPosition(point, defaultZoom, azimuth, tilt)
            )

            // show point on map
            mapView.mapWindow.map.mapObjects.addCircle(
                Circle(point, radius)
            )
        }
    )
}
