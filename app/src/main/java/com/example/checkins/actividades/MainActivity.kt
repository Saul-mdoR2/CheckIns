package com.example.checkins.actividades

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import com.example.checkins.foursquare.Foursquare
import com.example.checkins.R

class MainActivity : AppCompatActivity() {
    var foursquare: Foursquare? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        foursquare = Foursquare(this, PantallaPrincipal())
        val bLogin = findViewById<Button>(R.id.bLogin)

        if (foursquare?.hayToken()!!) {
            foursquare?.navegarSiguienteActividad()
        }

        bLogin.setOnClickListener {
            foursquare?.iniciarSesion()
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        foursquare?.validarActivityResult(requestCode, resultCode, data)
    }
}