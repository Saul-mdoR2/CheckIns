package com.example.checkins.actividades

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.GridView
import android.widget.TextView
import androidx.appcompat.widget.Toolbar
import androidx.core.content.ContextCompat
import com.example.checkins.foursquare.Foursquare
import com.example.checkins.foursquare.User
import com.example.checkins.foursquare.objetoGridView
import com.example.checkins.gridViewDetalle.AdaptadorGridView
import com.example.checkins.interfaces.UsuariosInterface
import com.example.checkins.R
import com.squareup.picasso.Picasso
import de.hdodenhof.circleimageview.CircleImageView
import java.text.NumberFormat
import java.util.*

class PerfilUsuario : AppCompatActivity() {

    var foursquare: Foursquare? = null
    var toolbar: Toolbar? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_perfil_usuario)

        foursquare = Foursquare(this, this)

        val tvNombre = findViewById<TextView>(R.id.tvNombreUsuario)
        val tvAmigosUsuario = findViewById<TextView>(R.id.tvAmigosUsuario)
        val tvTipsUsuario = findViewById<TextView>(R.id.tvTipsUsuario)
        val tvCheckinsUsuario = findViewById<TextView>(R.id.tvCheckInsUsuario)
        val tvFotosUsuario = findViewById<TextView>(R.id.tvFotosUsuario)
        val ivFotoUsuario = findViewById<CircleImageView>(R.id.profile_image)
        val gridView = findViewById<GridView>(R.id.gridViewPerfil)
        val listaGridView = ArrayList<objetoGridView>()

        if (foursquare?.hayToken()!!) {
            foursquare?.obtenerUsuarioActual(object : UsuariosInterface {
                override fun usuarioObtenido(usuario: User) {
                    implementarToolbar(usuario.firstName + " " + usuario.lastName)
                    tvNombre.text = String.format("%s %s", usuario.firstName, usuario.lastName)
                    tvAmigosUsuario.text = String.format("%d %s", usuario.friends?.count, getString(R.string.texto_amigos))
                    tvTipsUsuario.text = String.format("%d %s", usuario.tips?.count, getString(R.string.texto_tips))
                    tvCheckinsUsuario.text = String.format("%d %s", usuario.checkins?.count, getString(R.string.texto_checkins))
                    tvFotosUsuario.text = String.format("%d %s", usuario.photos?.count, getString(R.string.texto_fotos))

                    Picasso.get().load(usuario.foto?.urlIcono).placeholder(R.drawable.user).into(ivFotoUsuario)

                    listaGridView.add(objetoGridView(String.format("%s %s", NumberFormat.getNumberInstance(Locale.US).format(usuario.photos?.count), getString(R.string.texto_fotos)), R.drawable.icono_foto, ContextCompat.getColor(applicationContext, R.color.primaryLightColor)))
                    listaGridView.add(objetoGridView(String.format("%s %s", NumberFormat.getNumberInstance(Locale.US).format(usuario.checkins?.count), getString(R.string.texto_checkins)), R.drawable.icono_checkin, ContextCompat.getColor(applicationContext, R.color.primaryLightColor)))
                    listaGridView.add(objetoGridView(String.format("%s %s", NumberFormat.getNumberInstance(Locale.US).format(usuario.friends?.count), getString(R.string.texto_amigos)), R.drawable.icono_users, ContextCompat.getColor(applicationContext, R.color.primaryLightColor)))
                    listaGridView.add(objetoGridView(String.format("%s %s", NumberFormat.getNumberInstance(Locale.US).format(usuario.tips?.count), getString(R.string.texto_tips)), R.drawable.icono_tips, ContextCompat.getColor(applicationContext, R.color.primaryColor)))

                    val adaptador = AdaptadorGridView(applicationContext, listaGridView)
                    gridView.adapter = adaptador
                }
            })
        } else {
            foursquare?.regresarIniciarSesion()
        }
    }

    private fun implementarToolbar(nombreUsuario: String) {
        toolbar = findViewById(R.id.toolbarUsuario)
        toolbar?.title = nombreUsuario
        setSupportActionBar(toolbar)
        val actionbar = supportActionBar
        actionbar?.setDisplayHomeAsUpEnabled(true)
        toolbar?.setNavigationOnClickListener {
            finish()
        }
    }
}