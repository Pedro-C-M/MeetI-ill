package com.example.meet_ill.repos

import android.content.Context
import android.util.Log
import com.example.meet_ill.data_classes.Grupo
import com.example.meet_ill.data_classes.Message
import com.example.meet_ill.data_classes.User
import com.google.firebase.Firebase
import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.firestore
import kotlinx.coroutines.tasks.await
import java.text.SimpleDateFormat
import java.util.Locale
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
import kotlin.coroutines.suspendCoroutine

class GroupRepository {

    private val db = Firebase.firestore.collection("grupos")

    suspend fun getGrupoById(groupId : String, context:Context):Grupo?{
        Log.e("Repos", "OWOWOWO")
        return try {
            val document = db.document(groupId).get().await()
            if(!document.exists()){
                return null
            }
            // Obtén el nombre del recurso como String
            var fondo1 = document.getString("urlImagen") ?: "fondo1" // Si no existe, usa un valor predeterminado
            // Obtén el resourceId usando el nombre del recurso
            if(fondo1.isBlank())
            {
                fondo1 = "fondo1"
            }
            val resourceId = context.resources.getIdentifier(fondo1, "drawable", context.packageName)
            return Grupo(
                enfermedad = document.getString("nombre")?:"Sin título",
                numeroDeIntegrantes = (document.get("participantes") as? List<String>)?.count() ?: 0,
                urlImagen = resourceId,
                idGrupo = groupId
            )

        } catch (e: Exception) {
            Log.e("Repos", "Error al obtener el grupo", e)
            null
        }
    }

    suspend fun getMessageById(groupId : String):MutableList<Message>?{



        val messages = mutableListOf<Message>()
        try {
            val document = db.document(groupId).collection("mensajesGrupo").
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
                    val message = Message(text.toString(), false,nombre.toString(),formattedTime,"", message.id)
                    messages.add(message)
                }
                else{
                    val message = Message(text.toString(), true,nombre.toString(),formattedTime,"", message.id)
                    messages.add(message)
                }

            }

        } catch (e: Exception) {
            Log.e("Repos", "Error al obtener el grupo", e)
            null
        }
        return messages;
    }

    suspend fun getParticipantsById(groupId: String):MutableList<User>?{
        val participants = mutableListOf<User>()
        try {
            val document = db.document(groupId).get().await()
            for(participant in (document.get("participantes") as? List<String>)!!){

                if(participant!=FirebaseAuth.getInstance().currentUser?.uid.toString()) {

                    val usuario =
                        Firebase.firestore.collection("users").document(participant!!).get().await()

                    val nombre = usuario.getString("apodo")!!
                    val imagen = usuario.getString("imagenPerfil").orEmpty()
                    val nombreReal = usuario.getString("name")!!
                    val tipoUser = usuario.getString("user-type") ?: "user"
                    val finalUser = User(
                        participant, nombre, nombreReal, "", mutableListOf(),
                        mutableListOf(),imagen,tipoUser
                    )

                    participants.add(finalUser)
                }

            }

        } catch (e: Exception) {
            Log.e("Repos", "Error al obtener el grupo", e)
            null
        }
        return participants;
    }



    suspend fun addMessage(content : String, groupId: String){

        try {
            val document = db.document(groupId).collection("mensajesGrupo").document()

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
    suspend fun getAllGroups(context:Context):MutableList<Grupo>{
        val gruposList = mutableListOf<Grupo>()
        return try {
            val result = db.get().await()
            for (document in result.documents){
                var fondo = document.getString("urlImagen") ?: "fondo1"
                if(fondo.isBlank())
                {
                    fondo = "fondo1"
                }
                val resourceId = context.resources.getIdentifier(fondo, "drawable", context.packageName)
                //Creo el gurpo
                val grupo = Grupo(
                    enfermedad = document.getString("nombre") ?: "Sin título",
                    numeroDeIntegrantes = (document.get("participantes") as? List<String>)?.count() ?: 0,
                    urlImagen = resourceId,
                    idGrupo = document.id
                )
                gruposList.add(grupo)
            }

            return gruposList
        }
        catch (e: Exception) {
            Log.e("Repos", "Error al obtener los grupos", e)
            mutableListOf()  // Retorna una lista vacía si ocurre un error
        }
    }

    suspend fun meterParticipante(idGrupo: String, idUsuario: String) {
        try {
            db.document(idGrupo).update("participantes", FieldValue.arrayUnion(idUsuario)).await()
        } catch (e: Exception) {
            Log.e("Repos", "Error al meter usuario a grupo", e)
        }
    }

    suspend fun abandonarGrupo(groupId: String, idUsuario: String){
        try {
            val document = db.document(groupId)

            document.update("participantes",FieldValue.arrayRemove(idUsuario)).
            addOnSuccessListener { Log.d("borrar","se elimino correctamente") }.
            addOnFailureListener{Log.d("borrar","no se borro bien")}.
            await()


            val documentUsuarios = Firebase.firestore.collection("users").document(idUsuario)

            documentUsuarios.update("groupsIds",FieldValue.arrayRemove(groupId)).
            addOnSuccessListener { Log.d("borrar","se borro bien en el usuario") }.
            addOnFailureListener{Log.d("borrar","no se borro bien en el usuario")}.
            await()


        } catch (e: Exception) {
            Log.e("Repos", "Error al enviar el mensaje", e)
            null
        }
    }
}