package com.casoca.befriend.recyclerview

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.casoca.befriend.R

class RecyclerviewFrases(var contexto: Context,var listaFrases: MutableList<String>,var listener: OnFraseClickListener):RecyclerView.Adapter<RecyclerviewFrases.FrasesHolder>() {

    interface OnFraseClickListener{
        fun onFraseClick(position: Int)
    }

    inner class FrasesHolder(var itemView:View):RecyclerView.ViewHolder(itemView){
        var tvFrase:TextView = itemView.findViewById(R.id.tvFrase)
        fun bind(position:Int){
            itemView.setOnClickListener {
                listener.onFraseClick(position)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FrasesHolder {
        var itemView = LayoutInflater.from(contexto).inflate(R.layout.item_frase,parent,false)
        return FrasesHolder(itemView)
    }

    override fun onBindViewHolder(holder: FrasesHolder, position: Int) {
        holder.tvFrase.text = listaFrases[position]
        holder.bind(position)
    }

    override fun getItemCount(): Int {
        return listaFrases.size
    }

}