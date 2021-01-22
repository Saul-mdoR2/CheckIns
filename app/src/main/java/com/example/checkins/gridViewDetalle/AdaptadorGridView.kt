package com.example.checkins.gridViewDetalle

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import com.example.checkins.foursquare.objetoGridView
import com.example.checkins.R

class AdaptadorGridView(var context: Context,items:ArrayList<objetoGridView>): BaseAdapter() {
    var items:ArrayList<objetoGridView>? = null
    init {
        this.items = items
    }


    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        var vista = convertView
        var holder:viewHolder? = null
        if(vista==null){
            vista = LayoutInflater.from(context).inflate(R.layout.template_grid_detalle,null)
            holder = viewHolder(vista)
            vista.tag = holder
        }else{
            holder = vista.tag as? viewHolder
        }

        val item = items?.get(position) as objetoGridView
        holder?.tvNombre?.text = item.nombre
        holder?.ivImagen?.setImageResource(item.icono)
        holder?.contenedor?.setBackgroundColor(item.colorFondo)
        return vista!!
    }

    override fun getItem(position: Int): Any {
        return items?.get(position)!!
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    override fun getCount(): Int {
        return this.items?.count()!!
    }

    private class viewHolder(vista:View){
        var tvNombre:TextView? = null
        var ivImagen: ImageView? = null
        var contenedor:LinearLayout? = null
        init {
            tvNombre = vista.findViewById(R.id.tvNombreTemplateGrid) as TextView
            ivImagen = vista.findViewById(R.id.ivImagenTemplateGrid) as ImageView
            contenedor = vista.findViewById(R.id.contenedorGrid) as LinearLayout
        }
    }

}