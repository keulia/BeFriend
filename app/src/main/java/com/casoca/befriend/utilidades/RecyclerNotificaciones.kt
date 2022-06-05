package com.casoca.befriend.utilidades

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.casoca.befriend.R

class RecyclerNotificaciones(var contexto: Context, var listaNoti:MutableList<Noti> ):RecyclerView.Adapter<RecyclerNotificaciones.HolderNotificaciones>() {

    inner class HolderNotificaciones(itemView: View):RecyclerView.ViewHolder(itemView){

        var tvFrase = itemView.findViewById<TextView>(R.id.tvFrase)
        //var dias = itemView.findViewById<TextView>(R.id.tvDias)



    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HolderNotificaciones {
        var itemview = LayoutInflater.from(contexto).inflate(R.layout.item_notificacion, parent, false)
        return  HolderNotificaciones(itemview)

    }

    override fun onBindViewHolder(holder: HolderNotificaciones, position: Int) {

        holder.tvFrase.text = listaNoti[position].message
        //holder.dias.text = "Cada "+ listaNoti[position].dias +" dias"
    }

    override fun getItemCount(): Int = listaNoti.size


}