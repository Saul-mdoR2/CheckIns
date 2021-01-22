package com.example.checkins.mensajes

import android.content.Context
import android.widget.Toast

class Mensaje {
    companion object {
        fun mensaje(context: Context, mensaje: MensajesInfo) {
            var str = ""
            str = when (mensaje) {
                MensajesInfo.RATIONALE -> {
                    "Requiero permisos para obtener la ubicación."
                }
                MensajesInfo.CHECKIN_SUCCESS -> {
                    "El Check-In se registró exitosamente."
                }
                MensajesInfo.LIKE_SUCCESS -> {
                    "El Like se registró exitosamente."
                }
            }
            Toast.makeText(context, str, Toast.LENGTH_SHORT).show()
        }

        fun mensajeError(context: Context, error: Errores) {
            var mensaje = ""
            when (error) {
                Errores.NO_HAY_RED -> {
                    mensaje = "No hay conexión a internet disponible."
                }
                Errores.SOLICITUDHTTP_ERROR -> {
                    mensaje = "No se pudo realizar la solicitud HTTP."
                }
                Errores.NO_HAY_APP_FOURSQUARE -> {
                    mensaje = "No tiene la app de Foursquare instalada."
                }
                Errores.ERROR_CONEXION_FOURSQUARE -> {
                    mensaje = "No se pudo conectar a Foursquare."
                }
                Errores.ERROR_OBTENER_TOKEN -> {
                    mensaje = "No se pudo obtener el Token."
                }
                Errores.ERROR_GUARDAR_TOKEN -> {
                    mensaje = "No se pudo guardar el Token."
                }
                Errores.ERROR_PERMISO_NEGADO -> {
                    mensaje = "No tiene permisos para la ubicación."
                }
                Errores.ERROR_QUERY -> {
                    mensaje = "Hubo un problema con la solicitud a la API."
                }
            }
            Toast.makeText(context, mensaje, Toast.LENGTH_SHORT).show()
        }

        fun mensajeError(context: Context, error: String) {
            Toast.makeText(context, error, Toast.LENGTH_SHORT).show()
        }
    }
}