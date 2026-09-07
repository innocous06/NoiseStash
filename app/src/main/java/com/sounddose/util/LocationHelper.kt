package com.sounddose.util

import android.annotation.SuppressLint
import android.content.Context
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlin.coroutines.resume

class LocationHelper(context: Context) {

    private val fusedLocationClient: FusedLocationProviderClient =
        LocationServices.getFusedLocationProviderClient(context)

    @SuppressLint("MissingPermission")
    suspend fun getApproximateLocationTag(): String = suspendCancellableCoroutine { continuation ->
        try {
            fusedLocationClient.getCurrentLocation(Priority.PRIORITY_BALANCED_POWER_ACCURACY, null)
                .addOnSuccessListener { location ->
                    if (location != null) {
                        val lat = "%.3f".format(location.latitude)
                        val lon = "%.3f".format(location.longitude)
                        continuation.resume("GPS: $lat, $lon")
                    } else {
                        continuation.resume("Urban Transit Zone")
                    }
                }
                .addOnFailureListener {
                    continuation.resume("Urban Zone")
                }
        } catch (e: Exception) {
            continuation.resume("Urban Zone")
        }
    }
}
