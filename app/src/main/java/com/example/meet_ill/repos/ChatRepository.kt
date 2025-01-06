package com.example.meet_ill.repos

import android.util.Log
import com.example.meet_ill.data_classes.ChatRecientes
import com.example.meet_ill.data_classes.Message
import com.example.meet_ill.data_classes.User
import com.google.firebase.Firebase
import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.database
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.firestore
import kotlinx.coroutines.tasks.await
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
import kotlin.coroutines.suspendCoroutine

class ChatRepository {


    private val db = Firebase.firestore.collection("chats")
    private val UserRepository = UserRepository()

    suspend fun getChatsForUser(userId: String): List<ChatRecientes> {
        val chats = mutableListOf<ChatRecientes>()
        try {
            val users=UserRepository.getAllUsers()

            for (user in users) {
                val chatId = getChatId(user.idUsuario, userId)
                if (chatId != null) {
                    // Obtener la referencia de la base de datos de mensajes
                    val database = FirebaseDatabase.getInstance("https://meet-ill-default-rtdb.europe-west1.firebasedatabase.app")
                    val messagesRef = database.getReference("chats").child(chatId).child("messages")

                    // Leer los mensajes de la base de datos una sola vez
                    val mensajesSnapshot = messagesRef.get().await()

                    val mensajes = mutableListOf<Pair<String, String>>()
                    for (snapshot in mensajesSnapshot.children) {
                        // Convertir el snapshot en un Map para acceder a los valores
                        val data = snapshot.value as? Map<*, *>

                        val content = data?.get("text")?.toString() ?: ""
                        val fecha = data?.get("timeSent")?.toString() ?: ""
                        // Formatear la fecha
                        val formattedFecha = formatFecha(fecha)

                        // Agregar una dupla (content, fecha) a la lista
                        mensajes.add(Pair(content, formattedFecha))
                    }

                    if (mensajes.isNotEmpty()) {
                        val ultimoMensaje = mensajes.last()
                        chats.add(
                            ChatRecientes(
                                idChat = chatId,
                                nombre = user.nombreUsuario,
                                idUsuario = user.idUsuario,
                                imagenPerfil = user.imagenPerfil,
                                ultimoMensaje = ultimoMensaje.first, // 'first' es el content
                                horaUltimoMensaje = ultimoMensaje.second // 'second' es la fecha
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

    fun formatFecha(fecha: String): String {
        return try {
            // Convertir la fecha de String a Long (timestamp en segundos)
            val timestamp = fecha.toLongOrNull()

            if (timestamp != null) {
                // Convertir el timestamp de segundos a milisegundos (multiplicamos por 1000)
                val date = Date(timestamp * 1000)

                // Usar SimpleDateFormat para formatear la fecha
                val dateFormat = SimpleDateFormat("hh:mm a", Locale.getDefault())
                dateFormat.format(date)
            } else {
                fecha // Si no se puede convertir, devolver el valor original
            }
        } catch (e: Exception) {
            fecha // En caso de error, devolver la fecha original
        }
    }
    suspend fun getChatId(otroUsuario: String, usuarioId: String): String? {
        var chatId:String = ""

        val participantes = listOf(otroUsuario,usuarioId)
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
                val mssgeId = message.id
                val sender = message.getString("sender")
                val text = message.getString("texto")
                val timestamp = message.getTimestamp("timeSent")
                val date = timestamp?.toDate()
                val formatter = SimpleDateFormat("hh:mm a", Locale.getDefault())
                val formattedTime = formatter.format(date)

                val usuario =Firebase.firestore.collection("users").document(sender!!).get().await()

                val nombre = usuario.getString("apodo")


                if(sender.toString().equals(FirebaseAuth.getInstance().currentUser?.uid.toString())) {
                    val message = Message(text.toString(), false,nombre.toString(),formattedTime,"", mssgeId)
                    messages.add(message)
                }
                else{
                    val message = Message(text.toString(), true,nombre.toString(),formattedTime,"", mssgeId)
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

    suspend fun deleteGroupMessage(idGrupo: String, messgId: String) {
        /**val db_grupo = Firebase.firestore.collection("grupos")
        try {
            val document = db_grupo.document(idGrupo).collection("mensajesGrupo").document(messgId)

            document.delete()
                .addOnSuccessListener{
                    Log.d("Repos", "Mensaje eliminado correctamente del grupo")
                }
                .addOnFailureListener {
                    Log.e("Repos", "Error al eliminar el mensaje del grupo")
                }
        } catch (e: Exception) {
            Log.e("Repos", "Error inesperado al intentar eliminar el mensaje del grupo", e)
        }
        **/
        val database = FirebaseDatabase.getInstance("https://meet-ill-default-rtdb.europe-west1.firebasedatabase.app/")

        try {
            // Accedemos a la referencia del mensaje en la Realtime Database
            val databaseRef = database.reference
                .child("grupos")
                .child(idGrupo)
                .child("messages")
                .child(messgId)

            // Eliminamos el mensaje
            databaseRef.removeValue()
                .addOnSuccessListener {
                    Log.d("Repos", "Mensaje eliminado correctamente del grupo")
                }
                .addOnFailureListener { e ->
                    Log.e("Repos", "Error al eliminar el mensaje del grupo", e)
                }
        } catch (e: Exception) {
            Log.e("Repos", "Error inesperado al intentar eliminar el mensaje del grupo", e)
        }
    }

    suspend fun deletePrivateChatMessage(idGrupo: String, messgId: String) {
        /**try {
            val document = db.document(idGrupo).collection("mensajesChat").document(messgId)

            document.delete()
                .addOnSuccessListener{
                    Log.d("Repos", "Mensaje eliminado correctamente del chat privado")
                }
                .addOnFailureListener {
                    Log.e("Repos", "Error al eliminar el mensaje del chat privado")
                }
        } catch (e: Exception) {
            Log.e("Repos", "Error inesperado al intentar eliminar el mensaje del chat privado", e)
        }
        */
        val database = FirebaseDatabase.getInstance("https://meet-ill-default-rtdb.europe-west1.firebasedatabase.app/")

        try {
            // Accedemos a la referencia del mensaje en la Realtime Database
            val databaseRef = database.reference
                .child("chats")
                .child(idGrupo)
                .child("messages")
                .child(messgId)

            // Eliminamos el mensaje
            databaseRef.removeValue()
                .addOnSuccessListener {
                    Log.d("Repos", "Mensaje eliminado correctamente del chat privado")
                }
                .addOnFailureListener { e ->
                    Log.e("Repos", "Error al eliminar el mensaje del chat privado", e)
                }
        } catch (e: Exception) {
            Log.e("Repos", "Error inesperado al intentar eliminar el mensaje del chat privado", e)
        }
    }


}