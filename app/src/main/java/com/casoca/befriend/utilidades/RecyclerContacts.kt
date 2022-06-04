package com.casoca.befriend.utilidades

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.view.menu.ActionMenuItemView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.CenterCrop
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.casoca.befriend.R

class RecyclerContacts(private var contexto:Context, private var listaDeContactos:MutableList<Contacto>, private var itemClearListener:OnCajaClickListener):RecyclerView.Adapter<RecyclerContacts.ContactsHolder>() {
    interface OnCajaClickListener{
        fun OnCajaClick(id:String)
    }
    inner class ContactsHolder(itemView: View):RecyclerView.ViewHolder(itemView){
        var tvNombre: TextView = itemView.findViewById(R.id.tvNombre)
        var tvCumple: TextView = itemView.findViewById(R.id.tvCumple)
        var image: ImageView = itemView.findViewById(R.id.ivContact)
        fun bind(position: Int){
            itemView.setOnClickListener {
                itemClearListener.OnCajaClick(listaDeContactos[position].idContact.toString())
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ContactsHolder {
        var itemView = LayoutInflater.from(contexto).inflate(R.layout.item_contacto,parent,false)
        return ContactsHolder(itemView)
    }

    override fun onBindViewHolder(holder: ContactsHolder, position: Int) {
        var contacts = listaDeContactos[position]
        val radius = contexto.resources.getDimensionPixelSize(R.dimen.corner_radius)
        Glide.with(contexto).load(contacts.img)
            .transforms(CenterCrop(), RoundedCorners(radius))
            .placeholder(R.drawable.user)
            .transition(DrawableTransitionOptions.withCrossFade())
            .into(holder.image)
        holder.tvNombre.text = contacts.name
        holder.tvCumple.text = contacts.birthday
        holder.bind(position)
    }

    override fun getItemCount(): Int {
        return listaDeContactos.size
    }

}