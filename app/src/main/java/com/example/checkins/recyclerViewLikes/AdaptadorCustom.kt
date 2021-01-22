package com.example.checkins.recyclerViewLikes

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.checkins.foursquare.Venue
import com.example.checkins.R

class AdaptadorCustom(items:ArrayList<Venue>, var listener: ClickListener):RecyclerView.Adapter<AdaptadorCustom.ViewHolder>() {

    var items:ArrayList<Venue>? = null
    var viewHolder:ViewHolder? = null

    init {
        this.items = items
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val vista = LayoutInflater.from(parent.context).inflate(R.layout.template_likes,parent,false)
        viewHolder = ViewHolder(vista,listener)
        return viewHolder!!
    }

    override fun getItemCount(): Int {
        return items?.count()!!
    }

    override fun onBindViewHolder(holder:ViewHolder, position: Int) {
        val item = items?.get(position)

        holder.tvNombre?.text = item?.name

    }

     class ViewHolder(var vista: View, listener: ClickListener):RecyclerView.ViewHolder(vista),View.OnClickListener{
         var tvNombre:TextView? = null
        var listener:ClickListener? = null

        init {

            this.tvNombre = vista.findViewById(R.id.tvNombrRVCLikes) as TextView

            this.listener = listener

            vista.setOnClickListener(this)
        }

        override fun onClick(v: View?) {
            this.listener?.onClick(v!!,adapterPosition)
        }
    }
}