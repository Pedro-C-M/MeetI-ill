package com.example.meet_ill.adapters

import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.meet_ill.data_classes.Message
import com.example.meet_ill.R
import kotlin.random.Random

class ChatAdapter(val listaMessages: List<Message>): RecyclerView.Adapter<ChatAdapter.ViewHolder>() {




    class ViewHolder(view: View): RecyclerView.ViewHolder(view){
        private var tvMessage: TextView = view.findViewById(R.id.tvMessage)
        private  var tvName: TextView = view.findViewById(R.id.tvName)
        private var tvFecha: TextView = view.findViewById(R.id.tvMessageTime)
        private var message: Message? = null
        //private var colores: HashMap<String, String> = hashMapOf()




        fun bind(message: Message){
            tvMessage.text = message.content
            tvFecha.text = message.fecha
            if(message.isReceived) {
                tvName.text = message.user
                //cambiarColor(tvName,message.user)
            }
            else{
                tvName.text=""
            }

        }

        /**
        private fun cambiarColor(tvName: TextView, nombre: String) {

            if(!colores.containsKey(nombre)) {
                val numero = Random.nextInt(1, 11)
                var color = ""

                when (numero) {
                    1 -> color = "#FF5722"
                    2 -> color = "#4CAF50"
                    3 -> color = "#2196F3"
                    4 -> color = "#FFC107"
                    5 -> color = "#9C27B0"
                    6 -> color = "#E91E63"
                    7 -> color = "#00BCD4"
                    8 -> color = "#FF9800"
                    9 -> color = "#3F51B5"
                    10 -> color = "#8BC34A"
                }

                colores[nombre] = color
            }
            tvName.setTextColor(Color.parseColor(colores.get(nombre)))
        }
        **/
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