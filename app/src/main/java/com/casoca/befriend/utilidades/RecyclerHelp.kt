package com.casoca.befriend.utilidades

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.appcompat.view.menu.ActionMenuItemView
import androidx.recyclerview.widget.RecyclerView
import com.casoca.befriend.R

// Recycler view for my questions and answers
class RecyclerHelp(var contexto:Context,var questionList:MutableList<QuestionAnswer>) : RecyclerView.Adapter<RecyclerHelp.HolderHelp>(){
    inner class HolderHelp(var itemView: View):RecyclerView.ViewHolder(itemView){
        var question:TextView = itemView.findViewById(R.id.tvQuestion)
        var answer:TextView = itemView.findViewById(R.id.tvAnswer)
        fun bind(position:Int){
            question.setOnClickListener {
                if (answer.visibility==View.GONE){
                    answer.visibility=View.VISIBLE
                }else {
                    // If the answer is visible it will change to gone
                    answer.visibility=View.GONE
                }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HolderHelp {
        var itemView = LayoutInflater.from(contexto).inflate(R.layout.item_help,parent,false)
        return HolderHelp(itemView)
    }

    override fun onBindViewHolder(holder: HolderHelp, position: Int) {
        var quest = questionList[position]
        holder.question.text = quest.question
        holder.answer.text = quest.answer
        holder.bind(position)
    }

    override fun getItemCount(): Int {
        return questionList.size
    }



}