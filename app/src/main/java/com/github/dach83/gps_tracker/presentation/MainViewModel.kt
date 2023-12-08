package com.github.dach83.gps_tracker.presentation

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.github.dach83.gps_tracker.domain.LocationStore
import com.github.dach83.gps_tracker.domain.model.SharedLocation
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach

class MainViewModel(
    store: LocationStore
) : ViewModel() {

    var location: SharedLocation? by mutableStateOf(store.lastLocation)
        private set

    init {
        store.locationFlow
            .distinctUntilChanged()
            .onEach { location = it }
            .launchIn(viewModelScope)
    }
}
