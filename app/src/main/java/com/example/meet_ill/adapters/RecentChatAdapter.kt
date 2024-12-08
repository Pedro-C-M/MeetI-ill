package com.example.meet_ill.adapters

import android.graphics.BitmapFactory
import android.util.Base64
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


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyChatListHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.recycler_reciente_item, parent, false)
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
        holder.lastMessage.text = chat.ultimoMensaje
        holder.timeView.text = chat.horaUltimoMensaje
        if (chat.imagenPerfil.isNotEmpty()) {
            cargarImagenBase64(chat.imagenPerfil, holder.imageView)
        } else {
            holder.imageView.setImageResource(R.drawable.default_profile_image)
        }
        holder.itemView.setOnClickListener {
            listener?.getOnChatCLickedItem(position, chat)
        }
    }

    private fun cargarImagenBase64(base64String: String, imageView: CircleImageView) {
        try {
            val decodedBytes = Base64.decode(base64String, Base64.NO_WRAP)
            val bitmap = BitmapFactory.decodeByteArray(decodedBytes, 0, decodedBytes.size)
            imageView.setImageBitmap(bitmap)
        } catch (e: Exception) {
            e.printStackTrace()
            imageView.setImageResource(R.drawable.default_profile_image)
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