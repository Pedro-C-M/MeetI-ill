package com.example.meet_ill

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class ChatAdapter(val listaMessages: List<Message>): RecyclerView.Adapter<ChatAdapter.ViewHolder>() {

    class ViewHolder(view: View): RecyclerView.ViewHolder(view){
        private var tvMessage: TextView = view.findViewById(R.id.tvMessage)
        private var message: Message? = null



        fun bind(message:Message){
            tvMessage.text = message.content
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val layoutElement = R.layout.recycler_message_item

        val view = LayoutInflater.from(parent.context).inflate(layoutElement,parent,false)

        return ViewHolder(view)
    }

    override fun getItemCount(): Int {
       return listaMessages.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(listaMessages[position])
    }
}