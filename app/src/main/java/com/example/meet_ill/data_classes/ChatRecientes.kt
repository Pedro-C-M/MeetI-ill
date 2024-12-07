package com.example.meet_ill.data_classes

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class ChatRecientes(
    val idChat: String = "",
    val nombre: String = "",
    val imagenPerfil: String = "",
    val ultimoMensaje: String = "",
    val horaUltimoMensaje: String = ""

) : Parcelable