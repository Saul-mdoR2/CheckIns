package com.example.checkins.actividades

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import androidx.appcompat.widget.Toolbar
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.checkins.foursquare.Category
import com.example.checkins.foursquare.Foursquare
import com.example.checkins.foursquare.Venue
import com.example.checkins.interfaces.ObtenerVenuesInterface
import com.example.checkins.interfaces.UbicacionListener
import com.example.checkins.R
import com.example.checkins.utilidades.Ubicacion
import com.google.android.gms.location.LocationResult
import com.example.checkins.recyclerViewPrincipal.AdapterCustomPrincipal
import com.example.checkins.recyclerViewPrincipal.ClickListener
import com.google.gson.Gson

class VenuesPorCategoria : AppCompatActivity() {

    var toolbar: Toolbar? = null
    var layoutManager:RecyclerView.LayoutManager? = null
    var adaptador: AdapterCustomPrincipal? = null
    var rvListaLugaresPorCategoria:RecyclerView?=null
    var foursquare:Foursquare? = null
    var ubicacion: Ubicacion? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_venues_por_categoria)

        implementarRecyclerView()

        val categoriaActualString = intent.getStringExtra(Categorias.CATEGORIA_ACTUAL)
        val gson = Gson()
        val categoriaActual = gson.fromJson(categoriaActualString, Category::class.java)

        implementarToolbar(categoriaActual.name)

        foursquare = Foursquare(this, this)

        if(foursquare?.hayToken()!!){
            ubicacion = Ubicacion(this, object : UbicacionListener {
                override fun ubicacionResponse(locationResult: LocationResult) {
                    var lat = locationResult.lastLocation.latitude.toString()
                    var long = locationResult.lastLocation.longitude.toString()
                    val categoryId = categoriaActual.id
                    foursquare?.obtenerVenuesPorCategoria(lat, long, object : ObtenerVenuesInterface{
                        override fun venuesGenerados(venues: ArrayList<Venue>) {
                            crearAdaptador(venues)
                        }
                    },categoryId)
                }
            })
        }else{
            foursquare?.regresarIniciarSesion()
        }
    }

    private fun crearAdaptador(lugares:ArrayList<Venue>){
        adaptador = AdapterCustomPrincipal(lugares, object: ClickListener {
            override fun onClick(vista: View, Index: Int) {
                val venueToJson = Gson()
                val venuAcutalString  = venueToJson.toJson(lugares.get(Index))
                val intent = Intent(applicationContext, DetallesVenue::class.java)
                intent.putExtra(PantallaPrincipal.VENUE_ACTUAL,venuAcutalString)
                startActivity(intent)
            }
        })
        rvListaLugaresPorCategoria?.adapter=adaptador
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        foursquare?.validarActivityResult(requestCode,resultCode,data)
    }

    private fun implementarToolbar(categoriaRecibida:String){
        toolbar = findViewById(R.id.toolbarVenuesPorCategoria)
        toolbar?.setTitle(categoriaRecibida)
        setSupportActionBar(toolbar)
        val actionbar = supportActionBar
        actionbar?.setDisplayHomeAsUpEnabled(true)
        toolbar?.setNavigationOnClickListener {
            finish()
        }
    }

    private fun implementarRecyclerView(){
        rvListaLugaresPorCategoria = findViewById(R.id.tvListaLugaresPorCategoria)
        rvListaLugaresPorCategoria?.setHasFixedSize(true)
        layoutManager = LinearLayoutManager(this)
        rvListaLugaresPorCategoria?.layoutManager = layoutManager
    }

    override fun onStart() {
        ubicacion?.inicializarUbicacion()
        super.onStart()
    }

    override fun onPause() {
        ubicacion?.detenerActualizacionUbicacion()
        super.onPause()
    }
}