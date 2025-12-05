package ru.smak.locating1_0936x.location

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.location.LocationRequest
import android.os.Build
import android.util.Log
import androidx.annotation.RequiresPermission
import androidx.core.app.ActivityCompat
import androidx.core.content.getSystemService

object Locator : LocationListener {

    override fun onLocationChanged(location: Location) {
        Log.i(
            "LOCATION",
            "${location.latitude}, ${location.longitude}"
        )
        updateListeners.forEach { listener -> listener(location) }
    }

    @RequiresPermission(allOf = [
        android.Manifest.permission.ACCESS_FINE_LOCATION,
        android.Manifest.permission.ACCESS_COARSE_LOCATION
    ])
    fun startLocationListening(context: Context){
        val lm = context.getSystemService<LocationManager>()

        lm?.apply {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                getLastKnownLocation(LocationManager.FUSED_PROVIDER)
                requestLocationUpdates(
                    LocationManager.FUSED_PROVIDER,
                    5000,
                    10f,
                    Locator
                )
            } else {
                requestLocationUpdates(
                    LocationManager.GPS_PROVIDER,
                    5000,
                    10f,
                    Locator
                )
            }
        }
    }

    private val updateListeners = mutableListOf<(Location)->Unit>()
    fun addUpdateListener(updateListener: (Location) -> Unit) {
        updateListeners.add(updateListener)
    }
    fun removeUpdateListener(updateListener: (Location) -> Unit) {
        updateListeners.remove(updateListener)
    }
}