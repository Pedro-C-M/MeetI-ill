package com.example.meet_ill.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.meet_ill.R
import com.example.meet_ill.data_classes.ChatRecientes
import de.hdodenhof.circleimageview.CircleImageView

class RecentChatAdapter : RecyclerView.Adapter<MyChatListHolder>() {

    var listOfChats = listOf<ChatRecientes>()
    private var listener: onChatClicked? = null
    var chatShitModal = ChatRecientes()


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyChatListHolder {

        val view = LayoutInflater.from(parent.context).inflate(R.layout.chatrecientes, parent, false)
        return MyChatListHolder(view)


    }

    override fun getItemCount(): Int {

        return listOfChats.size


    }


    fun setList(list: List<ChatRecientes>) {
        this.listOfChats = list

    }

    override fun onBindViewHolder(holder: MyChatListHolder, position: Int) {

        val chat = listOfChats[position]
        holder.userName.text = chat.nombre
        val ss=chat.ultimoMensaje
        holder.lastMessage.text = chat.ultimoMensaje
        holder.timeView.text = chat.horaUltimoMensaje
        //Glide.with(holder.itemView.context).load(chat.imagenPerfil).into(holder.imageView)

        holder.itemView.setOnClickListener {
            listener?.getOnChatCLickedItem(position, chat)
        }
    }


    fun setOnChatClickListener(listener: onChatClicked) {
        this.listener = listener
    }



}

class MyChatListHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

    val imageView: CircleImageView = itemView.findViewById(R.id.recentChatImageView)
    val userName: TextView = itemView.findViewById(R.id.recentChatTextName)
    val lastMessage: TextView = itemView.findViewById(R.id.recentChatTextLastMessage)
    val timeView: TextView = itemView.findViewById(R.id.recentChatTextTime)


}


interface onChatClicked {
    fun getOnChatCLickedItem(position: Int, chatList: ChatRecientes)
}