package com.github.dach83.gps_tracker.di

import com.github.dach83.gps_tracker.data.AndroidLocationClient
import com.github.dach83.gps_tracker.data.AndroidLocationStore
import com.github.dach83.gps_tracker.domain.LocationClient
import com.github.dach83.gps_tracker.domain.LocationStore
import com.github.dach83.gps_tracker.presentation.MainViewModel
import com.google.android.gms.location.LocationServices
import org.koin.androidx.viewmodel.dsl.viewModelOf
import org.koin.dsl.module

val appModule = module {

    single<LocationClient> {
        AndroidLocationClient(
            context = get(),
            client = LocationServices.getFusedLocationProviderClient(get())
        )
    }

    single<LocationStore> {
        AndroidLocationStore(context = get())
    }

    viewModelOf(::MainViewModel)
}
