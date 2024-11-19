package com.example.meet_ill.data_classes

import android.content.res.Resources
import android.os.Parcelable
import com.example.meet_ill.R
import kotlinx.parcelize.Parcelize

@Parcelize
data class Grupo(
    val titulo: String,
    val numeroDeIntegrantes: Int,
    val urlImagen: Int,
    val idGrupo: String,
    var usuarioUnido: Boolean? = false // Opcional, y valor por defecto es null
) : Parcelable {
    val strIntegrantes: String//Get es para properties creo
    get() = if (numeroDeIntegrantes == -1) {
            "Prueba a unirte a algún grupo \nPuedes hacerlo en la pestaña de buscar grupo."
        } else {
            "$numeroDeIntegrantes participantes"
        }
}