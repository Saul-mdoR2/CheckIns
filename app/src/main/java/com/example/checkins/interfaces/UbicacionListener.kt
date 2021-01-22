package com.example.checkins.interfaces

import com.google.android.gms.location.LocationResult

interface UbicacionListener {
    fun ubicacionResponse(locationResult: LocationResult)
}