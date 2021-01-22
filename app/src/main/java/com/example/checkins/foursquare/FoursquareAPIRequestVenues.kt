package com.example.checkins.foursquare

// EN ESTA CLASE SE VA A MAPEAR LA INFORMACION OBTENIDA DE LA API

// ------- CLASE BASE PARA LA SOLICITUD DE LAS VENUE--------
class FoursquareAPIRequestVenues {
    var response: FoursquareResponseVenues? = null
    var meta: Meta? = null
}

class Meta {
    var code: Int = 0
    var errorDetail: String = ""
}

class FoursquareResponseVenues {
    var venues: ArrayList<Venue>? = null
}

class Venue {
    var id: String = ""
    var name: String = ""
    var location: Location? = null
    var categories: ArrayList<Category>? = null
    var stats: Stats? = null
    var imagenPreview: String? = null // PARA ALAMCENAR LA IMAGEN
    var iconCategory: String? = null
}

class Location {
    var lat: Double = 0.0
    var lng: Double = 0.0
    var state: String = ""
    var country: String = ""
}

class Stats {
    var checkinsCount = 0
    var usersCount = 0
    var tipCount = 0
}

class Category {
    var id: String = ""
    var name: String = ""

    /*var pluralName: String = ""
    var shortName: String = ""*/
    var icon: Icono? = null
}

open class Icono {
    var prefix: String = ""
    var suffix: String = ""
    var urlIcono: String = ""

    fun construirURLImagen(token: String, version: String, size: String): String {
        val prefix = prefix
        val suffix = suffix
        val tok = "oauth_token=$token"
        val url = "$prefix$size$suffix?$tok&$version"
        urlIcono = url
        return url
    }
}

// ------- CLASE BASE PARA LA SOLICITUD POST NUEVO CHECKIN--------
class FoursquareAPInuevoCheckin {
    var meta: Meta? = null
}

// ------- CLASE BASE PARA LA SOLICITUD DEL USUARIO ACTUAL--------
class FoursquareAPISelfUser {
    var meta: Meta? = null
    var response: FoursquareResponseSelfUser? = null
}

class FoursquareResponseSelfUser {
    var user: User? = null
}

class User {
    var id = ""
    var firstName = ""
    var lastName = ""
    var foto: Photo? = null
    var friends: Friends? = null
    var tips: Tips? = null
    var photos: Photos? = null
    var checkins: Checkins? = null
}

class Photo : Icono() {
    var id: String = ""
    var width = 0
    var height = 0
}

class Tips {
    var count = 0
}

class Friends {
    var count = 0
}

class Photos {
    var count = 0
    var items: ArrayList<Photo>? = null
}

class Checkins {
    var id = ""
    var count = 0
    var items: ArrayList<Checkin>? = null
}

class Checkin {
    // var shout = ""
    var venue: Venue? = null
}

// ------- CLASE BASE PARA LA SOLICITUD DE LAS CATEGORIAS--------
class FoursquareAPICategorias {
    var meta: Meta? = null
    var response: CategoriasResponse? = null
}

class CategoriasResponse {
    var categories: ArrayList<Category>? = null
}

// ------- CLASE BASE PARA LA SOLICITUD POST DEL LIKE--------
class FoursquareLikeResponse {
    var meta: Meta? = null
}

//-------CLASE BASE DE VENUES A LOS QUE LE DIMOS LIKE---------------

class VenuesPorLikes {
    var meta: Meta? = null
    var response: VenuesPorLikesResponse? = null
}

class VenuesPorLikesResponse {
    var venues: VenuesPorLikesObject? = null
}

class VenuesPorLikesObject {
    var items: ArrayList<Venue>? = null
}

//-------CLASE BASE PARA LA SOLICITUD DE LA FOTO---------------
class ImagePreviewVenueResponse {
    var meta: Meta? = null
    var response: PhotosResponse? = null
}

class PhotosResponse {
    var photos: Photos? = null
}
