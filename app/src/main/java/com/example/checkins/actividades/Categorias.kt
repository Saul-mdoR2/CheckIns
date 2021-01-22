package com.example.checkins.actividades

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.checkins.foursquare.Category
import com.example.checkins.foursquare.Foursquare
import com.example.checkins.interfaces.CategoriasInterface
import com.example.checkins.R
import androidx.appcompat.widget.Toolbar
import com.example.checkins.recyclerViewCategorias.AdapterCustomCategorias
import com.example.checkins.recyclerViewCategorias.ClickListener
import com.google.gson.Gson

class Categorias : AppCompatActivity() {

    var fsq:Foursquare? = null
    var rvListaCategorias:RecyclerView? = null
    var adaptador:AdapterCustomCategorias? = null
    var layoutManager:RecyclerView.LayoutManager? = null
    var toolbar: Toolbar? = null

    companion object{
        val CATEGORIA_ACTUAL = "com.example.checkins.Categorias"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_categorias)

        fsq = Foursquare(this,Categorias())

        if(fsq?.hayToken()!!){
            fsq?.obtenerCategorias(object:CategoriasInterface{
                override fun categoriasGeneradas(categorias: ArrayList<Category>) {
                    crearAdaptador(categorias)
                }
            })
        }else{
            fsq?.regresarIniciarSesion()
        }

        // TOOLBAR
       implementarToolbar()

        // RECYCLER VIEW
        rvListaCategorias = findViewById(R.id.rvListaCategorias)
        rvListaCategorias?.setHasFixedSize(true)

        layoutManager = LinearLayoutManager(this)
        rvListaCategorias?.layoutManager = layoutManager
    }

    private fun crearAdaptador(categorias:ArrayList<Category>){
        adaptador = AdapterCustomCategorias(categorias, object: ClickListener {
            override fun onClick(vista: View, Index: Int) {
                val categoriaJson = Gson()
                val categoriaActualString = categoriaJson.toJson(categorias.get(Index))
                val intent = Intent(applicationContext,VenuesPorCategoria::class.java)
                intent.putExtra(CATEGORIA_ACTUAL, categoriaActualString)
                startActivity(intent)
            }
        })
        rvListaCategorias?.adapter=adaptador
    }

    private fun implementarToolbar(){
        toolbar = findViewById(R.id.toolbarCategorias)
        toolbar?.setTitle(R.string.appCategories)
        setSupportActionBar(toolbar)
        var actionbar = supportActionBar
        actionbar?.setDisplayHomeAsUpEnabled(true)

        toolbar?.setNavigationOnClickListener {
            finish()
        }
    }

}