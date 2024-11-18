package com.example.meet_ill.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.meet_ill.data_classes.Message
import com.example.meet_ill.R

class ChatAdapter(val listaMessages: List<Message>): RecyclerView.Adapter<ChatAdapter.ViewHolder>() {

    class ViewHolder(view: View): RecyclerView.ViewHolder(view){
        private var tvMessage: TextView = view.findViewById(R.id.tvMessage)
        private  var tvName: TextView = view.findViewById(R.id.tvName)
        private var tvFecha: TextView = view.findViewById(R.id.tvMessageTime)
        private var message: Message? = null



        fun bind(message: Message){
            tvMessage.text = message.content
            tvFecha.text = message.fecha
            if(message.isReceived) {
                tvName.text = message.user
            }
            else{
                tvName.text=""
            }

        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        lateinit var view: View;
        if(viewType==1){
             view = LayoutInflater.from(parent.context).inflate(
                R.layout.recycler_message_receiver_item,parent,false)
        }
        else{
            view = LayoutInflater.from(parent.context).inflate(
                R.layout.recycler_message_item,parent,false)
        }

        return ViewHolder(view)
    }

    override fun getItemCount(): Int {
       return listaMessages.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(listaMessages[position])
    }

    override fun getItemViewType(position: Int): Int {

        if (listaMessages.get(position) != null) {

            if (!listaMessages.get(position).isReceived)
                return 1;
            else
                return -1;
        }
        return -1;
    }
}