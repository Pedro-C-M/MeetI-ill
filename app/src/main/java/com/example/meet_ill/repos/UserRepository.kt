package com.example.meet_ill.repos

import android.util.Log
import com.example.meet_ill.data_classes.User
import com.google.firebase.Firebase
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.firestore
import kotlinx.coroutines.tasks.await


class UserRepository {

    private val db = Firebase.firestore.collection("users")


    suspend fun getUserById(userId : String):User?{
        return try {
            val document = db.document(userId).get().await()
            if (document.exists()) {
                User(
                    idUsuario = userId,
                    nombreUsuario = document.getString("apodo") ?: "",
                    nombreReal = document.getString("name") ?: "",
                    patologias = mutableListOf(),
                    grupsIds = (document.get("groupsIds") as? List<String>)?.toMutableList() ?: mutableListOf(),
                    imagenPerfil = document.getString("imagenPerfil") ?: ""
                )
            } else {
                null // Si no existe el documento
            }
        } catch (e: Exception) {
            Log.e("Repos", "Error al obtener el usuario", e)
            null
        }
    }

    suspend fun unirGrupo(idUsuario: String, idGrupo: String) {
        try {
            db.document(idUsuario).update("groupsIds", FieldValue.arrayUnion(idGrupo)).await()
        } catch (e: Exception) {
            Log.e("Repos", "Error al meter grupo al usuario", e)
        }
    }
}