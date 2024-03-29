package com.github.dach83.gps_tracker.domain

import com.github.dach83.gps_tracker.domain.model.SharedLocation
import kotlinx.coroutines.flow.Flow

interface LocationClient {

    fun getLocationUpdates(timeInterval: Long): Flow<SharedLocation>
}
