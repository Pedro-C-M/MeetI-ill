package com.example.meet_ill.repos

import android.content.Context
import android.util.Log
import com.example.meet_ill.data_classes.Grupo
import com.google.firebase.Firebase
import com.google.firebase.firestore.firestore
import kotlinx.coroutines.tasks.await

class GroupRepository {

    private val db = Firebase.firestore.collection("grupos")

    suspend fun getGrupoById(groupId : String, context:Context):Grupo?{
        return try {
            val document = db.document(groupId).get().await()
            if(!document.exists()){
                return null
            }
            // Obtén el nombre del recurso como String
            val fondo1 = document.getString("urlImagen") ?: "fondo1" // Si no existe, usa un valor predeterminado
            // Obtén el resourceId usando el nombre del recurso
            val resourceId = context.resources.getIdentifier(fondo1, "drawable", context.packageName)
            return Grupo(
                titulo = document.getString("nombre")?:"Sin título",
                numeroDeIntegrantes = (document.get("participantes") as? List<String>)?.count() ?: 0,
                urlImagen = resourceId,
                idGrupo = groupId
            )

        } catch (e: Exception) {
            Log.e("Repos", "Error al obtener el grupo", e)
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
                    titulo = document.getString("nombre") ?: "Sin título",
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
}