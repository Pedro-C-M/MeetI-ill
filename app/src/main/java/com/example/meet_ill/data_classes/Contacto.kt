package com.example.meet_ill.data_classes

/**
 * Al ser una data class su constructor y getters están implicitos
 */
data class Contacto (

    val imagenPerfil: String,//Esto será la URL o lo que represente la imagen del usuario
    val nombre: String,
    val ultimoMensaje: String,
    val horaUltimoMensaje: String//Formato HH:MM es lo mejor

)