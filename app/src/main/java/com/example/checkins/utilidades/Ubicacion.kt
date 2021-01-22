package com.example.checkins.utilidades

import android.annotation.SuppressLint
import android.content.pm.PackageManager
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import com.example.checkins.interfaces.UbicacionListener
import com.example.checkins.mensajes.Errores
import com.example.checkins.mensajes.Mensaje
import com.example.checkins.mensajes.MensajesInfo
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult

class Ubicacion(var activity: AppCompatActivity, ubicacionListener: UbicacionListener) {

    private val permisoFineLocation = android.Manifest.permission.ACCESS_FINE_LOCATION
    private val permisoCoarseLocation = android.Manifest.permission.ACCESS_COARSE_LOCATION
    private val CODIGO_SOLICITUD_UBICACION = 100
    private var fusedLocationClient: FusedLocationProviderClient? = null
    private var locationRequest: LocationRequest? = null
    private var callback: LocationCallback? = null

    init {
        fusedLocationClient = FusedLocationProviderClient(activity.applicationContext)
        inicializarLocationRequest()
        callback = object : LocationCallback() {
            override fun onLocationResult(location: LocationResult?) {
                super.onLocationResult(location)
                ubicacionListener.ubicacionResponse(location!!)
            }
        }
    }

    private fun inicializarLocationRequest() {
        locationRequest = LocationRequest()
        locationRequest?.interval = 20000
        locationRequest?.fastestInterval = 10000
        locationRequest?.priority = LocationRequest.PRIORITY_HIGH_ACCURACY
    }

    private fun validarPermisosUbicacion(): Boolean {
        val hayUbicacionPrecisa = ActivityCompat.checkSelfPermission(activity.applicationContext, permisoFineLocation) == PackageManager.PERMISSION_GRANTED
        val hayUbicacionOrdinaria = ActivityCompat.checkSelfPermission(activity.applicationContext, permisoCoarseLocation) == PackageManager.PERMISSION_GRANTED
        return hayUbicacionOrdinaria && hayUbicacionPrecisa
    }

    private fun pedirPermisos() {
        val deboProveerContexto = ActivityCompat.shouldShowRequestPermissionRationale(activity, permisoFineLocation)
        if (deboProveerContexto) {
            Mensaje.mensaje(activity.applicationContext, MensajesInfo.RATIONALE)
        }
        solicitudPermiso()
    }

    private fun solicitudPermiso() {
        ActivityCompat.requestPermissions(activity, arrayOf(permisoFineLocation, permisoCoarseLocation), CODIGO_SOLICITUD_UBICACION)
    }

    fun onRequestPermissionsResult(requestCode: Int, grantResult: IntArray) {
        when (requestCode) {
            CODIGO_SOLICITUD_UBICACION -> {
                if (grantResult.isNotEmpty() && grantResult[0] == PackageManager.PERMISSION_GRANTED) {
                    obtenerUbicacion()
                } else {
                    Mensaje.mensajeError(activity.applicationContext, Errores.ERROR_PERMISO_NEGADO)
                }
            }
        }
    }

    @SuppressLint("MissingPermission")
    private fun obtenerUbicacion() {
        validarPermisosUbicacion()
        fusedLocationClient?.requestLocationUpdates(locationRequest, callback, null)
    }

    fun detenerActualizacionUbicacion() {
        this.fusedLocationClient?.removeLocationUpdates(callback)
    }

    fun inicializarUbicacion() {
        if (validarPermisosUbicacion()) {
            obtenerUbicacion()
        } else {
            pedirPermisos()
        }
    }

}