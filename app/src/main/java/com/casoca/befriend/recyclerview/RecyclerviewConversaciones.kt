package com.casoca.befriend.recyclerview

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.casoca.befriend.R

class RecyclerviewConversaciones (var contexto: Context,var listaConversaciones: MutableList<String>,var listener: OnConversacionClickListener):RecyclerView.Adapter<RecyclerviewConversaciones.ConversacionesHolder>() {

    interface OnConversacionClickListener{
        fun onConversacionClick(position: Int)
    }

    inner class ConversacionesHolder(var itemView:View): RecyclerView.ViewHolder(itemView){
        var tvConversacion:TextView = itemView.findViewById(R.id.tvConversacion)
        fun bind(position:Int){
            itemView.setOnClickListener {
                listener.onConversacionClick(position)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ConversacionesHolder {
        var itemView = LayoutInflater.from(contexto).inflate(R.layout.item_conversation,parent,false)
        return ConversacionesHolder(itemView)
    }

    override fun onBindViewHolder(holder: ConversacionesHolder, position: Int) {
        holder.tvConversacion.text = listaConversaciones[position]
        holder.bind(position)
    }

    override fun getItemCount(): Int {
        return listaConversaciones.size
    }

}