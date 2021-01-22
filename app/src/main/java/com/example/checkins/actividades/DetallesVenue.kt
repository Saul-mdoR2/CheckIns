package com.example.checkins.actividades

import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.content.ContextCompat
import com.example.checkins.foursquare.Foursquare
import com.example.checkins.foursquare.Venue
import com.example.checkins.foursquare.objetoGridView
import com.example.checkins.gridViewDetalle.AdaptadorGridView
import com.example.checkins.R
import com.google.gson.Gson
import com.squareup.picasso.Picasso
import java.net.URLEncoder

class DetallesVenue : AppCompatActivity() {
    var toolbar: Toolbar? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_detalles_venue)


        val venueActualString = intent.getStringExtra(PantallaPrincipal.VENUE_ACTUAL)
        val gson = Gson()
        val venueActual = gson.fromJson(venueActualString, Venue::class.java)

        implementarToolbar(venueActual.name)

        val listaGridView = ArrayList<objetoGridView>()


        val ivFoto = findViewById<ImageView>(R.id.ivFotoDetalle)
        val tvNombre = findViewById<TextView>(R.id.tvNombreDetalle)
        val tvEstado = findViewById<TextView>(R.id.tvEstadoDetalle)
        val tvPais = findViewById<TextView>(R.id.tvPaisDetalle)

        val gridView = findViewById<GridView>(R.id.gridViewDetalles)

        val bCheckIn = findViewById<Button>(R.id.btnCheckIn)
        val bLike = findViewById<Button>(R.id.btnLike)

        tvNombre.text = venueActual.name
        tvEstado.text = venueActual.location?.state
        tvPais.text = venueActual.location?.country

        Picasso.get().load(venueActual.imagenPreview).placeholder(R.drawable.placeholder_venue).into(ivFoto)

        listaGridView.add(objetoGridView(venueActual.categories?.get(0)?.name!!, R.drawable.icono_category_gris_detalle, ContextCompat.getColor(this, R.color.primaryColor)))
        listaGridView.add(objetoGridView("0 CheckIns", R.drawable.icono_checkin, ContextCompat.getColor(this, R.color.primaryLightColor)))
        listaGridView.add(objetoGridView("0 usuarios", R.drawable.icono_users, ContextCompat.getColor(this, R.color.primaryLightColor)))
        listaGridView.add(objetoGridView("0 tips", R.drawable.icono_tips, ContextCompat.getColor(this, R.color.primaryColor)))
        /*listaGridView.add(objetoGridView(String.format("%s checkins",NumberFormat.getNumberInstance(Locale.US).format(venueActual.stats?.checkinsCount)) ,R.drawable.icono_checkin, ContextCompat.getColor(this,R.color.primaryLightColor)))
        listaGridView.add(objetoGridView(String.format("%s checkins",NumberFormat.getNumberInstance(Locale.US).format(venueActual.stats?.checkinsCount)) ,R.drawable.icono_checkin, ContextCompat.getColor(this,R.color.primaryLightColor)))
        listaGridView.add(objetoGridView(String.format("%s usuarios",NumberFormat.getNumberInstance(Locale.US).format(venueActual.stats?.usersCount)),R.drawable.icono_users, ContextCompat.getColor(this,R.color.primaryLightColor)))
        listaGridView.add(objetoGridView(String.format("%s tips",NumberFormat.getNumberInstance(Locale.US).format(venueActual.stats?.tipCount)),R.drawable.icono_tips, ContextCompat.getColor(this,R.color.primaryColor)))*/

        val adaptador = AdaptadorGridView(this, listaGridView)
        gridView.adapter = adaptador

        val foursquare = Foursquare(this, DetallesVenue())

        bCheckIn.setOnClickListener {
            if (foursquare.hayToken()) {
                val etMensaje = EditText(this)
                etMensaje.hint = "Hola!"
                AlertDialog.Builder(this)
                        .setTitle("Nuevo Check-In")
                        .setMessage("Ingresa un mensaje para publicarlo junto al Check-In")
                        .setView(etMensaje)
                        .setPositiveButton("Check-In") { _, _ ->
                            foursquare.nuevoCheckIn(venueActual.id, venueActual.location!!, URLEncoder.encode(etMensaje.text.toString(), "UTF-8"))
                        }.setNegativeButton("Cancelar") { _, _ -> }
                        .show()
            } else {
                foursquare.regresarIniciarSesion()
            }
        }

        bLike.setOnClickListener {
            if (foursquare.hayToken()) {
                foursquare.nuevoLike(venueActual.id)
            }
        }

    }

    private fun implementarToolbar(venueActual: String) {
        toolbar = findViewById(R.id.toolbarDetalle)
        toolbar?.title = venueActual
        setSupportActionBar(toolbar)
        val actionbar = supportActionBar
        actionbar?.setDisplayHomeAsUpEnabled(true)
        toolbar?.setNavigationOnClickListener {
            finish()
        }
    }
}