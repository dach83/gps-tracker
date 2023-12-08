package com.github.dach83.gps_tracker.data

import android.content.Context
import android.content.SharedPreferences
import android.provider.SettingsSlicesContract.KEY_LOCATION
import com.github.dach83.gps_tracker.domain.LocationStore
import com.github.dach83.gps_tracker.domain.model.SharedLocation
import com.google.gson.Gson
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow

class AndroidLocationStore(
    context: Context
) : LocationStore {

    private val prefs = context.getSharedPreferences("location", Context.MODE_PRIVATE)

    override var lastLocation: SharedLocation?
        get() = getLocation()
        set(value) {
            putLocation(value)
        }

    private fun getLocation(): SharedLocation? {
        val json = prefs.getString(KEY_LOCATION, null) ?: return null
        return Gson().fromJson(json, SharedLocation::class.java)
    }

    private fun putLocation(value: SharedLocation?) {
        val json = Gson().toJson(value)
        prefs.edit().putString(KEY_LOCATION, json).apply()
    }

    override val locationFlow: Flow<SharedLocation?>
        get() = listenLocationChanged()

    private fun listenLocationChanged(): Flow<SharedLocation?> = callbackFlow {
        val listener = SharedPreferences.OnSharedPreferenceChangeListener { _, key ->
            if (key == KEY_LOCATION) trySend(lastLocation)
        }

        prefs.registerOnSharedPreferenceChangeListener(listener)
        awaitClose {
            prefs.unregisterOnSharedPreferenceChangeListener(listener)
        }
    }

    private companion object {
        const val KEY_LOCATION = "KEY_LOCATION"
    }
}
