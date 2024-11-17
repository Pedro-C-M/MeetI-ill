package com.example.meet_ill.data_classes

import android.content.res.Resources
import com.example.meet_ill.R

data class Grupo(
    val titulo: String,
    val numeroDeIntegrantes: Int,
    val urlImagen: Int,
    val idGrupo: String
){
    val strIntegrantes: String = "$numeroDeIntegrantes participantes"
}