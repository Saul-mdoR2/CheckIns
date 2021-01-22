package com.example.checkins.actividades

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import androidx.appcompat.widget.Toolbar
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.checkins.*
import com.example.checkins.foursquare.Foursquare
import com.example.checkins.foursquare.Venue
import com.example.checkins.interfaces.ObtenerVenuesInterface
import com.example.checkins.interfaces.UbicacionListener
import com.example.checkins.recyclerViewPrincipal.AdapterCustomPrincipal
import com.example.checkins.recyclerViewPrincipal.ClickListener
import com.example.checkins.utilidades.Ubicacion
import com.google.android.gms.location.LocationResult
import com.google.gson.Gson

class PantallaPrincipal : AppCompatActivity() {

    private var ubicacion: Ubicacion? = null
    var foursquare: Foursquare? = null
    var rvListaVenues:RecyclerView? = null
    var layoutManager:RecyclerView.LayoutManager? = null
    var adaptador:AdapterCustomPrincipal? = null

    var toolbar:Toolbar? = null

    companion object{
        var VENUE_ACTUAL = "com.example.checkins.Actividades.PantallaPrincipal"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_pantalla_principal)

        rvListaVenues = findViewById(R.id.rvListaLugares)
        rvListaVenues?.setHasFixedSize(true)
        layoutManager = LinearLayoutManager(this)
        rvListaVenues?.layoutManager = layoutManager

        implementarToolbar()

        foursquare = Foursquare(this, this)

        if(foursquare?.hayToken()!!){

            ubicacion = Ubicacion(this, object : UbicacionListener {
                override fun ubicacionResponse(locationResult: LocationResult) {

                    val lat = locationResult.lastLocation.latitude.toString()
                    val long = locationResult.lastLocation.longitude.toString()
                    foursquare?.obtenerVenues(lat, long, object : ObtenerVenuesInterface {

                        override fun venuesGenerados(venues: ArrayList<Venue>) {
                            crearAdaptador(venues)
                        }

                    })
                }
            })
        }
    }

    private fun implementarToolbar(){
        toolbar = findViewById(R.id.tooblarPrincipal)
        toolbar?.setTitle(R.string.app_name)
        setSupportActionBar(toolbar)
    }

    private fun crearAdaptador(lugares:ArrayList<Venue>){
        adaptador = AdapterCustomPrincipal(lugares, object: ClickListener {
            override fun onClick(vista: View, Index: Int) {
                val venueToJson = Gson()
                val venuAcutalString  = venueToJson.toJson(lugares[Index])
                val intent = Intent(applicationContext, DetallesVenue::class.java)
                intent.putExtra(VENUE_ACTUAL,venuAcutalString)
                startActivity(intent)
            }
        })
        rvListaVenues?.adapter= adaptador
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_pantalla_principal,menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when(item.itemId){
            R.id.itemCategoria->{
                val intent = Intent(this,Categorias::class.java)
                startActivity(intent)
                return true
            }
            R.id.itemFavoritos->{
                val intent = Intent(this,VenuesPorLikes::class.java)
                startActivity(intent)
                return true
            }
            R.id.itemPerfil->{
                val intent = Intent(this,PerfilUsuario::class.java)
                startActivity(intent)
                return true
            }
            R.id.itemCerrarSesion->{
                foursquare?.cerrarSesion()
                val intent = Intent(this,MainActivity::class.java)
                startActivity(intent)
                finish()
            }
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        ubicacion?.onRequestPermissionsResult(requestCode, grantResults)
    }

    override fun onStart() {
        super.onStart()
        ubicacion?.inicializarUbicacion()
    }

    override fun onPause() {
        super.onPause()
        ubicacion?.detenerActualizacionUbicacion()
    }
}