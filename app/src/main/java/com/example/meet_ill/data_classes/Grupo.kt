package com.example.meet_ill.data_classes

data class Grupo(
    val titulo: String,
    val numeroDeIntegrantes: Int,
    val urlImagen: Int,
    val idGrupo: String
){
    val strIntegrantes: String//Get es para properties creo
        get() = if (numeroDeIntegrantes == -1) {
            "Prueba a unirte a algún grupo \nPuedes hacerlo en la pestaña de buscar grupo."
        } else {
            "$numeroDeIntegrantes participantes"
        }
}