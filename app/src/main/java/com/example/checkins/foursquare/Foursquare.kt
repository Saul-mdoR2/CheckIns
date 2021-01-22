package com.example.checkins.foursquare

import android.content.Intent
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.example.checkins.actividades.MainActivity
import com.example.checkins.interfaces.*
import com.example.checkins.mensajes.Errores
import com.example.checkins.mensajes.Mensaje
import com.example.checkins.mensajes.MensajesInfo
import com.example.checkins.utilidades.Network
import com.foursquare.android.nativeoauth.FoursquareOAuth
import com.google.gson.Gson

class Foursquare(var activity: AppCompatActivity, var activityDestino: AppCompatActivity) {

    private val CODIGOCONEXION = 500
    private val CODIGO_TOKEN = 501

    private val CLIENT_ID = "NBYHNOREALYFAHC5X20TL2ZKDGZV154OYX1RX45OA1TY0HHD"
    private val CLIENT_SECRET = "SUE0EJLOT2LGFA2HCGH1GA1GUSDMD4QLR3XTH1CQ52TQHYVI"

    private val SETTINGS = "settings"
    private val ACCESS_TOKEN = "accessToken"

    private val URL_BASE = "https://api.foursquare.com/v2/"
    private val VERSION = "v=20210107"

    fun iniciarSesion() {
        val intent = FoursquareOAuth.getConnectIntent(activity.applicationContext, CLIENT_ID)
        if (FoursquareOAuth.isPlayStoreIntent(intent)) {
            Mensaje.mensajeError(activity.applicationContext, Errores.NO_HAY_APP_FOURSQUARE)
            activity.startActivity(intent)
        } else {
            activity.startActivityForResult(intent, CODIGOCONEXION)
        }
    }

