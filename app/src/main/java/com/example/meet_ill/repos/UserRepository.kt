package com.example.meet_ill.repos

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.util.Base64
import android.util.Log
import com.example.meet_ill.R
import com.example.meet_ill.data_classes.User
import com.example.meet_ill.util.AuthSingleton.auth
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.firestore
import kotlinx.coroutines.tasks.await


class UserRepository {

    private val db = Firebase.firestore.collection("users")
    private val auth = FirebaseAuth.getInstance()

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
                    imagenPerfil = document.getString("imagenPerfil") ?: "",
                    tipoUsuarioStr = document.getString("user-type") ?: "user"//Si no hay nada le meto user
                )
            } else {
                null // Si no existe el documento
            }
        } catch (e: Exception) {
            Log.e("Repos", "Error al obtener el usuario", e)
            null
        }
    }
    suspend fun getAllUsers(): List<User> {
        return try {
            db.get().await().documents.mapNotNull { document ->
                if (document.exists()) {
                    User(
                        idUsuario = document.id,
                        nombreUsuario = document.getString("apodo") ?: "",
                        nombreReal = document.getString("name") ?: "",
                        correo = document.getString("email") ?: "",
                        patologias = (document.get("patologias") as? List<String>)?.toMutableList() ?: mutableListOf(),
                        grupsIds = (document.get("groupsIds") as? List<String>)?.toMutableList() ?: mutableListOf(),
                        imagenPerfil = document.getString("imagenPerfil") ?: "",
                        tipoUsuarioStr = document.getString("user-type") ?: "user" // Predeterminado a "user"
                    )
                } else {
                    null
                }
            }
        } catch (e: Exception) {
            Log.e("Repos", "Error al obtener todos los usuarios", e)
            emptyList()
        }
    }

    suspend fun unirGrupo(idUsuario: String, idGrupo: String) {
        try {
            db.document(idUsuario).update("groupsIds", FieldValue.arrayUnion(idGrupo)).await()
        } catch (e: Exception) {
            Log.e("Repos", "Error al meter grupo al usuario", e)
        }
    }

    // Actualizar un usuario
    fun updateUser(userId: String, updates: Map<String, Any>, onSuccess: () -> Unit, onFailure: (Exception) -> Unit) {
        db.document(userId).update(updates)
            .addOnSuccessListener { onSuccess() }
            .addOnFailureListener { onFailure(it) }
    }

    fun getCurrentUserId(): String? = auth.currentUser?.uid

    suspend fun obtenerImgUserEnSesion(): Bitmap? {
        return try {
            val userId = auth.currentUser?.uid
            if (userId == null) {
                Log.e("convertir64aImg", "Usuario no autenticado")
                return null
            }

            val document = db.document(userId).get().await()
            if (!document.exists()) {
                Log.e("convertir64aImg", "El documento no existe para el usuario con ID: $userId")
                return null
            }

            val imagenPerfil = document.getString("imagenPerfil") ?: ""
            if (imagenPerfil.isEmpty()) {
                Log.e("convertir64aImg", "Campo 'imagenPerfil' vacío")
                return null
            }

            val decodedBytes = Base64.decode(imagenPerfil, Base64.NO_WRAP)
            BitmapFactory.decodeByteArray(decodedBytes, 0, decodedBytes.size)
        } catch (e: Exception) {
            Log.e("convertir64aImg", "Error al convertir la imagen", e)
            null
        }
    }

    suspend fun obtenerImgUserParametro(userId: String?): Bitmap? {
        return try {
            // Validar que el userId no sea nulo o vacío
            if (userId.isNullOrEmpty()) {
                Log.e("obtenerImgUserParametro", "El ID del usuario no es válido")
                return null
            }

            // Obtener el documento correspondiente al usuario
            val document = db.document(userId).get().await()
            if (!document.exists()) {
                Log.e("obtenerImgUserParametro", "El documento no existe para el usuario con ID: $userId")
                return null
            }

            // Extraer y validar el campo de imagenPerfil
            val imagenPerfil = document.getString("imagenPerfil") ?: ""
            if (imagenPerfil.isEmpty()) {
                Log.e("obtenerImgUserParametro", "El campo 'imagenPerfil' está vacío")
                return null
            }

            // Decodificar la imagen en formato Base64
            val decodedBytes = Base64.decode(imagenPerfil, Base64.NO_WRAP)
            BitmapFactory.decodeByteArray(decodedBytes, 0, decodedBytes.size)
        } catch (e: Exception) {
            Log.e("obtenerImgUserParametro", "Error al obtener o convertir la imagen", e)
            null
        }
    }
}