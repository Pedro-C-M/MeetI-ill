package com.example.meet_ill.data_classes

data class Grupo(
    val titulo: String,
    val numeroDeIntegrantes: Int,
    val urlImagen: Int
){
    val strIntegrantes: String = "$numeroDeIntegrantes participantes"
}