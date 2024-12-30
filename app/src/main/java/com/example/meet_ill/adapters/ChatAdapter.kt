package com.example.meet_ill.adapters

import android.app.Activity
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.meet_ill.data_classes.Message
import com.example.meet_ill.R
import com.example.meet_ill.repos.ChatRepository
import com.example.meet_ill.util.UserType
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

class ChatAdapter(
    val listaMessages: MutableList<Message>,
    val context: Context,
    val tipoUsuario: UserType,
    val idGrupo: String,
    val tipoChat: Int,//Para grupos 0 para chats 1
    private val coroutineScope: CoroutineScope
): RecyclerView.Adapter<ChatAdapter.ViewHolder>() {

    class ViewHolder(view: View, private val tipoUsuario:UserType, private val context:Context, private val idGrupo: String, private val tipoChat: Int,  private val coroutineScope: CoroutineScope): RecyclerView.ViewHolder(view){
        private var tvMessage: TextView = view.findViewById(R.id.tvMessage)
        private  var tvName: TextView = view.findViewById(R.id.tvName)
        private var tvFecha: TextView = view.findViewById(R.id.tvMessageTime)
        private var message: Message? = null
        private val messageRepository = ChatRepository()


        fun bind(message: Message){
            tvMessage.text = message.content
            tvFecha.text = message.fecha
            if(message.isReceived) {
                tvName.text = message.user
            }
            else{
                tvName.text=""
            }

            //De pedro para los dialog de borrar mensaje
            if(tipoUsuario.equals(UserType.ADMIN)){
                tvMessage.setOnLongClickListener{
                    //Esto muestra el dialog
                    showDeleteConfirmationDialog(message.messageId,message.user, message.content)
                    true
                }
            }
        }

        private fun showDeleteConfirmationDialog(messgId:String, user:String, contentText:String) {
            MaterialAlertDialogBuilder(context)
                .setTitle("Eliminar mensaje")
                .setMessage("¿Estás seguro de que deseas eliminar el mensaje \"${contentText}\" de ${user}")
                .setPositiveButton("Eliminar") { dialog, _ ->
                    coroutineScope.launch {
                        if (tipoChat == 0)//Es grupo
                        {
                            messageRepository.deleteGroupMessage(idGrupo, messgId)
                        } else if (tipoChat == 1)//Es chat privado
                        {
                            messageRepository.deletePrivateChatMessage(idGrupo, messgId)
                        }

                        dialog.dismiss()
                        val activity = context as Activity
                        val intent = activity.intent
                        activity.finish()
                        activity.startActivity(intent)
                    }
                }
                .setNegativeButton("Cancelar") { dialog, _ ->
                    dialog.dismiss()
                }
                .show()
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

        return ViewHolder(view,tipoUsuario,context, idGrupo, tipoChat, coroutineScope)
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

    fun updateMessages(newMessages: List<Message>) {
        listaMessages.clear()
        listaMessages.addAll(newMessages)
        notifyDataSetChanged()
    }


}