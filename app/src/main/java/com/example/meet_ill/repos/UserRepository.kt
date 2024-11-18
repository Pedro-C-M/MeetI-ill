package com.example.meet_ill.repos

import android.util.Log
import com.example.meet_ill.data_classes.User
import com.example.meet_ill.util.AuthSingleton.auth
import com.google.firebase.Firebase
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
                    correo = document.getString("email") ?: "",
                    patologias = (document.get("patologias") as? List<String>)?.toMutableList() ?: mutableListOf(),
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

    // Actualizar un usuario
    fun updateUser(userId: String, updates: Map<String, Any>, onSuccess: () -> Unit, onFailure: (Exception) -> Unit) {
        db.document(userId).update(updates)
            .addOnSuccessListener { onSuccess() }
            .addOnFailureListener { onFailure(it) }
    }

    // Obtener el usuario actual autenticado
    fun getCurrentUserId(): String? {
        return auth.currentUser?.uid
    }
}