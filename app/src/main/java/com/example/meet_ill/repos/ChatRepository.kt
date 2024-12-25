package com.example.meet_ill.repos

import android.util.Log
import com.example.meet_ill.data_classes.ChatRecientes
import com.example.meet_ill.data_classes.Message
import com.example.meet_ill.data_classes.User
import com.google.firebase.Firebase
import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.firestore
import kotlinx.coroutines.tasks.await
import java.text.SimpleDateFormat
import java.util.Locale

class ChatRepository {


    private val db = Firebase.firestore.collection("chats")

    //Funcion para ver todos los chats recientes de un usuario
    suspend fun getChatsForUser(userId: String): List<ChatRecientes> {
        val chats = mutableListOf<ChatRecientes>()
        try {
            // Obtenemos los documentos donde el usuario es participante
            val documents = db.whereArrayContains("participantes", userId).get().await()

            for (document in documents) {
                val participantes = document.get("participantes") as? List<String> ?: emptyList()

                // Obtenemos el ID del otro usuario
                val otroUsuarioId = participantes.first { it != userId }
                val otroUsuarioDoc = Firebase.firestore.collection("users").document(otroUsuarioId).get().await()
                val otroUsuarioNombre = otroUsuarioDoc.getString("apodo") ?: ""
                val otroUsuarioImagen = otroUsuarioDoc.getString("imagenPerfil") ?: ""

                // Obtener el ID del chat
                val otroUsuario = User(
                    idUsuario = otroUsuarioId,
                    nombreUsuario = otroUsuarioDoc.getString("apodo") ?: "",
                    nombreReal = otroUsuarioDoc.getString("name") ?: "",
                    correo = otroUsuarioDoc.getString("email") ?: "",
                    patologias = (otroUsuarioDoc.get("patologias") as? List<String>)?.toMutableList() ?: mutableListOf(),
                    grupsIds = (otroUsuarioDoc.get("groupsIds") as? List<String>)?.toMutableList() ?: mutableListOf(),
                    imagenPerfil = otroUsuarioDoc.getString("imagenPerfil") ?: "",
                    tipoUsuarioStr = document.getString("user-type") ?: "user"//Si no hay nada le meto user
                )

                val chatId = getChatId(otroUsuario, userId)
                if (chatId != null) {
                    // Obtener los mensajes del chat
                    val mensajes = getMessageById(chatId)
                    if (mensajes != null && mensajes.isNotEmpty()) {
                        val ultimoMensaje = mensajes.last()
                        chats.add(
                            ChatRecientes(
                                idChat = chatId,
                                nombre = otroUsuarioNombre,
                                idUsuario= otroUsuarioId,
                                imagenPerfil = otroUsuarioImagen,
                                ultimoMensaje = ultimoMensaje.content,
                                horaUltimoMensaje = ultimoMensaje.fecha
                            )
                        )
                    } else {
                        // Chat sin mensajes
                        chats.add(
                            ChatRecientes(
                                idChat = chatId,
                                nombre = otroUsuarioNombre,
                                imagenPerfil = otroUsuarioImagen,
                                ultimoMensaje = "Sin mensajes",
                                horaUltimoMensaje = ""
                            )
                        )
                    }
                }
            }
        } catch (e: Exception) {
            Log.e("Repos", "Error al obtener los chats del usuario", e)
        }
        return chats.sortedByDescending { it.horaUltimoMensaje }
    }

    suspend fun getChatId(otroUsuario: User, usuarioId: String): String? {
        var chatId:String = ""

        val participantes = listOf(otroUsuario.idUsuario,usuarioId)
        val ordenados = participantes.sorted()
        try{
            val document = db.whereEqualTo("participantes", ordenados)
                .get().await()
            if (document.isEmpty) {
                val documentReference = db.add(mapOf("participantes" to ordenados))
                    .await()
                chatId=documentReference.id
            }
            else{
                chatId = document.documents[0].id
            }


        } catch (e: Exception) {
            Log.e("Repos", "Error al obtener el grupo", e)
            null
        }
        return chatId

    }

    suspend fun getMessageById(chatId: String): MutableList<Message>? {
        val messages = mutableListOf<Message>()
        try {
            val document = db.document(chatId).collection("mensajesChat").
            orderBy("timeSent", Query.Direction.ASCENDING).get().await()
            for(message in document){
                val sender = message.getString("sender")
                val text = message.getString("texto")
                val timestamp = message.getTimestamp("timeSent")
                val date = timestamp?.toDate()
                val formatter = SimpleDateFormat("hh:mm a", Locale.getDefault())
                val formattedTime = formatter.format(date)

                val usuario =Firebase.firestore.collection("users").document(sender!!).get().await()

                val nombre = usuario.getString("apodo")


                if(sender.toString().equals(FirebaseAuth.getInstance().currentUser?.uid.toString())) {
                    val message = Message(text.toString(), false,nombre.toString(),formattedTime,"")
                    messages.add(message)
                }
                else{
                    val message = Message(text.toString(), true,nombre.toString(),formattedTime,"")
                    messages.add(message)
                }

            }

        } catch (e: Exception) {
            Log.e("Repos", "Error al obtener el grupo", e)
            null
        }
        return messages;
    }

    suspend fun addMessage(content: String, chatId: String) {
        try {
            val document = db.document(chatId).collection("mensajesChat").document()

            document.set(
                hashMapOf(
                    "sender" to FirebaseAuth.getInstance().currentUser?.uid.toString(),
                    "texto" to content,
                    "timeSent" to Timestamp.now()
                )
            ).await()


        } catch (e: Exception) {
            Log.e("Repos", "Error al enviar el mensaje", e)
            null
        }
    }

}