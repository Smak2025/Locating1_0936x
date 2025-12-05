package ru.smak.locating1_0936x

import android.Manifest
import android.app.Application
import android.location.Location
import androidx.annotation.RequiresPermission
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.ViewModel
import ru.smak.locating1_0936x.location.Locator

class MainViewModel(app: Application) : AndroidViewModel(app) {

    private var _permissionGranted by mutableStateOf(false)
    var permissionGranted: Boolean
        get() = _permissionGranted

        @RequiresPermission(allOf = [
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.ACCESS_COARSE_LOCATION
        ])
        set(value) {
            _permissionGranted = value
            if (value)
                Locator.startLocationListening(getApplication())
        }

    val locationList = mutableStateListOf<Location>()

    init {
        Locator.addUpdateListener{
            locationList.add(it)
        }
    }
}