    fun validarActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        when (requestCode) {
            CODIGOCONEXION -> {
                conexionCompleta(resultCode, data)
            }
            CODIGO_TOKEN -> {
                intercambioTokenCompleto(resultCode, data)
            }
        }
    }

    private fun conexionCompleta(resultCode: Int, data: Intent?) {
        val codigoRespuesta = FoursquareOAuth.getAuthCodeFromResult(resultCode, data)
        val excepcion = codigoRespuesta.exception

        if (excepcion == null) {
            val codigo = codigoRespuesta.code
            realizarIntercambioToken(codigo)
        } else {
            Mensaje.mensajeError(activity.applicationContext, Errores.ERROR_CONEXION_FOURSQUARE)
        }
    }

    private fun realizarIntercambioToken(codigo: String) {
        val intent = FoursquareOAuth.getTokenExchangeIntent(activity.applicationContext, CLIENT_ID, CLIENT_SECRET, codigo)
        activity.startActivityForResult(intent, CODIGO_TOKEN)
    }

    private fun intercambioTokenCompleto(resultCode: Int, data: Intent?) {
        val respuestaToken = FoursquareOAuth.getTokenFromResult(resultCode, data)
        val exception = respuestaToken.exception
        if (exception == null) {
            val accessToken = respuestaToken.accessToken
            if (!guardarToken(accessToken)) {
                Mensaje.mensajeError(activity.applicationContext, Errores.ERROR_GUARDAR_TOKEN)
            } else {
                navegarSiguienteActividad()
            }
        } else {
            Mensaje.mensajeError(activity.applicationContext, Errores.ERROR_OBTENER_TOKEN)
        }
    }

    private fun guardarToken(Token: String): Boolean {
        if (Token.isEmpty()) {
            return false
        }
        val settings = activity.getSharedPreferences(SETTINGS, 0)
        val editor = settings.edit()
        editor.putString(ACCESS_TOKEN, Token)
        editor.apply()
        return true
    }

    fun cerrarSesion() {
        val settings = activity.getSharedPreferences(SETTINGS, 0)
        val editor = settings.edit()
        editor.putString(ACCESS_TOKEN, "")
        editor.apply()
    }

    fun regresarIniciarSesion() {
        activity.startActivity(Intent(this.activity, MainActivity::class.java))
        activity.finish()
    }

    fun obtenerToken(): String {
        val settings = activity.getSharedPreferences(SETTINGS, 0)
        val token = settings.getString(ACCESS_TOKEN, "")
        return token!!
    }

    fun navegarSiguienteActividad() {
        activity.startActivity(Intent(activity, activityDestino::class.java))
        activity.finish()
    }

    fun hayToken(): Boolean {
        return obtenerToken() != ""
    }

    fun obtenerVenues(lat: String, long: String, obtenerVenuesInterface: ObtenerVenuesInterface) {
        val network = Network(activity)
        val seccion = "venues/"
        val metodo = "search/"
        val ll = "ll=$lat,$long"
        val token = "oauth_token=" + obtenerToken()
        val url = "$URL_BASE$seccion$metodo?$ll&$token&$VERSION"

        network.SolicitudHttp(activity.applicationContext, url, object : HttpResponse {
            override fun httpRespuestaExitosa(response: String) {
                val gson = Gson()
                val objetoRespuesta = gson.fromJson(response, FoursquareAPIRequestVenues::class.java)
                val meta = objetoRespuesta.meta
                val venues = objetoRespuesta.response?.venues!!

                if (meta?.code == 200) {
                    for (venue in venues) {
                        obtenerImagePreview(venue.id, object : ImagePreviewInterface {
                            override fun obtenerImagePreview(photos: ArrayList<Photo>) {
                                if (photos.count() > 0) {
                                    val urlImagen = photos[0].construirURLImagen(obtenerToken(), VERSION, "original")
                                    venue.imagenPreview = urlImagen

                                    if (venue.categories?.count()!! > 0) {
                                        val urlIcono = venue.categories?.get(0)?.icon?.construirURLImagen(obtenerToken(), VERSION, "64")
                                        venue.iconCategory = urlIcono
                                    }
                                }
                            }
                        })
                    }
                    obtenerVenuesInterface.venuesGenerados(venues)
                } else {
                    if (meta?.code == 400) {
                        Mensaje.mensajeError(activity.applicationContext, meta.errorDetail)
                    } else {
                        Mensaje.mensajeError(activity.applicationContext, Errores.ERROR_QUERY)
                    }
                }
            }
        })
    }

    private fun obtenerImagePreview(venueId: String, ImagePreviewInterface: ImagePreviewInterface) {
        val network = Network(activity)
        val seccion = "venues/"
        val metodo = "photos/"
        val token = "oauth_token=" + obtenerToken()
        val parametro = "limit=1"
        val url = "$URL_BASE$seccion$venueId/$metodo?$parametro&$token&$VERSION"

        network.SolicitudHttp(activity.applicationContext, url, object : HttpResponse {
            override fun httpRespuestaExitosa(response: String) {
                val gson = Gson()
                val objetoRespuesta = gson.fromJson(response, ImagePreviewVenueResponse::class.java)
                val meta = objetoRespuesta.meta
                val photos = objetoRespuesta.response?.photos?.items

                if (meta?.code == 200) {
                    ImagePreviewInterface.obtenerImagePreview(photos!!)
                } else {
                    if (meta?.code == 400) {
                        Mensaje.mensajeError(activity.applicationContext, meta.errorDetail)
                    } else {
                        Mensaje.mensajeError(activity.applicationContext, Errores.ERROR_QUERY)
                    }
                }
            }
        })
    }

    fun nuevoCheckIn(id: String, location: Location, mensaje: String) {
        val network = Network(activity)
        val seccion = "checkins/"
        val metodo = "add"
        val token = "oauth_token=" + obtenerToken()
        val query = "?venueId=" + id + "&shout=" + mensaje + "&ll=" + location.lat.toString() + "," + location.lng.toString() + "&" + token + "&" + VERSION
        val url = URL_BASE + seccion + metodo + query

        network.SolicitudHttpPOST(activity.applicationContext, url, object : HttpResponse {
            override fun httpRespuestaExitosa(response: String) {
                Log.d("NUEVO CHECK IN", response)
                val gson = Gson()
                val objetoRespuesta = gson.fromJson(response, FoursquareAPInuevoCheckin::class.java)
                val meta = objetoRespuesta.meta
                if (meta?.code == 200) {
                    Mensaje.mensaje(activity.applicationContext, MensajesInfo.CHECKIN_SUCCESS)
                } else {
                    if (meta?.code == 400) {
                        Mensaje.mensajeError(activity.applicationContext, meta.errorDetail)
                    } else {
                        Mensaje.mensajeError(activity.applicationContext, Errores.ERROR_QUERY)
                    }
                }
            }
        })
    }

    fun obtenerUsuarioActual(UsuarioActualInterface: UsuariosInterface) {
        val network = Network(activity)
        val seccion = "users/"
        val metodo = "self"
        val token = "oauth_token=" + obtenerToken()
        val query = "?$token&$VERSION"
        val url = URL_BASE + seccion + metodo + query

        network.SolicitudHttp(activity.applicationContext, url, object : HttpResponse {
            override fun httpRespuestaExitosa(response: String) {
                val gson = Gson()
                val objetoRespuesta = gson.fromJson(response, FoursquareAPISelfUser::class.java)
                val meta = objetoRespuesta.meta
                if (meta?.code == 200) {
                    val usuario = objetoRespuesta.response?.user
                    usuario?.foto?.construirURLImagen(obtenerToken(), VERSION, "128x128")
                    UsuarioActualInterface.usuarioObtenido(objetoRespuesta.response?.user!!)
                } else {
                    if (meta?.code == 400) {
                        Mensaje.mensajeError(activity.applicationContext, meta.errorDetail)
                    } else {
                        Mensaje.mensajeError(activity.applicationContext, Errores.ERROR_QUERY)
                    }
                }
            }
        })
    }

    fun obtenerCategorias(categoriasInterface: CategoriasInterface) {
        val network = Network(activity)
        val seccion = "venues/"
        val metodo = "categories"
        val token = "oauth_token=" + obtenerToken()
        val query = "?$token&$VERSION"
        val url = URL_BASE + seccion + metodo + query

        network.SolicitudHttp(activity.applicationContext, url, object : HttpResponse {
            override fun httpRespuestaExitosa(response: String) {
                val gson = Gson()
                val objetoRespuesta = gson.fromJson(response, FoursquareAPICategorias::class.java)
                val meta = objetoRespuesta.meta

                val categorias = objetoRespuesta.response?.categories!!
                if (meta?.code == 200) {
                /*    categorias.forEach { categoria ->
                        categoria.icon?.construirURLImagen(obtenerToken(), VERSION, "bg_64")
                    }*/
                    categoriasInterface.categoriasGeneradas(objetoRespuesta.response?.categories!!)
                } else {
                    if (meta?.code == 400) {
                        Mensaje.mensajeError(activity.applicationContext, meta.errorDetail)
                    } else {
                        Mensaje.mensajeError(activity.applicationContext, Errores.ERROR_QUERY)
                    }
                }
            }
        })
    }

    fun obtenerVenuesPorCategoria(lat: String, long: String, obtenerVenuesInterface: ObtenerVenuesInterface, categoryId: String) {
        val network = Network(activity)
        val seccion = "venues/"
        val metodo = "search/"
        val ll = "ll=$lat,$long"
        val categoria = "categoryId=$categoryId"
        val token = "oauth_token=" + obtenerToken()
        val url = "$URL_BASE$seccion$metodo?$ll&$categoria&$token&$VERSION"

        network.SolicitudHttp(activity.applicationContext, url, object : HttpResponse {
            override fun httpRespuestaExitosa(response: String) {
                val gson = Gson()
                val objetoRespuesta = gson.fromJson(response, FoursquareAPIRequestVenues::class.java)
                val meta = objetoRespuesta.meta
                val venues = objetoRespuesta.response?.venues!!
                if (meta?.code == 200) {
                    obtenerVenuesInterface.venuesGenerados(venues)
                } else {
                    if (meta?.code == 400) {
                        Mensaje.mensajeError(activity.applicationContext, meta.errorDetail)
                    } else {
                        Mensaje.mensajeError(activity.applicationContext, Errores.ERROR_QUERY)
                    }
                }
            }
        })
    }

    fun nuevoLike(id: String) {
        val network = Network(activity)
        val seccion = "venues/"
        val metodo = "like/"
        val token = "oauth_token=" + obtenerToken()
        val query = "?$token&$VERSION"
        val url = "$URL_BASE$seccion$id/$metodo$query"

        network.SolicitudHttpPOST(activity.applicationContext, url, object : HttpResponse {
            override fun httpRespuestaExitosa(response: String) {
                val gson = Gson()
                val objetoRespuesta = gson.fromJson(response, FoursquareLikeResponse::class.java)
                val meta = objetoRespuesta.meta
                if (meta?.code == 200) {
                    Mensaje.mensaje(activity.applicationContext, MensajesInfo.LIKE_SUCCESS)
                } else {
                    if (meta?.code == 400) {
                        Mensaje.mensajeError(activity.applicationContext, meta.errorDetail)
                    } else {
                        Mensaje.mensajeError(activity.applicationContext, Errores.ERROR_QUERY)
                    }
                }
            }
        })
    }

    fun obtenerVenuesLike(venuesPorLikesInterface: VenuesPorLikeInterface) {
        val network = Network(activity)
        val seccion = "users/"
        val metodo = "self/"
        val token = "oauth_token=" + obtenerToken()
        val url = URL_BASE + seccion + metodo + "venuelikes?limit=10&" + token + "&" + VERSION

        network.SolicitudHttp(activity.applicationContext, url, object : HttpResponse {
            override fun httpRespuestaExitosa(response: String) {
                val gson = Gson()
                val objetoRespuesta = gson.fromJson(response, VenuesPorLikes::class.java)
                val meta = objetoRespuesta.meta
                val venues = objetoRespuesta.response?.venues?.items!!

                if (meta?.code == 200) {
                    venuesPorLikesInterface.venuesPorLikeGenerados(venues)
                } else {
                    if (meta?.code == 400) {
                        Mensaje.mensajeError(activity.applicationContext, meta.errorDetail)
                    } else {
                        Mensaje.mensajeError(activity.applicationContext, Errores.ERROR_QUERY)
                    }
                }
            }
        })
    }

}