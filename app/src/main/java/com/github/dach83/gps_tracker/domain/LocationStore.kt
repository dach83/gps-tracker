package com.github.dach83.gps_tracker.domain

import com.github.dach83.gps_tracker.domain.model.SharedLocation
import kotlinx.coroutines.flow.Flow

interface LocationStore {

    var lastLocation: SharedLocation?

    val locationFlow: Flow<SharedLocation?>
}
