package com.example.checkins.recyclerViewCategorias

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.checkins.foursquare.Category
import com.example.checkins.R
import com.squareup.picasso.Picasso

class AdapterCustomCategorias(items: ArrayList<Category>, var listener: ClickListener) : RecyclerView.Adapter<AdapterCustomCategorias.ViewHolder>() {

    var items: ArrayList<Category>? = null
    var viewHolder: ViewHolder? = null

    init {
        this.items = items
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val vista = LayoutInflater.from(parent.context).inflate(R.layout.template_categorias, parent, false)
        viewHolder = ViewHolder(vista, listener)
        return viewHolder!!
    }

    override fun getItemCount(): Int {
        return items?.count()!!
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = items?.get(position)

        holder.tvNombre?.text = item?.name
        Picasso.get().load(item?.icon?.urlIcono).into(holder.ivFoto)
    }

    class ViewHolder(var vista: View, listener: ClickListener) : RecyclerView.ViewHolder(vista), View.OnClickListener {
        var tvNombre: TextView? = null
        var ivFoto: ImageView? = null
        var listener: ClickListener? = null

        init {

            this.tvNombre = vista.findViewById(R.id.tvNombrRVCategoria) as TextView
            this.ivFoto = vista.findViewById(R.id.ivFotoCategoria) as ImageView

            this.listener = listener

            vista.setOnClickListener(this)
        }

        override fun onClick(v: View?) {
            this.listener?.onClick(v!!, adapterPosition)
        }
    }
}
