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
    val idGrupo: String
) : Parcelable {
    val strIntegrantes: String = "$numeroDeIntegrantes participantes"
}