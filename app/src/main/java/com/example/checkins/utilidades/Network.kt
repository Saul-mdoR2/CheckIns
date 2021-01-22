package com.example.checkins.utilidades


import android.util.Log
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.example.checkins.interfaces.HttpResponse
import com.example.checkins.mensajes.Errores
import com.example.checkins.mensajes.Mensaje
import android.content.Context
import android.net.ConnectivityManager
import androidx.appcompat.app.AppCompatActivity

class Network(var activity: AppCompatActivity) {

    @Suppress("DEPRECATION")
    fun hayRed(): Boolean {
       val connectivityManager:ConnectivityManager = activity.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val networkInfo = connectivityManager.activeNetworkInfo
        return networkInfo!= null && networkInfo.isConnected
    }

    fun SolicitudHttp(context: Context, url: String, httpResponse: HttpResponse) {
        if (hayRed()) {
            val queue = Volley.newRequestQueue(context)
            val solicitud = StringRequest(Request.Method.GET, url,
                    Response.Listener<String> { response ->
                        httpResponse.httpRespuestaExitosa(response)
                    },
                    Response.ErrorListener { error ->
                        Log.d("ERROR HTTP REQUEST", error.message.toString())
                        Mensaje.mensajeError(context, Errores.SOLICITUDHTTP_ERROR)
                    })
            queue.add(solicitud)
        } else {
            Mensaje.mensajeError(context, Errores.NO_HAY_RED)
        }
    }

    fun SolicitudHttpPOST(context: Context, url: String, httpResponse: HttpResponse) {
        if (hayRed()) {
            val queue = Volley.newRequestQueue(context)
            val solicitud = StringRequest(Request.Method.POST, url,
                    Response.Listener<String> { response ->
                        httpResponse.httpRespuestaExitosa(response)
                    },
                    Response.ErrorListener { error ->
                        Log.d("ERROR HTTP POST", error.message.toString())
                        Mensaje.mensajeError(context, Errores.SOLICITUDHTTP_ERROR)
                    })
            queue.add(solicitud)
        } else {
            Mensaje.mensajeError(context, Errores.NO_HAY_RED)
        }
    }
}