package com.example.checkins.recyclerViewPrincipal

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.checkins.foursquare.Venue
import com.example.checkins.R
import com.squareup.picasso.Picasso

class AdapterCustomPrincipal(items:ArrayList<Venue>, var listener: ClickListener): RecyclerView.Adapter<AdapterCustomPrincipal.ViewHolder>() {

    var items:ArrayList<Venue>? = null
    var viewHolder:ViewHolder? = null

    init {
        this.items = items
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val vista = LayoutInflater.from(parent.context).inflate(R.layout.template_venues,parent,false)
        viewHolder = ViewHolder(vista,listener)
        return viewHolder!!
    }

    override fun getItemCount(): Int {
        return items?.count()!!
    }

    override fun onBindViewHolder( holder:ViewHolder, position: Int) {
        val item = items?.get(position)


            Picasso.get().load(item?.imagenPreview).placeholder(R.drawable.placeholder_venue).into(holder.ivFoto)



            Picasso.get().load(item?.iconCategory).placeholder(R.drawable.icono_category).into(holder.ivIconoCategoria)


        holder.tvNombre?.text = item?.name

        if(item?.categories?.size!! >0){
            holder.tvCategoria?.text = item.categories?.get(0)?.name
        }else{
            holder.tvCategoria?.setText(R.string.texto_noCategoria)
        }

        holder.tvEstado?.text = String.format(
            "%s %s",
            item.location?.state,
            item.location?.country
        )

       // if(item.stats?.checkinsCount!! != null) holder.tvNumeroCheckins?.text = item.stats?.checkinsCount.toString() else holder.tvNumeroCheckins?.text = "0"
    }

    class ViewHolder(var vista: View, listener: ClickListener):RecyclerView.ViewHolder(vista),View.OnClickListener{
        var ivFoto: ImageView? = null
        var ivIconoCategoria: ImageView? = null
        var tvNombre: TextView? = null
        var tvEstado:TextView? = null
        var tvCategoria:TextView? =  null

        private var listener:ClickListener? = null

        init {
            tvNombre = vista.findViewById(R.id.tvNombre) as TextView
            tvEstado = vista.findViewById(R.id.tvEstado) as TextView
            tvCategoria = vista.findViewById(R.id.tvCategoria) as TextView
            ivFoto = vista.findViewById(R.id.ivFoto) as ImageView
            ivIconoCategoria = vista.findViewById(R.id.ivIconoCategoria) as ImageView

            this.listener = listener

            vista.setOnClickListener(this)
        }

        override fun onClick(v: View?) {
            this.listener?.onClick(v!!,adapterPosition)
        }
    }
}
