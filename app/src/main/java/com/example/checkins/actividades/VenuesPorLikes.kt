package com.example.checkins.actividades

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import androidx.appcompat.widget.Toolbar
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.checkins.foursquare.Foursquare
import com.example.checkins.foursquare.Venue
import com.example.checkins.interfaces.VenuesPorLikeInterface
import com.example.checkins.R
import com.example.checkins.recyclerViewLikes.AdaptadorCustom
import com.example.checkins.recyclerViewLikes.ClickListener
import com.google.gson.Gson

class VenuesPorLikes : AppCompatActivity() {

    var toolbar: Toolbar? = null
    var layoutManager: RecyclerView.LayoutManager? = null
    var adaptador: AdaptadorCustom? = null
    var rvListaLugaresPorLikes: RecyclerView?=null

    var foursquare: Foursquare? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_venues_por_likes)

        implementarRecyclerView()
        implementarToolbar()
        foursquare = Foursquare(this, this)


        if(foursquare?.hayToken()!!){
            foursquare?.obtenerVenuesLike(object :VenuesPorLikeInterface{
                override fun venuesPorLikeGenerados(venues: ArrayList<Venue>) {
                    crearAdaptador(venues)
                }
            })
        }else{
            foursquare?.regresarIniciarSesion()
        }
    }



    private fun crearAdaptador(lugares:ArrayList<Venue>){

        adaptador = AdaptadorCustom(lugares, object : ClickListener{
            override fun onClick(vista: View, Index: Int) {
                val venueJson = Gson()
                val venueActualString = venueJson.toJson(lugares[Index])
                val intent = Intent(applicationContext,DetallesVenue::class.java)
                intent.putExtra(PantallaPrincipal.VENUE_ACTUAL, venueActualString)
                startActivity(intent)
            }
        })
        rvListaLugaresPorLikes?.adapter=adaptador
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        foursquare?.validarActivityResult(requestCode,resultCode,data)
    }

    private fun implementarToolbar(){
        toolbar = findViewById(R.id.toolbarVenuesPorLikes)
        toolbar?.setTitle(R.string.appLikes)
        setSupportActionBar(toolbar)
        var actionbar = supportActionBar
        actionbar?.setDisplayHomeAsUpEnabled(true)
        toolbar?.setNavigationOnClickListener {
            finish()
        }
    }

    private fun implementarRecyclerView(){
        rvListaLugaresPorLikes = findViewById(R.id.tvListaLugaresPorLikes)
        rvListaLugaresPorLikes?.setHasFixedSize(true)
        layoutManager = LinearLayoutManager(this)
        rvListaLugaresPorLikes?.layoutManager = layoutManager
    }
}