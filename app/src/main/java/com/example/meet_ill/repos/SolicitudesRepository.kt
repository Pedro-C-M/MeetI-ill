package com.example.meet_ill.repos

import android.util.Log
import com.example.meet_ill.data_classes.User
import com.google.firebase.Firebase
import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.firestore
import kotlinx.coroutines.tasks.await

class SolicitudesRepository {


    private val db = Firebase.firestore.collection("solicitudes")


    suspend fun getUserAlreadySolicited(userId : String, enfermedad : String): Boolean{
        try {
            val querySnapshot = db.whereEqualTo("idUsuario", userId).whereEqualTo("enfermedad", enfermedad).get().await()
            // Si la consulta devuelve documentos, significa que ya existe la solicitud
            return !querySnapshot.isEmpty
        }catch (e: Exception) {
            Log.e("Repos", "Error al comprobar ya solicitado", e)
            return false
        }
    }
    suspend fun createSolicitud(user : User, enfermedad : String): Boolean{
        try {
            val newSolicitud = db.document()

            val solicitudData = hashMapOf(
                "idUsuario" to user.idUsuario,
                "enfermedad" to enfermedad,
                "fecha" to Timestamp.now(),
                "usuario" to user.nombreUsuario
            )

            newSolicitud.set(solicitudData).await()
            return true
        } catch (e: Exception) {
            Log.e("Repos", "Error al crear la solicitud", e)
            return false
        }
    }
}