package com.example.checkins.interfaces

import com.example.checkins.foursquare.Photo

interface ImagePreviewInterface {
    fun obtenerImagePreview(photos:ArrayList<Photo>)
}