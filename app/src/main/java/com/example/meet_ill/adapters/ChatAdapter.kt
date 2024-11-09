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
        private var tvName: TextView = view.findViewById(R.id.tvName)
        private var message: Message? = null



        fun bind(message: Message){
            tvMessage.text = message.content
            if(message.isReceived) {
                tvMessage.textAlignment = View.TEXT_ALIGNMENT_TEXT_START
                tvName.text = "Contacto x"
                tvName.textAlignment = View.TEXT_ALIGNMENT_TEXT_START
            }
            else {
                tvMessage.textAlignment = View.TEXT_ALIGNMENT_TEXT_END
                tvName.text = "Yo"
                tvName.textAlignment = View.TEXT_ALIGNMENT_TEXT_END
            }
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